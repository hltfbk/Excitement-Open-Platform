package eu.excitementproject.eop.distsim.storage;

import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

/**
 * Base implementation of distributional similarity rules info
 * 
 *   
 * @author Meni Adler
 * @since 7 Aug 2013
 * 
 *  
 */
public class DistSimRuleInfo implements RuleInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static DistSimRuleInfo instance = new DistSimRuleInfo();
	
	/**
	 * Ctor
	 */
	private DistSimRuleInfo() {}
	
	public static DistSimRuleInfo getInstance()
	{
		return instance ;
	}
}
