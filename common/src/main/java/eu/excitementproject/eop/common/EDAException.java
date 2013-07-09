package eu.excitementproject.eop.common;

import eu.excitementproject.eop.common.exception.BaseException;

/**
 * Interfaces and implementations of EDAs can generate this type of exception. All
checked exceptions originated from EDA code should use or inherit this exception. <I>[Quote from Spec, Section 6.2]</I>  
 * @author Gil
 */
public class EDAException extends BaseException {

	private static final long serialVersionUID = -850791993030362703L;

	public EDAException(String message) {
		super(message);
	}

	public EDAException(String message, Throwable cause) {
		super(message, cause);
	}

	public EDAException(Throwable cause) {
		super(cause);
	}
}
