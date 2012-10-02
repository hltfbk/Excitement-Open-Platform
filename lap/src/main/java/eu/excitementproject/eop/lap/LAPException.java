package eu.excitementproject.eop.lap;

import eu.excitementproject.eop.core.exceptions.BaseException;

public class LAPException extends BaseException {

	private static final long serialVersionUID = -5677803552113728008L;

	public LAPException(String message) {
		super(message);
	}

	public LAPException(String message, Throwable cause) {
		super(message, cause);
	}

}
