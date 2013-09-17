package eu.excitementproject.eop.biutee.rteflow.macro.gap.pastabased;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;

/**
 * A {@link PredicateArgumentStructure} with a boolean flag.
 * 
 * @author Asher Stern
 * @since Aug 28, 2013
 *
 * @param <I>
 * @param <S>
 */
public class FlaggedPredicateArgumentStructure<I extends Info, S extends AbstractNode<I, S>>
{
	public FlaggedPredicateArgumentStructure(boolean flag,
			PredicateArgumentStructure<I, S> predicateArgumentStructure)
	{
		super();
		this.flag = flag;
		this.predicateArgumentStructure = predicateArgumentStructure;
	}
	
	
	
	public boolean isFlag()
	{
		return flag;
	}
	public PredicateArgumentStructure<I, S> getPredicateArgumentStructure()
	{
		return predicateArgumentStructure;
	}



	private final boolean flag;
	private final PredicateArgumentStructure<I, S> predicateArgumentStructure;
}
