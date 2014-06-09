package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * Represents an argument node (the syntactic head of the argument) and
 * the path from the predicate-head to that (syntactic head of the) argument.
 * 
 * @see ArgumentIdentificationUtilities#getArgumentNodes(ac.biu.nlp.nlp.predarg.representations.Predicate)
 * 
 * @author Asher Stern
 * @since 8 October 2012
 *
 */
public class ArgumentNodeAndPathFromPredicate<I extends Info, S extends AbstractNode<I, S>>
{
	public ArgumentNodeAndPathFromPredicate(List<S> pathFromPredicateToArgument, S syntacticArgumentNode)
	{
		super();
		this.pathFromPredicateToArgument = pathFromPredicateToArgument;
		this.syntacticArgumentNode = syntacticArgumentNode;
	}
	
	
	public List<S> getPathFromPredicateToArgument()
	{
		return pathFromPredicateToArgument;
	}
	public S getSyntacticArgumentNode()
	{
		return syntacticArgumentNode;
	}


	private final List<S> pathFromPredicateToArgument;
	private final S syntacticArgumentNode;
}
