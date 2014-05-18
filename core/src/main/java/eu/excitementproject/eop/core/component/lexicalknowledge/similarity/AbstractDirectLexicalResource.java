/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * this abstract class implements most of {@link Direct1000LexicalResource} and {@link Direct200LexicalResource}, and  contains everything but the table names.
 * <p> 
 * This LexResource wraps the {@code direct_nouns_X} and {@code direct_verbs_X} tables in the {@code bap} database, The tables contain {@code <lemma, lemma, similarity>} 
 * triplets. The first table contains NOUNs and the other VERBs. So all queries to other poses will retrieve empty results.
 * Each rule-list result of {@link #getRulesForLeft(String, PartOfSpeech)} and 
 * {@link #getRulesForRight(String, PartOfSpeech)}
 * is sorted in decreasing order of similarity (to the queried lemma).
 *  <p>
 * Also note that all digits in queried lemma will be replaced with '@', and the lemmas in all retrieved rules will have '@'s where you'd 
 * expect digits.
 * <p>
 * Documentation about the tables at  {@link http://u.cs.biu.ac.il/~nlp/downloads/DIRECT.html}.
 * <p>
 * The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved. 
 * 
 * @author Amnon Lotan
 * @since 16/05/2011
 * 
 */
public abstract class AbstractDirectLexicalResource extends AbstractSimilarityLexicalResource
{
	private static final String PARAM_CONNECTION_STRING = "database_url";
	
	// Strings for the prepared statements
	private static final String L_COL = "lhs";
	private static final String R_COL = "rhs";
	private static final String SIM_COL = "score";
	private static final String JDBC_DRIVER_CLASS = "com.mysql.jdbc.Driver";

//	private Connection con;
	protected Connection con;
	
	private final Set<PreparedStatementAndPos> ALL_SCORES_STMTS = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> NOUN_SCORE_STMT_SET = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> VERB_SCORE_STMT_SET = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> EMPTY_SET = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> ALL_RULES_FOR_RIGHT_STMTS = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> RULES_FOR_RIGHT_STMT_NOUN = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> RULES_FOR_RIGHT_STMT_VERB = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> ALL_RULES_FOR_LEFT_STMTS = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> RULES_FOR_LEFT_STMT_NOUN = new LinkedHashSet<PreparedStatementAndPos>();
	private final Set<PreparedStatementAndPos> RULES_FOR_LEFT_STMT_VERB = new LinkedHashSet<PreparedStatementAndPos>();
	

	///////////////////////////////////////////////// PUBLIC /////////////////////////////////////////////////////////////////////
	
	/**
	 * Ctor using {@link ConfigurationParams}
	 * @param params
	 * @throws LexicalResourceException
	 * @throws ConfigurationException 
	 */
	public AbstractDirectLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		this(params.getString(PARAM_CONNECTION_STRING), null, null, params.getInt(PARAM_RULES_LIMIT));
	}
	
	/**
	 * Ctor
	 * @param connStr
	 * @param user
	 * @param password
	 * @param limitOnRetrievedRules  must be non negative. zero means all rules matching the query will be retrieved. 
	 * A positive value X means that only the top X rules are retrieved. 
	 * @throws LexicalResourceException
	 */
	public AbstractDirectLexicalResource(String connStr, String user, String password, int limitOnRetrievedRules) throws LexicalResourceException
	{
		super(limitOnRetrievedRules);
		PreparedStatement getRulesForLeftStmt_noun;
		PreparedStatement getRulesForLeftStmt_verb;
		PreparedStatement getRulesForRightStmt_noun;
		PreparedStatement getRulesForRightStmt_verb;
		PreparedStatement getScoresStmt_noun;
		PreparedStatement getScoresStmt_verb;

		try
		{
			Class.forName(JDBC_DRIVER_CLASS).newInstance();
			con = (user != null && password != null?
				DriverManager.getConnection(connStr, user, password):
				DriverManager.getConnection(connStr));
			getRulesForLeftStmt_noun 		= con.prepareStatement(getRulesForLeftQueryStr(getNounTableName()));
			getRulesForLeftStmt_verb 		= con.prepareStatement(getRulesForLeftQueryStr(getVerbTableName()));
			getRulesForRightStmt_noun 		= con.prepareStatement(getRulesForRightQueryStr(getNounTableName()));
			getRulesForRightStmt_verb 		= con.prepareStatement(getRulesForRightQueryStr(getVerbTableName()));
			getScoresStmt_noun 				= con.prepareStatement(getRulesForBothSidesQueryStr(getNounTableName()));
			getScoresStmt_verb 				= con.prepareStatement(getRulesForBothSidesQueryStr(getVerbTableName()));
		} catch (SQLException e) 	{ 
			throw new LexicalResourceException("Couldn't open and use a connection with this connection string: " + connStr +
					" and credentials: " + user + "/" + password, e);	
		} catch (InstantiationException e) {
			throw new LexicalResourceException("Could not instantiate the JDBC driver: " + JDBC_DRIVER_CLASS + " " + e.toString());
		} catch (IllegalAccessException e) {
			throw new LexicalResourceException("Could not instantiate the JDBC driver: " + JDBC_DRIVER_CLASS + " " + e.toString());
		} catch (ClassNotFoundException e) {
			throw new LexicalResourceException("Could not instantiate the JDBC driver: " + JDBC_DRIVER_CLASS + " " + e.toString());
		}
		
		final PreparedStatementAndPos GET_RULES_FOR_RIGHT_STMT_NOUN_WITH_POS = new PreparedStatementAndPos(getRulesForRightStmt_noun, NOUN );
		final PreparedStatementAndPos GET_RULES_FOR_RIGHT_STMT_VERB_WITH_POS = new PreparedStatementAndPos(getRulesForRightStmt_verb, VERB );
		
		final PreparedStatementAndPos GET_RULES_FOR_LEFT_STMT_NOUN_WITH_POS = new PreparedStatementAndPos(getRulesForLeftStmt_noun, NOUN );
		final PreparedStatementAndPos GET_RULES_FOR_LEFT_STMT_VERB_WITH_POS = new PreparedStatementAndPos(getRulesForLeftStmt_verb, VERB );

		final PreparedStatementAndPos GET_SCORES_STMT_NOUN_WITH_POS = new PreparedStatementAndPos(getScoresStmt_noun, NOUN );
		final PreparedStatementAndPos GET_SCORES_STMT_VERB_WITH_POS = new PreparedStatementAndPos(getScoresStmt_verb, VERB );

		
		ALL_RULES_FOR_RIGHT_STMTS.add(GET_RULES_FOR_RIGHT_STMT_VERB_WITH_POS);
		ALL_RULES_FOR_RIGHT_STMTS.add(GET_RULES_FOR_RIGHT_STMT_NOUN_WITH_POS);
		RULES_FOR_RIGHT_STMT_NOUN .add(GET_RULES_FOR_RIGHT_STMT_NOUN_WITH_POS);
		RULES_FOR_RIGHT_STMT_VERB.add(GET_RULES_FOR_RIGHT_STMT_VERB_WITH_POS); 
		
		ALL_RULES_FOR_LEFT_STMTS.add(GET_RULES_FOR_LEFT_STMT_NOUN_WITH_POS);
		ALL_RULES_FOR_LEFT_STMTS.add(GET_RULES_FOR_LEFT_STMT_VERB_WITH_POS);
		RULES_FOR_LEFT_STMT_NOUN .add(GET_RULES_FOR_LEFT_STMT_NOUN_WITH_POS);
		RULES_FOR_LEFT_STMT_VERB.add(GET_RULES_FOR_LEFT_STMT_VERB_WITH_POS);
		
		ALL_SCORES_STMTS.add(GET_SCORES_STMT_NOUN_WITH_POS);
		ALL_SCORES_STMTS.add(GET_SCORES_STMT_VERB_WITH_POS);
		NOUN_SCORE_STMT_SET .add(GET_SCORES_STMT_NOUN_WITH_POS);
		VERB_SCORE_STMT_SET .add(GET_SCORES_STMT_VERB_WITH_POS);
	}
	
	public void close() throws LexicalResourceCloseException
	{
		try
		{
			this.con.close();
		}
		catch (SQLException e)
		{
			throw new LexicalResourceCloseException("DB failed to close the connection.",e);
		}
	}



	///////////////////////////////////////////////////////////// PROTECTED METHODS ///////////////////////////////////////////////////////

	/**
	 * @param pos
	 * @param isRHS 
	 * @return
	 */
	protected Set<PreparedStatementAndPos> posToRulesStmts(PartOfSpeech pos, boolean isRHS) 
	{
		if (isRHS)
		{
			if (pos == null)	// wildcard POS
				return ALL_RULES_FOR_RIGHT_STMTS;
			switch (simplerPos(pos.getCanonicalPosTag()))
				{
				case NOUN:
					return RULES_FOR_RIGHT_STMT_NOUN;
				case VERB:
					return RULES_FOR_RIGHT_STMT_VERB;
			default:
				break;
				}
		}
		else
		{
			if (pos == null)	// wildcard POS
				return ALL_RULES_FOR_LEFT_STMTS;
			switch (simplerPos(pos.getCanonicalPosTag()))
				{
				case NOUN:
					return RULES_FOR_LEFT_STMT_NOUN;
				case VERB:
					return RULES_FOR_LEFT_STMT_VERB;
			default:
				break;
				}
		}
		return EMPTY_SET;
	}
	
	/**
	 * @param lPos
	 * @return
	 */
	protected Set<PreparedStatementAndPos> posToScoreStmt(PartOfSpeech pos) 
	{
		if (pos == null)
			return ALL_SCORES_STMTS;
		switch (simplerPos(pos.getCanonicalPosTag()))
		{
			case NOUN:
				return NOUN_SCORE_STMT_SET;
			case VERB:
				return VERB_SCORE_STMT_SET;
		default:
			break;
		}
		return EMPTY_SET;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#L_COL()
	 */
	@Override
	protected String L_COL() {
		return L_COL;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#R_COL()
	 */
	@Override
	protected String R_COL() {
		return R_COL;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#SIM_COL()
	 */
	@Override
	protected String SIM_COL() {
		return SIM_COL;
	}
	
	//////////////////////////////////////////////// Abstract	/////////////////////////////////////////////////
	
	/**
	 * @return
	 */
	protected abstract String getNounTableName();
	
	/**
	 * @return
	 */
	protected abstract String getVerbTableName();
}

