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
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
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
 * @since Jun 17, 2011
 *
 */
public class AStarTextTreesProcessor extends AbstractTextTreesProcessor implements WithStatisticsTextTreesProcessor
{
	public static final double ANYTIME_MINIMUM_LEGAL_WEIGHT_OF_FUTURE = 0.0000001;
	
	public AStarTextTreesProcessor(String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment
			
			)
			throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree, originalMapTreesToSentences,
				coreferenceInformation, classifier, lemmatizer, script,
				teSystemEnvironment);
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
	
	public void setAnyTime_numberOfGoalStates(int anyTime_numberOfGoalStates)
	{
		this.anyTime_numberOfGoalStates = anyTime_numberOfGoalStates;
	}
	
	/**
	 * Any time mode = the algorithm finds a goal, then changes "cost of future" and starts again,
	 * to find (maybe another) goal, then changes "cost of future" and starts again, etc.
	 * It stops after total number of generations in all of the activations of the algorithm
	 * exceeded  <code>maxNumberOfGeneration</code>.
	 * <BR>
	 * Note that the first iteration is not limited by <code>maxNumberOfGeneration</code>, and
	 * it is guaranteed that the first iteration ends only if a goal state was found.
	 * 
	 * @param maxNumberOfGeneration
	 * @param futureCostReduceFactor
	 * @param anyTimeModeAccordingToExpensive
	 */
	public void useAnyTimeMode(long maxNumberOfGeneration,double futureCostReduceFactor, boolean anyTimeModeAccordingToExpensive)
	{
		this.anyTimeMode = true;
		this.maxNumberOfGeneration = maxNumberOfGeneration;
		this.futureCostReduceFactor = futureCostReduceFactor;
		this.anyTimeModeAccordingToExpensive = anyTimeModeAccordingToExpensive;
	}
	
	public void useAnyTimeMode(long maxNumberOfGeneration,double futureCostReduceFactor)
	{
		useAnyTimeMode(maxNumberOfGeneration,futureCostReduceFactor,false);
	}
	
	/**
	 * A bad idea that is no longer used. Forget this method.
	 * 
	 * @param maxNumberOfGeneration
	 * @param futureCostReduceFactor
	 */
	public void useSmartAnyTimeMode(long maxNumberOfGeneration,double futureCostReduceFactor)
	{
		this.smartAnyTimeMode = true;
		this.maxNumberOfGeneration = maxNumberOfGeneration;
		this.futureCostReduceFactor = futureCostReduceFactor;
	}

//   * This is a wrong comment
//	 * Another idea that did not work practically. "belief mode" means that for
//	 * some operations - the grand-children will be created immediately, so the "children"
//	 * actually will not be inserted to the queue, but only the grand-children. 
//	 * @param beliefMode
	public void setBeliefMode(boolean beliefMode)
	{
		this.beliefMode = beliefMode;
	}
	

//   * This is a wrong comment
//	 * Another idea that did not work empirically. The "preferred mode" means that for some
//	 * operations the "cost of future" differs than the "cost of future" of other operations.
//	 * 
//	 * @param preferredMode
	public void setPreferedMode(boolean preferredMode)
	{
		this.preferredMode = preferredMode;
	}
	
	/**
	 * See {@link AStarAlgorithm#setWhenEqualTakeAll(boolean)}. Default <tt>false</tt>
	 * @param whenEqualTakeAll
	 */
	public void setWhenEqualTakeAll(boolean whenEqualTakeAll)
	{
		this.whenEqualTakeAll = whenEqualTakeAll;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.TextTreesProcessor#getBestTree()
	 */
	public TreeAndFeatureVector getBestTree() throws TeEngineMlException
	{
		if (!processingDone) throw new TeEngineMlException("Not processed.");
		return this.bestTree;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.TextTreesProcessor#getBestTreeSentence()
	 */
	public String getBestTreeSentence() throws TeEngineMlException
	{
		if (!processingDone) throw new TeEngineMlException("Not processed.");
		return this.bestTreeSentence;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.TextTreesProcessor#getBestTreeHistory()
	 */
	public TreeHistory getBestTreeHistory() throws TeEngineMlException
	{
		if (!processingDone) throw new TeEngineMlException("Not processed.");
		return this.bestTreeHistory;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.macro.search.WithStatisticsTextTreesProcessor#getNumberOfExpandedElements()
	 */
	public long getNumberOfExpandedElements()
	{
		return numberOfExpandedElements;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.rteflow.macro.search.WithStatisticsTextTreesProcessor#getNumberOfGeneratedElements()
	 */
	public long getNumberOfGeneratedElements()
	{
		return numberOfGeneratedElements;
	}

	public static class ComparatorByCostOnly implements Comparator<AStarElement>
	{
		public int compare(AStarElement o1, AStarElement o2)
		{
			int ret = 0;
			if (o1.getCost()<o2.getCost())
				ret=-1;
			else if (o1.getCost()==o2.getCost())
				ret = 0;
			else
				ret = 1;
			return ret;
		}
	}

	
	//////////////////////////////// PROTECTED & PRIVATE ///////////////////////////////////
	
	


	@Override
	protected void processPair() throws ClassifierException,
			TreeAndParentMapException, TeEngineMlException, OperationException,
			ScriptException, RuleBaseException
	{
		Set<String> hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(operationsEnvironment.getHypothesis());
		int numberOfHypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(operationsEnvironment.getHypothesis().getTree()).size();
		Set<AStarElement> startStates = new LinkedHashSet<AStarElement>();
		for (ExtendedNode textTree : originalTextTrees)
		{
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(textTree);
			Map<Integer,Double> featureVector = initialFeatureVector();
			
			double cost = GeneratedTreeStateCalculations.generateCost(classifier, featureVector, this.weightOfCost);
			
			// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(treeAndParentMap, operationsEnvironment.getHypothesis(), hypothesisLemmasLowerCase, numberOfHypothesisNodes);
			SingleTreeEvaluations evaluations = new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), treeAndParentMap, operationsEnvironment.getHypothesis()).getEvaluations(operationsEnvironment.getHypothesisLemmasLowerCase(), operationsEnvironment.getHypothesisNumberOfNodes());
			double unweightedFutureEstimation = GeneratedTreeStateCalculations.generateUnweightedFutureEstimation(evaluations);
			double futureEstimation = GeneratedTreeStateCalculations.generateFutureEstimation(evaluations, this.weightOfFuture);
			boolean itIsGoal = (0==evaluations.getMissingRelations());

			AStarElement element =
				new AStarElement(0, textTree, originalMapTreesToSentences.get(textTree),featureVector , null, new TreeHistory(TreeHistoryComponent.onlyFeatureVector(featureVector)), null, cost, unweightedFutureEstimation, futureEstimation, itIsGoal);
			
			startStates.add(element);
		}
		
		try
		{
			if (this.anyTimeMode)
				processPairAnyTimeMode(hypothesisLemmasLowerCase, numberOfHypothesisNodes, startStates);
			else
			{
				GeneratedTreeStateCalculations stateCalculations =
					new GeneratedTreeStateCalculations(classifier, script,
							operationsEnvironment,
							hypothesisLemmasLowerCase,numberOfHypothesisNodes,
							weightOfCost, weightOfFuture);

				stateCalculations.setPreferredMode(this.preferredMode);
				stateCalculations.setBeliefMode(this.beliefMode);
					

				aStarAlgorithm = new AStarAlgorithm<AStarElement>(startStates,stateCalculations,comparatorByCostOnly);
				aStarAlgorithm.setWhenEqualTakeAll(this.whenEqualTakeAll);

				aStarAlgorithm.setK_expandInEachIteration(this.k_expandInEachIteration);
				aStarAlgorithm.setAnyTime_numberOfGoalStates(this.anyTime_numberOfGoalStates);
				
				if (this.smartAnyTimeMode)
				{
					aStarAlgorithm.useSmartAnyTime(new ReduceFutureStateManipulator(this.futureCostReduceFactor,weightOfFuture,stateCalculations),this.maxNumberOfGeneration);
				}

				aStarAlgorithm.find();

				AStarElement foundElement = aStarAlgorithm.getFoundGoalState();
				this.bestTree = new TreeAndFeatureVector(foundElement.getTree(), foundElement.getFeatureVector());
				this.bestTreeHistory = foundElement.getHistory();
				this.bestTreeSentence = foundElement.getOriginalSentence();

				this.numberOfExpandedElements = aStarAlgorithm.getNumberOfExpandedElements();
				this.numberOfGeneratedElements = aStarAlgorithm.getNumberOfGeneratedElements();

				this.processingDone = true;
			}
		}
		catch (AStarException e)
		{
			throw new TeEngineMlException("See nested",e);
		}
	}
	
	
	
	protected void processPairAnyTimeMode(Set<String> hypothesisLemmasLowerCase, int numberOfHypothesisNodes, Set<AStarElement> startStates) throws TeEngineMlException, AStarException
	{
		if (!this.anyTimeMode) throw new TeEngineMlException("BUG");
		long numberOfExpandedElementsSoFar = 0;
		List<AStarElement> foundElements = new ArrayList<AStarElement>();
		double actualWeightOfFuture = this.weightOfFuture;
		long numberOfGeneratedSoFar = 0;
		long totalNumberOfAllGenerationsBothExpansiveAndNot = 0;
		boolean endedWithEmptyQueue = false;
		boolean firstIteration = true;
		while ( (numberOfGeneratedSoFar<this.maxNumberOfGeneration) && (!endedWithEmptyQueue) && (actualWeightOfFuture>=ANYTIME_MINIMUM_LEGAL_WEIGHT_OF_FUTURE) )
		{
			logger.info("Actual weight-of-future = "+String.format("%-4.4f", actualWeightOfFuture));
			GeneratedTreeStateCalculations stateCalculations =
				new GeneratedTreeStateCalculations(classifier, script,
						operationsEnvironment,
						hypothesisLemmasLowerCase,numberOfHypothesisNodes,
						weightOfCost, actualWeightOfFuture);
			
			stateCalculations.setPreferredMode(this.preferredMode);
			stateCalculations.setBeliefMode(this.beliefMode);
			
			aStarAlgorithm = new AStarAlgorithm<AStarElement>(startStates,stateCalculations,comparatorByCostOnly);
			aStarAlgorithm.setWhenEqualTakeAll(this.whenEqualTakeAll);

			aStarAlgorithm.setK_expandInEachIteration(this.k_expandInEachIteration);
			aStarAlgorithm.setAnyTime_numberOfGoalStates(this.anyTime_numberOfGoalStates);
			if (firstIteration)
			{
				firstIteration = false;
			}
			else
			{
				if (anyTimeModeAccordingToExpensive)
				{
					aStarAlgorithm.setMaxNumberOfExpensiveGenerations(this.maxNumberOfGeneration-numberOfGeneratedSoFar);
				}
				else
				{
					aStarAlgorithm.setMaxNumberOfGenerations(this.maxNumberOfGeneration-numberOfGeneratedSoFar);
				}
			}

			if (logger.isDebugEnabled())logger.debug(String.format("Weight of future = %-5.5f", actualWeightOfFuture) );
			aStarAlgorithm.find();

			if (aStarAlgorithm.isAnyGoalFound())
			{
				foundElements.add(aStarAlgorithm.getFoundGoalState());
			}
			totalNumberOfAllGenerationsBothExpansiveAndNot += aStarAlgorithm.getNumberOfGeneratedElements();

			if (anyTimeModeAccordingToExpensive)
			{
				numberOfGeneratedSoFar += aStarAlgorithm.getNumberOfExpensiveGeneratedElements();
			}
			else
			{
				numberOfGeneratedSoFar += aStarAlgorithm.getNumberOfGeneratedElements();
			}
			numberOfExpandedElementsSoFar += aStarAlgorithm.getNumberOfExpandedElements();
			
			endedWithEmptyQueue = aStarAlgorithm.isEndedWithEmptyQueue();
			
			if (logger.isDebugEnabled())
			{
				logger.debug("Current run:\naStarAlgorithm.getNumberOfGeneratedElements()="+aStarAlgorithm.getNumberOfGeneratedElements()+"\n"+
						"aStarAlgorithm.getNumberOfExpansiveGeneratedElements()="+aStarAlgorithm.getNumberOfExpensiveGeneratedElements()+"\n"+
						"numberOfGeneratedSoFar="+numberOfGeneratedSoFar+"\n"+
						"endedWithEmptyQueue="+endedWithEmptyQueue);
			}
			actualWeightOfFuture *= this.futureCostReduceFactor;
		}
		if (logger.isDebugEnabled()){logger.debug("AnyTime mode ended with weight of future as: "+String.format("%-5.5f",actualWeightOfFuture));}
		Collections.sort(foundElements,comparatorByCostOnly);
		for (AStarElement element : foundElements)
		{if (element.getFutureEstimation()!=0)throw new TeEngineMlException("BUG");}
		if (foundElements.get(0).getCost()>foundElements.get(foundElements.size()-1).getCost())
		{throw new TeEngineMlException("BUG");}


		AStarElement foundElement = foundElements.get(0);
		this.bestTree = new TreeAndFeatureVector(foundElement.getTree(), foundElement.getFeatureVector());
		this.bestTreeHistory = foundElement.getHistory();
		this.bestTreeSentence = foundElement.getOriginalSentence();

		this.numberOfExpandedElements = numberOfExpandedElementsSoFar;
		this.numberOfGeneratedElements = totalNumberOfAllGenerationsBothExpansiveAndNot;

		this.processingDone = true;
	}



	
	
	
	
	
	
	




	private static final ComparatorByCostOnly comparatorByCostOnly =
		new ComparatorByCostOnly();
	

	private AStarAlgorithm<AStarElement> aStarAlgorithm;
	
	
	private double weightOfCost=1.0;
	private double weightOfFuture=1.0;
	private int k_expandInEachIteration = 1;
	private int anyTime_numberOfGoalStates = 1;
	private boolean whenEqualTakeAll = false;
	
	private boolean anyTimeMode = false;
	private boolean anyTimeModeAccordingToExpensive = false;
	private boolean smartAnyTimeMode = false;
	private long maxNumberOfGeneration;
	private double futureCostReduceFactor;
	
	private boolean beliefMode = false;
	private boolean preferredMode = false;
	
	private long numberOfExpandedElements = 0;
	private long numberOfGeneratedElements = 0;

	
	private TreeAndFeatureVector bestTree;
	private String bestTreeSentence;
	private TreeHistory bestTreeHistory;
	
	private boolean processingDone = false;
	
	private static final Logger logger = Logger.getLogger(AStarTextTreesProcessor.class);
}
