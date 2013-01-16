/**
 * 
 */
package eu.excitementproject.eop.transformations.utilities;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


/**
 * Contains some static methods that get details of {@link AbstractNode}s and returns them as String tuples.
 * @author Amnon Lotan
 *
 * @since Mar 7, 2012
 */
public class NodePrintUtilities {

	
	/**
	 * returns a String tuple like <(2) "boat", NOUN, nsubj>   
	 * 
	 * 
	 * @param id
	 * @param lemma
	 * @param pos
	 * @param relation
	 * @return
	 */
	public static String nodeDetailsToString(String id, String lemma, String pos, String relation) {
		if (relation == null || relation.isEmpty())
			return nodeDetailsToString(id, lemma, pos);
			
		StringBuffer sb = new StringBuffer();
		sb.append("<(");
		sb.append(id);
		sb.append(") \"");
		sb.append(lemma).append("\", ");
		sb.append(pos);
		sb.append(", ");
		sb.append(relation);
		sb.append('>');
		return sb.toString();
	}
	
	/**
	 * returns a String tuple like <(2) "boat", NOUN>
	 * 
	 * @param id
	 * @param lemma
	 * @param pos
	 * @return
	 */
	public static String nodeDetailsToString(String id, String lemma, String pos) {
		if ( pos == null || pos.isEmpty())
			return nodeDetailsToString(id, lemma);
		if ( id == null || id.isEmpty())
			return lemmaAndPosToString(lemma, pos);
		
		StringBuffer sb = new StringBuffer();
		sb.append("<(");
		sb.append(id);
		sb.append(") \"");
		sb.append(lemma).append("\", ");
		sb.append(pos);
		sb.append('>');
		return sb.toString();
	}

	/**
	 * returns a String tuple like <2, "boat">
	 * 
	 * @param id
	 * @param lemma
	 * @return
	 */
	public static String nodeDetailsToString(String id, String lemma) {
		StringBuffer sb = new StringBuffer();
		sb.append("<(");
		sb.append(id);
		sb.append(") \"");
		sb.append(lemma).append("\"");
		sb.append('>');
		return sb.toString();
	}

	/**
	 * returns a String tuple like <"boat", NOUN>
	 * @param newLemma
	 * @param newPos
	 * @return
	 */
	public static String lemmaAndPosToString(String lemma, String pos) {
		StringBuffer sb = new StringBuffer();
		sb.append("<\"");
		sb.append(lemma).append("\", ");
		sb.append(pos);
		sb.append('>');
		return sb.toString();
	}
}
