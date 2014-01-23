package eu.excitementproject.eop.lexicalminer.definition.Common;

import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

/*
 * This class added the getMetadata() function that every RuleInfo must implement
 */
public abstract class BaseRuleInfo implements RuleInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9219995087605862129L;
	// the id of the source that rule was build on
	protected int m_sourceId;
	
	protected int m_ruleId;
	
	/*
	 * this value is -1 on creation, otherwise it gets the database rule id
	 */
	public int getRuleId()
	{
		return m_ruleId;
	}
	
	public BaseRuleInfo(int sourceId,int ruleId)
	{
		m_sourceId=sourceId;
		m_ruleId=ruleId;
	}
	
	public BaseRuleInfo(int sourceId)
	{
		this(sourceId,-1);
	}
	public abstract String getMetadata();
	
	/*
	 * the id of the source that rule was build on (for example: the page id of wikipedia which the rule was built form)
	 */
	public int GetSourceId()
	{
		return m_sourceId;
	}
	

}
