package ac.biu.nlp.nlp.engineml.operations.finders;

import java.util.Set;

import ac.biu.nlp.nlp.engineml.datastructures.CanonicalLemmaAndPos;
import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.rules.ByLemmaPosLexicalRuleBase;
import ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NamedEntity;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;


/**
 * Finder for lexical rules that is restricted only to nodes
 * that are annotated by certain {@link NamedEntity}'ies, as given
 * by the parameter <code>namedEntities</code> given in the constructor.
 * 
 * @author Asher Stern
 * @since Apr 5, 2012
 *
 */
public class Substitution2DLexicalRuleByLemmaPosNerFinder extends Substitution2DLexicalRuleByLemmaPosFinder<LexicalRule>
{
	

//	public Substitution2DLexicalRuleByLemmaPosNerFinder(
//			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap,
//			ByLemmaPosLexicalRuleBase<LexicalRule> ruleBase,
//			String ruleBaseName,
//			ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmas,
//			ImmutableSet<String> hypothesisLemmasOnly,
//			Set<NamedEntity> namedEntities) throws OperationException
//	{
//		super(treeAndParentMap, ruleBase, ruleBaseName, hypothesisLemmas,
//				hypothesisLemmasOnly);
//		this.namedEntities = namedEntities;
//	}
	
	public Substitution2DLexicalRuleByLemmaPosNerFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> treeAndParentMap,
			ByLemmaPosLexicalRuleBase<LexicalRule> ruleBase,
			String ruleBaseName, boolean filterLeftStopWords,
			boolean filterRightStopWords, ImmutableSet<String> stopWords,
			ImmutableSet<CanonicalLemmaAndPos> hypothesisLemmas,
			ImmutableSet<String> hypothesisLemmasOnly,
			Set<NamedEntity> namedEntities)
			throws OperationException
	{
		super(treeAndParentMap, ruleBase, ruleBaseName, filterLeftStopWords,
				filterRightStopWords, stopWords, hypothesisLemmas, hypothesisLemmasOnly);
		this.namedEntities = namedEntities;
	}



	@Override
	protected boolean isRelevantNode(ExtendedNode node)
	{
		if (super.isRelevantNode(node))
		{
			boolean ret = false;
			NamedEntity ne = InfoGetFields.getNamedEntityAnnotation(node.getInfo());
			if (this.namedEntities.contains(ne))
			{
				ret=true;
			}
			else
			{
				ret = false;
			}
			
			return ret;
		}
		else
		{
			return false;
		}
	}

	
	
	private Set<NamedEntity> namedEntities;
}
