package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedPreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedTopicDataSetGenerator;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.PreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.Rte6mainIOException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.StopFlag;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.TimeElapsedTracker;

/**
 * 
 * @author Asher Stern
 * @since Jun 6, 2011
 *
 */
public class MultiThreadTopicsProcessor implements AllTopicsProcessor
{
	public MultiThreadTopicsProcessor(
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
	public void process() throws InterruptedException, TeEngineMlException, FileNotFoundException, IOException, Rte6mainIOException, OperationException, TreeStringGeneratorException, TreeCoreferenceInformationException, AnnotatorException
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
	
	
	
	
	
	
	
	
	private void initScripts() throws OperationException, InterruptedException, GapException
	{
		logger.info("Initializing scripts...");
		queueScripts = new ArrayBlockingQueue<ScriptAndHypothesisInformation>(numberOfThreads);
		for (int index=0;index<numberOfThreads;++index)
		{
			OperationsScript<Info, BasicNode> script = new ScriptFactory(configurationFile,teSystemEnvironment.getPluginRegistry(),teSystemEnvironment).getDefaultScript();
			script.init();
			queueScripts.put(new ScriptAndHypothesisInformation(script, null));
		}
		logger.info("Initializing scripts done.");
	}
	
	
	private void cleanUp()
	{
		if (queueScripts!=null)
		{
			for (ScriptAndHypothesisInformation script : queueScripts)
			{
				script.getScript().cleanUp();
			}
		}
	}
	
	
	private void buildListOfCandidates()
	{
		listOfCandidates = new ArrayList<CandidateIdentifier>();
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
	
	// private Map<String , Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults = null;
	private void constructAllTopicsResults()
	{
		allTopicsResults = new LinkedHashMap<String, Map<String,Map<SentenceIdentifier,RteSumSingleCandidateResult>>>();
		for (PreprocessedTopicDataSet topic : topics)
		{
			Map<String,Map<SentenceIdentifier,RteSumSingleCandidateResult>> hypothesisResultsMap = new LinkedHashMap<String, Map<SentenceIdentifier,RteSumSingleCandidateResult>>();
			allTopicsResults.put(topic.getTopicDataSet().getTopicId(), hypothesisResultsMap);
			for (String hypothesisId : topic.getTopicDataSet().getHypothesisMap().keySet())
			{
				hypothesisResultsMap.put(hypothesisId, new LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>());
			}
		}
	}
	
	
	
	private void processAllTopics() throws TeEngineMlException, TreeStringGeneratorException, TreeCoreferenceInformationException, OperationException, InterruptedException, AnnotatorException
	{
		try
		{
			logger.info("Processing all topics. Initializing...");
			buildListOfCandidates();
			buildMapsOfTopicsAndSurroundingUtilities();
			initScripts();
			constructAllTopicsResults();
			logger.info("Initialization done.");
			
			ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
			try
			{
				List<CandidateCallable> allCandidateCallables = new ArrayList<CandidateCallable>(listOfCandidates.size());
				for (CandidateIdentifier candidateIdentifier : listOfCandidates)
				{
					allCandidateCallables.add(new CandidateCallable(candidateIdentifier));
				}
				logger.info("Executing the processing of all candidates.");
				List<Future<RteSumSingleCandidateResult>> allResultsFutures = executor.invokeAll(allCandidateCallables);
				logger.info("Processing of all topics done.");
				logger.info("Checking for exceptions...");
				for (Future<RteSumSingleCandidateResult> future : allResultsFutures)
				{
					future.get();
				}
				if (stopFlag.isStop())
				{
					throw new TeEngineMlException("An unknown error occured during processing.");
				}
				logger.info("No exception has been thrown.");
				logger.info("All topics have been successfully processed.");
			}
			catch (ExecutionException e)
			{
				throw new TeEngineMlException("Processing failed. See nested exception",e);
			}
			finally
			{
				executor.shutdown();
			}
		}
		finally
		{
			cleanUp();
		}
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
	

	

	
	
	
	///////////////// PRIVATE NESTED CLASS CandidateCallable ///////////////// 
	
	private class CandidateCallable implements Callable<RteSumSingleCandidateResult>
	{
		public CandidateCallable(CandidateIdentifier candidateIdentifier)
		{
			super();
			this.candidateIdentifier = candidateIdentifier;
		}

		@Override
		public RteSumSingleCandidateResult call() throws Exception
		{
			RteSumSingleCandidateResult ret = null;
			if (!stopFlag.isStop())
			{
				boolean allOk = false;
				try
				{
					ret = processCandidate();
					allOk = true;
				}
				finally
				{
					if (!allOk)
					{
						try{stopFlag.stop();}catch(Throwable t){}
					}
				}
			}
			return ret;
		}
		
		private RteSumSingleCandidateResult processCandidate() throws InterruptedException, TeEngineMlException, OperationException, ClassifierException, AnnotatorException, ScriptException, RuleBaseException, TreeAndParentMapException
		{
			logger.info("Processing candidate: "+candidateIdentifier.getTopicId()+"/"+candidateIdentifier.getHypothesisID()+"/"+candidateIdentifier.getSentenceID().toString());
			RteSumSingleCandidateResult ret = null;
			ScriptAndHypothesisInformation scriptAndHypothesisInformation = queueScripts.take();
			OperationsScript<Info, BasicNode> script = scriptAndHypothesisInformation.getScript();
			try
			{
				extendedTopic = topics_mapIdToTopic.get(candidateIdentifier.getTopicId());

				hypothesisSentence =  extendedTopic.getTopicDataSet().getHypothesisMap().get(candidateIdentifier.getHypothesisID());
				logger.info("Hypothesis: "+candidateIdentifier.getHypothesisID()+" - "+hypothesisSentence);
				
				hypothesisTree = extendedTopic.getHypothesisTreesMap().get(candidateIdentifier.getHypothesisID());

				// for off-line chaining - i.e. a mechanism that makes the chaining outside
				// of the engine (Eyal's code).
				HypothesisInformation hypothesisInformationForThisHypothesis = new HypothesisInformation(hypothesisSentence, hypothesisTree);
				if (!hypothesisInformationForThisHypothesis.equals(scriptAndHypothesisInformation.getHypothesisInformation()))
				{
					logger.info("Setting hypothesis information in script.");
					script.setHypothesisInformation(hypothesisInformationForThisHypothesis);
					scriptAndHypothesisInformation = new ScriptAndHypothesisInformation(script, hypothesisInformationForThisHypothesis);
				}

				ret = processCandidateWithScript(script);
				logger.info("Candidate has been successfully processed.");
			}
			finally
			{
				queueScripts.put(scriptAndHypothesisInformation);
			}
			return ret;
		}
		
		private RteSumSingleCandidateResult processCandidateWithScript(OperationsScript<Info, BasicNode> script) throws TeEngineMlException, OperationException, ClassifierException, AnnotatorException, ScriptException, RuleBaseException, TreeAndParentMapException
		{
			RTESumSurroundingSentencesUtility surroundingUtility = topics_mapTopicidToSurroundingUtility.get(candidateIdentifier.getTopicId());
			Map<String, Map<Integer, ExtendedNode>> topicTreesMap = extendedTopic.getDocumentsTreesMap();
			TopicDataSet topicDS = extendedTopic.getTopicDataSet();
			
			// TODO: code duplicate with TopicProcessor
			
			// Take the parse tree of the candidate
			Map<Integer, ExtendedNode> documentTrees = topicTreesMap.get(candidateIdentifier.getSentenceID().getDocumentId());
			int sentenceIndex = Integer.valueOf(candidateIdentifier.getSentenceID().getSentenceId());
			ExtendedNode textTree = documentTrees.get(sentenceIndex); // this is the parse tree.

			// Wrap it in a list with only a single item - this tree.
			List<ExtendedNode> textTreeAsList = new ArrayList<ExtendedNode>(1);
			textTreeAsList.add(textTree);

			// Create a list of other sentence that will be considered as "exist in pair"
			List<ExtendedNode> surroundingTextTrees = null;
			synchronized(surroundingUtility)
			{
				surroundingTextTrees = surroundingUtility.getSurroundingSentences(candidateIdentifier.getSentenceID(), textTree);
			}

			// Create map from tree to sentence. It will contain only that single tree. 
			Map<ExtendedNode, String> mapTreesToSentences = new LinkedHashMap<ExtendedNode, String>();
			String textSentence = topicDS.getDocumentsMap().get(candidateIdentifier.getSentenceID().getDocumentId()).get(sentenceIndex);
			if (null==textSentence) throw new TeEngineMlException("BUG");
			mapTreesToSentences.put(textTree,textSentence);

			// And find the best proof.
			logger.info("Working on entailment by \""+textSentence+"\" of hypothesis: \""+hypothesisSentence+"\"");
			
			String textText = topicDS.getDocumentsMap().get(candidateIdentifier.getSentenceID().getDocumentId()).get(Integer.parseInt(candidateIdentifier.getSentenceID().getSentenceId()));
			String hypothesisText = topicDS.getHypothesisMap().get(candidateIdentifier.getHypothesisID());
			LocalCreativeTextTreesProcessor lcTextTreesProcessor = new LocalCreativeTextTreesProcessor(textText, hypothesisText, textTreeAsList, hypothesisTree, mapTreesToSentences, extendedTopic.getCoreferenceInformation().get(candidateIdentifier.getSentenceID().getDocumentId()), classifierForSearch, lemmatizer, script, teSystemEnvironment);
			lcTextTreesProcessor.setSurroundingsContext(surroundingTextTrees);
			WithStatisticsTextTreesProcessor processor = lcTextTreesProcessor;
			// TextTreesProcessor processor = new BeamSearchTextTreesProcessor(textTreeAsList, hypothesisTree, mapTreesToSentences, extendedTopic.getCoreferenceInformation().get(sentenceID.getDocumentId()), this.classifierForSearch, lemmatizer, script, unigramProbabilityEstimation,ruleBasesToRetrieveMultiWords);
			TimeElapsedTracker tracker = new TimeElapsedTracker();
			tracker.start();
			processor.process();
			tracker.end();
			logger.info("Proof construction CPU time(MS) ...... : "+tracker.getCpuTimeElapsed()/1000000);
			logger.info("Proof construction World-Clock time(MS): "+tracker.getWorldClockElapsed());
			logger.info("Number of generated elements: "+processor.getNumberOfGeneratedElements());
			

			// Now take the best proof (the tree, the feature vector and the history which
			// is the proof itself) - and build a result object "RteSumSingleCandidateResult"
			Map<Integer,Double> featureVector = processor.getBestTree().getFeatureVector();
			
			RteSumSingleCandidateResult resultCurrentCandidate = null;
			if (BiuteeConstants.PRINT_TIME_STATISTICS)
			{
				resultCurrentCandidate =
					new RteSumSingleCandidateResult(candidateIdentifier.getSentenceID(), candidateIdentifier.getHypothesisID(), textSentence,hypothesisSentence,processor.getBestTree().getTree(),featureVector,processor.getBestTreeHistory(),
							tracker.getCpuTimeElapsed(),processor.getNumberOfExpandedElements(),processor.getNumberOfGeneratedElements());
			}
			else
			{
				resultCurrentCandidate =
					new RteSumSingleCandidateResult(candidateIdentifier.getSentenceID(),candidateIdentifier.getHypothesisID(),textSentence,hypothesisSentence,processor.getBestTree().getTree(),featureVector,processor.getBestTreeHistory());
			}
			
			synchronized (allTopicsResults) // synchronization is not necessary, but let's be on the safe side.
			{
				allTopicsResults.get(candidateIdentifier.getTopicId()).get(candidateIdentifier.getHypothesisID()).put(candidateIdentifier.getSentenceID(), resultCurrentCandidate);	
			}
			
			return resultCurrentCandidate;
		}

		private final CandidateIdentifier candidateIdentifier;
		private ExtendedPreprocessedTopicDataSet extendedTopic;
		private String hypothesisSentence;
		private ExtendedNode hypothesisTree;
	}
	
	///////////////// END OF NESTED CLASS CandidateCallable ////////////////// 

	
	

	
	
	
	// INPUT
	private List<PreprocessedTopicDataSet> topics;
	private Map<String,Map<String,Set<SentenceIdentifier>>> goldStandardAnswers;
	private int numberOfThreads;
	private ConfigurationFile configurationFile;
	private LinearClassifier classifierForSearch;
	private Lemmatizer lemmatizer;
	private TESystemEnvironment teSystemEnvironment;

	// INTERNAL OBJECTS
	private List<CandidateIdentifier> listOfCandidates;
	private Map<String, ExtendedPreprocessedTopicDataSet> topics_mapIdToTopic;
	private Map<String, RTESumSurroundingSentencesUtility> topics_mapTopicidToSurroundingUtility;
	private BlockingQueue<ScriptAndHypothesisInformation> queueScripts;
	private StopFlag stopFlag = new StopFlag();
	
	
	// OUTPUT
	/**
	 * All Results.
	 * It is a map from topic-id to the result of the topic. The result of the topic
	 * is a map from hypothesis-id to its result, which is a map from candidate to its result.
	 */
	private Map<String , Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>>> allTopicsResults = null;
	private Vector<LabeledSample> resultsSamples = null;


	// LOGGER
	private static final Logger logger = Logger.getLogger(MultiThreadTopicsProcessor.class);
}
