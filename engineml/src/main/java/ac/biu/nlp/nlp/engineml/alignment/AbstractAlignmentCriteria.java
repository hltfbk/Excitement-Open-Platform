package ac.biu.nlp.nlp.engineml.alignment;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * 
 * @author Asher Stern
 * @since May 31, 2012
 *
 * @param <T>
 * @param <S>
 */
public abstract class AbstractAlignmentCriteria<T, S extends AbstractNode<T, S>> implements AlignmentCriteria<T, S>
{
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.alignment.AlignmentCriteria#triplesAligned(ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap, ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap, ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode, ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode)
	 */
	public boolean triplesAligned(TreeAndParentMap<T, S> textTree, TreeAndParentMap<T, S> hypothesisTree, S textTriple, S hypothesisTriple)
	{
		boolean ret = false;
		if (
				(null==textTree.getParentMap().get(textTriple))
				&&
				(null==hypothesisTree.getParentMap().get(hypothesisTriple))
				)
		{
			ret = nodesAligned(textTree,hypothesisTree,textTriple,hypothesisTriple);
		}
		else if (
				(null==textTree.getParentMap().get(textTriple))
				||
				(null==hypothesisTree.getParentMap().get(hypothesisTriple))
				)
		{
			ret = false;
		}
		else
		{
			S textParent = textTree.getParentMap().get(textTriple);
			S hypothesisParent = hypothesisTree.getParentMap().get(hypothesisTriple);
			ret =
					nodesAligned(textTree, hypothesisTree, textTriple, hypothesisTriple)
					&&
					edgesAligned(textTree, hypothesisTree, textTriple, hypothesisTriple)
					&&
					nodesAligned(textTree, hypothesisTree, textParent, hypothesisParent)
					;
		}
		return ret;
	}

}
