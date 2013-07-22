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

import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumDataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtesum.RteSumDatasetContents;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedPreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedTopicDataSetGenerator;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.PreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
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
	public RTESumETEDatasetFactory(ConfigurationParams configurationParams,
			String serialized_parameterName, String rawData_parameterName,
			TESystemEnvironment teSystemEnvironment)
	{
		super();
		this.configurationParams = configurationParams;
		this.serialized_parameterName = serialized_parameterName;
		this.rawData_parameterName = rawData_parameterName;
		this.teSystemEnvironment = teSystemEnvironment;
	}

	public void createDataset() throws BiuteeException
	{
		try
		{
			RteSumDatasetContents datasetContents = createDatasetContents();
			List<CandidateIdentifier> candidates = buildListOfCandidates();
			this.dataset = new RteSumDataset(datasetContents, candidates);
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

	
	public RteSumDataset getDataset() throws BiuteeException
	{
		if (null==dataset) throw new BiuteeException("Caller\'s bug. Dataset not yet built.");
		return dataset;
	}
	
	
	
	///////////////// PRIVATE /////////////////

	

	private List<CandidateIdentifier> buildListOfCandidates()
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

	
	@SuppressWarnings("unchecked")
	private RteSumDatasetContents createDatasetContents() throws BiuteeException, ConfigurationException, FileNotFoundException, IOException, Rte6mainIOException, ClassNotFoundException, TeEngineMlException, AnnotatorException, TreeStringGeneratorException, TreeCoreferenceInformationException 
	{
		File serFile = configurationParams.getFile(serialized_parameterName);
		logger.info("Reading all topic from serialization file: "+serFile.getPath());
		ObjectInputStream serStream = new ObjectInputStream(new FileInputStream(serFile));
		try
		{
			this.topics = (List<PreprocessedTopicDataSet>) serStream.readObject();
			
			File datasetDir = retrieveDatasetDirAndSetFileSystemNames(configurationParams,rawData_parameterName);
			
			goldStandardAnswers = null;
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
			
			buildMapsOfTopicsAndSurroundingUtilities();
			return new RteSumDatasetContents(topics_mapIdToTopic,topics_mapTopicidToSurroundingUtility,goldStandardAnswers);
		}
		finally
		{
			serStream.close();
		}
	}
	
	
	
	private File retrieveDatasetDirAndSetFileSystemNames(ConfigurationParams configurationParams, String parameterName) throws ConfigurationException, BiuteeException
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
		this.fileSystemNames = theFileSystemNames;
		
		logger.info("Working on folders of type: " + theFileSystemNames.getClass().getSimpleName()+
				"( "+FileSystemNamesFactory.chooseUnfilteredFileSystemNames(annualFlag,devTestFlag, datasetDir, isNoveltyTask).getClass().getSimpleName()+" )");
		
		return datasetDir;
	}
	
	private void buildMapsOfTopicsAndSurroundingUtilities() throws TeEngineMlException, TreeStringGeneratorException, TreeCoreferenceInformationException, AnnotatorException
	{
		topics_mapIdToTopic = new LinkedHashMap<String, ExtendedPreprocessedTopicDataSet>();
		topics_mapTopicidToSurroundingUtility = new LinkedHashMap<String, RTESumSurroundingSentencesUtility>();
		
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
		
		
	}

	
	
	
	
	// input
	private final ConfigurationParams configurationParams;
	private final String serialized_parameterName;
	private final String rawData_parameterName;
	private final TESystemEnvironment teSystemEnvironment;

	// internals
	private List<PreprocessedTopicDataSet> topics = null;
	private Rte6FileSystemNames fileSystemNames = null;
	private Map<String, Map<String, Set<SentenceIdentifier>>> goldStandardAnswers = null;
	private Map<String, ExtendedPreprocessedTopicDataSet> topics_mapIdToTopic = null;
	private Map<String, RTESumSurroundingSentencesUtility> topics_mapTopicidToSurroundingUtility = null;
	
	// output
	private RteSumDataset dataset = null;
	
	private static final Logger logger = Logger.getLogger(RTESumETEDatasetFactory.class);
}
