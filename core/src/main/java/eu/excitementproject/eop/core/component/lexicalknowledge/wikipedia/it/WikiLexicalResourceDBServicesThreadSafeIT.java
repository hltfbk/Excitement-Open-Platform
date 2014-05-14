/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.it;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.LhsRhs;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiExtractionType;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiLexicalResourceDBServicesThreadSafe;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiRuleInfo;


/**
 * A thread safe version of {@link WikiLexicalResourceDBServicesIT}.<br> 
 * Basically, it avoids using {@link PreparedStatement}.
 * @author Eyal Shnarch
 * @author Vivi Nastase
 *
 * @since 18/10/2012
 */
public class WikiLexicalResourceDBServicesThreadSafeIT extends WikiLexicalResourceDBServicesThreadSafe {


	//////////////////////////////////////////// SQL infrastructure for this class	/////////////////////////////////////////////////////////////////////

	/**
	 * the term as an ngram version 
	 */
	private static final String GET_RULES_FROM_RIGHT_QUERY = 
		  "select t2.value as lhs, t1.value as rhs, rt.ruleName as method "
		+ "from term t1, rules r, ruletype rt, term t2 "
		+ "where t1.id = r.rightTermId and t2.id = r.leftTermId and r.ruleType = rt.id and t1.value = '"+ARG1+"' Limit 500000";
	
	/**
	 * conditioned, Dice and cond*Dice from DB 
	 */
	private static final String GET_RULES_FROM_RIGHT_QUERY_2 = 
		  "select lhs.value as lhs, rhs.value as rhs, rt.ruleName as method "
		+ "from term rhs, rules r, term lhs, ruletype rt "
		+ "where rhs.id = r.rightTermId and lhs.id = r.leftTermId and r.ruleType = rt.id "
		+ "and rhs.value = '"+ARG1+"' limit 500000"; //
	
	/**
	 *  the term as an ngram version
	 */
	private static final String GET_RULES_FROM_LEFT_QUERY = 
		  "select t2.value as lhs, t1.value as rhs, rt.ruleType as method "
		+ "from term t1, rules r, ruletype rt, term t2 "
		+ "where t1.id = r.rightTermId and t2.id = r.leftTermId and r.ruleType = rt.id and t2.value = '"+ARG1+"' Limit 1000000";

	
	/**
	 * conditioned, Dice and cond*Dice from DB 
	 */
	private static final String GET_RULES_FROM_LEFT_QUERY_2 = 
		  "select lhs.value as lhs, rhs.value as rhs, rt.ruleName as method "
		+ "from term rhs, rules r, ruletype rt, term lhs "
		+ "where rhs.id = r.rightTermId and lhs.id = r.leftTermId and r.ruleType = rt.id "
		+ "and lhs.value = '"+ARG1+"' limit 500000"; //
	
	
	/**
	 *  the term as an ngram version
	 */
	private static final String GET_RULES_FROM_RIGTH_AND_LEFT_QUERY = 
		  "select t2.value as lhs, t1.value as rhs, rt.ruleName as method "
		+ "from term t1, rules r, term t2, ruletype rt "
		+ "where t1.value = '"+ARG1+"' and t2.value = '"+ARG2+"' and t1.id = r.rightTermId and t2.id = r.leftTermId and r.ruleType = rt.id Limit 1";
	
	/**
	 * the term as an ngram version 
	 */
	private static final String FIND_HEAD_RULE_QUERY = 
		  "select t1.value as rhs, rt.ruleName as method "
		+ "from term t1, rules r, term t2, ruletype rt "
		+ "where t1.id = r.rightTermId and t2.id = r.leftTermId and r.ruleType = rt.id and t2.value = '"+ARG1+"' and t1.value like '"+ARG2+"'";

	
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
	WikiLexicalResourceDBServicesThreadSafeIT(String dbConnectionString, String dbUser, String dbPassword, Double cooccurrence_threshold, 
			Set<WikiExtractionType> permittedExtractionTypes) throws LexicalResourceException 
	{
		super(dbConnectionString, dbUser, dbPassword, cooccurrence_threshold, permittedExtractionTypes);
	}
	

	/**
	 * Query the DB for one rule from left to right 
	 * @param leftLemma
	 * @param rightLemma
	 * @return
	 * @throws LexicalResourceException 
	 */
	@Override
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
	 * Retrieve from the DB all rules to/from the lemma. Rules with identical lemmas are consolidated. 
	 * @param lemma
	 * @param getRulesFromRight
	 * @return
	 * @throws LexicalResourceException 
	 */
	@Override
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
//					query = query.replace(ARG2, COOCURENCE_THRESHOLD.toString());
				}
			} else {	// get rules from left
				if( COOCURENCE_THRESHOLD == null){
					query = GET_RULES_FROM_LEFT_QUERY;
					query = query.replace(ARG1, lemma);
				}else{
					query = GET_RULES_FROM_LEFT_QUERY_2;
					query = query.replace(ARG1, lemma);
					//add the co-occurrence filter threshold to DB query - more accurate rules - retrieve less rules - improve run time
//					query = query.replace(ARG2, COOCURENCE_THRESHOLD.toString());
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
			Map<LhsRhs, LexicalRule<WikiRuleInfo>> mapLemmasToRules = new HashMap<LhsRhs, LexicalRule<WikiRuleInfo>>();	//	used to detect duplicate rules
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
	 * Construct a new {@link LexicalRule} from the db query result set
	 * @param resultSet
	 * @param lemma
	 * @param lemmaIsOnTheRight
	 * @return
	 * @throws LexicalResourceException 
	 */
	@Override
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
			double rulePrecision = 0.5; // resultSet.getDouble(7);
			double coocurenceScore = 0.5;
/*			try{
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
*/			if(coocurenceScore > COOCURENCE_THRESHOLD){
	
				System.out.println(this.getClass().toString() + " Extraction type: " + extractionTypesStr);
	
				Set<WikiExtractionType> extractionTypes = getExtractionTypes(extractionTypesStr, rulePrecision);
				if (!extractionTypes.isEmpty())
				{
					WikiRuleInfo wikiInfo = new WikiRuleInfo(extractionTypes, coocurenceScore);
					rule = new LexicalRule<WikiRuleInfo>(lhsLemma, NOUN, rhsLemma, NOUN, wikiInfo.getRank(), wikiInfo.getBestExtractionType().toString(), 
							WikiLexicalResourceIT.WIKIPEDIA_RESOURCE_NAME, wikiInfo);
				} else {
					System.out.println(this.getClass().toString() + " Extraction set EMPTY! ");					
				}
			}
		} catch (SQLException e) {
			throw new LexicalResourceException("SQL Error raised when reading a result set: " + resultSet, e);
		}
		return rule;
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
	@Override
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
//					ruleSideFreq = ngramCount - resultSet.getInt(3);
					ruleSideFreq = 1;
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

