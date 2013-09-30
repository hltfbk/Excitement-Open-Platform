package eu.excitementproject.eop.distsim.builders.reader;

import java.io.IOException;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * An implementation of the {@link SentenceReader} interface, where the next sentence is the next line of the given input stream,
 * where its frequency is defined at the last tab-separated string of the line.
 * 
 * @author Meni Adler
 * @since 08/01/2013
 *
 */
public class LineBasedStringCountSentenceReader extends LineBasedStringSentenceReader {

	public LineBasedStringCountSentenceReader() throws IOException {
		super();
		delimiter = DEFAULT_DELIMITER;
	}

	public LineBasedStringCountSentenceReader(ConfigurationParams params) {
		super(params);
		try {
			delimiter = params.get(Configuration.DELIMITER);
		} catch (ConfigurationException e) {
			delimiter = DEFAULT_DELIMITER;
		}
	}

	public LineBasedStringCountSentenceReader(String encoding)  {
		super(encoding);
		delimiter = DEFAULT_DELIMITER;
	}

	public LineBasedStringCountSentenceReader(String encoding, String delimiter) throws IOException {
		super(encoding);
		this.delimiter = delimiter;
	}


	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#nextSentence()
	 */
	@Override
	public Pair<String,Long> nextSentence() throws SentenceReaderException {
		try {
			String line = null;
			synchronized(this) {
				line=reader.readLine();
				if (line == null) 
					return null;
				else 
					position += line.getBytes(charset).length;
			}
			int pos = line.lastIndexOf(delimiter);
			return new Pair<String,Long>(line.substring(0,pos),Long.parseLong(line.substring(pos+1)));
		} catch (IOException e) {
			throw new SentenceReaderException(e);
		}
	}
	
	public static final String DEFAULT_DELIMITER = "\t";
	
	protected String delimiter;

}
