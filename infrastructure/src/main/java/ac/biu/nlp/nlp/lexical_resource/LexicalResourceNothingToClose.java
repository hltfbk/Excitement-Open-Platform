package ac.biu.nlp.nlp.lexical_resource;

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
