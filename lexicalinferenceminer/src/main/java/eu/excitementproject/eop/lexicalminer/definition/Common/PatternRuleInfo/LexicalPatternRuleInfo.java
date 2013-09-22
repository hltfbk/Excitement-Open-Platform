package eu.excitementproject.eop.lexicalminer.definition.Common.PatternRuleInfo;

public class LexicalPatternRuleInfo extends PatternRuleInfo {
	private static final long serialVersionUID = -4662379271252523258L;
	private String m_wordsPattern;
	private String m_PosPattern;
	private String m_fullPattern;
	public LexicalPatternRuleInfo(String ruleWordsPattern,String rulePosPattern, String fullPattern , int sourceId) 
	{
		super(sourceId);
		m_wordsPattern=ruleWordsPattern;
		m_PosPattern=rulePosPattern;
		m_fullPattern=fullPattern;
		
	}
	

	
	@Override
	public String toString() {
		return m_wordsPattern;
	}

	
	@Override
	public String getMetadata() {
		return m_fullPattern;
	}
	
	@Override
	public String getPosPattern() {
		return m_PosPattern;
	}
	
	@Override
	public String getWordsPattern() {
		return m_wordsPattern;
	}
	
	@Override
	public String getRelationPattern() {
		return null;
	}
	
	@Override
	public String getPosRelationPattern() {
		return null;
	}	
	
	
	@Override
	public String getFullPattern() {
		return m_fullPattern;
	}
	
}
