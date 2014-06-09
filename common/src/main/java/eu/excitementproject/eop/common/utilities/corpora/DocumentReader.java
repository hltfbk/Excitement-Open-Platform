package eu.excitementproject.eop.common.utilities.corpora;


/**
 * An interface which represents a document in a corpus. Its basic
 * capabilities is to read the document's contents. This is done by
 * a call to the method {@link #read()}, followed by {@link #getDocumentContents()}.
 * 
 * @author Asher Stern
 * @since Oct 18, 2012
 *
 */
public interface DocumentReader
{
	public void read() throws CorporaException;
	public String getDocumentContents() throws CorporaException;

}
