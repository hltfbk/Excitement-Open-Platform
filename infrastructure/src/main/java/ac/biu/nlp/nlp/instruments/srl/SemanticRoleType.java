package ac.biu.nlp.nlp.instruments.srl;

/**
 * 
 * @author Asher Stern
 * @since Dec 26, 2011
 *
 */
public enum SemanticRoleType
{
	ARGUMENT_CAUSER(true),
	ARGUMENT(true),
	MODIFIER(false)
	;
	
	public boolean isArgument()
	{
		return argument;
	}
	
	
	private SemanticRoleType(boolean argument)
	{
		this.argument = argument;
	}
	private final boolean argument;
}
