package eu.excitementproject.eop.lexicalminer.definition.idm;

import java.io.Serializable;

public class Pattern implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6267160468712700372L;
	public Pattern(String posPattern, String wordsPattern,
			String relationsPattern, String posRelationsPattern, String fullPattern) {
		this.m_posPattern = posPattern;
		this.m_wordsPattern = wordsPattern;
		this.m_relationsPattern = relationsPattern;
		this.m_posRelationsPattern = posRelationsPattern;
		this.m_fullPattern = fullPattern;
	}
	public String getPosPattern() {
		return m_posPattern;
	}
	public String getWordsPattern() {
		return m_wordsPattern;
	}
	public String getRelationsPattern() {
		return m_relationsPattern;
	}
	public String getPosRelationsPattern() {
		return m_posRelationsPattern;
	}		
	
	public String getFullPattern() {
		return m_fullPattern;
	}
	private String m_posPattern;
	private String m_wordsPattern;
	private String m_relationsPattern;
	private String m_posRelationsPattern;		
	private String m_fullPattern;
	@Override
	public String toString() {
		return "Pattern [fullPattern=" + m_fullPattern + "]";
	}
}
