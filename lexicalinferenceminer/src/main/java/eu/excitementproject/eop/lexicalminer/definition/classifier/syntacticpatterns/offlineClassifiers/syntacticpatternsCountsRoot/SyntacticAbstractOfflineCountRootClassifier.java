package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCountsRoot;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.SyntacticAbstractOfflineFuncClassifier;


public abstract class SyntacticAbstractOfflineCountRootClassifier extends
		SyntacticAbstractOfflineFuncClassifier {
	
	public SyntacticAbstractOfflineCountRootClassifier(RetrievalTool retrivalTool, double NPBonus) {
		super(retrivalTool,NPBonus);
	}	

	protected void setM_RankQuery() {
		double m_PowerFactor = 0.5;	//must be here, because it is been called before the c-tor is called


		
		m_RankQuery =  " select rp.ruleId rID, ? cId ,  power((t.patternCount*(?))," + m_PowerFactor +") rank " 
				+ "			FROM patterncounters t, rulepatterns rp " 
				+ "			where t.patternType = ? "
				+ " 		and rp." + m_PatternNameColumn + " = t.pattern";
	}

	@Override
	protected void setM_TotalSelectFunc() {
		m_TotalSelectFunc = "max(patternCount)";
	}

}
