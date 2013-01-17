package eu.excitementproject.eop.biutee.rteflow.macro.search.local_creative;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.micro.OperationsEnvironment;
import eu.excitementproject.eop.biutee.rteflow.micro.TreesGeneratorByOperations;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.script.SingleOperationItem;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.datastructures.SingleItemSet;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.SingleTreeEvaluations;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

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
