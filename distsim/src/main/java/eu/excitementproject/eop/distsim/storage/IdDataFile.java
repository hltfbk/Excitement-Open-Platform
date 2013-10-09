/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.IOException;
import java.io.Serializable;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SerializationException;


/**
 * The {@link eu.excitementproject.eop.distsim.storage.File} device stores the data as a general serialized object.
 * Serialized objects may be inefficient in terms of disk space 
 * The IdDataFile implementation support some other inner storage format as well (under the PersistenceDevice interface which write and read serializable objects).
 * Extensions of this abstract class should define the specific format by overriding the getData and the writeData methods.
 * 
 * 
 * @author Meni Adler
 * @since 10/09/2012
 *
 */
public abstract class IdDataFile extends File {

	/**
	 * @throws IOException 
	 * 
	 */
	public IdDataFile(java.io.File file, boolean bRead) {
		super(file,bRead);
	}

	public IdDataFile(java.io.File file, boolean bRead, String encoding) {
		super(file,bRead, encoding);
	}
	
	public IdDataFile(ConfigurationParams params) throws ConfigurationException {
		super(params);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#save(int, java.io.Serializable)
	 */
	@Override
	public synchronized void write(int id, Serializable data) throws SerializationException, IOException {
		if (writer == null)
			throw new IOException("Writing device is not opened");
		writeData(id,data);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#read()
	 */
	@Override
	public synchronized Pair<Integer, Serializable> read() throws SerializationException, IOException {
		if (reader == null)
			throw new IOException("Reading device is not opened");

		String line = reader.readLine();
		if (line == null)
			return null;
		String[] toks = line.split("\t");
		
		if (toks.length < 1)
			throw new SerializationException("wrong line format: " + line);		
		int id = Integer.parseInt(toks[0]);		
		return new Pair<Integer, Serializable>(id, getData(toks));
	}
	
	/**
	 * Converts the given data, represented by a tokenized string, to one Serializable object 
	 * 
	 * @param toks a representation of data as a tokenized string
	 * @return a representation of the given data as a Serializable object
	 * @throws SerializationException
	 */
	public abstract Serializable getData(String[] toks) throws SerializationException;
	
	/**
	 * Gets an id and data and write them as one line in the file, where the data is converted to some more efficient representation than a Serializable object 
	 * 
	 * @param id the id of the data
	 * @param data some data to be stored, represented by a Serializable object
	 */
	public abstract void writeData(int id,Serializable data);
}
