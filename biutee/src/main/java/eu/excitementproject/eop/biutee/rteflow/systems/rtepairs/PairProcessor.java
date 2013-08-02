package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.RtePairsProver;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairInstance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.rtrpairs.THPairProof;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.GlobalPairInformation;
import eu.excitementproject.eop.biutee.rteflow.macro.OriginalTreesAfterInitialization;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search.BeamSearchTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.biutee.utilities.ProgressFire;
import eu.excitementproject.eop.biutee.utilities.Provider;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
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
 * Given an (already pre-processed) Text-Hypothesis pair, represented as {@link ExtendedPairData},
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
	 * Constructor that gets a {@link ExtendedPairData} and other utilities.
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
			// Create "Prover"
			PairProcessorProver prover = new PairProcessorProver(teSystemEnvironment,
					new Provider<Lemmatizer>(){public Lemmatizer get() throws BiuteeException{return lemmatizer;}}
					);
			
			// Run the prover
			TimeElapsedTracker tracker = new TimeElapsedTracker();
			tracker.start();
			THPairProof proof = prover.prove(new THPairInstance(pairData), script, classifier);
			tracker.end();
			logger.info("PairProcessor.process Time: "+tracker.toString());

			// Collect the results
			this.bestTree = proof.getTreeAndFeatureVector();
			this.bestTreeHistory = proof.getHistory();
			this.bestTreeSentence = proof.getBestSentence();
			this.originalTreesAfterInitialization = abstractTextTreesProcessor.getOriginalTreesAfterInitialization();

			// Collect time-statistics
			this.cpuTime = tracker.getCpuTimeElapsed();
			this.worldClockTime = tracker.getWorldClockElapsed();
			if (textTreesProcessor!=null)
			{
				this.numberOfExpandedElements = textTreesProcessor.getNumberOfExpandedElements();
				this.numberOfGeneratedElements = textTreesProcessor.getNumberOfGeneratedElements();
			}
		}
		catch (BiuteeException e)
		{
			throw new TeEngineMlException("Pair process failed.",e);
		}
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
	
	public OriginalTreesAfterInitialization getOriginalTreesAfterInitialization()
	{
		return originalTreesAfterInitialization;
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

	
	/**
	 * Creates a {@link TextTreesProcessor} which will process the given T-H pair.<BR>
	 * <B>Note: this methods also assigns values to the fields
	 * {@link #textTreesProcessor} and {@link #abstractTextTreesProcessor}</B>.
	 * 
	 * @return the {@link TextTreesProcessor} which will process the given T-H pair.
	 * @throws TeEngineMlException
	 */
	protected TextTreesProcessor createProcessor() throws TeEngineMlException
	{
		if (pairData.getTextTrees().isEmpty())
			throw new TeEngineMlException("Empty text");


		LocalCreativeTextTreesProcessor lcTextTreesProcessor = null;
		if (!useOldBeam)
		{
			lcTextTreesProcessor = new LocalCreativeTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), classifier, lemmatizer, script, this.teSystemEnvironment);
		}
		BeamSearchTextTreesProcessor beamSearchTextTreesProcessor = null;
		if (useOldBeam)
		{
			beamSearchTextTreesProcessor = new BeamSearchTextTreesProcessor(pairData.getPair().getText(), pairData.getPair().getHypothesis(), pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(),classifier, lemmatizer, script,this.teSystemEnvironment);
		}
		
		if (!useOldBeam)
		{
			textTreesProcessor = lcTextTreesProcessor;
		}
		abstractTextTreesProcessor = null;
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

		logger.info("PairProcessor.process: Using TextTreesProcessor: "+abstractTextTreesProcessor.getClass().getName());

		return abstractTextTreesProcessor;
	}
	
	

	

	/**
	 * A {@link Prover}, subclass of {@link RtePairsProver}, which uses the
	 * {@link TextTreesProcessor} created by {@link PairProcessor#createProcessor()}.
	 * 
	 * @author Asher Stern
	 * @since Aug 2, 2013
	 *
	 */
	private class PairProcessorProver extends RtePairsProver
	{
		public PairProcessorProver(TESystemEnvironment teSystemEnvironment, Provider<Lemmatizer> lemmatizerProvider)
		{
			super(teSystemEnvironment, lemmatizerProvider);
		}
		
		@Override
		protected TextTreesProcessor createProcessor(THPairInstance instance,
				OperationsScript<Info, BasicNode> script,
				LinearClassifier classifierForSearch) throws BiuteeException, TeEngineMlException
		{
			TextTreesProcessor processor = PairProcessor.this.createProcessor();
			processor.setGlobalPairInformation(createGlobalPairInformation(instance.getPairData()));
			return processor;
		}
		
	
		@Override
		protected GlobalPairInformation createGlobalPairInformation(ExtendedPairData pairData) throws BiuteeException
		{
			GlobalPairInformation globalPairInformation = null;
			
			// Just logging
			if (ignoreTaskName){logger.info("Ignoring task name");}

			
			String datasetName = pairData.getDatasetName();
			// Set the global pair information
			if ( (ignoreTaskName) && (null==datasetName) ) // If you don't have to set anything, then don't
			{
				globalPairInformation = null;
			}
			else
			{
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
			}
			return globalPairInformation;
		}
	}
	



	// input
	protected final ExtendedPairData pairData;
	protected final LinearClassifier classifier;
	protected final Lemmatizer lemmatizer;
	protected final OperationsScript<Info, BasicNode> script;
	protected final TESystemEnvironment teSystemEnvironment;
	
	protected boolean richInformationInTreeHistory = false;
	protected List<ExtendedNode> surroundingsContext = null;
	protected boolean ignoreTaskName = false;
	
	// for GUI only
	protected ProgressFire progressFire = null;
	protected boolean useOldBeam = false;

	
	protected WithStatisticsTextTreesProcessor textTreesProcessor = null;
	protected AbstractTextTreesProcessor abstractTextTreesProcessor = null; 
	protected OriginalTreesAfterInitialization originalTreesAfterInitialization;
	
	// output
	protected TreeAndFeatureVector bestTree;
	protected String bestTreeSentence;
	protected TreeHistory bestTreeHistory;
	
	protected long cpuTime;
	protected long worldClockTime;
	protected Long numberOfExpandedElements;
	protected Long numberOfGeneratedElements;


	
	private static Logger logger = Logger.getLogger(PairProcessor.class);
}
