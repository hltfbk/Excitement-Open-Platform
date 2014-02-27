package eu.excitementproject.eop.biutee.rteflow.systems;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_ENGINE_GATE_LEMMATIZER_RULES_FILE;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_TRAIN_AND_TEST_STOP_WORDS;
import static eu.excitementproject.eop.transformations.utilities.Constants.FILTER_STOP_WORDS_IN_LEXICAL_RESOURCES;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierFactory;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.plugin.PluginException;
import eu.excitementproject.eop.biutee.plugin.PluginRegisterer;
import eu.excitementproject.eop.biutee.plugin.PluginRegistry;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolBox;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolBoxFactory;
import eu.excitementproject.eop.biutee.script.RuleBasesAndPluginsContainer;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.gate.GateLemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.alignment.DefaultAlignmentCriteria;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorFactory;
import eu.excitementproject.eop.transformations.generic.truthteller.SentenceAnnotator;
import eu.excitementproject.eop.transformations.generic.truthteller.SynchronizedAtomicAnnotator;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;
import eu.excitementproject.eop.transformations.utilities.LemmatizerFilterApostrophe;
import eu.excitementproject.eop.transformations.utilities.StopWordsFileLoader;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.UnigramProbabilityEstimation;

/**
 * Performs some initializations required for any flow of the system (by "flow"
 * I mean any type of data-set, and both train and test).
 * <P>
 * It is recommended that the flow itself (i.e. the class that is an entry point
 * to the system (e.g. {@link RTEPairsMultiThreadTrainer}) will be a subclass
 * of this class.
 * <P>
 * Note that this class does not initialize log4j. That initialization is
 * expected to be in the main() method of each entry point (using {@link LogInitializer}, see also {@link SystemMain}).
 * 
 * @author Asher Stern
 * @since Sep 19, 2011
 *
 */
public class SystemInitialization
{
	public static final boolean LEMMATIZER_SINGLE_INSTANCE = BiuteeConstants.LEMMATIZER_SINGLE_INSTANCE;
	
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
	
	public static ConfigurationFile loadConfigurationFile(String configurationFileName) throws ConfigurationException
	{
		try
		{
			return new ConfigurationFile(new ImplCommonConfig(new File(configurationFileName)));
		}
		catch (eu.excitementproject.eop.common.exception.ConfigurationException e)
		{
			throw new ConfigurationException("Failed to load configuration file. Please see nested exception.",e);
		}

	}

	protected void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, MalformedURLException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		// assuming log-initialization has already been done (it should be
		// done in the main() method of the entry-point).
		

		TeEngineMlException badConstants = constantsOK();
		if (badConstants!=null)throw badConstants;
		
		logger.info("Using configuration file: "+configurationFileName+", and module: "+configurationModuleName);
		configurationFile = loadConfigurationFile(configurationFileName);
		configurationFile.setExpandingEnvironmentVariables(true);
		configurationParams = configurationFile.getModuleConfiguration(configurationModuleName);
		
		PARSER parserMode = SystemUtils.setParserMode(configurationParams);
		boolean collapseMode = configurationParams.getBoolean(ConfigurationParametersNames.RTE_ENGINE_COLLAPSE_MODE);
		
		ClassifierFactory classifierFactory = new ClassifierFactory(readClassifierOptimizationParameter());
		
		
		
		
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
		ruleBasesToRetrieveMultiWords = SystemUtils.getLexicalRuleBasesForMultiWords(configurationFile);
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
		mleEstimation = SystemUtils.getUnigramProbabilityEstimation(configurationParams);
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
			ConfigurationParams transformationsParams = this.configurationFile.getModuleConfiguration(ConfigurationParametersNames.TRANSFORMATIONS_MODULE_NAME);
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
		
		AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria = new DefaultAlignmentCriteria();
		
		GapToolBox<ExtendedInfo, ExtendedNode> gapToolBox = new GapToolBoxFactory(configurationFile,configurationParams,alignmentCriteria,mleEstimation, stopWords).createGapToolBox();
		if (gapToolBox.isHybridMode()){logger.info("System in hybrid-gap mode.");}
		else{logger.info("System in pure transformations mode.");}
		warnIfGapAndCollapseAreInconsistent(collapseMode,gapToolBox);
		
		teSystemEnvironment = new TESystemEnvironment(ruleBasesToRetrieveMultiWords, mleEstimation, syncAnnotator, pluginRegistry, featureVectorStructureOrganizer, alignmentCriteria, stopWords,parserMode, collapseMode, gapToolBox, classifierFactory);
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
			ConfigurationParams params = configurationFile.getModuleConfiguration(ConfigurationParametersNames.TRANSFORMATIONS_MODULE_NAME);
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
		catch(RuntimeException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | PluginException | ClassNotFoundException | PluginAdministrationException | InstantiationException e)
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
	
	private Boolean readClassifierOptimizationParameter() throws ConfigurationException, TeEngineMlException
	{
		Boolean ret = null;
		if (configurationParams.containsKey(ConfigurationParametersNames.RTE_ENGINE_CLASSIFIER_OPTIMIZATION_PARAMETER_NAME))
		{
			String value = configurationParams.getString(ConfigurationParametersNames.RTE_ENGINE_CLASSIFIER_OPTIMIZATION_PARAMETER_NAME);
			if (value.equalsIgnoreCase(BiuteeConstants.CLASSIFIER_OPTIMIZATION_ACCURACY_PARAMETER_VALUE))
			{
				ret = false;
			}
			else if (value.equalsIgnoreCase(BiuteeConstants.CLASSIFIER_OPTIMIZATION_F1_PARAMETER_VALUE))
			{
				ret = true;
			}
			else
			{
				throw new TeEngineMlException("The parameter value \""+value+"\" is illegal for the parameter \""+ConfigurationParametersNames.RTE_ENGINE_CLASSIFIER_OPTIMIZATION_PARAMETER_NAME+"."
						+ "\nPlease remove this parameter from the configuration file, or use one of the following parameters: \""
						+ BiuteeConstants.CLASSIFIER_OPTIMIZATION_ACCURACY_PARAMETER_VALUE+"\" or \""+BiuteeConstants.CLASSIFIER_OPTIMIZATION_F1_PARAMETER_VALUE+"\".");
			}
		}
		return ret;
	}
	
	private void warnIfGapAndCollapseAreInconsistent(boolean collapseMode, GapToolBox<?, ?> gapToolBox) throws GapException
	{
		boolean hybrid = gapToolBox.isHybridMode();
		if (hybrid!=collapseMode)
		{
			String warning = "Inconsistent values of collapse mode and hybrid mode. Usually hybrid mode uses collapsed tree, while pure transformations mode uses non-collapsed set of trees.";
			GlobalMessages.globalWarn(warning, logger);
		}
		
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
