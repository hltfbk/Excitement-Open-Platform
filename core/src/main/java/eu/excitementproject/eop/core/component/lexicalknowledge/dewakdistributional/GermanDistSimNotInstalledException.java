package eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional;

import eu.excitementproject.eop.common.exception.ConfigurationException;

public class GermanDistSimNotInstalledException extends ConfigurationException {

	private static final long serialVersionUID = 6202214044529310838L;

	public GermanDistSimNotInstalledException(String message) {
		super(message);
	}

	public GermanDistSimNotInstalledException(String string, Exception e) {
		super(string, e);
	}

}

