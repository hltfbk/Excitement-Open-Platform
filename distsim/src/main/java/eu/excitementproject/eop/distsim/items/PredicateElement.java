package eu.excitementproject.eop.distsim.items;

/**
 * A predicate element is composed of string representation of a predicate
 *  
 * @author Meni Adler
 * @since 28/06/2012
 *
 */
public class PredicateElement extends StringBasedElement {
	
	
	private static final long serialVersionUID = 1L;

	public PredicateElement() {
		super();
	}
	
	public PredicateElement(String predicate) {
		super(predicate);
	}

	public PredicateElement(String predicate, AggregatedContext context) {
		super(predicate, context);
	}

	public PredicateElement(String predicate, int id, long count) {
		super(predicate,id,count);

	}
	
	public PredicateElement(String predicate, AggregatedContext context, int id, long count) {
		super(predicate,context,id,count);
	}	

}
