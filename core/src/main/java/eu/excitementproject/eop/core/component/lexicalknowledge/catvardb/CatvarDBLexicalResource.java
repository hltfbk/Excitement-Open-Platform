/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.catvardb;

import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import eu.excitementproject.eop.core.component.lexicalknowledge.EmptyRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceNothingToClose;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;


/**
 * <b>DEPRECATED</b> This class works, and is stable, but is deprecated, because accessing Catvar via DB is much slower than via simple file system. We expect a new 
 * file-based implementation sometime soon.
 * <p>  
 * This {@link LexicalResource} wraps the <b>catvar</b> database. It provides access to CatVar rules made up from {@code <lemma, pos>} terms in the 
 * same catvar <i>cluster</i>. The database must contain the following tables:
 * <li>{@code cluster_term} containing rows like {@code <cluster_id, term_id>}
 * <li>{@code term} containing rows like {@code <term_id, lemma, pos>} (pos is one of {'a','n','r','v'} representing the part-of-speech).<br>
 * Notice how this means the resource in essence is undirected.
 * <br>The user may alter these and other db parameters in the constructor. 
 * 
 * 
 * 
 * @author Amnon Lotan
 * @since 16/05/2011
 * 
 */
@Deprecated 
public class CatvarDBLexicalResource extends LexicalResourceNothingToClose<RuleInfo> 
{
	private static final String RESOURCE_NAME = "Catvar";
	private static final String PARAM_DB_CONNECTION_STRING = "db-connection-string"; 
	
	/**
	 * retrieve the cluster of the given lemma+pos
	 */
	private static final String CLUSTERS_ID_QUERY_STR = "SELECT cluster_id, pos FROM cluster_term ct, term " +
			"WHERE term.term_id = ct.term_id and " +
			"lemma = ? and pos = ?";
	private static final String CLUSTERS_ID_QUERY_STR_NO_POS = "SELECT cluster_id, pos FROM cluster_term ct, term " +
			"WHERE term.term_id = ct.term_id and " +
			"lemma = ?";

	private static final String CLUSTERS_TERMS_QUERY_BEGIN = "select lemma, term.pos, termClusters.pos FROM term, cluster_term ct, (";
	private static final String CLUSTERS_TERMS_QUERY_END = ") termClusters WHERE ct.cluster_id = termClusters.cluster_id AND term.term_id = ct.term_id"; 	
	/**
	 * retrieve all the <lemma,pos> tuples that are in the cluster of the given lemma+pos 
	 */
	private static final  String CLUSTERS_TERMS_QUERY_STR = CLUSTERS_TERMS_QUERY_BEGIN + CLUSTERS_ID_QUERY_STR + CLUSTERS_TERMS_QUERY_END;
	private static final  String CLUSTERS_TERMS_QUERY_STR_NO_POS = CLUSTERS_TERMS_QUERY_BEGIN + CLUSTERS_ID_QUERY_STR_NO_POS + CLUSTERS_TERMS_QUERY_END;

	private static final String X_CLUSTERS_QUERY_BEGIN = "SELECT leftTermClusters.cluster_id, leftTermClusters.pos, rightTermClusters.pos FROM (";
	private static final String X_CLUSTERS_QUERY_MIDDLE = ") leftTermClusters, (";
	private static final String X_CLUSTERS_QUERY_END = ") rightTermClusters WHERE leftTermClusters.cluster_id = rightTermClusters.cluster_id ";

	/**
	 * to retrive the POS used in the rule look at resultSet.getString(2), the second result value
	 */
	private final PreparedStatement common_clusters_stmt_no_pos ;
	private final PreparedStatement common_clusters_stmt_no_left_pos;
	private final PreparedStatement common_clusters_stmt_no_right_pos;
	private final PreparedStatement common_clusters_stmt;
	
	private final PreparedStatement common_terms_stmt;
	private final PreparedStatement common_terms_stmt_no_pos;
	
	protected static final RuleInfo EMPTY_RULE_INFO = EmptyRuleInfo.getInstance();

	/**
	 * <b>DEPRECATED</b> This class works, and is stable, but is deprecated, because accessing Catvar via DB is much slower than via simple file system. We expect a new 
	 * file-based implementation sometime soon.
	 * <p>  
	 * Ctor
	 * use default db parameters
	 * @throws LexicalResourceException 
	 * @throws ConfigurationException 
	 */
	@Deprecated
	public CatvarDBLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException 
	{
		this(params.get(PARAM_DB_CONNECTION_STRING), null, null);
	}
	
	/**
	 * <b>DEPRECATED</b> This class works, and is stable, but is deprecated, because accessing Catvar via DB is much slower than via simple file system. We expect a new 
	 * file-based implementation sometime soon.
	 * <p>  
	 * Ctor
	 * @param dbConnectionString connection string to catvar database. e.g. "jdbc:mysql://qa-srv:3308/catvar"
	 * @param dbUser 	e.g. "db_readonly"
	 * @param dbPassword 
	 * @throws LexicalResourceException
	 */
	@Deprecated
	public CatvarDBLexicalResource(String dbConnectionString, String dbUser, String dbPassword) throws LexicalResourceException 
	{
		try
		{
			Connection con = DriverManager.getConnection(dbConnectionString, dbUser, dbPassword);
			common_terms_stmt = con.prepareStatement(CLUSTERS_TERMS_QUERY_STR);
			common_terms_stmt_no_pos = con.prepareStatement(CLUSTERS_TERMS_QUERY_STR_NO_POS);
			common_clusters_stmt = con.prepareStatement(constructXClustersQueryString(CLUSTERS_ID_QUERY_STR,CLUSTERS_ID_QUERY_STR));
			common_clusters_stmt_no_pos = con.prepareStatement(constructXClustersQueryString(CLUSTERS_ID_QUERY_STR_NO_POS,CLUSTERS_ID_QUERY_STR_NO_POS));       
			common_clusters_stmt_no_left_pos = con.prepareStatement(constructXClustersQueryString(CLUSTERS_ID_QUERY_STR_NO_POS,CLUSTERS_ID_QUERY_STR));  
			common_clusters_stmt_no_right_pos = con.prepareStatement(constructXClustersQueryString(CLUSTERS_ID_QUERY_STR,CLUSTERS_ID_QUERY_STR_NO_POS));  
		} catch (SQLException e) 	{ 
			throw new LexicalResourceException("Couldn't open and use a connection with this connection string: " + dbConnectionString +
					" and credentials: " + dbUser + "/" + dbPassword, e);	}
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRules(lemma, pos, true);
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRules(lemma, pos, false);
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends RuleInfo>> getRules(String lLemma, PartOfSpeech lPos, String rLemma, PartOfSpeech rPos) throws LexicalResourceException 
	{
		List<LexicalRule<? extends RuleInfo>> rules = new Vector<LexicalRule<? extends RuleInfo>>();
		ResultSet rs;
		try 					
		{
			// query for this lemma+pos, lemma+pos
			PreparedStatement stmt = prepareCommonClustersPreparedStatement(lLemma, lPos, rLemma, rPos);
			rs = stmt.executeQuery();	
		} 
		catch (SQLException e) 	{	throw new LexicalResourceException("Error executing the query " + common_clusters_stmt,e);	}
		
		try 					
		{
			// create a rule for each result
			if(rs.first())
				do {
					PartOfSpeech lPosFromQuery = toPartOfSpeech(rs.getString(2));	// it's possible that lPos and rPos are null, so we take the POS from the result
					PartOfSpeech rPosFromQuery = toPartOfSpeech(rs.getString(3));
					rules.add(new LexicalRule<RuleInfo>(lLemma, lPosFromQuery, rLemma, rPosFromQuery, null, RESOURCE_NAME, EMPTY_RULE_INFO));
//					if (rs.next())
//						throw new LexicalResourceException("Bug alert! got two different catvar rules featuring: " +
//								lLemma + ", " + lPos + ", " + rLemma + ", " + rPos + " using this query: " + common_clusters_stmt);
				}	while (rs.next());
			rs.close();
		}
		catch (SQLException e) 	{	throw new LexicalResourceException("Error reading the result set of the query " + common_clusters_stmt,e);	}
		return rules;
	}

	/**
	 * return the catvar POS matching the given POS. if null, return all POSs
	 * @param pos
	 * @return
	 */
	public String toShortPos(PartOfSpeech pos) {
		{
			if (pos == null)
				return null;
			else
				switch(simplerPos(pos.getCanonicalPosTag()) )
				{
				case ADJECTIVE:
					return "a";
				case ADVERB:
					return "r";
				case DETERMINER:
					return "d";
				case NOUN:
					return "n";
				case VERB:
					return "v";
				case PREPOSITION:
					return "p";
				case PRONOUN:
					return "pro";
				case PUNCTUATION:
					return "punc";
				case OTHER:
					return "o";
				default:
					return "o";
				}
		}
	}
	
	////////////////////////////////////////// PRIVATE ////////////////////////////////////////////////////////////////

	/**
	 * Retrieve all the catvar rules that feature the given lemma+pos in either their left or right side.<br>
	 * the pos may be null, to signify a wildcard
	 * 
	 * @param lemma
	 * @param pos
	 * @param isRHS
	 * @return
	 * @throws LexicalResourceException
	 */
	private List<LexicalRule<? extends RuleInfo>> getRules(String lemma, PartOfSpeech pos, boolean isRHS) throws LexicalResourceException 
	{
		List<LexicalRule<? extends RuleInfo>> rules = new Vector<LexicalRule<? extends RuleInfo>>();
		String shortPos = toShortPos(pos);
		
		{
			ResultSet rs;
			PreparedStatement stmt = null;
			try 					
			{
				// query for this lemma+pos
				stmt = prepareCommonTermsPreparedStatement(lemma, shortPos, isRHS);
				rs = stmt.executeQuery();	} 
			catch (SQLException e) 	{	throw new LexicalResourceException("Error executing the query " + stmt, e);	}

			try 					
			{
				// create a rule for each result
				if(rs.first()){
					do {
						String otherLemma = rs.getString(1);	
						PartOfSpeech otherPos = toPartOfSpeech(rs.getString(2));
						PartOfSpeech posFromQuery = toPartOfSpeech(rs.getString(3)); // it's possible that pos is null, so we take the POS from the result

						if (!lemma.equals(otherLemma) || !posFromQuery.equals(otherPos))	// don't create a reflexive rule
						{
							rules.add( isRHS ? 	
								new LexicalRule<RuleInfo>(otherLemma, otherPos, lemma, posFromQuery, null, RESOURCE_NAME, EMPTY_RULE_INFO) 
									:
								new LexicalRule<RuleInfo>(lemma, posFromQuery, otherLemma, otherPos, null, RESOURCE_NAME, EMPTY_RULE_INFO) 
							);
						}
					} while (rs.next());
				}
				rs.close();
			}
			catch (SQLException e) 	{	throw new LexicalResourceException("Error reading the result set of the query " + common_terms_stmt,e);	}
		}
		return rules;
	}
		
	/**
	 * @param lemma
	 * @param shortPos
	 * @param isRHS
	 * @return
	 * @throws LexicalResourceException 
	 */
	private PreparedStatement prepareCommonTermsPreparedStatement(String lemma,	String shortPos, boolean isRHS) throws LexicalResourceException {
		PreparedStatement stmt = null;
		try {
			if (shortPos == null)
			{
				stmt = common_terms_stmt_no_pos;
				common_terms_stmt_no_pos.setString(1, lemma);
			}
			else
			{
				stmt = common_terms_stmt;
				stmt.setString(1, lemma); 
				stmt.setString(2, shortPos );				
			}
		} catch (SQLException e) {
			throw new LexicalResourceException("Error constructing the query " + stmt, e);	
		}

		return stmt;
	}

	/**
	 * @param lLemma
	 * @param lPos
	 * @param rLemma
	 * @param rPos
	 * @return
	 * @throws LexicalResourceException 
	 */
	private PreparedStatement prepareCommonClustersPreparedStatement(String lLemma, PartOfSpeech lPos, String rLemma, PartOfSpeech rPos) 
			throws LexicalResourceException {
		PreparedStatement stmt;
		
		String shortLPos =  toShortPos(lPos);
		String shortRPos =  toShortPos(rPos);
		try {
			if (shortLPos == null && shortRPos == null)
			{
				stmt = common_clusters_stmt_no_pos;
				stmt.setString(1, lLemma);
				stmt.setString(2, rLemma);
			}
			else if (shortLPos == null)
			{
				stmt = common_clusters_stmt_no_left_pos;
				stmt.setString(1, lLemma);
				stmt.setString(2, rLemma);
				stmt.setString(3, shortRPos);
			}
			else if (shortRPos == null)
			{
				stmt = common_clusters_stmt_no_right_pos;
				stmt.setString(1, lLemma);
				stmt.setString(2, shortLPos);
				stmt.setString(3, rLemma);
			} 
			else
			{
				stmt = common_clusters_stmt;
				stmt.setString(1, lLemma); 
				stmt.setString(2, shortLPos);
				stmt.setString(3, rLemma); 
				stmt.setString(4, shortRPos);
			}
		} catch (SQLException e) {
			throw new LexicalResourceException("Error setting the prepared statement: " + common_clusters_stmt,e);	
		}
		return stmt;
	}
	
	/**
	 * @param otherShortPos
	 * @return
	 * @throws LexicalResourceException 
	 */
	private PartOfSpeech toPartOfSpeech(String shortPos) throws LexicalResourceException
	{
	 	SimplerCanonicalPosTag canonicalPosTag;
		 
		 switch(shortPos.charAt(0) )
		 {
			case 'a':
				canonicalPosTag = SimplerCanonicalPosTag.ADJECTIVE;
				 break;
	
			 case 'r':
				 canonicalPosTag = SimplerCanonicalPosTag.ADVERB;
				 break;

			 case 'd':
				 canonicalPosTag = SimplerCanonicalPosTag.DETERMINER;
				 break;
	
			 case 'n':
				 canonicalPosTag = SimplerCanonicalPosTag.NOUN;
				 break;
			
			 case 'v':
				 canonicalPosTag = SimplerCanonicalPosTag.VERB;
				 break;

			 default:
			 	if (shortPos.equals("p"))
		 			canonicalPosTag = SimplerCanonicalPosTag.PREPOSITION;
			 	else if (shortPos.equals("pro"))
			 			canonicalPosTag = SimplerCanonicalPosTag.PRONOUN;
			 	else if (shortPos.equals("punc"))
			 		canonicalPosTag = SimplerCanonicalPosTag.PUNCTUATION;
			 	else
			 		canonicalPosTag = SimplerCanonicalPosTag.OTHER;
		 }
		 try {
			return new BySimplerCanonicalPartOfSpeech(canonicalPosTag);
		} catch (UnsupportedPosTagStringException e) 
		{
			throw new LexicalResourceException("This is some strange bug regarding UnspecifiedPartOfSpeech", e);
		}
	}
	
	/**
	 * @param leftClustersIdQueryStr
	 * @param rightClustersIdQueryStr
	 * @return
	 */
	private String constructXClustersQueryString(String leftClustersIdQueryStr,	String rightClustersIdQueryStr) {
		return X_CLUSTERS_QUERY_BEGIN + leftClustersIdQueryStr + X_CLUSTERS_QUERY_MIDDLE + rightClustersIdQueryStr + X_CLUSTERS_QUERY_END;
	}
}

