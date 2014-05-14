package eu.excitementproject.eop.lexicalminer.redis;

import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

/**
 * Base implementation of wikipedia rules info
 * 
 *   
 * @author Meni Adler
 * 
 *  
 */
public class WikiRuleInfo implements RuleInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static WikiRuleInfo instance = new WikiRuleInfo();
	
	/**
	 * Ctor
	 */
	private WikiRuleInfo() {}
	
	public static WikiRuleInfo getInstance()
	{
		return instance ;
	}
}
