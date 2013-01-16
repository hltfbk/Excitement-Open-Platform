package eu.excitementproject.eop.transformations.operations.rules.lexicalchain;
import java.io.Serializable;

import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;



/**
 * 
 * @author Asher Stern
 * @since Jul 17, 2011
 *
 */
public class LexicalRuleWithName implements Serializable
{
	private static final long serialVersionUID = 6431767748643885024L;
	
	public LexicalRuleWithName(LexicalRule rule, String ruleBaseName)
	{
		super();
		this.rule = rule;
		this.ruleBaseName = ruleBaseName;
	}
	
	
	public LexicalRule getRule()
	{
		return rule;
	}
	public String getRuleBaseName()
	{
		return ruleBaseName;
	}

	
	

	@Override
	public String toString() {
		return getRule() + "\t" + getRuleBaseName();
	}




	private final LexicalRule rule;
	private final String ruleBaseName;
}
