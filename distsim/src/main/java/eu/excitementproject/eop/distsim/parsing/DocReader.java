package eu.excitementproject.eop.distsim.parsing;

import java.io.Reader;


/**
 * @author Amnon Lotan
 *
 * @since 08/03/2011
 */
public abstract class DocReader extends Reader 
{
	/**
	 * Progresses the reader's head to the next doc to read
	 * (either sentence or paragraph depending on the implementation).
	 * @return false iff reached the end of corpus. 
	 */
	public abstract boolean next() throws Exception;

	/**
	 * @return the current doc's ID
	 * @throws Exception
	 */
	public abstract String docId() throws Exception;
	
	/**
	 * @return the current doc's contents
	 * @throws Exception
	 */
	public abstract String doc() throws Exception;

	/**
	 * @return the next token, or null when finished current text.
	 * @throws Exception
	 */
	public abstract String readToken() throws Exception;

	


}
