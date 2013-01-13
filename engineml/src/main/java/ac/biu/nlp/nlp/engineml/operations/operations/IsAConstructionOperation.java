package ac.biu.nlp.nlp.engineml.operations.operations;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import ac.biu.nlp.nlp.engineml.datastructures.FromBidirectionalMapValueSetMap;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.IsASpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.EasyFirst_IsA_Constructor;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.ParserSpecificConfigurations;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

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
			IsASpecification isASpecification)
			throws OperationException
	{
		super(textTree, hypothesisTree);
		this.isASpecification = isASpecification;
	}

	@Override
	protected void generateTheTree() throws OperationException
	{
		if (!ParserSpecificConfigurations.getParserMode().equals(ParserSpecificConfigurations.PARSER.EASYFIRST))
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
}
