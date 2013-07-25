/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application.ct;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDepedencyRelationType;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.generic.truthteller.TruthTellerConstants;


/**
 * This class contains several sets of {@link StanfordDepedencyRelationType} containing various predicate-complement relations
 * <P>
 * TODO future work: what about subjects? subj, csubj, subjpass, csubjpass? add them?
 * 
 * @author Amnon Lotan
 *
 * @since Jun 18, 2012
 * @see  http://nlp.stanford.edu/software/dependencies_manual.pdf
 */
@LanguageDependent("English")
@ParserSpecific("easyfirst")
public class ComplementRelations {

	/**
	 * All possible {@link StanfordDepedencyRelationType}s between a predicate and non-finite args. This is the complement of all possible relations to finite arsg. 
	 */
	public static final ImmutableSet<StanfordDepedencyRelationType> NON_FINITE_RELATIONS = new ImmutableSetWrapper<StanfordDepedencyRelationType>(
			Utils.arrayToCollection(new StanfordDepedencyRelationType[] {	 
					StanfordDepedencyRelationType.xcomp,
					StanfordDepedencyRelationType.infmod,
					// FIXME use of 'dep' is sketchy and usually means some parser failure/error. It's good for many sentences, but may cause false positives
					StanfordDepedencyRelationType.dep
			},	new LinkedHashSet<StanfordDepedencyRelationType>(25)));

	
	/**
	 * Some {@link StanfordDepedencyRelationType}s between a predicate and a nominal modifier that is not usually considered an argument/complement, like 
	 * {@link StanfordDepedencyRelationType#nn}. Should be included in the bigger complement relations list if the flag 
	 * {@link TruthTellerConstants#INCLUDE_NOMINAL_RELATIONS_IN_THE_COMPLEMENT_RELATIONS} is lit.
	 */
	public static final ImmutableSet<StanfordDepedencyRelationType> NOMINAL_RELATION = new ImmutableSetWrapper<StanfordDepedencyRelationType>(
			Utils.arrayToCollection(new StanfordDepedencyRelationType[] {
					StanfordDepedencyRelationType.nn,
					StanfordDepedencyRelationType.mod,
					StanfordDepedencyRelationType.abbrev,
					StanfordDepedencyRelationType.amod,
					StanfordDepedencyRelationType.advcl ,
					StanfordDepedencyRelationType.tmod,
					StanfordDepedencyRelationType.npadvmod,
			},	new LinkedHashSet<StanfordDepedencyRelationType>(25)));

	
	/**
	 * All possible {@link StanfordDepedencyRelationType}s between a predicate and its args. prep+pobj and prep+pcomp are a special case of a 2-chain of 
	 * relations that must be treated as a unit.<br>
	 * This contains all the {@link #NON_FINITE_RELATIONS} 
	 * TODO what about subjects?
	 */
	public static final ImmutableSet<StanfordDepedencyRelationType> COMPLEMENT_RELATIONS;
	static
	{
		Set<StanfordDepedencyRelationType> tmp_arg_relations = Utils.arrayToCollection(new StanfordDepedencyRelationType[] {	
				StanfordDepedencyRelationType.comp,
				StanfordDepedencyRelationType.pcomp,
				StanfordDepedencyRelationType.ccomp, 
				StanfordDepedencyRelationType.complm,
				StanfordDepedencyRelationType.obj,
				StanfordDepedencyRelationType.dobj, 
				StanfordDepedencyRelationType.pobj,
				StanfordDepedencyRelationType.iobj,
				
				// I added these only because they are also kinds of COMP. didn't test.
				StanfordDepedencyRelationType.acomp,
				StanfordDepedencyRelationType.attr,
				StanfordDepedencyRelationType.rel,
				StanfordDepedencyRelationType.mark,
				
				
	
			},		new LinkedHashSet<StanfordDepedencyRelationType>(25));
		tmp_arg_relations.addAll(NON_FINITE_RELATIONS.getMutableCollectionCopy());

		if (TruthTellerConstants.INCLUDE_NOMINAL_RELATIONS_IN_THE_COMPLEMENT_RELATIONS)
			tmp_arg_relations.addAll(NOMINAL_RELATION.getMutableCollectionCopy());
		
		
		COMPLEMENT_RELATIONS = new ImmutableSetWrapper<StanfordDepedencyRelationType>(tmp_arg_relations);
	}
	
	/**
	 * These are all the possible relations between a <code>prep</code> node and its argument. If a <code>prep</code> node is found without a child connected 
	 * by one of these relations, an exception is thrown.
	 */
	public static final ImmutableSet<StanfordDepedencyRelationType> PREP_RELATIONS = new ImmutableSetWrapper<StanfordDepedencyRelationType>(  
			Utils.arrayToCollection(new StanfordDepedencyRelationType[]
			{	StanfordDepedencyRelationType.pobj, 
				StanfordDepedencyRelationType.pcomp, 
				StanfordDepedencyRelationType.dep, 
				StanfordDepedencyRelationType.rcmod,
				StanfordDepedencyRelationType.npadvmod},
			new LinkedHashSet<StanfordDepedencyRelationType>()));


	/**
	 * an apposition/conjunct/copular  simply gets its parent's CT. 
	   TODO does it matter if the conj was CT? ?? 
	 */
	public static final ImmutableSet<StanfordDepedencyRelationType> RELATIONS_THAT_COPY_CT_FROM_PARENT = new ImmutableSetWrapper<StanfordDepedencyRelationType> (  
			Utils.arrayToCollection(new StanfordDepedencyRelationType[]
			{	StanfordDepedencyRelationType.appos, 
				StanfordDepedencyRelationType.conj,
				StanfordDepedencyRelationType.aux,
				StanfordDepedencyRelationType.auxpass,
				StanfordDepedencyRelationType.cop},
			new LinkedHashSet<StanfordDepedencyRelationType>()));
}
