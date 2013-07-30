package eu.excitementproject.eop.transformations.alignment;
import static eu.excitementproject.eop.transformations.utilities.Constants.USE_ADVANCED_EQUALITIES;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.generic.rule_compiler.Constants;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.AdvancedEqualities;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.ContentAncestorSetter;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.Equalities;

/**
 * The actual {@link AlignmentCriteria} used by the BIUTEE system.
 * <BR>
 * Roughly, it requires from two nodes to be aligned that their lemmas are
 * equal, their parts-of-speech are equal, their predicate-truth values are equal
 * (depends on some constants in {@link Constants}), and that they have similar
 * "content ancestor" as described in {@link ContentAncestorSetter}.
 * Two edges are aligned if they have the same relation (the same edge-label).
 * 
 * @author Asher Stern
 * @since May 31, 2012
 *
 * @param <T>
 * @param <S>
 */
public class DefaultAlignmentCriteria extends AbstractAlignmentCriteria<ExtendedInfo, ExtendedNode>
{
	@Override
	public boolean nodesSimilar(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			ExtendedNode textNode, ExtendedNode hypothesisNode)
	{
		boolean ret = false;
		if (USE_ADVANCED_EQUALITIES)
		{
			ret = AdvancedEqualities.nodesSimilarContents(textNode.getInfo(),hypothesisNode.getInfo());
		}
		else
		{
			ret = Equalities.areEqualNodes(textNode.getInfo(),hypothesisNode.getInfo());
		}
		return ret;
	}

	@Override
	public boolean nodesAligned(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			ExtendedNode textNode, ExtendedNode hypothesisNode)
	{
		boolean ret = false;
		if (USE_ADVANCED_EQUALITIES)
		{
			ret = AdvancedEqualities.nodesEqual(textNode.getInfo(), hypothesisNode.getInfo());
		}
		else
		{
			ret = Equalities.areEqualNodes(textNode.getInfo(), hypothesisNode.getInfo());
		}
		return ret;
	}

	@Override
	public boolean edgesAligned(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			ExtendedNode textEdge, ExtendedNode hypothesisEdge)
	{
		return InfoGetFields.getRelation(textEdge.getInfo()).equalsIgnoreCase(
				InfoGetFields.getRelation(hypothesisEdge.getInfo())
				);
	}
	
}
