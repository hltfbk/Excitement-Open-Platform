package ac.biu.nlp.nlp.engineml.rteflow.systems.rtesum;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.LABELED_SAMPLES_FILE_POSTFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.LABELED_SAMPLES_FILE_PREFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.RTE_SUM_OUTPUT_ANSWER_FILE_POSTFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.RTE_SUM_OUTPUT_ANSWER_FILE_PREFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.RTE_SUM_OUTPUT_RESULTS_FILE_POSTFIX;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.RTE_SUM_OUTPUT_RESULTS_FILE_PREFIX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.classifiers.Classifier;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierUtils;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.plugin.PluginAdministrationException;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TextTreesProcessor;
import ac.biu.nlp.nlp.engineml.rteflow.systems.ConfigurationParametersNames;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.rteflow.systems.RTESystemsUtils;
import ac.biu.nlp.nlp.engineml.utilities.LogInitializer;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.safemodel.SafeSamplesUtils;
import ac.biu.nlp.nlp.engineml.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import ac.biu.nlp.nlp.instruments.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswerScoreComputer;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileWriter;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileWriter;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;

/**
 * 
 * 
 * <P>
 * <B>Note:</B> The real entailment work starts in {@link TextTreesProcessor} and its implementations.
 * If you want to make porting of the system to another type of data-set or another mode
 * of work, consider starting your work by using {@link TextTreesProcessor}.
 * 
 * @see TextTreesProcessor
 * 
 * @author Asher Stern
 * @since Jun 8, 2011
 *
 */
public class RTESumTester extends RTESumBaseEngine
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
			
			ExperimentManager.getInstance().start();
			ExperimentManager.getInstance().addMessage("RTE - Sum - Tester");
			ExperimentManager.getInstance().setConfigurationFile(configurationFileName);
			logger.info("RTESumTester");
			
			RTESumTester application = new RTESumTester(configurationFileName);
			application.init();
			try
			{
				application.test();
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

	
	public RTESumTester(String configurationFileName)
	{
		super(configurationFileName);
	}

	@Override
	public void init() throws ConfigurationFileDuplicateKeyException, FileNotFoundException, ConfigurationException, LemmatizerException, TeEngineMlException, IOException, PluginAdministrationException
	{
		super.init();
		try
		{
			// this.classifierForSearch = RTESystemsUtils.searchClassifierForTest(configurationFile, configurationParams,this.ruleBasesNames.getRuleBasesNames(),teSystemEnvironment.getPluginRegistry().getSortedCustomFeatures());
			
			if (configurationParams.containsKey(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL))
			{
				File modelFile = configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL);
				logger.info("Loading classifier for search for test from file: "+modelFile.getPath());
				this.classifierForSearch =  SafeClassifiersIO.loadLinearClassifier(teSystemEnvironment.getFeatureVectorStructureOrganizer(), modelFile);
				this.classifierForSearch.setFeaturesNames(teSystemEnvironment.getFeatureVectorStructureOrganizer().createMapOfFeatureNames());
			}
			else
			{
				logger.warn("Parameter "+ConfigurationParametersNames.RTE_TEST_SEARCH_MODEL+" does not exist in configuration module.");
				logger.warn("Trying to create classifier-for-search from serialized samples...");
				this.classifierForSearch = RTESystemsUtils.createF1Classifier(
						teSystemEnvironment.getFeatureVectorStructureOrganizer(),
						SafeSamplesUtils.load(
								this.configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_SAMPLES_FOR_SEARCH_CLASSIFIER),
								teSystemEnvironment.getFeatureVectorStructureOrganizer()).getSamples()
						);
			}
			logger.info("Classifier for search description:\n"+classifierForSearch.descriptionOfTraining());
			
			if (configurationParams.containsKey(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL))
			{
				File modelFile = configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL);
				logger.info("Loading classifier for predictions for test from file: "+modelFile.getPath());
				this.classifierForPredictions =  SafeClassifiersIO.load(teSystemEnvironment.getFeatureVectorStructureOrganizer(), modelFile);
				this.classifierForPredictions.setFeaturesNames(teSystemEnvironment.getFeatureVectorStructureOrganizer().createMapOfFeatureNames());
			}
			else
			{
				logger.warn("Could not find parameter "+ConfigurationParametersNames.RTE_TEST_PREDICTIONS_MODEL);
				logger.warn("Trying to load classifier for predictions from serialized samples...");

				// this.classifierForPredictions = RTESystemsUtils.newClassiferForPredictionsForTest(configurationParams,this.ruleBasesNames.getRuleBasesNames(),teSystemEnvironment.getPluginRegistry().getSortedCustomFeatures());
				this.classifierForPredictions = RTESystemsUtils.createF1Classifier(
						teSystemEnvironment.getFeatureVectorStructureOrganizer(),
						SafeSamplesUtils.load(
								this.configurationParams.getFile(ConfigurationParametersNames.RTE_TEST_SAMPLES_FOR_SEARCH_CLASSIFIER),
								teSystemEnvironment.getFeatureVectorStructureOrganizer()).getSamples()
						);
			}
			
			logger.info("Classifier for predictions description:\n"+classifierForPredictions.descriptionOfTraining());
		}
//		catch (OperationException e)
//		{
//			throw new TeEngineMlException("Initialization failure",e);
//		}
		catch (ClassifierException e)
		{
			throw new TeEngineMlException("Initialization failure",e);
		}
		catch (ClassNotFoundException e)
		{
			throw new TeEngineMlException("Initialization failure",e);
		}
	}

	public void test() throws FileNotFoundException, InterruptedException, TeEngineMlException, IOException, Rte6mainIOException, ClassifierException, PluginAdministrationException, OperationException, TreeStringGeneratorException, TreeCoreferenceInformationException, AnnotatorException
	{
		String answerFileName = RTE_SUM_OUTPUT_ANSWER_FILE_PREFIX+"_test"+RTE_SUM_OUTPUT_ANSWER_FILE_POSTFIX;
		String resultsFileName = RTE_SUM_OUTPUT_RESULTS_FILE_PREFIX+"_test"+RTE_SUM_OUTPUT_RESULTS_FILE_POSTFIX;
		String samplesFileName = LABELED_SAMPLES_FILE_PREFIX+"_test"+LABELED_SAMPLES_FILE_POSTFIX;
		logger.info("Starting full dataset process.");
		logger.info("Answers will be written to "+answerFileName+".");
		logger.info("Results will be written to "+resultsFileName+".");
		ExperimentManager.getInstance().register(new File(answerFileName));
		ExperimentManager.getInstance().register(new File(resultsFileName));
		ExperimentManager.getInstance().register(new File(samplesFileName));
		if (this.goldStandardAnswers!=null)
		{
			logger.info("Samples will be written to "+samplesFileName+".");
		}
		else
		{
			logger.info("Note! labeled-samples file will not be created! the reason: gold-standard does not exist.");
		}
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
		
		// First, get the results from the processor.
		// Results are given as RteSumSingleCandidateResult for each candidate. 
		Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults = processor.getAllTopicsResults();
		// Write the results to a serialization file
		writeResults(allTopicsResults, resultsFileName);

		// The results returned from the MultiThreadTopicsProcessor are only the feature
		// vectors. Now we make classification of all of those feature vectors, using
		// the classifierForPredictions
		logger.info("Classifying results.");
		Map<String , Map<String, Map<SentenceIdentifier, SingleClassification>>> classificationOfAll =
			classifyAll(classifierForPredictions,allTopicsResults);

		// Writing many-many lines into the log that describe, for each candidate, the best
		// proof that was found by the engine.
		logger.info("Writing the results to the log.");
		logResult(allTopicsResults,classificationOfAll,this.classifierForSearch);
		
		// Creating an answer file - this is an XML file in the formant of the submission
		// to RTE organizers.
		logger.info("Creating answer file");
		Map<String,Map<String,Set<SentenceIdentifier>>> answer = createAnswer(classificationOfAll);
		AnswersFileWriter answersWriter = new DefaultAnswersFileWriter();
		answersWriter.setWriteTheEvaluationAttribute(false);
		answersWriter.setAnswers(answer);
		answersWriter.setXml(answerFileName);
		answersWriter.write();
		
		
		if ( (this.goldStandardAnswers!=null) && (this.goldStandardFileName!=null) )
		{
			logger.info("Writing labled-samples...");
			Vector<LabeledSample> samples = processor.getResultsSamples();
			writeSamples(samples, samplesFileName);
			logger.info("Writing labled-samples done.");
			
			logger.info("Writing samples in SVM format:");
			ClassifierUtils.printSamplesAsSvmLightInput(samples, logger);
			
			AnswerScoreComputer answersComputer = new AnswerScoreComputer(goldStandardFileName, answerFileName);
			answersComputer.compute();
			logger.info("F1 / Precision / Recall of the answer:\n"+answersComputer.getResultsAsString());
			
			logger.info("Accuracy of classifier for predictions = "+strDouble(ClassifierUtils.accuracyOf(classifierForPredictions, samples)));
		}
		else
		{
			logger.info("Attention! not writing labled-samples since gold-standard does not exist.");
			logger.info("Attention! the samples in SVM format will not be written either! They are just not available.");
			logger.info("Attention! no calculation of Accuracy / Recall / Precision / F1 is performed, since gold-standard is not available.");
		}

		logger.info("Test done.");
	}

	protected LinearClassifier classifierForSearch;
	protected Classifier classifierForPredictions;

	private static final Logger logger = Logger.getLogger(RTESumBaseEngine.class);
}
