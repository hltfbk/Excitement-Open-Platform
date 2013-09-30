package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers.syntacticpatternsCountsRoot;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;

public class SyntacticOfflineRelationCountRootClassifier extends
		SyntacticAbstractOfflineCountRootClassifier {

	public SyntacticOfflineRelationCountRootClassifier(RetrievalTool retrivalTool, Double NPBonus) {
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
