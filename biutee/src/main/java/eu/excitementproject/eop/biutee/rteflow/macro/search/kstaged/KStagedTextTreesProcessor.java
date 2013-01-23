package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search.BeamSearchTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.datastructures.SingleItemList;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * Yet another implementation of {@link TextTreesProcessor}, that makes the <B>search</B>
 * for the best proof by an algorithm named "K Staged Weighted A*", which is similar
 * to the old beam search ( see {@link BeamSearchTextTreesProcessor}).
 * 
 * 
 * @see TextTreesProcessor
 * @see BeamSearchTextTreesProcessor
 * 
 * @author Asher Stern
 * @since Sep 2, 2011
 *
 */
public class KStagedTextTreesProcessor extends AbstractTextTreesProcessor implements WithStatisticsTextTreesProcessor
{
	public KStagedTextTreesProcessor(String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment,
			int numberToExpand,
			int numberToRetain,
			int numberOfIterationsAfterFound,
			double weightOfCost,
			double weightOfFuture
			)
			throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree, originalMapTreesToSentences,
				coreferenceInformation, classifier, lemmatizer, script,
				teSystemEnvironment);
		if (0==numberToExpand) throw new TeEngineMlException("0==numberToExpand");
		if (0==numberToRetain) throw new TeEngineMlException("0==numberToRetain");
		this.numberToExpand = numberToExpand;
		this.numberToRetain = numberToRetain;
		this.numberOfIterationsAfterFound = numberOfIterationsAfterFound;
		this.weightOfCost = weightOfCost;
		this.weightOfFuture = weightOfFuture;
	}
	
	public void setGradientMode(boolean gradientMode)
	{
		this.gradientMode = gradientMode;
	}
	public void setLocalCreativeMode(boolean localCreativeMode)
	{
		this.localCreativeMode = localCreativeMode;
	}
	public void setSeparatelyProcessTextSentencesMode(boolean separatelyProcessTextSentencesMode)
	{
		this.separatelyProcessTextSentencesMode = separatelyProcessTextSentencesMode;
	}
	public void setkStagedDiscardExpandedStates(boolean kStagedDiscardExpandedStates)
	{
		this.kStagedDiscardExpandedStates = kStagedDiscardExpandedStates;
	}
	
	/**
	 * See {@link SimpleKStagedComparator#SimpleKStagedComparator(double, double, int)}
	 * @param iterationOfEquality
	 */
	public void setDynamicWeightingMode(int iterationOfEquality)
	{
		this.dynamicWeightingIterationOfEquality = new Integer(iterationOfEquality);
	}

	public TreeAndFeatureVector getBestTree() throws TeEngineMlException
	{
		if (null==bestTree) throw new TeEngineMlException("Seems that process was not called.");
		return this.bestTree;
	}

	public String getBestTreeSentence() throws TeEngineMlException
	{
		if (null==bestTreeSentence) throw new TeEngineMlException("Seems that process was not called.");
		return this.bestTreeSentence;
	}

	public TreeHistory getBestTreeHistory() throws TeEngineMlException
	{
		if (null==bestTreeHistory) throw new TeEngineMlException("Seems that process was not called.");
		return this.bestTreeHistory;
	}
	
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.macro.search.WithStatisticsTextTreesProcessor#getNumberOfExpandedElements()
	 */
	public long getNumberOfExpandedElements() throws TeEngineMlException
	{
		if (null==bestTree) throw new TeEngineMlException("Seems that process was not called.");
		return numberOfExpansions;
	}

	public long getNumberOfExpensiveGenerations() throws TeEngineMlException
	{
		if (null==bestTree) throw new TeEngineMlException("Seems that process was not called.");
		return numberOfExpensiveGenerations;
	}
	
	public long getNumberOfGeneratedElements() throws TeEngineMlException
	{
		if (null==bestTree) throw new TeEngineMlException("Seems that process was not called.");
		return numberOfGenerations;
	}


	private void logParameters()
	{
		if (logger.isInfoEnabled())
		{
			logger.info("KStagedTextTreesProcessor parameters:"+
					" numberToExpand = "+ numberToExpand+
					" numberToRetain = "+ numberToRetain+
					" numberOfIterationsAfterFound = "+ numberOfIterationsAfterFound+
					" weightOfCost = "+ weightOfCost+
					" weightOfFuture = "+ weightOfFuture+
					" gradientMode = "+ gradientMode+
					" localCreativeMode = "+ localCreativeMode+
					" separatelyProcessTextSentencesMode = "+ separatelyProcessTextSentencesMode+
					" kStagedDiscardExpandedStates = "+ kStagedDiscardExpandedStates+
					" dynamicWeightingIterationOfEquality = "+ dynamicWeightingIterationOfEquality);
		}
	}

	@Override
	protected void processPair() throws ClassifierException,
	TreeAndParentMapException, TeEngineMlException, OperationException,
	ScriptException, RuleBaseException
	{
		logParameters();
		if ( (this.dynamicWeightingIterationOfEquality!=null) && (this.gradientMode) )throw new TeEngineMlException("Gradient mode does not support dynamic weighting!");
		List<KStagedElement> initialStates = createInitialStates();
		KStagedElement goal = null;
		if (separatelyProcessTextSentencesMode)
		{
			logger.debug("Working in separatelyProcessTextSentencesMode");
			List<KStagedElement> goals = new ArrayList<KStagedElement>(this.originalTextTrees.size());
			int sentenceIndex=0;
			for (KStagedElement initialState : initialStates)
			{
				if (logger.isDebugEnabled())logger.debug("Working on sentence: #"+sentenceIndex);
				goals.add(
						processStates(new SingleItemList<KStagedElement>(initialState))
				);
				if (logger.isDebugEnabled())logger.debug("done #"+sentenceIndex);
			}
			goal = Collections.min(goals, new CostOnlyComparator());
		}
		else
		{
			goal = processStates(initialStates);
		}
		if (null==goal) throw new TeEngineMlException("BUG Null goal");
		this.bestTree = new TreeAndFeatureVector(goal.getTree(), goal.getFeatureVector());
		this.bestTreeHistory = goal.getHistory();
		this.bestTreeSentence = goal.getOriginalSentence();
		if (null==bestTree) throw new TeEngineMlException("BUG Null tree");
		if (null==bestTreeHistory) throw new TeEngineMlException("BUG Null history");
		if (null==bestTreeSentence) throw new TeEngineMlException("BUG Null sentence");
	}
	
	
	
	protected KStagedElement processStates(List<KStagedElement> initialStates) throws TeEngineMlException
	{
		StateCalculator<KStagedElement> calculator;
		if (localCreativeMode)
		{
			calculator = new KStagedLocalCreativeStateCalculator(script, operationsEnvironment,this.classifier);			
		}
		else
		{
			calculator = new KStagedStateCalculator(this.script, this.operationsEnvironment,this.classifier);
		}

		ByIterationComparator<KStagedElement> comparatorForExpand;
		ByIterationComparator<KStagedElement> comparatorForCut;
		if (gradientMode)
		{
			comparatorForExpand = new GradientKStagedComparator(this.weightOfCost,this.weightOfFuture);
			comparatorForCut = new GradientKStagedComparator(this.weightOfCost,this.weightOfFuture);
		}
		else
		{
			if (null==this.dynamicWeightingIterationOfEquality)
			{
				comparatorForExpand = new SimpleKStagedComparator(this.weightOfCost,this.weightOfFuture);
				comparatorForCut = new SimpleKStagedComparator(this.weightOfCost,this.weightOfFuture);
			}
			else
			{
				comparatorForExpand = new SimpleKStagedComparator(this.weightOfCost,this.weightOfFuture,this.dynamicWeightingIterationOfEquality.intValue());
				comparatorForCut = new SimpleKStagedComparator(this.weightOfCost,this.weightOfFuture,this.dynamicWeightingIterationOfEquality.intValue());
			}
		}

		
		KStagedAlgorithm<KStagedElement> algorithm =
			new KStagedAlgorithm<KStagedElement>(
					initialStates,
					comparatorForExpand,
					comparatorForCut,
					calculator,
					this.numberToExpand,
					this.numberToRetain,
					false,
					this.numberOfIterationsAfterFound,
					new CostOnlyComparator(),
					kStagedDiscardExpandedStates
			);
		try
		{
			logger.debug("Running KStagedAlgorithm...");
			algorithm.find();
			KStagedElement goal = algorithm.getBestGoal();
			this.numberOfExpansions += algorithm.getNumberOfExpansions();
			this.numberOfGenerations += algorithm.getNumberOfGenerations();
			this.numberOfExpensiveGenerations += algorithm.getNumberOfExpensiveGenerations();
			return goal;
		}
		catch(KStagedAlgorithmException e)
		{
			throw new TeEngineMlException("Search failed.",e);
		}
	}
	
	
	
	
	protected List<KStagedElement> createInitialStates() throws ClassifierException, TreeAndParentMapException, TeEngineMlException
	{
		List<KStagedElement> initialStates = new ArrayList<KStagedElement>(originalTextTrees.size());
		for (ExtendedNode tree : originalTextTrees)
		{
			Map<Integer,Double> featureVector = initialFeatureVector();
			double cost = -this.classifier.getProduct(featureVector);
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
			
			// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(treeAndParentMap, operationsEnvironment.getHypothesis(), this.operationsEnvironment.getHypothesisLemmasLowerCase(), this.operationsEnvironment.getHypothesisNumberOfNodes());
			SingleTreeEvaluations evaluations = new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), treeAndParentMap, operationsEnvironment.getHypothesis()).getEvaluations(operationsEnvironment.getHypothesisLemmasLowerCase(), operationsEnvironment.getHypothesisNumberOfNodes());
			KStagedElement element =
				new KStagedElement(
						tree,
						new TreeHistory(TreeHistoryComponent.onlyFeatureVector(initialFeatureVector())),
						featureVector,
						originalMapTreesToSentences.get(tree),
						cost,
						evaluations,
						0,
						evaluations,
						cost
						);
			
			initialStates.add(element);
		}
		return initialStates;
	}
	
	
	private static class CostOnlyComparator implements Comparator<KStagedElement>
	{
		public int compare(KStagedElement o1, KStagedElement o2)
		{
			double cost1 = o1.getCost();
			double cost2 = o2.getCost();
			if (cost1<cost2)return -1;
			else if (cost1==cost2)return 0;
			else return 1;
		}
	}
	
	

	private int numberToExpand; // small k
	private int numberToRetain; // big K
	private int numberOfIterationsAfterFound;
	private double weightOfCost;
	private double weightOfFuture;
	private boolean gradientMode = false;
	private boolean localCreativeMode = false;
	private boolean separatelyProcessTextSentencesMode = false;
	private boolean kStagedDiscardExpandedStates = false;
	private Integer dynamicWeightingIterationOfEquality = null;
	


	
	private TreeAndFeatureVector bestTree=null;
	private String bestTreeSentence=null;
	private TreeHistory bestTreeHistory=null;
	
	private long numberOfExpansions = 0;
	private long numberOfExpensiveGenerations = 0;
	private long numberOfGenerations = 0;
	
	private static final Logger logger = Logger.getLogger(KStagedTextTreesProcessor.class);
}
