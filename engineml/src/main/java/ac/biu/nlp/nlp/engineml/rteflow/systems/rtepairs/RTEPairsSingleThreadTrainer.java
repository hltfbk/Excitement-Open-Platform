package ac.biu.nlp.nlp.engineml.rteflow.systems.rtepairs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierUtils;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.classifiers.LinearTrainableStorableClassifier;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.plugin.PluginAdministrationException;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TextTreesProcessor;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.rteflow.systems.RTESystemsUtils;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.script.ScriptFactory;
import ac.biu.nlp.nlp.engineml.utilities.LogInitializer;
import ac.biu.nlp.nlp.engineml.utilities.StopFlag;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.general.ExceptionUtil;
import ac.biu.nlp.nlp.general.configuration.ConfigurationException;
import ac.biu.nlp.nlp.general.configuration.ConfigurationFileDuplicateKeyException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.lemmatizer.LemmatizerException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * An entry point for "system" flow for RTE-pairs training phase.
 * @deprecated Use {@link RTEPairsMultiThreadTrainer}.
 *
 * 
 * 
 * @see TextTreesProcessor
 * 
 * 
 * @author Asher Stern
 * @since Jan 6, 2011
 *
 */
@Deprecated
public class RTEPairsSingleThreadTrainer extends RTEPairsTrainer
{
	public static final double ACCURACY_DIFFERENCE_TO_STOP = Constants.TRAINER_ACCURACY_DIFFERENCE_TO_STOP;
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1)throw new TeEngineMlException("No arguments. Enter configuration file name as argument.");
			
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			
			logger.info("RTEPairsSingleThreadTrainer");

			
			RTEPairsSingleThreadTrainer trainer = new RTEPairsSingleThreadTrainer(configurationFileName);
			try
			{
				trainer.init();
				trainer.train();
			}
			finally
			{
				trainer.cleanUp();
			}
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			ExceptionUtil.logException(e, logger);
		}
	}
	
	

	public RTEPairsSingleThreadTrainer(String configurationFileName) throws ConfigurationFileDuplicateKeyException, FileNotFoundException, ConfigurationException, IOException, ClassNotFoundException, OperationException, TeEngineMlException, TreeCoreferenceInformationException, LemmatizerException
	{
		super(configurationFileName);
	}

	
	@Override
	protected void init() throws ConfigurationFileDuplicateKeyException, MalformedURLException, ConfigurationException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		try
		{
			super.init();
			this.script = new ScriptFactory(this.configurationFile,teSystemEnvironment.getPluginRegistry()).getDefaultScript();
			script.init();
			scriptInitialized=true;
			completeInitializationWithScript(script);
		}
		catch (OperationException e)
		{
			throw new TeEngineMlException("Initialization failed.",e);
		}
	}
	
	protected void cleanUp()
	{
		super.cleanUp();
		if (script!=null && scriptInitialized)
			this.script.cleanUp();
	}
	

	
	@Override
	protected LinearTrainableStorableClassifier newReasonableGuessClassifier() throws ClassifierException, OperationException, TeEngineMlException
	{
		//return RTESystemsUtils.reasonableGuessClassifier(script.getRuleBasesNames());
		return RTESystemsUtils.reasonableGuessClassifier(teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}

	/**
	 * This method updates the classifier.
	 * Returns the accuracy on training data.
	 * 
	 * Each "iteration" is the whole process on the whole data set. In each
	 * iteration the system finds the "cheapest" proof for each T-H pair, and
	 * constructs a feature vector for those proofs. Then, the classifier (the
	 * learning algorithm) is trained on those feature vectors. Thus, this
	 * method updates the classifier, and returns the accuracy of the trained
	 * classifier on the data set it was trained on.
	 * 
	 * This method also writes a single file named *N.ser, where N is the mainLoopIterationIndex.
	 * 
	 * @throws ClassifierException 
	 * @throws OperationException 
	 * @throws TeEngineMlException 
	 * @throws ScriptException 
	 * @throws RuleBaseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws AnnotatorException 
	 * @throws PluginAdministrationException 
	 */
	protected double oneIteration() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, FileNotFoundException, IOException, AnnotatorException, PluginAdministrationException
	{
		StopFlag stopFlag = new StopFlag();
		Vector<LabeledSample> samples = new Vector<LabeledSample>(this.pairsData.size());
		ListOfPairsProcessor pairsTrainer =
			new ListOfPairsProcessor(stopFlag, pairsData, script, classifierForSearch, lemmatizer, teSystemEnvironment);
		pairsTrainer.processList();

		LinkedHashMap<ExtendedPairData, PairProcessResult> results = pairsTrainer.getResults();
		for (ExtendedPairData pair : results.keySet())
		{
			PairProcessResult result = results.get(pair);
			samples.add(result.getLabeledSample());
		}
		
		logger.info("Iteration done.");
		RTESystemsUtils.saveSamplesInSerFile(samples, mainLoopIterationIndex, pathToStoreLabledSamples, teSystemEnvironment.getFeatureVectorStructureOrganizer());
		
		logger.info("training classifierForSearch...");
		this.classifierForSearch = RTESystemsUtils.createClassifierForSearch(teSystemEnvironment.getFeatureVectorStructureOrganizer(), samples);
		logger.info("training classifierForPredictions...");
		this.classifierForPredictions = RTESystemsUtils.createClassifierForPredictions(teSystemEnvironment.getFeatureVectorStructureOrganizer(), samples);
		RTESystemsUtils.printIterationSummary(results,samples,classifierForPredictions);
		mainLoopIterationIndex++;
		return ClassifierUtils.accuracyOf(classifierForPredictions, samples);
	}
	
	protected OperationsScript<Info, BasicNode> script = null;
	protected boolean scriptInitialized = false;
	
	private static Logger logger = Logger.getLogger(RTEPairsSingleThreadTrainer.class);


}