package eu.excitementproject.eop.core.component.lexicalknowledge.dewacTransDm;

import eu.excitementproject.eop.common.exception.ConfigurationException;

public class GermanTransDmNotInstalledException extends ConfigurationException {

	private static final long serialVersionUID = 5666748774189023526L;


	public GermanTransDmNotInstalledException(String message) {
		super(message);
	}

	public GermanTransDmNotInstalledException(String string, Exception e) {
		super(string, e);
	}

}

