package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

import java.io.FileNotFoundException;

import eu.excitementproject.eop.common.exception.ConfigurationException;

public class DerivBaseNotInstalledException extends ConfigurationException {

	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -5164113671266746355L;

	public DerivBaseNotInstalledException(String message) {
		super(message);
	}

	public DerivBaseNotInstalledException(String string, FileNotFoundException e) {
		super(string, e);
	}

}

