package eu.excitementproject.eop.transformations.operations.operations;
import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.transformations.datastructures.FromBidirectionalMapValueSetMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.IsASpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.ParserSpecificConfigurations;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.EasyFirst_IsA_Constructor;

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
			ParserSpecificConfigurations.PARSER parser)
			throws OperationException
	{
		super(textTree, hypothesisTree);
		this.isASpecification = isASpecification;
		this.parser = parser;
		if (null==this.parser) throw new OperationException("The given parser configuration is null.");
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		if (!parser.equals(ParserSpecificConfigurations.PARSER.EASYFIRST))
		{
			throw new OperationException("IsAConstructionOperation is not implemented for minipar.");
		}
		try
		{
			EasyFirst_IsA_Constructor isAConstructor = new EasyFirst_IsA_Constructor(isASpecification.getEntity1(), isASpecification.getEntity2());
			isAConstructor.construct();
			generatedTree = isAConstructor.getGeneratedTree();
			mapOriginalToGenerated = new FromBidirectionalMapValueSetMap<ExtendedNode, ExtendedNode>(isAConstructor.getBidiMapOriginalToGenerated());
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
	private ParserSpecificConfigurations.PARSER parser;
}
