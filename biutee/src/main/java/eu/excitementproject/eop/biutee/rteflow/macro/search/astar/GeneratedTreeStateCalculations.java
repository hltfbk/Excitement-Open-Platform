package eu.excitementproject.eop.biutee.rteflow.macro.search.astar;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.search.astar.AStarAlgorithm.AStarException;
import eu.excitementproject.eop.biutee.rteflow.micro.OperationsEnvironment;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.biutee.script.SingleOperationType;
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
 * Ignores the closed-set.
 * 
 * @author Asher Stern
 * @since Jun 17, 2011
 *
 */
public class GeneratedTreeStateCalculations implements AStarAlgorithm.StateCalculations<AStarElement>
{
	/////////////////////////////////// STATIC PART /////////////////////////////////////////
	/////////////////////// USED FOR CALCULATION OF COST AND FUTURE  ////////////////////////
	
	public static final Map<SingleOperationType,Integer> BELIEFSTTL_MAP;
	public static final Map<SingleOperationType,Integer> PREFER_MAP;
	static
	{
		BELIEFSTTL_MAP = new LinkedHashMap<SingleOperationType, Integer>();
		BELIEFSTTL_MAP.put(SingleOperationType.UNJUSTIFIED_INSERTION,0);
		BELIEFSTTL_MAP.put(SingleOperationType.UNJUSTIFIED_MOVE,0);
		BELIEFSTTL_MAP.put(SingleOperationType.PARSER_ANTECEDENT_SUBSTITUTION,0);
		BELIEFSTTL_MAP.put(SingleOperationType.MULTIWORD_SUBSTITUTION,0);
		BELIEFSTTL_MAP.put(SingleOperationType.FLIP_POS_SUBSTITUTION,0);
		BELIEFSTTL_MAP.put(SingleOperationType.COREFERENCE_SUBSTITUTION,0);
		BELIEFSTTL_MAP.put(SingleOperationType.RULE_APPLICATION,2);
		BELIEFSTTL_MAP.put(SingleOperationType.LEXICAL_RULE_BY_LEMMA_APPLICATION,2);
		BELIEFSTTL_MAP.put(SingleOperationType.LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION,2);
		BELIEFSTTL_MAP.put(SingleOperationType.LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION_2D,2);
		
		
		PREFER_MAP = new LinkedHashMap<SingleOperationType, Integer>();
		for (SingleOperationType type : SingleOperationType.values())
		{
			if (type.isRuleApplication())
				PREFER_MAP.put(type,1);
			else
				PREFER_MAP.put(type,0);
		}
	}
	
	public static double getBeliefForOperationItem(SingleOperationItem operationItem)
	{
		return 0.5;
	}
	
	public static double generateUnweightedFutureEstimation(SingleTreeEvaluations evaluations)
	{
		double futureEstimation = evaluations.getMissingLemmasPortion()+evaluations.getMissingNodesPortion()+evaluations.getMissingRelationsPortion();
		return futureEstimation;
	}
	
	public static double generateFutureEstimation(SingleTreeEvaluations evaluations, double weightOfFuture)
	{
		double futureEstimation = generateUnweightedFutureEstimation(evaluations);
		futureEstimation = weightOfFuture*futureEstimation;
		return futureEstimation;
	}

	
	public static double generateUnweightedFutureEstimationWithBelief(SingleTreeEvaluations evaluations,double belief,Set<String> hypothesisLemmasLowerCase,int numberOfHypothesisNodes)
	{
		double ret = 0;
		int missingRelations = evaluations.getMissingRelations();
		
		if (0==missingRelations) // if it is goal
		{
			ret = 0;
		}
		else
		{
			int missingNodes = evaluations.getMissingNodes();
			int missingLemmas = evaluations.getMissingLemmas();
			missingRelations-=belief;
			missingNodes-=belief;
			missingLemmas-=belief;

			double missingNodesPortion = ((double)missingNodes) / ((double)numberOfHypothesisNodes);
			double missingRelationsPortion = ((double)missingRelations) / ((double)(numberOfHypothesisNodes-1));
			double missingLemmasPortion = ((double)missingLemmas) / ((double)(hypothesisLemmasLowerCase.size()));
			
			ret = missingLemmasPortion+missingNodesPortion+missingRelationsPortion;
		}
		return ret;
	}
	
	public static double generateFutureEstimationWithBelief(SingleTreeEvaluations evaluations,double belief,Set<String> hypothesisLemmasLowerCase,int numberOfHypothesisNodes,double weightOfFuture)
	{
		double future = generateUnweightedFutureEstimationWithBelief(evaluations,belief,hypothesisLemmasLowerCase,numberOfHypothesisNodes);
		future = future*weightOfFuture;
		return future;
	}
	
	
	public static double generateCost(LinearClassifier linearClassifier, Map<Integer,Double> featureVector, double weightOfCost) throws ClassifierException
	{
		double cost = -linearClassifier.getProduct(featureVector);
		cost = weightOfCost*cost;
		return cost;
	}
	
	/////////////////////// PUBLIC CONSTRUCTOR AND METHODS ////////////////////////////
	
	public GeneratedTreeStateCalculations(LinearClassifier classifier,
			OperationsScript<Info, BasicNode> script,
			OperationsEnvironment operationsEnvironment,
			Set<String> hypothesisLemmasLowerCase, int numberOfHypothesisNodes,
			double weightOfCost, double weightOfFuture)
	{
		super();
		this.classifier = classifier;
		this.script = script;
		this.operationsEnvironment = operationsEnvironment;
		this.hypothesisLemmasLowerCase = hypothesisLemmasLowerCase;
		this.numberOfHypothesisNodes = numberOfHypothesisNodes;
		this.weightOfCost = weightOfCost;
		this.weightOfFuture = weightOfFuture;
	}
	
	public void setWeightOfFuture(double weightOfFuture)
	{
		this.weightOfFuture = weightOfFuture;
	}
	
	public void setBeliefMode(boolean beliefMode)
	{
		this.beliefMode = beliefMode;
	}
	
	public void setPreferredMode(boolean preferedMode)
	{
		this.preferredMode = preferedMode;
	}

	public boolean isGoal(AStarElement state)
	{
		return state.isGoal();
	}

	public List<AStarElement> getChildren(AStarElement state, Set<AStarElement> closedSet) throws AStarException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Working on element with estimation: "+strDouble(state.getTotalEstimation()));
		}
		try
		{
			List<AStarElement> ret;
			if ((!beliefMode)&&(state.getChildren()!=null))
			{
				List<AStarElement> knownChildren = state.getChildren();
				ret = new ArrayList<AStarElement>(knownChildren.size());
				for (AStarElement knownElement : knownChildren)
				{
					double unweightedFuture = knownElement.getUnweightedFutureEstimation();
					if ((0==unweightedFuture) && (!knownElement.isGoal()))
						throw new AStarException("0 future");
					AStarElement newElement =
						new AStarElement(knownElement.getIteration(),
								knownElement.getTree(),
								knownElement.getOriginalSentence(),
								knownElement.getFeatureVector(),
								knownElement.getLastSpec(),
								knownElement.getHistory(),
								state,
								knownElement.getCost(),
								unweightedFuture,
								weightOfFuture*unweightedFuture,
								knownElement.isGoal());
					
					newElement.setChildren(knownElement.getChildren());
					newElement.setBeliefTTL(knownElement.getBeliefTTL());
					newElement.setBelievedFuture(knownElement.getBelievedFuture());
					
					ret.add(newElement);
				}
			}
			else
			{
				if (this.preferredMode)
				{
					ret = preferModeGetChildren(state,closedSet, 0);
				}
				else
				{
					TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(state.getTree(), state.getFeatureVector());
					Set<TreeAndFeatureVector> setTrees = new LinkedHashSet<TreeAndFeatureVector>();
					setTrees.add(treeAndFeatureVector);
					ImmutableList<SingleOperationItem> operations = script.getItemListForIteration(state.getIteration(), setTrees);
					TreesGeneratorByOperations generator = 
						new TreesGeneratorByOperations(treeAndFeatureVector, operations, script,  state.getHistory(),this.operationsEnvironment);

					generator.generateTrees();

					if (!beliefMode)
					{
						ret = regularGetChildren(state,generator);
					}
					else
					{
						ret = beliefModeGetChildren(state,generator);
					}
				}
			}
				
			state.setChildren(ret);
			
			if (ret.size()==0)
			{
				logger.warn("A state with no children. Seems like a BUG, unless an unusual OperationsScript is used.");
			}
			if ( (ret.size()==0) && (logger.isDebugEnabled()) )
			{
				// SingleTreeEvaluations evalsOriginalState = SingleTreeEvaluations.create(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(state.getTree()) , operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, numberOfHypothesisNodes);
				SingleTreeEvaluations evalsOriginalState = createSingleTreeEvaluations(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(state.getTree()));
				logger.debug("No children for state with missing relations: "+evalsOriginalState.getMissingRelations());
			}
			return ret;
		}
		catch(TeEngineMlException e)
		{
			throw new AStarAlgorithm.AStarException("See nested",e);
		} catch (OperationException e)
		{
			throw new AStarAlgorithm.AStarException("See nested",e);
		} catch (ScriptException e)
		{
			throw new AStarAlgorithm.AStarException("See nested",e);
		} catch (RuleBaseException e)
		{
			throw new AStarAlgorithm.AStarException("See nested",e);
		} catch (TreeAndParentMapException e)
		{
			throw new AStarAlgorithm.AStarException("See nested",e);
		} catch (ClassifierException e)
		{
			throw new AStarAlgorithm.AStarException("See nested",e);
		}
	}
	
	public boolean stateChildrenAlreadyKnown(AStarElement state, Set<AStarElement> cloasedSet)
	{
		if (this.beliefMode)return false;
		else return (state.getChildren()!=null);
	}

	
	
	///////////////////// PROTECTED & PRIVATE //////////////////////////
	
	protected List<AStarElement> preferModeGetChildren(AStarElement state, Set<AStarElement> closedSet, int level) throws AStarException, TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
	{
		List<AStarElement> ret = new ArrayList<AStarElement>();
		TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(state.getTree(), state.getFeatureVector());
		Set<TreeAndFeatureVector> setTrees = new LinkedHashSet<TreeAndFeatureVector>();
		setTrees.add(treeAndFeatureVector);
		ImmutableList<SingleOperationItem> operations = script.getItemListForIteration(state.getIteration(), setTrees);
		TreesGeneratorByOperations generator = 
			new TreesGeneratorByOperations(treeAndFeatureVector, operations, script, state.getHistory(), this.operationsEnvironment);

		generator.generateTrees();
		
		Map<TreeAndFeatureVector,TreeHistory> generatedTreesHistory = generator.getHistoryMap();
		Map<SingleOperationItem, Set<TreeAndFeatureVector>> mapGenerated = generator.getMapGeneratedByOperation();
		for (SingleOperationItem operationItem : mapGenerated.keySet())
		{
			Integer preferLevelOfItem = PREFER_MAP.get(operationItem.getType());
			if (null==preferLevelOfItem)
				preferLevelOfItem=0;
			
			for (TreeAndFeatureVector generatedTree : mapGenerated.get(operationItem))
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
				TreeHistory history = generatedTreesHistory.get(generatedTree);
				Specification lastSpec = history.getSpecifications().get(history.getSpecifications().size()-1);
				
				double cost = generateCost(classifier, generatedTree.getFeatureVector(), weightOfCost);
				
				// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(treeAndParentMap, operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, numberOfHypothesisNodes);
				SingleTreeEvaluations evaluations = createSingleTreeEvaluations(treeAndParentMap);
				double unweightedFutureEstimation = generateUnweightedFutureEstimation(evaluations);
				double futureEstimation = generateFutureEstimation(evaluations, weightOfFuture);
				
				boolean itIsGoal = false;
				if (0==evaluations.getMissingRelations())
					itIsGoal=true;
				
				
				AStarElement element =
					new AStarElement(state.getIteration()+1, generatedTree.getTree(),
							state.getOriginalSentence(), generatedTree.getFeatureVector(), lastSpec,
							history, state, cost, unweightedFutureEstimation, futureEstimation, itIsGoal);
				
				if ( (level<preferLevelOfItem) && (!itIsGoal) )
				{
					ret.addAll(preferModeGetChildren(element,closedSet,level+1));
				}
				else
				{
					ret.add(element);
				}
			}
		}
		
		if (ret.size()==0)
		{
			logger.warn("A state with no children. Seems like a BUG, unless an unusual OperationsScript is used.");
		}
		return ret;

	}
	
	protected List<AStarElement> regularGetChildren(AStarElement state, TreesGeneratorByOperations generator) throws TreeAndParentMapException, ClassifierException
	{
		Set<TreeAndFeatureVector> generatedTrees = generator.getGeneratedTrees();
		Map<TreeAndFeatureVector,TreeHistory> generatedTreesHistory = generator.getHistoryMap();

		List<AStarElement> ret = new ArrayList<AStarElement>(generatedTrees.size());
		for(TreeAndFeatureVector generatedTree : generatedTrees)
		{
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
			TreeHistory history = generatedTreesHistory.get(generatedTree);
			Specification lastSpec = history.getSpecifications().get(history.getSpecifications().size()-1);
			
			double cost = generateCost(classifier, generatedTree.getFeatureVector(), weightOfCost);
			
			// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(treeAndParentMap, operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, numberOfHypothesisNodes);
			SingleTreeEvaluations evaluations = createSingleTreeEvaluations(treeAndParentMap);
			double unweightedFutureEstimation = generateUnweightedFutureEstimation(evaluations);
			double futureEstimation = generateFutureEstimation(evaluations, weightOfFuture);
			
			boolean itIsGoal = false;
			if (0==evaluations.getMissingRelations())
				itIsGoal=true;
			
			
			AStarElement element =
				new AStarElement(state.getIteration()+1, generatedTree.getTree(),
						state.getOriginalSentence(), generatedTree.getFeatureVector(), lastSpec,
						history, state, cost, unweightedFutureEstimation, futureEstimation, itIsGoal);
			
			if (logger.isDebugEnabled())
			{
				logger.debug("Adding element with total-estimation: "+strDouble(element.getTotalEstimation())+", and cost: "+strDouble(cost)+", and future: "+strDouble(futureEstimation)+", and missing-relations = "+evaluations.getMissingRelations());
				logger.debug("Added element spec: "+lastSpec.toString());
			}
			
			ret.add(element);
		}
		
		return ret;
	}
	

	
	protected List<AStarElement> beliefModeGetChildren(AStarElement state, TreesGeneratorByOperations generator) throws TreeAndParentMapException, ClassifierException, TeEngineMlException
	{
		Map<SingleOperationItem, Set<TreeAndFeatureVector>> mapGenerated =
			generator.getMapGeneratedByOperation();
		List<AStarElement> ret = new ArrayList<AStarElement>(generator.getGeneratedTrees().size());
		Map<TreeAndFeatureVector,TreeHistory> generatedTreesHistory = generator.getHistoryMap();
		for (SingleOperationItem operationItem : mapGenerated.keySet())
		{
			Set<TreeAndFeatureVector> generatedTrees = mapGenerated.get(operationItem);
			for (TreeAndFeatureVector generatedTree : generatedTrees)
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
				TreeHistory history = generatedTreesHistory.get(generatedTree);
				if (null==history) throw new TeEngineMlException("BUG");
				Specification lastSpec = history.getSpecifications().get(history.getSpecifications().size()-1);
				
				double cost = generateCost(classifier, generatedTree.getFeatureVector(), weightOfCost);
				
				
				// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(treeAndParentMap, operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, numberOfHypothesisNodes);
				SingleTreeEvaluations evaluations = createSingleTreeEvaluations(treeAndParentMap);
				int beliefTTL;
				double futureEstimation;
				double believedFutureEstimation;
				if (state.getBeliefTTL()>1) // then current > 0
				{
					believedFutureEstimation = state.getBelievedFuture();
					double calculatedFutureEstimation =
						generateFutureEstimation(evaluations,this.weightOfFuture);
					
					futureEstimation = Math.min(calculatedFutureEstimation, believedFutureEstimation);
					beliefTTL = state.getBeliefTTL()-1;
				}
				else
				{
					Integer byMapBeliefTTL = BELIEFSTTL_MAP.get(operationItem.getType());
					if (null==byMapBeliefTTL)
						byMapBeliefTTL=0;
					
					if (0==byMapBeliefTTL)
					{
						beliefTTL=0;
						futureEstimation = generateFutureEstimation(evaluations,this.weightOfFuture);
						believedFutureEstimation = futureEstimation;
					}
					else
					{
						double belief = getBeliefForOperationItem(operationItem);
						
						TreeAndParentMap<ExtendedInfo, ExtendedNode> previousTreeAndParentMap =
							new TreeAndParentMap<ExtendedInfo, ExtendedNode>(state.getTree());
						// SingleTreeEvaluations previousEvaluations = SingleTreeEvaluations.create(previousTreeAndParentMap, operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, numberOfHypothesisNodes);
						SingleTreeEvaluations previousEvaluations = createSingleTreeEvaluations(previousTreeAndParentMap);
						
						believedFutureEstimation = generateFutureEstimationWithBelief(previousEvaluations,belief,hypothesisLemmasLowerCase,numberOfHypothesisNodes,this.weightOfFuture);
						double calculatedFutureEstimation =
							generateFutureEstimation(evaluations,this.weightOfFuture);
						
						futureEstimation = Math.min(believedFutureEstimation, calculatedFutureEstimation);
						beliefTTL = byMapBeliefTTL.intValue();
					}
				}
				
				// TODO put some value in the unweighted.
				double unweightedFutureEstimation=0;
				
				boolean itIsGoal = false;
				if (0==evaluations.getMissingRelations())
					itIsGoal=true;
				
				
				AStarElement element =
					new AStarElement(state.getIteration()+1, generatedTree.getTree(),
							state.getOriginalSentence(), generatedTree.getFeatureVector(), lastSpec,
							history, state, cost, unweightedFutureEstimation, futureEstimation, itIsGoal);
				
				element.setBeliefTTL(beliefTTL);
				element.setBelievedFuture(believedFutureEstimation);
				
				if (logger.isDebugEnabled())
				{
					logger.debug("Adding element with total-estimation: "+strDouble(element.getTotalEstimation())+", and cost: "+strDouble(cost)+", and future: "+strDouble(futureEstimation)+", and missing-relations = "+evaluations.getMissingRelations());
					logger.debug("Added element spec: "+lastSpec.toString());
				}
				ret.add(element);
			}
		}
		if (ret.size()!=generator.getGeneratedTrees().size())throw new TeEngineMlException("BUG");
		return ret;
		
	}
	
	protected SingleTreeEvaluations createSingleTreeEvaluations(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree)
	{
		return new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), textTree, operationsEnvironment.getHypothesis()).getEvaluations(operationsEnvironment.getHypothesisLemmasLowerCase(), operationsEnvironment.getHypothesisNumberOfNodes());
	}
	
	
	private static String strDouble(double d)
	{
		return String.format("%4.5f", d);
	}
	

	private LinearClassifier classifier;
	private OperationsScript<Info, BasicNode> script;
	private OperationsEnvironment operationsEnvironment;

	private Set<String> hypothesisLemmasLowerCase;
	private int numberOfHypothesisNodes;
	
	private double weightOfCost=1.0;
	private double weightOfFuture=1.0;
	private boolean beliefMode = false;
	private boolean preferredMode = false;
	
	private static final Logger logger = Logger.getLogger(GeneratedTreeStateCalculations.class);

}
