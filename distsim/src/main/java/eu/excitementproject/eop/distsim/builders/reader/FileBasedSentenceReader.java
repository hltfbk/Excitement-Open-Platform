package eu.excitementproject.eop.distsim.builders.reader;

import java.io.File;

/**
 * @author Meni Adler
 * @since 25 April 2013
 *
 */
public abstract class FileBasedSentenceReader<T> implements SentenceReader<File, T> {
	/**
	 * @return the current position in the file
	 */
	public synchronized long getPosition() {
		return position;
	}

	protected long position;
}
