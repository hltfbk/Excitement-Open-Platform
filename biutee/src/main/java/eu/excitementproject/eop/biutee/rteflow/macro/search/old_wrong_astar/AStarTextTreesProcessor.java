package eu.excitementproject.eop.biutee.rteflow.macro.search.old_wrong_astar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;


/**
 * 
 * This class gets a text as list of parse-trees, and the hypothesis as a parse tree,
 * and finds a proof of the hypothesis from the text, by applying generation-operations.
 * <BR>
 * This class uses A* search algorithm to decide which tree to expand, i.e.
 * which tree, from the set of original (given) trees and generated trees, to
 * operate on. 
 * 
 * @author Asher Stern
 * @since Apr 11, 2011
 *
 */
@Deprecated
public class AStarTextTreesProcessor extends AbstractTextTreesProcessor implements TextTreesProcessor
{
	///////////////////// PUBLIC //////////////////////////////////
	
	public AStarTextTreesProcessor(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,
			ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier,
			Lemmatizer lemmatizer, OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment
			) throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees,hypothesisTree,originalMapTreesToSentences,coreferenceInformation,classifier,lemmatizer,script,teSystemEnvironment);
	}


	public TreeAndFeatureVector getBestTree() throws TeEngineMlException
	{
		if (null==found)throw new TeEngineMlException("Not found. Probably you did not call process()");
		return new TreeAndFeatureVector(found.getTree(), found.getFeatureVector());
	}

	public String getBestTreeSentence() throws TeEngineMlException
	{
		if (null==found)throw new TeEngineMlException("Not found. Probably you did not call process()");
		return found.getOriginalSentence();
	}

	public TreeHistory getBestTreeHistory() throws TeEngineMlException
	{
		if (null==found)throw new TeEngineMlException("Not found. Probably you did not call process()");
		return found.getHistory();
	}
	
	////////////////////////// PROTECTED & PRIVATE //////////////////////////////
	
	@Override
	protected void processPair() throws ClassifierException, TreeAndParentMapException, TeEngineMlException, OperationException, ScriptException, RuleBaseException
	{
		logger.info("A* processing...");
		initializeQueue();
		examinedElements = new LinkedHashSet<AStarTreeElement>();
		this.found=null;
		while (this.found==null)
		{
			expandTopElement();
		}
		logger.info("A proof has been found. size of queue = "+queue.size());
		if (logger.isDebugEnabled())
		{
			logger.debug("History:");
			for (Specification spec : this.found.getHistory().getSpecifications())
			{
				logger.debug(spec.toString());
			}
		}
	}
	
	private void initializeQueue() throws ClassifierException, TreeAndParentMapException, TeEngineMlException
	{
		queue = new PriorityQueue<AStarTreeElement>();
		for (ExtendedNode tree : originalTextTrees)
		{
			Map<Integer,Double> featureVector = initialFeatureVector();
			String sentence = originalMapTreesToSentences.get(tree);
			if (sentence==null)throw new TeEngineMlException("Could not find a sentence to a given original tree");
			
			AStarTreeElement element = AStarUtilities.createElement(new TreeAndFeatureVector(tree, featureVector), new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree), this.operationsEnvironment.getHypothesis(), this.classifier, new TreeHistory(TreeHistoryComponent.onlyFeatureVector(initialFeatureVector())), sentence, 0, null, 0);
			queue.offer(element);
		}
	}
	
	
	private boolean alreadyExamined(AStarTreeElement element) throws TreeAndParentMapException, ClassifierException
	{
		boolean ret = false;
		TreeAndParentMap<ExtendedInfo, ExtendedNode> elementTreeAndParentMap = 
			new TreeAndParentMap<ExtendedInfo, ExtendedNode>(element.getTree(),element.getParentMap());
		
		AStarTreeElement olderElement = element.getGeneratedFrom();
		while ((false==ret)&&(olderElement!=null))
		{
			TreeAndParentMap<ExtendedInfo, ExtendedNode> olderElementTreeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(olderElement.getTree(),olderElement.getParentMap());
			
			if (TreeUtilities.areEqualTrees(elementTreeAndParentMap, olderElementTreeAndParentMap))
			{
				double classificationScoreOfElement = classifier.classify(element.getFeatureVector());
				double classificationScoreOfOlderElement = classifier.classify(olderElement.getFeatureVector());
				if (classificationScoreOfOlderElement>=classificationScoreOfElement) // the old is no worse than the new
				{
					ret = true;
				}
			}

			olderElement = olderElement.getGeneratedFrom();
		}
		

		return ret;
	}
	
	
	protected void dfs(int dfsLevel, AStarTreeElement element, AStarTreeElement startingElementInRecursion) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
	{
		examinedElements.add(element);
		TreesGeneratorByOperations generator = new TreesGeneratorByOperations(
				new TreeAndFeatureVector(element.getTree(), element.getFeatureVector()),
				script.getItemListForIteration(element.getDistance(), null),
				script,
				element.getHistory(),this.operationsEnvironment);
		
		generator.generateTrees();
		Set<TreeAndFeatureVector> generatedTrees = generator.getGeneratedTrees();
		Map<TreeAndFeatureVector,TreeHistory> generatedTreesHistory = generator.getHistoryMap();

		if (dfsLevel>0)
		{
			for (TreeAndFeatureVector generatedTree : generatedTrees)
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> generatedTreeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
				double aStarEstimation = AStarUtilities.aStarEstimation(generatedTree, generatedTreeAndParentMap, this.operationsEnvironment.getHypothesis(), this.classifier, this.futureEstimationPerNode);
				AStarTreeElement generatedElement =
					new AStarTreeElement(generatedTree.getTree(), generatedTreeAndParentMap.getParentMap(), generatedTreesHistory.get(generatedTree), generatedTree.getFeatureVector(), element.getOriginalSentence(), element.getDistance()+1, aStarEstimation, element);

				if (!alreadyExamined(generatedElement))
					dfs(dfsLevel-1,generatedElement,startingElementInRecursion);
			}
		}
		else
		{
			generatorsOfLastDfsIterations.add(generator);
			mapGeneratorToPreviousElement.put(generator, element);
			
//			TreeAndParentMap<ExtendedInfo, ExtendedNode> startingTreeAndParentMap =
//				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(startingElementInRecursion.getTree(),startingElementInRecursion.getParentMap());
//			
//			int missingRelationsInStarting = TreeUtilities.findRelationsNoMatch(startingTreeAndParentMap, hypothesis).size();
//			double starintElementWeight = 0-ClassifierUtils.inverseSigmoid(this.classifier.classify(startingElementInRecursion.getFeatureVector()));
//			
//			Map<TreeAndFeatureVector,TreeAndParentMap<ExtendedInfo, ExtendedNode>> mapToTreeAndParentMap =
//				new LinkedHashMap<TreeAndFeatureVector, TreeAndParentMap<ExtendedInfo,ExtendedNode>>();
//			Vector<Double> estimations = new Vector<Double>();
//			for (TreeAndFeatureVector generatedTree : generatedTrees)
//			{
//				TreeAndParentMap<ExtendedInfo, ExtendedNode> generatedTreeAndParentMap =
//					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
//				mapToTreeAndParentMap.put(generatedTree, generatedTreeAndParentMap);
//				int missingRelationsInGenerated = TreeUtilities.findRelationsNoMatch(generatedTreeAndParentMap, this.hypothesis).size();
//				double generatedWeight = 0-ClassifierUtils.inverseSigmoid(this.classifier.classify(generatedTree.getFeatureVector()));
//				
//				if (generatedWeight<starintElementWeight)throw new TeEngineMlException("BUG");
//
//				if (missingRelationsInStarting>missingRelationsInGenerated)
//				{
//					estimations.add(
//							(generatedWeight-starintElementWeight)/((double)(missingRelationsInStarting-missingRelationsInGenerated))
//					);
//				}
//			}
//			double sumEstimations = 0;
//			for (double estimation : estimations){sumEstimations+=estimation;}
//			double averageEstimation = sumEstimations/estimations.size();
//			this.futureEstimationPerNode =
//				Constants.LEARNING_RATE_ASTAR_FUTURE_ESTIMATION*averageEstimation+
//				(1-Constants.LEARNING_RATE_ASTAR_FUTURE_ESTIMATION)*this.futureEstimationPerNode;
//			
//
//			for (TreeAndFeatureVector generatedTree : generatedTrees)
//			{
//				TreeAndParentMap<ExtendedInfo, ExtendedNode> generatedTreeAndParentMap =
//					mapToTreeAndParentMap.get(generatedTree);
//				double estimation = AStarUtilities.aStarEstimation(generatedTree, generatedTreeAndParentMap, hypothesis, classifier, this.futureEstimationPerNode);
//				AStarTreeElement generatedElement =
//					new AStarTreeElement(generatedTree.getTree(), generatedTreeAndParentMap.getParentMap(), generatedTreesHistory.get(generatedTree), generatedTree.getFeatureVector(), element.getOriginalSentence(), element.getDistance()+1, estimation, element);
//				
//				if (!alreadyExamined(generatedElement))
//				{
//					queue.offer(generatedElement);
//				}
//			}
		}
	}
	
	
	protected void offerAllLastDfsIterations(AStarTreeElement startingElementInRecursion) throws TreeAndParentMapException, ClassifierException, TeEngineMlException
	{
		TreeAndParentMap<ExtendedInfo, ExtendedNode> startingTreeAndParentMap =
			new TreeAndParentMap<ExtendedInfo, ExtendedNode>(startingElementInRecursion.getTree(),startingElementInRecursion.getParentMap());
		
		int missingRelationsInStarting = TreeUtilities.findRelationsNoMatch(startingTreeAndParentMap, operationsEnvironment.getHypothesis()).size();
		double staringElementWeight = 0-ClassifierUtils.inverseSigmoid(this.classifier.classify(startingElementInRecursion.getFeatureVector()));
		
		Map<TreeAndFeatureVector,TreeAndParentMap<ExtendedInfo, ExtendedNode>> mapToTreeAndParentMap =
			new LinkedHashMap<TreeAndFeatureVector, TreeAndParentMap<ExtendedInfo,ExtendedNode>>();
		Vector<Double> estimations = new Vector<Double>();
		for (TreesGeneratorByOperations generator : generatorsOfLastDfsIterations)
		{
			for (TreeAndFeatureVector generatedTree : generator.getGeneratedTrees())
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
				mapToTreeAndParentMap.put(generatedTree, treeAndParentMap);
				
				int missingRelations = TreeUtilities.findRelationsNoMatch(treeAndParentMap, operationsEnvironment.getHypothesis()).size();
				double weight = 0-ClassifierUtils.inverseSigmoid(this.classifier.classify(generatedTree.getFeatureVector()));
				
				if (missingRelations<missingRelationsInStarting)
				{
					double estimationThisTree = (weight-staringElementWeight)/(((double)missingRelationsInStarting)-((double)missingRelations));
					estimations.add(estimationThisTree);
				}
			}
		}
		double sumEstimations = 0;
		for (Double estimation : estimations){sumEstimations+=estimation;}
		double averageEstimation = sumEstimations/((double)estimations.size());
		
		this.futureEstimationPerNode =
				BiuteeConstants.LEARNING_RATE_ASTAR_FUTURE_ESTIMATION*averageEstimation+
			(1-BiuteeConstants.LEARNING_RATE_ASTAR_FUTURE_ESTIMATION)*this.futureEstimationPerNode;
		
		for (TreesGeneratorByOperations generator : generatorsOfLastDfsIterations)
		{
			AStarTreeElement previousElement = mapGeneratorToPreviousElement.get(generator);
			for (TreeAndFeatureVector generatedTree : generator.getGeneratedTrees())
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
					mapToTreeAndParentMap.get(generatedTree);
				
				
				double estimation = AStarUtilities.aStarEstimation(generatedTree, treeAndParentMap, operationsEnvironment.getHypothesis(), classifier, this.futureEstimationPerNode);
				
				AStarTreeElement newElement = new AStarTreeElement(generatedTree.getTree(), mapToTreeAndParentMap.get(generatedTree).getParentMap(), generator.getHistoryMap().get(generatedTree), generatedTree.getFeatureVector(), startingElementInRecursion.getOriginalSentence(), previousElement.getDistance()+1, estimation, previousElement);
				
				if (!alreadyExamined(newElement))
				{
					queue.offer(newElement);
				}
			}
		}
	}
	
	
	
	/**
	 * According to A* algorithm, in each iteration the top element, i.e.
	 * the element with the lowest value for "distance+cost" function, is chosen
	 * and expanded.
	 * The expansion, in our case, is applying all operations, and creating new
	 * elements, each is a result of an operation.
	 * 
	 * 
	 * @throws TeEngineMlException
	 * @throws TreeAndParentMapException
	 * @throws OperationException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws ClassifierException
	 */
	private void expandTopElement() throws TeEngineMlException, TreeAndParentMapException, OperationException, ScriptException, RuleBaseException, ClassifierException
	{
		AStarTreeElement element = queue.poll();
		if (null==element)throw new TeEngineMlException("empty queue");
		if (logger.isDebugEnabled())
		{
			logger.debug("Expanding top element. Queue size = "+queue.size());
			logger.debug(String.format("Current future estimation per node = %-3.5f", this.futureEstimationPerNode));
			logger.debug("Memory used: "+Utils.stringMemoryUsedInMB());
			logger.debug("Element distance = "+element.getDistance());
		}
		
		

		TreeAndParentMap<ExtendedInfo, ExtendedNode> tree = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(element.getTree());
		
		if (isMatch(tree))
		{
			this.found = element;
		}
		else
		{
			generatorsOfLastDfsIterations = new Vector<TreesGeneratorByOperations>();
			mapGeneratorToPreviousElement = new LinkedHashMap<TreesGeneratorByOperations, AStarTreeElement>();
			dfs(BiuteeConstants.ASTAR_DFS_ITERATIONS,element,element);
			offerAllLastDfsIterations(element);
			generatorsOfLastDfsIterations = null;
			mapGeneratorToPreviousElement = null;
			
		}
	}
	
	
	/**
	 * According to A* algorithm, in each iteration the top element, i.e.
	 * the element with the lowest value for "distance+cost" function, is chosen
	 * and expanded.
	 * The expansion, in our case, is applying all operations, and creating new
	 * elements, each is a result of an operation.
	 * 
	 * 
	 * @throws TeEngineMlException
	 * @throws TreeAndParentMapException
	 * @throws OperationException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws ClassifierException
	 */
	@SuppressWarnings("unused")
	private void expandTopElementOld() throws TeEngineMlException, TreeAndParentMapException, OperationException, ScriptException, RuleBaseException, ClassifierException
	{
		AStarTreeElement element = queue.poll();
		examinedElements.add(element);
		if (null==element)throw new TeEngineMlException("empty queue");
		
		TreeAndParentMap<ExtendedInfo, ExtendedNode> tree = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(element.getTree());
		
		if (logger.isDebugEnabled())
		{
			logger.debug(eu.excitementproject.eop.common.utilities.StringUtil.generateStringOfCharacter('=', 50));
			logger.debug("Current \"future estimation\" is "+String.format("%-4.6f", this.futureEstimationPerNode) );
			logger.debug("Current element estimation = "+element.getaStarEstimation());
			logger.debug("Current element distance = "+element.getDistance());
			
			// Calculating according to current this.futureEstimationPerNode
			logger.debug("Current element future estimation, calculated now = " + String.format("%4.6f", AStarUtilities.futureEstimation(tree, operationsEnvironment.getHypothesis(),this.futureEstimationPerNode)) );
			TreeAndFeatureVector tafvElement = new TreeAndFeatureVector(element.getTree(), element.getFeatureVector());
			logger.debug("Current element past estimation = "+AStarUtilities.pastEstimation(tafvElement, this.classifier));
			logger.debug("Queue size = "+queue.size());
			logger.debug("Examined elements size = "+examinedElements.size());
		}

		if (isMatch(tree))
		{
			this.found = element;
		}
		else
		{
			
			// Here, we apply all operations, thus generating new trees.
			TreesGeneratorByOperations generator = new TreesGeneratorByOperations(
					new TreeAndFeatureVector(element.getTree(), element.getFeatureVector()),
					script.getItemListForIteration(element.getDistance(), null),
					script,
					element.getHistory(),this.operationsEnvironment);
			
			generator.generateTrees();
			Set<TreeAndFeatureVector> generatedTrees = generator.getGeneratedTrees();
			Map<TreeAndFeatureVector,TreeHistory> generatedTreesHistory = generator.getHistoryMap();
			
			if (logger.isDebugEnabled())
			{
				logger.debug("Number of trees to generate = "+generatedTrees.size());
			}
			
			int originalMissing = TreeUtilities.findRelationsNoMatch(tree, this.operationsEnvironment.getHypothesis()).size();
			double originalClassification = this.classifier.classify(element.getFeatureVector());
			double originalWeight = 0-ClassifierUtils.inverseSigmoid(originalClassification);
			
			Vector<Double> estimations = new Vector<Double>();
			Map<TreeAndFeatureVector, TreeAndParentMap<ExtendedInfo, ExtendedNode>> mapToTreeAndParentMap =
				new LinkedHashMap<TreeAndFeatureVector, TreeAndParentMap<ExtendedInfo,ExtendedNode>>();
			for (TreeAndFeatureVector generatedTree : generatedTrees)
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
				mapToTreeAndParentMap.put(generatedTree, treeAndParentMap);
				
				double classification = this.classifier.classify(generatedTree.getFeatureVector());
				double generatedTreeWeight = 0-ClassifierUtils.inverseSigmoid(classification);
				
				if (generatedTreeWeight<originalWeight)throw new TeEngineMlException("BUG");
				
				int generatedMissing = TreeUtilities.findRelationsNoMatch(treeAndParentMap, this.operationsEnvironment.getHypothesis()).size();
				
				if (generatedMissing<originalMissing)
				{
					estimations.add(
					(generatedTreeWeight-originalWeight)/((double)(originalMissing-generatedMissing))
					);
				}
			}
			double sumEstimations = 0;
			for (Double estimation : estimations)
			{
				sumEstimations+=estimation;
			}
			double averageEstimation = sumEstimations/((double)estimations.size());
			
			this.futureEstimationPerNode =
					BiuteeConstants.LEARNING_RATE_ASTAR_FUTURE_ESTIMATION*averageEstimation+
				(1-BiuteeConstants.LEARNING_RATE_ASTAR_FUTURE_ESTIMATION)*this.futureEstimationPerNode;
			
			// Here, we insert those new trees into the queue, as new elements.
			for (TreeAndFeatureVector generatedTree : generatedTrees)
			{
				TreeAndParentMap<ExtendedInfo, ExtendedNode> generatedTreeAndParentMap =
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
				double aStarEstimation = AStarUtilities.aStarEstimation(generatedTree, generatedTreeAndParentMap, operationsEnvironment.getHypothesis(), classifier, futureEstimationPerNode);
				
				AStarTreeElement generatedTreeElement = new AStarTreeElement(
						generatedTree.getTree(), generatedTreeAndParentMap.getParentMap(),
						generatedTreesHistory.get(generatedTree),
						generatedTree.getFeatureVector(), element.getOriginalSentence(),
						element.getDistance()+1,
						aStarEstimation, element);
				
				if (!alreadyExamined(generatedTreeElement))
				{
					queue.offer(generatedTreeElement);
				}
				else
				{
					if (logger.isDebugEnabled())
					{
						ImmutableList<Specification> specsHistory = generatedTreesHistory.get(generatedTree).getSpecifications();
						logger.debug("Already examined: "+specsHistory.get(specsHistory.size()-1).toString());
					}
				}
			}
		}
	}
	 
	
	private boolean isMatch(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree)
	{
		if (TreeUtilities.findRelationsNoMatch(tree, operationsEnvironment.getHypothesis()).size()==0)
			return true;
		else
			return false;
	}
	
	
	
	
	private PriorityQueue<AStarTreeElement> queue;
	private Set<AStarTreeElement> examinedElements;
	private AStarTreeElement found = null;
	
	private double futureEstimationPerNode = AStarUtilities.MINIMUM_COST_PER_NODE_ESTIMATION;
	private List<TreesGeneratorByOperations> generatorsOfLastDfsIterations;
	private Map<TreesGeneratorByOperations,AStarTreeElement> mapGeneratorToPreviousElement;
	
	
	private static Logger logger = Logger.getLogger(AStarTextTreesProcessor.class);
}
