package eu.excitementproject.eop.transformations.utilities.parsetreeutils;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.srl_informations.SemanticRoleLabelById;
import eu.excitementproject.eop.transformations.representation.srl_informations.SemanticRoleLabelByString;
import eu.excitementproject.eop.transformations.representation.srl_informations.SemanticRoleLabelSet;
import eu.excitementproject.eop.transformations.representation.srl_informations.SrlPredicateId;
import eu.excitementproject.eop.transformations.representation.srl_types.SemanticRole;
import eu.excitementproject.eop.transformations.representation.srl_types.SemanticRoleType;

/**
 * 
 * @author Asher Stern
 * @since Dec 27, 2011
 *
 */
public class FindUnmatchedSrlUtility
{
	public FindUnmatchedSrlUtility(ExtendedNode generatedTree,
			ExtendedNode movedTextNode,
			ImmutableSet<SemanticRoleLabelByString> hypothesisNodeSrl)
	{
		super();
		this.generatedTree = generatedTree;
		this.movedTextNode = movedTextNode;
		this.hypothesisNodeSrl = hypothesisNodeSrl;
	}

	public void find()
	{
		SrlPredicateIdMapper srlPredicateMapper = new SrlPredicateIdMapper(generatedTree);
		srlPredicateMapper.map();
		Map<SrlPredicateId, String> mapIdToLemma = srlPredicateMapper.getMapIdToLemma();
		
		Set<SemanticRoleLabelByString> matching = new LinkedHashSet<SemanticRoleLabelByString>();
		
		SemanticRoleLabelSet srlSet = ExtendedInfoGetFields.getSemanticRoleLabelSet(movedTextNode.getInfo());
		for (SemanticRoleLabelById srl : srlSet.getSrlById())
		{
			if (mapIdToLemma.containsKey(srl.getPredicateId()))
			{
				String predicateString = mapIdToLemma.get(srl.getPredicateId());
				if (areMatch(predicateString, srl.getSemanticRole()))
				{
					matching.add(new SemanticRoleLabelByString(srl.getSemanticRole(),predicateString));
				}
			}
		}
		for (SemanticRoleLabelByString srl : srlSet.getSrlByString())
		{
			if (areMatch(srl.getPredicateString(),srl.getSemanticRole()))
			{
				matching.add(srl);
			}
		}
		
		
		unmathced = new LinkedHashSet<SemanticRoleLabelByString>();
		for (SemanticRoleLabelByString hypothesisSrl : hypothesisNodeSrl)
		{
			if (!matching.contains(hypothesisSrl))
			{
				unmathced.add(hypothesisSrl);
			}
		}
	}
	
	
	
	public Set<SemanticRoleLabelByString> getUnmathced()
	{
		return unmathced;
	}

	private boolean areMatch(String predicateLemma, SemanticRole semanticRole)
	{
		boolean ret = false;
		for (SemanticRoleLabelByString srlHypothesis : hypothesisNodeSrl)
		{
			if (srlHypothesis.getPredicateString().equalsIgnoreCase(predicateLemma))
			{
				if (semanticRoleMatch(semanticRole,srlHypothesis.getSemanticRole()))
				{
					ret = true;
					break;
				}
				
			}
		}
		return ret;
	}
	
	private boolean semanticRoleMatch(SemanticRole textSR, SemanticRole hypothesisSR)
	{
		boolean match = false;
		if (textSR.getType().equals(SemanticRoleType.ARGUMENT))
		{
			if (hypothesisSR.getType().equals(SemanticRoleType.ARGUMENT))
			{
				if (textSR.getArgumentNumber()==hypothesisSR.getArgumentNumber())
					match = true;
				else if ( (textSR.getArgumentNumber()!=0) && (hypothesisSR.getArgumentNumber()!=0) )
					match = true;
			}
			else if ( (hypothesisSR.getType().equals(SemanticRoleType.ARGUMENT_CAUSER)) && (textSR.getArgumentNumber()!=0) )
				match = true;
		}
		else if (textSR.getType().equals(SemanticRoleType.ARGUMENT_CAUSER))
		{
			if (hypothesisSR.getType().equals(SemanticRoleType.ARGUMENT_CAUSER))
				match = true;
			else if ( (hypothesisSR.getType().equals(SemanticRoleType.ARGUMENT)) && (hypothesisSR.getArgumentNumber()!=0) )
				match = true;
		}
		else if ( (textSR.getType().equals(SemanticRoleType.MODIFIER)) && (hypothesisSR.getType().equals(SemanticRoleType.MODIFIER)) )
		{
			match = true;
		}
		
		return match;
	}
	
	// input
	private ExtendedNode generatedTree;
	private ExtendedNode movedTextNode;
	private ImmutableSet<SemanticRoleLabelByString> hypothesisNodeSrl;
	
	// output
	private Set<SemanticRoleLabelByString> unmathced;
}
