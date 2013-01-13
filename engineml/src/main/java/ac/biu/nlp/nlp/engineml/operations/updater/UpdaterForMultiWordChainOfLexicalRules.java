package ac.biu.nlp.nlp.engineml.operations.updater;
import java.util.Map;

import ac.biu.nlp.nlp.engineml.operations.operations.GenerationOperation;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ChainOfRulesWithConfidenceAndDescription;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ConfidenceChainItem;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * 
 * @author Asher Stern
 * 
 *
 */
public class UpdaterForMultiWordChainOfLexicalRules extends FeatureVectorUpdater<RuleSpecification>
{

	@Override
	public Map<Integer, Double> updateFeatureVector(
			Map<Integer, Double> originalFeatureVector,
			FeatureUpdate featureUpdate,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GenerationOperation<ExtendedInfo, ExtendedNode> operation,
			RuleSpecification specification) throws TeEngineMlException
	{
		// TODO get rid of this RTTI
		if (specification.getRule() instanceof ChainOfRulesWithConfidenceAndDescription)
		{
			ChainOfRulesWithConfidenceAndDescription<Info,BasicNode> chainOfRules =
				(ChainOfRulesWithConfidenceAndDescription<Info,BasicNode>) specification.getRule();
			
			Map<Integer,Double> featureVector = featureUpdate.forChainOfRules(originalFeatureVector, chainOfRules.getConfidences());
			addChainInformationToSpecification(specification, chainOfRules);
			return featureVector;
		}
		else
		{
			throw new TeEngineMlException("RTTI Failure. When generating trees for" +
					"multi-word lexical rules that were given from a chain of lexical rules," +
					"the rules were not chain of rules.\n" +
					"The obviously correct solution is to get rid of this RTTI.");
		}
		
	}
	
	private void addChainInformationToSpecification(RuleSpecification spec, ChainOfRulesWithConfidenceAndDescription<Info,BasicNode> chain)
	{
		StringBuffer sb = new StringBuffer();
		boolean firstIteration=true;
		for (ConfidenceChainItem item : chain.getConfidences())
		{
			if (firstIteration)firstIteration=false;
			else sb.append(", ");
			sb.append(item.getRuleBaseName());
			sb.append("(");
			sb.append(String.format("%-3.3f", item.getConfidence()));
			sb.append(")");
		}
		spec.addDescription(sb.toString());
	}

	

}
