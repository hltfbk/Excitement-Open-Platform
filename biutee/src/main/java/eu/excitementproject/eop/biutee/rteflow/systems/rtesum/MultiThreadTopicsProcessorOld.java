package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.PreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.lap.biu.en.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.StopFlag;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Used only if {@link Constants#USE_OLD_CONCURRENCY_IN_RTE_SUM} is <tt>true</tt>
 * 
 * @author Asher Stern
 * @since Jun 6, 2011
 *
 */
public class MultiThreadTopicsProcessorOld implements AllTopicsProcessor
{
	public MultiThreadTopicsProcessorOld(
			List<PreprocessedTopicDataSet> topics,
			Map<String, Map<String, Set<SentenceIdentifier>>> goldStandardAnswers,
			int numberOfThreads,
			ConfigurationFile configurationFile,
			LinearClassifier classifierForSearch,
			Lemmatizer lemmatizer,
			TESystemEnvironment teSystemEnvironment
			)
	{
		super();
		this.topics = topics;
		this.goldStandardAnswers = goldStandardAnswers;
		this.numberOfThreads = numberOfThreads;
		this.configurationFile = configurationFile;
		this.classifierForSearch = classifierForSearch;
		this.lemmatizer = lemmatizer;
		this.teSystemEnvironment = teSystemEnvironment;
	}

	@Override
	public void process() throws InterruptedException, TeEngineMlException, FileNotFoundException, IOException, Rte6mainIOException
	{
		processAllTopics();
		processResults();
	}
	
	@Override
	public Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> getAllTopicsResults() throws TeEngineMlException
	{
		if (null==allTopicsResults) throw new TeEngineMlException("null==allTopicsResults");
		return allTopicsResults;
	}

	@Override
	public Vector<LabeledSample> getResultsSamples() throws TeEngineMlException
	{
		if (null==resultsSamples) throw new TeEngineMlException("null==resultsSamples");
		return resultsSamples;
	}




	protected void processAllTopics() throws InterruptedException, TeEngineMlException
	{
		logger.info("Processing list of topics. Number of topics = "+topics.size());
		
		// Split the topics to threads.
		List<List<PreprocessedTopicDataSet>> topicsPerThread = Utils.fairPartition(topics, numberOfThreads);
		int threadIndexForLog = 0;
		for (List<PreprocessedTopicDataSet> listTopicsSingleThread : topicsPerThread)
		{
			logger.info("Thread #"+threadIndexForLog+" has "+listTopicsSingleThread.size()+" topics.");
		}
		
		// Construct a result object that will hold the results of all topics.
		allTopicsResults = new LinkedHashMap<String, Map<String,Map<SentenceIdentifier,RteSumSingleCandidateResult>>>();
		
		// Create the threads with their topics, and start them.
		Thread[] threads = new Thread[topicsPerThread.size()];
		SingleTopicRunnable[] runnables = new SingleTopicRunnable[topicsPerThread.size()];
		int threadIndex=0;
		for (List<PreprocessedTopicDataSet> topicsSingleThread : topicsPerThread)
		{
			SingleTopicRunnable runnable = new SingleTopicRunnable(topicsSingleThread);
			runnables[threadIndex] = runnable;
			Thread thread = new Thread(runnable);
			threads[threadIndex] = thread;
			thread.start();
			++threadIndex;
		}
		
		// Join the threads. The main thread will continue only after all threads have finished. 
		for (threadIndex=0;threadIndex<threads.length;++threadIndex)
		{
			Thread thread = threads[threadIndex];
			thread.join();
		}
		
		// Collect the results from all threads.
		TeEngineMlException exceptionFromThread = null;
		for (SingleTopicRunnable runnable : runnables)
		{
			TeEngineMlException exceptionFromCurrentThread = runnable.getException();
			if (exceptionFromCurrentThread!=null)
			{
				logger.error("An exception occurred in a thread:\n",exceptionFromCurrentThread);  
				if (null==exceptionFromThread)
				{
					exceptionFromThread = runnable.getException();
				}
			}
			if (!stopFlag.isStop())
			{
				for (String topicID : runnable.getTopicsResults().keySet())
				{
					if (allTopicsResults.containsKey(topicID))throw new TeEngineMlException("duplicate result: "+topicID);
					Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>> singleTopicResult = runnable.getTopicsResults().get(topicID);
					if (null==singleTopicResult) throw new TeEngineMlException("Null result for topic: "+topicID);
					allTopicsResults.put(topicID, singleTopicResult);
				}
			}
		}
		
		// Make sure there was not bug with the stopFlag and the exception (exception from thread).
		if (stopFlag.isStop()&&(exceptionFromThread==null)) // a bug
			throw new TeEngineMlException("Stop was set but no exception has been thrown. Bug.");
		if ((exceptionFromThread!=null)&&(!stopFlag.isStop())) // a bug
			throw new TeEngineMlException("Exception was thrown but stop flag was not set. See the exception in the nested exception.",exceptionFromThread);
		if (exceptionFromThread!=null) // Correct behavior when an exception was thrown.
			throw exceptionFromThread;
	}
	
	protected void processResults() throws FileNotFoundException, IOException, Rte6mainIOException, TeEngineMlException
	{
		logger.info("Processing results...");
		logger.info("Writing all topics results to log file");
		
		if (goldStandardAnswers!=null)
		{
			logger.info("Building vector of LabeledSamples");
			buildResultsSamples();
		}
		else
		{
			logger.info("Vector of LabeledSamples cannot be built since no gold-standard answers were given.");
		}
		
//		logger.info("Creating an answer file: "+answerFileName);
//		Map<String,Map<String,Set<SentenceIdentifier>>> answer = createAnswer();
//		File answerFile = new File(answerFileName);
//		AnswersFileWriter answerFileWriter = new DefaultAnswersFileWriter();
//		answerFileWriter.setAnswers(answer);
//		answerFileWriter.setXml(answerFile.getPath());
//		answerFileWriter.write();
//		
//		if (goldStandardFileName!=null)
//		{
//			logger.info("Answer file was created. Proceeding to compute current success rate.");
//			AnswerScoreComputer answerScoreComputer = new AnswerScoreComputer(goldStandardFileName, answerFile.getPath());
//			answerScoreComputer.compute();
//			logger.info("Answers scores:\n"+answerScoreComputer.getResultsAsString());
//		}
//		else
//		{
//			logger.info("Answer file was created. Scores cannot be computed since no gold-standard file was given.");
//		}
	}
	

	
	private void buildResultsSamples() throws Rte6mainIOException, TeEngineMlException
	{
		resultsSamples = new Vector<LabeledSample>();
		for (String topicID : allTopicsResults.keySet())
		{
			if (!goldStandardAnswers.containsKey(topicID)) throw new TeEngineMlException("Seems that a wrong gold-standard file was given. The gold-standard file does not contain the given topic: "+topicID);
			Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>> resultsTopic = allTopicsResults.get(topicID);
			for (String hypothesisID : resultsTopic.keySet())
			{
				if (!goldStandardAnswers.get(topicID).containsKey(hypothesisID))throw new TeEngineMlException("Seems that a wrong gold-standard file was given. The gold-standard file does not contain (for topic "+topicID+") the given hypothesis: "+hypothesisID);
				Map<SentenceIdentifier, RteSumSingleCandidateResult> hypothesisResult = resultsTopic.get(hypothesisID);
				for (SentenceIdentifier sentenceID : hypothesisResult.keySet())
				{
					RteSumSingleCandidateResult singleResult = hypothesisResult.get(sentenceID);
					boolean trueInGoldStandard = false;
					if (goldStandardAnswers.get(topicID).get(hypothesisID).contains(sentenceID))
					{
						trueInGoldStandard = true;
					}
					else
					{
						trueInGoldStandard = false;
					}
					resultsSamples.add(new LabeledSample(singleResult.getFeatureVector(),trueInGoldStandard));
				}
			}
		}
	}
	

	
	private class SingleTopicRunnable implements Runnable
	{
		public SingleTopicRunnable(List<PreprocessedTopicDataSet> topics)
		{
			super();
			this.topics = topics;
		}

		public void run()
		{
			try
			{
				OperationsScript<Info, BasicNode> script = new ScriptFactory(configurationFile,teSystemEnvironment.getPluginRegistry()).getDefaultScript();
				script.init();
				try
				{
					topicsResults = new LinkedHashMap<String, Map<String,Map<SentenceIdentifier,RteSumSingleCandidateResult>>>();
					for (PreprocessedTopicDataSet topic : topics)
					{
						TopicProcessor processor = new TopicProcessor(topic, classifierForSearch, lemmatizer, script, teSystemEnvironment, stopFlag);
						processor.process();
						if (!stopFlag.isStop())
						{
							this.topicsResults.put(topic.getTopicDataSet().getTopicId(),processor.getResult());
						}
					}
				}
				finally
				{
					script.cleanUp();
				}
				logger.info("A thread ended (with no exception or error).");
				if (stopFlag.isStop())
				{
					logger.info("The thread aborted due to an error that occurred in another thread."); 
				}
			}
			catch(Throwable e)
			{
				stopFlag.stop();
				logger.error("A throwable (exception or error) was thrown by a thread.",e);
				this.exception = new TeEngineMlException("SingleTopicRunnable failed. See nested exception",e);
			}
		}
		
		public Map<String, Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> getTopicsResults()
		{
			return topicsResults;
		}

		public TeEngineMlException getException()
		{
			return exception;
		}

		private List<PreprocessedTopicDataSet> topics;
		
		private Map<String,Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> topicsResults;
		private TeEngineMlException exception = null;
	}
	
	
	
	private List<PreprocessedTopicDataSet> topics;
	private Map<String,Map<String,Set<SentenceIdentifier>>> goldStandardAnswers;
	private int numberOfThreads;
	

	private ConfigurationFile configurationFile;
	private LinearClassifier classifierForSearch;
	private Lemmatizer lemmatizer;
	private TESystemEnvironment teSystemEnvironment;

	private StopFlag stopFlag = new StopFlag();
	/**
	 * All Results.
	 * It is a map from topic-id to the result of the topic. The result of the topic
	 * is a map from hypothesis-id to its result, which is a map from candidate to its result.
	 */
	private Map<String , Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults = null;
	private Vector<LabeledSample> resultsSamples = null;


	private static final Logger logger = Logger.getLogger(MultiThreadTopicsProcessorOld.class);
}
