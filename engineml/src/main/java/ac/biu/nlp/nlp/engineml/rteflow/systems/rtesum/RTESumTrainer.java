package ac.biu.nlp.nlp.engineml.rteflow.systems.rtesum;

import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.LABELED_SAMPLES_FILE_POSTFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.LABELED_SAMPLES_FILE_PREFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.LEARNING_MODEL_FILE_PREDICTIONS_INDICATOR;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.LEARNING_MODEL_FILE_SEARCH_INDICATOR;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.RTE_SUM_OUTPUT_ANSWER_FILE_POSTFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.RTE_SUM_OUTPUT_ANSWER_FILE_PREFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.RTE_SUM_OUTPUT_RESULTS_FILE_POSTFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.RTE_SUM_OUTPUT_RESULTS_FILE_PREFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.TRAINER_ACCURACY_DIFFERENCE_TO_STOP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswerScoreComputer;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileWriter;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileWriter;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierUtils;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.classifiers.LinearTrainableStorableClassifier;
import ac.biu.nlp.nlp.engineml.classifiers.TrainableStorableClassifier;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.plugin.PluginAdministrationException;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TextTreesProcessor;
import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.rteflow.systems.RTESystemsUtils;
import ac.biu.nlp.nlp.engineml.utilities.LogInitializer;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.lemmatizer.LemmatizerException;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;

/**
 * An executable class that performs training over RTE-Summarization full data-set.
 * The data-set is assumed to be RTE-Summarization data-set, e.g. RTE-6 main task.
 * The results will be stored in files: "labeled_samplesX.ser", where X is iteration number
 * starting from 0.
 * 
 * <P>
 * <B>Note:</B> The real entailment work starts in {@link TextTreesProcessor} and its implementations.
 * If you want to make porting of the system to another type of data-set or another mode
 * of work, consider starting your work by using {@link TextTreesProcessor}. 
 * 
 * @see TextTreesProcessor
 * 
 * @author Asher Stern
 * @since Jun 7, 2011
 *
 */
public class RTESumTrainer extends RTESumBaseEngine
{
	public static void main(String[] args)
	{
		boolean loggerInitialized = false;
		try
		{
			if (args.length<1)throw new TeEngineMlException("No arguments. Enter configuration file name as argument.");
			
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			loggerInitialized = true;
			
			
			logger.info("RTESumTrainer");
			
			ExperimentManager.getInstance().start();
			ExperimentManager.getInstance().setConfigurationFile(configurationFileName);
			ExperimentManager.getInstance().addMessage("Training on RTE-sum");
			
			
			RTESumTrainer application = new RTESumTrainer(configurationFileName);
			application.init();
			try
			{
				application.train();
			}
			finally
			{
				application.cleanUp();
			}
			ExperimentManager.getInstance().save();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			if (loggerInitialized)
				ExceptionUtil.logException(e, logger);
		}
	}
	
	
	public RTESumTrainer(String configurationFileName)
	{
		super(configurationFileName);
	}

	@Override
	public void init() throws ConfigurationFileDuplicateKeyException, FileNotFoundException, ConfigurationException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		try
		{
			this.classifierForSearch = RTESystemsUtils.reasonableGuessClassifier(teSystemEnvironment.getFeatureVectorStructureOrganizer());
			logger.info("Initializing classifier for search as a reasonable guess. Description:\n"+classifierForSearch.descriptionOfTraining());
			
			
			if (configurationParams.containsKey(ConfigurationParametersNames.RTE_TRAIN_SERIALIZED_SAMPLES_BASE_PATH))
			{
				pathToStoreLabledSamples = configurationParams.get(ConfigurationParametersNames.RTE_TRAIN_SERIALIZED_SAMPLES_BASE_PATH);
			}
		}
		catch (ClassifierException e)
		{
			throw new TeEngineMlException("Initialization failure",e);
		}
		catch (OperationException e)
		{
			throw new TeEngineMlException("Initialization failure",e);
		}
		
	}
	
	@SuppressWarnings("unused")
	public void train() throws FileNotFoundException, InterruptedException, TeEngineMlException, IOException, Rte6mainIOException, ClassifierException, PluginAdministrationException, OperationException, TreeStringGeneratorException, TreeCoreferenceInformationException, AnnotatorException
	{
		boolean stop=false;
		trainingIterationIndex=0;
		boolean firstIteration = true;
		double oldAccuracy = 0;
		while (!stop)
		{
			logger.info("Starting iteration #"+trainingIterationIndex);
			double accuracy = oneIteration();
			logger.info("Iteration #"+trainingIterationIndex+" done. Current accuracy = "+strDouble(accuracy));
			if (firstIteration)
			{
				firstIteration=false;
			}
			else
			{
				if ((Math.abs(accuracy-oldAccuracy)<=TRAINER_ACCURACY_DIFFERENCE_TO_STOP)
					&&
					(Constants.MAIN_LOOP_STOPS_WHEN_ACCURACY_CONVERGES)
					)
				{
					stop = true;
				}
			}
			
			oldAccuracy = accuracy;
			
			trainingIterationIndex++;
			if (trainingIterationIndex>=Constants.MAX_NUMBER_OF_MAIN_LOOP_ITERATIONS)
			{
				stop = true;
			}
		}
	}
	
//	// In the method train(), the following hack can be used (replace the line double accuracy = oneIteration(); by the following code)
//	double oldAccuracy = 0;
//	while (!stop)
//	{
//		logger.info("Starting iteration #"+trainingIterationIndex);
//		double accuracy = 50.0;
//		if (TopicProcessor.FIRST_ITERATION)
//		{
//			TopicProcessor.FIRST_ITERATION=false;
//			logger.info("Hack, classifier for search...");
//			try
//			{
//				SafeSamples samples = SafeSamplesUtils.load(new File("labeled_samples0.ser"), this.ruleBasesNames.getRuleBasesNames());
//				this.classifierForSearch = RTESystemsUtils.createClassifierForSearch(this.ruleBasesNames.getRuleBasesNames(),samples.getSamples());
//				logger.info("Hack done. Current classifier for search is:\n"+classifierForSearch.descriptionOfTraining());
//			}
//			catch(ClassNotFoundException eee)
//			{
//				throw new TeEngineMlException("Hack failed",eee);
//			}
//		}
//		else
//		{
//			accuracy = oneIteration();	
//		}
	
	public double oneIteration() throws FileNotFoundException, InterruptedException, TeEngineMlException, IOException, Rte6mainIOException, ClassifierException, PluginAdministrationException, OperationException, TreeStringGeneratorException, TreeCoreferenceInformationException, AnnotatorException
	{
		String answerFileName = RTE_SUM_OUTPUT_ANSWER_FILE_PREFIX+"_"+trainingIterationIndex+RTE_SUM_OUTPUT_ANSWER_FILE_POSTFIX;
		String resultsFileName = RTE_SUM_OUTPUT_RESULTS_FILE_PREFIX+"_"+trainingIterationIndex+RTE_SUM_OUTPUT_RESULTS_FILE_POSTFIX;
		String samplesFileName = null;
		if (null==pathToStoreLabledSamples)
		{
			samplesFileName = LABELED_SAMPLES_FILE_PREFIX+trainingIterationIndex+LABELED_SAMPLES_FILE_POSTFIX;
		}
		else
		{
			samplesFileName = pathToStoreLabledSamples+trainingIterationIndex+LABELED_SAMPLES_FILE_POSTFIX;
		}
		logger.info("Starting full dataset process.");
		logger.info("Answers will be written to "+answerFileName+".");
		logger.info("Results will be written to "+resultsFileName+".");
		logger.info("Samples will be written to "+samplesFileName+".");
		AllTopicsProcessor processor = null;
		if (Constants.USE_OLD_CONCURRENCY_IN_RTE_SUM)
		{
			processor = new MultiThreadTopicsProcessorOld(topics, goldStandardAnswers, numberOfThreads, this.configurationFile, classifierForSearch, lemmatizer, teSystemEnvironment);			
		}
		else
		{
			processor = new MultiThreadTopicsProcessor(topics, goldStandardAnswers, numberOfThreads, this.configurationFile, classifierForSearch, lemmatizer, teSystemEnvironment);
		}
		processor.process();
		logger.info("Processing full dataset done. Writing results and samples to serialization files.");
		Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults = processor.getAllTopicsResults();
		writeResults(allTopicsResults, resultsFileName);
		Vector<LabeledSample> samples = processor.getResultsSamples();
		writeSamples(samples, samplesFileName);
		
		ExperimentManager.getInstance().register(new File(resultsFileName));
		ExperimentManager.getInstance().register(new File(samplesFileName));
		

		
		logger.info("Creating new classifiers for search and predictions.");
		classifierForSearch = RTESystemsUtils.createF1Classifier(teSystemEnvironment.getFeatureVectorStructureOrganizer(), samples);
		storeClassifier(classifierForSearch,trainingIterationIndex,LEARNING_MODEL_FILE_SEARCH_INDICATOR,pathToStoreLabledSamples);
		// classifierForSearch = RTESystemsUtils.createClassifierForSearch(ruleBasesNames.getRuleBasesNames(), samples);
		logger.info("Classifier for search description:\n"+classifierForSearch.descriptionOfTraining());
		classifierForPredictions = RTESystemsUtils.createF1Classifier(teSystemEnvironment.getFeatureVectorStructureOrganizer(), samples);
		storeClassifier(classifierForPredictions,trainingIterationIndex,LEARNING_MODEL_FILE_PREDICTIONS_INDICATOR,pathToStoreLabledSamples);
		// classifierForPredictions = RTESystemsUtils.createClassifierForPredictions(ruleBasesNames.getRuleBasesNames(), samples);
		logger.info("Classifier for predictions description:\n"+classifierForPredictions.descriptionOfTraining());
		
		logger.info("Using the new classifer for predictions, classifying all results.");
		// The results returned from the MultiThreadTopicsProcessor are only the feature
		// vectors. Now we make classification of all of those feature vectors, using
		// the new classifierForPredictions
		
		Map<String , Map<String, Map<SentenceIdentifier, SingleClassification>>> classificationOfAll =
			classifyAll(classifierForPredictions,allTopicsResults);
		
		logger.info("Writing the results to the log.");
		logResult(allTopicsResults,classificationOfAll,classifierForSearch);

		
		logger.info("Writing samples in SVM format:");
		ClassifierUtils.printSamplesAsSvmLightInput(samples, logger);
		
		
		// Creating answer
		logger.info("Creating answer file");
		Map<String,Map<String,Set<SentenceIdentifier>>> answer = createAnswer(classificationOfAll);
		AnswersFileWriter answersWriter = new DefaultAnswersFileWriter();
		answersWriter.setWriteTheEvaluationAttribute(false);
		answersWriter.setAnswers(answer);
		answersWriter.setXml(answerFileName);
		answersWriter.write();

		
		AnswerScoreComputer answersComputer = new AnswerScoreComputer(goldStandardFileName, answerFileName);
		answersComputer.compute();
		logger.info("F1 / Precision / Recall of the answer:\n"+answersComputer.getResultsAsString());
		
		ExperimentManager.getInstance().register(new File(answerFileName));
		
		return ClassifierUtils.accuracyOf(classifierForPredictions, samples);
	}
	
	

	
	
	
	protected String pathToStoreLabledSamples = null;
	protected int trainingIterationIndex;
	protected LinearTrainableStorableClassifier classifierForSearch;
	protected TrainableStorableClassifier classifierForPredictions;
	
	

	private static final Logger logger = Logger.getLogger(RTESumTrainer.class);
}
