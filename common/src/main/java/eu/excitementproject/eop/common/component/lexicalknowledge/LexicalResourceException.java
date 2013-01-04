package eu.excitementproject.eop.common.component.lexicalknowledge;

import eu.excitementproject.eop.common.component.lexicalknowledge.KnowledgeComponentException;


public class LexicalResourceException extends KnowledgeComponentException {

	private static final long serialVersionUID = -2578869289508192705L;

	public LexicalResourceException(String message) {
		super(message);
	}

	public LexicalResourceException(String message, Throwable cause) {
		super(message, cause);
	}

}
