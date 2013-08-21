package eu.excitementproject.eop.lap.biu.en.pasta;


import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.Nominalization;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexMapBuilder;

/**
 * Finds all the nominal-predicates in a given parse tree.
 * 
 * @author Asher Stern
 * @since Oct 15, 2012
 *
 * @param <I>
 * @param <S>
 */
public class NominalPredicateHeadsIdentifier<I extends Info, S extends AbstractNode<I, S>>
{
	/**
	 * The constructor is given the parse tree, and Nomlex-map which is built by {@link NomlexMapBuilder}.
	 * @param tree
	 * @param nomlexMap
	 */
	public NominalPredicateHeadsIdentifier(TreeAndParentMap<I, S> tree,
			ImmutableMap<String, Nominalization> nomlexMap)
	{
		super();
		this.tree = tree;
		this.nomlexMap = nomlexMap;
	}


	/**
	 * Finds the nominal predicates in the parse tree.
	 */
	public void identify()
	{
		predicateHeads = new LinkedHashSet<S>();
		for (S node : TreeIterator.iterableTree(tree.getTree()))
		{
			if (SimplerCanonicalPosTag.NOUN.equals(SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(node.getInfo()))))
			{
				if (nomlexMap.keySet().contains(InfoGetFields.getLemma(node.getInfo()).trim().toLowerCase(Locale.ENGLISH)))
				{
					predicateHeads.add(node);
				}
			}
		}
	}
	

	/**
	 * Returns the predicates, that have been found by {@link #identify()}.
	 * @return
	 */
	public Set<S> getPredicateHeads()
	{
		return predicateHeads;
	}


	private final TreeAndParentMap<I, S> tree;
	private final ImmutableMap<String, Nominalization> nomlexMap;
	
	private Set<S> predicateHeads;
}
