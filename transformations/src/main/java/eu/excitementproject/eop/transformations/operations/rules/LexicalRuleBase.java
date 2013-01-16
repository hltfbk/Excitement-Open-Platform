package eu.excitementproject.eop.transformations.operations.rules;

/**
 * A rule-base (container of rules) that returns {@linkplain LexicalRule}s, according
 * to some criteria.
 * <P>
 * <B>Not thread safe</B> unless specified otherwise.
 * 
 * 
 * <P>
 * @author Asher Stern
 * @since Feb  2011
 *
 */
public abstract class LexicalRuleBase<T extends LexicalRule>
{
	/**
	 * Closes any resources (files, db-connections, etc.)
	 */
	public void close() throws LexicalRuleBaseCloseException {}

}
