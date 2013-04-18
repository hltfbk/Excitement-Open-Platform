package eu.excitementproject.eop.distsim.items;

/**
 * Defines an object which is assigned  to a unique id
 * 
 * @author Meni Adler
 * @since 22/05/2012
 *
 * 
 */
public interface Identifiable {
	/**
	 * Get the id of the object
	 * 
	 * @return a unique id for this object
	 * @throws InvalidIDException
	 */
	int getID() throws InvalidIDException;
	
	/**
	 * Set an ID for the object
	 * 
	 * @param id a unique id
	 */
	void setID(int id);
}
