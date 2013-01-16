package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public interface DocumentReader
{
	public void setFileName(String fileName) throws RteKbpIOException;
	
	public void read() throws RteKbpIOException;
	
	public DocumentContents getDocumentContents() throws RteKbpIOException;
}
