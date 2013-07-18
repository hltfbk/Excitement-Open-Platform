package eu.excitementproject.eop.biutee.rteflow.micro;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.transformations.datastructures.DsUtils;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.lexicalchain.ChainOfLexicalRules;
import eu.excitementproject.eop.transformations.operations.specifications.InsertNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.MoveNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.operations.specifications.SubstituteNodeSpecification;
import eu.excitementproject.eop.transformations.operations.specifications.SubstituteNodeSpecificationMultiWord;
import eu.excitementproject.eop.transformations.operations.specifications.SubstitutionSubtreeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * Given a set of {@link Specification}s and a set of "affected nodes", this class
 * removes from the set of {@linkplain Specification}s all of the specifications
 * that specify operations that do <B>not</B> operate on the "affected nodes".
 * <P>
 * This class is used for local-creative algorithm.
 * <P>
 * <B>
 * TODO: I have to delete all methods and use only {@link #filterSpecifications(Set)}.
 * </B> 
 * 
 * @see TreesGeneratorByOperations
 * 
 * @author Asher Stern
 * @since Jul 24, 2011
 *
 */
public class FilterSpecifications
{
	// TODO - this constant is only temporary, until I am sure that filterSpecifications() is OK.
	public static final boolean DEBUG_MODE = false;
	
	public FilterSpecifications(ExtendedNode tree,
			Set<ExtendedNode> affectedNodes) throws TeEngineMlException
	{
		super();
		this.tree = tree;
		this.affectedNodes = affectedNodes;
		// TODO after being convinced that the code is stable - this verification
		// may be removed.
		verifyAffectedNodes();
	}



	
	public <T extends Specification> Set<T> filterSpecifications(Set<T> specs)
	{
		Set<T> ret = new LinkedHashSet<T>();
		for (T spec : specs)
		{
			if (DsUtils.intersectionNotEmpty(spec.getInvolvedNodesInTree(), affectedNodes))
				ret.add(spec);
		}
		if (false==debugFilterSpecifications(specs,ret)) throw new RuntimeException("Debug of filter specification found an error!");
		return ret;
	}
	
	

	// TODO - this is only temporary, until I am sure that filterSpecifications() is OK.
	@SuppressWarnings("unchecked")
	private <T extends Specification> boolean debugFilterSpecifications(Set<T> specs, Set<T> filtered)
	{
		boolean ret = true;
		if (DEBUG_MODE)
		{
			if (specs.size()>0)
			{
				T firstSpec = specs.iterator().next();
				if (firstSpec instanceof InsertNodeSpecification)
				{
					ret = equalsNullNonNull(filterInsertNodeSpecifications((Set<InsertNodeSpecification>)specs),filtered);
				}
				else if (firstSpec instanceof MoveNodeSpecification)
				{
					ret = equalsNullNonNull(filterMoveNodeSpecifications((Set<MoveNodeSpecification>)specs),filtered);
				}
				else if (firstSpec instanceof RuleSpecification)
				{
					ret = equalsNullNonNull(filterRuleSpecifications((Set<RuleSpecification>)specs),filtered);
				}
				else if (firstSpec instanceof RuleSubstituteNodeSpecification)
				{
					if (((RuleSubstituteNodeSpecification<?>) firstSpec).getRule() instanceof ChainOfLexicalRules)
					{
						ret = equalsNullNonNull(filterChainOfRuleSubstitueNodeSpecifications((Set<RuleSubstituteNodeSpecification<ChainOfLexicalRules>>)specs),filtered);
					}
					else
					{
						ret = equalsNullNonNull(filterRuleSubstitueNodeSpecifications((Set<RuleSubstituteNodeSpecification<LexicalRule>>)specs),filtered);
					}
				}
				else if (firstSpec instanceof SubstituteNodeSpecificationMultiWord)
				{
					ret = equalsNullNonNull(filterSubstitueNodeSpecificationsMultiWord((Set<SubstituteNodeSpecificationMultiWord>)specs),filtered);
					
				}
				else if (firstSpec instanceof SubstituteNodeSpecification)
				{
					ret = equalsNullNonNull(filterSubstitueNodeSpecifications((Set<SubstituteNodeSpecification>)specs),filtered);
				}
				else if (firstSpec instanceof SubstitutionSubtreeSpecification)
				{
					ret = equalsNullNonNull(filterSubstitutionSubtreeSpecifications((Set<SubstitutionSubtreeSpecification>)specs),filtered);
				}
					
			}
			
			
			
			
		}
		return ret;
	}
	
	private <T> boolean equalsNullNonNull(T t1, T t2)
	{
		if (null==t1)return (null==t2);
		else return t1.equals(t2);
	}
	
	
	
	
	
	private Set<RuleSpecification> filterRuleSpecifications(Set<RuleSpecification> specs)
	{
		Set<RuleSpecification> ret = new LinkedHashSet<RuleSpecification>();
		for (RuleSpecification spec : specs)
		{
			for (ExtendedNode node : spec.getMapLhsToTree().rightSet())
			{
				if (affectedNodes.contains(node))
				{
					ret.add(spec);
					break;
				}
			}
		}
		return ret;
	}

	
	private Set<SubstituteNodeSpecification> filterSubstitueNodeSpecifications(Set<SubstituteNodeSpecification> specs)
	{
		Set<SubstituteNodeSpecification> ret = new LinkedHashSet<SubstituteNodeSpecification>();
		for (SubstituteNodeSpecification spec : specs)
		{
			if (affectedNodes.contains(spec.getTextNodeToBeSubstituted()))
			{
				ret.add(spec);
			}
		}
		return ret;
	}

	private Set<RuleSubstituteNodeSpecification<LexicalRule>> filterRuleSubstitueNodeSpecifications(Set<RuleSubstituteNodeSpecification<LexicalRule>> specs)
	{
		Set<RuleSubstituteNodeSpecification<LexicalRule>> ret = new LinkedHashSet<RuleSubstituteNodeSpecification<LexicalRule>>();
		for (RuleSubstituteNodeSpecification<LexicalRule> spec : specs)
		{
			if (affectedNodes.contains(spec.getTextNodeToBeSubstituted()))
			{
				ret.add(spec);
			}
		}
		return ret;
	}

	private Set<RuleSubstituteNodeSpecification<ChainOfLexicalRules>> filterChainOfRuleSubstitueNodeSpecifications(Set<RuleSubstituteNodeSpecification<ChainOfLexicalRules>> specs)
	{
		Set<RuleSubstituteNodeSpecification<ChainOfLexicalRules>> ret = new LinkedHashSet<RuleSubstituteNodeSpecification<ChainOfLexicalRules>>();
		for (RuleSubstituteNodeSpecification<ChainOfLexicalRules> spec : specs)
		{
			if (affectedNodes.contains(spec.getTextNodeToBeSubstituted()))
			{
				ret.add(spec);
			}
		}
		return ret;
	}

	private Set<SubstituteNodeSpecificationMultiWord> filterSubstitueNodeSpecificationsMultiWord(Set<SubstituteNodeSpecificationMultiWord> specs)
	{
		Set<SubstituteNodeSpecificationMultiWord> ret = new LinkedHashSet<SubstituteNodeSpecificationMultiWord>();
		for (SubstituteNodeSpecificationMultiWord spec : specs)
		{
			if (affectedNodes.contains(spec.getTextNodeToBeSubstituted()))
			{
				ret.add(spec);
			}
		}
		return ret;
	}

	private Set<InsertNodeSpecification> filterInsertNodeSpecifications(Set<InsertNodeSpecification> specs)
	{
		Set<InsertNodeSpecification> ret = new LinkedHashSet<InsertNodeSpecification>();
		for (InsertNodeSpecification spec : specs)
		{
			if (affectedNodes.contains(spec.getTextNodeToBeParent()))
			{
				ret.add(spec);
			}
			
		}
		return ret;
	}
	

	private Set<MoveNodeSpecification> filterMoveNodeSpecifications(Set<MoveNodeSpecification> specs)
	{
		Set<MoveNodeSpecification> ret = new LinkedHashSet<MoveNodeSpecification>();
		for (MoveNodeSpecification spec : specs)
		{
			if (affectedNodes.contains(spec.getTextNodeToBeParent()))
			{
				ret.add(spec);
			}
			else if (affectedNodes.contains(spec.getTextNodeToMove()))
			{
				ret.add(spec);
			}
			
		}
		return ret;
	}

	private Set<SubstitutionSubtreeSpecification> filterSubstitutionSubtreeSpecifications(Set<SubstitutionSubtreeSpecification> specs)
	{
		Set<SubstitutionSubtreeSpecification> ret = new LinkedHashSet<SubstitutionSubtreeSpecification>();
		for (SubstitutionSubtreeSpecification spec : specs)
		{
			if (affectedNodes.contains(spec.getSubtreeToAdd()))
				ret.add(spec);
			else if (affectedNodes.contains(spec.getSubtreeToRemove()))
				ret.add(spec);
		}
		return ret;
	}
	
	

	
	
	private void verifyAffectedNodes() throws TeEngineMlException
	{
		if (DEBUG_MODE)
		{
			if (affectedNodes.size()>0)
			{
				Set<ExtendedNode> treeNodes = AbstractNodeUtils.treeToLinkedHashSet(tree);
				// if (!treeNodes.retainAll(affectedNodes))throw new TeEngineMlException("Unknown problem");
				treeNodes.retainAll(affectedNodes);
				if (treeNodes.size()==0)
					throw new TeEngineMlException("Non of the affected nodes exist in the given tree - this is a caller\'s bug");
			}
		}
	}
	
	private ExtendedNode tree;
	private Set<ExtendedNode> affectedNodes;
}
