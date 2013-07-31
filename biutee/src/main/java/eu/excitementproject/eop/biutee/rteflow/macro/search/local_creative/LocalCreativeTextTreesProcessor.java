package eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative;

import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.TreeHistoryUtilities;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.datastructures.SingleItemSet;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * Implementation of LLGS algorithm
 * 
 * @author Asher Stern
 * @since August 2011
 *
 */
public class LocalCreativeTextTreesProcessor extends AbstractTextTreesProcessor implements WithStatisticsTextTreesProcessor
{
	public static final int WARNING_IF_EXCEEDS_NUMBER_OF_GLOBAL_ITERATIONS = 20;
	
	/**
	 * How many trees of the given text trees to process.
	 * If <=0 it means all.
	 */
	public static final int NUMBER_OF_TREES_TO_PROCESS = BiuteeConstants.LOCAL_CREATIVE_NUMBER_OF_TREES_TO_PROCESS;

	
	
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
	
	
	public void setNumberOfLocalIterations(int numberOfLocalIterations)
	{
		this.numberOfLocalIterations = numberOfLocalIterations;
	}


	public TreeAndFeatureVector getBestTree() throws TeEngineMlException
	{
		if (null==this.bestResult)throw new TeEngineMlException("Not computed");
		return bestResult.getTree();
	}

	public String getBestTreeSentence() throws TeEngineMlException
	{
		if (null==this.bestResult)throw new TeEngineMlException("Not computed");
		return bestResult.getSentence();
	}

	public TreeHistory getBestTreeHistory() throws TeEngineMlException
	{
		if (null==this.bestResult)throw new TeEngineMlException("Not computed");
		return bestResult.getHistory();
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
	

	//////////////// PROTECTED & PRIVATE /////////////////////
	


	/**
	 * Processes the pair, and finds the best proof.
	 * If the text contains several sentences, then each sentence is
	 * processed by {@link #processTree(ExtendedNode, String)}. 
	 */
	@Override
	protected void processPair() throws ClassifierException,
			TreeAndParentMapException, TeEngineMlException, OperationException,
			ScriptException, RuleBaseException
	{
		logger.info("Processing T-H pair...");
		// initialization
		hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(operationsEnvironment.getHypothesis());
		results = new Vector<LocalCreativeSearchResult>();
		
		numberOfExpandedElements = 0;
		numberOfGeneratedElements = 0;
		
		List<TreeAndIndex> treesToProcess = filterTreesByGap(originalTextTrees);
		
		// used for GUI
		progressSingleTree = 1.0/((double)treesToProcess.size());
		
		// computation
		for (TreeAndIndex tree : treesToProcess)
		{
			logger.info("Processing sentence #"+tree.getIndex());
			processTree(tree.getTree(),originalMapTreesToSentences.get(tree.getTree()));
		}
		if (results.size()==0)
			throw new TeEngineMlException("Bug: In LocalCreativeTextTreesProcessor: results.size = 0");
		findBestResult();
		if (logger.isDebugEnabled())
		{
			logger.debug("Best result is:\nSentence = "+bestResult.getSentence()+
					"\nHistory = \n"+
					TreeHistoryUtilities.historyToString(bestResult.getHistory()));
		}
		logger.info("Processing T-H pair done.");
	}
	
	/**
	 * Processes a single sentence of the text.
	 * This method adds at least one result to {@link #results}.
	 * @param tree
	 * @param sentence
	 * @throws TreeAndParentMapException
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws ClassifierException
	 */
	protected void processTree(ExtendedNode tree, final String sentence) throws TreeAndParentMapException, TeEngineMlException, OperationException, ScriptException, RuleBaseException, ClassifierException
	{
		if (!originalMapTreesToSentences.get(tree).equals(sentence)) throw new TeEngineMlException("BUG");
		int debug_resultsSize = results.size();
		
		// In this function, there is a while loop - each iteration in this
		// loop is considered as "global iteration".
		//
		// The "currentX" objects (currentTree, currentTreeAndParentMap, currentHistory, currentFeatureVector)
		// represent the single tree which survives after the end of each global iteration.
		
		ExtendedNode currentTree = tree;
		TreeAndParentMap<ExtendedInfo, ExtendedNode> currentTreeAndParentMap =
			new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
		TreeHistory currentHistory = new TreeHistory(TreeHistoryComponent.onlyFeatureVector(initialFeatureVector()));
		Map<Integer,Double> currentFeatureVector = initialFeatureVector();
		// Handle cases in which the original tree is identical to the hypothesis
		if (0==getHeuristicGap(currentTreeAndParentMap))
			addSingleGoalToResutls(new LocalCreativeTreeElement(currentTree, currentHistory, currentFeatureVector, 0, 0, null, getCost(currentFeatureVector), 0), sentence);
		
		// for GUI
		double initialHeuristicGap = getHeuristicGap(currentTreeAndParentMap);
		
		int[] localHistory = createLocalHistoryArray();
		int actualNumberOfLocalIterations = this.numberOfLocalIterations;
		int currentIteration = 0;
		while (getHeuristicGap(currentTreeAndParentMap)>0)
		{
			// for GUI
			if (this.progressFire!=null)
			{
				double currentProgress = progressSoFar+progressSingleTree*((initialHeuristicGap-getHeuristicGap(currentTreeAndParentMap))/initialHeuristicGap);
				progressFire.fire(currentProgress);
			}
			
			if (logger.isDebugEnabled())
			{
				logger.debug("Iteration: "+currentIteration);
				logger.debug("Current gap: "+String.format("%-4.6f", getHeuristicGap(currentTreeAndParentMap)) );
				logger.debug("Current cost: "+String.format("%-4.6f",getCost(currentFeatureVector)));
				logger.debug("Current history:\n"+TreeHistoryUtilities.historyToString(currentHistory));
				logger.debug("Number of local iterations to be performed: "+actualNumberOfLocalIterations);
				logger.debug("\"localHistory\" = "+((null==localHistory)?"null":Arrays.toString(localHistory)));
			}
			
			++numberOfExpandedElements;

			TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(currentTree, currentFeatureVector);
//			Set<TreeAndFeatureVector> treeAsSet = new HashSet<TreeAndFeatureVector>();
//			treeAsSet.add(treeAndFeatureVector);
			Set<TreeAndFeatureVector> treeAsSet = new SingleItemSet<TreeAndFeatureVector>(treeAndFeatureVector);
			elements = new LinkedHashSet<LocalCreativeTreeElement>();
			ImmutableList<SingleOperationItem> operations = script.getItemListForLocalCreativeIteration(currentIteration, 0, treeAsSet);
			//if (logger.isDebugEnabled()){logger.debug("operations: "+operations.toString());}
			TreesGeneratorByOperations generator =
				new TreesGeneratorByOperations(
						treeAndFeatureVector,
						operations,
						script,
						currentHistory,
						operationsEnvironment
						);
			
			generator.generateTrees();
			numberOfGeneratedElements += generator.getGeneratedTrees().size();
			if (logger.isDebugEnabled())
			{
				logger.debug("First local iteration, number of generated trees = "+generator.getGeneratedTrees().size());
			}
			Map<TreeAndFeatureVector,TreeHistory> historyMap = generator.getHistoryMap();
			Map<ExtendedNode, Set<ExtendedNode>> mapAffectedNodes = generator.getMapAffectedNodes();
			for (TreeAndFeatureVector generatedTree : generator.getGeneratedTrees())
			{
				double cost = getCost(generatedTree.getFeatureVector());
				double gap = getHeuristicGap(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree()));
				LocalCreativeTreeElement generatedElement =
					new LocalCreativeTreeElement(generatedTree.getTree(), historyMap.get(generatedTree), generatedTree.getFeatureVector(), 1, currentIteration, mapAffectedNodes.get(generatedTree.getTree()),cost,gap);
				elements.add(generatedElement);
				processElement(generatedElement,currentIteration,actualNumberOfLocalIterations);
			}
			if (logger.isDebugEnabled())
			{
				logger.debug("End of global iteration, nubmer of generated elements = "+elements.size());
			}
			
			double currentTreeCost = getCost(currentFeatureVector);
			double currentTreeGap = getHeuristicGap(currentTreeAndParentMap);
			LocalCreativeTreeElement bestElement = findBest(elements, currentTreeCost, currentTreeGap);
			
			currentTree = bestElement.getTree();
			currentTreeAndParentMap = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(currentTree);
			currentHistory = bestElement.getHistory();
			currentFeatureVector = bestElement.getFeatureVector();
			
			// looks like a bug. currentIteration += bestElement.getLocalIteration();
			// Fixing (Asher: 27-September-2011)
			currentIteration++;
			
			// warning if works too hard on this pair
			if (0 == (currentIteration%WARNING_IF_EXCEEDS_NUMBER_OF_GLOBAL_ITERATIONS) )
			{
				logger.warn("Too many global iterations, though proof has not been found! Number of global iterations so far is "+currentIteration
						+" The current gap is: "+String.format("%-4.4f", currentTreeGap)+
						" The current cost is: "+String.format("%-4.4f", currentTreeCost)
						);
			}

			
			// How many local iterations will be performed in the next
			// local-lookahead loop? Well, this.numberOfLocalIterations. Right?
			// Well, right, usually. However, for efficiency, the number might
			// be smaller. See description in the JavaDoc of the method
			// updateActualNumberOfLocalIterations()
			updateLocalHistory(localHistory, bestElement);
			actualNumberOfLocalIterations = updateActualNumberOfLocalIterations(currentIteration,localHistory, actualNumberOfLocalIterations);
			
			
			addGoalsToResults(findGoals(elements), sentence);
		}
		progressSoFar += progressSingleTree;
		
		// Some explanation.
		// In theory, the solution (i.e. the best proof) is found at the end of the 
		// "while" loop. However, some goal states might be found in the mean time,
		// and thus - all goals are added to "results".
		// Thus, I don't have to add the final results to "results" outside the
		// "while" loop, since it is added at the end of the loop.
//		results.add(
//				new LocalCreativeSearchResult(
//						new TreeAndFeatureVector(currentTree, currentFeatureVector),
//						originalMapTreesToSentences.get(tree),
//						currentHistory));
		
		if (results.size()<=debug_resultsSize) throw new TeEngineMlException("BUG - Seems that no results were added for the given sentence.");
	}
	
	/**
	 * The goal of this method is to add more elements to {@link #elements}.
	 * Remember that {@link #elements} is deleted in each iteration in the "while"
	 * loop of {@link #processTree(ExtendedNode, String)}.
	 * @param element
	 * @param globalBaseIteration
	 * @param maxLocalIteration
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws TreeAndParentMapException
	 * @throws ClassifierException
	 */
	private void processElement(LocalCreativeTreeElement element, int globalBaseIteration, int maxLocalIteration) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
	{
		if (element.getLocalIteration()<maxLocalIteration)
		{
			++numberOfExpandedElements;
			Set<TreeAndFeatureVector> treeAsSet = new LinkedHashSet<TreeAndFeatureVector>();
			TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(element.getTree(), element.getFeatureVector());
			treeAsSet.add(treeAndFeatureVector);

			ImmutableList<SingleOperationItem> operations = script.getItemListForLocalCreativeIteration(globalBaseIteration, element.getLocalIteration(), treeAsSet);
			//if (logger.isDebugEnabled()){logger.debug("operations: "+operations.toString());}
			TreesGeneratorByOperations generator =
				new TreesGeneratorByOperations(
						treeAndFeatureVector,
						operations,
						script,
						element.getHistory(),
						operationsEnvironment
						);
			
			generator.setAffectedNodes(element.getAffectedNodes()); // this is the most important line here.
			generator.generateTrees();
			numberOfGeneratedElements+=generator.getGeneratedTrees().size();
			Map<TreeAndFeatureVector,TreeHistory> historyMap = generator.getHistoryMap();
			Map<ExtendedNode, Set<ExtendedNode>> mapAffectedNodes = generator.getMapAffectedNodes();
			for (TreeAndFeatureVector generatedTree : generator.getGeneratedTrees())
			{
				double cost = getCost(generatedTree.getFeatureVector());
				double gap = getHeuristicGap(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree()));

				LocalCreativeTreeElement generatedElement = 
					new LocalCreativeTreeElement(generatedTree.getTree(), historyMap.get(generatedTree), generatedTree.getFeatureVector(), element.getLocalIteration()+1, globalBaseIteration, mapAffectedNodes.get(generatedTree.getTree()),cost,gap);

				elements.add(generatedElement);
				
				processElement(generatedElement,globalBaseIteration,maxLocalIteration);
			}
		}

	}
	
//	// TO_DO REMOVE THIS METHOD - IT IS ONLY TEMPORARY FOR EXPERIMENT
//	protected LocalCreativeTreeElement findBest(Set<LocalCreativeTreeElement> elements, double originalCost, double originalGap) throws TeEngineMlException
//	{
//		if (elements.size()==0)throw new TeEngineMlException("BUG");
//		double w = 1.0;
//		Double bestF = null; // null is interpreted as infinity
//		LocalCreativeTreeElement bestElement = null;
//		for (LocalCreativeTreeElement element : elements)
//		{
//			double f = element.getCost()+w*element.getGap();
//			if (null==bestF) // current best is infinity, so everything is better that the current best.
//			{
//				bestF=f;
//				bestElement = element;
//			}
//			else
//			{
//				if (f<bestF)
//				{
//					bestF=f;
//					bestElement=element;
//				}
//			}
//		}
//		
//		return bestElement;
//	}
	
	protected LocalCreativeTreeElement findBest(Set<LocalCreativeTreeElement> elements, double originalCost, double originalGap) throws TeEngineMlException
	{
		if (elements.size()==0)throw new TeEngineMlException("BUG");
		Double bestProportion = null; // null is interpreted as infinity
		LocalCreativeTreeElement bestElement = null;
		for (LocalCreativeTreeElement element : elements)
		{
			Double proportion = null;
			if ((originalGap-element.getGap())>0)
				proportion = (element.getCost()-originalCost)/(originalGap-element.getGap());
			if (null==bestProportion) // current best is infinity, so everything is better that the current best.
			{
				bestProportion=proportion;
				bestElement = element;
			}
			else
			{
				if (proportion!=null)
				{
					if (proportion<bestProportion)
					{
						bestProportion=proportion;
						bestElement=element;
					}
				}
			}
		}
		
		return bestElement;
	}

	
	private Set<LocalCreativeTreeElement> findGoals(Collection<LocalCreativeTreeElement> givenElements)
	{
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
	
	private void addGoalsToResults(Set<LocalCreativeTreeElement> goalElements, String origianlSentence) throws TeEngineMlException
	{
		for (LocalCreativeTreeElement element : goalElements)
		{
			addSingleGoalToResutls(element,origianlSentence);
		}
	}
	
	private void addSingleGoalToResutls(LocalCreativeTreeElement element, String origianlSentence) throws TeEngineMlException
	{
		this.results.add(
				new LocalCreativeSearchResult(
						new TreeAndFeatureVector(element.getTree(), element.getFeatureVector()),
						origianlSentence,
						element.getHistory()
						)
				);
	}
	

	
	protected void findBestResult() throws TeEngineMlException, ClassifierException
	{
		if (results.size()==0)throw new TeEngineMlException("BUG");
		this.bestResult = results.iterator().next();
		double lowestCost = getCost(bestResult.getTree().getFeatureVector());
		for (LocalCreativeSearchResult result : results)
		{
			double cost = getCost(result.getTree().getFeatureVector());
			if (cost<lowestCost)
			{
				lowestCost=cost;
				bestResult = result;
			}
		}
	}
	
	protected double getCost(Map<Integer,Double> featureVector) throws ClassifierException
	{
		return -this.classifier.getProduct(featureVector);
	}
	
	/**
	 * Returns a number that describes the gap between the given tree and the
	 * hypothesis tree.<BR>
	 * This number is based on the number of missing words, missing nodes
	 * and missing relations (where "relation" is parent-child-label).
	 * For more information see {@link SingleTreeEvaluations}.
	 * @param tree
	 * @return
	 */
	protected double getHeuristicGap(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree)
	{
		// SingleTreeEvaluations evaluations = SingleTreeEvaluations.create(tree,operationsEnvironment.getHypothesis(),hypothesisLemmasLowerCase,hypothesisNumberOfNodes);
		SingleTreeEvaluations evaluations = new AlignmentCalculator(this.teSystemEnvironment.getAlignmentCriteria(),tree,operationsEnvironment.getHypothesis()).getEvaluations(hypothesisLemmasLowerCase,hypothesisNumberOfNodes);
		return (double)(evaluations.getMissingLemmas()+evaluations.getMissingNodes()+evaluations.getMissingRelations());
	}
	
	
	/**
	 * Returns a list of {@link TreeAndIndex} which contains the given trees, along
	 * with running indexes (0,1,2,...).
	 * <BR>
	 * Also, filters out some trees, if the constant NUMBER_OF_TREES_TO_PROCESS is a
	 * positive number. If so, the returned list contains only some of the given
	 * trees, where trees with high gap between them and the hypothesis are filtered
	 * out.
	 * 
	 * @param originalTrees
	 * @return
	 * @throws TreeAndParentMapException
	 * @throws TeEngineMlException
	 */
	@SuppressWarnings("unused")
	protected List<TreeAndIndex> filterTreesByGap(List<ExtendedNode> originalTrees) throws TreeAndParentMapException, TeEngineMlException
	{
		List<TreeAndIndex> ret = null;
		if (NUMBER_OF_TREES_TO_PROCESS<=0)
		{
			ret = new ArrayList<TreeAndIndex>(originalTrees.size());
			int treeIndex=0;
			for (ExtendedNode tree : originalTrees)
			{
				ret.add(new TreeAndIndex(tree, treeIndex));
				++treeIndex;
			}
		}
		else
		{
			Map<TreeAndIndex, Double> mapTreesToGap = new LinkedHashMap<TreeAndIndex, Double>();
			int treeIndex=0;
			for (ExtendedNode tree : originalTrees)
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree);
				mapTreesToGap.put(new TreeAndIndex(tree, treeIndex), getHeuristicGap(treeAndParentMap));
				++treeIndex;
			}
			List<TreeAndIndex> sortedByGapTrees = Utils.getSortedByValue(mapTreesToGap);
			ret = new ArrayList<TreeAndIndex>(NUMBER_OF_TREES_TO_PROCESS);
			// add at least NUMBER_OF_TREES_TO_PROCESS trees. If the one-after-last tree has the same gap as last, add it too.
			boolean stop = false;
			TreeAndIndex previousTree = null;
			treeIndex=0;
			Iterator<TreeAndIndex> treesIterator = sortedByGapTrees.iterator();
			while ( (treesIterator.hasNext()) && (!stop) )
			{
				TreeAndIndex currentTree = treesIterator.next();
				if (treeIndex<NUMBER_OF_TREES_TO_PROCESS)
				{
					stop=false;
				}
				else
				{
					stop = true;
					if (previousTree!=null)
					{
						if (mapTreesToGap.get(previousTree).doubleValue()<mapTreesToGap.get(currentTree).doubleValue())
							stop = true;
						else if (mapTreesToGap.get(previousTree).doubleValue()==mapTreesToGap.get(currentTree).doubleValue())
							stop = false;
						else throw new TeEngineMlException("BUG");
					}
					else throw new TeEngineMlException("BUG");
				}
				if (!stop)
					ret.add(currentTree);
				previousTree=currentTree;
				treeIndex++;
			}
		}
		if ( (ret.size()<NUMBER_OF_TREES_TO_PROCESS) && (ret.size()<originalTrees.size()) )throw new TeEngineMlException("BUG");
		return ret;
	}
	
	private static final class TreeAndIndex
	{
		public TreeAndIndex(ExtendedNode tree, int index)
		{
			this.tree = tree;
			this.index = index;
		}
		public ExtendedNode getTree()
		{
			return tree;
		}
		public int getIndex()
		{
			return index;
		}

		private final ExtendedNode tree;
		private final int index;
	}
	
	
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

	protected Set<String> hypothesisLemmasLowerCase; //= TreeUtilities.constructSetLemmasLowerCase(hypothesis);
	
	private Set<LocalCreativeTreeElement> elements;
	

	protected Vector<LocalCreativeSearchResult> results;
	protected LocalCreativeSearchResult bestResult = null;
	
	protected long numberOfExpandedElements = 0;
	protected long numberOfGeneratedElements = 0;
	
	// For example: 3 means son, grandson, great-grandson
	protected int numberOfLocalIterations = BiuteeConstants.LOCAL_CREATIVE_NUMBER_OF_LOCAL_ITERATIONS;

	// used for GUI
	protected double progressSoFar = 0.0;
	protected double progressSingleTree = 1.0;

	
	private static final Logger logger = Logger.getLogger(LocalCreativeTextTreesProcessor.class);
}
