package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCounts;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;

public class SyntacticOfflinePosRelationCountClassifier extends
		SyntacticAbstractOfflineCountClassifier {

	public SyntacticOfflinePosRelationCountClassifier(RetrievalTool retrivalTool, Double NPBonus) {
		super(retrivalTool,NPBonus);
	}	
	
	
	@Override
	protected void setM_PatternNameColumn() {
		m_PatternNameColumn = "POSrelationsPattern";
	}

	@Override
	protected void setM_PatternKind() {
		m_PatternKind = "pos_relations";
	}
}
