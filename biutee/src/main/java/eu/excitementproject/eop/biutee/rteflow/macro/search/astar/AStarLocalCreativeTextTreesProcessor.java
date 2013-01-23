package eu.excitementproject.eop.biutee.rteflow.macro.search.astar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.astar.AStarAlgorithm.AStarException;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.TreeHistoryUtilities;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;


/**
 * 
 * @author Asher Stern
 * @since Aug 10, 2011
 *
 */
public class AStarLocalCreativeTextTreesProcessor extends AbstractTextTreesProcessor implements WithStatisticsTextTreesProcessor
{
	public AStarLocalCreativeTextTreesProcessor(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees, ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment)
			throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree, originalMapTreesToSentences,
				coreferenceInformation, classifier, lemmatizer, script,
				teSystemEnvironment);
		
	}
	
	public void setLimitNumberOfChildren(Integer limitNumberOfChildren) throws TeEngineMlException
	{
		if(null==limitNumberOfChildren)throw new TeEngineMlException("Null value of parameter limitNumberOfChildren");
		this.limitNumberOfChildren = limitNumberOfChildren;
	}
	
	public void setCompareByCostPlusFuture(boolean compareByCostPlusFuture)
	{
		this.compareByCostPlusFuture = compareByCostPlusFuture;
	}
	
	public void setWeightOfCost(double weightOfCost)
	{
		this.weightOfCost = weightOfCost;
	}

	public void setWeightOfFuture(double weightOfFuture)
	{
		this.weightOfFuture = weightOfFuture;
	}
	
	public void setK_expandInEachIteration(int k_expandInEachIteration)
	{
		this.k_expandInEachIteration = k_expandInEachIteration;
	}

	public TreeAndFeatureVector getBestTree() throws TeEngineMlException
	{
		if (null==this.bestTreeAndFeatureVector)throw new TeEngineMlException("Not computed");
		return this.bestTreeAndFeatureVector;
	}

	
	public String getBestTreeSentence() throws TeEngineMlException
	{
		if (null==this.bestTreeSentence)throw new TeEngineMlException("Not computed");
		return this.bestTreeSentence;
	}

	
	public TreeHistory getBestTreeHistory() throws TeEngineMlException
	{
		if (null==this.bestTreeHistory)throw new TeEngineMlException("Not computed");
		return this.bestTreeHistory;
	}
	
	public long getNumberOfExpandedElements()
	{
		return numberOfExpandedElements;
	}

	public long getNumberOfGeneratedElements()
	{
		return numberOfGeneratedElements;
	}
	
	public long getNumberOfExpensiveGeneratedElements()
	{
		return numberOfExpensiveGeneratedElements;
	}

	@Override
	protected void processPair() throws ClassifierException,
			TreeAndParentMapException, TeEngineMlException, OperationException,
			ScriptException, RuleBaseException
	{
		numberOfExpandedElements=0;
		numberOfGeneratedElements=0;
		numberOfExpensiveGeneratedElements=0;
		hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(operationsEnvironment.getHypothesis());
		try
		{
			Set<AStarLocalCreativeElement> initialStates = initialStates();
			if (logger.isDebugEnabled()){logger.debug("Number of initial states = "+initialStates.size());}
			List<AStarLocalCreativeElement> goals = new ArrayList<AStarLocalCreativeElement>(initialStates.size());
			int sentenceIndex=0;
			for (AStarLocalCreativeElement initialState : initialStates)
			{
				logger.info("Working on sentence: #"+sentenceIndex);
				TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(initialState.getTree());
				// SingleTreeEvaluations initialTreeEvaluations = SingleTreeEvaluations.create(textTreeAndParentMap, operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, this.hypothesisNumberOfNodes);
				SingleTreeEvaluations initialTreeEvaluations = new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), textTreeAndParentMap, operationsEnvironment.getHypothesis()).getEvaluations(operationsEnvironment.getHypothesisLemmasLowerCase(), operationsEnvironment.getHypothesisNumberOfNodes());
				
				double initialCost = GeneratedTreeStateCalculations.generateCost(this.classifier, initialState.getFeatureVector(), 1.0);
				
				AStarLocalCreativeStateCalculations stateCalculations =
					new AStarLocalCreativeStateCalculations(
							classifier,
							script,
							operationsEnvironment,
							hypothesisNumberOfNodes,
							hypothesisLemmasLowerCase,
							initialTreeEvaluations,
							initialCost,
							compareByCostPlusFuture
							);
				stateCalculations.setWeightOfCost(this.weightOfCost);
				stateCalculations.setWeightOfFuture(this.weightOfFuture);
				if (limitNumberOfChildren!=null)
					stateCalculations.setLimitNumberOfChildren(limitNumberOfChildren);
					
				
				Set<AStarLocalCreativeElement> setState = new LinkedHashSet<AStarLocalCreativeElement>();
				setState.add(initialState);
				AStarAlgorithm<AStarLocalCreativeElement> astarAlgorithm =
					new AStarAlgorithm<AStarLocalCreativeElement>(setState,stateCalculations,new CostOnlyAStarElementComparator());
				if (this.k_expandInEachIteration!=null)
				{
					astarAlgorithm.setK_expandInEachIteration(this.k_expandInEachIteration);
				}
				astarAlgorithm.find();
				numberOfExpandedElements+=astarAlgorithm.getNumberOfExpandedElements();
				numberOfGeneratedElements+=astarAlgorithm.getNumberOfGeneratedElements();
				numberOfExpensiveGeneratedElements+=astarAlgorithm.getNumberOfExpensiveGeneratedElements();
				AStarLocalCreativeElement goalFound = astarAlgorithm.getFoundGoalState();
				if (astarAlgorithm.isAnyGoalFound())
				{
					goals.add(goalFound);
				}
				else
				{
					throw new TeEngineMlException("Goal was not found.");
				}
				if (logger.isDebugEnabled())
				{
					logger.debug("History of proof for sentence #"+sentenceIndex+":\n"+
							TreeHistoryUtilities.historyToString(goalFound.getHistory()));
					logger.debug("Cost of proof for sentence #"+sentenceIndex+": "+String.format("%-3.6f", goalFound.getCost()));
				}
				
				++sentenceIndex;
			}
			Collections.sort(goals, new CostOnlyAStarElementComparator());
			if (goals.size()<1) throw new TeEngineMlException("No goals.");
			AStarLocalCreativeElement bestElement = goals.iterator().next();
			
			this.bestTreeAndFeatureVector = new TreeAndFeatureVector(bestElement.getTree(),bestElement.getFeatureVector());
			this.bestTreeSentence = bestElement.getOriginalSentence();
			this.bestTreeHistory = bestElement.getHistory();
		}
		catch(AStarException e)
		{
			throw new TeEngineMlException("AStar failed.",e);
		}
	}
	
	private static final class CostOnlyAStarElementComparator implements Comparator<AStarLocalCreativeElement>
	{
		public int compare(AStarLocalCreativeElement o1, AStarLocalCreativeElement o2)
		{
			double o1Cost = o1.getCost();
			double o2Cost = o2.getCost();
			if (o1Cost<o2Cost) return -1;
			else if (o1Cost==o2Cost) return 0;
			else return 1;
		}
	}
	
	private Set<AStarLocalCreativeElement> initialStates() throws TreeAndParentMapException, TeEngineMlException, ClassifierException
	{
		Set<AStarLocalCreativeElement> ret = new LinkedHashSet<AStarLocalCreativeElement>();
		for (ExtendedNode textTree : this.originalTextTrees)
		{
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(textTree);
			// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(textTreeAndParentMap, operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, hypothesisNumberOfNodes);
			SingleTreeEvaluations evaluations = new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), textTreeAndParentMap, operationsEnvironment.getHypothesis()).getEvaluations(operationsEnvironment.getHypothesisLemmasLowerCase(), operationsEnvironment.getHypothesisNumberOfNodes());
			boolean itIsGoal = (evaluations.getMissingRelations()==0);
			double unweightedFutureEstimation = GeneratedTreeStateCalculations.generateUnweightedFutureEstimation(evaluations);
			double futureEstimation = GeneratedTreeStateCalculations.generateFutureEstimation(evaluations,weightOfFuture);
			Map<Integer,Double> initialFeatureVector = initialFeatureVector();
			double initialCost = GeneratedTreeStateCalculations.generateCost(this.classifier, initialFeatureVector, weightOfCost);
			AStarLocalCreativeElement element = 
				new AStarLocalCreativeElement(
						0,
						textTree,
						originalMapTreesToSentences.get(textTree),
						initialFeatureVector(),
						null,
						new TreeHistory(TreeHistoryComponent.onlyFeatureVector(initialFeatureVector())),
						null,
						initialCost,
						unweightedFutureEstimation,
						futureEstimation,
						itIsGoal,
						null,
						compareByCostPlusFuture
						);
			
			ret.add(element);
		}
		return ret;
	}

	private Set<String> hypothesisLemmasLowerCase;
	
	private double weightOfCost = 1.0;
	private double weightOfFuture = 1.0;
	private boolean compareByCostPlusFuture = false;
	private Integer k_expandInEachIteration = null;

	
	private TreeAndFeatureVector bestTreeAndFeatureVector;
	private String bestTreeSentence;
	private TreeHistory bestTreeHistory;
	
	private Integer limitNumberOfChildren = null; // null = no limit
	
	private long numberOfExpandedElements = 0;
	private long numberOfGeneratedElements = 0;
	private long numberOfExpensiveGeneratedElements = 0;

	
	private static final Logger logger = Logger.getLogger(AStarLocalCreativeTextTreesProcessor.class);
}
