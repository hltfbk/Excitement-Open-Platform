package ac.biu.nlp.nlp.engineml.operations.operations;

import ac.biu.nlp.nlp.engineml.datastructures.FromBidirectionalMapValueSetMap;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.Rule;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNodeConstructor;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.engineml.utilities.rules.ExtendedInfoServices;
import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultEdgeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultNodeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultSyntacticInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DependencyRelation;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.EdgeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;


/**
 * Application of a rule, such that the right-hand-side of the rule becomes the
 * new tree, not part of the original tree (sometimes named "extraction" instead
 * of "introduction").
 * 
 * 
 * @author Asher Stern
 * @since Feb 13, 2011
 *
 */
public class IntroductionRuleApplicationOperation extends GenerationOperationForExtendedNode
{
	public IntroductionRuleApplicationOperation(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree, Rule<Info, BasicNode> rule,
			BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree) throws OperationException
	{
		super(textTree, hypothesisTree);
		this.rule = rule;
		this.mapLhsToTree = mapLhsToTree;
	}
	
	protected void setOverrideRelationToArtificialRoot(EdgeInfo overrideRelationToArtificialRoot) throws UnsupportedPosTagStringException
	{
		this.overrideRelationToArtificialRoot = new ExtendedInfo("", new DefaultNodeInfo("", "", 0, null, new DefaultSyntacticInfo(new UnspecifiedPartOfSpeech(""))), overrideRelationToArtificialRoot, ExtendedNodeConstructor.EMPTY_ADDITIONAL_NODE_INFORMATION);
	}
	
	


	@Override
	protected void generateTheTree() throws OperationException
	{
		// The relation to the root should be empty
		try{setOverrideRelationToArtificialRoot(overrideRelationArtificialRoot);} catch (UnsupportedPosTagStringException e1){throw new OperationException("Operation failed. Very unexpected. See nested exception",e1);}
		
		// Generate the RHS, which will be the generated tree.
		RuleRhsInstantiation<ExtendedInfo, ExtendedNode, Info, BasicNode> rhsInstantiation = 
			new RuleRhsInstantiation<ExtendedInfo, ExtendedNode, Info, BasicNode>(new ExtendedInfoServices(), new ExtendedNodeConstructor(), textTree.getTree(), this.rule, this.mapLhsToTree, overrideRelationToArtificialRoot);
		try
		{
			rhsInstantiation.generate();
			ExtendedNode fromRhsInstantiation = rhsInstantiation.getGeneratedTree();
			if (TreeUtilities.isArtificialRoot(fromRhsInstantiation))
				this.generatedTree = fromRhsInstantiation;
			else
				this.generatedTree = TreeUtilities.addArtificialRoot(fromRhsInstantiation);
			
			this.mapOriginalToGenerated = new FromBidirectionalMapValueSetMap<ExtendedNode, ExtendedNode>(rhsInstantiation.getMapOrigToGenerated());
			
			affectedNodes = rhsInstantiation.getAffectedNodes();
		}
		catch(TeEngineMlException e)
		{
			throw new OperationException("Introduction-rule (Extraction-rule) application failed. See nested exception",e);
		}
		
	}

	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		// Do nothing. Already done in generate()
		
	}

	
	protected Rule<Info, BasicNode> rule;
	protected BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree;
	protected ExtendedInfo overrideRelationToArtificialRoot = null;
	
	private static final EdgeInfo overrideRelationArtificialRoot = new DefaultEdgeInfo(new DependencyRelation("", null));

}
