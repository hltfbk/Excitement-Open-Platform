/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.charger;


/**
 * This generic class represents a (regular or partial) alignment arrow in a Cgx file. It has:
 * the IDs of a generic node and an annotations record, and a String (the name of the partial alignment type)
 * the two are not necessarily aligned<br>
 * <b>IMMUTABLE</b>
 * @author Amnon Lotan
 *
 * @since Jul 4, 2012
 */
public class GenericAlignment {
	private final Long nodeId;
	private final Long annotationsId;
	private final String type;
	/**
	 * Ctor
	 * @param nodeId
	 * @param annotationsId
	 * @param type
	 */
	public GenericAlignment(Long nodeId, Long annotationsId, String type) {
		super();
		this.nodeId = nodeId;
		this.annotationsId = annotationsId;
		this.type = type;
	}
	/**
	 * @return the nodeId
	 */
	public Long getLeftId() {
		return nodeId;
	}
	/**
	 * @return the annotationsId
	 */
	public Long getRightId() {
		return annotationsId;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
}
