package eu.excitementproject.eop.common.datastructures;

import java.util.Comparator;

/**
 * a Bounded Heap is a Heap with a maxSize.
 * You can insert an element only if there is place in the heap or the new element is bigger
 * than the smallest element, in which case, the smallest element is dropped out.
 * 
 * @author nlp lab legacy code
 *
 * @param <T>
 */
public class BoundedHeap<T> extends Heap<T>
{
	/**
	 * @param iMaxSize
	 * @param iCmp
	 * @throws IllegalArgumentException if Heap() throws it
	 */
	public BoundedHeap(int iMaxSize, Comparator<T> iCmp) throws IllegalArgumentException 
	{
		super(iMaxSize, iCmp);
		
		m_maxSize = iMaxSize;
	}

	/**
	 * @param iMaxSize
	 */
	public BoundedHeap(int iMaxSize) 
	{ 
		this(iMaxSize, null); 
	}
 
	/**
	 * only insert element if there is place in the heap or the new element is bigger
	 * than the smallest element (at the head of the heap), in which case, that smallest element is deleted.
	 *
	 * @see org.BURST.ds.Heap#insert(java.lang.Object)
	 */
	public void insert(T x) 
	{
		if(super.size() < m_maxSize){
			super.insert(x);
		}
		else if(super.compare(x, peek()) > 0){
			super.extract();
			super.insert(x);
		}
	}

	/**
	 * the heap's size limit 
	 */
	private int m_maxSize;
}
