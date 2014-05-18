
package eu.excitementproject.eop.lexicalminer.definition.classifier;

import java.sql.SQLException;



import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.lexicalminer.LexiclRulesRetrieval.RuleData;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.redis.RedisRuleData;

/**
 * Classify patterns that connect rule sides as being a valid 
 * or invalid indication for inference.
 * 
 * @author Eyal Shnarch
 * @since 12/04/12
 *
 */
public abstract class Classifier {
	
	protected int classifierID = (-1);
	
	protected double m_NPBonus = 0.00000005 ;	//what to add to the total rank if it's a NP (can also be negative)
	protected RetrievalTool m_retrivalTool;

	protected abstract double getClassifierRank(RuleData rule);
	protected abstract double getClassifierRank(RedisRuleData rule);
	public abstract void setAllRank() throws LexicalResourceException;
	
	
	protected Classifier(RetrievalTool retrivalTool, double NPBonus ){
		this.m_retrivalTool = retrivalTool;
		this.m_NPBonus = NPBonus;
	}	
	
	protected Classifier(double NPBonus ){
		this.m_retrivalTool = null;
		this.m_NPBonus = NPBonus;
	}
	
	/**
	 * return rank, adds the relevant bouns, but make sure it's between 0 - 1 
	 * @param rule
	 * @return
	 */
	public final double getRank(RuleData rule)
	{
		double rank = getClassifierRank(rule) + getRuleBonus(rule);
		// rank must be between 0 - 1 
		if (rank > 1)
		{
			return 1;
		}
		else if (rank < 0)
		{
			return 0;
		}
		else
		{
			return rank;
		}
	}

	/**
	 * return rank, adds the relevant bouns, but make sure it's between 0 - 1 
	 * @param rule
	 * @return
	 */
	public final double getRank(RedisRuleData rule)
	{
		double rank = getClassifierRank(rule) + getRuleBonus(rule);
		// rank must be between 0 - 1 
		if (rank > 1)
		{
			return 1;
		}
		else if (rank < 0)
		{
			return 0;
		}
		else
		{
			return rank;
		}
	}
	
	public String getClassifierUniqueName() {
		return this.getClass().getSimpleName();
	}
	
	
	/**
	 * The function check if already got the classifierID, if so- return it. if not- get it from DB
	 * @return
	 * @throws SQLException
	 */
	public int getClassifierId() throws SQLException	//will check if id is null, if so- get it from DB, if not, return it...
	{
		if (classifierID < 0)
		{
			classifierID = m_retrivalTool.getClassifierId(getClassifierUniqueName(), false);
		}
		return classifierID;
	}
	
	public int getSimpleClassifierId() 
	{
		return classifierID;
	}
	
	public void setClassifierId(int classifierID) {
		this.classifierID= classifierID;	
	}
	
	/** 
	 * An optional function, to return a bonus to the ClassifierRank, in special cases,
	 * by default it returns m_NPBonus (from config file) if it's a NP phrase
	 * if no need for a bonus- 
	 * @param rule
	 * @return	by default it returns m_NPBonus (from config file), it can also return negative results, if wishes
	 * 			
	 */
	protected double getRuleBonus(RuleData rule)
	{
		if (rule.getRuleMetadata().contains("m_isNPphrase=true"))
		{
			return m_NPBonus;
		}
		else
		{
			return 0;
		}
	}		

	
	protected double getRuleBonus(RedisRuleData rule)
	{
		if (rule.getIsBonus())
		{
			return m_NPBonus;
		}
		else
		{
			return 0;
		}
	}		

	
	
}
