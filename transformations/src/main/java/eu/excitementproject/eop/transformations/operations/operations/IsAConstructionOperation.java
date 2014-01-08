package eu.excitementproject.eop.transformations.operations.operations;
import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.transformations.datastructures.FromBidirectionalMapValueSetMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.IsASpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.EasyFirst_IsA_Constructor;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreePatcher;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * 
 * TODO implement also for minipar
 * 
 * @author Asher Stern
 * @since Sep 9, 2012
 *
 */
@LanguageDependent("english")
@ParserSpecific("easyfirst")
public class IsAConstructionOperation extends GenerationOperationForExtendedNode
{

	public IsAConstructionOperation(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			IsASpecification isASpecification,
			PARSER parser, boolean collapseMode)
			throws OperationException
	{
		super(textTree, hypothesisTree);
		this.isASpecification = isASpecification;
		this.parser = parser;
		if (null==this.parser) throw new OperationException("The given parser configuration is null.");
		this.collapseMode = collapseMode;
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		if (!parser.equals(PARSER.EASYFIRST))
		{
			throw new OperationException("IsAConstructionOperation is not implemented for minipar.");
		}
		try
		{
			EasyFirst_IsA_Constructor isAConstructor = new EasyFirst_IsA_Constructor(isASpecification.getEntity1(), isASpecification.getEntity2());
			isAConstructor.construct();
			ExtendedNode theIsA_generated = isAConstructor.getGeneratedTree();
			if (collapseMode)
			{
				TreePatcher<ExtendedInfo, ExtendedNode> patcher = new TreePatcher<ExtendedInfo, ExtendedNode>(textTree.getTree(),theIsA_generated, new ExtendedNodeConstructor());
				patcher.generate();
				generatedTree = patcher.getGeneratedTree();
				if (!TreeUtilities.isArtificialRoot(generatedTree)) {throw new OperationException("Bug - the generated tree does not have an artificial root.");}
				
				mapOriginalToGenerated = patcher.getMapOriginalToGenerated();
				BidirectionalMap<ExtendedNode, ExtendedNode> mapIsAConstructorOriginalToGenerated = isAConstructor.getBidiMapOriginalToGenerated();
				for (ExtendedNode originalNode : mapIsAConstructorOriginalToGenerated.leftSet())
				{
					mapOriginalToGenerated.put(originalNode, mapIsAConstructorOriginalToGenerated.leftGet(originalNode));
				}
			}
			else
			{
				if (!TreeUtilities.isArtificialRoot(theIsA_generated)) // likely - it is not. It's an anomaly if it is.
				{
					generatedTree = TreeUtilities.addArtificialRoot(theIsA_generated);
				}
				else
				{
					generatedTree =	theIsA_generated;
				}
				
				mapOriginalToGenerated = new FromBidirectionalMapValueSetMap<ExtendedNode, ExtendedNode>(isAConstructor.getBidiMapOriginalToGenerated());
			}
			
			affectedNodes = isAConstructor.getAffectedNodes();
		}
		catch (UnsupportedPosTagStringException e)
		{
			throw new OperationException("Problem when creating tree. See nested Exception",e);
		}
	}

	@Override
	protected void generateMapOriginalToGenerated() throws OperationException
	{
		// Do nothing. Already done in generateTheTree()
	}




	private IsASpecification isASpecification;
	private final PARSER parser;
	private final boolean collapseMode;
}
