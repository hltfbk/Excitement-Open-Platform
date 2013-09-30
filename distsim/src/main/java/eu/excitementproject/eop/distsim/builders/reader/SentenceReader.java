package eu.excitementproject.eop.distsim.builders.reader;

import eu.excitementproject.eop.distsim.util.Pair;


/**
 * An interface for reading sentences, of general type of representation T, from some general type of source S
 * 
 * @author Meni Adler
 * @since 08/01/2013
 *
 * @param <S> the type of the sentence source (e.g., InputStream)
 * @param <T> the type of sentence representation, retrieved by the reader (e.g., String, BasicNode)
 */
public interface SentenceReader<S,T> {
	
	/**
	 * Sets a source for sentence reading 
	 * 
	 * @param source a given source of sentences
	 */
	void setSource(S source) throws SentenceReaderException;
	
	/**
	 * Closes the current source
	 * 
	 * @throws Exception
	 */
	void closeSource() throws SentenceReaderException;
	
	/**
	 * Reads the next sentence from some source
	 * 
	 * @return a pair, composed of the next sentence from the source, represented by the generic type T, and its frequency
	 */
	Pair<T,Long> nextSentence() throws SentenceReaderException;
}
