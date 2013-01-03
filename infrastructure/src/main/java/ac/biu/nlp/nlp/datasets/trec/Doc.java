/**
 * 
 */
package ac.biu.nlp.nlp.datasets.trec;

import java.util.List;

/**
 * A common interface to all the JAXB-generated "DOC" classes that are marshaled out of the second-top level of each xml file in the 
 * various TREC corpora.
 *   
 * @author Amnon Lotan
 * @since 04/07/2011
 * 
 */
public interface Doc {

	/**
	 * get all the String texts this Doc holds. Each String holds the text from one entire XML subsection
	 * @return
	 */
	public List<String> getTexts();
	
	/**
	 * An id for this document, unique across the TREC corpora
	 * @return
	 */
	public String getDOCNO();
	
}
