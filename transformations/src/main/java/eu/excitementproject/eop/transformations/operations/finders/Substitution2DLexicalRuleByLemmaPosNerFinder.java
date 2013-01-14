package eu.excitementproject.eop.transformations.operations.finders;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.datastructures.CanonicalLemmaAndPos;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


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
