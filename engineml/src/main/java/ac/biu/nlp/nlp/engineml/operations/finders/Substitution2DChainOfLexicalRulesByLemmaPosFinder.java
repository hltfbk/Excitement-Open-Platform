package ac.biu.nlp.nlp.engineml.operations.finders;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import ac.biu.nlp.nlp.engineml.datastructures.CanonicalLemmaAndPos;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ChainOfLexicalRules;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.LexicalRuleWithName;
import ac.biu.nlp.nlp.engineml.operations.specifications.RuleSubstituteNodeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NodeInfo;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;

/**
 * Finds lexical rules of type {@link ChainOfLexicalRules} that can be applied on the given tree and their right-hand-side
 * also matches the hypothesis (this is the meaning of "2D").
 * 
 * 
 * @author Asher Stern
 * @since Feb 20, 2012
 *
 */
public class Substitution2DChainOfLexicalRulesByLemmaPosFinder extends Substitution2DLexicalRuleByLemmaPosFinder<ChainOfLexicalRules>
{
	
	
//	public Substitution2DChainOfLexicalRulesByLemmaPosFinder(
//			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap,
//			ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> ruleBase,
//			String ruleBaseName, ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmas,
//			ImmutableSet<String> hypothesisLemmasOnly) throws OperationException
//	{
//		super(treeAndParentMap, ruleBase, ruleBaseName, hypothesisLemmas, hypothesisLemmasOnly);
//	}

	public Substitution2DChainOfLexicalRulesByLemmaPosFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap,
			ByLemmaPosLexicalRuleBase<ChainOfLexicalRules> ruleBase,
			String ruleBaseName, boolean filterLeftStopWords,
			boolean filterRightStopWords, ImmutableSet<String> stopWords,
			ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmas,
			ImmutableSet<String> hypothesisLemmasOnly)
			throws OperationException
	{
		super(treeAndParentMap, ruleBase, ruleBaseName, filterLeftStopWords,
				filterRightStopWords, stopWords, hypothesisLemmas, hypothesisLemmasOnly);
	}


	@Override
	protected RuleSubstituteNodeSpecification<ChainOfLexicalRules> createSpec(ExtendedNode node, NodeInfo newNodeInfo, String ruleBaseName, ChainOfLexicalRules rule)
	{
		return createSpec(node,newNodeInfo,ruleBaseName,rule,false);
	}


	@Override
	protected void addAdditionalDescription(RuleSubstituteNodeSpecification<ChainOfLexicalRules> spec,ChainOfLexicalRules rule)
	{
		StringBuilder sb = new StringBuilder();
		for (LexicalRuleWithName realRule : rule.getChain())
		{
			sb.append("[").append(realRule.getRuleBaseName()).append(" ");
			sb.append(realRule.getRule().getLhsLemma());
			sb.append("/").append(simplerPos(realRule.getRule().getLhsPos().getCanonicalPosTag()).name());
			sb.append("==>");
			sb.append(realRule.getRule().getRhsLemma());
			sb.append("/").append(simplerPos(realRule.getRule().getRhsPos().getCanonicalPosTag()).name());
			sb.append(" ");
			sb.append(String.format("%-4.4f", realRule.getRule().getConfidence()));
			sb.append("] ");
		}
		spec.addDescription(sb.toString());
	}


}
