/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.conll;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


/**
 * Convert {@link AbstractNode}s from a tree to strings in CoNLL format
 * 
 * @see	http://ilk.uvt.nl/conll/\#dataformat
 *  
 * @author Amnon Lotan
 *
 * @since Jul 14, 2012
 */
public interface TreeConllStringConverter<I extends Info, N extends AbstractNode<I, N>>  {
	
	/**
	 * Get a node, and a map from nodes to their respective CoNLL representation IDs, the antecedent, and return the line representing the 
	 * node in CoNLL format.<br>
	 * <b>ASSUMPTION:</b> the map contains entries for the node and its antecedent
	 * 
	 * @param node
	 * @param mapNodeToId
	 * @param antecedent
	 * @return
	 * @throws ConllConverterException
	 */
	String convert(N node, Map<N, Integer> mapNodeToId,	N antecedent) throws ConllConverterException;
	
	/**
	 * The CoNLL id of the artificial ROOT of each sentence si always zero
	 */
	static final int ROOT_ID = 0;
	static final char TAB = '\t';
	static final String UNDERSCORE = "_";
	static final String ROOT = "ROOT";

}
