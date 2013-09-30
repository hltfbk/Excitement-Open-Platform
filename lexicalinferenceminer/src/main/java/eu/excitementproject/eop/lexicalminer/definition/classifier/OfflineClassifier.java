package eu.excitementproject.eop.lexicalminer.definition.classifier;

import java.sql.SQLException;


import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.lexicalminer.LexiclRulesRetrieval.RuleData;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;

public abstract class OfflineClassifier extends Classifier {

	protected OfflineClassifier(RetrievalTool retrivalTool, double NPBonus){
		super(retrivalTool, NPBonus);
	}
	
	
	@Override
	protected final double getClassifierRank(RuleData rule)
	{
		if (rule.getClassifierRank() == null)
		{
			return rule.getDefultRank();
		}
		else
		{		
			return rule.getClassifierRank();

		}
	}

	@Override
	public final void setAllRank() throws LexicalResourceException {
		try {
			classifierID = m_retrivalTool.getClassifierId(getClassifierUniqueName(), true);
			setAllRanksOffline();
		} catch (SQLException e) {
			throw new LexicalResourceException(e.toString());
		}
	}
	
	public abstract void setAllRanksOffline() throws SQLException;
}
