package eu.excitementproject.eop.common.exception;

/**
 * This is an exception caused within an entailment component. 
 * It is the base type that will be inherited by core component exceptions 
 * like <code>KnowledgeComponentException</code>, <code>DistanceComponentException</code>, 
 * or that of future core components. <I>[Quote from Spec, Section 6.2]</I>  
 * @author Gil
 */
public class ComponentException extends BaseException {

	private static final long serialVersionUID = -3063735610377120178L;

	public ComponentException(String message) {
		super(message);
	}

	public ComponentException(Throwable cause) {
	    super(cause);
	}
	
	public ComponentException(String message, Throwable cause) {
		super(message, cause);
	}

}
