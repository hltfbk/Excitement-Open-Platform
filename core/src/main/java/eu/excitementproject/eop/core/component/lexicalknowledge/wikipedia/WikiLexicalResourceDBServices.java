package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;



/**
 * This service class wraps the access to the Wikipedia mysql DB, and is supposed to be used only by {@link WikiLexicalResource}. That's why 
 * the visibility of exported methods is package, so no other class can access this one.
 * <p>
 * <b>NOTE</b> Wiki supports only nouns. In case the user gives a POS that is not a noun nor null, the class returns a an empty list (not null).<br> 
 * <b>NOTE 2</b> This class is not thread safe, if you need multi-threaded code consider using {@link WikiLexicalResourceDBServicesThreadSafe}.
 * @author Amnon Lotan
 *
 * @since Jan 11, 2012
 */
public class WikiLexicalResourceDBServices {

	protected static final String JDBC_DRIVER_CLASS = "com.mysql.jdbc.Driver";
	protected static final WikiRuleScoreComparator RULE_RANK_AND_COOCURRENCE_COMPARATOR = new WikiRuleScoreComparator();
	protected static final String WILDCARD = "%";

	protected final Double COOCURENCE_THRESHOLD;
	protected final PartOfSpeech NOUN ;
	protected final Set<WikiExtractionType> PERMITTED_EXTRACTION_TYPES;

	protected final PreparedStatement getRulesFromRightStmt;
	protected final PreparedStatement getRulesFromRightStmt2;
	protected final PreparedStatement getRulesFromLeftStmt;
	protected final PreparedStatement getRulesFromLeftStmt2;
	protected final PreparedStatement getRulesFromRightAndLeftStmt;
	protected final PreparedStatement getHeadRuleStmt;
	
	protected Connection con;

	//////////////////////////////////////////// SQL infrastructure for this class	/////////////////////////////////////////////////////////////////////

	/**
	 * the term as an ngram version 
	 */
	private static final String GET_RULES_FROM_RIGHT_QUERY = 
		  "select t2.term as lhs, t1.term as rhs, r.method, t2.ngram_count as lhs_count, "
		+ "t1.ngram_count as rhs_count, r.ngram_count, r.rule_perc "
		+ "from terms t1, rules_new r, terms t2 "
		+ "where t1.id = r.rhs and t2.id = r.lhs and t1.term = ? Limit 500000";
	
	/**
	 * conditioned, Dice and cond*Dice from DB 
	 */
	private static final String GET_RULES_FROM_RIGHT_QUERY_2 = 
		  "select lhs.term as lhs, rhs.term as rhs, r.method, lhs.ngram_count as lhs_count, "
		+ "rhs.ngram_count as rhs_count, r.ngram_count, r.rule_perc, rc.condDice "
		+ "from terms rhs, rules_new r, terms lhs, rules_stats rc "
		+ "where rhs.id = r.rhs and lhs.id = r.lhs and r.lhs = rc.lhs and r.rhs = rc.rhs "
		+ "and rhs.term = ? and rc.condDice > ? limit 500000"; //
	
	/**
	 *  the term as an ngram version
	 */
	private static final String GET_RULES_FROM_LEFT_QUERY = 
		  "select t2.term as lhs, t1.term as rhs, r.method, t2.ngram_count as lhs_count, "
		+ "t1.ngram_count as rhs_count, r.ngram_count, r.rule_perc "
		+ "from terms t1, rules_new r, terms t2 "
		+ "where t1.id = r.rhs and t2.id = r.lhs and t2.term = ? Limit 1000000";

	
	/**
	 * conditioned, Dice and cond*Dice from DB 
	 */
	private static final String GET_RULES_FROM_LEFT_QUERY_2 = 
		  "select lhs.term as lhs, rhs.term as rhs, r.method, lhs.ngram_count as lhs_count, "
		+ "rhs.ngram_count as rhs_count, r.ngram_count, r.rule_perc, rc.condDice "
		+ "from terms rhs, rules_new r, terms lhs, rules_stats rc "
		+ "where rhs.id = r.rhs and lhs.id = r.lhs and r.lhs = rc.lhs and r.rhs = rc.rhs "
		+ "and lhs.term = ? and rc.condDice > ? limit 500000"; //
	
	
	/**
	 *  the term as an ngram version
	 */
	private static final String GET_RULES_FROM_RIGTH_AND_LEFT_QUERY = 
		  "select t2.term as lhs, t1.term as rhs, r.method, t2.ngram_count as lhs_count, "
		+ "t1.ngram_count as rhs_count, r.ngram_count, r.rule_perc " 
		+ "from terms t1, rules_new r, terms t2 "
		+ "where t1.term = ? and t2.term = ? and t1.id = r.rhs and t2.id = r.lhs  Limit 1";
	
	/**
	 * the term as an ngram version 
	 */
	private static final String FIND_HEAD_RULE_QUERY = 
		  "select t1.term as rhs, r.method, r.ngram_count "
		+ "from terms t1, rules_new r, terms t2 "
		+ "where t1.id = r.rhs and t2.id = r.lhs and t2.term = ? and t1.term like ?";
	
	/////////////////////////////////// PACKAGE 	////////////////////////////////////////////////////////////
	
	
	/**
	 * Ctor
	 * @param dbConnectionString
	 * @param dbUser
	 * @param dbPassword
	 * @param cocurrence_threshold	may be null
	 * @param permittedExtractionTypes 
	 * @throws LexicalResourceException 
	 */
	protected WikiLexicalResourceDBServices(String dbConnectionString, String dbUser, String dbPassword, Double cocurrence_threshold, 
			Set<WikiExtractionType> permittedExtractionTypes) throws LexicalResourceException 
	{
		// setup the prepared statements
		try {
			Class.forName(JDBC_DRIVER_CLASS).newInstance();
//			Connection con;
			if (dbUser != null && dbPassword != null)
				con = DriverManager.getConnection(dbConnectionString, dbUser, dbPassword);
			else
				con = DriverManager.getConnection(dbConnectionString);
			getRulesFromRightStmt = con.prepareStatement(GET_RULES_FROM_RIGHT_QUERY);
			getRulesFromRightStmt2 = con.prepareStatement(GET_RULES_FROM_RIGHT_QUERY_2);
			getRulesFromLeftStmt = con.prepareStatement(GET_RULES_FROM_LEFT_QUERY);
			getRulesFromLeftStmt2 = con.prepareStatement(GET_RULES_FROM_LEFT_QUERY_2);
			getHeadRuleStmt = con.prepareStatement(FIND_HEAD_RULE_QUERY);
			getRulesFromRightAndLeftStmt = con.prepareStatement(GET_RULES_FROM_RIGTH_AND_LEFT_QUERY);
		} catch (SQLException e) {
			throw new LexicalResourceException("error in establishing a connection to " + dbConnectionString +" "+dbUser+" "+dbPassword,e);
		} catch (InstantiationException e) {
			throw new LexicalResourceException("Could not instantiate the JDBC driver: " + JDBC_DRIVER_CLASS + " " + e.toString(),e);
		} catch (IllegalAccessException e) {
			throw new LexicalResourceException("Could not instantiate the JDBC driver: " + JDBC_DRIVER_CLASS + " " + e.toString(),e);
		} catch (ClassNotFoundException e) {
			throw new LexicalResourceException("Could not instantiate the JDBC driver: " + JDBC_DRIVER_CLASS + " " + e.toString(),e);
		}

		COOCURENCE_THRESHOLD = cocurrence_threshold;	// may be null
		this.PERMITTED_EXTRACTION_TYPES = permittedExtractionTypes ;
		try {	NOUN = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);	}
		catch (UnsupportedPosTagStringException e) {	throw new LexicalResourceException("Internal error", e);	}
	}
	
	/**
	 * Retrieve from the DB all rules to/from the lemma. Rules with identical lemmas are consolidated. 
	 * @param lemma
	 * @param getRulesFromRight
	 * @return
	 * @throws LexicalResourceException 
	 */
	List<LexicalRule<? extends WikiRuleInfo>> getRulesForSideImpl(	String lemma, boolean getRulesFromRight) throws LexicalResourceException 
	{
		List<LexicalRule<? extends WikiRuleInfo>> rules = new ArrayList<LexicalRule<? extends WikiRuleInfo>>();
		String query = null;
		try {
			// set the "get rule for side" statement and execute it
			PreparedStatement stmt;
			if (getRulesFromRight) {
				if( COOCURENCE_THRESHOLD == null){
					getRulesFromRightStmt.setString(1, lemma);
					stmt = getRulesFromRightStmt;
				}else{
					//add the co-occurrence filter threshold to DB query - more accurate rules - retrieve less rules - improve run time
					getRulesFromRightStmt2.setString(1, lemma);
					getRulesFromRightStmt2.setDouble(2, COOCURENCE_THRESHOLD);
					stmt = getRulesFromRightStmt2;
				}
			} else {	// get rules from left
				if( COOCURENCE_THRESHOLD == null){
					getRulesFromLeftStmt.setString(1, lemma);
					stmt = getRulesFromLeftStmt;
//					System.out.println(stmt.toString());
				}else{
					//add the co-occurrence filter threshold to DB query - more accurate rules - retrieve less rules - improve run time
					getRulesFromLeftStmt2.setString(1, lemma);
					getRulesFromLeftStmt2.setDouble(2, COOCURENCE_THRESHOLD);
					stmt = getRulesFromLeftStmt2;
				}
				
			}
//			Date s = new Date();
			ResultSet resultSet = stmt.executeQuery();
//			System.out.println("[WikiLexRes] "+query);
//			Date e = new Date();
//			System.out.println("    WikipediaLexicalResource - after executeQuery "+(e.getTime() - s.getTime())+"\t"+stmt.toString());
			// make a new rule from every result row
			Map<LhsRhs, LexicalRule<WikiRuleInfo>> mapLemmasToRules = new LinkedHashMap<LhsRhs, LexicalRule<WikiRuleInfo>>();	//	used to detect duplicate rules
			while (resultSet.next()) {
				LexicalRule<WikiRuleInfo> rule = constructRule(resultSet, lemma, getRulesFromRight);
				addToRules(rules, rule, mapLemmasToRules);
			}
			resultSet.close();
		} catch (SQLException e) {
			throw new LexicalResourceException("Error executing sql query: "+query+" See nested", e);
		}
		
		// sort rules in descending rank and coocurrence order
		Collections.sort(rules, RULE_RANK_AND_COOCURRENCE_COMPARATOR);
		return rules;
	}
	
	/**
	 * Query the DB for one rule from left to right 
	 * @param leftLemma
	 * @param rightLemma
	 * @return
	 * @throws LexicalResourceException 
	 */
	List<LexicalRule<? extends WikiRuleInfo>> getRulesFromDb(String leftLemma, String rightLemma) throws LexicalResourceException {
		// query database and create one rule
		List<LexicalRule<? extends WikiRuleInfo>> ruleList;
		try {
			getRulesFromRightAndLeftStmt.setString(1, rightLemma);
			getRulesFromRightAndLeftStmt.setString(2, leftLemma);
			ResultSet resultSet = getRulesFromRightAndLeftStmt.executeQuery();
			resultSet.beforeFirst();
			ruleList = new ArrayList<LexicalRule<? extends WikiRuleInfo>>();
			if(resultSet.next())
				ruleList.add(constructRule(resultSet, leftLemma, false));
			resultSet.close();
		} catch (SQLException e) {
			throw new LexicalResourceException("SQL error while executing and reading from this statement: " + getRulesFromRightAndLeftStmt, e);
		}
		return ruleList;
	}

	/////////////////////////////////////////////////////// PRIVATE	//////////////////////////////////////////////////////////
	
	/**
	 * Construct a new {@link LexicalRule} from the db query result set
	 * @param resultSet
	 * @param lemma
	 * @param lemmaIsOnTheRight
	 * @return
	 * @throws LexicalResourceException 
	 */
	public LexicalRule<WikiRuleInfo> constructRule(ResultSet resultSet, String lemma, boolean lemmaIsOnTheRight) throws LexicalResourceException 
	{
		LexicalRule<WikiRuleInfo> rule = null;
		
		try {
			String lhsLemma, rhsLemma;
			if(lemmaIsOnTheRight)
			{	
				rhsLemma = lemma;
				lhsLemma = resultSet.getString(1);
			}else{
				lhsLemma = lemma;
				rhsLemma = resultSet.getString(2);
			}
			String extractionTypesStr = resultSet.getString(3);
			double rulePrecision = resultSet.getDouble(7);
			double coocurenceScore;
			try{
				coocurenceScore = resultSet.getDouble(8);
			}catch(SQLException e){
				if(COOCURENCE_THRESHOLD != null){		//TODO: is this can possible?
					int ngramCount = resultSet.getInt(6);
					int freqOfLhsInDocs = resultSet.getInt(4);
					int freqOfRhsInDocs = resultSet.getInt(5);
					coocurenceScore = computeCoocurence(lhsLemma, rhsLemma, ngramCount, extractionTypesStr,	freqOfLhsInDocs, freqOfRhsInDocs);
				}else{
					coocurenceScore = Integer.MAX_VALUE;
				}
			}
			if(coocurenceScore > COOCURENCE_THRESHOLD){
				Set<WikiExtractionType> extractionTypes = getExtractionTypes(extractionTypesStr, rulePrecision);
				if (!extractionTypes.isEmpty())
				{
					WikiRuleInfo wikiInfo = new WikiRuleInfo(extractionTypes, coocurenceScore);
					rule = new LexicalRule<WikiRuleInfo>(lhsLemma, NOUN, rhsLemma, NOUN, wikiInfo.getRank(), wikiInfo.getBestExtractionType().toString(), 
							WikiLexicalResource.WIKIPEDIA_RESOURCE_NAME, wikiInfo);
				}
			}
		} catch (SQLException e) {
			throw new LexicalResourceException("SQL Error raised when reading a result set: " + resultSet, e);
		}
		return rule;
	}
	
	/**
	 * Consolidate this rule with an existing similar rule (if any) and add to the list
	 * @param rules
	 * @param rule
	 * @param mapLemmasToRules 
	 * @throws LexicalResourceException 
	 */
	void addToRules(List<LexicalRule<? extends WikiRuleInfo>> rules, LexicalRule<WikiRuleInfo> rule, 
			Map<LhsRhs, LexicalRule<WikiRuleInfo>> mapLemmasToRules) throws LexicalResourceException {
		if (rule != null)
		{
			LhsRhs lhsRhs = new LhsRhs(rule.getLLemma().toLowerCase(), rule.getRLemma().toLowerCase());	// don't compare case
			if (mapLemmasToRules.containsKey(lhsRhs))
			{
				// remove the existing rule  
				LexicalRule<WikiRuleInfo> otherRule = mapLemmasToRules.get(lhsRhs);
				rules.remove(otherRule);
				
				// and consolidate the  two rules
				Set<WikiExtractionType> unitedExtractionTypes = rule.getInfo().getExtractionTypes().getMutableSetCopy();
				unitedExtractionTypes.addAll(otherRule.getInfo().getExtractionTypes().getMutableSetCopy());
				double highestCoocurrenceScore = Math.max(rule.getInfo().getCoocurenceScore(), otherRule.getInfo().getCoocurenceScore());
				WikiRuleInfo newWikiInfo = new WikiRuleInfo( unitedExtractionTypes, highestCoocurrenceScore);
				rule = new LexicalRule<WikiRuleInfo>(rule.getLLemma(), NOUN, rule.getRLemma(), NOUN, newWikiInfo.getBestExtractionType().toString(), 
						WikiLexicalResource.WIKIPEDIA_RESOURCE_NAME, newWikiInfo);
			}
			mapLemmasToRules.put(lhsRhs, rule);
			rules.add(rule);
		}
	}
	
	private double computeCoocurence(String lhsLemma, String rhsLemma, int ngramCount, String extractionTypesStr, int freqOfLhsInDocs, int freqOfRhsInDocs) 
			throws LexicalResourceException {
		int bothFreqsInDocuments = getBothFreqsInDocuments(lhsLemma, rhsLemma, ngramCount, extractionTypesStr);
		double conditioned = (freqOfLhsInDocs != 0) ? (double) bothFreqsInDocuments / freqOfLhsInDocs : 0;
		double dice = (freqOfRhsInDocs + freqOfLhsInDocs > 0) ? (double) 2 * bothFreqsInDocuments / (freqOfRhsInDocs + freqOfLhsInDocs) :	0;
		return dice * conditioned;
	}

	/**
	 * If there is a defined coocurrence threshold, and the rhs lemma is a single word, query the DB for the coocurrence frequency of the two lemmas. 
	 * Else, return the given ngram frequency.  
	 * @param lhsLemma
	 * @param rhsLemma
	 * @param ngramCount
	 * @param extactionTypesString
	 * @return
	 * @throws LexicalResourceException
	 */
	private int getBothFreqsInDocuments(String lhsLemma, String rhsLemma, int ngramCount, String extactionTypesString) throws LexicalResourceException {
		return (COOCURENCE_THRESHOLD != null || !rhsLemma.contains(" ")) ? getRuleSideFreqFromDB(lhsLemma, rhsLemma, ngramCount, extactionTypesString)	:	ngramCount;
	}

	/**
	 * query the DB for the coocurrence frequency of the two lemmas. If the DB query returns empty, return the default ngram count. 
	 * @param lhsLemma
	 * @param rhsLemma	a single word
	 * @param ngramCount	the frequency of the combination of both lemmas in texts
	 * @param extactionTypesString
	 * @return
	 * @throws LexicalResourceException
	 */
	private int getRuleSideFreqFromDB(String lhsLemma, String rhsLemma, int ngramCount, String extactionTypesString)	throws LexicalResourceException {
		int ruleSideFreq = ngramCount;
		try {
			getHeadRuleStmt.setString(1, lhsLemma);
			getHeadRuleStmt.setString(2, WILDCARD + rhsLemma + WILDCARD);
			ResultSet resultSet = getHeadRuleStmt.executeQuery();
			boolean foundACoocurrence = false;
			while (!foundACoocurrence && resultSet.next()) {
				String queriedRhsLemma = resultSet.getString(1);
				String method = resultSet.getString(2);

				// accept any queriedRhsLemma that really contains the given lhsLemma
				if (!queriedRhsLemma.equals(rhsLemma) && queriedRhsLemma.contains(rhsLemma)	&& queriedRhsLemma.contains(" ")	 
						&& extactionTypesString.equals(method))		// accept only coocorences of the same extraction type
				{
					ruleSideFreq = ngramCount - resultSet.getInt(3);
					foundACoocurrence  = true;
				}
			} 
			resultSet.close();
		} catch (SQLException e) {	throw new LexicalResourceException("Error executing SQL statement: " + getHeadRuleStmt, e);		}
		return ruleSideFreq;
	}
	
	/**
	 * Parse the extractionTypesStr according to its rulePrecisionScore, and retain only the types that are in PERMITTED_EXTRACTION_TYPES
	 * @param extractionTypesStr
	 * @param rulePrecisionScore
	 * @return
	 * @throws LexicalResourceException
	 */
	private Set<WikiExtractionType> getExtractionTypes(String extractionTypesStr, double rulePrecisionScore) throws LexicalResourceException {
		Set<WikiExtractionType> extractionTypes = WikiExtractionType.parseExtractionTypeDBEntry(extractionTypesStr, rulePrecisionScore);
		// retain only the permitted extraction types
		extractionTypes.retainAll(PERMITTED_EXTRACTION_TYPES);	
		return extractionTypes;
	}
}

