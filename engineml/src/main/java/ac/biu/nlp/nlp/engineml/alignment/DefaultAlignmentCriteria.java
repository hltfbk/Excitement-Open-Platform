package ac.biu.nlp.nlp.engineml.alignment;
import static ac.biu.nlp.nlp.engineml.rteflow.systems.Constants.USE_ADVANCED_EQUALITIES;
import ac.biu.nlp.nlp.engineml.generic.rule_compiler.Constants;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.AdvancedEqualities;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.ContentAncestorSetter;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.Equalities;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

/**
 * The actual {@link AlignmentCriteria} used by the system.
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
