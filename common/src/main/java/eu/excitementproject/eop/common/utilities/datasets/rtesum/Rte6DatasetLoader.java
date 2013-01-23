package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;



/**
 * Loads the whole data set into memory.
 * Note that it is not always recommended to use such a loader, since it takes a large
 * memory space to store the whole data set.
 * <P>
 * To load the whole data set use the constructor with the path to the data set directory.
 * This will load the "default", which is the dev-set, main task, then call {@link #load()}.
 * <B>If you want to load novelty task or test set, or RTEX where X!=6, you have to
 * call {@link #setFileSystemNames(Rte6FileSystemNames)}</B> with the appropriate class that
 * represents the names for the requested data-set.
 * For example:
 * <code>
 * setFileSystemNames(new TestsetRte6NoveltyFileSystemNames());
 * </code>
 * (do it before you call {@link #load()}).
 * 
 * 
 * @author Asher Stern
 * @since Aug 11, 2010
 *
 */
public class Rte6DatasetLoader
{
	/**
	 * Ctor Best use the other Ctors that take a {@link File}. This is kept for backwards compatibility.
	 * @param datasetDirName
	 * @throws Rte6mainIOException
	 */
	public Rte6DatasetLoader(String datasetDirName) throws Rte6mainIOException
	{
		this(new File(datasetDirName));
	}	
	
	/**
	 * Ctor for loading the main task files. Dataset is <i>filtered </i> and <i>main task</i> by default.
	 * @param datasetDir
	 * @throws Rte6mainIOException
	 */
	public Rte6DatasetLoader(File datasetDir) throws Rte6mainIOException
	{
		super();
		if (null==datasetDir) throw new Rte6mainIOException("null dataset dir");
		this.datasetDir = datasetDir;

//		this(datasetDir, false, true);	// default flag is RTE Main Task
	}
	
//	/**
//	 * Ctor with Novelty Task flag, and <i>filtered</i> flag
//	 * @param datasetDir
//	 * @param isFiltered
//	 * @param isNoveltyTask is true, the novelty task files will be loaded. If false, the main task will be loaded
//	 * @throws Rte6mainIOException
//	 */
//	public Rte6DatasetLoader(File datasetDir, boolean isNoveltyTask, boolean isFiltered) throws Rte6mainIOException
//	{
//		super();
//		if (null==datasetDir) throw new Rte6mainIOException("null dataset dir");
//		this.DATASET_DIR = datasetDir;
//		this.IS_NOVELTY_TASK = isNoveltyTask;
//		this.IS_FILTERED = isFiltered;
//	}
	
	/**
	 * Specifies whether to load the gold-standard, which will be retrieved by
	 * the {@link #getAnswers()} method.
	 * <P> 
	 * If this method <B> is not called </B>, then when calling {@link #load()}, it loads the
	 * gold standard file if and only if that file exist.
	 * If this method <B> is called </B> with a non-null value, then it loads the gold standard
	 * file only if the given value is <tt>true</tt>, and {@link #load()} will throw
	 * an exception if the given value was true and the gold-standard file does not exist.
	 * 
	 * @param loadGoldStandard <tt>true</tt> / <tt>false</tt>. Default: <code>null</code>
	 */
	public void setLoadGoldStandard(Boolean loadGoldStandard)
	{
		this.loadGoldStandard = loadGoldStandard;
	}
	
	/**
	 * Set the {@link Rte6FileSystemNames} to be used. 
	 * @param fileSystemNames
	 */
	public void setFileSystemNames(Rte6FileSystemNames fileSystemNames)
	{
		this.fileSystemNames = fileSystemNames;
	}


	/**
	 * Loads the whole data set. Default is RTE Main task
	 * @throws Rte6mainIOException
	 */
	public void load() throws Rte6mainIOException
	{
		// choosing the right fileSystemNames according to the dataset dir name
		if (null==datasetDir) throw new Rte6mainIOException("datasetDir is null");
		if (fileSystemNames == null)
			throw new Rte6mainIOException("You must set the file-system-names. Please call the method: setFileSystemNames");
//			fileSystemNames = IS_FILTERED ? FileSystemNamesFactory.chooseFilteredFileSystemNames(DATASET_DIR, IS_NOVELTY_TASK)
//										  :	FileSystemNamesFactory.chooseUnfilteredFileSystemNames(DATASET_DIR, IS_NOVELTY_TASK);
//			
		if (!datasetDir.isDirectory()) throw new Rte6mainIOException("not dir: "+datasetDir.getPath());
		readGoldStandard(datasetDir);
		
		File[] topicsDirs = datasetDir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				if (pathname.isDirectory()) if (pathname.getName().startsWith(fileSystemNames.getTopicDirectoryNamePrefix()))
					return true;
				
				return false;
			}
		});
		
		topics = new LinkedHashMap<String, TopicDataSet>();
		
		for (File topicDir : topicsDirs)
		{
			String candidatesFileName = new File(new File(topicDir,fileSystemNames.getTaskDirectoryName()),fileSystemNames.getEvaluationPairsFileName()).getPath();
			String hypothesesFileName = new File(new File(topicDir,fileSystemNames.getTaskDirectoryName()),fileSystemNames.getHypothesisFileName()).getPath();
			
			EvaluationPairsReader evaluationPairsReader = new DefaultEvaluationPairsReader();
			evaluationPairsReader.setXml(candidatesFileName);
			evaluationPairsReader.read();
			String topicId = evaluationPairsReader.getTopicId();
			Map<String, Set<SentenceIdentifier>> candidatesMap = evaluationPairsReader.getCandidateSentencesMap();
			
			HypothesisFileReader hypothesesReader = new DefaultHypothesisFileReader();
			hypothesesReader.setXml(hypothesesFileName);
			hypothesesReader.read();
			Map<String,String> hypothesisMap = hypothesesReader.getHypothesisTextMap();
			
			File corpusDir = new File(topicDir,fileSystemNames.getCorpusDirectoryName());
			File[] documentsFiles = corpusDir.listFiles(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					if (pathname.isFile()) if (pathname.getName().endsWith(fileSystemNames.getCorpusFileNamePostfix()))
						return true;
					
					return false;
				}
			});
			
			Map<String,DocumentMetaData> metaDataMap = new LinkedHashMap<String, DocumentMetaData>();
			Map<String,Map<Integer, String>> documentsMap = new LinkedHashMap<String, Map<Integer,String>>();
			for (File documentFile : documentsFiles)
			{
				CorpusDocumentReader documentReader = new DefaultCorpusDocumentReader();
				documentReader.setXml(documentFile.getPath());
				documentReader.read();
				Map<Integer, String> document = documentReader.getMapSentences();
				String documentId = documentReader.getDocId();
				
				DocumentMetaData documentMetaData =
					new DocumentMetaData(documentId, documentReader.getType(), documentReader.getHeadline(), documentReader.getDateline());
				
				documentsMap.put(documentId,document);
				metaDataMap.put(documentId, documentMetaData);
				
			}
			
			topics.put(topicId, new TopicDataSet(topicId, candidatesMap, hypothesisMap, documentsMap, metaDataMap));
		}
	}
	
	/**
	 * Returns the gold standard, a map from topic-id to a map from hypothesis-id to text sentences.
	 * @return
	 */
	public Map<String, Map<String, Set<SentenceIdentifier>>> getAnswers()
	{
		return answers;
	}

	public Map<String, TopicDataSet> getTopics()
	{
		return topics;
	}
	
	/**
	 * Returns the {@link Rte6FileSystemNames} that was used to load the data-set.
	 * Call this method only <b>after</b> calling {@link #load()}.
	 * @return
	 */
	public Rte6FileSystemNames getFileSystemNames()
	{
		return this.fileSystemNames;
	}
	
	
	///////////////////////// PRIVATE ///////////////////////////////
	
	
	private void readGoldStandard(File datasetDir) throws Rte6mainIOException
	{
		boolean loadGs = false;
		if (this.loadGoldStandard!=null)
		{
			if (this.loadGoldStandard.booleanValue())
			{
				loadGs = true;
			}
			else
			{
				loadGs = false;
			}
		}
		else
		{
			File goldstandardFile = new File(datasetDir,fileSystemNames.getGoldStandardFileName());
			if (goldstandardFile.exists())
				loadGs = true;
			else
				loadGs = false;
		}
		
		if (loadGs)
		{
			AnswersFileReader answersReader = new DefaultAnswersFileReader();
			answersReader.setXml(new File(datasetDir,fileSystemNames.getGoldStandardFileName()).getPath());
			answersReader.read();
			answersReader.setGoldStandard(false);
			answers = answersReader.getAnswers();
		}
		else
		{
			answers = null;
		}
		
	}





	private final File datasetDir;
//	private final boolean IS_NOVELTY_TASK;
//	private final boolean IS_FILTERED;
	
	private Map<String,Map<String,Set<SentenceIdentifier>>> answers;
	private Map<String,TopicDataSet> topics;
	
	private Boolean loadGoldStandard = null;
	private Rte6FileSystemNames fileSystemNames = null;
	
}
