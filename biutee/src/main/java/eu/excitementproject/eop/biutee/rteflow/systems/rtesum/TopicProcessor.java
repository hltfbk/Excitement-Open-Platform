package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedPreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedTopicDataSetGenerator;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.PreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.StopFlag;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.TimeElapsedTracker;

/**
 * !!! NO LONGER USED !!!
 * This class is used only if {@link Constants#USE_OLD_CONCURRENCY_IN_RTE_SUM} is <tt>true</tt>
 * 
 * @see MultiThreadTopicsProcessorOld
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class TopicProcessor
{
	public TopicProcessor(PreprocessedTopicDataSet topic,
			LinearClassifier classifierForSearch,
			Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment,
			StopFlag stopFlag)
	{
		super();
		this.topic = topic;
		this.classifierForSearch = classifierForSearch;
		this.lemmatizer = lemmatizer;
		this.script = script;
		this.teSystemEnvironment = teSystemEnvironment;
		this.stopFlag = stopFlag;
	}


	public void process() throws TreeCoreferenceInformationException, TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, TreeAndParentMapException, TreeStringGeneratorException, AnnotatorException
	{
		try
		{
			logger.info("Working on topic: "+this.topic.getTopicDataSet().getTopicId());
			logger.info("Initializing...");
			init();

			logger.info("Processing...");
			Map<String, Map<Integer, ExtendedNode>> topicTreesMap = extendedTopic.getDocumentsTreesMap();
			Map<String, Set<SentenceIdentifier>> candidates = topicDS.getCandidatesMap();
			for (String hypothesisID : candidates.keySet())
			{
				String hypothesisSentence = topicDS.getHypothesisMap().get(hypothesisID);
				logger.info("Working on hypothesis: "+hypothesisID+" - "+hypothesisSentence);
				
				ExtendedNode hypothesisTree = extendedTopic.getHypothesisTreesMap().get(hypothesisID);

				// for off-line chaining - i.e. a mechanism that makes the chaining outside
				// of the engine (Eyal's code).
				script.setHypothesisInformation(new HypothesisInformation(hypothesisSentence, hypothesisTree));
				
				// results for current hypothesis
				Map<SentenceIdentifier,RteSumSingleCandidateResult> currentHypothesisResult = new LinkedHashMap<SentenceIdentifier, RteSumSingleCandidateResult>();

				// candidates for current hypothesis
				Set<SentenceIdentifier> candidateSentences = candidates.get(hypothesisID);
				
				for (SentenceIdentifier sentenceID : candidateSentences) // for each candidate
				{
					if (this.stopFlag.isStop())throw new StopFlag.StopException();

					// Take the parse tree of the candidate
					Map<Integer, ExtendedNode> documentTrees = topicTreesMap.get(sentenceID.getDocumentId());
					int sentenceIndex = Integer.valueOf(sentenceID.getSentenceId());
					ExtendedNode textTree = documentTrees.get(sentenceIndex); // this is the parse tree.

					// Wrap it in a list with only a single item - this tree.
					List<ExtendedNode> textTreeAsList = new ArrayList<ExtendedNode>(1);
					textTreeAsList.add(textTree);

					// Create a list of other sentence that will be considered as "exist in pair"
					List<ExtendedNode> surroundingTextTrees = surroundingUtility.getSurroundingSentences(sentenceID, textTree);

					// Create map from tree to sentence. It will contain only that single tree. 
					Map<ExtendedNode, String> mapTreesToSentences = new LinkedHashMap<ExtendedNode, String>();
					String textSentence = topicDS.getDocumentsMap().get(sentenceID.getDocumentId()).get(sentenceIndex);
					if (null==textSentence) throw new TeEngineMlException("BUG");
					mapTreesToSentences.put(textTree,textSentence);

					// And find the best proof.
					logger.info("Working on entailment by \""+textSentence+"\" of hypothesis: \""+hypothesisSentence+"\"");
					// KStagedTextTreesProcessor beamSearch = new KStagedTextTreesProcessor(textTreeAsList, hypothesisTree, mapTreesToSentences, extendedTopic.getCoreferenceInformation().get(sentenceID.getDocumentId()), this.classifierForSearch, lemmatizer, script, teSystemEnvironment,150,150,5,1.0,5.0);
					// beamSearch.setkStagedDiscardExpandedStates(true);
					// beamSearch.setSeparatelyProcessTextSentencesMode(true);
					// AStarTextTreesProcessor pureHeuristicTextTreesProcessor = new AStarTextTreesProcessor(textTreeAsList, hypothesisTree, mapTreesToSentences, extendedTopic.getCoreferenceInformation().get(sentenceID.getDocumentId()), this.classifierForSearch, lemmatizer, script, teSystemEnvironment);
					// pureHeuristicTextTreesProcessor.setWeightOfFuture(1.0);
					// pureHeuristicTextTreesProcessor.setWeightOfCost(0.0);
					// aStarTextTreesProcessor.setWeightOfFuture(200.0);
					// aStarTextTreesProcessor.useAnyTimeMode(10000, 0.5);
					// KStagedTextTreesProcessor greedyTextTreesProcessor = new KStagedTextTreesProcessor(textTreeAsList, hypothesisTree, mapTreesToSentences, extendedTopic.getCoreferenceInformation().get(sentenceID.getDocumentId()), this.classifierForSearch, lemmatizer, script, teSystemEnvironment,1,1,0,1.0,3.0);
					// greedyTextTreesProcessor.setkStagedDiscardExpandedStates(true);
					// greedyTextTreesProcessor.setSeparatelyProcessTextSentencesMode(true);
					
					// BeamSearchTextTreesProcessor beamTextTreesProcessor = new BeamSearchTextTreesProcessor(textTreeAsList, hypothesisTree, mapTreesToSentences, extendedTopic.getCoreferenceInformation().get(sentenceID.getDocumentId()), this.classifierForSearch, lemmatizer, script, teSystemEnvironment);
					String textText = topic.getTopicDataSet().getDocumentsMap().get(sentenceID.getDocumentId()).get(Integer.parseInt(sentenceID.getSentenceId()));
					String hypothesisText = topic.getTopicDataSet().getHypothesisMap().get(hypothesisID);
					LocalCreativeTextTreesProcessor lcTextTreesProcessor = new LocalCreativeTextTreesProcessor(textText, hypothesisText, textTreeAsList, hypothesisTree, mapTreesToSentences, extendedTopic.getCoreferenceInformation().get(sentenceID.getDocumentId()), this.classifierForSearch, lemmatizer, script, teSystemEnvironment);
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
							new RteSumSingleCandidateResult(sentenceID,hypothesisID,textSentence,hypothesisSentence,processor.getBestTree().getTree(),featureVector,processor.getBestTreeHistory(),
									tracker.getCpuTimeElapsed(),processor.getNumberOfExpandedElements(),processor.getNumberOfGeneratedElements());
					}
					else
					{
						resultCurrentCandidate =
							new RteSumSingleCandidateResult(sentenceID,hypothesisID,textSentence,hypothesisSentence,processor.getBestTree().getTree(),featureVector,processor.getBestTreeHistory());
					}
					currentHypothesisResult.put(sentenceID, resultCurrentCandidate);
					
					
					logger.info("Hypothesis-sentence entailment processing done.");

				}
				result.put(hypothesisID, currentHypothesisResult);
			}
		}
		catch(StopFlag.StopException se)
		{
			logger.warn("Stop flag was set. Stopping.");
			this.result = null;
		}
	}
	
	
	/**
	 * Returns the results of the current topic. The result is a map from hypothesis-id
	 * to a map from each candidate to its result.
	 * 
	 * @return a map from hypothesis-id to a map from each candidate to its result.
	 * @throws TeEngineMlException 
	 */
	public Map<String, Map<SentenceIdentifier, RteSumSingleCandidateResult>> getResult() throws TeEngineMlException
	{
		if (null==result) throw new TeEngineMlException("Null result");
		return result;
	}



	private void init() throws TreeCoreferenceInformationException, TeEngineMlException, TreeStringGeneratorException, AnnotatorException
	{
		boolean noProblem = false;
		try
		{
			logger.debug("init()...");

			// map from hypothesis-id to a map from sentence-identifiers to their results.
			result = new LinkedHashMap<String, Map<SentenceIdentifier,RteSumSingleCandidateResult>>();

			ExtendedTopicDataSetGenerator extendedGenerator = new ExtendedTopicDataSetGenerator(topic,teSystemEnvironment);
			logger.debug("Calling ExtendedTopicDataSetGenerator.generate()...");
			extendedGenerator.generate();
			logger.debug("ExtendedTopicDataSetGenerator.generate() done.");
			extendedTopic = extendedGenerator.getExtendedTopic();
			topicDS = topic.getTopicDataSet();

			logger.debug("Building RTESumSurroundingSentencesUtility...");
			this.surroundingUtility = new RTESumSurroundingSentencesUtility(extendedTopic);
			logger.debug("Building RTESumSurroundingSentencesUtility done.");
			noProblem = true;
		}
		finally
		{
			if (logger.isDebugEnabled())
			{
				if (noProblem)
				{
					logger.debug("init() succeeded.");
				}
				else
				{
					logger.debug("init() failed.");
				}
			}
		}
	}
	

	private PreprocessedTopicDataSet topic;
	private LinearClassifier classifierForSearch;
	private Lemmatizer lemmatizer;
	private OperationsScript<Info, BasicNode> script;
	private TESystemEnvironment teSystemEnvironment;
	
	private ExtendedPreprocessedTopicDataSet extendedTopic;
	private TopicDataSet topicDS;
	/**
	 * map from hypothesis-id to a map from sentence-identifiers to their results.
	 */
	private Map<String,Map<SentenceIdentifier,RteSumSingleCandidateResult>> result;
	private RTESumSurroundingSentencesUtility surroundingUtility;
	
	private final StopFlag stopFlag;

	private static final Logger logger = Logger.getLogger(TopicProcessor.class);
}
