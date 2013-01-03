/**
 * 
 */
package ac.biu.nlp.nlp.datasets.trec;

import java.util.List;
import java.util.Vector;


/**
 * A common interface to all the JAXB-generated classes that represent xml-files in the various TREC corpora.
 * 
 * @author Amnon Lotan
 * @since 04/07/2011
 * 
 */
public abstract class DataFile {

	/**
	 * The name of the package containing all the packages of jaxb-generated java and xsd files for marshalling TREC.
	 * Should be adjacent to the package of this class.
	 */
	public static final String BASE_TREC_GENERATED_PACKAGE = "ac.biu.nlp.nlp.datasets.trec.jaxb_generated.";

	/**
	 * not visible: return the 'DOC' objects at the second-top level of the xml file, using the specific DOC class for 
	 * the implemented corpus's package
	 * @return
	 */
	protected abstract List<? extends Object> getDOC(); 
	
	/**
	 * visible: return a list of the more generic {@link Doc} interface, representing the fixed 
	 * xml objects at the second-top level
	 * @return
	 * @throws TrecException 
	 */
	public List<Doc> getDocs() throws TrecException
	{
		List<? extends Object> docsAsObjects = this.getDOC();
		List<Doc> docs = new Vector<Doc>(docsAsObjects.size());
		for (Object docAsObject : docsAsObjects)
			try {
				docs.add((Doc) docAsObject);
			} catch (Exception e) {
				throw new TrecException("Error. This object's DOC elements aparently doesn't implement Doc");
			}
		return docs;
	}
}
