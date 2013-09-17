package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.verbals;

import static eu.excitementproject.eop.lap.biu.en.pasta.utils.IdentificationStaticMethods.getInternalNodes;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

/**
 * Finds predicates of copular clauses.
 * For example "John is a boy" - the predicate is "boy".
 * <P>
 * Such predicates are identified by detecting a child of the predicate-head
 * which is connected to its parent with relation "cop". In the above example
 * the node of "is" is connected via "cop" to "boy".
 * <BR>
 * Note that copulas are not always "to be". sometimes "become" is a copular
 * as well (and perhaps also other words. I don't know). 
 * 
 * @author Asher Stern
 * @since Aug 6, 2013
 *
 * @param <I>
 * @param <S>
 */
@StandardSpecific("stanford-dependencies")
public class CopularPredicatesIdentifier<I extends Info, S extends AbstractNode<I, S>>
{
	////////// PUBLIC ////////// 
	
	public static final String COPULA_RELATION = "cop";

	public CopularPredicatesIdentifier(TreeAndParentMap<I, S> tree)
	{
		super();
		this.tree = tree;
	}
	
	public void identify() throws PredicateArgumentIdentificationException
	{
		Set<S> predicateHeads = new LinkedHashSet<>();
		for (S node : TreeIterator.iterableTree(tree.getTree()))
		{
			if (hasCopula(node))
			{
				predicateHeads.add(node);
			}
		}
		
		copularPredicates = new LinkedHashSet<>();
		for (S head : predicateHeads)
		{
			copularPredicates.add(new Predicate<I, S>(getInternalNodes(head), head));
		}
	}
	
	
	
	public Set<Predicate<I, S>> getCopularPredicates() throws PredicateArgumentIdentificationException
	{
		if (null==copularPredicates) throw new PredicateArgumentIdentificationException("Not yet identified.");
		return copularPredicates;
	}



	////////// PRIVATE ////////// 
	
	private boolean hasCopula(S node)
	{
		if (node.hasChildren())
		{
			for (S child : node.getChildren())
			{
				String relation = InfoGetFields.getRelation(child.getInfo());
				if (COPULA_RELATION.equals(relation))
				{
					return true;
				}
			}
		}
		return false;
	}
	

	private final TreeAndParentMap<I,S> tree;
	
	private Set<Predicate<I, S>> copularPredicates = null;
}
