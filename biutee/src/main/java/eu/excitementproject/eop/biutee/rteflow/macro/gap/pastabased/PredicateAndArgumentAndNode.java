package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;

/**
 * 
 * @author Asher Stern
 * @since Aug 8, 2013
 *
 * @param <I>
 * @param <S>
 */
public class PredicateAndArgumentAndNode<I extends Info, S extends AbstractNode<I, S>>
{
	public PredicateAndArgumentAndNode(
			PredicateArgumentStructure<I, S> predicateStructure,
			TypedArgument<I, S> argument, S node)
	{
		super();
		this.predicateStructure = predicateStructure;
		this.argument = argument;
		this.node = node;
	}
	
	
	
	
	public PredicateArgumentStructure<I, S> getPredicateStructure()
	{
		return predicateStructure;
	}
	public TypedArgument<I, S> getArgument()
	{
		return argument;
	}
	public S getNode()
	{
		return node;
	}




	private final PredicateArgumentStructure<I, S> predicateStructure;
	private final TypedArgument<I, S> argument;
	private final S node;
}
