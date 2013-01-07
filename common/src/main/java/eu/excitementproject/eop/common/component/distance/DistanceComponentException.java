package eu.excitementproject.eop.common.component.distance;

import eu.excitementproject.eop.common.exception.ComponentException;

/**
 * Implementations of distance calculation components can throw
this exception. <I>[Quote from Spec, Section 6.2]</I>  
 * @author Gil
 */
public class DistanceComponentException extends ComponentException {

	private static final long serialVersionUID = 8590600874323127477L;

	public DistanceComponentException(String message) {
		super(message);
	}

	public DistanceComponentException(String message, Throwable cause) {
		super(message, cause);
	}

}
