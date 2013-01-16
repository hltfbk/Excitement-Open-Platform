package eu.excitementproject.eop.core.component.lexicalknowledge.germanet;

import java.io.FileNotFoundException;

import eu.excitementproject.eop.common.exception.ConfigurationException;

public class GermaNetNotInstalledException extends ConfigurationException {

	private static final long serialVersionUID = -1106754182691757185L;

	public GermaNetNotInstalledException(String message) {
		super(message);
	}

	public GermaNetNotInstalledException(String string, FileNotFoundException e) {
		super(string, e);
	}

}

