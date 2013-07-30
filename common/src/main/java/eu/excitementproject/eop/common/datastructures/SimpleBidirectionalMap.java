package eu.excitementproject.eop.common.datastructures;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;


/**
 * An implementation of {@linkplain BidirectionalMap} that uses two
 * <code>java.util.Map</code>s as underlying maps, and keeps them
 * compatible with each other to represent exactly one-to-one mapping.
 * <P>
 * <B>Not thread safe!</B>
 * <BR>
 * <code>null</code> values behavior is undefined (never tested).
 * 
 * @author Asher Stern
 *
 * @param <L>
 * @param <R>
 */
public class SimpleBidirectionalMap<L,R> implements BidirectionalMap<L, R>
{
	private static final long serialVersionUID = -5046454142494454879L;

	//////////////////// PUBLIC PART ///////////////////////////////
	
	public SimpleBidirectionalMap()
	{
		
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#clear()
	 */
	public void clear()
	{
		this.mapLeftToRight.clear();
		this.mapRightToLeft.clear();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this.mapLeftToRight.isEmpty();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#leftContains(java.lang.Object)
	 */
	public boolean leftContains(L left)
	{
		return this.mapLeftToRight.containsKey(left);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#leftRemove(java.lang.Object)
	 */
	public void leftRemove(L left)
	{
		if (mapLeftToRight.containsKey(left))
		{
			R itsRight = mapLeftToRight.get(left);
			mapLeftToRight.remove(left);
			mapRightToLeft.remove(itsRight);
		}
		
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#leftSet()
	 */
	public ImmutableSet<L> leftSet()
	{
		return new ImmutableSetWrapper<L>(mapLeftToRight.keySet());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#put(java.lang.Object, java.lang.Object)
	 */
	public void put(L left, R right)
	{
		try
		{
			if (mapLeftToRight.containsKey(left))
			{
				R oldRight = mapLeftToRight.get(left);
				mapRightToLeft.remove(oldRight);
			}
		} catch(Exception e){}
		
		try
		{
			if (mapRightToLeft.containsKey(right))
			{
				L oldLeft = mapRightToLeft.get(right);
				mapLeftToRight.remove(oldLeft);
			}
		} catch(Exception e){}
		
		mapLeftToRight.put(left,right);
		mapRightToLeft.put(right,left);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#rightContains(java.lang.Object)
	 */
	public boolean rightContains(R right)
	{
		return mapRightToLeft.containsKey(right);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#rightRemove(java.lang.Object)
	 */
	public void rightRemove(R right)
	{
		if (mapRightToLeft.containsKey(right))
		{
			L oldLeft = mapRightToLeft.get(right);
			mapLeftToRight.remove(oldLeft);
			mapRightToLeft.remove(right);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#rightSet()
	 */
	public ImmutableSet<R> rightSet()
	{
		return new ImmutableSetWrapper<R>(mapRightToLeft.keySet());
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#size()
	 */
	public int size()
	{
		return mapLeftToRight.size();
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#leftGet(java.lang.Object)
	 */
	public R leftGet(L left)
	{
		return mapLeftToRight.get(left);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.BidirectionalMap#rightGet(java.lang.Object)
	 */
	public L rightGet(R right)
	{
		return mapRightToLeft.get(right);
	}

	
	/////////////// equals() and hashCode() implementations //////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mapLeftToRight == null) ? 0 : mapLeftToRight.hashCode());
		result = prime * result
				+ ((mapRightToLeft == null) ? 0 : mapRightToLeft.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		SimpleBidirectionalMap other = (SimpleBidirectionalMap) obj;
		if (mapLeftToRight == null) {
			if (other.mapLeftToRight != null)
				return false;
		} else if (!mapLeftToRight.equals(other.mapLeftToRight))
			return false;
		if (mapRightToLeft == null) {
			if (other.mapRightToLeft != null)
				return false;
		} else if (!mapRightToLeft.equals(other.mapRightToLeft))
			return false;
		return true;
	}
	
	///////////////////// PROTECTED PART //////////////////////////

	
	protected Map<L,R> mapLeftToRight = new LinkedHashMap<L, R>();
	protected Map<R,L> mapRightToLeft = new LinkedHashMap<R, L>();
}

