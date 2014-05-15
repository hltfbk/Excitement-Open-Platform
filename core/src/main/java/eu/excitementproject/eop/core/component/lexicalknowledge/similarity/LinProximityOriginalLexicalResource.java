package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * <b>Resource name</b>: Dekang Lin's proximity-based thesaurus<br>
   <b>Resource description</b>: Resource is available for download from Dekang Lin's website, [2]. "Proximity-based thesaurus. Similar to the dependency-based thesaurus. But the words similarity is computed based on the linear proximity relationship between words only, where as the above thesaurus used dependency relationships extracted from a parsed corpus."<br>
   <b>POS</b>: nouns (n), verbs (v), adjectives & adverbs (a)<br>
   <b>Ref to relevant Paper</b>: �Automatic Retrieval and Clustering of Similar Words�, Dekang Lin, COLING-ACL, 1998, pp. 768-774.<br>
   <b>DB Scheme</b>: lin (qa-srv:3308)<br>
   <b>DB tables</b>: lin_proximity<br>
 * The table contains {@code <lemma, lemma, similarity>} 
 * triplets without parts of speech. So all queries ignore the given parts of speech and return rules featuring the {@code OTHER} 
 * {@link PartOfSpeech}.Each rule-list result of {@link #getRulesForLeft(String, PartOfSpeech)} and {@link #getRulesForRight(String, PartOfSpeech)}
 * is sorted in decreasing order of similarity (to the queried lemma).
 * <P>
 * See also: http://irsrv2/wiki/index.php/Lexical_Resources
 * 
 * 
 * @author Amnon Lotan
 * @since 16/05/2011
 * 
 */
public class LinProximityOriginalLexicalResource extends AbstractSinglePosLexicalResource
{
	private static final String RESOURCE_NAME = "Original Lin Proximity";
	public static final String PARAM_CONNECTION_STRING = "database_url";
	
	// Strings for the prepared statements
	private static final String TABLE = "lin_proximity";
	private static final String L_COL = "word1";
	private static final String R_COL = "word2";
	private static final String SIM_COL = "sim";
	
	final private Set<PreparedStatementAndPos> setOfGetRulesForLeftStmt = new LinkedHashSet<PreparedStatementAndPos>();
	final private Set<PreparedStatementAndPos> setOfGetRulesForRightStmt = new LinkedHashSet<PreparedStatementAndPos>();
	final private Set<PreparedStatementAndPos> setOfGetScoresStmt = new LinkedHashSet<PreparedStatementAndPos>();
	
	private PartOfSpeech DEFAULT_POS = null;
	
	///////////////////////////////////////////////// PUBLIC /////////////////////////////////////////////////////////////////////
	
	/**
	 * Ctor with {@link ConfigurationParams}
	 * @throws LexicalResourceException 
	 * @throws ConfigurationException 
	 */
	public LinProximityOriginalLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException 
	{
		this(params.getString(PARAM_CONNECTION_STRING), null, null, params.getInt(PARAM_RULES_LIMIT)	);
	}	

	/**
	 * Ctor
	 * <p>
 *  The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved.
	 * @param defaultConnStr
	 * @param user
	 * @param password
	 * @param limitOnRetrievedRules 
	 * @throws LexicalResourceException 
	 */
	public LinProximityOriginalLexicalResource(String connStr, String user, String password, int limitOnRetrievedRules) throws LexicalResourceException
	{
		super(limitOnRetrievedRules);
		
		// DEFAULT_POS must be initialized first thing, cos subsequent statements read it
		try 										{ DEFAULT_POS = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.OTHER);	} 
		catch (UnsupportedPosTagStringException e) 	{ throw new LexicalResourceException("Bug: couldn't construct a new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.OTHER)",e);		}
		
		PreparedStatement getRulesForLeftStmt;
		PreparedStatement getRulesForRightStmt;
		PreparedStatement getScoresStmt;
		
		try
		{
			Connection con = DriverManager.getConnection(connStr, user, password);
			getRulesForLeftStmt = con.prepareStatement(getRulesForLeftQueryStr(TABLE));
			getRulesForRightStmt = con.prepareStatement(getRulesForRightQueryStr(TABLE));
			getScoresStmt = con.prepareStatement(getRulesForBothSidesQueryStr(TABLE));
		} catch (SQLException e) 	{ 
			throw new LexicalResourceException("Couldn't open and use a connection with this connection string: " + connStr +
					" and credentials: " + user + "/" + password, e);	}

		setOfGetRulesForLeftStmt.add(new PreparedStatementAndPos(getRulesForLeftStmt, getDEFAULT_POS()));
		setOfGetRulesForRightStmt.add(new PreparedStatementAndPos(getRulesForRightStmt, getDEFAULT_POS()));
		setOfGetScoresStmt.add(new PreparedStatementAndPos(getScoresStmt, getDEFAULT_POS())) ;
	}

	///////////////////////////////////////////////////////////// PROTECTED METHODS ///////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractSinglePosLexResource#getDEFAULT_POS()
	 */
	@Override
	protected PartOfSpeech getDEFAULT_POS() {
		return DEFAULT_POS;
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

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#posToRulesStmt(ac.biu.nlp.nlp.representation.PartOfSpeech, boolean)
	 */
	@Override
	protected Set<PreparedStatementAndPos> posToRulesStmts(PartOfSpeech pos, boolean isRHS) {
		return isRHS ? setOfGetRulesForRightStmt: setOfGetRulesForLeftStmt;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#posToScoreStmt(ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	protected Set<PreparedStatementAndPos> posToScoreStmt(PartOfSpeech pos) {
		return setOfGetScoresStmt;
	}
	
	/** a null overriding implementation, that doesn't conceal the digits in the lemmas
	 * @see ac.biu.cs.nlp.lexical.resource.impl.similarity.AbstractSimilarityLexicalResource#cleanLemma(java.lang.String)
	 */
	@Override
	protected String cleanLemma(String lemma) {
		return lemma;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractSimilarityLexResource#getResourceName()
	 */
	@Override
	protected String getResourceName() {
		return RESOURCE_NAME;
	}
}

