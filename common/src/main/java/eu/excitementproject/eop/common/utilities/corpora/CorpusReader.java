package eu.excitementproject.eop.common.utilities.corpora;

import java.util.Iterator;

/**
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
