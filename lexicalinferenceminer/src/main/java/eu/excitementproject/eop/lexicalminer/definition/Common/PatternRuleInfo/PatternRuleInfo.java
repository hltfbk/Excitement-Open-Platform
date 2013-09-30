package eu.excitementproject.eop.lexicalminer.definition.Common.PatternRuleInfo;


import eu.excitementproject.eop.lexicalminer.definition.Common.BaseRuleInfo;

public abstract class PatternRuleInfo extends BaseRuleInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2107873911447888453L;

	public PatternRuleInfo(int sourceId) {
		super(sourceId);
	}

	public abstract String getFullPattern();
	
	public abstract String getPosPattern();
	
	public abstract String getWordsPattern();
	
	public abstract String getRelationPattern();
	
	public abstract String getPosRelationPattern();	

}
