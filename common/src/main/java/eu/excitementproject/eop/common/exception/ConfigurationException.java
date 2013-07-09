package eu.excitementproject.eop.common.exception;

/**
 * Interfaces of common configuration can throw exceptions of various
kinds. Exceptions originated from common configuration code, and that can be checked, should use
or inherit this exception. <I>[Quote from Spec, Section 6.2]</I>  
 * @author Gil
 */
public class ConfigurationException extends BaseException {

	private static final long serialVersionUID = 2022969604092885084L;

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}
}
