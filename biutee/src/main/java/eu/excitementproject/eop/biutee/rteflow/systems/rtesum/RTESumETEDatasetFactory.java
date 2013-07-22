package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.RTESUM_DATASET_PARAM_DELIMITER;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_IS_NOVELTY_TASK_FLAG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumDataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumDatasetContents;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumInstance;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedPreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedTopicDataSetGenerator;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.PreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.datastructures.Envelope;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.AnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DefaultAnswersFileReader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.FileSystemNamesFactory;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6FileSystemNames;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jul 22, 2013
 *
 */
public class RTESumETEDatasetFactory
{
	
	public static Dataset<RteSumInstance> createDataset(ConfigurationParams configurationParams, String serialized_parameterName, String rawData_parameterName, TESystemEnvironment teSystemEnvironment) throws BiuteeException
	{
		try
		{
			Envelope<List<PreprocessedTopicDataSet>> topicsEnvelope = new Envelope<List<PreprocessedTopicDataSet>>();
			RteSumDatasetContents datasetContents = createDatasetContents(configurationParams,serialized_parameterName,rawData_parameterName,teSystemEnvironment,topicsEnvelope);
			List<CandidateIdentifier> candidates = buildListOfCandidates(topicsEnvelope.getT());
			return new RteSumDataset(datasetContents, candidates);
		}
		catch (ClassNotFoundException
				| TeEngineMlException | AnnotatorException
				| ConfigurationException | IOException | Rte6mainIOException
				| TreeStringGeneratorException
				| TreeCoreferenceInformationException e)
		{
			throw new BiuteeException("Failed to create dataset",e);
		}
		
	}
	
	///////////////// PRIVATE /////////////////

	
	private static List<CandidateIdentifier> buildListOfCandidates(List<PreprocessedTopicDataSet> topics)
	{
		List<CandidateIdentifier> listOfCandidates = new ArrayList<CandidateIdentifier>();
		for (PreprocessedTopicDataSet topic : topics)
		{
			String topicId = topic.getTopicDataSet().getTopicId();
			Map<String, Set<SentenceIdentifier>> mapCandidates = topic.getTopicDataSet().getCandidatesMap();
			for (String hypothesisId : mapCandidates.keySet())
			{
				Set<SentenceIdentifier> candidateSentences = mapCandidates.get(hypothesisId);
				for (SentenceIdentifier candidateSentence : candidateSentences)
				{
					CandidateIdentifier candidateIdentifier = new CandidateIdentifier(topicId, hypothesisId, candidateSentence);
					listOfCandidates.add(candidateIdentifier);
				}
				
			}
		}
		return listOfCandidates;
	}

	
	private static RteSumDatasetContents createDatasetContents(ConfigurationParams configurationParams, String serialized_parameterName, String rawData_parameterName, TESystemEnvironment teSystemEnvironment, Envelope<List<PreprocessedTopicDataSet>> topicsEnvelope) throws BiuteeException, ConfigurationException, FileNotFoundException, IOException, Rte6mainIOException, ClassNotFoundException, TeEngineMlException, AnnotatorException, TreeStringGeneratorException, TreeCoreferenceInformationException 
	{
		File serFile = configurationParams.getFile(serialized_parameterName);
		logger.info("Reading all topic from serialization file: "+serFile.getPath());
		ObjectInputStream serStream = new ObjectInputStream(new FileInputStream(serFile));
		try
		{
			@SuppressWarnings("unchecked")
			List<PreprocessedTopicDataSet> topics = (List<PreprocessedTopicDataSet>) serStream.readObject();
			topicsEnvelope.setT(topics);
			
			Envelope<Rte6FileSystemNames> fileSystemNamesEnvelope = new Envelope<Rte6FileSystemNames>();
			File datasetDir = retrieveDatasetDirAndSetFileSystemNames(configurationParams,rawData_parameterName,fileSystemNamesEnvelope);
			Rte6FileSystemNames fileSystemNames = fileSystemNamesEnvelope.getT();
			
			Map<String, Map<String, Set<SentenceIdentifier>>> goldStandardAnswers = null;
			File goldStandardFile = new File(datasetDir,fileSystemNames.getGoldStandardFileName());
			if (goldStandardFile.exists()&&goldStandardFile.isFile())
			{
				String goldStandardFileName = goldStandardFile.getPath();
				logger.info("Retrieving gold-standard file: "+goldStandardFileName);
				AnswersFileReader gsReader = new DefaultAnswersFileReader();
				gsReader.setXml(goldStandardFileName);
				gsReader.read();
				goldStandardAnswers = gsReader.getAnswers();
			}
			else
			{
				logger.info("No gold-standard exist.");
				goldStandardAnswers = null;
			}
			
			return buildMapsOfTopicsAndSurroundingUtilities(topics,goldStandardAnswers,teSystemEnvironment);
		}
		finally
		{
			serStream.close();
		}
	}
	
	
	
	private static File retrieveDatasetDirAndSetFileSystemNames(ConfigurationParams configurationParams, String parameterName, Envelope<Rte6FileSystemNames> fileSystemNames) throws ConfigurationException, BiuteeException
	{
		//File datasetDir = configurationParams.getDirectory(RTE_SUM_DATASET_DIR_NAME);
		String datasetParameterValue = configurationParams.get(parameterName);
		String[] datasetValueComponents = datasetParameterValue.split(RTESUM_DATASET_PARAM_DELIMITER);
		Iterator<String> datasetValueIterator = Utils.arrayToCollection(datasetValueComponents, new LinkedList<String>()).iterator();
		BiuteeException badDatasetValueException = new BiuteeException("Bad value for dataset name: \""+datasetParameterValue+"\". Should be annual-flag"+RTESUM_DATASET_PARAM_DELIMITER+"dev-test-flag"+RTESUM_DATASET_PARAM_DELIMITER+"path" +
				"\nAnnual flag should be: "+FileSystemNamesFactory.RTE6_FLAG+" or "+FileSystemNamesFactory.RTE7_FLAG+
				"\ndev-test-flag should be: "+FileSystemNamesFactory.DEV_FLAG+" or "+FileSystemNamesFactory.TEST_FLAG);
		if (!datasetValueIterator.hasNext()) throw badDatasetValueException;
		String annualFlag = datasetValueIterator.next().trim();
		if (!datasetValueIterator.hasNext()) throw badDatasetValueException;
		String devTestFlag = datasetValueIterator.next().trim();
		if (!datasetValueIterator.hasNext()) throw badDatasetValueException;
		String datasetDirAsString = datasetValueIterator.next().trim();
		datasetDirAsString = FileUtils.normalizeCygwinPathToWindowsPath(datasetDirAsString);
		File datasetDir = new File(datasetDirAsString);
		datasetDir = FileUtils.normalizeFileNameByOS(datasetDir);
		
		
		boolean isNoveltyTask = false;
		if (configurationParams.containsKey(RTE_SUM_IS_NOVELTY_TASK_FLAG))
			isNoveltyTask = configurationParams.getBoolean(RTE_SUM_IS_NOVELTY_TASK_FLAG);
		logger.info("Loading " + (isNoveltyTask ? "Novelty" : "Main") + " task files");
		
		//fileSystemNames
		Rte6FileSystemNames theFileSystemNames = FileSystemNamesFactory.chooseFilteredFileSystemNames(annualFlag,devTestFlag, datasetDir, isNoveltyTask);
		fileSystemNames.setT(theFileSystemNames);
		
		logger.info("Working on folders of type: " + theFileSystemNames.getClass().getSimpleName()+
				"( "+FileSystemNamesFactory.chooseUnfilteredFileSystemNames(annualFlag,devTestFlag, datasetDir, isNoveltyTask).getClass().getSimpleName()+" )");
		
		return datasetDir;
	}
	
	private static RteSumDatasetContents buildMapsOfTopicsAndSurroundingUtilities(List<PreprocessedTopicDataSet> topics, Map<String, Map<String, Set<SentenceIdentifier>>> goldStandardAnswers, TESystemEnvironment teSystemEnvironment) throws TeEngineMlException, TreeStringGeneratorException, TreeCoreferenceInformationException, AnnotatorException
	{
		Map<String, ExtendedPreprocessedTopicDataSet> topics_mapIdToTopic = new LinkedHashMap<String, ExtendedPreprocessedTopicDataSet>();
		Map<String, RTESumSurroundingSentencesUtility> topics_mapTopicidToSurroundingUtility = new LinkedHashMap<String, RTESumSurroundingSentencesUtility>();
		
		for (PreprocessedTopicDataSet topic : topics)
		{
			String topicId = topic.getTopicDataSet().getTopicId();
			logger.info("Converting topic: \""+topicId+"\" ...");
			
			ExtendedTopicDataSetGenerator extendedGenerator = new ExtendedTopicDataSetGenerator(topic, teSystemEnvironment);
			logger.debug("Calling ExtendedTopicDataSetGenerator.generate()...");
			extendedGenerator.generate();
			logger.debug("ExtendedTopicDataSetGenerator.generate() done.");
			ExtendedPreprocessedTopicDataSet extendedTopic = extendedGenerator.getExtendedTopic();

			logger.debug("Building RTESumSurroundingSentencesUtility...");
			RTESumSurroundingSentencesUtility surroundingUtility = new RTESumSurroundingSentencesUtility(extendedTopic);
			logger.debug("Building RTESumSurroundingSentencesUtility done.");

			topics_mapIdToTopic.put(topicId, extendedTopic);
			topics_mapTopicidToSurroundingUtility.put(topicId, surroundingUtility);
		}
		
		return new RteSumDatasetContents(topics_mapIdToTopic,topics_mapTopicidToSurroundingUtility,goldStandardAnswers);
	}

	
	
	
	
	
	

	
	private static final Logger logger = Logger.getLogger(RTESumETEDatasetFactory.class);
}
