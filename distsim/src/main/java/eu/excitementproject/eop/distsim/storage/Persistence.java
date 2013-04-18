package eu.excitementproject.eop.distsim.storage;

/**
 * Defines persistence data
 * 
 * @author Meni Adler
 * @since 01/07/2012
 *
 */
public interface Persistence {
	
	/**
	 * Save the state of the object to a given persistence device
	 * @param devices one or more devices to store the state of the persistent object
	 */
	void saveState(PersistenceDevice... devices) throws SavingStateException;
	
	
	/**
	 * Load the state of the object from a given persistence device
	 * @param devices one or more devices, contain the data to be loaded
	 */
	void loadState(PersistenceDevice... devices) throws LoadingStateException;
}
