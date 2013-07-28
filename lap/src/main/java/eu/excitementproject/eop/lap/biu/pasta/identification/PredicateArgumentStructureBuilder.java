package eu.excitementproject.eop.lap.biu.pasta.identification;

import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;

/**
 * Given a parse-tree, this class builds a set with all of the predicate-argument structures that exist in that tree. 
 * @author Asher Stern
 * @since Oct 14, 2012
 *
 * @param <I>
 * @param <S>
 */
public abstract class PredicateArgumentStructureBuilder<I extends Info, S extends AbstractNode<I, S>>
{
	/**
	 * The constructor gets the tree, and a "nomlex map" - which is created by the class {@link NomlexMapBuilder}.
	 * @param tree
	 * @param nomlexMap
	 */
	public PredicateArgumentStructureBuilder(TreeAndParentMap<I, S> tree)
	{
		super();
		this.tree = tree;
	}
	
	public abstract void build() throws PredicateArgumentIdentificationException;
	
	public abstract Set<PredicateArgumentStructure<I, S>> getPredicateArgumentStructures() throws PredicateArgumentIdentificationException;


	protected final TreeAndParentMap<I,S> tree;
}
