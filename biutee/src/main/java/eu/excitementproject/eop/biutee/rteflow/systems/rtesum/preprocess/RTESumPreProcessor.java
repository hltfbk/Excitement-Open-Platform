package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess;

import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_DO_NER;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.PREPROCESS_DO_TEXT_NORMALIZATION;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_DATASET_DIR_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_IS_NOVELTY_TASK_FLAG;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_PREPROCESS_MODULE_NAME;
import static eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames.RTE_SUM_PREPROCESS_SERIALIZATION_FILE_NAME;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.RTESUM_DATASET_PARAM_DELIMITER;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.preprocess.Instruments;
import eu.excitementproject.eop.biutee.rteflow.preprocess.InstrumentsFactory;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.FileSystemNamesFactory;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6DatasetLoader;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6FileSystemNames;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Executable class that makes pre-processing to the whole data-set, and stores
 * the pre-processed data into a serialization file.
 * The data-set is expected to be RTE-Summarization data-set, i.e. a data-set like the
 * RTE-6 main task.
 * 
 * @author Asher Stern
 * @since Jun 6, 2011
 *
 */
public class RTESumPreProcessor
{
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1)
			{
				System.out.println("Argument missing. Should be configuration file name");
			}
			String configurationFileName = args[0];
			new LogInitializer(configurationFileName).init();
			
			RTESumPreProcessor application = new RTESumPreProcessor(configurationFileName);
			application.preprocess();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			ExceptionUtil.logException(e, logger);
		}
	}
	
	public RTESumPreProcessor(String configurationFileName)
	{
		super();
		this.configurationFileName = configurationFileName;
	}

	public void preprocess() throws ConfigurationFileDuplicateKeyException, ConfigurationException, NumberFormatException, TeEngineMlException, ParserRunException, NamedEntityRecognizerException, TextPreprocessorException, CoreferenceResolutionException, Rte6mainIOException, TreeCoreferenceInformationException, FileNotFoundException, IOException
	{
		this.configurationFile = SystemInitialization.loadConfigurationFile(configurationFileName);
		this.configurationFile.setExpandingEnvironmentVariables(true);
		preprocessParameters = configurationFile.getModuleConfiguration(RTE_SUM_PREPROCESS_MODULE_NAME);
		this.instruments = new InstrumentsFactory().getDefaultInstruments(preprocessParameters);
		if (preprocessParameters.containsKey(PREPROCESS_DO_NER))
			doNer = preprocessParameters.getBoolean(PREPROCESS_DO_NER);
		if (preprocessParameters.containsKey(PREPROCESS_DO_TEXT_NORMALIZATION))
			doTextNormalization = preprocessParameters.getBoolean(PREPROCESS_DO_TEXT_NORMALIZATION);
		
		if (!doNer)
		{
			logger.warn("Warning: Do not make named-entity recognition!");
		}
		if (!doTextNormalization)
		{
			logger.warn("Warning: Do not make text normalization!");
		}
		
		instruments.getParser().init();
		try
		{
			instruments.getCoreferenceResolver().init();
			try
			{
				if (doNer)
					instruments.getNamedEntityRecognizer().init();
				try
				{
					logger.info("Loading and pre-processing dataset");
					loadAndProcessDataSet();
					logger.info("Pre-processing done. Saving the pre-processed dataset into a serialization file.");
					savePreprocessedTopics();
				}
				finally
				{
					if (doNer)
						instruments.getNamedEntityRecognizer().cleanUp();
				}
				
				
			}
			finally
			{
				instruments.getCoreferenceResolver().cleanUp();
			}
			
		}
		finally
		{
			instruments.getParser().cleanUp();
		}
	}
	
	protected void loadAndProcessDataSet() throws ConfigurationException, Rte6mainIOException, TeEngineMlException, ParserRunException, NamedEntityRecognizerException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException
	{
//		String datasetDirName = preprocessParameters.get(RTE_SUM_DATASET_DIR_NAME);
//		logger.info("Loading dataset from directory: "+datasetDirName);
//		boolean isNoveltyTask = false;
//		if (preprocessParameters.containsKey(RTE_SUM_IS_NOVELTY_TASK_FLAG))
//			isNoveltyTask = preprocessParameters.getBoolean(RTE_SUM_IS_NOVELTY_TASK_FLAG);
//		logger.info("Loading " + (isNoveltyTask ? "Novelty" : "Main") + " task files");
		
		File datasetDirAsFile = retrieveDatasetDirAndSetFileSystemNames(preprocessParameters);
		
		Rte6DatasetLoader loader = new Rte6DatasetLoader(datasetDirAsFile);
		loader.setFileSystemNames(this.fileSystemNames);
		// Note: you can set here other file-system-names.
		
		loader.load();
		logger.info("Loaded using file-system-names: "+loader.getFileSystemNames().getClass().getName());
		
		Map<String, TopicDataSet> allTopics = loader.getTopics();
		logger.info("Dataset was loaded successfully. Starting pre-processing."); 
		preprocessedTopics = new ArrayList<PreprocessedTopicDataSet>(allTopics.keySet().size());
		for (String topicId : allTopics.keySet())
		{
			TopicPreProcessor preProcessor = new TopicPreProcessor(allTopics.get(topicId), instruments, doNer, doTextNormalization);
			preProcessor.preprocess();
			preprocessedTopics.add(preProcessor.getPreprocessedTopicDataSet());
		}
	}
	
	protected void savePreprocessedTopics() throws ConfigurationException, FileNotFoundException, IOException
	{
		String serializationFileName = preprocessParameters.get(RTE_SUM_PREPROCESS_SERIALIZATION_FILE_NAME);
		ObjectOutputStream serStream = new ObjectOutputStream(new FileOutputStream(new File(serializationFileName)));
		try
		{
			serStream.writeObject(preprocessedTopics);
		}
		finally
		{
			serStream.close();
		}
	}
	
	
	// TODO code duplication with RTESumBaseEngine
	private File retrieveDatasetDirAndSetFileSystemNames(ConfigurationParams configurationParams) throws ConfigurationException, TeEngineMlException
	{
		//File datasetDir = configurationParams.getDirectory(RTE_SUM_DATASET_DIR_NAME);
		String datasetParameterValue = configurationParams.get(RTE_SUM_DATASET_DIR_NAME);
		String[] datasetValueComponents = datasetParameterValue.split(RTESUM_DATASET_PARAM_DELIMITER);
		Iterator<String> datasetValueIterator = Utils.arrayToCollection(datasetValueComponents, new LinkedList<String>()).iterator();
		TeEngineMlException badDatasetValueException = new TeEngineMlException("Bad value for dataset name: \""+datasetParameterValue+"\". Should be annual-flag"+RTESUM_DATASET_PARAM_DELIMITER+"dev-test-flag"+RTESUM_DATASET_PARAM_DELIMITER+"path" +
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
		
		fileSystemNames = FileSystemNamesFactory.chooseFilteredFileSystemNames(annualFlag,devTestFlag, datasetDir, isNoveltyTask);
		logger.info("Working on folders of type: " + fileSystemNames.getClass().getSimpleName()+
				"( "+FileSystemNamesFactory.chooseUnfilteredFileSystemNames(annualFlag,devTestFlag, datasetDir, isNoveltyTask).getClass().getSimpleName()+" )");
		
		return datasetDir;
	}
	

	private String configurationFileName;
	
	private ConfigurationFile configurationFile;
	private ConfigurationParams preprocessParameters;
	private Rte6FileSystemNames fileSystemNames;
	private Instruments<Info, BasicNode> instruments;
	private boolean doNer = true;
	private boolean doTextNormalization = true;
	
	private List<PreprocessedTopicDataSet> preprocessedTopics;
	
	private static final Logger logger = Logger.getLogger(RTESumPreProcessor.class);
}
