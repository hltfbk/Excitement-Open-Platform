package eu.excitementproject.eop.common.utilities;

import java.util.HashMap;
import java.util.Map;


/**
 * <B>Caution: There is a bug in this class. I don't know where and why.
 * Use another {@link Cache} implementation. Sorry. </B><BR>
 * Implementation of {@linkplain Cache} using a <code>java.util.Map</code>
 * and a linked list.
 * <P>
 * The policy is to remove the oldest element when limit was reached
 * and {@link #put(K, V)} is called.
 * The oldest element is the one which the last operation that was done on it
 * was done earlier than the last operations that were done on any other element.
 * <P>
 * A new element is inserted to the beginning of the list.
 * A {@link #get(K)} operation will bring the key to the beginning of the list.
 * A {@link #put(K, V)} operation will remove the element in the list's tail. If
 * the key-value pair to be put are already exist in the cache, then they just
 * moved to the beginning of the list.
 *
 * 
 * <P>
 * <B>NOT THREAD SAFE!</B>
 * 
 * 
 * @author Asher Stern
 *
 * @param <K>
 * @param <V>
 */
@Deprecated
public class ListMapCache<K, V> implements Cache<K, V>
{
	//////////////////////// PUBLIC PART //////////////////////////
	
	public static final int MINIMUM_CAPACITY = 10;
	
	/**
	 * Constructs a <code>Cache</code> with capacity of at
	 * least "<code>capacity</code>".
	 * 
	 * @param capacity The capacity of the cache will not be smaller then
	 * the value specified.
	 */
	public ListMapCache(int capacity)
	{
		if (MINIMUM_CAPACITY>capacity)
			this.capacity = MINIMUM_CAPACITY;
		else
			this.capacity = capacity;
		
		map = new HashMap<K, LinkedListNode<K,V>>();
	}



	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#containsKey(java.lang.Object)
	 */
	public boolean containsKey(K key)
	{
		return map.containsKey(key);
	}

	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#get(java.lang.Object)
	 */
	public V get(K key)
	{
		System.out.println("get");
		System.out.println("map.size = "+map.size());
		int counter = 0;
		if (head != null)
		{
			LinkedListNode<K,V> e = head;
			while (e != null)
			{
				++counter;
				e = e.getNext();
			}
		}
		System.out.println("counter = "+ counter);

		if (!map.containsKey(key)) return null;
		else
		{
			System.out.println("element in map");
			LinkedListNode<K, V> element = map.get(key);
			if (element==head) System.out.println("element is head.");
			if (element==tail) System.out.println("element is tail.");
			
			if (null==element.getPrev()) ;
			else
			{
				System.out.println("element has prev.");
				if (element==tail)
					tail=element.getPrev();
				element.getPrev().setNext(element.getNext());
				element.setPrev(null);
				element.setNext(head);
				head.setPrev(element);
				head = element;
			}
			return element.getValue();
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.general.Cache#put(java.lang.Object, java.lang.Object)
	 */
	public void put(K key, V value)
	{
		System.out.println("put");
		System.out.println("map.size = "+map.size());
		int counter = 0;
		if (head != null)
		{
			LinkedListNode<K,V> e = head;
			while (e != null)
			{
				++counter;
				e = e.getNext();
			}
		}
		System.out.println("counter = "+ counter);
		if (map.containsKey(key))
		{
			LinkedListNode<K,V> element = map.get(key);
			if (element.getPrev()==null) ;
			else
			{
				element.getPrev().setNext(element.getNext());
				if (tail==element)
					tail=element.getPrev();
				element.setPrev(null);
				element.setNext(head);
				head.setPrev(element);
				head = element;
			}
		}
		else
		{
			if (currentSize>=capacity)
			{
				tail.getPrev().setNext(null);
				map.remove(tail.getKey());
				tail = tail.getPrev();
				--currentSize;
			}
			++currentSize;
			LinkedListNode<K,V> element = new LinkedListNode<K,V>(key, value);
			map.put(key, element);
			if (head!=null)
			{
				head.setPrev(element);
				element.setNext(head);
				element.setPrev(null);
				head = element;
			}
			else // head == null
			{
				element.setNext(null);
				element.setPrev(null);
				head = element;
				tail = element;
			}
		}
		
	}
	
	
	///////////////////// PROTECTED AND PRIVATE PART ////////////////////
	
	protected Map<K,LinkedListNode<K,V>> map;
	protected LinkedListNode<K,V> head = null;
	protected LinkedListNode<K,V> tail = null;
	
	protected int capacity;
	protected int currentSize = 0;
	
	

	
	private static final class LinkedListNode<K,V>
	{
		private K key;
		private V value;
		private LinkedListNode<K,V> prev;
		private LinkedListNode<K,V> next;
		
		public LinkedListNode(K key, V value)
		{this.key = key; this.value = value;prev=null;next=null;}
		
		public LinkedListNode<K,V> getPrev(){return prev;}
		public LinkedListNode<K,V> getNext(){return next;}
		public void setPrev(LinkedListNode<K,V> prev){this.prev = prev;}
		public void setNext(LinkedListNode<K,V> next){this.next = next;}
		public V getValue(){return value;}
		public K getKey(){return key;}
	}
	
	
	 

}
