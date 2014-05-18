/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.RuleAnnotations;

/**
 * This generic class represents a partial alignment arrow in a Cgx file. It has:
 * a generic {@link AbstractNode} and a {@link RuleAnnotations}, and a String (the name of the partial alignment type)
 * the two are not necessarily aligned<br>
 * <b>IMMUTABLE</b>
 * @author Amnon Lotan
 *
 * @since Jul 4, 2012
 */
public class PartialAlignment<N extends AbstractNode<? extends Info, N>, A extends RuleAnnotations> {
	
	private final N node;
	private final A annotations;
	private final String type;
	/**
	 * Ctor
	 * @param node
	 * @param annotations
	 * @param type
	 */
	public PartialAlignment(N node, A annotations, String type) {
		super();
		this.node = node;
		this.annotations = annotations;
		this.type = type;
	}
	/**
	 * @return the node
	 */
	public N getNode() {
		return node;
	}
	/**
	 * @return the annotations
	 */
	public A getAnnotations() {
		return annotations;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
}
