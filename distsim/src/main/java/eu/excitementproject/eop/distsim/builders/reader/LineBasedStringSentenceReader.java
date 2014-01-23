package eu.excitementproject.eop.distsim.builders.reader;

import java.io.IOException;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * An implementation of the {@link SentenceReader} interface, where the next sentence is the next line of the given input stream.
 * 
 * @author Meni Adler
 * @since 08/01/2013
 *
 */
public class LineBasedStringSentenceReader extends ReaderBasedSentenceReader<String>{

	public LineBasedStringSentenceReader() {
		super();
	}
	
	public LineBasedStringSentenceReader(	ConfigurationParams params) {
		super(params);
	}


	public LineBasedStringSentenceReader(String encoding)  {
		super(encoding);
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#nextSentence()
	 */
	@Override
	public synchronized Pair<String,Long> nextSentence() throws SentenceReaderException {
		try {
			String line = reader.readLine();
			if (line == null)
				return null;
			else {
				position += line.getBytes(charset).length;
				return new Pair<String,Long>(line,1L);
			}
		} catch (IOException e) {
			throw new SentenceReaderException(e);
		}
	}
	
	

}
