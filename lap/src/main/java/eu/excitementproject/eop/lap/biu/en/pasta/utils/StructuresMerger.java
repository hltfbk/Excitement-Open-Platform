package eu.excitementproject.eop.lap.biu.en.pasta.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.ClausalArgument;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

/**
 * 
 * @author Asher Stern
 * @since Aug 6, 2013
 *
 * @param <I>
 * @param <S>
 */
public class StructuresMerger<I extends Info, S extends AbstractNode<I,S>>
{
	////////// PUBLIC //////////
	
	public StructuresMerger(TreeAndParentMap<I, S> tree,
			PredicateArgumentStructure<I, S> firstStructure,
			PredicateArgumentStructure<I, S> secondStructure)
	{
		super();
		this.tree = tree;
		this.firstStructure = firstStructure;
		this.secondStructure = secondStructure;
	}

	public PredicateArgumentStructure<I, S> merge() throws PredicateArgumentIdentificationException
	{
		if (firstStructure.getPredicate().getHead()!=secondStructure.getPredicate().getHead())
			throw new PredicateArgumentIdentificationException("Cannot merge structures that do not have the same predicate-head.");
		
		argumentNodesOfFirst = getAllArgumentNodes(firstStructure);
		argumentNodesOfSecond = getAllArgumentNodes(secondStructure);
		
		Set<TypedArgument<I, S>> arguments = new LinkedHashSet<>();
		arguments.addAll(firstStructure.getArguments());
		for (TypedArgument<I, S> argumentInSecond : secondStructure.getArguments())
		{
			if (!(argumentNodesOfFirst.contains(argumentInSecond.getArgument().getSemanticHead())))
			{
				arguments.add(argumentInSecond);
			}
		}
		
		Set<ClausalArgument<I, S>> clausalArguments = new LinkedHashSet<>();
		clausalArguments.addAll(firstStructure.getClausalArguments());
		for (ClausalArgument<I, S> argumentInSecond : secondStructure.getClausalArguments())
		{
			if (!(argumentNodesOfFirst.contains(argumentInSecond.getClause())))
			{
				clausalArguments.add(argumentInSecond);
			}
		}
		
		Set<S> predicateNodes = new LinkedHashSet<>();
		for (S node : firstStructure.getPredicate().getNodes())
		{
			if (!(argumentNodesOfSecond.contains(node)))
			{
				predicateNodes.add(node);
			}
		}
		
		Predicate<I, S> predicate = new Predicate<I, S>(predicateNodes,firstStructure.getPredicate().getHead());
		
		PredicateArgumentStructure<I, S> ret = new PredicateArgumentStructure<I, S>(tree,predicate,arguments,clausalArguments);
		
		return ret;
	}
	
	////////// PRIVATE //////////
	
	private Set<S> getAllArgumentNodes(PredicateArgumentStructure<I, S> structure)
	{
		Set<S> ret = new LinkedHashSet<>();
		for (TypedArgument<I, S> argument : structure.getArguments())
		{
			ret.addAll(argument.getArgument().getNodes());
		}
		for (ClausalArgument<I, S> argument : structure.getClausalArguments())
		{
			ret.addAll(AbstractNodeUtils.treeToLinkedHashSet(argument.getClause()));
		}
		return ret;
	}

	private final TreeAndParentMap<I, S> tree;
	private final PredicateArgumentStructure<I, S> firstStructure;
	private final PredicateArgumentStructure<I, S> secondStructure;
	
	private Set<S> argumentNodesOfFirst=null;
	private Set<S> argumentNodesOfSecond=null;
}
