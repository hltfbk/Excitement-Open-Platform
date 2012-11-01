package eu.excitementproject.eop.lap;

import eu.excitementproject.eop.common.exception.ComponentException;

public class LAPException extends ComponentException {

	private static final long serialVersionUID = -5677803552113728008L;

	public LAPException(String message) {
		super(message);
	}
	
	public LAPException(Throwable cause) {
	    super(cause);
	}

	public LAPException(String message, Throwable cause) {
		super(message, cause);
	}

}
