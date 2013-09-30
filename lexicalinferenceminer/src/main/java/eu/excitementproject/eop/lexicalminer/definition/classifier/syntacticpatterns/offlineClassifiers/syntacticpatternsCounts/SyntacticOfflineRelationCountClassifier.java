package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCounts;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;

public class SyntacticOfflineRelationCountClassifier extends
		SyntacticAbstractOfflineCountClassifier {

	public SyntacticOfflineRelationCountClassifier(RetrievalTool retrivalTool, Double NPBonus) {
		super(retrivalTool,NPBonus);
	}	

	@Override
	protected void setM_PatternNameColumn() {
		m_PatternNameColumn = "relationsPattern";
	}

	@Override
	protected void setM_PatternKind() {
		m_PatternKind = "relations";
	}
}
