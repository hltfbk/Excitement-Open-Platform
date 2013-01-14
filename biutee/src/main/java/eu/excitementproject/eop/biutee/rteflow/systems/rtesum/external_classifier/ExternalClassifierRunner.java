package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.external_classifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.dummy.DummyAllTrueClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.LearningModel;
import eu.excitementproject.eop.biutee.classifiers.scaling.LinearScalingTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.scaling.ScalingClassifier;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.RteSumSingleCandidateResult;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.SVMPerfNative;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswerScoreComputer;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileWriter;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileWriter;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * No longer used.
 * TODO (comment by Asher Stern): This class is not written well (but it works, however).
 * <P>
 * This class (now) uses {@link SVMPerfNative} so the user must put $JARS/svm/svm_perf_libraries/win64 in the PATH
 * 
 * @author Asher Stern
 * @since Aug 3, 2011
 *
 */
public class ExternalClassifierRunner
{
	public static final boolean RUN_EXTERNAL_PROGRAM = true;
	public static final boolean MAKE_SCALING = true;
	public static final String TRAIN_SAMPLES_FILE_NAME = "train";
	public static final String MODEL_FILE_NAME = "model";
	public static final String PREDICTIONS_FILE_NAME = "predictions";
	public static final String PREDICTIONS_FILE_NAME_FOR_TEST = "predictions_test";
	public static final String SVM_LEARN = OS.programName("svm_perf_learn");
	public static final String SVM_CLASSIFY = OS.programName("svm_perf_classify");
	
	public static final String TRAINING_ANSWER_FILE_NAME = "external_answer_training.xml";
	public static final String TEST_ANSWER_FILE_NAME = "external_answer_test.xml";
	public static final String TEST_SAMPLES_FILE_NAME = "test";
	
	private static final String SVM_ARGS_POSTFIX = " -l 1 -w 3";
	private static final String SVM_ARGS_PREFIX = "-c ";
	private static final int DEFAULT_C_ARG = 100;
	
	public static String svm_args = SVM_ARGS_PREFIX + DEFAULT_C_ARG + SVM_ARGS_POSTFIX;
	public static String external_classifier_command_learn = svm_args+" "+TRAIN_SAMPLES_FILE_NAME+" "+MODEL_FILE_NAME;
	
	/**
	 * input:	trainingResultsFileName trainingGsFileName [testResultsFileName [testGsFileName]] [svm_c_param]
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			LinkedList<String> argsQueue = new LinkedList<String>(Utils.arrayToCollection(args, new LinkedList<String>()));
			
			if (argsQueue.isEmpty()) throw new RuntimeException("args:	trainingResultsFileName trainingGsFileName [testResultsFileName [testGsFileName]] [svm_c_param]");
			String trainingResultsFileName = argsQueue.poll();
			if (argsQueue.isEmpty()) throw new RuntimeException("args:	trainingResultsFileName trainingGsFileName [testResultsFileName [testGsFileName]] [svm_c_param]");
			String trainingGsFileName = argsQueue.poll();

			if (!argsQueue.isEmpty())
				// read c param (which must be the last arg, if it's there at all) and pop it out of args
				try { 
					double c = Double.parseDouble(argsQueue.getLast());
					
					// if no NumberFormatException was raised above, then we indeed have a C arg
					argsQueue.removeLast();
					svm_args = SVM_ARGS_PREFIX + c + SVM_ARGS_POSTFIX;
					external_classifier_command_learn = svm_args+" "+TRAIN_SAMPLES_FILE_NAME+" "+MODEL_FILE_NAME;
					System.out.println("The new learn command: " + external_classifier_command_learn);
				}
				catch (NumberFormatException e)	{}
			
			if (argsQueue.isEmpty())
			{
				System.out.println("Test not given. Calculation will be performed on training only.");
				goByFiles(trainingResultsFileName,trainingGsFileName,null,null);
			}
			else
			{
				String testResultsFileName = argsQueue.poll();
				String testGsFileName=null;
				if (argsQueue.isEmpty())
				{
					System.out.println("Gold standard for test not given.\n" +
					"For test only answer file will be created, but no R/P/F1 will be calculated.");
				}
				else
				{
					testGsFileName=argsQueue.poll();
				}

//				if (args.length>(index))
//				{
//					double c = Double.parseDouble(args[index]);
//					svm_args = SVM_ARGS_PREFIX + c + SVM_ARGS_POSTFIX;
//					external_classifier_command_learn = svm_args+" "+TRAIN_SAMPLES_FILE_NAME+" "+MODEL_FILE_NAME;
//					System.out.println("The new learn command: " + external_classifier_command_learn);
//				}
				
				goByFiles(trainingResultsFileName,trainingGsFileName,testResultsFileName,testGsFileName);
			}

			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static void goByFiles(String trainingResultsFileName, String trainingGoldStandardFileName, String testResultsFileName, String testGoldStandardFileName) throws FileNotFoundException, IOException, ClassNotFoundException, Rte6mainIOException, TeEngineMlException, InterruptedException, ClassifierException
	{
		System.out.println("MAKE_SCALING = " + MAKE_SCALING);
		
		System.out.println("Reading training results.");
		Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> trainingResults;
		ObjectInputStream serStream = new ObjectInputStream(new FileInputStream(new File(trainingResultsFileName)));
		try
		{
			trainingResults = (Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>>) serStream.readObject();
		}
		finally
		{
			serStream.close();
		}
		System.out.println("Memory used = "+Utils.stringMemoryUsedInMB());
		

		System.out.println("Reading training gold standard");
		Map<String, Map<String, Set<SentenceIdentifier>>> trainingGoldStandard = readGoldStandard(trainingGoldStandardFileName);
		
		
		if (null==testResultsFileName)
		{
			ExternalClassifierRunner app = new ExternalClassifierRunner(trainingResults,null,trainingGoldStandard,null,trainingGoldStandardFileName,null);
			trainingResults=null;
			trainingGoldStandard=null;
			app.go(false);
		}
		else
		{
			System.out.println("Reading test results.");
			Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> testResults;
			serStream = new ObjectInputStream(new FileInputStream(new File(testResultsFileName)));
			try
			{
				testResults = (Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>>) serStream.readObject();
			}
			finally
			{
				serStream.close();
			}
			System.out.println("Memory used = "+Utils.stringMemoryUsedInMB());

			Map<String, Map<String, Set<SentenceIdentifier>>> testGoldStandard=null;
			if (testGoldStandardFileName!=null)
			{
				System.out.println("Reading test gold standard");
				testGoldStandard = readGoldStandard(testGoldStandardFileName);
			}
			
			ExternalClassifierRunner app =
				new ExternalClassifierRunner(trainingResults,testResults,trainingGoldStandard,testGoldStandard,trainingGoldStandardFileName,testGoldStandardFileName);
			trainingResults=null;
			trainingGoldStandard=null;
			testResults=null;
			testGoldStandard=null;
			app.go(true);
		}
	}
	
	
	


	public ExternalClassifierRunner(
			Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> resultsTraining,
			Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> resultsTest,
			Map<String, Map<String, Set<SentenceIdentifier>>> trainingGoldStandardAnswers,
			Map<String, Map<String, Set<SentenceIdentifier>>> testGoldStandardAnswers,
			String trainingGoldStandardFileName, String testGoldStandardFileName)
	{
		super();
		this.resultsTraining = resultsTraining;
		this.resultsTest = resultsTest;
		this.trainingGoldStandardAnswers = trainingGoldStandardAnswers;
		this.testGoldStandardAnswers = testGoldStandardAnswers;
		this.trainingGoldStandardFileName = trainingGoldStandardFileName;
		this.testGoldStandardFileName = testGoldStandardFileName;
	}








	public void go(boolean testset) throws TeEngineMlException, IOException, Rte6mainIOException, InterruptedException, ClassifierException
	{
		System.out.println("Convert training results.");
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>>> resultsTrainingLHM =
			convertToLinkedHashMap(resultsTraining);
		resultsTraining=null;
		System.gc();
		System.out.println("Memory used = "+Utils.stringMemoryUsedInMB());
		
		
		// build the model using training data
		System.out.println("Creating labeled-samples for training.");
		ResultsToSamples resultsToSamples = new ResultsToSamples(resultsTrainingLHM,trainingGoldStandardAnswers);
		resultsToSamples.createSamples();
		Vector<LabeledSample> samples = resultsToSamples.getSamples();
		LinearScalingTrainableStorableClassifier scalingClassifier = null;
		if (MAKE_SCALING)
		{
			scalingClassifier = new LinearScalingTrainableStorableClassifier(new DummyLinearTrainableStroableAllTrueClassifier());
			scalingClassifier.train(samples);
			samples = getNormalizedSamples(samples, scalingClassifier);
		}
		
		StringBuffer stringBufferSamples = ClassifierUtils.printSamplesAsSvmLightInput(samples, false);
		System.out.println("Writing the samples to: "+TRAIN_SAMPLES_FILE_NAME);
		PrintWriter writer = new PrintWriter(new File(TRAIN_SAMPLES_FILE_NAME));
		try
		{
			writer.println(stringBufferSamples.toString());
		}
		finally
		{
			writer.close();
		}
		
		if (RUN_EXTERNAL_PROGRAM)
		{
			String commandLine = SVM_LEARN+" "+external_classifier_command_learn;
			System.out.println("Running external classifier as an external program.");
			System.out.println("Running command: "+commandLine);
			Process processLearn = Runtime.getRuntime().exec(commandLine);
			if (OS.isWindows())
			{
				processLearn.getErrorStream().close();
				processLearn.getInputStream().close();
				processLearn.getOutputStream().close();
			}
			int statusProcessLearn = processLearn.waitFor();
			System.out.println("learn executed and exited with exit code: "+statusProcessLearn);
			if (statusProcessLearn!=0)
				throw new RuntimeException("process exited with status: "+statusProcessLearn);
		}
		else
		{
			System.out.println("Execute learn-classifier via svmperf_lean.dll: "+external_classifier_command_learn);
			SVMPerfNative.svmPerfLearn(external_classifier_command_learn);
		}
		System.out.println("Done.");
		
		
		// create prediction file for training data
		System.out.println("Execute classifier: "+SVM_CLASSIFY);
		String[] cmdClassifyTrainingData =
			new String[]{SVM_CLASSIFY,TRAIN_SAMPLES_FILE_NAME,MODEL_FILE_NAME,PREDICTIONS_FILE_NAME};
		

		if (RUN_EXTERNAL_PROGRAM)
		{
			System.out.println("Executing command: "+stringArrayToString(cmdClassifyTrainingData));
			Process processClassifyTraining = Runtime.getRuntime().exec(cmdClassifyTrainingData);
			if (OS.isWindows())
			{
				processClassifyTraining.getErrorStream().close();
				processClassifyTraining.getInputStream().close();
				processClassifyTraining.getOutputStream().close();
			}
			int statusProcessClassifyTraining = processClassifyTraining.waitFor();
			System.out.println("classify executed and exited with exit code: "+statusProcessClassifyTraining);
			if (statusProcessClassifyTraining!=0)
				throw new RuntimeException("process exited with status: "+statusProcessClassifyTraining);
		}
		else
		{
			System.out.println("Running native function SVMPerfNative.svmPerfClassify with parameters: "+TRAIN_SAMPLES_FILE_NAME+" "+MODEL_FILE_NAME+" "+PREDICTIONS_FILE_NAME);
			SVMPerfNative.svmPerfClassify(TRAIN_SAMPLES_FILE_NAME+" "+MODEL_FILE_NAME+" "+PREDICTIONS_FILE_NAME);
		}
		System.out.println("Done.");

		
		// create answer file for training data
		System.out.println("Create answer file according the predictions of classifier.");
		Vector<Boolean> trainingDataPrediction =
			classificationFromPredictionFile(new File(PREDICTIONS_FILE_NAME));
		
		AnswerFromExternalPredictions answerCreator =
			new AnswerFromExternalPredictions(resultsTrainingLHM,trainingDataPrediction);
		answerCreator.createAnswers();
		LinkedHashMap<String, LinkedHashMap<String, Set<SentenceIdentifier>>> answersLHM =
			answerCreator.getAnswers();
		
		Map<String, Map<String, Set<SentenceIdentifier>>> answers = answerLHM_toMap(answersLHM);
		
		System.out.println("Answers were created in memory. Writing to file: "+TRAINING_ANSWER_FILE_NAME);
		AnswersFileWriter trainingAnswersFileWriter = new DefaultAnswersFileWriter();
		trainingAnswersFileWriter.setWriteTheEvaluationAttribute(false);
		trainingAnswersFileWriter.setXml(TRAINING_ANSWER_FILE_NAME);
		trainingAnswersFileWriter.setAnswers(answers);
		trainingAnswersFileWriter.write();
		
		System.out.println("Computing score.");
		AnswerScoreComputer trainingAnswerComputer = new AnswerScoreComputer(trainingGoldStandardFileName,TRAINING_ANSWER_FILE_NAME);
		trainingAnswerComputer.compute();
		System.out.println("Results on training data");
		System.out.println(trainingAnswerComputer.getResultsAsString());
		
		
		if (testset)
		{
			System.out.println("Convert test results.");
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>>> resultsTestLHM =
				convertToLinkedHashMap(resultsTest);
			resultsTest=null;
			System.gc();
			System.out.println("Memory used = "+Utils.stringMemoryUsedInMB());
			
			System.out.println("Creating labeled-samples for test.");
			ResultsToSamples resultsToSamplesForTest = new ResultsToSamples(resultsTestLHM,testGoldStandardAnswers);
			resultsToSamplesForTest.createSamples();
			Vector<LabeledSample> samplesTest = resultsToSamplesForTest.getSamples();
			if (MAKE_SCALING)
			{
				samplesTest = getNormalizedSamples(samplesTest, scalingClassifier);
			}
			StringBuffer stringBufferSamplesForTest = ClassifierUtils.printSamplesAsSvmLightInput(samplesTest, false);
			System.out.println("Writing the samples to: "+TEST_SAMPLES_FILE_NAME);
			PrintWriter writerForTest = new PrintWriter(new File(TEST_SAMPLES_FILE_NAME));
			try
			{
				writerForTest.println(stringBufferSamplesForTest.toString());
			}
			finally
			{
				writerForTest.close();
			}
			
			
			// create prediction file for test data
			System.out.println("Execute classifier: "+SVM_CLASSIFY);
			String[] cmdClassifyTestData =
				new String[]{SVM_CLASSIFY,TEST_SAMPLES_FILE_NAME,MODEL_FILE_NAME,PREDICTIONS_FILE_NAME_FOR_TEST};
			
			if (RUN_EXTERNAL_PROGRAM)
			{
				System.out.println("Executing command: "+stringArrayToString(cmdClassifyTestData));
				Process processClassifyTest = Runtime.getRuntime().exec(cmdClassifyTestData);
				if (OS.isWindows())
				{
					processClassifyTest.getErrorStream().close();
					processClassifyTest.getInputStream().close();
					processClassifyTest.getOutputStream().close();
				}
				int statusProcessClassifyTest = processClassifyTest.waitFor();
				System.out.println("classify executed and exited with exit code: "+statusProcessClassifyTest);
				if (statusProcessClassifyTest!=0)
					throw new RuntimeException("process exited with status: "+statusProcessClassifyTest);
			}
			else
			{
				System.out.println("Running native function SVMPerfNative.svmPerfClassify with parameters: "+TEST_SAMPLES_FILE_NAME+" "+MODEL_FILE_NAME+" "+PREDICTIONS_FILE_NAME_FOR_TEST);
				SVMPerfNative.svmPerfClassify(TEST_SAMPLES_FILE_NAME+" "+MODEL_FILE_NAME+" "+PREDICTIONS_FILE_NAME_FOR_TEST);
			}
			System.out.println("Done.");
			
			// create answer file for test data
			System.out.println("Create answer file according the predictions (for test) of classifier.");
			Vector<Boolean> testDataPrediction =
				classificationFromPredictionFile(new File(PREDICTIONS_FILE_NAME_FOR_TEST));
			
			AnswerFromExternalPredictions answerCreatorForTest =
				new AnswerFromExternalPredictions(resultsTestLHM,testDataPrediction);
			answerCreatorForTest.createAnswers();
			LinkedHashMap<String, LinkedHashMap<String, Set<SentenceIdentifier>>> answersForTestLHM =
				answerCreatorForTest.getAnswers();
			
			Map<String, Map<String, Set<SentenceIdentifier>>> answersForTest = answerLHM_toMap(answersForTestLHM);
			
			
			System.out.println("Answers for test were created in memory. Writing to file: "+TEST_ANSWER_FILE_NAME);
			AnswersFileWriter testAnswersFileWriter = new DefaultAnswersFileWriter();
			testAnswersFileWriter.setWriteTheEvaluationAttribute(false);
			testAnswersFileWriter.setXml(TEST_ANSWER_FILE_NAME);
			testAnswersFileWriter.setAnswers(answersForTest);
			testAnswersFileWriter.write();
			
			if (this.testGoldStandardAnswers!=null)
			{
				System.out.println("Computing score.");
				AnswerScoreComputer testAnswerComputer = new AnswerScoreComputer(testGoldStandardFileName,TEST_ANSWER_FILE_NAME);
				testAnswerComputer.compute();
				System.out.println("Results on test data");
				System.out.println(testAnswerComputer.getResultsAsString());
			}
			else
			{
				System.out.println("Score computing for test will not be performed, since gold-standard file was not supplied.");
			}
		}
	}
	
	private static Map<String, Map<String, Set<SentenceIdentifier>>> readGoldStandard(String goldStandardFileName) throws Rte6mainIOException
	{
		AnswersFileReader reader = new DefaultAnswersFileReader();
		reader.setXml(goldStandardFileName);
		//reader.setGoldStandard(true);
		reader.read();
		return reader.getAnswers();
	}

	private static LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>>>
	convertToLinkedHashMap(Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> results)
	{
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>>> ret =
			new LinkedHashMap<String, LinkedHashMap<String,LinkedHashMap<SentenceIdentifier,RteSumSingleCandidateResult>>>();
		for (String topidId : results.keySet())
		{
			Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>> topicResults = results.get(topidId);
			LinkedHashMap<String, LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>> topicRet =
				new LinkedHashMap<String, LinkedHashMap<SentenceIdentifier,RteSumSingleCandidateResult>>();
			
			for (String hypothesisId : topicResults.keySet())
			{
				Map<SentenceIdentifier, RteSumSingleCandidateResult> hypothesisResults = topicResults.get(hypothesisId);
				LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult> hypothesisRet =
					new LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>();
				
				for (SentenceIdentifier sentenceId : hypothesisResults.keySet())
				{
					hypothesisRet.put(sentenceId,hypothesisResults.get(sentenceId));
				}
				
				topicRet.put(hypothesisId,hypothesisRet);
			}
			
			ret.put(topidId,topicRet);
		}
		return ret;
	}
	
	private static Map<String, Map<String, Set<SentenceIdentifier>>>
	answerLHM_toMap(LinkedHashMap<String, LinkedHashMap<String, Set<SentenceIdentifier>>> answerLHM)
	{
		Map<String, Map<String, Set<SentenceIdentifier>>> ret = new LinkedHashMap<String, Map<String,Set<SentenceIdentifier>>>();
		for (String topicId : answerLHM.keySet())
		{
			LinkedHashMap<String, Set<SentenceIdentifier>> topicAnswerLHM = answerLHM.get(topicId);
			Map<String, Set<SentenceIdentifier>> retTopic = new LinkedHashMap<String, Set<SentenceIdentifier>>();
			for (String hypothesisId : topicAnswerLHM.keySet())
			{
				retTopic.put(hypothesisId, topicAnswerLHM.get(hypothesisId));
			}
			ret.put(topicId, retTopic);
		}
		
		return ret;
	}
	
	private static Vector<Boolean> classificationFromPredictionFile(File predictions) throws IOException
	{
		Vector<Boolean> ret = new Vector<Boolean>();
		BufferedReader reader = new BufferedReader(new FileReader(predictions));
		try
		{
			String line = reader.readLine();
			while (line != null)
			{
				line = line.trim();
				if (line.length()>0)
				{
					double predictedValue = Double.parseDouble(line);
					if (predictedValue<0)
						ret.add(false);
					else
						ret.add(true);
				}
				line = reader.readLine();
			}
		}
		finally
		{
			reader.close();
		}
		return ret;
	}
	
	private static String stringArrayToString(String[] array)
	{
		StringBuffer sb = new StringBuffer();
		for (String str : array)
		{
			sb.append(str);
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private static Vector<LabeledSample> getNormalizedSamples(Vector<LabeledSample> samples, ScalingClassifier scalingClassifier) throws ClassifierException
	{
		Vector<LabeledSample> ret = new Vector<LabeledSample>();
		for (LabeledSample sample : samples)
		{
			ret.add(scalingClassifier.getNormalizedSample(sample));
		}
		
		return ret;
	}
	
	
	private static final class DummyLinearTrainableStroableAllTrueClassifier extends DummyAllTrueClassifier implements LinearTrainableStorableClassifier
	{
		@Override
		public ImmutableMap<Integer, Double> getWeights()
				throws ClassifierException
		{
			throw new ClassifierException("Not implemented");
		}

		@Override
		public double getThreshold() throws ClassifierException
		{
			throw new ClassifierException("Not implemented");
		}

		@Override
		public double getProduct(Map<Integer, Double> featureVector)
				throws ClassifierException
		{
			throw new ClassifierException("Not implemented");
		}

		@Override
		public LearningModel store() throws ClassifierException
		{
			throw new ClassifierException("Not implemented");
		}
	}
	
	private Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> resultsTraining;
	private Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> resultsTest=null;
	private Map<String, Map<String, Set<SentenceIdentifier>>> trainingGoldStandardAnswers;
	private Map<String, Map<String, Set<SentenceIdentifier>>> testGoldStandardAnswers=null;
	private String trainingGoldStandardFileName;
	private String testGoldStandardFileName=null;
	
}
