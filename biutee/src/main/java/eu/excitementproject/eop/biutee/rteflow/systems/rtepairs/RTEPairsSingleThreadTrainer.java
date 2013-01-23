package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.RTESystemsUtils;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.StopFlag;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

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
