package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * A thread safe version of {@link WikiLexicalResourceDBServices}.<br> 
 * Basically, it avoids using {@link PreparedStatement}.
 * @author Eyal Shnarch
 *
 * @since 18/10/2012
 */
public class WikiLexicalResourceDBServicesThreadSafe {

	protected static final String JDBC_DRIVER_CLASS = "com.mysql.jdbc.Driver";
	protected static final WikiRuleScoreComparator RULE_RANK_AND_COOCURRENCE_COMPARATOR = new WikiRuleScoreComparator();
	protected static final String WILDCARD = "%";

	protected final Double COOCURENCE_THRESHOLD;
	protected final PartOfSpeech NOUN ;
	protected final Set<WikiExtractionType> PERMITTED_EXTRACTION_TYPES;
	
	protected static final String ARG1 = "?";
	protected static final String ARG2 = "@";

//	private final PreparedStatement getRulesFromRightStmt;
//	private final PreparedStatement getRulesFromRightStmt2;
//	private final PreparedStatement getRulesFromLeftStmt;
//	private final PreparedStatement getRulesFromLeftStmt2;
//	private final PreparedStatement getRulesFromRightAndLeftStmt;
//	private final PreparedStatement getHeadRuleStmt;
	
	protected Connection con;

	//////////////////////////////////////////// SQL infrastructure for this class	/////////////////////////////////////////////////////////////////////

	/**
	 * the term as an ngram version 
	 */
	private static final String GET_RULES_FROM_RIGHT_QUERY = 
		  "select t2.term as lhs, t1.term as rhs, r.method, t2.ngram_count as lhs_count, "
		+ "t1.ngram_count as rhs_count, r.ngram_count, r.rule_perc "
		+ "from terms t1, rules_new r, terms t2 "
		+ "where t1.id = r.rhs and t2.id = r.lhs and t1.term = '"+ARG1+"' Limit 500000";
	
	/**
	 * conditioned, Dice and cond*Dice from DB 
	 */
	private static final String GET_RULES_FROM_RIGHT_QUERY_2 = 
		  "select lhs.term as lhs, rhs.term as rhs, r.method, lhs.ngram_count as lhs_count, "
		+ "rhs.ngram_count as rhs_count, r.ngram_count, r.rule_perc, rc.condDice "
		+ "from terms rhs, rules_new r, terms lhs, rules_stats rc "
		+ "where rhs.id = r.rhs and lhs.id = r.lhs and r.lhs = rc.lhs and r.rhs = rc.rhs "
		+ "and rhs.term = '"+ARG1+"' and rc.condDice > "+ARG2+" limit 500000"; //
	
	/**
	 *  the term as an ngram version
	 */
	private static final String GET_RULES_FROM_LEFT_QUERY = 
		  "select t2.term as lhs, t1.term as rhs, r.method, t2.ngram_count as lhs_count, "
		+ "t1.ngram_count as rhs_count, r.ngram_count, r.rule_perc "
		+ "from terms t1, rules_new r, terms t2 "
		+ "where t1.id = r.rhs and t2.id = r.lhs and t2.term = '"+ARG1+"' Limit 1000000";

	
	/**
	 * conditioned, Dice and cond*Dice from DB 
	 */
	private static final String GET_RULES_FROM_LEFT_QUERY_2 = 
		  "select lhs.term as lhs, rhs.term as rhs, r.method, lhs.ngram_count as lhs_count, "
		+ "rhs.ngram_count as rhs_count, r.ngram_count, r.rule_perc, rc.condDice "
		+ "from terms rhs, rules_new r, terms lhs, rules_stats rc "
		+ "where rhs.id = r.rhs and lhs.id = r.lhs and r.lhs = rc.lhs and r.rhs = rc.rhs "
		+ "and lhs.term = '"+ARG1+"' and rc.condDice > "+ARG2+" limit 500000"; //
	
	
	/**
	 *  the term as an ngram version
	 */
	private static final String GET_RULES_FROM_RIGTH_AND_LEFT_QUERY = 
		  "select t2.term as lhs, t1.term as rhs, r.method, t2.ngram_count as lhs_count, "
		+ "t1.ngram_count as rhs_count, r.ngram_count, r.rule_perc " 
		+ "from terms t1, rules_new r, terms t2 "
		+ "where t1.term = '"+ARG1+"' and t2.term = '"+ARG2+"' and t1.id = r.rhs and t2.id = r.lhs  Limit 1";
	
	/**
	 * the term as an ngram version 
	 */
	private static final String FIND_HEAD_RULE_QUERY = 
		  "select t1.term as rhs, r.method, r.ngram_count "
		+ "from terms t1, rules_new r, terms t2 "
		+ "where t1.id = r.rhs and t2.id = r.lhs and t2.term = '"+ARG1+"' and t1.term like '"+ARG2+"'";
	
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
	protected WikiLexicalResourceDBServicesThreadSafe(String dbConnectionString, String dbUser, String dbPassword, Double cocurrence_threshold, 
			Set<WikiExtractionType> permittedExtractionTypes) throws LexicalResourceException 
	{
		// setup the prepared statements
		
		System.out.println(this.getClass().toString() + " connecting: " + dbConnectionString + " / user: " + dbUser + " / password: " + dbPassword );
		
		try {
			Class.forName(JDBC_DRIVER_CLASS).newInstance();
//			Connection con;
			if (dbUser != null && dbPassword != null)
				con = DriverManager.getConnection(dbConnectionString, dbUser, dbPassword);
			else
				con = DriverManager.getConnection(dbConnectionString);
//			getRulesFromRightStmt = con.prepareStatement(GET_RULES_FROM_RIGHT_QUERY);
//			getRulesFromRightStmt2 = con.prepareStatement(GET_RULES_FROM_RIGHT_QUERY_2);
//			getRulesFromLeftStmt = con.prepareStatement(GET_RULES_FROM_LEFT_QUERY);
//			getRulesFromLeftStmt2 = con.prepareStatement(GET_RULES_FROM_LEFT_QUERY_2);
//			getHeadRuleStmt = con.prepareStatement(FIND_HEAD_RULE_QUERY);
//			getRulesFromRightAndLeftStmt = con.prepareStatement(GET_RULES_FROM_RIGTH_AND_LEFT_QUERY);
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
	protected List<LexicalRule<? extends WikiRuleInfo>> getRulesForSideImpl(	String lemma, boolean getRulesFromRight) throws LexicalResourceException 
	{
		List<LexicalRule<? extends WikiRuleInfo>> rules = new ArrayList<LexicalRule<? extends WikiRuleInfo>>();
		lemma = lemma.replace("'", "\\'");	//escape apostrophe (e.g. Sophie's Choice)
		String query = null;
		try {
			// set the "get rule for side" statement and execute it
			if (getRulesFromRight) {
				if( COOCURENCE_THRESHOLD == null){
					query = GET_RULES_FROM_RIGHT_QUERY;
					query = query.replace(ARG1, lemma);
				}else{
					query = GET_RULES_FROM_RIGHT_QUERY_2;
					query = query.replace(ARG1, lemma);
					//add the co-occurrence filter threshold to DB query - more accurate rules - retrieve less rules - improve run time
					query = query.replace(ARG2, COOCURENCE_THRESHOLD.toString());
				}
			} else {	// get rules from left
				if( COOCURENCE_THRESHOLD == null){
					query = GET_RULES_FROM_LEFT_QUERY;
					query = query.replace(ARG1, lemma);
				}else{
					query = GET_RULES_FROM_LEFT_QUERY_2;
					query = query.replace(ARG1, lemma);
					//add the co-occurrence filter threshold to DB query - more accurate rules - retrieve less rules - improve run time
					query = query.replace(ARG2, COOCURENCE_THRESHOLD.toString());
				}
				
			}
//			Date s = new Date();
//			ResultSet resultSet = stmt.executeQuery();
			Statement stmt = con.createStatement();
//			System.out.println("[WikiLexRes] "+query);
			ResultSet resultSet = stmt.executeQuery(query);
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
	public List<LexicalRule<? extends WikiRuleInfo>> getRulesFromDb(String leftLemma, String rightLemma) throws LexicalResourceException {
		// query database and create one rule
		List<LexicalRule<? extends WikiRuleInfo>> ruleList;
		leftLemma = leftLemma.replace("'", "\\'");	//escape apostrophe (e.g. Sophie's Choice)
		rightLemma = rightLemma.replace("'", "\\'");	//escape apostrophe (e.g. Sophie's Choice)
		String query = null;
		try {
			query = GET_RULES_FROM_RIGTH_AND_LEFT_QUERY;
			query = query.replace(ARG1, rightLemma);
			query = query.replace(ARG2, leftLemma);
			Statement stmt = con.createStatement();
//			System.out.println("[WikiLexRes] "+query);
			ResultSet resultSet = stmt.executeQuery(query);
			resultSet.beforeFirst();
			ruleList = new ArrayList<LexicalRule<? extends WikiRuleInfo>>();
			if(resultSet.next())
				ruleList.add(constructRule(resultSet, leftLemma, false));
			resultSet.close();
		} catch (SQLException e) {
			throw new LexicalResourceException("SQL error while executing and reading from this statement: " + query, e);
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
	protected LexicalRule<WikiRuleInfo> constructRule(ResultSet resultSet, String lemma, boolean lemmaIsOnTheRight) throws LexicalResourceException 
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
	protected void addToRules(List<LexicalRule<? extends WikiRuleInfo>> rules, LexicalRule<WikiRuleInfo> rule, 
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
	protected int getRuleSideFreqFromDB(String lhsLemma, String rhsLemma, int ngramCount, String extactionTypesString)	throws LexicalResourceException {
		int ruleSideFreq = ngramCount;
		lhsLemma = lhsLemma.replace("'", "\\'");	//escape apostrophe (e.g. Sophie's Choice)
		rhsLemma = rhsLemma.replace("'", "\\'");	//escape apostrophe (e.g. Sophie's Choice)
		String query = null;
		try {
			query = FIND_HEAD_RULE_QUERY;
			query = query.replace(ARG1, lhsLemma);
			query = query.replace(ARG2, WILDCARD + rhsLemma + WILDCARD);
			Statement stmt = con.createStatement();
//			System.out.println("[WikiLexRes] "+query);
			ResultSet resultSet = stmt.executeQuery(query);
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
		} catch (SQLException e) {	throw new LexicalResourceException("Error executing SQL statement: " + query, e);		}
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

