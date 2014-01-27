package eu.excitementproject.eop.transformations.operations.finders.auxiliary;

import java.util.Collections;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;


/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2013
 *
 * @param <L>
 * @param <R>
 */
public class SingleItemBidirectionalMap<L,R> implements BidirectionalMap<L, R>
{
	private static final long serialVersionUID = 8966555483439681603L;
	
	public SingleItemBidirectionalMap(L left, R right)
	{
		this.left = left;
		this.right = right;
	}
	

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean leftContains(L left)
	{
		return this.left.equals(left);
	}

	@Override
	public boolean rightContains(R right)
	{
		return this.right.equals(right);
	}

	@Override
	public R leftGet(L left)
	{
		if (this.left.equals(left))
		{
			return this.right;
		}
		else
		{
			return null;
		}
	}

	@Override
	public L rightGet(R right)
	{
		if (this.right.equals(right))
		{
			return this.left;
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public ImmutableSet<L> leftSet()
	{
		return new ImmutableSetWrapper<>(Collections.singleton(this.left));
	}

	@Override
	public ImmutableSet<R> rightSet()
	{
		return new ImmutableSetWrapper<>(Collections.singleton(this.right));
	}

	@Override
	public void put(L left, R right)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void leftRemove(L left)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void rightRemove(R right)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int size()
	{
		return 1;
	}

	
	private final L left;
	private final R right;
}
