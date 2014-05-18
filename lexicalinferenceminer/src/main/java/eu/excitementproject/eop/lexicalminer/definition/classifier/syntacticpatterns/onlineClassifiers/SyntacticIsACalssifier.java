package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.onlineClassifiers;

import eu.excitementproject.eop.lexicalminer.LexiclRulesRetrieval.RuleData;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.OnlineClassifier;
import eu.excitementproject.eop.lexicalminer.redis.RedisRuleData;

/**
 * The Classifier returns 1 rank to all patterns that are "is a" (in the syntactic format), and 0 otherwise
 * @author jmiron1
 *
 */
public class SyntacticIsACalssifier extends OnlineClassifier {

	public SyntacticIsACalssifier(RetrievalTool retrivalTool, Double NPBonus) {
		super(retrivalTool, NPBonus);
	}

	@Override
	public double getClassifierRank(RuleData rule) {
		
		//it's a "IS A" pattern
		if (rule.getPOSPattern().equals("NN") || rule.getPOSPattern().equals("NNS"))
		{
			return 1;
		}
		
		
		return 0;
	}
	
	@Override
	public double getClassifierRank(RedisRuleData rule) {
		
		//it's a "IS A" pattern
		if (rule.getPOSPattern().equals("NN") || rule.getPOSPattern().equals("NNS"))
		{
			return 1;
		}
		
		
		return 0;
	}
	
	@Override
	/**
	 * 	don't add a bonus in that classifier
	 */
	protected double getRuleBonus(RuleData rule)
	{
		return 0;
	}	
	
}


