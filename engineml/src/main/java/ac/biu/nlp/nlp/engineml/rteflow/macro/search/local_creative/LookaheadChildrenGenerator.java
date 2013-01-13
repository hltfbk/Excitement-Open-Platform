package ac.biu.nlp.nlp.engineml.rteflow.macro.search.local_creative;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCalculator;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LinearClassifier;
import ac.biu.nlp.nlp.engineml.datastructures.SingleItemSet;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.RuleBaseException;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.SingleTreeEvaluations;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeAndFeatureVector;
import ac.biu.nlp.nlp.engineml.rteflow.macro.TreeHistory;
import ac.biu.nlp.nlp.engineml.rteflow.micro.OperationsEnvironment;
import ac.biu.nlp.nlp.engineml.rteflow.micro.TreesGeneratorByOperations;
import ac.biu.nlp.nlp.engineml.script.OperationsScript;
import ac.biu.nlp.nlp.engineml.script.ScriptException;
import ac.biu.nlp.nlp.engineml.script.SingleOperationItem;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;

/**
 * Gets a parse-tree and generates all descendants according to the
 * local-creative algorithm
 * 
 * @author Asher Stern
 * @since Sep 27, 2011
 *
 */
public class LookaheadChildrenGenerator
{
	public LookaheadChildrenGenerator(LocalCreativeTreeElement rootElement,
			int lookAheadDepth, OperationsScript<Info, BasicNode> script,
			OperationsEnvironment operationsEnvironment,
			LinearClassifier linearClassifier) throws TeEngineMlException
	{
		super();
		
		if (lookAheadDepth<1) throw new TeEngineMlException("lookAheadDepth<1");
		if (null==rootElement) throw new TeEngineMlException("Null rootElement");
		if (rootElement.getLocalIteration()!=0) throw new TeEngineMlException("rootElement.getLocalIteration()!=0");
		
		this.rootElement = rootElement;
		this.lookAheadDepth = lookAheadDepth;
		this.script = script;
		this.operationsEnvironment = operationsEnvironment;
		this.linearClassifier = linearClassifier;
	}
	
	public void generate() throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
	{
		generatedElements = generate(rootElement);
	}
	
	public List<LocalCreativeTreeElement> getGeneratedElements()
	{
		return generatedElements;
	}

	public int getNumberOfExpansions()
	{
		return numberOfExpansions;
	}

	public int getNumberOfGenerations()
	{
		return numberOfGenerations;
	}

	protected List<LocalCreativeTreeElement> generate(LocalCreativeTreeElement element) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
	{
		numberOfExpansions+=1;
		List<LocalCreativeTreeElement> immediateChildren = generateImmediateChildren(element);
		numberOfGenerations += immediateChildren.size();
		List<LocalCreativeTreeElement> ret = new ArrayList<LocalCreativeTreeElement>(immediateChildren.size());
		ret.addAll(immediateChildren);
		if (element.getLocalIteration()<lookAheadDepth) // keep consistent with LocalCreativeTextTreesProcessor
		{
			for (LocalCreativeTreeElement immediateChild : immediateChildren)
			{
				ret.addAll(generate(immediateChild));
			}
		}
		return ret;
	}
	
	protected List<LocalCreativeTreeElement> generateImmediateChildren(LocalCreativeTreeElement element) throws TeEngineMlException, OperationException, ScriptException, RuleBaseException, TreeAndParentMapException, ClassifierException
	{
		TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(element.getTree(), element.getFeatureVector());
		SingleItemSet<TreeAndFeatureVector> setTrees = new SingleItemSet<TreeAndFeatureVector>(treeAndFeatureVector);
		ImmutableList<SingleOperationItem> operations = script.getItemListForLocalCreativeIteration(element.getGlobalIteration(),element.getLocalIteration(),setTrees);
		TreesGeneratorByOperations generator =
			new TreesGeneratorByOperations(
					treeAndFeatureVector,
					operations,
					script,
					element.getHistory(),
					operationsEnvironment
					);
		
		if (element.getLocalIteration()>0)
		{
			generator.setAffectedNodes(element.getAffectedNodes());
		}
		generator.generateTrees();
		Set<TreeAndFeatureVector> generatedTrees = generator.getGeneratedTrees();
		Map<TreeAndFeatureVector,TreeHistory> historyMap = generator.getHistoryMap();
		Map<ExtendedNode, Set<ExtendedNode>> affectedNodes = generator.getMapAffectedNodes();
		List<LocalCreativeTreeElement> ret = new ArrayList<LocalCreativeTreeElement>(generatedTrees.size());
		for (TreeAndFeatureVector generatedTree : generatedTrees)
		{
			double cost = -linearClassifier.getProduct(generatedTree.getFeatureVector());
			double gap = getHeuristicGap(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(generatedTree.getTree()));

			LocalCreativeTreeElement generatedElement =
				new LocalCreativeTreeElement(
						generatedTree.getTree(),
						historyMap.get(generatedTree),
						generatedTree.getFeatureVector(),
						element.getLocalIteration()+1,
						rootElement.getGlobalIteration()+1,
						affectedNodes.get(generatedTree.getTree()),
						cost,
						gap
						);
			
			ret.add(generatedElement);
		}
		return ret;
	}
	
	protected double getHeuristicGap(TreeAndParentMap<ExtendedInfo, ExtendedNode> tree)
	{
		SingleTreeEvaluations evaluations = new AlignmentCalculator(operationsEnvironment.getAlignmentCriteria(), tree, operationsEnvironment.getHypothesis()).getEvaluations(operationsEnvironment.getHypothesisLemmasLowerCase(), operationsEnvironment.getHypothesisNumberOfNodes());
		return (double)(evaluations.getMissingLemmas()+evaluations.getMissingNodes()+evaluations.getMissingRelations());
		// return TreeUtilities.getHeuristicGap(tree, this.operationsEnvironment);
	}

	private final LocalCreativeTreeElement rootElement;
	private final int lookAheadDepth;
	private final OperationsScript<Info, BasicNode> script;
	private final OperationsEnvironment operationsEnvironment;
	private final LinearClassifier linearClassifier;
	
	private List<LocalCreativeTreeElement> generatedElements;
	private int numberOfExpansions = 0;
	private int numberOfGenerations = 0;
}
