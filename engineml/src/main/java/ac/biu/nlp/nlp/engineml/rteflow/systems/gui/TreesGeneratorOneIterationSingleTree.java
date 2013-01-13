package ac.biu.nlp.nlp.engineml.rteflow.systems.gui;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCalculator;
import ac.biu.nlp.nlp.engineml.classifiers.Classifier;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.generic.truthteller.AnnotatorException;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.operations.specifications.Specification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.InitializationTextTreesProcessor;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeAndFeatureVector;
import ac.biu.nlp.nlp.engineml.rteflow.macro.search.old_beam_search.EvaluationFunction;
import ac.biu.nlp.nlp.engineml.rteflow.micro.TreesGeneratorByOperations;
import ac.biu.nlp.nlp.engineml.rteflow.systems.TESystemEnvironment;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.script.SingleOperationItem;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.lemmatizer.Lemmatizer;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;

/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since May 25, 2011
 *
 */
public class TreesGeneratorOneIterationSingleTree extends InitializationTextTreesProcessor
{
	public TreesGeneratorOneIterationSingleTree(
			String textText, String hypothesisText,
			List<ExtendedNode> originalTextTrees, ExtendedNode hypothesisTree,
			Map<ExtendedNode, String> originalMapTreesToSentences,
			TreeCoreferenceInformation<ExtendedNode> coreferenceInformation,
			LinearClassifier classifier, Lemmatizer lemmatizer,
			OperationsScript<Info, BasicNode> script,
			Classifier classifierForPredictions,TESystemEnvironment teSystemEnvironment) throws TeEngineMlException
	{
		super(textText, hypothesisText, originalTextTrees, hypothesisTree, originalMapTreesToSentences,
				coreferenceInformation, classifier, lemmatizer, script,
				teSystemEnvironment);
		
		this.classifierForPredictions = classifierForPredictions;
	}


	@Override
	public void init() throws TeEngineMlException, OperationException, TreeAndParentMapException, AnnotatorException
	{
		super.init();
		hypothesisTotalNumberOfNodes = (double)AbstractNodeUtils.treeToSet(operationsEnvironment.getHypothesis().getTree()).size();
		hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(operationsEnvironment.getHypothesis());
		this.initialized=true;
		initialFeatureVector = initialFeatureVector();
	}
	
	

	public Map<Integer, Double> getInitialFeatureVector()
	{
		return initialFeatureVector;
	}


	public List<SingleTreeComponent> generate(SingleTreeComponent originalSingleTreeComponent, int nextId) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException, VisualTracingToolException
	{
		if (!initialized)throw new VisualTracingToolException("Not initialized");
		List<SingleTreeComponent> generatedTrees;
		TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(originalSingleTreeComponent.getTree(), originalSingleTreeComponent.getFeatureVector());
		Set<TreeAndFeatureVector> setTreeAndFeatureVector = new HashSet<TreeAndFeatureVector>();
		setTreeAndFeatureVector.add(treeAndFeatureVector);
		ImmutableList<SingleOperationItem> operations = this.script.getItemListForIteration(originalSingleTreeComponent.getIterationNumber(), setTreeAndFeatureVector);
		
		String originalSentence = originalMapTreesToSentences.get(originalSingleTreeComponent.getTree());
		
		
		
		TreesGeneratorByOperations generator = 
			new TreesGeneratorByOperations(treeAndFeatureVector, operations, script, originalSingleTreeComponent.getHistory(),this.operationsEnvironment);
		generator.generateTrees();
		Map<ExtendedNode, Set<ExtendedNode>> mapAffectedNodes = generator.getMapAffectedNodes();
		generatedTrees = new ArrayList<SingleTreeComponent>(generator.getGeneratedTrees().size());
		int generatedIteration = originalSingleTreeComponent.getIterationNumber()+1;
		int originalSentenceNo = originalSingleTreeComponent.getOriginalSentenceNo();
		for (TreeAndFeatureVector generatedTree : generator.getGeneratedTrees())
		{
			originalMapTreesToSentences.put(generatedTree.getTree(), originalSentence);
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap =
				new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree());
			int sizeOfSpecs = generator.getHistoryMap().get(generatedTree).getSpecifications().size();
			Specification lastSpec = generator.getHistoryMap().get(generatedTree).getSpecifications().get(sizeOfSpecs-1);
			double classificationScore = classifier.classify(generatedTree.getFeatureVector());
			double classificationScoreForPredictions = classifierForPredictions.classify(generatedTree.getFeatureVector());
			double evaluation = calculateEvaluation(treeAndParentMap, classificationScore, generatedIteration);
			double cost = -classifier.getProduct(generatedTree.getFeatureVector());
			
			Set<ExtendedNode> affectedNodes = mapAffectedNodes.get(generatedTree.getTree());
			if (null==affectedNodes) throw new VisualTracingToolException("null affected nodes");
			
			Set<ExtendedNode> missingNodes =
					new AlignmentCalculator(teSystemEnvironment.getAlignmentCriteria(), treeAndParentMap, operationsEnvironment.getHypothesis()).getMissingTriples();
//			if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
//			{
//				missingNodes = AdvancedEqualities.findMissingRelations(treeAndParentMap, operationsEnvironment.getHypothesis());
//			}
//			else
//			{
//				missingNodes = TreeUtilities.findRelationsNoMatch(treeAndParentMap, operationsEnvironment.getHypothesis());
//			}
			
			

			SingleTreeComponent generatedComponent =
				new SingleTreeComponent(generatedTree.getTree(), generator.getHistoryMap().get(generatedTree), lastSpec, affectedNodes, 
						generatedTree.getFeatureVector(), classificationScore, evaluation, originalSingleTreeComponent, generatedIteration, nextId, missingNodes,
						classificationScoreForPredictions, cost, originalSentenceNo );
			++nextId;
			
			generatedTrees.add(generatedComponent);
		}
		
		return generatedTrees;
	}
	
	public List<ExtendedNode> getOriginalTextTrees()
	{
		return this.originalTextTrees;
	}
	
	public ExtendedNode getHypothesisTree()
	{
		return this.hypothesisTree;
	}
	
	public Map<ExtendedNode,String> getMapTreeToSentence()
	{
		return this.originalMapTreesToSentences;
	}
	
	public int getHypothesisNumberOfNodes()
	{
		return this.hypothesisNumberOfNodes;
	}
	
	public TreeAndParentMap<ExtendedInfo, ExtendedNode> getHypothesis()
	{
		return this.operationsEnvironment.getHypothesis();
	}
	
	
	public double calculateEvaluation(TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap, double classificationScore, int generatedIteration)
	{
		// TODO implement!
		return 0;
//		double missingNodesPortion;
//		double missingRelationsPortion;
//		double missingLemmasPortion;
//		if (AdvancedEqualities.USE_ADVANCED_EQUALITIES)
//		{
//			missingNodesPortion = AdvancedEqualities.findMissingNodes(treeAndParentMap, hypothesis).size()/hypothesisTotalNumberOfNodes;
//			missingRelationsPortion = AdvancedEqualities.findMissingRelations(treeAndParentMap, hypothesis).size()/hypothesisTotalNumberOfNodes;
//		}
//		else
//		{
//			missingNodesPortion = TreeUtilities.missingNodesPortion(treeAndParentMap, hypothesis);
//			missingRelationsPortion = TreeUtilities.missingRelationsPortion(treeAndParentMap, hypothesis);
//		}
//		missingLemmasPortion = TreeUtilities.missingLemmasPortion(treeAndParentMap, hypothesisLemmasLowerCase);
//
//		double evaluation = evaluationFunction.evaluateTree(missingNodesPortion, missingRelationsPortion, missingLemmasPortion, classificationScore, generatedIteration);
//		return evaluation;
//		
	}
	
	
	
	
	

	

	protected EvaluationFunction evaluationFunction = new EvaluationFunction();
	protected Classifier classifierForPredictions;
	
	protected double hypothesisTotalNumberOfNodes;
	protected Set<String> hypothesisLemmasLowerCase;
	protected Map<Integer, Double> initialFeatureVector;
	protected boolean initialized = false;
	
}
