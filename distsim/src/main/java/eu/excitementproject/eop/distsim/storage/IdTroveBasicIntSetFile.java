/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.IOException;
import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
/**
 * Extends the {@link IdDataFile} class, by storing the given TroveBasedBasicIntSet data with no serialization information
 * 
 * @author Meni Adler
 * @since 10/09/2012
 *
 */
public class IdTroveBasicIntSetFile extends IdDataFile {

	/**
	 * @throws IOException 
	 * 
	 */
	public IdTroveBasicIntSetFile(java.io.File file, Boolean bRead) {
		super(file,bRead);
	}

	public IdTroveBasicIntSetFile(java.io.File file, Boolean bRead, String encoding) {
		super(file,bRead, encoding);
	}
	
	public IdTroveBasicIntSetFile(ConfigurationParams params) throws ConfigurationException {
		super(params);
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.IdDataFile#getData(java.lang.String[])
	 */
	@Override
	public synchronized Serializable getData(String[] toks) {
		TroveBasedBasicIntSet set = new TroveBasedBasicIntSet();
		for (int i=1; i< toks.length; i++)
			set.add(Integer.parseInt(toks[i]));
		return set;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.IdDataFile#writeData(int, java.io.Serializable)
	 */
	@Override
	public synchronized void writeData(int id, Serializable data) {
		TroveBasedBasicIntSet set = (TroveBasedBasicIntSet) data;
		if (set.size() >0) {
			writer.print(id);
			ImmutableIterator<Integer> it = set.iterator();
			while (it.hasNext()) {
				writer.print("\t");
				writer.print(it.next());
			}
			writer.print("\n");
		}
	}
}
