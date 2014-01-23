package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCounts;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;

public class SyntacticOfflinePosCountClassifier extends
		SyntacticAbstractOfflineCountClassifier {

	public SyntacticOfflinePosCountClassifier(RetrievalTool retrivalTool, Double NPBonus) {
		super(retrivalTool,NPBonus);
	}	
	
	@Override
	protected void setM_PatternNameColumn() {
		m_PatternNameColumn = "POSPattern";
	}

	@Override
	protected void setM_PatternKind() {
		m_PatternKind = "pos";
	}
}
