package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.GlobalPairInformation;
import eu.excitementproject.eop.biutee.rteflow.macro.OriginalTreesAfterInitialization;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search.BeamSearchTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.HypothesisInformation;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.ProgressFire;
import eu.excitementproject.eop.biutee.utilities.TreeHistoryUtilities;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.TimeElapsedTracker;



/**
 * Given an (already pre-processed) Text-Hypothesis pair, represented as {@link PairData},
 * this class finds the best entailment proof, and returns a feature-vector that represents that
 * proof.
 * 
 * @author Asher Stern
 * @since Feb 17, 2011
 *
 */
public class PairProcessor
{
	////////////////////// PUBLIC /////////////////////////////////

	// constructor and methods
	/**
	 * Constructor that gets a {@link PairData} and other utilities.
	 * 
	 * @param pairData The text-hypothesis pair to be processed
	 * @param classifier The classifier, by which the "best" proof is chosen, and being searched for.
	 * @param lemmatizer the {@linkplain Lemmatizer} is required for flip part-of-speech generation operation.
	 * @param script The {@linkplain OperationsScript} specifies which operations are done, and in which order.
	 * @param unigramProbabilityEstimation estimation of words probability, may be required for values in the feature-vector (currently not used) 
	 */
	public PairProcessor(ExtendedPairData pairData, LinearClassifier classifier, Lemmatizer lemmatizer, OperationsScript<Info, BasicNode> script, TESystemEnvironment teSystemEnvironment)
	{
		this.pairData = pairData;
		this.classifier = classifier;
		this.lemmatizer = lemmatizer;
		this.script = script;
		this.teSystemEnvironment = teSystemEnvironment;
	}
	
	/**
	 * If set to <tt>true</tt>, then the {@link TreeHistory} that will be
	 * created for this text-hypothesis pair will contain more knowledge
	 * about the history, then usual.
	 * Default - <tt>false</tt>
	 * <P>
	 * Usually, the {@link TreeHistory} contains a vector of {@link Specification}s
	 * (and optionally also a vector of feature-vectors). On the other
	 * hand, "rich history" contains also the trees themselves, as well
	 * as the affected nodes of each iteration.
	 * @param richInformationInTreeHistory
	 */
	public void setRichInformationInTreeHistory(boolean richInformationInTreeHistory)
	{
		this.richInformationInTreeHistory = richInformationInTreeHistory;
	}


	/**
	 * If set to <tt>true</tt>, then the features that correspond
	 * to the task name will not be set any value (i.e. will be
	 * set to zero).
	 * <BR>
	 * Default - <tt>false</tt>
	 * @param ignoreTaskName
	 */
	public void setIgnoreTaskName(boolean ignoreTaskName)
	{
		if (ignoreTaskName)
		{
			logger.warn("Ignoring task-name. The results will be inaccurate, unless the training was performed with that ignorance as well!");
		}
		this.ignoreTaskName = ignoreTaskName;
	}

	

	/**
	 * Sets the "surrounding context" of the text to the given list of
	 * trees. Usually, the surrounding context of each sentence are the
	 * other trees of the text.
	 * <BR>
	 * <B>In a normal mode of work - this method shouldn't be called!</B>
	 * <BR>
	 * <B>This method is used only by GUI</B>
	 * @param surroundingsContext
	 * @throws TeEngineMlException
	 */
	public void overrideSurroundingsContext(List<ExtendedNode> surroundingsContext) throws TeEngineMlException
	{
		if (null==surroundingsContext) throw new TeEngineMlException("Null surroundingsContext");
		logger.warn("Warning! overriding the surrounding context. Valid only for GUI");
		this.surroundingsContext = surroundingsContext;
	}
	

	/**
	 * Used in GUI only!
	 * @param percentageFire
	 */
	public void setProgressFire(ProgressFire percentageFire)
	{
		this.progressFire = percentageFire;
	}

	/**
	 * USED ONLY BY GUI
	 */
	public void setUseOldBeam(boolean useOldBeam)
	{
		this.useOldBeam = useOldBeam;
	}

	/**
	 * Finds the best proof. Later, a feature-vector that represents that proof can be retrieved
	 * by {@link #getBestTree()}.
	 * The proof itself is stored in a vector of {@linkplain Specification}s, which can be
	 * retrieved by {@link #getBestTreeHistory()}
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ClassifierException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 */
	public void process() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, AnnotatorException
	{
		try
		{
			if (pairData.getTextTrees().isEmpty())
				throw new TeEngineMlException("Empty text");

			this.script.setHypothesisInformation(new HypothesisInformation(pairData.getPair().getHypothesis(), pairData.getHypothesisTree()));
			//			textTreesProcessor = new BeamSearchTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, unigramProbabilityEstimation, this.ruleBasesToRetrieveMultiWords);
			//			textTreesProcessor = new Rte7TextTreesProcessor(pairData.getTextTrees(),pairData.getHypothesisTree(),pairData.getMapTreesToSentences(),pairData.getCoreferenceInformation(),classifier, lemmatizer,script, unigramProbabilityEstimation,this.ruleBasesToRetrieveMultiWords);

			//			AStarTextTreesProcessor astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, unigramProbabilityEstimation);
			//			astarTextTreesProcessor.setWeightOfCost(1);
			//			astarTextTreesProcessor.setWeightOfFuture(1000);
			//			astarTextTreesProcessor.setK_expandInEachIteration(10);
			//			astarTextTreesProcessor.useAnyTimeMode(10000, 0.1);
			//			textTreesProcessor = astarTextTreesProcessor;

			LocalCreativeTextTreesProcessor lcTextTreesProcessor = null;
			if (!useOldBeam)
			{
				lcTextTreesProcessor = new LocalCreativeTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, this.teSystemEnvironment);
			}
			// lcTextTreesProcessor.setNumberOfLocalIterations(4);
			BeamSearchTextTreesProcessor beamSearchTextTreesProcessor = null;
			if (useOldBeam)
			{
				beamSearchTextTreesProcessor = new BeamSearchTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,this.teSystemEnvironment);
			}
			// AStarTextTreesProcessor aStarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, this.teSystemEnvironment);
			// aStarTextTreesProcessor.setWeightOfFuture(30.0);
			// aStarTextTreesProcessor.useAnyTimeMode(10000, 0.5);
			// aStarTextTreesProcessor.setK_expandInEachIteration(30);
			// KStagedTextTreesProcessor kStagedTextTreesProcessor = new KStagedTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, this.teSystemEnvironment,1,1,0,0.0,1.0);
			// kStagedTextTreesProcessor.setkStagedDiscardExpandedStates(true);
			// kStagedTextTreesProcessor.setSeparatelyProcessTextSentencesMode(true);
			
			// beam search
			// KStagedTextTreesProcessor kStagedTextTreesProcessor = new KStagedTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, this.teSystemEnvironment,150,150,5,1.0,5.0);
			// kStagedTextTreesProcessor.setkStagedDiscardExpandedStates(true);
			// kStagedTextTreesProcessor.setSeparatelyProcessTextSentencesMode(true);
			
			
			// this is greedy with f=h
			// KStagedTextTreesProcessor kStagedTextTreesProcessor = new KStagedTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,this.teSystemEnvironment,1,1,0,0,1);
			
			if (!useOldBeam)
			{
				textTreesProcessor = lcTextTreesProcessor;
			}
			AbstractTextTreesProcessor abstractTextTreesProcessor = null;
			if (!useOldBeam)
			{
				abstractTextTreesProcessor = lcTextTreesProcessor;
			}
			else
			{
				abstractTextTreesProcessor = beamSearchTextTreesProcessor;
			}
			
			if (true==this.richInformationInTreeHistory)
			{
				abstractTextTreesProcessor.setRichInformationInTreeHistory(this.richInformationInTreeHistory);
			}
			if (surroundingsContext!=null) // only by GUI!
			{
				abstractTextTreesProcessor.setSurroundingsContext(surroundingsContext);
			}
			if (this.progressFire!=null)
			{
				abstractTextTreesProcessor.setProgressFire(this.progressFire);
			}
			
			
			
			
//			AStarLocalCreativeTextTreesProcessor aStarLocalCreativeTextTreesProcessor = new AStarLocalCreativeTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, unigramProbabilityEstimation, this.ruleBasesToRetrieveMultiWords);
//			// TODO hard coded
//			aStarLocalCreativeTextTreesProcessor.setLimitNumberOfChildren(2);
//			textTreesProcessor = aStarLocalCreativeTextTreesProcessor;

			// Just logging
			if (ignoreTaskName)
			{
				logger.info("Ignoring task name");
			}
			
			String datasetName = this.pairData.getDatasetName();
			// Set the global pair information
			if ( (ignoreTaskName) && (null==datasetName) ) // If you don't have to set anything, then don't
			{}
			else
			{
				GlobalPairInformation globalPairInformation = null;
				if (ignoreTaskName)
				{
					globalPairInformation = new GlobalPairInformation(null,datasetName);
				}
				else if  (null==datasetName)
				{
					globalPairInformation = new GlobalPairInformation(pairData.getPair().getAdditionalInfo(),null);
				}
				else
				{
					globalPairInformation = new GlobalPairInformation(pairData.getPair().getAdditionalInfo(),datasetName);
				}
				logger.info("Setting GlobalPairInformation: "+globalPairInformation.toString());
				abstractTextTreesProcessor.setGlobalPairInformation(globalPairInformation);
			}

			logger.info("PairProcessor.process: Using TextTreesProcessor: "+abstractTextTreesProcessor.getClass().getName());

			TimeElapsedTracker tracker = new TimeElapsedTracker();
			tracker.start();


			//textTreesProcessor = new AStarTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, unigramProbabilityEstimation);
			//			AStarTextTreesProcessor astarTextTreesProcessor = new AStarTextTreesProcessor(pairData.getTextTrees(), pairData.getHypothesisTree(),pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,unigramProbabilityEstimation);
			//			textTreesProcessor = astarTextTreesProcessor;
			abstractTextTreesProcessor.process();

			tracker.end();
			logger.info("PairProcessor.process Time: "+tracker.toString());
			
			if (!useOldBeam)
			{
				this.cpuTime = tracker.getCpuTimeElapsed();
				this.worldClockTime = tracker.getWorldClockElapsed();
				this.numberOfExpandedElements = textTreesProcessor.getNumberOfExpandedElements();
				this.numberOfGeneratedElements = textTreesProcessor.getNumberOfGeneratedElements();
			}
			

			//			if (logger.isDebugEnabled())
			//			{
			//				logger.debug("Lin average query time: "+LinDependencyFromDBLexicalRuleBase.linQueryTracker.getAverages());
			//			}
			
			this.originalTreesAfterInitialization = abstractTextTreesProcessor.getOriginalTreesAfterInitialization();

			this.bestTree = abstractTextTreesProcessor.getBestTree();
			this.bestTreeHistory = abstractTextTreesProcessor.getBestTreeHistory();
			this.bestTreeSentence = abstractTextTreesProcessor.getBestTreeSentence();

			if (logger.isDebugEnabled())
			{
				StringBuffer sb = new StringBuffer();
				sb.append("Done with sentence: ");
				sb.append(bestTreeSentence);
				sb.append("\nHistory:\n");
				sb.append(TreeHistoryUtilities.historyToString(bestTreeHistory));
				logger.debug(sb.toString());
			}

		}
		catch(TreeAndParentMapException e)
		{
			throw new TeEngineMlException("Processing failed. See nested exception",e);
		}
	}
	
	public OriginalTreesAfterInitialization getOriginalTreesAfterInitialization()
	{
		return originalTreesAfterInitialization;
	}


	/**
	 * Returns a tree that is the final consequent of the proof, with the feature-vector
	 * that represents the proof.
	 * @return
	 */
	public TreeAndFeatureVector getBestTree()
	{
		return this.bestTree;
	}

	/**
	 * A raw sentence which the best tree was originally created from.
	 * (the sentence that the original tree was created from)
	 * @return
	 */
	public String getBestTreeSentence()
	{
		return this.bestTreeSentence;
	}

	/**
	 * Returns the history, which is a vector of {@linkplain Specification}s, that describe
	 * the operations done in the proof.
	 * @return
	 */
	public TreeHistory getBestTreeHistory()
	{
		return this.bestTreeHistory;
	}
	

	public long getCpuTime()
	{
		return cpuTime;
	}

	public long getWorldClockTime()
	{
		return worldClockTime;
	}
	
	public Long getNumberOfExpandedElements()
	{
		return numberOfExpandedElements;
	}

	public Long getNumberOfGeneratedElements()
	{
		return numberOfGeneratedElements;
	}






	protected ExtendedPairData pairData;
	protected LinearClassifier classifier;
	protected Lemmatizer lemmatizer;
	protected OperationsScript<Info, BasicNode> script;
	protected TESystemEnvironment teSystemEnvironment;
	protected boolean richInformationInTreeHistory = false;
	
	protected List<ExtendedNode> surroundingsContext = null;


	protected boolean ignoreTaskName = false;

	
	protected WithStatisticsTextTreesProcessor textTreesProcessor = null;

	protected OriginalTreesAfterInitialization originalTreesAfterInitialization;
	
	protected TreeAndFeatureVector bestTree;
	protected String bestTreeSentence;
	protected TreeHistory bestTreeHistory;
	
	protected long cpuTime;
	protected long worldClockTime;
	protected Long numberOfExpandedElements;
	protected Long numberOfGeneratedElements;


	// for GUI only
	protected ProgressFire progressFire = null;
	
	protected boolean useOldBeam = false;
	
	private static Logger logger = Logger.getLogger(PairProcessor.class);
}
