package eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo;

import eu.excitementproject.eop.lexicalminer.definition.Common.BaseRuleInfo;

/**
 * Represents extra data about rule created by hyperlink on Wikipedia article
 * 
 * @author Alon Halfon
 * @since 29/04/12
 *
 */
public class RedirectRuleInfo extends BaseRuleInfo {

	private static final long serialVersionUID = 7509039395609397694L;
	private String m_linkPageTitle;
	private boolean m_titleToTarget;
	
	public RedirectRuleInfo(int linkPageID,String linkPageTitle,boolean titleToTarget)
	{
		super(linkPageID);
		m_linkPageTitle=linkPageTitle;
		
		m_titleToTarget=titleToTarget;
	}
	
	public String GetLinkPageTitle()
	{
		return m_linkPageTitle;
	}
	
	public int GetLinkPageID()
	{
		return m_sourceId;
	}
	
	@Override
	public String toString() {
		
		return String.format("%s (%d) titleToTarget:%s", m_linkPageTitle,m_sourceId,m_titleToTarget);
	}

	public String getMetadata() {
		
		return String.format("m_linkPageTitle= %s, m_titleToTarget= %s ", m_linkPageTitle, m_titleToTarget);
	}
	
}
