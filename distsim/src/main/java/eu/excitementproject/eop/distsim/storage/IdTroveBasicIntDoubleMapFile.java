/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.io.IOException;
import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * Extends the {@link IdDataFile} class, by storing the given TroveBasedIDKeyBasicMap<Double> data of integers and doubles
 * with no serialization information
 * 
 * @author Meni Adler
 * @since 10/09/2012
 *
 */
public class IdTroveBasicIntDoubleMapFile extends IdDataFile {

	/**
	 * @throws IOException 
	 * 
	 */
	public IdTroveBasicIntDoubleMapFile(java.io.File file, Boolean bRead) {
		super(file,bRead);
	}

	public IdTroveBasicIntDoubleMapFile(java.io.File file, Boolean bRead, String encoding) {
		super(file,bRead,encoding);
	}

	public IdTroveBasicIntDoubleMapFile(ConfigurationParams params) throws ConfigurationException {
		super(params);
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.storage.IdDataFile#getData(java.lang.String[])
	 */
	@Override
	public synchronized Serializable getData(String[] toks) throws SerializationException {
		if (toks.length % 2 != 1)
			throw new SerializationException("wrong line format: " + toks);		
		TroveBasedIDKeyBasicMap<Double> map = new TroveBasedIDKeyBasicMap<Double>();
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
		BasicMap<Integer,Double> map = (BasicMap<Integer, Double>) data;
		if (map.size() > 0) {
			writer.print(id);
			ImmutableIterator<Pair<Integer,Double>> it = map.iterator();
			while (it.hasNext()) {
				Pair<Integer, Double> pair = it.next();
				writer.print("\t");
				writer.print(pair.getFirst());
				writer.print("\t");
				writer.print(pair.getSecond());
			}
			writer.print("\n");
		}
	}
}
