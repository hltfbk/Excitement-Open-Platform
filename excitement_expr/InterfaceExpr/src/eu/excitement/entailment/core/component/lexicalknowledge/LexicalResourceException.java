package eu.excitement.entailment.core.component.lexicalknowledge;

import eu.excitement.entailment.core.exceptions.KnowledgeComponentException;

public class LexicalResourceException extends KnowledgeComponentException {

	private static final long serialVersionUID = -2578869289508192705L;

	public LexicalResourceException(String message) {
		super(message);
	}

	public LexicalResourceException(String message, Throwable cause) {
		super(message, cause);
	}

}
