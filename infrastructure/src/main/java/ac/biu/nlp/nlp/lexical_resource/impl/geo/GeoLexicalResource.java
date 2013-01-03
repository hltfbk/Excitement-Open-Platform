package ac.biu.nlp.nlp.lexical_resource.impl.geo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ac.biu.nlp.nlp.general.configuration.ConfigurationException;
import ac.biu.nlp.nlp.general.configuration.ConfigurationParams;
import ac.biu.nlp.nlp.lexical_resource.EmptyRuleInfo;
import ac.biu.nlp.nlp.lexical_resource.LexicalResource;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceNothingToClose;
import ac.biu.nlp.nlp.lexical_resource.LexicalRule;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * A {@link LexicalResource} to wrap the Geo resource's MYSQL tables. 
 * <p>
 * Based on Idan's old code.
 * <p>
 * The part of speech on GEO is always NOUN, and thus the POS parameters are ignored.
 *   
 * @author Amnon Lotan
 *
 * @since 15 Jan 2012
 */
public class GeoLexicalResource extends LexicalResourceNothingToClose<EmptyRuleInfo> {

	private static final String GEO_RESOURCE_NAME = "GEO";
	private static final String PARAM_DB_CONNECTION_STRING = "db-connection-string";
	private static final String PARAM_TABLE_NAME = "geo-rules-table-name";
	private static final EmptyRuleInfo EMPTY_INFO = EmptyRuleInfo.getInstance();	
	private static final String GET_RULES_QUERY_HEAD = "select * from ";
	private static final String GET_RULES_ENTAILED_BY_QUERY_TAIL = " where entailed = ?";
	private static final String GET_RULES_ENTAILING_QUERY_TAIL =   " where entailing = ?";
	private static final String GET_RULE_FOR_BOTH_SIDES_QUERY_TAIL = " where entailing = ? and entailed = ?";
	
	private final PreparedStatement GET_RULES_ENTAILED_BY_STMT;
	private final PreparedStatement GET_RULES_ENTAILING_STMT;
	private final PreparedStatement GET_RULE_FOR_BOTH_SIDES_STMT;
	
	private final UnspecifiedPartOfSpeech NOUN;
	
	/**
	 * Ctor
	 * @throws ConfigurationException 
	 * @throws LexicalResourceException 
	 */
	public GeoLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException {
		this(params.get(PARAM_DB_CONNECTION_STRING), params.get(PARAM_TABLE_NAME));
	}
	
	/**
	 * Ctor
	 * @throws LexicalResourceException 
	 */
	public GeoLexicalResource(String dbConnectionString, String tableName) throws LexicalResourceException {

		if (dbConnectionString == null)
			throw new LexicalResourceException("got null connection string");
		if (dbConnectionString.isEmpty())
			throw new LexicalResourceException("got empty connection string");
		if (tableName == null)
			throw new LexicalResourceException("got null table name");
		if (tableName.isEmpty())
			throw new LexicalResourceException("got empty table name");
		
		Connection con;
		try {	
			con = DriverManager.getConnection(dbConnectionString);	
			GET_RULES_ENTAILED_BY_STMT = con.prepareStatement(GET_RULES_QUERY_HEAD + tableName + GET_RULES_ENTAILED_BY_QUERY_TAIL);
			GET_RULES_ENTAILING_STMT = con.prepareStatement(GET_RULES_QUERY_HEAD + tableName + GET_RULES_ENTAILING_QUERY_TAIL);
			GET_RULE_FOR_BOTH_SIDES_STMT = con.prepareStatement(GET_RULES_QUERY_HEAD + tableName + GET_RULE_FOR_BOTH_SIDES_QUERY_TAIL);
		}
		catch (SQLException e) {	throw new LexicalResourceException("Could not open connection or create statments at : " + dbConnectionString, e);		}
		
		try 										{ NOUN = new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN);	} 
		catch (UnsupportedPosTagStringException e) 	{ throw new LexicalResourceException("Bug: couldn't construct a new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN)",e);		}
	}


	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends EmptyRuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos)
			throws LexicalResourceException {
		if (leftLemma == null)		throw new LexicalResourceException("got null left lemma");
		if (rightLemma == null)		throw new LexicalResourceException("got null right lemma");
		
		List<LexicalRule<? extends EmptyRuleInfo>> rules = new ArrayList<LexicalRule<? extends EmptyRuleInfo>>();
		try {
			GET_RULE_FOR_BOTH_SIDES_STMT.setString(1, leftLemma);
			GET_RULE_FOR_BOTH_SIDES_STMT.setString(2, rightLemma);
			ResultSet resultSet = GET_RULE_FOR_BOTH_SIDES_STMT.executeQuery();
			if (resultSet.next()) {
				rules.add(new LexicalRule<EmptyRuleInfo>(leftLemma, NOUN, rightLemma, NOUN, null, GEO_RESOURCE_NAME, EMPTY_INFO));
				
				if (resultSet.next())
					throw new LexicalResourceException("Geo bug! found two rules for "+ leftLemma + " and " + rightLemma); 
			}
			resultSet.close();
		} catch (SQLException e) {
			throw new LexicalResourceException("Error executing query: " + GET_RULE_FOR_BOTH_SIDES_STMT, e);
		}
		
		return rules;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends EmptyRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRuleForSide(lemma, GET_RULES_ENTAILED_BY_STMT);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends EmptyRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRuleForSide(lemma, GET_RULES_ENTAILING_STMT);
	}
	
	
	
	//////////////////////////// PRIVATE	//////////////////////////////////////////////
	
	/**
	 * @param lemma
	 * @param stmt
	 * @return
	 * @throws LexicalResourceException 
	 */
	private List<LexicalRule<? extends EmptyRuleInfo>> getRuleForSide(String lemma, PreparedStatement stmt) throws LexicalResourceException 
	{
		if (lemma == null)
			throw new LexicalResourceException("got null lemma");
		
		List<LexicalRule<? extends EmptyRuleInfo>> rules = new ArrayList<LexicalRule<? extends EmptyRuleInfo>>();
		try {
			stmt.setString(1, lemma);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				String entailing = resultSet.getString(1);
				String entailed = resultSet.getString(2);
				rules.add(new LexicalRule<EmptyRuleInfo>(entailing, NOUN, entailed, NOUN, null, GEO_RESOURCE_NAME, EMPTY_INFO));
			}
			resultSet.close();
		} catch (SQLException e) {
			throw new LexicalResourceException("Error executing query: " + stmt, e);
		}
		
		return rules;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		GET_RULE_FOR_BOTH_SIDES_STMT.close();
		GET_RULES_ENTAILED_BY_STMT.close();
		GET_RULES_ENTAILING_STMT.close();
		super.finalize();
	}
}
