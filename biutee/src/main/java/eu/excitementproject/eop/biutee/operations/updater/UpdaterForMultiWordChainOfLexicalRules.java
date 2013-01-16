package eu.excitementproject.eop.biutee.operations.updater;
import java.util.Map;

import eu.excitementproject.eop.biutee.rteflow.macro.FeatureUpdate;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.transformations.operations.operations.GenerationOperation;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfRulesWithConfidenceAndDescription;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ConfidenceChainItem;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


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
