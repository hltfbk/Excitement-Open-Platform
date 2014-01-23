package eu.excitementproject.eop.lexicalminer.definition.Common.PatternRuleInfo;

import java.util.List;


import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lexicalminer.definition.idm.Pattern;
import eu.excitementproject.eop.lexicalminer.definition.idm.SyntacticUtils;

public class SyntacticPatternRuleInfo extends PatternRuleInfo{

	private static final long serialVersionUID = -4662379271222523258L;

	private Pattern  m_pattern;
	String m_Sentence;
	private boolean m_isNPphrase;	
		
	public SyntacticPatternRuleInfo(Pattern pattern, int sourceId , boolean isNPphrase, String title, String word, String orginialSentence) {
			this(pattern, null, 0, null, sourceId, isNPphrase, title, word, orginialSentence);
		}	
	
	private SyntacticPatternRuleInfo(Pattern pattern ,BasicNode sentTree, int wordId,
			List<BasicNode> path, int sourceId , boolean isNPphrase, String title, String word, String orginialSentence) {
			super(sourceId);	
			this.m_isNPphrase = isNPphrase;
			this.m_pattern = pattern;
			this.m_Sentence = orginialSentence;
		}
			
	public SyntacticPatternRuleInfo(SyntacticUtils utils ,BasicNode sentTree, int wordId,
			List<BasicNode> path, int sourceId , boolean isNPphrase, String title, String word, String orginialSentence) {
			this(utils.getPatternStrings(sentTree, path, wordId, isNPphrase, title, word),sentTree,wordId, path, sourceId, isNPphrase, title, word, orginialSentence);

	}
	
	public SyntacticPatternRuleInfo(SyntacticUtils utils, BasicNode m_sentTree, int m_wordId, List<BasicNode> path, int sourceId, String title, String word, String orginialSentence) {
		this(utils,m_sentTree,m_wordId, path, sourceId, false, title, word, orginialSentence);
	}		



	public boolean isNPphrase() {
		return m_isNPphrase;
	}

	@Override
	public String toString() {
		return "[m_Sentence= \"" + m_Sentence + "\", m_patternString=" + m_pattern
				+ ", m_isNPphrase=" + m_isNPphrase + "]";
	}

	@Override
	public String getMetadata() {
		return "[m_Sentence= " + m_Sentence + ", m_patternString=" + m_pattern
				+ ", m_isNPphrase=" + m_isNPphrase + "]";
	}
	
	@Override
	public String getPosPattern() {
		return this.m_pattern.getPosPattern();
	}
	
	@Override
	public String getWordsPattern() {
		return this.m_pattern.getWordsPattern();
	}
	
	@Override
	public String getRelationPattern() {
		return this.m_pattern.getRelationsPattern();
	}
	
	@Override
	public String getFullPattern() {
		return this.m_pattern.getFullPattern();

	}

	@Override
	public String getPosRelationPattern() {
		return this.m_pattern.getPosRelationsPattern();
	}
	
	
}
