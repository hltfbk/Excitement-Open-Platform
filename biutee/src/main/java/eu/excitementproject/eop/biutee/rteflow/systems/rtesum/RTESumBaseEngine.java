package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_ENGINE_NUMBER_OF_THREADS_PARAMETER_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_DATASET_DIR_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_IS_NOVELTY_TASK_FLAG;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_PREPROCESS_SERIALIZATION_FILE_NAME;
import static eu.excitementproject.eop.transformations.utilities.Constants.LEARNING_MODEL_FILE_POSTFIX;
import static eu.excitementproject.eop.transformations.utilities.Constants.LEARNING_MODEL_FILE_PREFIX;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.io.StorableClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.PreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamplesUtils;
import eu.excitementproject.eop.biutee.utilities.safemodel.classifiers_io.SafeClassifiersIO;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExperimentManager;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.FileSystemNamesFactory;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6FileSystemNames;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

import static eu.excitementproject.eop.transformations.utilities.Constants.RTESUM_DATASET_PARAM_DELIMITER;
/**
 * 
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
public class RTESumBaseEngine extends SystemInitialization
{
	public RTESumBaseEngine(String configurationFileName)
	{
		super(configurationFileName, ConfigurationParametersNames.RTE_SUM_TRAIN_AND_TEST_MODULE_NAME);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init() throws ConfigurationFileDuplicateKeyException, ConfigurationException, LemmatizerException, TeEngineMlException, FileNotFoundException, IOException, PluginAdministrationException
	{
		super.init();
		logger.info("Initializing...");
		
		String serializedTopicsFileName = configurationParams.get(RTE_SUM_PREPROCESS_SERIALIZATION_FILE_NAME);
		logger.info("Reading all topic from serialization file: "+serializedTopicsFileName);
		ObjectInputStream serStream = new ObjectInputStream(new FileInputStream(new File(serializedTopicsFileName)));
		try
		{
			topics = (List<PreprocessedTopicDataSet>) serStream.readObject();
			
			//File datasetDir = configurationParams.getDirectory(RTE_SUM_DATASET_DIR_NAME);
			
			File datasetDir = retrieveDatasetDirAndSetFileSystemNames();
			
			File goldStandardFile = new File(datasetDir,fileSystemNames.getGoldStandardFileName());
			if (goldStandardFile.exists()&&goldStandardFile.isFile())
			{
				goldStandardFileName = goldStandardFile.getPath();
				logger.info("Retrieving gold-standard file: "+goldStandardFileName);
				AnswersFileReader gsReader = new DefaultAnswersFileReader();
				gsReader.setXml(goldStandardFileName);
				gsReader.read();
				goldStandardAnswers = gsReader.getAnswers();
			}
			else
			{
				logger.info("No gold-standard exist.");
				goldStandardFileName = null;
				goldStandardAnswers = null;
			}
			
			this.numberOfThreads = configurationParams.getInt(RTE_ENGINE_NUMBER_OF_THREADS_PARAMETER_NAME);
			logger.info("Number of threads = "+numberOfThreads);
			
			logger.info("Retrieving rules bases names.");
			OperationsScript<Info, BasicNode> scriptOnlyForNames = new ScriptFactory(configurationFile,teSystemEnvironment.getPluginRegistry()).getDefaultScript();
			scriptOnlyForNames.init();
			ruleBasesNamesInitialized = true;
			this.ruleBasesNames = scriptOnlyForNames;
			completeInitializationWithScript(ruleBasesNames);
			
			if (logger.isInfoEnabled())
			{
				logger.info("The following rule bases have been found:");
				StringBuffer sb = new StringBuffer();
				for (String ruleBaseName : this.ruleBasesNames.getRuleBasesNames())
				{
					sb.append(ruleBaseName);
					sb.append(", ");
				}
				logger.info(sb.toString());
			}
			
			logger.info(this.getClass().getSimpleName()+": Initialization done.");
		}
		catch (ClassNotFoundException e)
		{
			throw new TeEngineMlException("Initialization failure",e);
		}
		catch (Rte6mainIOException e)
		{
			throw new TeEngineMlException("Initialization failure",e);
		}
		catch (OperationException e)
		{
			throw new TeEngineMlException("Initialization failure",e);
		}
		finally
		{
			serStream.close();
		}
	}
	
	public void cleanUp()
	{
		super.cleanUp();
		if (ruleBasesNamesInitialized)
			ruleBasesNames.cleanUp();
	}
	
	
	/**
	 * Writes the results to a serialization file.
	 * 
	 * @param results as follows:
	 * <pre>
	 * Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>>
	 *        ^           ^              ^                    ^
	 *        |           |              |                    |
	 *    topic-id  hypothesis-id  sentence-id          its-results
	 * </pre>
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void writeResults(Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> results, String fileName) throws FileNotFoundException, IOException
	{
		ObjectOutputStream serStream = new ObjectOutputStream(new FileOutputStream(new File(fileName)));
		try
		{
			serStream.writeObject(results);
		}
		finally
		{
			serStream.close();
		}
	}
	
	/**
	 * Writes the samples (which are feature vectors+labels) to a serialization file.
	 * 
	 * @param samples
	 * @param fileName
	 * @throws IOException 
	 * @throws TeEngineMlException 
	 * @throws FileNotFoundException 
	 * @throws PluginAdministrationException 
	 */
	protected void writeSamples(Vector<LabeledSample> samples, String fileName) throws FileNotFoundException, TeEngineMlException, IOException, PluginAdministrationException
	{
		File serFile = new File(fileName);
		SafeSamplesUtils.store(serFile, SafeSamplesUtils.create(samples, teSystemEnvironment.getFeatureVectorStructureOrganizer()));
	}
	
	protected void storeClassifier(StorableClassifier classifier, int loopIndex, String searchOrPredictionsIndicator, String pathToStoreLabledSamples) throws TeEngineMlException
	{
		String storeClassifierFileName = LEARNING_MODEL_FILE_PREFIX+"_"+searchOrPredictionsIndicator+"_"+loopIndex+LEARNING_MODEL_FILE_POSTFIX;
		File storeClassifierForSearchFile;
		if (pathToStoreLabledSamples!=null)
		{
			storeClassifierForSearchFile = new File(new File(pathToStoreLabledSamples),storeClassifierFileName);	
		}
		else
		{
			storeClassifierForSearchFile = new File(storeClassifierFileName);
		}
		SafeClassifiersIO.store(classifier, teSystemEnvironment.getFeatureVectorStructureOrganizer(), storeClassifierForSearchFile);
		ExperimentManager.getInstance().register(storeClassifierForSearchFile);

	}
	
	protected Map<String , Map<String, Map<SentenceIdentifier, SingleClassification>>> classifyAll(Classifier classifier, Map<String , Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults) throws ClassifierException
	{
		Map<String , Map<String, Map<SentenceIdentifier, SingleClassification>>> ret = new LinkedHashMap<String, Map<String,Map<SentenceIdentifier,SingleClassification>>>();
		for (String topicId : allTopicsResults.keySet())
		{
			Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>> topicResults = allTopicsResults.get(topicId);
			Map<String, Map<SentenceIdentifier, SingleClassification>> topicClassifications = new LinkedHashMap<String, Map<SentenceIdentifier,SingleClassification>>();
			for (String hypothesisId : topicResults.keySet())
			{
				Map<SentenceIdentifier, RteSumSingleCandidateResult> hypothesisResults = topicResults.get(hypothesisId);
				Map<SentenceIdentifier, SingleClassification> hypothesisClassifications = new LinkedHashMap<SentenceIdentifier, SingleClassification>();
				for (SentenceIdentifier sentenceId : hypothesisResults.keySet())
				{
					RteSumSingleCandidateResult result = hypothesisResults.get(sentenceId);
					Map<Integer,Double> featureVector = result.getFeatureVector();
					double score = classifier.classify(featureVector);
					boolean classification = ClassifierUtils.classifierResultToBoolean(score);
					SingleClassification singleClassification = new SingleClassification(score, classification);
					hypothesisClassifications.put(sentenceId, singleClassification);
				}
				topicClassifications.put(hypothesisId, hypothesisClassifications);
			}
			ret.put(topicId, topicClassifications);
		}
		return ret;
	}
	
	protected void logResult(Map<String , Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults, Map<String , Map<String, Map<SentenceIdentifier, SingleClassification>>> allTopicsClassifications) throws ClassifierException
	{
		logResult(allTopicsResults,allTopicsClassifications,null);
	}
	protected void logResult(Map<String , Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults, Map<String , Map<String, Map<SentenceIdentifier, SingleClassification>>> allTopicsClassifications, LinearClassifier classifierForSearch) throws ClassifierException
	{
		//private Map<String , Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults = null;
		// private Map<String,Map<String,Set<SentenceIdentifier>>> goldStandardAnswers;
		long count = 0;
		long sumCpuTime = 0;
		long sumExpand = 0;
		long sumGenerated = 0;
		double sumCosts = 0.0;
		for (String topicId : allTopicsResults.keySet())
		{
			logger.info("Results for topic: "+topicId);
			Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>> topicResults = allTopicsResults.get(topicId);
			Map<String, Map<SentenceIdentifier, SingleClassification>> topicClassifications = allTopicsClassifications.get(topicId);			
			Map<String,Set<SentenceIdentifier>> gsTopic = null;
			if (goldStandardAnswers!=null)
				gsTopic = goldStandardAnswers.get(topicId);
			for (String hypothesisId : topicResults.keySet())
			{
				logger.info("Results for hypothesis: "+hypothesisId);
				Map<SentenceIdentifier, RteSumSingleCandidateResult> hypothesisResults = topicResults.get(hypothesisId);
				Map<SentenceIdentifier, SingleClassification> hypothesisClassifications = topicClassifications.get(hypothesisId);
				Set<SentenceIdentifier> gsHypothesis = null;
				if (goldStandardAnswers!=null)
					gsHypothesis = gsTopic.get(hypothesisId);
				for (SentenceIdentifier sentenceId : hypothesisResults.keySet())
				{
					RteSumSingleCandidateResult candidateResult = hypothesisResults.get(sentenceId);
					SingleClassification candidateClassification = hypothesisClassifications.get(sentenceId);
					StringBuffer sb = new StringBuffer();
					sb.append("Results for ");
					sb.append(hypothesisId);
					sb.append(": ");
					sb.append(sentenceId.getDocumentId());
					sb.append(" / ");
					sb.append(sentenceId.getSentenceId());
					sb.append("\n");
					
					sb.append("T = ");
					sb.append(candidateResult.getSentenceString());
					sb.append("\n");
					sb.append("H = ");
					sb.append(candidateResult.getHypothesisString());
					sb.append("\n");
					sb.append("Gold Standard = ");
					if (goldStandardAnswers!=null)
					{
						sb.append(gsHypothesis.contains(sentenceId));
					}
					else
					{
						sb.append("unknown");
					}
					sb.append(". Answer = ");
					sb.append(candidateClassification.isClassification());
					sb.append(" ");
					sb.append(strDouble(candidateClassification.getScore()));
					sb.append("\n");
					
					for (Map.Entry<Integer, Double> feature : candidateResult.getFeatureVector().entrySet())
					{
						sb.append(feature.getKey());
						sb.append(":");
						sb.append(strDouble(feature.getValue()));
						sb.append(" ");
					}
					sb.append("\n");
					
					for (Specification spec : candidateResult.getHistory().getSpecifications())
					{
						sb.append(spec.toString());
						sb.append("\n");
					}
					
					logger.info(sb.toString());
					
					count++;
					if (Constants.PRINT_TIME_STATISTICS)
					{
						sumCpuTime += candidateResult.getCpuTime();
						sumExpand += candidateResult.getNumberOfExpanded();
						sumGenerated += candidateResult.getNumberOfGenerated();
						double cost = -classifierForSearch.getProduct(candidateResult.getFeatureVector());
						sumCosts += cost;
					}
				}
				
			}
		}
		logAverageTimes( sumCpuTime,  sumExpand,  sumGenerated,  sumCosts,  count);
	}
	
	private final void logAverageTimes(long sumCpuTime, long sumExpand, long sumGenerated, double sumCosts, long count)
	{
		if (Constants.PRINT_TIME_STATISTICS)
		{
			logger.info(
					"Average CPU time = " + sumCpuTime/count
					);
			logger.info(
					"Average expanded = " + sumExpand/count
					);
			logger.info(
					"Average generated = " + sumGenerated/count
					);
			logger.info(
					"Average cost = " + String.format("%-4.4f", sumCosts/((double)count))
					);

		}
		
	}
	
	
	protected Map<String,Map<String,Set<SentenceIdentifier>>> createAnswer(Map<String , Map<String, Map<SentenceIdentifier, SingleClassification>>> allTopicsClassifications)
	{
		Map<String,Map<String,Set<SentenceIdentifier>>> ret = new LinkedHashMap<String, Map<String,Set<SentenceIdentifier>>>();
		for (String topicID : allTopicsClassifications.keySet())
		{
			Map<String, Map<SentenceIdentifier, SingleClassification>> topicClassifications = allTopicsClassifications.get(topicID);
			Map<String,Set<SentenceIdentifier>> topicAnswer = new LinkedHashMap<String, Set<SentenceIdentifier>>();
			for (String hypothesisID : topicClassifications.keySet())
			{
				Map<SentenceIdentifier, SingleClassification> hypothesisClassification = topicClassifications.get(hypothesisID);
				Set<SentenceIdentifier> hypothesisAnswer = new LinkedHashSet<SentenceIdentifier>();
				for (SentenceIdentifier sentenceID : hypothesisClassification.keySet())
				{
					SingleClassification candidateClassification = hypothesisClassification.get(sentenceID);
					if (candidateClassification.isClassification())
					{
						hypothesisAnswer.add(sentenceID);
					}
				}
				
				topicAnswer.put(hypothesisID,hypothesisAnswer);
			}
			
			ret.put(topicID,topicAnswer);
		}
		return ret;
	}
	
	
	protected static String strDouble(double d)
	{
		return String.format("%-3.6f", d);
	}
	
	private File retrieveDatasetDirAndSetFileSystemNames() throws ConfigurationException, TeEngineMlException
	{
		//File datasetDir = configurationParams.getDirectory(RTE_SUM_DATASET_DIR_NAME);
		String datasetParameterValue = configurationParams.get(RTE_SUM_DATASET_DIR_NAME);
		String[] datasetValueComponents = datasetParameterValue.split(RTESUM_DATASET_PARAM_DELIMITER);
		Iterator<String> datasetValueIterator = Utils.arrayToCollection(datasetValueComponents, new LinkedList<String>()).iterator();
		TeEngineMlException badDatasetValueException = new TeEngineMlException("Bad value for dataset name: \""+datasetParameterValue+"\". Should be annual-flag"+RTESUM_DATASET_PARAM_DELIMITER+"dev-test-flag"+RTESUM_DATASET_PARAM_DELIMITER+"path" +
				"\nAnnual flag should be: "+FileSystemNamesFactory.RTE6_FLAG+" or "+FileSystemNamesFactory.RTE7_FLAG+
				"\ndev-test-flag should be: "+FileSystemNamesFactory.DEV_FLAG+" or "+FileSystemNamesFactory.TEST_FLAG);
		if (datasetValueIterator.hasNext()) throw badDatasetValueException;
		String annualFlag = datasetValueIterator.next().trim();
		if (datasetValueIterator.hasNext()) throw badDatasetValueException;
		String devTestFlag = datasetValueIterator.next().trim();
		if (datasetValueIterator.hasNext()) throw badDatasetValueException;
		String datasetDirAsString = datasetValueIterator.next().trim();
		datasetDirAsString = FileUtils.normalizeCygwinPathToWindowsPath(datasetDirAsString);
		File datasetDir = new File(datasetDirAsString);
		datasetDir = FileUtils.normalizeFileNameByOS(datasetDir);
		
		
		boolean isNoveltyTask = false;
		if (configurationParams.containsKey(RTE_SUM_IS_NOVELTY_TASK_FLAG))
			isNoveltyTask = configurationParams.getBoolean(RTE_SUM_IS_NOVELTY_TASK_FLAG);
		logger.info("Loading " + (isNoveltyTask ? "Novelty" : "Main") + " task files");
		
		fileSystemNames = FileSystemNamesFactory.chooseFilteredFileSystemNames(annualFlag,devTestFlag, datasetDir, isNoveltyTask);
		logger.info("Working on folders of type: " + fileSystemNames.getClass().getSimpleName()+
				"( "+FileSystemNamesFactory.chooseUnfilteredFileSystemNames(annualFlag,devTestFlag, datasetDir, isNoveltyTask).getClass().getSimpleName()+" )");
		
		return datasetDir;
	}

	
	
	
	protected Rte6FileSystemNames fileSystemNames;
	
	protected List<PreprocessedTopicDataSet> topics;
	protected Map<String, Map<String, Set<SentenceIdentifier>>> goldStandardAnswers;
	protected String goldStandardFileName;
	protected int numberOfThreads;
	protected OperationsScript<?,?> ruleBasesNames;
	protected boolean ruleBasesNamesInitialized = false;
	
	
	private static final Logger logger = Logger.getLogger(RTESumBaseEngine.class);
}
