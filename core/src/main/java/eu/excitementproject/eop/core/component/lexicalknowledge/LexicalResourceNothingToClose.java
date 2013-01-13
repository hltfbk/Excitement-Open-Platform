package eu.excitementproject.eop.core.component.lexicalknowledge;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

/**
 * 
 * @author Asher Stern
 * @since Jun 26, 2012
 *
 */
public abstract class LexicalResourceNothingToClose<I extends RuleInfo>  implements LexicalResource<I>
{
	public void close() throws LexicalResourceCloseException{}
}

