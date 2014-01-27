package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsLocationsSquare;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;

public class SyntacticOfflinePosLocationSquareClassifier extends
		SyntacticAbstractOfflineLocationSquareClassifier {

	public SyntacticOfflinePosLocationSquareClassifier(RetrievalTool retrivalTool, Double NPBonus) {
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
