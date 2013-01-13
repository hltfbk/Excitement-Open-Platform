package ac.biu.nlp.nlp.engineml.representation;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.LeastCommonAncestor;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeCopier;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * Represents a parse tree node in which each node has {@link ExtendedInfo}, which
 * is like {@link Info} plus {@link AdditionalNodeInformation}.
 * <P>
 * This node is used in the system. All the parse-trees are represented using
 * {@link ExtendedNode}.
 * <P>
 * Note that all of the algorithms for parse-trees are available for this node: see
 * for example {@link AbstractNodeUtils}, {@link TreeCopier} and {@link LeastCommonAncestor}.
 * <P>
 * @see BasicNode
 * @see AbstractNode
 * @see ExtendedNodeConstructor
 * 
 * 
 * @author Asher Stern
 * 
 *
 */
public class ExtendedNode extends AbstractNode<ExtendedInfo, ExtendedNode>
{
	private static final long serialVersionUID = -8452545495868398800L;

	public ExtendedNode(ExtendedInfo info)
	{
		super(info);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtendedNode [info=" + info + "]";
	}
	
	
}
