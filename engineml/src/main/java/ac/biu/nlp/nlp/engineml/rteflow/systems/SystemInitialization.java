package ac.biu.nlp.nlp.engineml.rteflow.systems;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.RTE_ENGINE_GATE_LEMMATIZER_RULES_FILE;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames.RTE_TRAIN_AND_TEST_STOP_WORDS;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.FILTER_STOP_WORDS_IN_LEXICAL_RESOURCES;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Set;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.alignment.DefaultAlignmentCriteria;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorFactory;
import ac.biu.nlp.nlp.engineml.generic.truthteller.SentenceAnnotator;
import ac.biu.nlp.nlp.engineml.generic.truthteller.SynchronizedAtomicAnnotator;
import ac.biu.nlp.nlp.engineml.plugin.PluginAdministrationException;
import ac.biu.nlp.nlp.engineml.plugin.PluginException;
import ac.biu.nlp.nlp.engineml.plugin.PluginRegisterer;
import ac.biu.nlp.nlp.engineml.plugin.PluginRegistry;
import ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs.RTEPairsMultiThreadTrainer;
import ac.biu.nlp.nlp.engineml.script.RuleBasesAndPluginsContainer;
import ac.biu.nlp.nlp.engineml.utilities.LemmatizerFilterApostrophe;
import ac.biu.nlp.nlp.engineml.utilities.LogInitializer;
import ac.biu.nlp.nlp.engineml.utilities.StopWordsFileLoader;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.UnigramProbabilityEstimation;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.GateLemmatizer;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.LemmatizerException;

/**
 * Performs some initializations required for any flow of the system (by "flow"
 * I mean any type of data-set, and both train and test).
 * <P>
 * It is recommended that the flow itself (i.e. the class that is an entry point
 * to the system (e.g. {@link RTEPairsMultiThreadTrainer}) will be a subclass
 * of this class).
 * <P>
 * Note that this class does not initialize log4j. That initialization is
 * expected to be in the main() method of each entry point (using {@link LogInitializer}).
 * 
 * @author Asher Stern
 * @since Sep 19, 2011
 *
 */
public class SystemInitialization
{
	public static final boolean LEMMATIZER_SINGLE_INSTANCE = Constants.LEMMATIZER_SINGLE_INSTANCE;
	
	/**
	 * Constructor which takes the configuration file name, and the module name
	 * of the system-flow. <B>Usually</B> the module name is either
	 * {@value ConfigurationParametersNames#RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME} or
	 * {@value ConfigurationParametersNames#RTE_SUM_TRAIN_AND_TEST_MODULE_NAME}.
	 * 
	 * @param configurationFileName The configuration-file name
	 * @param configurationModuleName The module name of this system-flow
	 */
	public SystemInitialization(String configurationFileName,
			String configurationModuleName)
	{
		super();
		this.configurationFileName = configurationFileName;
		this.configurationModuleName = configurationModuleName;
	}

	protected void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, MalformedURLException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		// assuming log-initialization has already been done (it should be
		// done in the main() method of the entry-point).
		

		TeEngineMlException badConstants = constantsOK();
		if (badConstants!=null)throw badConstants;
		
		logger.info("Using configuration file: "+configurationFileName+", and module: "+configurationModuleName);
		configurationFile = new ConfigurationFile(configurationFileName);
		configurationFile.setExpandingEnvironmentVariables(true);
		configurationParams = configurationFile.getModuleConfiguration(configurationModuleName);
		
		RTESystemsUtils.setParserMode(configurationParams);
		
		lemmatizerRulesFileName = configurationParams.getFile(RTE_ENGINE_GATE_LEMMATIZER_RULES_FILE).getAbsolutePath();
		if (LEMMATIZER_SINGLE_INSTANCE)
		{
			logger.info("Using a single instance of lemmatizer for the whole system.");
			logger.info("Initializing lemmatizer using file: "+lemmatizerRulesFileName+" ...");
			this.lemmatizer = createLemmatizer();
			logger.info("done.");
		}
		else
		{
			logger.info("Using multiple instances of lemmatizers. Will be created by rule file: "+lemmatizerRulesFileName);
		}
		
		
		logger.info("Retrieving rule bases to retrieve multi words...");
		ruleBasesToRetrieveMultiWords = RTESystemsUtils.getLexicalRuleBasesForMultiWords(configurationFile);
		if (logger.isInfoEnabled())
		{
			logger.info("Rule bases retrieved:");
			for (String ruleBaseName : ruleBasesToRetrieveMultiWords)
			{
				logger.info(ruleBaseName);
			}
		}
		logger.info("done.");
		
		logger.info("Loading MLE estimation...");
		mleEstimation = RTESystemsUtils.getUnigramProbabilityEstimation(configurationParams);
		logger.info("done.");
		
		logger.info("Constructing tree annotator...");
		this.treeAnnotator = new AnnotatorFactory(this.configurationFile).getSentenceAnnotator();
		logger.info("done.");
		SynchronizedAtomicAnnotator syncAnnotator = new SynchronizedAtomicAnnotator(treeAnnotator);
		
		PluginRegistry pluginRegistry = new PluginRegistry();
		registerPlugins(pluginRegistry);
		
		FeatureVectorStructureOrganizer featureVectorStructureOrganizer =
			new FeatureVectorStructureOrganizer();
		featureVectorStructureOrganizer.setPluginRegistry(pluginRegistry);
		
		ImmutableSet<String> stopWords = null;
		if (FILTER_STOP_WORDS_IN_LEXICAL_RESOURCES)
		{
			logger.info("Loading stop words");
			ConfigurationParams transformationsParams = this.configurationFile.getModuleConfiguration(ConfigurationParametersNames.KNOWLEDGE_RESOURCES_MODULE_NAME);
			File stopWordsFile = transformationsParams.getFile(RTE_TRAIN_AND_TEST_STOP_WORDS);
			StopWordsFileLoader stopWordsLoader = new StopWordsFileLoader(stopWordsFile.getPath());
			stopWordsLoader.load();
			stopWords = stopWordsLoader.getStopWords();
			if (logger.isDebugEnabled())
			{
				StringBuffer sb = new StringBuffer();
				sb.append("Stop words are:\n");
				for (String word : stopWords)
				{
					sb.append(word).append("\n");
				}
				logger.debug(sb.toString());
			}
		}
		
		if (logger.isDebugEnabled())
		{
			if (!FILTER_STOP_WORDS_IN_LEXICAL_RESOURCES)
			{
				logger.debug("Stop words will not be loaded, since their constant flag is set to false.");
			}
		}
		
		
		teSystemEnvironment = new TESystemEnvironment(ruleBasesToRetrieveMultiWords, mleEstimation, syncAnnotator, pluginRegistry, featureVectorStructureOrganizer, new DefaultAlignmentCriteria(),stopWords);
	}
	
	protected void completeInitializationWithScript(RuleBasesAndPluginsContainer<?, ?> script) throws TeEngineMlException
	{
		this.teSystemEnvironment.getFeatureVectorStructureOrganizer().setRuleBasesContainer(script);
		this.teSystemEnvironment.getFeatureVectorStructureOrganizer().buildStructure();
	}
	
	protected void cleanUp()
	{
		if (this.lemmatizer!=null)
		{
			lemmatizer.cleanUp();
		}
	}
	
	protected void registerPlugins(PluginRegistry pluginRegistry) throws TeEngineMlException, ConfigurationException
	{
		try
		{
			ConfigurationParams params = configurationFile.getModuleConfiguration(ConfigurationParametersNames.KNOWLEDGE_RESOURCES_MODULE_NAME);
			if (params.containsKey(ConfigurationParametersNames.PLUGIN_REGISTERER_PARAMETER_NAME))
			{
				String pluginRegistererClassName = params.get(ConfigurationParametersNames.PLUGIN_REGISTERER_PARAMETER_NAME);
				@SuppressWarnings("unchecked")
				Class<? extends PluginRegisterer> classPluginRegisterer = (Class<? extends PluginRegisterer>) Class.forName(pluginRegistererClassName);
				Constructor<? extends PluginRegisterer> constructor = classPluginRegisterer.getConstructor(ConfigurationFile.class,PluginRegistry.class);
				PluginRegisterer pluginRegisterer = constructor.newInstance(this.configurationFile,pluginRegistry);
				pluginRegisterer.register();
			}
			pluginRegistry.sealRegistry();
		}
		catch(RuntimeException e)
		{
			throw new TeEngineMlException("Failed to register plugins",e);
		} catch (NoSuchMethodException e)
		{
			throw new TeEngineMlException("Failed to register plugins",e);
		} catch (InstantiationException e)
		{
			throw new TeEngineMlException("Failed to register plugins",e);
		} catch (IllegalAccessException e)
		{
			throw new TeEngineMlException("Failed to register plugins",e);
		} catch (InvocationTargetException e)
		{
			throw new TeEngineMlException("Failed to register plugins",e);
		} catch (PluginException e)
		{
			throw new TeEngineMlException("Failed to register plugins",e);
		} catch (ClassNotFoundException e)
		{
			throw new TeEngineMlException("Failed to register plugins",e);
		} catch (PluginAdministrationException e)
		{
			throw new TeEngineMlException("Failed to register plugins",e);
		}
	}
	
	protected Lemmatizer getLemmatizer() throws MalformedURLException, LemmatizerException
	{
		if (LEMMATIZER_SINGLE_INSTANCE)
		{
			return this.lemmatizer;
		}
		else
		{
			logger.info("Creating lemmatizer");
			return createLemmatizer();
		}
	}
	
	protected void cleanUpLemmatizer(Lemmatizer lemmatizer)
	{
		if (LEMMATIZER_SINGLE_INSTANCE)
			;
		else
		{
			lemmatizer.cleanUp();
		}
	}
	
	private Lemmatizer createLemmatizer() throws MalformedURLException, LemmatizerException
	{
		Lemmatizer gateLemmatizer = new GateLemmatizer(new File(lemmatizerRulesFileName).toURI().toURL());
		Lemmatizer lemmatizer = new LemmatizerFilterApostrophe(gateLemmatizer);
		lemmatizer.init();
		return lemmatizer;
	}
	
	@SuppressWarnings("unused")
	private TeEngineMlException constantsOK()
	{
		if (
				(Constants.REQUIRE_PREDICATE_TRUTH_EQUALITY)
				&&
				(!Constants.USE_ADVANCED_EQUALITIES)
		)
			return new TeEngineMlException("inconsistency in constants: Constants.REQUIRE_PREDICATE_TYPE_EQUALITY && !Constants.USE_ADVANCED_EQUALITIES"); 
		else
			return null;
	}

	protected String configurationFileName;
	protected String configurationModuleName;
	
	protected ConfigurationFile configurationFile;
	protected ConfigurationParams configurationParams;

	protected String lemmatizerRulesFileName;
	protected Lemmatizer lemmatizer = null;
	protected Set<String> ruleBasesToRetrieveMultiWords;
	protected UnigramProbabilityEstimation mleEstimation;
	private SentenceAnnotator treeAnnotator;
	
	protected TESystemEnvironment teSystemEnvironment;

	
	private static final Logger logger = Logger.getLogger(SystemInitialization.class);
}
