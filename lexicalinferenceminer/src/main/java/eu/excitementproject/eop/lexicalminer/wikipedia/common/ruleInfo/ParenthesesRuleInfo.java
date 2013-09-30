package eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo;

import eu.excitementproject.eop.lexicalminer.definition.Common.BaseRuleInfo;

/**
 * Represents extra data about rule created by parentheses in the title of wikipedia article
 * 
 * @author Alon Halfon
 * @since 29/04/12
 *
 */
public class ParenthesesRuleInfo extends BaseRuleInfo {

	private static final long serialVersionUID = -4042753887696387614L;
	private String m_pageTitle;

	
	public ParenthesesRuleInfo(int pageID,String linkPageTitle)
	{
		super(pageID);
		m_pageTitle=linkPageTitle;

	}
	
	public String GetLinkPageTitle()
	{
		return m_pageTitle;
	}
	
	public int GetLinkPageID()
	{
		return m_sourceId;
	}
	
	@Override
	public String toString() {
		
		return String.format("%s (%d)", m_pageTitle,m_sourceId);
	}


	public String getMetadata() {
		
		return String.format("m_pageTitle= %s", m_pageTitle);
	}

}
