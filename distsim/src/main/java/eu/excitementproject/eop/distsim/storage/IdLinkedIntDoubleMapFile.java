/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.SerializationException;


/**
 * Extends the {@link IdDataFile} class, by storing the given LinkedHashMap data of integers and doubles
 * with no serialization information
 * 
 * @author Meni Adler
 * @since 10/09/2012
 *
 */
public class IdLinkedIntDoubleMapFile extends IdDataFile {

	/**
	 * @throws IOException 
	 * 
	 */
	public IdLinkedIntDoubleMapFile(java.io.File file, Boolean bRead) {
		super(file,bRead);
	}

	public IdLinkedIntDoubleMapFile(ConfigurationParams params) throws ConfigurationException {
		super(params);
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.IdDataFile#getData(java.lang.String[])
	 */
	@Override
	public synchronized Serializable getData(String[] toks) throws SerializationException {
		if (toks.length % 2 != 1) {
			throw new SerializationException("wrong line format: " + toks);
			//tmp
			/*System.out.println("IdLinkedIntDoubleMapFile: double value is missing for int key");
			LinkedHashMap<Integer,Double> map = new LinkedHashMap<Integer,Double>();
			for (int i=1; i< toks.length; i+=2) {
				if (i+1 < toks.length)
					map.put(Integer.parseInt(toks[i]),Double.parseDouble(toks[i+1]));
			}
			return map;*/ 
		} 
		LinkedHashMap<Integer,Double> map = new LinkedHashMap<Integer,Double>();
		for (int i=1; i< toks.length; i+=2)
			map.put(Integer.parseInt(toks[i]),Double.parseDouble(toks[i+1]));
		return map;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.IdDataFile#writeData(int, java.io.Serializable)
	 */
	@Override
	public synchronized void writeData(int id, Serializable data) {
		@SuppressWarnings("unchecked")
		Map<Integer,Double> map = (Map<Integer, Double>) data;
		if (!map.isEmpty()) {
			writer.print(id);
			for (Entry<Integer,Double> entry : map.entrySet()) {			
				writer.print("\t");
				writer.print(entry.getKey());
				writer.print("\t");
				writer.print(entry.getValue());
			}
			writer.print("\n");
		}
	}
}
