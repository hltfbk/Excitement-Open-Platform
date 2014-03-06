package eu.excitementproject.eop.common.datastructures;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;


/**
 * A reverse of an existing {@link BidirectionalMap}.
 * Given a {@link BidirectionalMap} ("real map"), for each <r,l> in the "real map", this map
 * will contain <l,r>.
 * 
 * @author Asher Stern
 * @since Feb 14, 2011
 *
 * @param <L>
 * @param <R>
 */
public class FlippedBidirectionalMap<L, R> implements BidirectionalMap<L, R>
{
	private static final long serialVersionUID = -9021902477543898759L;


	public FlippedBidirectionalMap(BidirectionalMap<R, L> realMap)
	{
		this.realMap = realMap;
	}

	public void clear()
	{
		realMap.clear();
	}

	public boolean leftContains(L left)
	{
		return realMap.rightContains(left);
	}

	public boolean rightContains(R right)
	{
		return realMap.leftContains(right);
	}

	public R leftGet(L left)
	{
		return realMap.rightGet(left);
	}

	public L rightGet(R right)
	{
		return realMap.leftGet(right);
	}

	public boolean isEmpty()
	{
		return realMap.isEmpty();
	}

	public ImmutableSet<L> leftSet()
	{
		return realMap.rightSet();
	}

	public ImmutableSet<R> rightSet()
	{
		return realMap.leftSet();
	}

	public void put(L left, R right)
	{
		realMap.put(right, left);
	}

	public void leftRemove(L left)
	{
		realMap.rightRemove(left);
	}

	public void rightRemove(R right)
	{
		realMap.leftRemove(right);
	}

	public int size()
	{
		return realMap.size();
	}
	

	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((realMap == null) ? 0 : realMap.hashCode());
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
		FlippedBidirectionalMap<?,?> other = (FlippedBidirectionalMap<?,?>) obj;
		if (realMap == null)
		{
			if (other.realMap != null)
				return false;
		} else if (!realMap.equals(other.realMap))
			return false;
		return true;
	}




	protected BidirectionalMap<R, L> realMap;
}
