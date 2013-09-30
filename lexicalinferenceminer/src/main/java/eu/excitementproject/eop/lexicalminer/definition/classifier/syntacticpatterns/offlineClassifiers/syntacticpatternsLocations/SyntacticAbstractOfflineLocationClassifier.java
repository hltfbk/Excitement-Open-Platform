package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocations;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.SyntacticAbstractOfflineFuncClassifier;


public abstract class SyntacticAbstractOfflineLocationClassifier extends
		SyntacticAbstractOfflineFuncClassifier {
	
	protected SyntacticAbstractOfflineLocationClassifier(RetrievalTool retrivalTool, double NPBonus) {
		super(retrivalTool, NPBonus);
	}

	@Override
	protected void setM_RankQuery() {
		m_RankQuery =  " select rp.ruleId rID, ? cId ,  (patternsIndex.rowIndex*(?)) rank " 
				+ " from             (SELECT t.pattern, t.patternCount, @row := @row + 1  as rowIndex "
				+ "					FROM patterncounters t, (SELECT @row := 0) r " 
				+ "					where t.patternType = ? "
				+ "					order by patternCount, id) patternsIndex, rulepatterns rp"
				+ " where rp." + m_PatternNameColumn + " = patternsIndex.pattern";
	}

	@Override
	protected void setM_TotalSelectFunc() {
		m_TotalSelectFunc = "count(*)";
	}

}
