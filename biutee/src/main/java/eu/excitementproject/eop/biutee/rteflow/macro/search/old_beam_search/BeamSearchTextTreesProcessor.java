package eu.excitementproject.eop.biutee.rteflow.macro.search.old_beam_search;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.TrainableClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.AbstractTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.macro.search.WithStatisticsTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative.LocalCreativeTextTreesProcessor;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;


/**
 * Constructing the best proof for the given parse-tree.<BR>
 * <B>NO LONGER USED</B><BR>
 * Currently, the LLGS algorithm is used, see {@link LocalCreativeTextTreesProcessor}.
 * 
 * This class, as all {@link TextTreesProcessor}'s implementations, gets the text and the
 * hypothesis, and finds out whether the text entails or not entails the hypothesis.
 * The class uses a given {@linkplain TrainableClassifier}, and returns its result on the given
 * T-H pair.
 * To make the calculation, the class applies rules and does all operations, as specified in the
 * {@linkplain OperationsScript} (given in the constructor), and finds the best proof (proof is a 
 * sequence of operations) for the given T-H pair. That proof has a corresponding feature-vector,
 * that is given to the {@linkplain TrainableClassifier} to get the result, which is a real number between
 * 0 to 1.
 * 
 * @author Asher Stern
 * @since Jan 11, 2011
 *
 */
public class BeamSearchTextTreesProcessor extends AbstractTextTreesProcessor implements WithStatisticsTextTreesProcessor
{
	////////////////////// PUBLIC /////////////////////////////////
	
	public static final boolean USE_CACHE_OF_GENERATED_TREES = BiuteeConstants.BEAM_SEARCH_USE_CACHE_OF_GENERATED_TREES;
	

	// constructor and methods
	public BeamSearchTextTreesProcessor(String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees,ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			TESystemEnvironment teSystemEnvironment
			) throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees,hypothesisTree,originalMapTreesToSentences,coreferenceInformation,classifier,lemmatizer,script,teSystemEnvironment);
	}
	

	
	public TreeAndFeatureVector getBestTree()
	{
		return bestTree;
	}

	public String getBestTreeSentence()
	{
		return bestTreeSentence;
	}

	public TreeHistory getBestTreeHistory()
	{
		return bestTreeHistory;
	}

	
	public long getNumberOfExpandedElements() throws TeEngineMlException
	{
		return numberOfExpandedElements;
	}

	public long getNumberOfGeneratedElements() throws TeEngineMlException
	{
		return numberOfGeneratedElements;
	}

	
	////////////////////////// PRIVATE & PROTECTED ///////////////////////////////
	
	
	
	@Override
	protected void processPair() throws TeEngineMlException, OperationException, ClassifierException, ScriptException, RuleBaseException, TreeAndParentMapException
	{
		logger.debug("Starting BeamSearchTextTreesProcessor.processPair()");
		this.hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(operationsEnvironment.getHypothesis());
		this.numberOfHypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(operationsEnvironment.getHypothesis().getTree()).size();
		Set<TreeAndFeatureVector> treesSet = new LinkedHashSet<TreeAndFeatureVector>();
		this.mapTreeToSentence = new LinkedHashMap<TreeAndFeatureVector, String>();
		this.historyMap = new LinkedHashMap<TreeAndFeatureVector, TreeHistory>();
		this.evaluationHistoryMap = new LinkedHashMap<TreeAndFeatureVector, TreeEvaluationsHistory>();
		
		// Initializing set of trees with the original text trees
		for (ExtendedNode textTree : this.originalTextTrees)
		{
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(textTree);
			Map<Integer,Double> initialFeatureVector = initialFeatureVector();
			TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(textTree, initialFeatureVector);
			treesSet.add(treeAndFeatureVector);
			historyMap.put(treeAndFeatureVector, new TreeHistory(TreeHistoryComponent.onlyFeatureVector(initialFeatureVector)));
			mapTreeToSentence.put(treeAndFeatureVector, this.originalMapTreesToSentences.get(textTree));
			evaluationHistoryMap.put(treeAndFeatureVector, new TreeEvaluationsHistory(createEvaluationForTree(textTreeAndParentMap)));
		}
		
		logger.debug("Starting beam search loop");
		matchingTrees = new LinkedHashSet<TreeAndFeatureVector>();
		if (USE_CACHE_OF_GENERATED_TREES) cache = new LinkedHashMap<TreeAndOperationItem, Set<TreeAndHistory>>();
		iterationIndex=0;
		int iterationIndexAfterConversion=0;
		while (iterationIndexAfterConversion<BiuteeConstants.PAIR_PROCESS_ITERATION_AFTER_CONVERSION)
		{
			Set<TreeAndFeatureVector> thisIterationMatchingTrees = findMatchingTextTrees(treesSet);
			matchingTrees.addAll(thisIterationMatchingTrees);
			ImmutableList<SingleOperationItem> operations = script.getItemListForIteration(iterationIndex, treesSet);
			if (logger.isDebugEnabled()){logger.debug("#"+iterationIndex+": operations = "+operations.toString());}
			Set<TreeAndFeatureVector> generatedTrees = generateTreesFromSet(treesSet, operations);
			treesSet.addAll(generatedTrees);
			if (logger.isDebugEnabled()){logger.debug("treesSet size before shrink: "+treesSet.size());}
			treesSet = shrinkSetOfTrees(treesSet,iterationIndex);
			if (treesSet.size()>BiuteeConstants.MAX_NUMBER_OF_TREES)
				throw new TeEngineMlException("BUG: bad shrink.");
			
			if (USE_CACHE_OF_GENERATED_TREES)
			{
				createCache(treesSet);
				this.generatedSingleIteration=null;
				if (logger.isDebugEnabled())
				{
					logger.debug("Current cache size = "+this.cache.keySet().size());
				}
			}
			this.historyMap = shrinkMap(treesSet, matchingTrees, this.historyMap);
			this.mapTreeToSentence = shrinkMap(treesSet, matchingTrees, mapTreeToSentence);
			this.evaluationHistoryMap = shrinkMap(treesSet, matchingTrees, evaluationHistoryMap);
			
			if (iterationIndexAfterConversion>0)
				++iterationIndexAfterConversion;
			else if (thisIterationMatchingTrees.size()>0)
			{
				logger.debug("Matching tree was found.");
				++iterationIndexAfterConversion;
			}
			
			logger.debug("------------------------------------");
			
			++iterationIndex;
		}
		logger.info("Done with "+iterationIndex+" iterations.");
		
		logger.info("Used memory: "+Utils.stringMemoryUsedInMB());
		Set<TreeAndFeatureVector> lastIterationMatchingTrees = findMatchingTextTrees(treesSet);
		matchingTrees.addAll(lastIterationMatchingTrees);
		treesSet=null;
		historyMap = shrinkMap(matchingTrees, historyMap);
		mapTreeToSentence = shrinkMap(matchingTrees, mapTreeToSentence);
		evaluationHistoryMap = shrinkMap(matchingTrees, evaluationHistoryMap);
		
		
		// Processing done.
		// Now - find the best tree
		this.bestTree = findBest(matchingTrees);
		
		if (!historyMap.containsKey(bestTree))
			throw new TeEngineMlException("BUG: historyMap does not contain the best tree.");
		this.bestTreeHistory = historyMap.get(bestTree);
		
		if (!mapTreeToSentence.containsKey(bestTree))
			throw new TeEngineMlException("BUG: mapTreeToSentence does not contain the best tree.");
		this.bestTreeSentence = mapTreeToSentence.get(bestTree);
	}
	
	
	
	
	private Set<TreeAndFeatureVector> findMatchingTextTrees(Set<TreeAndFeatureVector> textTrees) throws TeEngineMlException, TreeAndParentMapException
	{
		Set<TreeAndFeatureVector> matchingTrees = new LinkedHashSet<TreeAndFeatureVector>();
		for (TreeAndFeatureVector textTree : textTrees)
		{
			TreeAndParentMap<ExtendedInfo,ExtendedNode> textTreeAndParentMap = new TreeAndParentMap<ExtendedInfo,ExtendedNode>(textTree.getTree());
			if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
			{
				if (AdvancedEqualities.findMissingRelations(textTreeAndParentMap, operationsEnvironment.getHypothesis()).size()==0)
					matchingTrees.add(textTree);
			}
			else
			{
				boolean nodesOk = (TreeUtilities.findNodesNoMatch(textTreeAndParentMap,operationsEnvironment.getHypothesis()).size()==0);
				if (nodesOk)
				{
					boolean relationsOk = (TreeUtilities.findRelationsNoMatch(textTreeAndParentMap, operationsEnvironment.getHypothesis()).size()==0);
					if (relationsOk)
					{
						matchingTrees.add(textTree);
					}
				}
			}
		}
		return matchingTrees;
	}
	
	
	private Set<TreeAndFeatureVector> generateTreesFromSet(Set<TreeAndFeatureVector> textTrees, ImmutableList<SingleOperationItem> operations) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException
	{
		this.generatedSingleIteration = new LinkedHashMap<TreeAndOperationItem, Set<TreeAndHistory>>();
		this.debugFoundInCacheCount = 0;
		this.debugGeneratedFoundInCacheCount = 0;
		Set<TreeAndFeatureVector> ret = new LinkedHashSet<TreeAndFeatureVector>();
		for (TreeAndFeatureVector textTree : textTrees)
		{
			ret.addAll(generateTrees(textTree,operations));
		}
		if (USE_CACHE_OF_GENERATED_TREES && logger.isDebugEnabled())
		{
			logger.debug("generateTreesFromSet: debugFoundInCache = "+debugFoundInCacheCount);
			logger.debug("generateTreesFromSet: debugGeneratedFoundInCacheCount = "+debugGeneratedFoundInCacheCount);
		}
		return ret;
	}
	
	private Set<TreeAndFeatureVector> generateTrees(TreeAndFeatureVector textTree, ImmutableList<SingleOperationItem> operations) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException
	{
		++this.numberOfExpandedElements;
		TreesGeneratorByOperations generator =
			new TreesGeneratorByOperations(textTree, operations, script, historyMap.get(textTree),this.operationsEnvironment);
		if (USE_CACHE_OF_GENERATED_TREES) generator.setCache(cache);
		generator.generateTrees();
		if (USE_CACHE_OF_GENERATED_TREES) this.generatedSingleIteration.putAll(generator.getGeneratedTreesAsMap());
		if (USE_CACHE_OF_GENERATED_TREES) this.debugFoundInCacheCount += generator.getDebugFoundInCacheCount();
		if (USE_CACHE_OF_GENERATED_TREES) this.debugGeneratedFoundInCacheCount += generator.getDebugGeneratedFoundInCacheCount();
		
		// new code, after changing the generator working method.
		String originalSentence = mapTreeToSentence.get(textTree);
		for (TreeAndFeatureVector generatedTree: generator.getGeneratedTrees())
		{
			mapTreeToSentence.put(generatedTree, originalSentence);
			
			// Asher 10-June-2011
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
			evaluationHistoryMap.put(generatedTree, new TreeEvaluationsHistory(evaluationHistoryMap.get(textTree),createEvaluationForTree(treeAndParentMap)));
		}
		this.historyMap.putAll(generator.getHistoryMap());
		
		Set<TreeAndFeatureVector> generator_generatedTrees = generator.getGeneratedTrees();
		this.numberOfGeneratedElements += generator_generatedTrees.size();
		return generator_generatedTrees;
	}
	
//	private Set<TreeAndFeatureVector> shrinkSetOfTrees(Set<TreeAndFeatureVector> setOfTrees, int iterationIndex) throws ClassifierException, TeEngineMlException, TreeAndParentMapException
//	{
//		Vector<EvaluatedTreeAndFeatureVector> evaluatedTrees = new Vector<EvaluatedTreeAndFeatureVector>();
//		//hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(hypothesis);
//		double hypothesisTotalNumberOfNodes = (double)AbstractNodeUtils.treeToSet(hypothesis.getTree()).size();
//		for (TreeAndFeatureVector tree : setOfTrees)
//		{
//			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTreeAndParentMap = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(tree.getTree());
//			double missingNodesPortion;
//			double missingRelationsPortion;
//			double missingLemmasPortion;
//			if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
//			{
//				missingNodesPortion = AdvancedEqualities.findMissingNodes(textTreeAndParentMap, hypothesis).size()/hypothesisTotalNumberOfNodes;
//				missingRelationsPortion = AdvancedEqualities.findMissingRelations(textTreeAndParentMap, hypothesis).size()/hypothesisTotalNumberOfNodes;
//			}
//			else
//			{
//				missingNodesPortion = TreeUtilities.missingNodesPortion(textTreeAndParentMap, hypothesis);
//				missingRelationsPortion = TreeUtilities.missingRelationsPortion(textTreeAndParentMap, hypothesis);
//			}
//			missingLemmasPortion = TreeUtilities.missingLemmasPortion(textTreeAndParentMap, hypothesisLemmasLowerCase);
//			double classifierResult = classifier.classify(tree.getFeatureVector());
////			double classifierResult = mapTreeToClassification.get(tree);
////			classifierResult = ClassifierUtils.normalize(minimum, maximum, classifierResult);
//			if (classifierResult<0)throw new TeEngineMlException("bug: classifierResult<0 for "+classifierResult);
//			if (classifierResult>1.0)throw new TeEngineMlException("bug: classifierResult>1.0 for "+classifierResult);
//			//logger.info("classifierResult"+classifierResult);
//			
//			double evaluation = evaluationFunction.evaluateTree(missingNodesPortion, missingRelationsPortion, missingLemmasPortion, classifierResult, iterationIndex);
//			
//			evaluatedTrees.add(new EvaluatedTreeAndFeatureVector(evaluation, tree));
//		}
//		Collections.sort(evaluatedTrees);
//		
//		
//		Set<TreeAndFeatureVector> ret = new LinkedHashSet<TreeAndFeatureVector>();
//		int retNumberOfTrees = 0;
//		for (int index=evaluatedTrees.size()-1;(index>=0)&&(retNumberOfTrees<Constants.MAX_NUMBER_OF_TREES);--index)
//		{
//			ret.add(evaluatedTrees.get(index).getTreeAndFeatureVector());
//			retNumberOfTrees++;
//		}
//		
//		return ret;
//	}
	
	private Set<TreeAndFeatureVector> shrinkSetOfTrees(Set<TreeAndFeatureVector> setOfTrees, int iterationIndex) throws ClassifierException, TeEngineMlException, TreeAndParentMapException
	{
		Set<TreeAndFeatureVector> ret = null;
		if (setOfTrees.size()==0)throw new TeEngineMlException("BUG");
		if (setOfTrees.size()<=BiuteeConstants.MAX_NUMBER_OF_TREES)
		{
			ret = setOfTrees;
		}
		else
		{
			Vector<EvaluatedTreeAndFeatureVector> evaluatedTrees = new Vector<EvaluatedTreeAndFeatureVector>();
			for (TreeAndFeatureVector tree : setOfTrees)
			{
				TreeEvaluationsHistory treeEvaluationHistory = this.evaluationHistoryMap.get(tree);
				if (null==treeEvaluationHistory) throw new TeEngineMlException("BUG");

				double classifierResult = classifier.classify(tree.getFeatureVector());
				if (classifierResult<0)throw new TeEngineMlException("bug: classifierResult<0 for "+classifierResult);
				if (classifierResult>1.0)throw new TeEngineMlException("bug: classifierResult>1.0 for "+classifierResult);

				double evaluation = evaluationFunction.evaluateTree(treeEvaluationHistory, classifierResult, iterationIndex);
				evaluatedTrees.add(new EvaluatedTreeAndFeatureVector(evaluation, tree));
			}

			Collections.sort(evaluatedTrees);

			EvaluatedTreeAndFeatureVector[] evaluatedTreesAsArray = evaluatedTrees.toArray(new EvaluatedTreeAndFeatureVector[0]);
			ret = new LinkedHashSet<TreeAndFeatureVector>();
			int retNumberOfTrees = 0;
			for (int index=(evaluatedTreesAsArray.length-1);(index>=0)&&(retNumberOfTrees<BiuteeConstants.MAX_NUMBER_OF_TREES);--index)
			{
				ret.add(evaluatedTreesAsArray[index].getTreeAndFeatureVector());
				retNumberOfTrees++;
			}
			if (ret.size()!=BiuteeConstants.MAX_NUMBER_OF_TREES)throw new TeEngineMlException("BUG. Single we deal with setOfTrees with size>MAX_NUMBER_OF_TREES, the returned set must be of size MAX_NUMBER_OF_TREES");
		}
		return ret;
	}
	
	private TreeAndFeatureVector findBest(Set<TreeAndFeatureVector> treesSet) throws ClassifierException
	{
		double bestEvaluation = 0;
		TreeAndFeatureVector bestTree = null;
		boolean firstIteration = true;
		for (TreeAndFeatureVector tree : treesSet)
		{
			double evaluation = classifier.classify(tree.getFeatureVector());
			if (firstIteration)
			{
				bestTree = tree;
				bestEvaluation = evaluation;
				firstIteration=false;
			}
			else
			{
				if (bestEvaluation<evaluation)
				{
					bestEvaluation=evaluation;
					bestTree = tree;
				}
			}
		}
		return bestTree;
	}
	
	

	

	

	
	/**
	 * Returns a new map, such that it contains all the (k,v) of "originalMap" that their "k"
	 * exists in "keysToExist" 
	 * @param <K>
	 * @param <V>
	 * @param keysToExist
	 * @param originalMap
	 * @return
	 */
	private static <K,V> Map<K,V> shrinkMap(Set<K> keysToExist, Map<K,V> originalMap)
	{
		Map<K,V> ret = new LinkedHashMap<K, V>();
		for (K k : originalMap.keySet())
		{
			if (keysToExist.contains(k))
				ret.put(k,originalMap.get(k));
		}
		return ret;
	}

	private static <K,V> Map<K,V> shrinkMap(Set<K> keysToExist1, Set<K> keysToExist2, Map<K,V> originalMap)
	{
		Map<K,V> ret = new LinkedHashMap<K, V>();
		for (K k : originalMap.keySet())
		{
			if ( (keysToExist1.contains(k)) || (keysToExist2.contains(k)))
				ret.put(k,originalMap.get(k));
		}
		return ret;
	}
	
	private void createCache(Set<TreeAndFeatureVector> currentTrees)
	{
		Map<TreeAndOperationItem, Set<TreeAndHistory>> oldCache = cache;
		this.cache = new LinkedHashMap<TreeAndOperationItem, Set<TreeAndHistory>>();
		for (TreeAndOperationItem treeAndOperation : this.generatedSingleIteration.keySet())
		{
			if (currentTrees.contains(treeAndOperation.getTree()))
			{
				cache.put(treeAndOperation, generatedSingleIteration.get(treeAndOperation));
			}
		}
		for (TreeAndOperationItem treeAndOperation : oldCache.keySet())
		{
			if (!cache.containsKey(treeAndOperation))
			{
				if (currentTrees.contains(treeAndOperation.getTree()))
				{
					cache.put(treeAndOperation, oldCache.get(treeAndOperation));
				}
			}
		}

	}
	
	private SingleTreeEvaluations createEvaluationForTree(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree)
	{
		AlignmentCalculator alignmentCalculator =
				new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), tree, operationsEnvironment.getHypothesis());
		return alignmentCalculator.getEvaluations(hypothesisLemmasLowerCase,hypothesisNumberOfNodes);
		
//		int missingNodes;
//		int missingRelations;
//		int missingLemmas;
//		if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
//		{
//			missingNodes = AdvancedEqualities.findMissingNodes(tree, operationsEnvironment.getHypothesis()).size();
//			missingRelations = AdvancedEqualities.findMissingRelations(tree, operationsEnvironment.getHypothesis()).size();			
//		}
//		else
//		{
//			missingNodes = TreeUtilities.findNodesNoMatch(tree, operationsEnvironment.getHypothesis()).size();
//			missingRelations = TreeUtilities.findRelationsNoMatch(tree, operationsEnvironment.getHypothesis()).size();
//		}
//		int coveredLemmas = TreeUtilities.findCoveredLemmasLowerCase(tree,hypothesisLemmasLowerCase).size();
//		missingLemmas = hypothesisLemmasLowerCase.size()-coveredLemmas;
//		
//		
//		
//		double missingNodesPortion = ((double)missingNodes) / ((double)numberOfHypothesisNodes);
//		double missingRelationsPortion = ((double)missingRelations) / ((double)(numberOfHypothesisNodes-1));
//		double missingLemmasPortion = ((double)missingLemmas) / ((double)(hypothesisLemmasLowerCase.size()));
//		
//		return new SingleTreeEvaluations(missingNodes, missingRelations, missingLemmas, missingNodesPortion, missingRelationsPortion, missingLemmasPortion);
	}
	

	
	
	
	

	
	
	private EvaluationFunction evaluationFunction = new EvaluationFunction();
	private Map<TreeAndFeatureVector,TreeEvaluationsHistory> evaluationHistoryMap;

	
	private Set<String> hypothesisLemmasLowerCase;
	@SuppressWarnings("unused")
	private int numberOfHypothesisNodes;
	
	private Map<TreeAndFeatureVector, String> mapTreeToSentence;
	private Map<TreeAndFeatureVector,TreeHistory> historyMap;
	
	
	private int iterationIndex = 0;
	private Set<TreeAndFeatureVector> matchingTrees;
	
	private TreeAndFeatureVector bestTree;
	private String bestTreeSentence;
	private TreeHistory bestTreeHistory;
	
	private Map<TreeAndOperationItem, Set<TreeAndHistory>> cache;
	private Map<TreeAndOperationItem, Set<TreeAndHistory>> generatedSingleIteration;
	private int debugFoundInCacheCount = 0;
	private int debugGeneratedFoundInCacheCount = 0;
	
	private long numberOfExpandedElements = 0;
	private long numberOfGeneratedElements = 0;

	

	private static Logger logger = Logger.getLogger(BeamSearchTextTreesProcessor.class);
}
