/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;

/**
 * An implementation of the {@link SentenceReader} interface, where the sentences are read from a given input stream.
 * The class is abstract - the concrete extraction of the sentence from the input stream should be defined in the subclasses.
 * 
 * 
 * @author Meni Adler
 * @since 08/01/2013
 *
 */
public abstract class StreamBasedSentenceReader<T> implements SentenceReader<InputStream,T> {

	
	public StreamBasedSentenceReader() {
		charset = null;
	}

	public StreamBasedSentenceReader(ConfigurationParams params) {
		try {
			charset = Charset.forName(params.get(Configuration.ENCODING));
		} catch (ConfigurationException e) {
			charset = Charset.forName(DEFAULT_ENCODING);	
		}
	}

	public StreamBasedSentenceReader(String encoding)  {
		charset = Charset.forName(encoding);
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#setSource(java.lang.Object)
	 */
	@Override
	public void setSource(InputStream in) throws SentenceReaderException {
		try {
			close();		
			reader = new BufferedReader(new InputStreamReader(in));
		} catch (IOException e) {
			throw new SentenceReaderException(e);
		}
		
	}
	
	/**
	 * Closes the current stream
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (reader != null)
			reader.close();
	}
	
	protected static final String DEFAULT_ENCODING = "UTF-8";
	protected BufferedReader reader;
	protected Charset charset;

}
