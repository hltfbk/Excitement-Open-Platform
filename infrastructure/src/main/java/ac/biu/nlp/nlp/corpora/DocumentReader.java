package ac.biu.nlp.nlp.corpora;


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
