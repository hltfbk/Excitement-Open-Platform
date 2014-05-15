/**
 * 
 */
package eu.excitementproject.eop.transformations.representation.annotations;
import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDepedencyRelationType;
import eu.excitementproject.eop.transformations.generic.truthteller.application.ct.ComplementRelations;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;

/**
 * <b>IMPORTANT!</b> each enum const must be accounted for in {@link #getPositiveSide(StanfordDepedencyRelationType)}, 
 * {@link #getNegativeSide(StanfordDepedencyRelationType)} and {@link #isFactiveRelation(StanfordDepedencyRelationType)} !
 * <p>
 * 
 * This is an annotation feature, member of {@link AdditionalNodeInformation}.  
 * It represents a lexical-semantic classification of predicates that distinguishes between implicatives, factives and regular predicates. It serves mainly in 
 * computing the {@link ClauseTruth} annotation. It is assumed that each {@link PredicateSignature} is looked up in a special external predicate table, built manually. 
 * <p>
 * In the table below, the two components of each signature define the {@link ClauseTruth} of the predicate's argument(s). The left one applies in non-negated 
 * environments, and the right one is for sentences where the predicate is negated. '+' implies positive clausal-truth, '-' implies negative, and '?' implies unknown.
 *  For instance, refuse has a '-' on the left of its -/? signature, so when we're told <i>He refused to fight</i>, we know that <i>He didn't fight</i>. 
 *  Contrarily, the '?' on the right side means that if  <i>He might have refused to fight</i>, we don't know if there was actually a fight or not.
 <p>
	<b>Category					Signature	Example	<br></b>                        
	Implicatives Cat1 {I1}		+ / -		he managed to escape	--> 	he escaped	<br>
	Cat2 {I2}					+ / ?		he was forced to sell	-->	he sold	<br>
	Cat3 {I3}					? / -		he wasn't permitted to live	-->	He didn't  live	<br>
	Cat4 {I4}					- / +		he forgot to pay	-->	he didn't pay	<br>
	Cat5 {I5}					- / ?		he refused to fight	--> He didn't fight	<br>
	Cat6 {I6}					? / +		he didn't hesitate to ask	-->	he asked	<br>
	Factives {FA}				+ / +		he admitted that he knew	-->	he knew	<br>
	Negative Factives {NF}		- / -		N/A	<br>
	Regular {RE}				? / ?		he asked to fly	-->	no entailments	<br>
<p>

	The <code>Negative Factives</code> row has no example because, although (MacCartney & Manning 2009) used it, we believe there are no such predicates in English. 
	In fact, the row is included merely for mathematical completeness. The <code>UNSPECIFIED</code> row is an artificial category to be used as a default for 
	all predicates not found in any of the lists. A good implementation should keep the use of this category to a minimum, by keeping those lists comprehensive.
 * 
 * @author Amnon Lotan
 * @since 31/05/2011
 * 
 */
@LanguageDependent("English")
public enum PredicateSignature {

	//
	//	All the display strings must be unique to avoid confusion!
	//
	U_U("?/?", false, false),		// DEFAULT value in the predicate list! // regulars imply UNCERTAIN CT to all args. 
	P_P_FinP("+/+Fin", true, false),		// Factives that take finite args
	P_P("+/+", true, true),	// Factives that take both finite and non-finite args
	P_P_FinP_P_N_InfP("+/+Fin&+/-NoF", true, true),
	N_N("-/-", true, true),		// we don't believe in negative factives, and this list should be blank in the predicate lists
	P_N("+/-", true, true),
	P_N_InfP("+/-NoF", false, true),
	P_U_InfP("+/?NoF", false, true),
	P_U("+/?", true, true),
	P_U_FinP("+/?Fin", true, false),
	P_P_FinP_P_U_InfP("+/+Fin&+/?NoF", true, true),
	U_N("?/-", true, true),
	N_P("-/+", true, true),
	P_P_FinP_N_P_InfP("+/+Fin&-/+NoF", true, true),
	N_U("-/?", true, true),
	N_U_InfP("-/?NoF", false, true),
	P_P_FinP_N_U_InfP("+/+Fin&-/?NoF", true, true),
	U_P("?/+", true, true),
	
	/**
	 * This value <i>may</i> be used as a default for all predicates not found in the lexicon, and <b>must not</b> list any predicates in the lexicon. 
	 * The "default" signature is determined in the annotation rules, (first two rules, maybe referenced in others). 
	 */
	NOT_IN_LEXICON("0/0", false, false),
	;
	
	
	/**
	 *  true iff this {@link PredicateSignature} takes finite arguments
	 */
	public final boolean takesFinPArg;	
	/**
	 * true iff this {@link PredicateSignature} takes non-finite arguments
	 */
	public final boolean takesInfPArg;
	/**
	 * display for representation
	 */
	public final String display;	
	
	private PredicateSignature(String display, boolean takesFinPArg, boolean takesInfPArg)
	{
		this.display = display ;
		this.takesFinPArg = takesFinPArg;
		this.takesInfPArg = takesInfPArg;
	}
	
	
	////////////////////////////////////// 4 methods that help generalize the classification above	////////////////////////////////////////////////
	
	/**
	 * return true iff this {@link StanfordDepedencyRelationType} is a valid relation between a factive predicate and its arg.
	 * @param relation
	 * @return
	 */
	public boolean isFactiveRelation(StanfordDepedencyRelationType relation)
	{
		if (!ComplementRelations.COMPLEMENT_RELATIONS.contains(relation))
			return false;		// the relation isn't argumental. factivity is impossible.
		switch (this)
		{
		case P_P:
			return true;
		case P_P_FinP:
		case P_P_FinP_P_N_InfP:
		case P_P_FinP_N_P_InfP:
		case P_P_FinP_P_U_InfP:
		case P_P_FinP_N_U_InfP:
			return !ComplementRelations.NON_FINITE_RELATIONS.contains(relation);
		default:			// non factive signature
			return false;
		}
	}
	
	/**
	 * return true iff this {@link StanfordDepedencyRelationType} is a valid relation between a NEGATIVE-factive predicate and its arg.
	 * @param relation
	 * @return
	 */
	public boolean isNegativeFactiveRelation(StanfordDepedencyRelationType relation)
	{
		if (!ComplementRelations.COMPLEMENT_RELATIONS.contains(relation))
			return false;		// the relation isn't argumental. factivity is impossible.
		else
			return this.equals(N_N);		
	}
	
	/**
	 * return true iff this is a {@link PredicateSignature#U_U}, or the relation isn't argumental
	 * @param relation
	 * @return
	 */
	public boolean isUncertainRelation(StanfordDepedencyRelationType relation)
	{
		if (!ComplementRelations.COMPLEMENT_RELATIONS.contains(relation))
			return true;		// the relation isn't argumental. factivity/implicativity is impossible.
		else
			return this.equals(U_U);
	}
	
	/**
	 * return true iff this is a complement of a {@link PredicateSignature#NOT_IN_LEXICON} predicate
	 * @param relation
	 * @return
	 */
	public boolean isUnspecifiedRelation(StanfordDepedencyRelationType relation) {
		return  (ComplementRelations.COMPLEMENT_RELATIONS.contains(relation) 
				&& this.equals(NOT_IN_LEXICON));
	}	
	
	/**
	 * return the {@link ClauseTruth} value on the 'left side of the slash' of this {@link PredicateSignature}'s signature, according to the table in the class comment
	 * <p>
	 * TODO <b>NOTICE:</b> If takesInfPArg==false, (meaning this predicate is not supposed to take non finite args) and the relation is InfP, we return POSITIVE. 
	 * That's because we assume any InfP modifiers it has in the text are 
	 * probably "extraposed complement clause construction "s (i.e. you can prefix it with "in order to"). Therefore, they are presupposed.
	 * See Kartunnen's paper http://www.stanford.edu/~laurik/publications/simple-and-phrasal-implicatives.pdf.<br>
	 * TODO can we externalize this feature to an annotation rule?
	 * 
	 * @throws AnnotationValueException 
	 * @return
	 */
	public ClauseTruth getPositiveSide(StanfordDepedencyRelationType relation) throws AnnotationValueException {
		switch (this)
		{
		case P_P:
		case P_P_FinP_P_N_InfP:
		case P_N:
		case P_U:
		case P_P_FinP_P_U_InfP:
			return ClauseTruth.P;
	
		case N_N:
		case N_P:
		case N_U:
			return ClauseTruth.N;
		
		case U_N:
		case U_P:
		case U_U:
			return ClauseTruth.U;

		case P_U_InfP:			
		case P_N_InfP:
			return ComplementRelations.NON_FINITE_RELATIONS.contains(relation) ? ClauseTruth.P : ClauseTruth.U;
			
		case P_P_FinP:
		case P_U_FinP:
			return ComplementRelations.NON_FINITE_RELATIONS.contains(relation) ? ClauseTruth.U : ClauseTruth.P;
			
		case P_P_FinP_N_P_InfP:
		case P_P_FinP_N_U_InfP:
			return ComplementRelations.NON_FINITE_RELATIONS.contains(relation) ? ClauseTruth.N : ClauseTruth.P;

		case N_U_InfP:
			return ComplementRelations.NON_FINITE_RELATIONS.contains(relation) ? ClauseTruth.N : ClauseTruth.U;
			
		case NOT_IN_LEXICON:
			return ClauseTruth.O;
			
		default:
			throw new AnnotationValueException("internal bug: this pred type is not accounted for in this method: " + this); 
		}
	}
	
	/**
	 * return the {@link ClauseTruth} value on the 'right side of the slash' of this {@link PredicateSignature}'s signature, according to the table in the class comment
	 * <p>
	 * TODO <b>NOTICE:</b> If takesInfPArg==false, (meaning this predicate is not supposed to take non finite args) and the relation is InfP, we return POSITIVE. 
	 * That's because we assume any InfP modifiers it has in the text are 
	 * probably "extraposed complement clause construction "s (i.e. you can prefix it with "in order to"). Therefore, they are presupposed.
	 * See Kartunnen's paper http://www.stanford.edu/~laurik/publications/simple-and-phrasal-implicatives.pdf. 
	 * @param relation 
	 * @return the {@link ClauseTruth} value on the 'right side of the slash' of this {@link PredicateSignature}'s signature, according to the table in the class comment
	 * @throws AnnotationValueException 
	 */
	public ClauseTruth getNegativeSide(StanfordDepedencyRelationType relation) throws AnnotationValueException {
		switch (this)
		{
		case P_P:
		case P_P_FinP_N_P_InfP:
		case N_P:
		case U_P:
			return ClauseTruth.P;
			
		case N_N:
		case U_N:
		case P_N:
			return ClauseTruth.N;
			
		case P_U:
		case P_U_InfP:			
		case N_U:
		case U_U:
		case P_U_FinP:
		case N_U_InfP:
			return ClauseTruth.U;			

		case P_N_InfP:
			return ComplementRelations.NON_FINITE_RELATIONS.contains(relation) ? ClauseTruth.N : ClauseTruth.U;
			
		case P_P_FinP_P_N_InfP:
			return ComplementRelations.NON_FINITE_RELATIONS.contains(relation) ? ClauseTruth.N : ClauseTruth.P;
			
		case P_P_FinP:
		case P_P_FinP_P_U_InfP:
		case P_P_FinP_N_U_InfP:
			return ComplementRelations.NON_FINITE_RELATIONS.contains(relation) ? ClauseTruth.U : ClauseTruth.P;
			
		case NOT_IN_LEXICON:
			return ClauseTruth.O;
			
		default:
			throw new AnnotationValueException("internal bug: this pred type is not accounted for in this method: " + this); 
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return display;
	}
}
