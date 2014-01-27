/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.IOException;
import java.io.Serializable;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.SerializationException;


/**
 * Extends the {@link IdDataFile} class, by storing the data as a simple double instead of it serialization
 * 
 * @author Meni Adler
 * @since 10/09/2012
 *
 */
public class IdDoubleFile extends IdDataFile {

	/**
	 * @throws IOException 
	 * 
	 */
	public IdDoubleFile(java.io.File file, Boolean bRead) {
		super(file,bRead);
	}

	public IdDoubleFile(ConfigurationParams params) throws ConfigurationException {
		super(params);
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.IdDataFile#getData(java.lang.String[])
	 */
	@Override
	public synchronized  Serializable getData(String[] toks) throws SerializationException {
		if (toks.length != 2)
			throw new SerializationException("wrong line format: " + toks);		
		return Double.parseDouble(toks[1]);
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.IdDataFile#writeData(int, java.io.Serializable)
	 */
	@Override
	public synchronized void writeData(int id, Serializable data) {
		writer.print(id);
		writer.print("\t");
		writer.print(data);
		writer.print("\n");
	}
}
