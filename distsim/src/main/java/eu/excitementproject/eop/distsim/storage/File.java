/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * A file-based implementation of the {@link PersistenceDevice} interface, where each line is composed
 * of an id and a serialized data object 
 * 
 * 
 * @author Meni Adler
 * @since 10/09/2012
 *
 */
public class File implements PersistenceDevice {

	//protected static final String DEFAULT_ENCODING = "UTF-8";
	
	
	public File(java.io.File file, boolean bRead) {
		this(file,bRead, null);
	}
	
	public File(java.io.File file, boolean bRead, String encoding) {
		this.file = file;
		this.bRead = bRead;
		this.encoding = encoding;
	}

	public File(ConfigurationParams params) throws ConfigurationException {
		this.file = new java.io.File(params.get(Configuration.FILE));
		this.bRead = params.get(Configuration.READ_WRITE).equals("read");
		try {
			this.encoding = params.get(Configuration.ENCODING);
		} catch (ConfigurationException e) {
			this.encoding = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#open()
	 */
	@Override
	public synchronized void open()  throws IOException {
		if (bRead) {
			reader = (encoding == null ? new BufferedReader(new InputStreamReader(new FileInputStream(file)))
									   : new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding)));
			writer = null;
		} else {
			reader = null;
			writer = (encoding == null ? new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)))
									   : new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),encoding)));
		}		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#save(int, java.io.Serializable)
	 */
	@Override
	public synchronized void write(int id, Serializable data) throws SerializationException, IOException {
		if (writer == null)
			throw new IOException("Writing device is not opened");
		writer.print(id);
		writer.print("\t");
		writer.print(Serialization.serialize(data));
		writer.print("\n");
		
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
		if (toks.length != 2)
			throw new SerializationException("wrong line format: " + line);
		return new Pair<Integer, Serializable>(Integer.parseInt(toks[0]), Serialization.deserialize(toks[1]));
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#getType()
	 */
	@Override
	public PersistenceDeviceType getType() {
		return PersistenceDeviceType.FILE;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.PersistenceDevice#close()
	 */
	@Override
	public synchronized void close() throws IOException {
		if (reader != null) 
			reader.close();
		if (writer != null) 
			writer.close();
	}

	protected java.io.File file;
	protected boolean bRead;
	protected String encoding;
	protected BufferedReader reader;
	protected PrintWriter writer;
}
