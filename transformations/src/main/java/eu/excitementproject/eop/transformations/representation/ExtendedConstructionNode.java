package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;

/**
 * 
 * @author Asher Stern
 * 
 *
 */
public final class ExtendedConstructionNode extends AbstractConstructionNode<ExtendedInfo, ExtendedConstructionNode>
{
	private static final long serialVersionUID = -2510500297062273175L;

	public ExtendedConstructionNode(ExtendedInfo info)
	{
		super(info);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [info=" + info + "]";
	}
	
	
	
}
