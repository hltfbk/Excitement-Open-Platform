package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCounts;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.SyntacticAbstractOfflineFuncClassifier;


public abstract class SyntacticAbstractOfflineCountClassifier extends
		SyntacticAbstractOfflineFuncClassifier {
	
	protected SyntacticAbstractOfflineCountClassifier(RetrievalTool retrivalTool, double NPBonus) {
		super(retrivalTool,NPBonus);
	}	
	
	@Override
	protected void setM_RankQuery() {
		m_RankQuery =  " select rp.ruleId rID, ? cId ,  (t.patternCount*(?)) rank " 
				+ "			FROM patterncounters t, rulepatterns rp " 
				+ "			where t.patternType = ? "
				+ " 		and rp." + m_PatternNameColumn + " = t.pattern";
	}

	@Override
	protected void setM_TotalSelectFunc() {
		m_TotalSelectFunc = "max(patternCount)";
	}

}
