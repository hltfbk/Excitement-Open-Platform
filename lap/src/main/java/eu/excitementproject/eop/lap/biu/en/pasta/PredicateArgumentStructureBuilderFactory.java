package eu.excitementproject.eop.lap.biu.en.pasta;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.Nominalization;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.EasyFirstPredicateArgumentStructureBuilder;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.PastaMode;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentStructureBuilder;

/**
 * A factory that returns the default {@link PredicateArgumentStructureBuilder}.
 * For identification of nominal-predicates and their arguments, a parameter of
 * <code>nomlexMap</code> should be given. See {@link NomlexMapBuilder} about
 * that map.
 * 
 * @author Asher Stern
 * @since Oct 17, 2012
 * 
 * @see NomlexMapBuilder
 *
 * @param <I> The information type of every node in the parse tree.
 * @param <S> The type of every node in the parse-tree.
 */
public class PredicateArgumentStructureBuilderFactory<I extends Info, S extends AbstractNode<I, S>>
{
	public PredicateArgumentStructureBuilderFactory(ImmutableMap<String, Nominalization> nomlexMap, PastaMode mode)
	{
		super();
		this.nomlexMap = nomlexMap;
		this.mode = mode;
	}

	public PredicateArgumentStructureBuilderFactory(ImmutableMap<String, Nominalization> nomlexMap)
	{
		this(nomlexMap,PastaMode.BASIC);
	}
	
	public PastaMode getMode()
	{
		return mode;
	}

	public PredicateArgumentStructureBuilder<I,S> createBuilder(TreeAndParentMap<I, S> tree)
	{
		return new EasyFirstPredicateArgumentStructureBuilder<I, S>(tree, nomlexMap,mode);
	}

	private final ImmutableMap<String, Nominalization> nomlexMap;
	private final PastaMode mode;
}
