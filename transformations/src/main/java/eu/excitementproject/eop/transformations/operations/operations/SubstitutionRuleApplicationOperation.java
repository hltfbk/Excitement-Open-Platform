package eu.excitementproject.eop.transformations.operations.operations;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;
import eu.excitementproject.eop.transformations.datastructures.FromBidirectionalMapValueSetMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.utilities.InfoServices;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * This class performs the regular <B>rule application</B>.
 * It replaces the left-hand-side instance in the given tree by the instantiation
 * of the right hand side. 
 * 
 * @author Asher Stern
 * @since February  2011
 *
 * @param <TI>	type of the text node info
 * @param <TN>	type of the text node
 * @param <RI>	type of the rule node info
 * @param <RN>	type of the rule node
 */
public abstract class SubstitutionRuleApplicationOperation
	<TI extends Info, TN extends AbstractNode<TI, TN>, RI extends Info, RN extends AbstractNode<RI, RN>> 
	extends GenerationOperation<TI, TN>
{

	/**
	 * Constructor that defines the text tree, the rule, and other information
	 * required for applying the rule.
	 * 
	 * @param textTree The text tree
	 * @param hypothesisTree The hypothesis tree (not required, actually)
	 * @param rule The rule
	 * @param mapLhsToTree A one to one mapping from the rule's left-hand-side's
	 * nodes to nodes in the tree. Those tree nodes are actually a connected
	 * component in the tree that will be completely replaced by the rule's
	 * right-hand-side.
	 * @throws OperationException
	 */
	public SubstitutionRuleApplicationOperation(TreeAndParentMap<TI,TN> textTree, TreeAndParentMap<TI,TN> hypothesisTree,
			SyntacticRule<RI, RN> rule, BidirectionalMap<RN, TN> mapLhsToTree) throws OperationException
	{
		super(textTree, hypothesisTree);
		this.rule = rule;
		this.mapLhsToTree = mapLhsToTree;
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		bidiMapOrigToGenerated = new SimpleBidirectionalMap<TN, TN>();
		this.rootOfLhsInTree = mapLhsToTree.leftGet(rule.getLeftHandSide());
		if (null==rootOfLhsInTree)
		{
			throw new OperationException("LHS root not mapped to any node in the original tree.\n" +
					"The map from LHS to the tree is:\n"+debug_print_mapLhsToTree());
		}
		try
		{
			generatedTree = copySubTree(this.textTree.getTree());
		}
		catch(TeEngineMlException e)
		{
			throw new OperationException("Rule application failed. See nested exception.",e);
		}
	}
	

	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		mapOriginalToGenerated = new FromBidirectionalMapValueSetMap<TN, TN>(bidiMapOrigToGenerated);
	}
	
	
	protected TN copySubTree(TN subtree) throws TeEngineMlException
	{
		TN ret = null;
		if (rootOfLhsInTree == subtree)
		{
			rhsInstantiation = new RuleRhsInstantiation<TI, TN, RI, RN>(
					getNewInfoServices(), nodeConstructor, this.textTree.getTree(),
					rule, mapLhsToTree, subtree.getInfo());
			
			rhsInstantiation.generate();
			TN generatedInstance = rhsInstantiation.getGeneratedTree();
//			try {	RulesViewer.printTree(generatedInstance);	} 
//			catch (TreeStringGeneratorException e) {	e.printStackTrace();}
			DsUtils.BidiMapAddAll(bidiMapOrigToGenerated, rhsInstantiation.getMapOrigToGenerated());
			
//			// copy to the generatedInstance those children of rootOfLhsInTree that aren't matched in the rule 
//			for (TN child : rootOfLhsInTree.getChildren())
//			{
//				if (!bidiMapOrigToGenerated.leftContains(child) & !mapLhsToTree.rightContains(child))
//					generatedInstance.addChild(child);
//			}

//			try {
//				RulesViewer.printTree(generatedInstance);;
//			} catch (TreeStringGeneratorException e) {
//				e.printStackTrace();
//			}

			affectedNodes = rhsInstantiation.getAffectedNodes();
//			affectedNodes = new LinkedHashSet<TN>();
//			affectedNodes.addAll(AbstractNodeUtils.treeToSet(generatedInstance));

			ret = generatedInstance;
		}
		else
		{
			ret = nodeConstructor.newNode(subtree.getInfo());
			if (subtree.getChildren()!=null)
			{
				for (TN child : subtree.getChildren())
				{
					ret.addChild(copySubTree(child));
				}
			}
			bidiMapOrigToGenerated.put(subtree, ret);
		}
		
		return ret;
	}

	
	/**
	 * return a new implementation of {@link AbstractNodeConstructor} to match the generic types
	 * @return
	 */
	protected abstract AbstractNodeConstructor<TI, TN> getNewNodeConstructor();

	/**
	 * return a new implementation of InfoServices to match the generic types
	 * @return 
	 */
	protected abstract InfoServices<TI, RI> getNewInfoServices();


	private String debug_print_mapLhsToTree()
	{
		StringBuilder sb = new StringBuilder();
		for (RN ruleNode : mapLhsToTree.leftSet())
		{
			String ruleLemma = "null";
			if (ruleNode!=null) {ruleLemma = InfoGetFields.getLemma(ruleNode.getInfo());}
			TN treeNode = mapLhsToTree.leftGet(ruleNode);
			String treeLemma = "null";
			if (treeNode!=null) {treeLemma = InfoGetFields.getLemma(treeNode.getInfo());}
			
			sb.append(ruleLemma).append(" --> ").append(treeLemma).append("\n");
		}
		return sb.toString();
	}

	
	
	protected AbstractNodeConstructor<TI, TN> nodeConstructor = getNewNodeConstructor();
	protected SyntacticRule<RI, RN> rule;
	protected BidirectionalMap<RN, TN> mapLhsToTree;
	
	protected RuleRhsInstantiation<TI, TN, RI, RN> rhsInstantiation;
	
	protected TN rootOfLhsInTree;
	protected BidirectionalMap<TN, TN> bidiMapOrigToGenerated;
	
	

}
