package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableStorableClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.lap.biu.en.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * This is a base class for {@link RTEPairsSingleThreadTrainer} and
 * {@link RTEPairsMultiThreadTrainerOld}. 
 * 
 * Training the system on the dev-set.
 * 
 * The results of the trainer are "ser" files: "labeled_samplesX.ser" where X
 * is an integer number, starting from 0. Each of those files contain a vector
 * of {@link LabeledSample}s. Using that vector, a classifier (a learning algorithm)
 * can learn how to classify a test sample.
 * 
 * One of the "labeled_samplesX.ser" files should be used for the test, and one
 * of them (possibly the same file) for the search in test. 
 * 
 * @author Asher Stern
 * @since Apr 4, 2011
 *
 */
public abstract class RTEPairsTrainer extends RTEPairsBaseSystem
{

	public static final double ACCURACY_DIFFERENCE_TO_STOP = Constants.TRAINER_ACCURACY_DIFFERENCE_TO_STOP;
	
	public RTEPairsTrainer(String configurationFileName) throws ConfigurationFileDuplicateKeyException, FileNotFoundException, ConfigurationException, IOException, ClassNotFoundException, OperationException, TeEngineMlException, TreeCoreferenceInformationException, LemmatizerException
	{
		super(configurationFileName,ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
	}
	
	/**
	 * The train process merely calls {@link #mainLoop()}
	 * 
	 * @throws TeEngineMlException
	 * @throws ParserRunException
	 * @throws SentenceSplitterException
	 * @throws OperationException
	 * @throws ClassifierException
	 * @throws LemmatizerException
	 * @throws CoreferenceResolutionException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws AnnotatorException 
	 * @throws PluginAdministrationException 
	 */
	public void train() throws TeEngineMlException, ParserRunException, SentenceSplitterException, OperationException, ClassifierException, LemmatizerException, CoreferenceResolutionException, ScriptException, RuleBaseException, FileNotFoundException, IOException, InterruptedException, AnnotatorException, PluginAdministrationException
	{
		logger.info("starting...");
		Date startDate = new Date();
		logger.info("Starting date: "+startDate.toString());
		logger.info("Starting main loop");
		double accuracy = mainLoop();
		logger.info("Accuracy on training set: "+String.format("%4.4f", accuracy) );
		Date endDate = new Date();
		logger.info("Starting date: "+startDate.toString());
		logger.info("End date: "+endDate.toString());
		long elapsedTime = endDate.getTime()-startDate.getTime();
		logger.info("Elapsed time = "+elapsedTime/(60*1000)+" minutes and "+ (elapsedTime/1000)%60+" seconds");
	}



	@Override
	protected void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, MalformedURLException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		try
		{
			super.init();
			List<PairData> originalPairsData = readDatasetAndUpdateFeatureVectorStructure();
			// List<PairData> originalPairsData = RTESystemsUtils.readPairsData(configurationParams);
			this.pairsData = new ArrayList<ExtendedPairData>(originalPairsData.size());
			logger.info("Converting all pairs to an ExtendedPairData format. (Since annotations take place here, it might take some time)...");
			for (PairData originalPairData : originalPairsData)
			{
				PairDataToExtendedPairDataConverter converter = 
					new PairDataToExtendedPairDataConverter(originalPairData,this.teSystemEnvironment);
				converter.convert();
				pairsData.add(converter.getExtendedPairData());
			}
			logger.info("Converting all pairs to an ExtendedPairData format - Done.");
			if (configurationParams.containsKey(ConfigurationParametersNames.RTE_TRAIN_SERIALIZED_SAMPLES_BASE_PATH))
			{
				pathToStoreLabledSamples = configurationParams.get(ConfigurationParametersNames.RTE_TRAIN_SERIALIZED_SAMPLES_BASE_PATH);
			}

		}
		catch (ClassNotFoundException e)
		{
			throw new TeEngineMlException("Initialization failed.",e);
		}
		catch (TreeCoreferenceInformationException e)
		{
			throw new TeEngineMlException("Initialization failed.",e);
		}
		catch (AnnotatorException e)
		{
			throw new TeEngineMlException("Initialization failed.",e);
		}
	}
	


	protected abstract LinearTrainableStorableClassifier newReasonableGuessClassifier() throws ClassifierException, OperationException, TeEngineMlException;

	
	/**
	 * The main-loop calls iteratively to {@link #oneIteration()}.
	 * 
	 * The result of this method is a series of files named *N.ser, where N is the mainLoopIterationIndex.
	 * 
	 * @return
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ClassifierException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws AnnotatorException 
	 * @throws PluginAdministrationException 
	 */
	protected double mainLoop() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, FileNotFoundException, IOException, InterruptedException, AnnotatorException, PluginAdministrationException
	{
		this.classifierForSearch = newReasonableGuessClassifier();

		double oldAccuracy = 0;
		logger.info("Starting first main loop iteration");
		double accuracy = oneIteration();
		logger.info("Memory used: "+Utils.stringMemoryUsedInMB());
		logger.info("Iteration 0 done. Current accuracy: "+String.format("%4.4f",accuracy));
		int mainLoopIterationCounter = 1;
		if (mainLoopIterationCounter<Constants.MAX_NUMBER_OF_MAIN_LOOP_ITERATIONS)
		{
			boolean stopDueToConvergence=false;
			do
			{
				logger.info("Starting main loop iteration");
				oldAccuracy = accuracy;
				accuracy = oneIteration();
				stopDueToConvergence = (Math.abs(accuracy-oldAccuracy)<=ACCURACY_DIFFERENCE_TO_STOP)&&Constants.MAIN_LOOP_STOPS_WHEN_ACCURACY_CONVERGES;
				logger.info("Memory used: "+Utils.stringMemoryUsedInMB());
				logger.info("Iteration "+mainLoopIterationCounter+" done. Current accuracy: "+String.format("%4.4f",accuracy));
				mainLoopIterationCounter++;
			}while( (mainLoopIterationCounter<Constants.MAX_NUMBER_OF_MAIN_LOOP_ITERATIONS) && !stopDueToConvergence );
		}
		logger.info("All iterations in main loop done. Returning.");
		if (Constants.MAX_NUMBER_OF_MAIN_LOOP_ITERATIONS>1)
		{
			logger.info("Accuracy last delta: "+ String.format("%-4.5f",Math.abs(accuracy-oldAccuracy)));
		}
		return accuracy;
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
	 * @throws ClassifierException 
	 * @throws OperationException 
	 * @throws TeEngineMlException 
	 * @throws ScriptException 
	 * @throws RuleBaseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected abstract double oneIteration() throws InterruptedException, TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, FileNotFoundException, IOException, AnnotatorException, PluginAdministrationException;


	protected String pathToStoreLabledSamples = null;
	protected List<ExtendedPairData> pairsData;
	
	protected int mainLoopIterationIndex=0;
	protected LinearTrainableStorableClassifier classifierForSearch;
	protected TrainableStorableClassifier classifierForPredictions;
	
	private static Logger logger = Logger.getLogger(RTEPairsTrainer.class);
}
