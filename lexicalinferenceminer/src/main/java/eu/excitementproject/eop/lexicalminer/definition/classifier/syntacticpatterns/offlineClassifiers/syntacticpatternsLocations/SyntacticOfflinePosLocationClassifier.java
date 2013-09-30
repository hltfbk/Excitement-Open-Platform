package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocations;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;

public class SyntacticOfflinePosLocationClassifier extends
		SyntacticAbstractOfflineLocationClassifier {

	public SyntacticOfflinePosLocationClassifier(
			RetrievalTool retrivalTool, Double NPBonus) {
		super(retrivalTool, NPBonus);
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
