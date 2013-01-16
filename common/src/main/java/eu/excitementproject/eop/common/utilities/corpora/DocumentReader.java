package eu.excitementproject.eop.common.utilities.corpora;


/**
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
