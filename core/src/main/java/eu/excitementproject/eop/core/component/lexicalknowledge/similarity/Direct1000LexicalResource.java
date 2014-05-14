/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;
import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * <b>Resource description</b>: Directional similarity rules calculated using the balancedAP (bap) measure over Reuters RCV1 corpus with dependency-based features. Downloabable from our homepage.<br>
   Direct1000LexicalResource - up to 1000 similarity rules per right-hand-side term (nouns and verbs).<br>
   <b>Relevant POS</b>: nouns & verbs<br>
   <b>Ref to relevant Paper</b>:
 
   	<li> Lili Kotlerman, Ido Dagan, Idan Szpektor and Maayan Zhitomirsky-Geffet. Directional Distributional Similarity for Lexical Inference. Special Issue of Natural Language Engineering on Distributional Lexical Semantics (JNLE-DLS), 2010.</li>
	<li> Lili Kotlerman, Ido Dagan, Idan Szpektor and Maayan Zhitomirsky-Geffet. Directional Distributional Similarity for Lexical Expansion. In Proceedings of ACL (short papers), 2009.
	</li>
	<br>
    <p>
    <b>DB Scheme</b>: bap (qa-srv:3308)<br>
   <b>DB tables</b>:  direct_nouns_1000, direct_verbs_1000<br>
 * The tables contain {@code <lemma, lemma, similarity>} 
 * triplets. The first table contains NOUNs and the other VERBs. So all queries to other poses will retrieve empty results.
 * Each rule-list result of {@link #getRulesForLeft(String, PartOfSpeech)} and 
 * {@link #getRulesForRight(String, PartOfSpeech)}
 * is sorted in decreasing order of similarity (to the queried lemma).
 *  <p>
 * Also note that all digits in queried lemma will be replaced with '@', and the lemmas in all retrieved rules will have '@'s where you'd 
 * expect digits.
 * <p>
 *  The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved.
 * <p>
 * Documentation about the tables at  {@link http://u.cs.biu.ac.il/~nlp/downloads/DIRECT.html}.
 * <P>
 * See also: http://irsrv2/wiki/index.php/Lexical_Resources
 * 
 * @author Amnon Lotan
 * @since 16/05/2011
 * 
 */
public class Direct1000LexicalResource extends AbstractDirectLexicalResource {

	private static final String RESOURCE_NAME = "Direct1000";

	private static final String NOUN_TABLE = "nouns_1000"; 
	private static final String VERB_TABLE = "verbs_1000";
	
	/**
	 * Ctor using {@link ConfigurationParams}
	 * @param params
	 * @throws LexicalResourceException
	 * @throws ConfigurationException 
	 */
	public Direct1000LexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		super(params);
	}
	
	/**
	 * Ctor
	 * <p>
 *  The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved.
	 * @param connStr
	 * @param user
	 * @param password
	 * @param limitOnRetrievedRules 
	 * @throws LexicalResourceException
	 */
	public Direct1000LexicalResource(String connStr, String user,	String password, int limitOnRetrievedRules) throws LexicalResourceException {
		super(connStr, user, password, limitOnRetrievedRules);
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.similarity.AbstractDirectLexicalResource#getNounTableName()
	 */
	@Override
	protected String getNounTableName() {
		return NOUN_TABLE;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.similarity.AbstractDirectLexicalResource#getVerbTableName()
	 */
	@Override
	protected String getVerbTableName() {
		return VERB_TABLE;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.similarity.AbstractSimilarityLexicalResource#getResourceName()
	 */
	@Override
	protected String getResourceName() {
		return RESOURCE_NAME;
	}

	public static void main(String args[]) throws LexicalResourceException, UnsupportedPosTagStringException {
		Direct1000LexicalResource resource = new Direct1000LexicalResource("jdbc:mysql://localhost:3306/bap","root","root",10);
		List<? extends LexicalRule<? extends RuleInfo>> similarities = resource.getRulesForLeft("find",new ByCanonicalPartOfSpeech(CanonicalPosTag.V.name()));
		for (LexicalRule<? extends RuleInfo> similarity : similarities)
			System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());

	}
}

