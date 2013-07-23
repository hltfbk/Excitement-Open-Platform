package eu.excitementproject.eop.biutee.rteflow.macro.search.astar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.search.astar.AStarAlgorithm.AStarException;
import eu.excitementproject.eop.biutee.rteflow.macro.search.astar.AStarAlgorithm.StateCalculations;
import eu.excitementproject.eop.biutee.rteflow.micro.OperationsEnvironment;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Aug 9, 2011
 *
 */
public class AStarLocalCreativeStateCalculations implements StateCalculations<AStarLocalCreativeElement>
{
	public static final int NUMBER_OF_LOCAL_ITERATIONS = BiuteeConstants.LOCAL_CREATIVE_NUMBER_OF_LOCAL_ITERATIONS;
	
	public AStarLocalCreativeStateCalculations(LinearClassifier classifier,
			OperationsScript<Info, BasicNode> script,
			OperationsEnvironment operationsEnvironment,
			int numberOfHypothesisNodes, Set<String> hypothesisLemmasLowerCase,
			SingleTreeEvaluations initialTreeEvaluations,
			double initialCost, boolean compareByCostPlusFuture) throws TeEngineMlException
	{
		super();
		
		if (null==classifier)throw new TeEngineMlException("Null");
		if (null==script)throw new TeEngineMlException("Null");
		if (null==operationsEnvironment)throw new TeEngineMlException("Null");
		if (0==numberOfHypothesisNodes)throw new TeEngineMlException("0");
		if (null==hypothesisLemmasLowerCase)throw new TeEngineMlException("Null");
		if (null==initialTreeEvaluations)throw new TeEngineMlException("Null");
		
		this.classifier = classifier;
		this.script = script;
		this.operationsEnvironment = operationsEnvironment;
		this.numberOfHypothesisNodes = numberOfHypothesisNodes;
		this.hypothesisLemmasLowerCase = hypothesisLemmasLowerCase;
		this.initialElementEvaluations = initialTreeEvaluations;
		this.initialCost = initialCost;
		this.compareByCostPlusFuture = compareByCostPlusFuture;
	}
	
	

	/**
	 * Puts a limit on the number of generated children. When a limit is set,
	 * for each element (being polled from the open list) all children are generated, but
	 * immediately pruned and only the chipest <code>limitNumberOfChildren</code> children
	 * are being inserted into the open list, while the others are deleted.
	 *  
	 * @param limitNumberOfChildren
	 * @throws TeEngineMlException
	 */
	public void setLimitNumberOfChildren(Integer limitNumberOfChildren) throws TeEngineMlException
	{
		if(null==limitNumberOfChildren)throw new TeEngineMlException("Null value of parameter limitNumberOfChildren");
		this.limitNumberOfChildren = limitNumberOfChildren;
	}
	
	
	public void setWeightOfCost(double weightOfCost)
	{
		this.weightOfCost = weightOfCost;
	}

	public void setWeightOfFuture(double weightOfFuture)
	{
		this.weightOfFuture = weightOfFuture;
	}



	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.search.AStarAlgorithm.StateCalculations#isGoal(java.lang.Object)
	 */
	public boolean isGoal(AStarLocalCreativeElement state) throws AStarException
	{
		return state.isGoal();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.search.AStarAlgorithm.StateCalculations#stateChildrenAlreadyKnown(java.lang.Object, java.util.Set)
	 */
	public boolean stateChildrenAlreadyKnown(AStarLocalCreativeElement state, Set<AStarLocalCreativeElement> cloasedSet)
	{
		return (state.getLCChildren()!=null);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.search.AStarAlgorithm.StateCalculations#getChildren(java.lang.Object, java.util.Set)
	 */
	public List<AStarLocalCreativeElement> getChildren(AStarLocalCreativeElement state, Set<AStarLocalCreativeElement> cloasedSet) throws AStarException
	{
		if (state.getLCChildren()!=null)
			return state.getLCChildren();
		else
		{
			try
			{
				ChildrenGenerator childrenGenerator = new ChildrenGenerator(state, state.getIteration());
				childrenGenerator.generateAllChildren();
				List<AStarLocalCreativeElement> generatedChildren = childrenGenerator.getChildren();
				if (limitNumberOfChildren!=null)
				{
					Collections.sort(generatedChildren);
					List<AStarLocalCreativeElement> limitedChildren = new ArrayList<AStarLocalCreativeElement>(limitNumberOfChildren);
					Iterator<AStarLocalCreativeElement> childrenIterator = generatedChildren.iterator();
					for (int limitIndex=0;limitIndex<limitNumberOfChildren;++limitIndex)
					{
						if (childrenIterator.hasNext())
						limitedChildren.add(childrenIterator.next());
					}
					generatedChildren = limitedChildren;
				}
				if (logger.isDebugEnabled())
				{
					logger.debug("generatedChildren.size() = "+generatedChildren.size());
					for (AStarLocalCreativeElement childElement : generatedChildren)
					{
						
						logger.debug("costProfitGain = "+childElement.getCostProfitGain()+". History size = "+childElement.getHistory().getSpecifications().size());
					}
				}
				
				state.setLCChildren(generatedChildren);
				return generatedChildren;
			}
			catch (TeEngineMlException e)
			{
				throw new AStarException("Failed to generate children. See nested exception.",e);
			}
			catch (OperationException e)
			{
				throw new AStarException("Failed to generate children. See nested exception.",e);
			}
			catch (ScriptException e)
			{
				throw new AStarException("Failed to generate children. See nested exception.",e);
			}
			catch (RuleBaseException e)
			{
				throw new AStarException("Failed to generate children. See nested exception.",e);
			}
			catch (TreeAndParentMapException e)
			{
				throw new AStarException("Failed to generate children. See nested exception.",e);
			}
			catch (ClassifierException e)
			{
				throw new AStarException("Failed to generate children. See nested exception.",e);
			}
		}
	}
	
	private class ChildrenGenerator
	{
		public ChildrenGenerator(AStarLocalCreativeElement startingElement, int globalIteration)
		{
			super();
			this.startingElement = startingElement;
			this.globalIteration = globalIteration;
		}
		
		public void generateAllChildren() throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
		{
			this.children = new LinkedList<AStarLocalCreativeElement>();
			generate(startingElement,null,0);
			children = convertToArrayList(children);
			done=true;
		}
		
		public List<AStarLocalCreativeElement> getChildren() throws TeEngineMlException
		{
			if (!done)throw new TeEngineMlException("Children were not generated.");
			return children;
		}

		private void generate(AStarLocalCreativeElement element, Set<ExtendedNode> affectedNodes, int localIteration) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
		{
			TreeAndFeatureVector textTreeAndFeatureVector = new TreeAndFeatureVector(element.getTree(), element.getFeatureVector());
			Set<TreeAndFeatureVector> setTrees = new LinkedHashSet<TreeAndFeatureVector>();
			setTrees.add(textTreeAndFeatureVector);
			ImmutableList<SingleOperationItem> operations = script.getItemListForLocalCreativeIteration(globalIteration, localIteration, setTrees);
			TreesGeneratorByOperations generator =
				new TreesGeneratorByOperations(
						textTreeAndFeatureVector,
						operations,
						script,
						element.getHistory(),
						operationsEnvironment
						);
			
			if (affectedNodes!=null)
			{
				generator.setAffectedNodes(affectedNodes);
			}
			generator.generateTrees();
			
			Set<TreeAndFeatureVector> generatedTrees = generator.getGeneratedTrees();
			Map<TreeAndFeatureVector,TreeHistory> historyMap = generator.getHistoryMap();
			Map<ExtendedNode, Set<ExtendedNode>> mapAffectedNodes = generator.getMapAffectedNodes();
			
			for (TreeAndFeatureVector generatedTree : generatedTrees)
			{
				TreeHistory history = historyMap.get(generatedTree);
				Specification lastSpec = history.getSpecifications().get(history.getSpecifications().size()-1);
				
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap = 
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
				
				// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(treeAndParentMap, operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, numberOfHypothesisNodes);
				SingleTreeEvaluations evaluations = createSingleTreeEvaluations(treeAndParentMap);
				boolean itIsGoal = false;
				if (0==evaluations.getMissingRelations()) itIsGoal=true;
				double unweightedFutureEstimation = GeneratedTreeStateCalculations.generateUnweightedFutureEstimation(evaluations);
				double futureEstimation = GeneratedTreeStateCalculations.generateFutureEstimation(evaluations,weightOfFuture);
				double cost = GeneratedTreeStateCalculations.generateCost(classifier,generatedTree.getFeatureVector(),weightOfCost);
				Double costProfitGain = getCostProfitGain(initialElementEvaluations,evaluations,cost);
				
				AStarLocalCreativeElement childElement =
					new AStarLocalCreativeElement(
							globalIteration+1,
							generatedTree.getTree(),
							element.getOriginalSentence(),
							generatedTree.getFeatureVector(),
							lastSpec,
							history,
							element,
							cost,
							unweightedFutureEstimation,
							futureEstimation,
							itIsGoal,
							costProfitGain,
							compareByCostPlusFuture
							);
				
				children.add(childElement);
				if ((!itIsGoal) && (localIteration<NUMBER_OF_LOCAL_ITERATIONS))
				{
					if (!mapAffectedNodes.containsKey(generatedTree.getTree())) throw new TeEngineMlException("BUG");
					Set<ExtendedNode> childAffectedNodes = mapAffectedNodes.get(generatedTree.getTree());
					if (null==childAffectedNodes)throw new TeEngineMlException("BUG");
					generate(childElement,childAffectedNodes,localIteration+1);
				}
				
			}
		}
		
		private AStarLocalCreativeElement startingElement;
		private int globalIteration;
		
		private List<AStarLocalCreativeElement> children = new LinkedList<AStarLocalCreativeElement>();
		private boolean done=false;
	}
	
	
	protected SingleTreeEvaluations createSingleTreeEvaluations(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree)
	{
		return new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), textTree, operationsEnvironment.getHypothesis()).getEvaluations(operationsEnvironment.getHypothesisLemmasLowerCase(), operationsEnvironment.getHypothesisNumberOfNodes());
	}


	private static <T> List<T> convertToArrayList(List<T> list)
	{
		ArrayList<T> ret = new ArrayList<T>(list.size());
		ret.addAll(list);
		return ret;
	}
	
	private Double getCostProfitGain(SingleTreeEvaluations initialElementEvaluations, SingleTreeEvaluations currentTreeEvaluations, double cost)
	{
		if (cost<initialCost)throw new RuntimeException("cost<initialCost");
		double gapDelta = getGapOfEvaluations(initialElementEvaluations)-getGapOfEvaluations(currentTreeEvaluations);
		if (gapDelta<=0) return null; // null==infinite
		else return (cost-initialCost)/gapDelta;
	}
	
	private static double getGapOfEvaluations(SingleTreeEvaluations evaluations)
	{
		return (double)(evaluations.getMissingLemmas()+evaluations.getMissingNodes()+evaluations.getMissingRelations());
	}

	
	private LinearClassifier classifier;
	private OperationsScript<Info, BasicNode> script;
	private OperationsEnvironment operationsEnvironment;
	
	private Integer limitNumberOfChildren = null; // null = no limit
	
	private double weightOfCost = 1.0;
	private double weightOfFuture = 1.0;
	private boolean compareByCostPlusFuture;

	@SuppressWarnings("unused")
	private Set<String> hypothesisLemmasLowerCase;
	@SuppressWarnings("unused")
	private int numberOfHypothesisNodes;
	
	private SingleTreeEvaluations initialElementEvaluations;
	private double initialCost;
	
	
	
	private static final Logger logger = Logger.getLogger(AStarLocalCreativeStateCalculations.class);
}
