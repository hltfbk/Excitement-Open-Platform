package eu.excitementproject.eop.common.representation.parse.tree;

/**
 * This class makes the information in a node changeable.
 * Of course, <B>the information is immutable</B>, but it can be
 * just replaced with a new information.
 * @author Asher Stern
 *
 * @param <T>
 * @param <S>
 */
public class AbstractConstructionNode<T,S extends AbstractConstructionNode<T,S>> extends AbstractNode<T, S>
{
	private static final long serialVersionUID = 945481896438133559L;

	protected AbstractConstructionNode(T info)
	{
		super(info);
	}
	
	public void setInfo(T info)
	{
		this.info = info;
	}
}
