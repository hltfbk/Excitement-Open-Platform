package eu.excitementproject.eop.lap.biu.en.pasta.stanforddependencies;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.StandardSpecific;
import eu.excitementproject.eop.common.representation.pasta.ArgumentType;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;

/**
 * Groups all Stanford-dependencies into disjoint groups that indicate a semantic relation:
 * subject, object, modifier, internal-facet (which means that the nodes represent the same entity together), and
 * new-clause (with some more fine-grained types of new-clauses).
 * <P>
 * This class is used by {@link EasyFirstPredicateArgumentStructureBuilder},
 * to identify arguments and their types.
 * 
 * @author Asher Stern
 * @since Oct 4, 2012
 *
 */
@StandardSpecific("stanford-dependencies")
public class RelationTypes
{
	public static ArgumentType fromRelation(String relation)
	{
		ArgumentType argumentType = null;
		if (getSemanticSubjectRelations().contains(relation))
		{
			argumentType = ArgumentType.SUBJECT;
		}
		else if (getSemanticObjectRelations().contains(relation))
		{
			argumentType = ArgumentType.OBJECT;
		}
		else if (getSemanticModifierRelations().contains(relation))
		{
			argumentType = ArgumentType.MODIFIER;
		}
		else
		{
			argumentType = ArgumentType.UNKNOWN;
		}
		
		return argumentType;
	}
	
	public static ImmutableSet<String> getSemanticSubjectRelations()
	{
		return semanticSubjectRelations;
	}

	public static ImmutableSet<String> getSemanticObjectRelations()
	{
		return semanticObjectRelations;
	}

	public static ImmutableSet<String> getSemanticModifierRelations()
	{
		return semanticModifierRelations;
	}

	public static ImmutableSet<String> getSemanticInternalFacetRelations()
	{
		return semanticInternalFacetRelations;
	}

	public static ImmutableSet<String> getSemanticInternalTermRelations()
	{
		return semanticInternalTermRelations;
	}

	public static ImmutableSet<String> getSemanticTermConnectingRelations()
	{
		return semanticTermConnectingRelations;
	}

	public static ImmutableSet<String> getSemanticOtherInternalFacetRelations()
	{
		return semanticOtherInternalFacetRelations;
	}

	public static ImmutableSet<String> getSemanticNewClauseRelations()
	{
		return semanticNewClauseRelations;
	}
	
	public static ImmutableSet<String> getSemanticNewModifierClauseRelations()
	{
		return semanticNewModifierClauseRelations;
	}

	public static ImmutableSet<String> getSemanticNewObjectClauseRelations()
	{
		return semanticNewObjectClauseRelations;
	}

	public static ImmutableSet<String> getSemanticNewSubjectClauseRelations()
	{
		return semanticNewSubjectClauseRelations;
	}

	public static ImmutableSet<String> getSemanticNewOtherClauseRelations()
	{
		return semanticNewOtherClauseRelations;
	}
	
	public static ImmutableSet<String> getSemanticAuxiliaryVerbRelations()
	{
		return semanticAuxiliaryVerbRelations;
	}



	private static final ImmutableSet<String> semanticSubjectRelations;
	private static final ImmutableSet<String> semanticObjectRelations;
	private static final ImmutableSet<String> semanticModifierRelations;
	private static final ImmutableSet<String> semanticAuxiliaryVerbRelations;
	
	
	// new facet relation
	// This is the union of internal-term relations, term-connecting relations and other-internal-facet relations.
	private static final ImmutableSet<String> semanticInternalFacetRelations;
	
	private static final ImmutableSet<String> semanticInternalTermRelations;
	private static final ImmutableSet<String> semanticTermConnectingRelations;
	private static final ImmutableSet<String> semanticOtherInternalFacetRelations;
	
	
	// new clause relations
	// all new clause relations (union of all)
	private static final ImmutableSet<String> semanticNewClauseRelations;
	
	private static final ImmutableSet<String> semanticNewModifierClauseRelations;
	private static final ImmutableSet<String> semanticNewObjectClauseRelations;
	private static final ImmutableSet<String> semanticNewSubjectClauseRelations;
	private static final ImmutableSet<String> semanticNewOtherClauseRelations;
	
	
	static
	{
		String[] semanticSubjectRelationsArray = new String[]{
				"agent",
				"nsubj",
				"xsubj"
		};
		
		String[] semanticObjectRelationsArray = new String[]{
				"dobj",
				"iobj",
				"nsubjpass",
				"pobj",
				"rel"
		};
		
		String[] semanticModifierRelationsArray = new String[]{
				"acomp",
				"advmod",
				"num",
				"prep",
				"tmod"
		};
		
		
		
		
		
		
		
		
		
		
		String[] semanticInternalTermRelationsArray = new String[]{
				"amod",
				"det",
				"mwe",
				"nn",
				"number",
				"possessive",
				"predet",
				"prt",
				"quantmod",
		};
		
		String[] semanticTermConnectingRelationsArray = new String[]{
				"abbrev",
				"appos",
				"conj",
				"poss",
		};
		
		String[] semanticOtherInternalFacetRelationsArray = new String[]{
				"attr",
				"cc",
				"complm",
				"dep",
				"expl",
				"mark",
				"neg",
				"npadvmod",
				"preconj",
				"punct",
				"ref"
		};
		
		Set<String> semanticInternalTermRelationsSet = Utils.arrayToCollection(semanticInternalTermRelationsArray, new HashSet<String>());
		Set<String> semanticTermConnectingRelationsSet = Utils.arrayToCollection(semanticTermConnectingRelationsArray, new HashSet<String>());
		Set<String> semanticOtherInternalFacetRelationsSet = Utils.arrayToCollection(semanticOtherInternalFacetRelationsArray, new HashSet<String>());

		semanticInternalTermRelations = new ImmutableSetWrapper<String>(semanticInternalTermRelationsSet);
		semanticTermConnectingRelations	= new ImmutableSetWrapper<String>(semanticTermConnectingRelationsSet);
		semanticOtherInternalFacetRelations	= new ImmutableSetWrapper<String>(semanticOtherInternalFacetRelationsSet);

		HashSet<String> semanticInternalFacetRelationsSet = new HashSet<String>();
		semanticInternalFacetRelationsSet.addAll(semanticInternalTermRelationsSet);
		semanticInternalFacetRelationsSet.addAll(semanticTermConnectingRelationsSet);
		semanticInternalFacetRelationsSet.addAll(semanticOtherInternalFacetRelationsSet);
		semanticInternalFacetRelations = new ImmutableSetWrapper<String>(semanticInternalFacetRelationsSet);
		

		
		
		
		
		
		
		
		
		
		String[] semanticAuxiliaryVerbRelationsArray = new String[]{
				"aux",
				"auxpass",
				"cop"
		};
		

		
		semanticSubjectRelations = new ImmutableSetWrapper<String>(
				Utils.arrayToCollection(semanticSubjectRelationsArray, new HashSet<String>())
				);
		
		semanticObjectRelations = new ImmutableSetWrapper<String>(
				Utils.arrayToCollection(semanticObjectRelationsArray, new HashSet<String>())
				);

		
		semanticModifierRelations = new ImmutableSetWrapper<String>(
				Utils.arrayToCollection(semanticModifierRelationsArray, new HashSet<String>())
				);

		
		
		semanticAuxiliaryVerbRelations = new ImmutableSetWrapper<String>(
				Utils.arrayToCollection(semanticAuxiliaryVerbRelationsArray, new HashSet<String>())
				);
		
		// New clause relations
		
		String[] semanticNewModifierClauseRelationsArray = new String[]{
				"advcl",
				"partmod",
				"prepc",
				"purpcl"
		};

		String[] semanticNewObjectClauseRelationsArray = new String[]{
				"ccomp",
				"csubjpass"
		};

		String[] semanticNewSubjectClauseRelationsArray = new String[]{
				"csubj"
		};

		String[] semanticNewOtherClauseRelationsArray = new String[]{
				"parataxis",
				"pcomp",
				"rcmod",
				"xcomp",
				"infmod"
		};
		
		Set<String> semanticNewModifierClauseRelationsSet = Utils. arrayToCollection(semanticNewModifierClauseRelationsArray, new HashSet<String>()); 
		Set<String> semanticNewObjectClauseRelationsSet = Utils. arrayToCollection(semanticNewObjectClauseRelationsArray, new HashSet<String>());
		Set<String> semanticNewSubjectClauseRelationsSet = Utils. arrayToCollection(semanticNewSubjectClauseRelationsArray, new HashSet<String>());
		Set<String> semanticNewOtherClauseRelationsSet = Utils. arrayToCollection(semanticNewOtherClauseRelationsArray, new HashSet<String>());

		semanticNewModifierClauseRelations = new ImmutableSetWrapper<String>(semanticNewModifierClauseRelationsSet);
		semanticNewObjectClauseRelations = new ImmutableSetWrapper<String>(semanticNewObjectClauseRelationsSet);
		semanticNewSubjectClauseRelations = new ImmutableSetWrapper<String>(semanticNewSubjectClauseRelationsSet);
		semanticNewOtherClauseRelations = new ImmutableSetWrapper<String>(semanticNewOtherClauseRelationsSet);


		Set<String> semanticNewClauseRelationsSet = new HashSet<String>();
		semanticNewClauseRelationsSet.addAll(semanticNewModifierClauseRelationsSet);
		semanticNewClauseRelationsSet.addAll(semanticNewObjectClauseRelationsSet);
		semanticNewClauseRelationsSet.addAll(semanticNewSubjectClauseRelationsSet);
		semanticNewClauseRelationsSet.addAll(semanticNewOtherClauseRelationsSet);
		
		semanticNewClauseRelations = new ImmutableSetWrapper<String>(semanticNewClauseRelationsSet);
	}
}
