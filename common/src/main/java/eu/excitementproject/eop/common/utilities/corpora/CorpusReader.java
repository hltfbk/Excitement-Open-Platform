package eu.excitementproject.eop.common.utilities.corpora;

import java.util.Iterator;

/**
 * An interface which represents a corpus. It's only method is
 * {@link #iterator()}, which returns an iterator over the corpus' documents.
 * 
 * @author Asher Stern
 * @since October, 2012
 *
 * @param <D>
 */
public interface CorpusReader<D extends DocumentReader>
{
	public Iterator<CorpusDocumentEntity<D>> iterator() throws CorporaException;
}
