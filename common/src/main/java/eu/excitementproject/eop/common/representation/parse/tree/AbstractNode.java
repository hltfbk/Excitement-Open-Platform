package eu.excitementproject.eop.common.representation.parse.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 * Represents a node in a parse tree.
 * <P>
 * Important notes:<BR>
 * <OL>
 * <LI>{@linkplain AbstractNode} is not immutable.</LI>
 * <LI>No class that inherits {@linkplain AbstractNode}
 * may implement <code>equals()</code> and <code>hashCode()</code>
 * methods. Implementing those methods breaks the internal logic.
 * The assumptions is that a node is unique, and not equal any other
 * node, even though they may have the same contetns.
 * </LI>
 * <LI><B>The information inside the node (the generic
 * parameter <code>T</code>) must be immutable!</B></LI>
 * <LI>The information type (the generic parameter T) must implement Serializable</LI>
 * </OL>
 * 
 * @author Asher Stern
 *
 * @param <T> The information type of the node. 
 * @param <S> The type of the node itself (this insures that the
 * children have the same type of their parent).
 * 
 * @see AbstractNodeUtils
 * @see TreeAndParentMap
 * 
 */
public abstract class AbstractNode<T,S extends AbstractNode<T,S>> implements Serializable
{
	private static final long serialVersionUID = -7225401510395672952L;

	protected AbstractNode(T info)
	{
		this.info = info;
	}
	
	public T getInfo()
	{
		return info;
	}
	
	public void addChild(S child)
	{
		if(sealed) {throw new SealedTreeViolationException();}
		if (this.children==null)
			this.children = new ArrayList<S>();
		
		this.children.add(child);
	}
	
	public List<S> getChildren()
	{
		if (!sealed)
		{
			return children;
		}
		else
		{
			return Collections.unmodifiableList(children);
		}
	}
	
	/**
	 * Seals the tree. Any attempt to change this tree would raise an exception.
	 */
	public void seal()
	{
		this.sealed = true;
	}
	
	
	public boolean hasChildren()
	{
		if (children!=null)
		{
			if (children.size()>0)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public S getAntecedent()
	{
		return antecedent;
	}


	public void setAntecedent(S antecedent)
	{
		if(sealed) {throw new SealedTreeViolationException();}
		this.antecedent = antecedent;
	}

	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AbstractNode [info=" + getInfo() + "]";
	}





	protected T info;
	protected ArrayList<S> children;
	
	// This field should not be used for co-reference.
	// Its purpose is for those nodes that it was decided by the parser
	// that they are actually refer to another node.
	protected S antecedent;
	
	protected boolean sealed=false;
}
