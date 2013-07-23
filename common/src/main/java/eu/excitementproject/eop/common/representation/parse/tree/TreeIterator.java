package eu.excitementproject.eop.common.representation.parse.tree;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Provides a <code>java.util.Iterator</code> over a tree.<BR>
 * To have the tree as an Iterable
 * (such that you can use, e.g., <BR><code>for(BasicNode node : tree)</code>)<BR>
 * use the static method {@link #iterableTree(AbstractNode)}
 * <P>
 * The iterator iterates over the tree's nodes in a preorder-DFS, which means that
 * it prints the root, and then recursively its children in depth-first order.
 * 
 * @author Asher Stern
 * @since Jul 18, 2013
 *
 * @param <T>
 * @param <S>
 */
public class TreeIterator<T,S extends AbstractNode<T,S>> implements Iterator<S>
{
	public static <T,S extends AbstractNode<T,S>> Iterable<S> iterableTree(final S tree)
	{
		return new Iterable<S>()
		{
			@Override
			public Iterator<S> iterator()
			{
				return new TreeIterator<T,S>(tree);
			}
		};
	}
	
	public TreeIterator(S tree)
	{
		stack = new Stack<S>();
		stack.push(tree);
	}
	
	@Override
	public boolean hasNext()
	{
		return !stack.isEmpty();
	}
	
	

	@Override
	public S next()
	{
		if (stack.isEmpty())
		{
			throw new NoSuchElementException();
		}
		S ret = stack.pop();
		if (ret.getChildren()!=null)
		{
			ListIterator<S> childrenIterator = ret.getChildren().listIterator(ret.getChildren().size());
			while (childrenIterator.hasPrevious())
			{
				stack.push(childrenIterator.previous());
			}
		}
		return ret;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
	
	
	private Stack<S> stack;
}
