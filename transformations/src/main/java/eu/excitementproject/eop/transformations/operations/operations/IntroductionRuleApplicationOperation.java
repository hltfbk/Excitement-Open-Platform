package eu.excitementproject.eop.transformations.operations.operations;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.transformations.datastructures.FromBidirectionalMapValueSetMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreePatcher;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;
import eu.excitementproject.eop.transformations.utilities.rules.ExtendedInfoServices;


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
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree, SyntacticRule<Info, BasicNode> rule,
			BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree, boolean collapseMode) throws OperationException
	{
		super(textTree, hypothesisTree);
		this.rule = rule;
		this.mapLhsToTree = mapLhsToTree;
		this.collapseMode = collapseMode;
	}
	
	protected void setOverrideRelationToArtificialRoot(EdgeInfo overrideRelationToArtificialRoot) throws UnsupportedPosTagStringException
	{
		this.overrideRelationToArtificialRoot = new ExtendedInfo("", new DefaultNodeInfo("", "", 0, null, new DefaultSyntacticInfo(new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.OTHER))), overrideRelationToArtificialRoot, ExtendedNodeConstructor.EMPTY_ADDITIONAL_NODE_INFORMATION);
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
			if (collapseMode)
			{
				TreePatcher<ExtendedInfo, ExtendedNode> treePatcher = new TreePatcher<ExtendedInfo, ExtendedNode>(textTree.getTree(),fromRhsInstantiation,new ExtendedNodeConstructor());
				treePatcher.generate();
				this.generatedTree = treePatcher.getGeneratedTree();
				if (!TreeUtilities.isArtificialRoot(this.generatedTree)) {throw new OperationException("Bug: the generated tree does not have an artificial root.");}
				
				this.mapOriginalToGenerated = treePatcher.getMapOriginalToGenerated();
				BidirectionalMap<ExtendedNode, ExtendedNode> mapOfRhsInstantiation = rhsInstantiation.getMapOrigToGenerated();
				for (ExtendedNode originalNode : mapOfRhsInstantiation.leftSet())
				{
					mapOriginalToGenerated.put(originalNode, mapOfRhsInstantiation.leftGet(originalNode));
				}
			}
			else
			{
				if (TreeUtilities.isArtificialRoot(fromRhsInstantiation))
					this.generatedTree = fromRhsInstantiation;
				else
					this.generatedTree = TreeUtilities.addArtificialRoot(fromRhsInstantiation);

				this.mapOriginalToGenerated = new FromBidirectionalMapValueSetMap<ExtendedNode, ExtendedNode>(rhsInstantiation.getMapOrigToGenerated());
			}

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

	
	protected SyntacticRule<Info, BasicNode> rule;
	protected BidirectionalMap<BasicNode, ExtendedNode> mapLhsToTree;
	protected ExtendedInfo overrideRelationToArtificialRoot = null;
	
	protected final boolean collapseMode;

	
	private static final EdgeInfo overrideRelationArtificialRoot = new DefaultEdgeInfo(new DependencyRelation("", null));

}
