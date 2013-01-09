package eu.excitementproject.eop.common.datastructures;

import java.io.Serializable;
import java.util.Map;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;



/**
 * A one-to-one mapping of elements of type <code>L</code> to elements of
 * type <code>R</code>.
 * <BR>
 * Each element of type <code>L</code> in the {@linkplain BidirectionalMap} has
 * exactly one element of type <code>R</code> mapped to it.
 * <BR>
 * Each element of type <code>R</code> in the {@linkplain BidirectionalMap} has
 * exactly one element of type <code>L</code> mapped to it.
 * 
 * <B>Not thread safe, unless explicitly specified.</B>
 * 
 * @author Asher Stern
 * 
 * @see <code>lava.util.Map</code>
 *
 * @param <L>
 * @param <R>
 */
public interface BidirectionalMap<L,R> extends Serializable
{
	
	/**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     *
	 */
	public void clear();
	
	/**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * type <code>L</code> element.
     * 
     * @param left element of type <code>L</code> whose presence in this
     * map is to be tested.
     * 
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         element
	 */
	public boolean leftContains(L left);
	

	/**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * type <code>R</code> element.
     * 
     * @param right element of type <code>R</code> whose presence in this
     * map is to be tested.
     * 
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         element
	 */
	public boolean rightContains(R right);

	/**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a {@linkplain BidirectionalMap}
     * and the two bidirectional-maps represent the same mappings.
 	 * 
	 * @param o object to be compared for equality with this map
	 * @return <tt>true</tt> if the specified object is equal to this map
	 */
	public boolean equals(Object o);
	
	/**
     * Returns the hash code value for this map.
     * The implementor should take care that if <tt>m1.equals(m2)</tt>
     * then <tt>m1.hashCode()==m2.hashCode()</tt> for any two bidirectional-maps
     * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * @return the hash code value for this map
     * @see Map.Entry#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
	 */
	public int hashCode();
	
	/**
	 * Returns the right-element mapped to the given left-element. 
	 * @param left a left element
	 * @return The right-element which is mapped to the given left-element.
	 */
	public R leftGet(L left);
	
	/**
	 * Returns the left-element mapped to the given right-element. 
	 * @param right a right element
	 * @return The left-element which is mapped to the given right-element.
	 */
	public L rightGet(R right);
	
	
	/**
     * Returns <tt>true</tt> if this map contains no mappings.
     *
     * @return <tt>true</tt> if this map contains no mappings
	 */
	public boolean isEmpty();
	
	/**
	 * Returns a set of the left elements contained in the map.
	 * @return a set of the left elements contained in the map.
	 */
	public ImmutableSet<L> leftSet();
	
	/**
	 * Returns a set of the right elements contained in the map.
	 * @return a set of the right elements contained in the map.
	 */
	public ImmutableSet<R> rightSet();
	
	
	/**
	 * Adds a one-to-one mapping between the given type <code>L</code> element
	 * and the type <code>R</code> element.
     * If the map previously contained a mapping for
     * either the left element or the right element, the old maping(s) are
     * removed.
     *
	 * @param left a type <code>L</code> element
	 * @param right a type <code>R</code> element
	 */
	public void put(L left, R right);

	/**
	 * Removes the mapping of the given type <code>L</code> element.
	 * Since it is one-to-one mapping, the type <code>R</code> element that was
	 * associated to the given type <code>L</code> element is removed from
	 * the map as well.
	 * @param left a type <code>L</code> element to be removed from the map.
	 */
	public void leftRemove (L left);
	
	/**
	 * Removes the mapping of the given type <code>R</code> element.
	 * Since it is one-to-one mapping, the type <code>L</code> element that was
	 * associated to the given type <code>R</code> element is removed from
	 * the map as well.
	 * @param right a type <code>R</code> element to be removed from the map.
	 */
	public void rightRemove (R right);
	
	/**
	 * Returns the number of mapping contained in the map, which is also the
	 * number of type <code>L</code> elements contained in the map, which is also
	 * the number of type <code>L</code> elements contained in the map.
	 * 
	 * @return the number of mappings in the map.
	 */
	public int size();
	
}
