/**
 * 
 */
package eu.excitementproject.eop.common.representation.parse.representation.basic;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;

/**
 * A {@link DependencyRelation} backed by the {@link StanfordDepedencyRelationType} enum representation of the standard Stanford Dependency 
 * Relations.
 * <p>
 * Immutable
 * 
 * @author Amnon Lotan
 * @since 10/06/2011
 * @see http://nlp.stanford.edu/software/dependencies_manual.pdf
 * 
 */
@LanguageDependent("English")
public class StanfordDependencyRelation extends DependencyRelation {
	
	private static final long serialVersionUID = 2616311861583235365L;
	public final StanfordDepedencyRelationType stanfordDependancyRelationTag;
	
	/**
	 * Ctor, Check that the dependency relation string is not null and valid  
	 * @param relation
	 * @param type
	 * @throws CompilationException 
	 */
	public StanfordDependencyRelation(String relation) throws StanfordDependencyException 
	{
		this(relation, null);
	}
	
	/**
	 * Ctor, Check that the dependency relation string is not null and valid  
	 * @param relation
	 * @param type
	 * @throws CompilationException 
	 */
	public StanfordDependencyRelation(String relation, DependencyRelationType type) throws StanfordDependencyException 
	{
		super(relation, type);
		if (relation == null)
			throw new StanfordDependencyException("Got NULL as a Stanford Dependency Relation");
		try { stanfordDependancyRelationTag = StanfordDepedencyRelationType.valueOf(relation); }
		catch (Exception e) { throw new StanfordDependencyException(relation + " is not a valid Stanford Dependency Relation"); }
	}
	
	/**
	 * An enum of the Stanford grammatical relationships. Printed here in  hierarchical form. 
	 * 
	 * @author Amnon Lotan
	 * @see http://nlp.stanford.edu/software/dependencies_manual.pdf
	 *
	 */
	@LanguageDependent("English")
	public enum StanfordDepedencyRelationType 
	{
		dep, // dependent
			aux, // auxiliary
				auxpass, // passive auxiliary
				cop, 	// copula
			arg,	// argument
				agent,	// agent
				comp,	// complement
					acomp,	// adjectival complement
					attr,	// attributive
					ccomp,	// clausal complement with internal subject
					xcomp,	// clausal complement with external subject
					pcomp,	// prepositional complemen
					complm,	// complementizer
					obj,	// object
						dobj,	// direct object
						iobj,	// indirect object
						pobj,	// object of preposition
					mark,	// marker (word introducing an advcl )
					rel,	// relative (word introducing a rcmod )
				subj,	// subject
					nsubj,	// nominal subject
						nsubjpass,	// passive nominal subject
					csubj,	// clausal subject
						csubjpass,	// passive clausal subject
			cc,	// coordination
			conj,	// conjunct
			expl,	// expletive (expletive \there")
			mod,	// modier
				abbrev,	// abbreviation modier
				amod,	// adjectival modier
				appos,	// appositional modier
				advcl,	// adverbial clause modier
				purpcl,	// purpose clause modier
				det,	// determiner
				predet,	// predeterminer
				preconj,	// preconjunct
				infmod,	// innitival modier
				mwe,	// multi-word expression modier
				partmod,	// participial modier
				advmod,	// adverbial modier
					neg,	// negation modier
				rcmod,	// relative clause modier
				quantmod,	// quantier modier
				tmod,	// temporal modier
				nn,	// noun compound modier
				npadvmod,	// noun phrase adverbial modier
				num,	// numeric modier
				number,	// element of compound number
				prep,	// prepositional modier
				poss,	// possession modier
				possessive,	// possessive modier ('s)
				prt,	// phrasal verb particle
			parataxis,	// parataxis
			punct,	// punctuation
			ref,	// referent
			sdep,	// semantic dependent
				xsubj,	// controlling subject

	}

	public class StanfordDependencyException extends Exception {

		private static final long serialVersionUID = 6875911777605431295L;
		public StanfordDependencyException(String arg0) {
			super(arg0);
		}
		public StanfordDependencyException(String arg0, Throwable arg1) {
			super(arg0, arg1);
		}
	}
}
