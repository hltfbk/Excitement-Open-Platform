/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
public abstract class ReaderBasedSentenceReader<T> extends FileBasedSentenceReader<T> {

	
	public ReaderBasedSentenceReader() {
		charset = null;
		reader = null;
	}

	public ReaderBasedSentenceReader(ConfigurationParams params) {
		try {
			charset = Charset.forName(params.get(Configuration.ENCODING));
		} catch (ConfigurationException e) {
			charset = Charset.forName(DEFAULT_ENCODING);	
		}
		reader = null;
	}

	public ReaderBasedSentenceReader(String encoding)  {
		charset = Charset.forName(encoding);
		reader = null;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#setSource(java.lang.Object)
	 */
	@Override
	public synchronized void setSource(File source) throws SentenceReaderException {
		closeSource();		
		try {
			reader = new BufferedReader(new FileReader(source));
		} catch (FileNotFoundException e) {
			throw new SentenceReaderException(e);
		}	
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#closeSource()
	 */
	@Override
	public synchronized void closeSource() throws SentenceReaderException {
		try {
			if (reader != null)
				reader.close();
		} catch (IOException e) {
			throw new SentenceReaderException(e);
		}
	}

	protected static final String DEFAULT_ENCODING = "UTF-8";
	protected BufferedReader reader;
	protected Charset charset;

}
