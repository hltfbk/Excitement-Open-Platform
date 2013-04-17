package eu.excitementproject.eop.distsim.builders.cooccurrence;


import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;

/**
 * The TextUnitDataStructureFactory defines a factory for construction of text unit storage 
 * 
 * @author Meni Adler
 * @since 26/12/2012
 *
 */
public interface TextUnitDataStructureFactory {
	/**
	 * 
	 * Create an instance of TextUnit storage according to some policy
	 *  
	 * @return
	 */
	CountableIdentifiableStorage<TextUnit> create();
}
