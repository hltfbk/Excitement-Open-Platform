/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * <b>Resource name</b>: Dekang Lin's dependency-based thesaurus.<br>
   <b>Resource description</b>: Resource was downloaded from Dekang Lin's website, [1], but is not available there anymore. 
   "Dependency-based thesaurus. This contains a list automatically constructed thesauri. For each word, the thesaurus 
   lists up to 200 most similar words and their similarities. The similar words are clustered (also automatically)."<br>
   <b>POS</b>: nouns (n), verbs (v), adjectives & adverbs (a)<br>
   <b>Ref to relevant Paper</b>: �Automatic Retrieval and Clustering of Similar Words�, Dekang Lin, COLING-ACL, 1998, pp. 768-774.<br>
   <b>DB Scheme</b>: lin (qa-srv:3308)<br>
   <b>DB tables</b>: <code>lin_dep_n, lin_dep_v, lin_dep_a</code>. Each table contains {@code <lemma, lemma, similarity>} triplets of 
   adjectives, nouns and verbs, correspondingly. Queries for other
 * parts of speech will return empty results (not null). 
 * <p>
 * Each rule-list result of {@link #getRulesForLeft(String, PartOfSpeech)} and {@link #getRulesForRight(String, PartOfSpeech)}
 * is sorted in decreasing order of similarity (to the queried lemma+pos).
 * <p>
 * Also note that all digits in queried lemma will be replaced with '@', and the lemmas in all retrieved rules will have '@'s where you'd 
 * expect digits.
 * <p>
 *  The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved.
 * <P>
 * See also: http://irsrv2/wiki/index.php/Lexical_Resources
 * 
 * @author Amnon Lotan
 * @since 16/05/2011
 * 
 */
public class LinDependencyOriginalLexicalResource extends AbstractSimilarityLexicalResource
{
	public static final String PARAM_CONNECTION_STRING = "connectionString";
	public static final String PARAM_USER = "user";
	public static final String PARAM_PASSWORD = "password";
	
	private static final String RESOURCE_NAME = "Original Lin Dependancy";

	// Strings for the prepared statements
	private static final String NOUN_TABLE = 	"lin_dep_n";
	private static final String VERB_TABLE = 	"lin_dep_v";
	private static final String ADJECTIVE_TABLE = "lin_dep_a";

	private final Set<PreparedStatementAndPos> SET_OF_ALL_SCORES_STMTS = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_SCORES_STMT_NOUN  = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_SCORES_STMT_VERB  = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_SCORES_STMT_ADJECTIVE  = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> EMPTY_SET = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_ALL_RIGHT_STMTS = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_RIGHT_STMT_NOUN = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_RIGHT_STMT_VERB = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_RIGHT_STMT_ADJECTIVE = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_ALL_LEFT_STMTS = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_LEFT_STMT_NOUN = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_LEFT_STMT_VERB = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> SET_OF_LEFT_STMT_ADJECTIVE = new LinkedHashSet<PreparedStatementAndPos>();
	
	@Override
	protected String L_COL() {	return "word1";	}
	@Override
	protected String R_COL() {		return "word2";	}
	@Override
	protected String SIM_COL() {	return "sim";	}

	///////////////////////////////////////////////// PUBLIC /////////////////////////////////////////////////////////////////////
	

	/**
	 * Ctor
	 * <p>
 *  The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved.
	 * @param limitOnRetrievedRules 
	 * @param defaultConnStr
	 * @param string
	 * @param string2
	 * @throws LexicalResourceException 
	 */
	public LinDependencyOriginalLexicalResource(String connStr, String user, String password, int limitOnRetrievedRules) throws LexicalResourceException
	{
		super(limitOnRetrievedRules);
		PreparedStatement getRulesForLeftStmt_noun;
		PreparedStatement getRulesForLeftStmt_verb;
		PreparedStatement getRulesForLeftStmt_adjective;
		PreparedStatement getRulesForRightStmt_noun;
		PreparedStatement getRulesForRightStmt_verb;
		PreparedStatement getRulesForRightStmt_adjective;
		PreparedStatement getScoresStmt_noun;
		PreparedStatement getScoresStmt_verb;
		PreparedStatement getScoresStmt_adjective;

		try
		{
			Connection con = DriverManager.getConnection(connStr, user, password);
			getRulesForLeftStmt_noun 		= con.prepareStatement(getRulesForLeftQueryStr(NOUN_TABLE));
			getRulesForLeftStmt_verb 		= con.prepareStatement(getRulesForLeftQueryStr(VERB_TABLE));
			getRulesForLeftStmt_adjective 	= con.prepareStatement(getRulesForLeftQueryStr(ADJECTIVE_TABLE));
			getRulesForRightStmt_noun 		= con.prepareStatement(getRulesForRightQueryStr(NOUN_TABLE));
			getRulesForRightStmt_verb 		= con.prepareStatement(getRulesForRightQueryStr(VERB_TABLE));
			getRulesForRightStmt_adjective 	= con.prepareStatement(getRulesForRightQueryStr(ADJECTIVE_TABLE));
			getScoresStmt_noun 				= con.prepareStatement(getRulesForBothSidesQueryStr(NOUN_TABLE));
			getScoresStmt_verb 				= con.prepareStatement(getRulesForBothSidesQueryStr(VERB_TABLE));
			getScoresStmt_adjective 		= con.prepareStatement(getRulesForBothSidesQueryStr(ADJECTIVE_TABLE));
		} catch (Exception e) 	{ 
			throw new LexicalResourceException("Couldn't open and use a connection with this connection string: " + connStr +
					" and credentials: " + user + "/" + password, e);	
		}
		
		final PreparedStatementAndPos GET_RULES_FOR_RIGHT_STMT_ADJECTIVE_WITH_POS = new PreparedStatementAndPos(getRulesForRightStmt_adjective, ADJECTIVE );
		final PreparedStatementAndPos GET_RULES_FOR_RIGHT_STMT_NOUN_WITH_POS = new PreparedStatementAndPos(getRulesForRightStmt_noun, NOUN );
		final PreparedStatementAndPos GET_RULES_FOR_RIGHT_STMT_VERB_WITH_POS = new PreparedStatementAndPos(getRulesForRightStmt_verb, VERB );
		
		final PreparedStatementAndPos GET_RULES_FOR_LEFT_STMT_ADJECTIVE_WITH_POS = new PreparedStatementAndPos(getRulesForLeftStmt_adjective, ADJECTIVE );
		final PreparedStatementAndPos GET_RULES_FOR_LEFT_STMT_NOUN_WITH_POS = new PreparedStatementAndPos(getRulesForLeftStmt_noun, NOUN );
		final PreparedStatementAndPos GET_RULES_FOR_LEFT_STMT_VERB_WITH_POS = new PreparedStatementAndPos(getRulesForLeftStmt_verb, VERB );

		final PreparedStatementAndPos GET_SCORES_STMT_ADJECTIVE_WITH_POS = new PreparedStatementAndPos(getScoresStmt_adjective, ADJECTIVE );
		final PreparedStatementAndPos GET_SCORES_STMT_NOUN_WITH_POS = new PreparedStatementAndPos(getScoresStmt_noun, NOUN );
		final PreparedStatementAndPos GET_SCORES_STMT_VERB_WITH_POS = new PreparedStatementAndPos(getScoresStmt_verb, VERB );
		
		SET_OF_ALL_RIGHT_STMTS.add(GET_RULES_FOR_RIGHT_STMT_ADJECTIVE_WITH_POS);
		SET_OF_ALL_RIGHT_STMTS.add(GET_RULES_FOR_RIGHT_STMT_NOUN_WITH_POS);
		SET_OF_ALL_RIGHT_STMTS.add(GET_RULES_FOR_RIGHT_STMT_VERB_WITH_POS);
		SET_OF_RIGHT_STMT_NOUN.add(GET_RULES_FOR_RIGHT_STMT_NOUN_WITH_POS);
		SET_OF_RIGHT_STMT_VERB.add(GET_RULES_FOR_RIGHT_STMT_VERB_WITH_POS);
		SET_OF_RIGHT_STMT_ADJECTIVE.add(GET_RULES_FOR_RIGHT_STMT_ADJECTIVE_WITH_POS);
		
		SET_OF_ALL_LEFT_STMTS.add(GET_RULES_FOR_LEFT_STMT_ADJECTIVE_WITH_POS);
		SET_OF_ALL_LEFT_STMTS.add(GET_RULES_FOR_LEFT_STMT_NOUN_WITH_POS);
		SET_OF_ALL_LEFT_STMTS.add(GET_RULES_FOR_LEFT_STMT_VERB_WITH_POS);
		SET_OF_LEFT_STMT_NOUN.add(GET_RULES_FOR_LEFT_STMT_NOUN_WITH_POS);
		SET_OF_LEFT_STMT_VERB.add(GET_RULES_FOR_LEFT_STMT_VERB_WITH_POS);
		SET_OF_LEFT_STMT_ADJECTIVE.add(GET_RULES_FOR_LEFT_STMT_ADJECTIVE_WITH_POS);
		
		SET_OF_ALL_SCORES_STMTS.add(GET_SCORES_STMT_NOUN_WITH_POS);
		SET_OF_ALL_SCORES_STMTS.add(GET_SCORES_STMT_VERB_WITH_POS);
		SET_OF_ALL_SCORES_STMTS.add(GET_SCORES_STMT_ADJECTIVE_WITH_POS);
		SET_OF_SCORES_STMT_NOUN.add(GET_SCORES_STMT_NOUN_WITH_POS);
		SET_OF_SCORES_STMT_VERB.add(GET_SCORES_STMT_VERB_WITH_POS);
		SET_OF_SCORES_STMT_ADJECTIVE.add(GET_SCORES_STMT_ADJECTIVE_WITH_POS);
	}
	
	public LinDependencyOriginalLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		this(
				params.getString(PARAM_CONNECTION_STRING),
				params.getString(PARAM_USER),
				params.getString(PARAM_PASSWORD),
				params.getInt(PARAM_RULES_LIMIT)
				);
	}

	///////////////////////////////////////////////////////////// PRIVATE METHODS ///////////////////////////////////////////////////////

	/**
	 * @param pos
	 * @param isRHS 
	 * @return
	 */
	protected Set<PreparedStatementAndPos> posToRulesStmts(PartOfSpeech pos, boolean isRHS) 
	{
		if (isRHS)
		{
			if (pos == null)
				return SET_OF_ALL_RIGHT_STMTS;
			switch (simplerPos(pos.getCanonicalPosTag()))
			{
			case NOUN:
				return SET_OF_RIGHT_STMT_NOUN;
			case VERB:
				return SET_OF_RIGHT_STMT_VERB;
			case ADJECTIVE:
				return SET_OF_RIGHT_STMT_ADJECTIVE;
			default:
				break;
			}
		}
		else
		{
			if (pos == null)
				return SET_OF_ALL_LEFT_STMTS;
			switch (simplerPos(pos.getCanonicalPosTag()))
			{
			case NOUN:
				return SET_OF_LEFT_STMT_NOUN;
			case VERB:
				return SET_OF_LEFT_STMT_VERB;
			case ADJECTIVE:
				return SET_OF_LEFT_STMT_ADJECTIVE;
			default:
				break;
			}
		}
		return EMPTY_SET; 
	}
	
	/**
	 * @param pos
	 * @return
	 */
	protected Set<PreparedStatementAndPos> posToScoreStmt(PartOfSpeech pos) 
	{
		if (pos == null)
			return SET_OF_ALL_SCORES_STMTS;
		switch (simplerPos(pos.getCanonicalPosTag()))
		{
			case NOUN:
				return SET_OF_SCORES_STMT_NOUN;
			case VERB:
				return  SET_OF_SCORES_STMT_VERB;
			case ADJECTIVE:
				return  SET_OF_SCORES_STMT_ADJECTIVE;
			default:
				return EMPTY_SET;
		}
	}
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractSimilarityLexResource#getResourceName()
	 */
	@Override
	protected String getResourceName() {
		return RESOURCE_NAME;
	}
}

