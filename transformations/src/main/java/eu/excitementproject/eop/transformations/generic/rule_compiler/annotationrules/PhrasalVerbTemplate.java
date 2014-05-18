/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;

/**
 * Holds the info in one line of the PhrasalImplicativeTemplates.properties file.
 * <p>
 * IMMUTABLE
 * <p>
 *  Right now, it looks like this:<br>
 *  1 = HAVE, ABILITY/OPPORTUNITY, 	I3	
2 = HAVE, COURAGE, I1	
		
3 = LACK,	ABILITY/OPPORTUNITY/COURAGE, I2_FinP_InfP	
		
4 = MAKE,	EFFORT, I3	
		
5 = MEET, OBLIGATION,	I1	
		
6 = SHOW,	HESITATION,	I6_FinP_InfP,
		
7 = TAKE,	ASSET/EFFORT/OPPORTUNITY, I1
		
8 = USE,	ASSET/OPPORTUNITY,	I1
		
9 = WASTE,	ASSET,	I1,
10 = WASTE,	OPPORTUNITY,	I4_FinP_InfP,
 *  
 * @author Amnon Lotan
 *
 * @since May 16, 2012
 * @see 	http://www.stanford.edu/group/csli_lnr/Lexical_Resources/phrasal-implicatives/simple-and-phrasal-implicatives.pdf
 * 
 */
@LanguageDependent("English")
public class PhrasalVerbTemplate {
	
	private final ImmutableSet<String> verbs;
	private final ImmutableSet<String>	nouns;
	private final PredicateSignature predicateType;
	/**
	 * Ctor
	 * @param verbs
	 * @param nouns
	 * @param predicateType
	 * @throws AnnotationCompilationException 
	 */
	public PhrasalVerbTemplate(Set<String> verbs, Set<String> nouns, PredicateSignature predicateType) throws AnnotationCompilationException {
		if (verbs == null || verbs.isEmpty())
			throw new AnnotationCompilationException("got empty/null verbs");
		if (nouns == null || nouns.isEmpty())
			throw new AnnotationCompilationException("got empty/null nouns");
		if (predicateType == null )
			throw new AnnotationCompilationException("got null predicate type");
		this.verbs = new ImmutableSetWrapper<String>(verbs);
		this.nouns = new ImmutableSetWrapper<String>(nouns);
		this.predicateType = predicateType;
	}
	
	/**
	 * @return the verbs
	 */
	public ImmutableSet<String> getVerbs() {
		return verbs;
	}
	/**
	 * @return the predicateType
	 */
	public PredicateSignature getPredicateType() {
		return predicateType;
	}
	/**
	 * @return the nouns
	 */
	public ImmutableSet<String> getNouns() {
		return nouns;
	}
}
