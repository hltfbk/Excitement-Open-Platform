package eu.excitementproject.eop.common.component.lexicalknowledge;

import eu.excitementproject.eop.common.exception.ComponentException;

/**
 * Implementations of knowledge components (lexical resource
and syntactic resource) can raise this exception. <I>[Quote from Spec, Section 6.2]</I>  
 * @author Gil
 */
public class KnowledgeComponentException extends ComponentException {

	private static final long serialVersionUID = 2583907787885648667L;

	public KnowledgeComponentException(String message) {
		super(message);
	}

	public KnowledgeComponentException(String message, Throwable cause) {
		super(message, cause);
	}

}
