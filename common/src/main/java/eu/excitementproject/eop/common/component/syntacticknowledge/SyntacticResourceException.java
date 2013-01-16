package eu.excitementproject.eop.common.component.syntacticknowledge;

import eu.excitementproject.eop.common.component.lexicalknowledge.KnowledgeComponentException;

public class SyntacticResourceException extends KnowledgeComponentException {

	private static final long serialVersionUID = -4780686286151798814L;

	public SyntacticResourceException(String message) {
		super(message);
	}

	public SyntacticResourceException(String message, Throwable cause) {
		super(message, cause); 
	}

}
