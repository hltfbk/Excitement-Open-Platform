package eu.excitementproject.eop.biutee.script;
import eu.excitementproject.eop.biutee.rteflow.macro.DefaultOperationScript;



/**
 * An item contains an operation type, and optionally a rule-base-name.
 * <P>
 * The operation type is of {@link SingleOperationType}.
 * The name is a rule-base-name, and is required only for items that are rule application
 * (either lexical or not)
 * ( {@link SingleOperationType#RULE_APPLICATION}, {@link SingleOperationType#LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION},
 * {@link SingleOperationType#LEXICAL_RULE_BY_LEMMA_AND_POS_APPLICATION_2D}, {@link SingleOperationType#LEXICAL_RULE_BY_LEMMA_APPLICATION} )
 * 
 * @see DefaultOperationScript
 * 
 * @author Asher Stern
 * @since February  2011
 *
 */
public class SingleOperationItem
{
	public SingleOperationItem(SingleOperationType type)
	{
		this(type,null,null);
	}
	
	public SingleOperationItem(SingleOperationType type, String ruleBaseName)
	{
		this(type,ruleBaseName,null);
	}
	
	public SingleOperationItem(SingleOperationType type, String ruleBaseName, String pluginId)
	{
		this.type = type;
		this.ruleBaseName = ruleBaseName;
		this.pluginId = pluginId;
	}
	
	
	public SingleOperationType getType()
	{
		return type;
	}
	
	public String getRuleBaseName()
	{
		return ruleBaseName;
	}
	
	public String getPluginId()
	{
		return pluginId;
	}

	
	public String toString()
	{
		return this.getType().name()+((null==this.getRuleBaseName())?"":": "+this.getRuleBaseName())+((this.getPluginId()==null)?"":": "+this.getPluginId());
	}



	private final SingleOperationType type;
	private final String ruleBaseName;
	private final String pluginId;
}
