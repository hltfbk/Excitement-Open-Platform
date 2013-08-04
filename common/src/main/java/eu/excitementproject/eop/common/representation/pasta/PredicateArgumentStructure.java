package eu.excitementproject.eop.common.representation.pasta;

import java.io.Serializable;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * Represents a predicate and all of its arguments.
 * @author Asher Stern
 * @since Oct 4, 2012
 *
 */
public class PredicateArgumentStructure<I extends Info, S extends AbstractNode<I, S>> implements Serializable
{
	private static final long serialVersionUID = 6926203306760518515L;
	
	public PredicateArgumentStructure(TreeAndParentMap<I, S> tree,
			Predicate<I, S> predicate, Set<TypedArgument<I, S>> arguments,
			Set<ClausalArgument<I, S>> clausalArguments)
	{
		super();
		this.tree = tree;
		this.predicate = predicate;
		this.arguments = arguments;
		this.clausalArguments = clausalArguments;
	}
	
	
	
	public TreeAndParentMap<I, S> getTree()
	{
		return tree;
	}
	public Predicate<I, S> getPredicate()
	{
		return predicate;
	}
	public Set<TypedArgument<I, S>> getArguments()
	{
		return arguments;
	}
	public Set<ClausalArgument<I, S>> getClausalArguments()
	{
		return clausalArguments;
	}

	@Override
	public String toString() {
		return PredicateArgumentStructurePrinter.getString(this);
	}


	private final TreeAndParentMap<I, S> tree;
	private final Predicate<I, S> predicate;
	private final Set<TypedArgument<I, S>> arguments;
	private final Set<ClausalArgument<I, S>> clausalArguments;
}
