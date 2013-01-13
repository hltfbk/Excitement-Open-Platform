package eu.excitementproject.eop.common.representation.parse.tree;

import java.io.Serializable;
import java.util.Map;

/**
 * Encapsulates tree and Its parent-map.
 * A parent-map is a map from each node to its parent.
 * 
 * @see AbstractNodeUtils#parentMap(ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode)
 * 
 * @author Asher Stern
 * @since Dec 29, 2010
 *
 */
public class TreeAndParentMap<I, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 9166289739886239513L;
	
	@SuppressWarnings("serial")
	public static class TreeAndParentMapException extends Exception
	{
		public TreeAndParentMapException(String message)
		{super(message);}
	}
	
	public TreeAndParentMap(S tree, Map<S, S> parentMap) throws TreeAndParentMapException
	{
		super();
		if ((null==tree)||(null==parentMap)) throw new TreeAndParentMapException("null");
		this.tree = tree;
		this.parentMap = parentMap;
	}
	
	public TreeAndParentMap(S tree) throws TreeAndParentMapException
	{
		super();
		if (null==tree) throw new TreeAndParentMapException("null");
		this.tree = tree;
		this.parentMap = AbstractNodeUtils.parentMap(tree);
	}
	
	public S getTree()
	{
		return tree;
	}

	public Map<S, S> getParentMap()
	{
		return parentMap;
	}
	
	
	@Override
	public int hashCode()
	{
		return this.tree.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (null==obj)return false;
		if (obj instanceof TreeAndParentMap)
		{
			return this.tree.equals(((TreeAndParentMap<?,?>)obj).tree);	
		}
		else
			return false;
		
	}

	private final S tree;
	private final Map<S, S> parentMap;
}
