package eu.excitementproject.eop.biutee.rteflow.systems.rtesum;
import java.io.Serializable;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


/**
 * 
 * @author Asher Stern
 * @since Dec 11, 2012
 *
 * @param <I>
 * @param <S>
 */
public class TreeAndIdentifier<I, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = -5879973615435559787L;
	
	public TreeAndIdentifier(S tree, String identifier, String originalSentence)
	{
		super();
		this.tree = tree;
		this.identifier = identifier;
		this.originalSentence = originalSentence;
	}
	
	
	
	public S getTree()
	{
		return tree;
	}
	public String getIdentifier()
	{
		return identifier;
	}
	public String getOriginalSentence()
	{
		return originalSentence;
	}
	
	



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime
				* result
				+ ((originalSentence == null) ? 0 : originalSentence.hashCode());
		result = prime * result + ((tree == null) ? 0 : tree.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeAndIdentifier<?,?> other = (TreeAndIdentifier<?,?>) obj;
		if (identifier == null)
		{
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (originalSentence == null)
		{
			if (other.originalSentence != null)
				return false;
		} else if (!originalSentence.equals(other.originalSentence))
			return false;
		if (tree == null)
		{
			if (other.tree != null)
				return false;
		} else if (!tree.equals(other.tree))
			return false;
		return true;
	}





	private final S tree;
	private final String identifier;
	private final String originalSentence;
}
