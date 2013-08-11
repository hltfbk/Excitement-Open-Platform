package eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessingResult;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Contain methods used by {@link LLGSTextTreesProcessor}.<BR>
 * Local-Creative is the former name of LLGS.
 * 
 * @author Asher Stern
 * @since August 2011
 *
 */
public class LocalCreativeTextTreesProcessor extends LLGSTextTreesProcessor
{
	public LocalCreativeTextTreesProcessor(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees, ExtendedNode hypothesisTree,
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
	
	//////////////// PROTECTED & PRIVATE /////////////////////
	

	protected void prepareGlobalLoop(TreeInProcess currentInProcess) throws TeEngineMlException, TreeAndParentMapException
	{
		this.localIterationsArray = createLocalHistoryArray();
		// For GUI
		initialHeuristicGap = getHeuristicGap(currentInProcess);
	}
	
	
	protected int calculateNumberOfLocalIterations(int globalIteraion, int actualNumberOfLocalIterations) throws TeEngineMlException
	{
		if (0==globalIteraion) return this.numberOfLocalIterations;
		return updateActualNumberOfLocalIterations(globalIteraion, localIterationsArray, actualNumberOfLocalIterations);
	}
	
	
	

	@Override
	protected void postGlobalLoop(LocalCreativeTreeElement bestElement) throws TeEngineMlException, TreeAndParentMapException
	{
		if (bestElement!=null)
		{
			updateLocalHistory(this.localIterationsArray, bestElement);

			// For GUI
			if (this.progressFire!=null)
			{
				double currentProgress = progressSoFar+progressSingleTree*((initialHeuristicGap-getHeuristicGap(new TreeAndParentMap<ExtendedInfo,ExtendedNode>(bestElement.getTree()),bestElement.getFeatureVector()))/initialHeuristicGap);
				progressFire.fire(currentProgress);
			}
		}
	}
	
	
	protected boolean continueGlobalIteration(TreeInProcess currentInProcess, TreeInProcess previousIterationTree) throws TeEngineMlException, TreeAndParentMapException
	{
		if (hybridGapMode)
		{
			if (null==currentInProcess) // It's OK in hybrid mode. It means that in the previous global iteration no element was generated.
			{
				return false;
			}
			else if (null==previousIterationTree)
			{
				return true;
			}
			else
			{
				double previousTreeGap = getHeuristicGap(previousIterationTree);
				LocalCreativeTreeElement elementWithSmallestGap = pickElementWithSmallestGapMeasure(elements);
				boolean gapMode_GapIsDecreasing = true;
				if (elementWithSmallestGap.getGap()<previousTreeGap)
				{
					gapMode_GapIsDecreasing=true;
				}
				else
				{
					gapMode_GapIsDecreasing=false;
				}
				return gapMode_GapIsDecreasing;
			}
		}
		else
		{
			if (null==currentInProcess) throw new TeEngineMlException("Internal bug. No element to process at the beginning of a global iteration. It is a bug in pure-transformation mode.");
			double currentTreeGap = getHeuristicGap(currentInProcess);
			return (currentTreeGap>0.0);
		}
	}

	
	@Override
	protected void conditionallyInsertInitialElementToResults(TreeInProcess currentInProcess) throws TeEngineMlException, ClassifierException, TreeAndParentMapException
	{
		double gap = getHeuristicGap(currentInProcess);
		if (hybridGapMode || (0==gap))
		{
			addSingleGoalToResutls(
					new LocalCreativeTreeElement(currentInProcess.getTree().getTree(), currentInProcess.getHistory(), currentInProcess.getTree().getFeatureVector(), 0, 0, null,
							getCost(currentInProcess.getTree().getFeatureVector()),gap),
							currentInProcess.getSentence());			
		}
	}


	@Override
	protected void addSingleGoalToResutls(LocalCreativeTreeElement element, String origianlSentence) throws TeEngineMlException, TreeAndParentMapException
	{
		Map<Integer, Double> featureVector = null;
		if (hybridGapMode)
		{
			featureVector = getFeatureVectorOfTransformationsPlusGap(element);
		}
		else
		{
			featureVector=element.getFeatureVector();
		}
		this.results.add(
				new TextTreesProcessingResult(
						new TreeAndFeatureVector(element.getTree(), featureVector),
						origianlSentence,
						element.getHistory()
						)
				);
	}
	

	@Override
	protected void addResultsFromElements(final String sentence) throws TeEngineMlException, ClassifierException, TreeAndParentMapException
	{
		// Add every element that can be the final result - to the field "results".
		if (hybridGapMode)
		{
			LocalCreativeTreeElement bestResultElement = calculateBestResultElement(elements);
			if (bestResultElement!=null)
			{
				addSingleGoalToResutls(bestResultElement,sentence);
			}
		}
		else
		{
			Set<LocalCreativeTreeElement> goals = findGoals(elements);
			for (LocalCreativeTreeElement goal : goals)
			{
				addSingleGoalToResutls(goal,sentence);
			}
		}
	}
	
	private double getHeuristicGap(TreeInProcess treeInProcess) throws GapException, TreeAndParentMapException
	{
		return getHeuristicGap(new TreeAndParentMap<ExtendedInfo,ExtendedNode>(treeInProcess.getTree().getTree(),treeInProcess.getParentMap()),treeInProcess.getTree().getFeatureVector());
	}

	/**
	 * Used when it is NOT hybrid-mode
	 * @param givenElements
	 * @return
	 * @throws TeEngineMlException 
	 */
	private Set<LocalCreativeTreeElement> findGoals(Collection<LocalCreativeTreeElement> givenElements) throws TeEngineMlException
	{
		if (hybridGapMode) throw new TeEngineMlException("Internal bug in "+LocalCreativeTextTreesProcessor.class.getSimpleName()+" The method findGoals() must not be called in hybrid gap mode.");
		Set<LocalCreativeTreeElement> ret = new LinkedHashSet<LocalCreativeTreeElement>();
		for (LocalCreativeTreeElement element : givenElements)
		{
			if (0==element.getGap())
			{
				ret.add(element);
			}
		}
		return ret;
	}
	

	

	

	
	
	/**
	 * Returns a number that describes the gap between the given tree and the
	 * hypothesis tree.<BR>
	 * This number is based on the number of missing words, missing nodes
	 * and missing relations (where "relation" is parent-child-label).
	 * For more information see {@link SingleTreeEvaluations}.
	 * @param tree
	 * @return
	 * @throws GapException 
	 */
	@Override
	protected double getHeuristicGap(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree, Map<Integer, Double> featureVector) throws GapException
	{
		if (hybridGapMode)
		{
			return gapTools.getGapHeuristicMeasure().measure(tree,featureVector,gapEnvironment);
		}
		else
		{
			// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(tree,operationsEnvironment.getHypothesis(),hypothesisLemmasLowerCase,hypothesisNumberOfNodes);
			SingleTreeEvaluations evaluations = new AlignmentCalculator(this.teSystemEnvironment.getAlignmentCriteria(),tree,operationsEnvironment.getHypothesis()).getEvaluations(hypothesisLemmasLowerCase,hypothesisNumberOfNodes);
			return (double)(evaluations.getMissingLemmas()+evaluations.getMissingNodes()+evaluations.getMissingRelations());
		}
	}
	
	

	

	



	
	/**
	 * At the end of each global iteration, the one element with the highest confidence is added to
	 * results.
	 * Used only in gapMode
	 * @param setOfElements
	 * @return
	 * @throws TreeAndParentMapException 
	 * @throws ClassifierException 
	 * @throws TeEngineMlException 
	 */
	private LocalCreativeTreeElement calculateBestResultElement(Set<LocalCreativeTreeElement> setOfElements) throws ClassifierException, TreeAndParentMapException, TeEngineMlException
	{
		if (!hybridGapMode) throw new TeEngineMlException("Internal bug in "+LocalCreativeTextTreesProcessor.class.getSimpleName()+" The method calculateBestResultElement() uses calculation of transformations+gap to calculate confidence. Thus it must not be called in not-hybrid-gap mode.");
		LocalCreativeTreeElement best = null;
		double highestConfidence = 0.0;
		 
		for (LocalCreativeTreeElement element : setOfElements)
		{
			double currentConfidence = this.classifier.classify(getFeatureVectorOfTransformationsPlusGap(element));
			if (null==best)
			{
				best = element;
				highestConfidence = currentConfidence;
			}
			else
			{
				if (highestConfidence<currentConfidence)
				{
					best = element;
					highestConfidence = currentConfidence;
				}
			}
		}
		return best;
	}
	
	private Map<Integer, Double> getFeatureVectorOfTransformationsPlusGap(LocalCreativeTreeElement element) throws TreeAndParentMapException, TeEngineMlException
	{
		return getFeatureVectorOfTransformationsPlusGap(element.getTree(),element.getFeatureVector());
	}
	
	private Map<Integer, Double> getFeatureVectorOfTransformationsPlusGap(ExtendedNode tree, Map<Integer,Double> featureVector) throws TreeAndParentMapException, TeEngineMlException
	{
		if (!hybridGapMode) throw new TeEngineMlException("Internal bug! This method should be called only in hybrid-gap mode.");
		TreeAndParentMap<ExtendedInfo, ExtendedNode> tapm = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
		return gapTools.getGapFeaturesUpdate().updateForGap(tapm, featureVector,gapEnvironment);
	}

	
	private LocalCreativeTreeElement pickElementWithSmallestGapMeasure(Set<LocalCreativeTreeElement> setOfElements)
	{
		LocalCreativeTreeElement smallestGapElement = null;
		double smallestGap = 0.0;
		for (LocalCreativeTreeElement element : setOfElements)
		{
			double currentGap = element.getGap();
			if (null==smallestGapElement)
			{
				smallestGapElement=element;
				smallestGap=currentGap;
			}
			else
			{
				if (currentGap<smallestGap)
				{
					smallestGapElement=element;
					smallestGap=currentGap;
				}
			}
		}
		return smallestGapElement;
	}
	
	


	
	
	
	
	
	
	/**
	 * This method calculates how many "local iterations" will be performed in the
	 * next local-lookahead (i.e., in the next "global iteration").<BR>
	 * The number of local iterations is usually {@link #numberOfLocalIterations}.
	 * However, for efficiency, the number might be smaller, as follows.
	 * <P>
	 * For the first {@link BiuteeConstants#LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY}
	 * global iterations - the number will be {@link #numberOfLocalIterations}.
	 * After {@link BiuteeConstants#LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY}
	 * global-iterations were passed, the following heuristic is utilized:<BR>
	 * The local iteration number in which the best tree has been found is
	 * stored in an array, for the last {@link BiuteeConstants#LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY}
	 * global iterations.
	 * The minimum value stored in this array plus one is then returned, as
	 * the number of local iterations to be performed.
	 * 
	 *  
	 * @param currentIteration the iteration number of the current global iteration
	 * @param localHistory an array which stores the local-iteration numbers
	 * where the best tree has been found in the recent global-iterations. 
	 * @param currentActualNumberOfLocalIterations the number of local iterations
	 * that were performed in the last global iteration.
	 * 
	 * @return the number of local iterations to be performed in the next global
	 * iteration.
	 */
	@SuppressWarnings("unused")
	private final int updateActualNumberOfLocalIterations(int currentIteration, int[] localHistory, int currentActualNumberOfLocalIterations)
	{
		if (hybridGapMode) return this.numberOfLocalIterations;
		
		if (LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY<=0)
		{
			return currentActualNumberOfLocalIterations;
		}
		else
		{
			if (currentIteration<LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY)
			{
				return currentActualNumberOfLocalIterations;
			}
			else
			{
				return Math.min(arrayMax(localHistory)+1, currentActualNumberOfLocalIterations);
			}
		}
	}
	
	
	/**
	 * See {@link #updateActualNumberOfLocalIterations(int, int[], int)}
	 * @param localHistory
	 * @param bestElement
	 */
	@SuppressWarnings("unused")
	private final void updateLocalHistory(int[] localHistory, LocalCreativeTreeElement bestElement)
	{
		if (LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY>0)
		{
			roleToRightAndAddToLeft(localHistory, bestElement.getLocalIteration());
		}
		else{}
	}
	
	/**
	 * See {@link #updateActualNumberOfLocalIterations(int, int[], int)}
	 * 
	 * @return an array that stores the local iteration number in which the best
	 * tree has been found in the recent global iterations.
	 */
	@SuppressWarnings("unused")
	private static final int[] createLocalHistoryArray()
	{
		if (LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY>0)
		{
			int[] ret = new int[LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY];
			for (int index=0;index<ret.length;++index)
			{
				ret[index]=0;
			}
			return ret;
		}
		else
			return null;
	}

	
	
	/**
	 * Array like [a,b,c,d] becomes [n,a,b,c], where n is the <code>newValue</code>.
	 * @param array
	 * @param newValue
	 */
	private static final void roleToRightAndAddToLeft(int[] array, int newValue)
	{
		
		for (int index=(array.length-1);index>0;--index)
		{
			array[index]=array[index-1];
		}
		array[0] = newValue;
	}

	private static final int arrayMax(final int[] array)
	{
		int ret = array[0];
		for (int index=0;index<array.length;++index)
		{
			if (ret < array[index])
				ret = array[index];
		}
		return ret;
	}
	
	


	private int[] localIterationsArray; // updated at the end of each global iteration.
	private double initialHeuristicGap=0.0;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(LocalCreativeTextTreesProcessor.class);
}
