package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocationsSquare;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.SyntacticAbstractOfflineFuncClassifier;


public abstract class SyntacticAbstractOfflineLocationSquareClassifier extends
		SyntacticAbstractOfflineFuncClassifier {

	protected SyntacticAbstractOfflineLocationSquareClassifier
							(RetrievalTool retrivalTool, double NPBonus) {
		super(retrivalTool,NPBonus);
	}

	@Override
	protected void setM_RankQuery() {
		double m_PowerFactor = 2; //must be here, because it is been called before the c-tor is called
		
		m_RankQuery =  " select rp.ruleId rID, ? cId ,  power((patternsIndex.rowIndex*(?))," + m_PowerFactor +") rank " 
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
