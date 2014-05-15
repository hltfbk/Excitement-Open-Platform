package eu.excitementproject.eop.common.utilities.uima;

public class UimaUtilsException extends Exception {

	public UimaUtilsException(String message, Throwable cause) {
		super(message, cause);
	}

	public UimaUtilsException(Throwable cause) {
		super("Exception in a UimaUtils method. See inner exception.", cause);
	}

	public UimaUtilsException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -169346567754793054L;

}
