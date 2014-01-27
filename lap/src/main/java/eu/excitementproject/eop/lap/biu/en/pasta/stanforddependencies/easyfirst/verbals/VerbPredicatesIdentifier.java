package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.easyfirst.verbals;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.common.representation.pasta.Predicate;
import eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies.RelationTypes;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

import static eu.excitementproject.eop.lap.biu.en.pasta.utils.IdentificationStaticMethods.getInternalNodes;

/**
 * 
 * @author Asher Stern
 * @since Oct 9, 2012
 *
 */
@StandardSpecific("stanford-dependencies")
public class VerbPredicatesIdentifier<I extends Info, S extends AbstractNode<I, S>>
{
	////////// PUBLIC //////////
	
	public VerbPredicatesIdentifier(TreeAndParentMap<I, S> tree)
	{
		super();
		this.tree = tree;
	}

	public void identifyVerbPredicates()
	{
		verbPredicates = new LinkedHashSet<Predicate<I,S>>();
		Set<S> verbPredicateHeads = findVerbPredicateHeads();
		for (S head : verbPredicateHeads)
		{
			verbPredicates.add(new Predicate<I, S>(getInternalNodes(head), head));
		}
	}
	
	
	public Set<Predicate<I, S>> getVerbPredicates() throws PredicateArgumentIdentificationException
	{
		if (null==verbPredicates) throw new PredicateArgumentIdentificationException("Please call identifyVerbPredicates() before calling this method.");
		return verbPredicates;
	}

	
	////////// PRIVATE //////////


	private Set<S> findVerbPredicateHeads()
	{
		Set<S> verbPredicateHeads = new LinkedHashSet<S>();
		for (S node : TreeIterator.iterableTree(tree.getTree()))
		{
			if (SimplerCanonicalPosTag.VERB.equals(SimplerPosTagConvertor.simplerPos(InfoGetFields.getCanonicalPartOfSpeech(node.getInfo()))))
			{
				String relation = InfoGetFields.getRelation(node.getInfo());
				if (!RelationTypes.getSemanticAuxiliaryVerbRelations().contains(relation))
				{
					verbPredicateHeads.add(node);
				}
			}
		}
		return verbPredicateHeads;
	}
	

	private final TreeAndParentMap<I,S> tree;
	
	private Set<Predicate<I, S>> verbPredicates = null;
}
