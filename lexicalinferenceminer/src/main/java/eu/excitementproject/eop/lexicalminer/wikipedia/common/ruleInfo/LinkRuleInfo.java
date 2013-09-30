package eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo;

import eu.excitementproject.eop.lexicalminer.definition.Common.BaseRuleInfo;

/**
 * Represents extra data about rule created by hyperlink on Wikipedia article
 * 
 * @author Alon Halfon
 * @since 29/04/12
 *
 */
public class LinkRuleInfo extends BaseRuleInfo {

	private static final long serialVersionUID = 6160966853356381947L;
	private String m_linkPageTitle;
	
	public LinkRuleInfo(int linkPageID,String linkPageTitle) 
	{
		this(linkPageID,linkPageTitle,-1);
		
	}
	
	public LinkRuleInfo(int linkPageID,String linkPageTitle,int ruleId) 
	{
		super(linkPageID,ruleId);
		m_linkPageTitle=linkPageTitle;

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
		
		return String.format("%s (%d)", m_linkPageTitle,m_sourceId);
	}
	
	public String getMetadata() {
		
		return String.format("m_linkPageTitle= %s", m_linkPageTitle);
	}	

}
