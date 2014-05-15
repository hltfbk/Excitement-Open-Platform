/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules.conll;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


/**
 * Convert {@link AbstractNode}s to strings in CoNLL format
 * 
 * @see	http://ilk.uvt.nl/conll/\#dataformat
 *  
 * @author Amnon Lotan
 *
 * @since Jul 14, 2012
 */
public interface RuleConllStringConverter<I extends Info, N extends AbstractNode<I, N>>  {
	
	/**
	 * Get a node, and a map from nodes to their respective CoNLL representation IDs, and return the line representing the 
	 * node in CoNLL format.<br>
	 * <b>ASSUMPTION:</b> the map contains entries for the node and its antecedent
	 * 
	 * @param node
	 * @param mapNodeToId
	 * @param variableId
	 * @return
	 * @throws ConllConverterException
	 */
	String convert(N node, Map<N, Integer> mapNodeToId,	int variableId) throws ConllConverterException;
}
