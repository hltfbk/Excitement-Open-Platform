package eu.excitementproject.eop.lap.biu.en.pasta.utils;


import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.RelationTypes;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

/**
 * 
 * @author Asher Stern
 * @since Aug 6, 2013
 *
 */
@StandardSpecific("stanford-dependencies") // All the methods here are supposed to be restricted to Stanford-dependencies
public class IdentificationStaticMethods
{

	public static <I extends Info, S extends AbstractNode<I, S>> Set<S> getInternalNodes(S node)
	{
		Set<S> ret = new LinkedHashSet<S>();
		ret.add(node);
		if (node.hasChildren())
		{
			for (S child : node.getChildren())
			{
				String relation = InfoGetFields.getRelation(child.getInfo());
				if (RelationTypes.getSemanticInternalFacetRelations().contains(relation))
				{
					ret.addAll(getInternalNodes(child));
				}
			}
		}
		return ret;
	}
	
	public static <I extends Info, S extends AbstractNode<I, S>>
	Set<PredicateArgumentStructure<I, S>> mergeStructureSets(TreeAndParentMap<I, S> tree, Set<PredicateArgumentStructure<I, S>> firstStructures, Set<PredicateArgumentStructure<I, S>> secondStructures) throws PredicateArgumentIdentificationException
	{
		Set<PredicateArgumentStructure<I, S>> ret = new LinkedHashSet<>();
		Map<S,PredicateArgumentStructure<I, S>> secondPredicateHeads = new LinkedHashMap<>();
		for (PredicateArgumentStructure<I, S> structureInSecond : secondStructures)
		{
			S head = structureInSecond.getPredicate().getHead();
			if (secondPredicateHeads.keySet().contains(head)) throw new PredicateArgumentIdentificationException("Bug: the same predicate head is the head of two different predicates.");
			secondPredicateHeads.put(head,structureInSecond);
		}

		Set<PredicateArgumentStructure<I, S>> second_alreadyHandeled = new LinkedHashSet<>();
		for (PredicateArgumentStructure<I, S> structureInFirst : firstStructures)
		{
			S head = structureInFirst.getPredicate().getHead();
			if (secondPredicateHeads.keySet().contains(head))
			{
				PredicateArgumentStructure<I, S> structureInSecond = secondPredicateHeads.get(head);
				second_alreadyHandeled.add(structureInSecond);
				ret.add(new StructuresMerger<>(tree, structureInFirst, structureInSecond).merge());
			}
			else
			{
				ret.add(structureInFirst);
			}
		}
		
		for (PredicateArgumentStructure<I, S> structureInSecond : secondStructures)
		{
			if (!(second_alreadyHandeled.contains(structureInSecond)))
			{
				ret.add(structureInSecond);
			}
		}
		
		return ret;
	}
}
