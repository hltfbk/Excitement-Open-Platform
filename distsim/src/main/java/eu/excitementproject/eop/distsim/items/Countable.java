package eu.excitementproject.eop.distsim.items;


/**
 * Basic counting functionality.
 *
 * @author Meni Adler
 * @since 21/03/2012
 *
 * 
 */

public interface Countable {
	/**
	 * Get the counting of the object
	 * 
	 * @return a count of the object.
	 */
	double getCount() throws InvalidCountException;
	
	/**
	 * Adds the given value to the counting
	 * 
	 * @param val a value to be added to the counting
	 */
	void incCount(double val) throws InvalidCountException;
	
	/**
	 * Set the count to the given parameter
	 * 
	 * @param count a new count to be set
	 */
	void setCount(double count);
}
