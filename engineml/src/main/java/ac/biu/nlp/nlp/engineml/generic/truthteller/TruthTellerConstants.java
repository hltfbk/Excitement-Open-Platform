/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.truthteller;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import ac.biu.nlp.nlp.engineml.generic.truthteller.application.ct.ClauseTruthAnnotationRuleApplier;
import ac.biu.nlp.nlp.engineml.generic.truthteller.application.ct.ComplementRelations;
import ac.biu.nlp.nlp.engineml.representation.annotations.ClauseTruth;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.StanfordDependencyRelation.StanfordDepedencyRelationType;

/**
 * @author Amnon Lotan
 *
 * @since Jul 11, 2012
 */
@LanguageDependent("English")
@ParserSpecific("easyfirst")
public class TruthTellerConstants {

	/**
	 * if a node is the nsubj of a "there is" construction (with a "there/EX/expl" node), replace the relation with dobj
	 */
	public static final boolean AMMEND_COMPLEMENT_RELATION_IN_EXPLATIVE_PHRASES = true;
	
	/**
	 * This is the CT assigned by {@link ClauseTruthAnnotationRuleApplier} to predicates where no information is available. 
	 * By implication, it is also the default PT value.
	 */
	public static final ClauseTruth DEFAULT_CT = ClauseTruth.U;
	
	/**
	 * This flag decides whether to include {@link ComplementRelations#NOMINAL_RELATION} (some {@link StanfordDepedencyRelationType}s between a predicate and a 
	 * nominal modifier that is not usually considered an argument/complement, like 
	 * {@link StanfordDepedencyRelationType#nn} ) in the bigger complement relations list. That list is used in the {@link ClauseTruthAnnotationRuleApplier} to identify
	 * and annotate complements of each visited predicate.
	 */
	public static final boolean INCLUDE_NOMINAL_RELATIONS_IN_THE_COMPLEMENT_RELATIONS = false;

}
