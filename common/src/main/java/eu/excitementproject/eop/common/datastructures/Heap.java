package eu.excitementproject.eop.common.datastructures;

import java.util.Comparator;

/**
 * A heap-based priority queue, without any concurrency control
 * (i.e., no blocking on empty/full states).
 * This class provides the data structure mechanics for BoundedPriorityQueue.
 * <p>
 * The class currently uses a standard array-based heap, as described
 * in, for example, Sedgewick's Algorithms text. All methods
 * are fully synchronized. In the future,
 * it may instead use structures permitting finer-grained locking.
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>]
 * 
 * @author nlp lab legacy code
 **/
public class Heap<T>  
{
	/**
	 * Create a Heap with the given initial capacity and comparator
	 * @param capacity
	 * @param cmp
	 * @throws IllegalArgumentException if capacity less or equal to zero
	 */
  @SuppressWarnings("unchecked")
  public Heap(int capacity, Comparator<T> cmp) throws IllegalArgumentException 
  {
    if (capacity <= 0) throw new IllegalArgumentException();
    nodes_ = (T[])new Object[capacity];
    cmp_ = cmp;
  }

  /**
   * Create a Heap with the given capacity,
   * and relying on natural ordering.
   **/
  public Heap(int capacity) 
  { 
    this(capacity, null); 
  }

 /**
  * insert an element, resize if necessary
  * @param x
  */
  public void insert(T x) 
  {
	// resize if necessary
    if (count_ >= nodes_.length) 
    {
      int newCapacity =  3 * nodes_.length / 2 + 1;
      @SuppressWarnings("unchecked")
      T[] newnodes = (T[])new Object[newCapacity];
      System.arraycopy(nodes_, 0, newnodes, 0, nodes_.length);
      nodes_ = newnodes;
    }

    // insert x
    int k = count_;
    ++count_;
    while (k > 0) {
      int parent = parent(k);
      if (compare(x, nodes_[parent]) < 0) {
        nodes_[k] = nodes_[parent];
        k = parent;
      }
      else break;
    }
    nodes_[k] = x;
  }

 /**
  * Return and remove least element, or null if empty
  * @return
  */
  public T extract() 
  {
	T ret;
    if (count_ < 1) 
    	ret = null;
    else
    {
	    int k = 0; 		 // take element at root;
	    ret = nodes_[k]; // the least element
	    
	    // decrease the heap's size
	    --count_;
	    
	    // move the last element from its old spot
	    T x = nodes_[count_];
	    nodes_[count_] = null;
	    
	    // heapify
	    boolean go = true;
	    while (go) 
	    {
	      // at this point nodes_[k] is empty
	    	
	      int left = left(k);
	      if (left >= count_)
	        go  = false;	// stop, no more children to push up 
	      else 
	      {
	    	// push up the smallest between left and right

	        int right = right(k);
	        int smallerChild = (right >= count_ || compare(nodes_[left], nodes_[right]) < 0)? left : right; 
	        if (compare(x, nodes_[smallerChild]) > 0) 
	        {
	          // push nodes_[smallerChild] up
	          nodes_[k] = nodes_[smallerChild];
	          k = smallerChild;
	        }
	        else 
	        	go = false;	// stop, place x here
	      }
	    }
	    nodes_[k] = x;
    }
    return ret;
  }

  /**
  * Return least element without removing it, or null if empty
  * @return
  */
  public T peek() 
  {
    if (count_ > 0) 
      return nodes_[0];
    else
      return null;
  }

  /**
  * Return number of elements
  * @return
  */
  public int size() 
  {
    return count_;
  }
  
  /**
  *  remove all elements
  */
  public void clear() 
  {
    for (int i = 0; i < count_; ++i)
      nodes_[i] = null;
    count_ = 0;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  	/**
   	* perform element comparisons using comparator or natural ordering
  	* @param a
  	* @param b
  	* @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
  	*/
   @SuppressWarnings("unchecked")
   protected int compare(T a, T b) 
   {
     int ret;
 	  
 	if (cmp_ == null) 
 	  ret = ((Comparable<T>)a).compareTo(b);
 	else
 		ret = cmp_.compare(a, b);
 	return ret;
   }

   // indexes of heap parents and children
   protected final int parent(int k) { return (k - 1) / 2; }
   protected final int left(int k)   { return 2 * k + 1; }
   protected final int right(int k)  { return 2 * (k + 1); }

   	/**
	 * the tree nodes, packed into an array
	 */
	protected T[] nodes_;

	/**
	 * number of used slots
	 */
	protected int count_ = 0; 

	/**
	 * for ordering
	 */
	protected final Comparator<T> cmp_;
}
