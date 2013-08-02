/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.it;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiExtractionType;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiLexicalResourceDBServices;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiRuleInfo;



/**
 * This service class wraps the access to the Wikipedia mysql DB, and is supposed to be used only by {@link WikiLexicalResourceIT}. That's why 
 * the visibility of exported methods is package, so no other class can access this one.
 * <p>
 * <b>NOTE</b> Wiki supports only nouns. In case the user gives a POS that is not a noun nor null, the class returns a an empty list (not null).<br> 
 * <b>NOTE 2</b> This class is not thread safe, if you need multi-threaded code consider using {@link WikiLexicalResourceDBServicesThreadSafeIT}.
 * @author Amnon Lotan
 * @author Vivi Nastase (FBK)
 * 
 * @since Jan 11, 2012
 */
public class WikiLexicalResourceDBServicesIT extends WikiLexicalResourceDBServices{


	//////////////////////////////////////////// SQL infrastructure for this class	/////////////////////////////////////////////////////////////////////

	/**
	 * the term as an ngram version 
	 */
	@SuppressWarnings("unused")
	private static final String GET_RULES_FROM_RIGHT_QUERY = 
		  "select t2.value as lhs, t1.value as rhs, rt.ruleName as method, "
		+ "from term t1, rules r, ruletype rt, term t2 "
		+ "where t1.id = r.rightTermId and t2.id = r.leftTermId and r.ruleType = rt.id and t1.value = ? Limit 500000";
	
	/**
	 * conditioned, Dice and cond*Dice from DB 
	 */
	@SuppressWarnings("unused")
	private static final String GET_RULES_FROM_RIGHT_QUERY_2 = 
		  "select lhs.value as lhs, rhs.value as rhs, rt.ruleName as method, "
		+ "from term rhs, rules r, term lhs, ruletype rt "
		+ "where rhs.id = r.rightTermId and lhs.id = r.leftTermId and r.ruleType = rt.id "
		+ "and rhs.value = ? limit 500000"; //
	
	/**
	 *  the term as an ngram version
	 */
	@SuppressWarnings("unused")
	private static final String GET_RULES_FROM_LEFT_QUERY = 
		  "select t2.value as lhs, t1.value as rhs, rt.ruleName as method, "
		+ "from term t1, rules r, ruletype rt, term t2 "
		+ "where t1.id = r.rightTermId and t2.id = r.leftTermId and r.ruleType = rt.id and t2.value = ? Limit 1000000";

	
	/**
	 * conditioned, Dice and cond*Dice from DB 
	 */
	@SuppressWarnings("unused")
	private static final String GET_RULES_FROM_LEFT_QUERY_2 = 
		  "select lhs.value as lhs, rhs.value as rhs, rt.ruleName as method, "
		+ "from term rhs, rules r, ruletype rt, term lhs, "
		+ "where rhs.id = r.rightTermId and lhs.id = r.leftTermId and r.ruleType = rt.id "
		+ "and lhs.value = ? limit 500000"; //
	
	
	/**
	 *  the term as an ngram version
	 */
	@SuppressWarnings("unused")
	private static final String GET_RULES_FROM_RIGTH_AND_LEFT_QUERY = 
		  "select t2.value as lhs, t1.value as rhs, rt.ruleName as method, "
		+ "from term t1, rules r, term t2, ruletype rt "
		+ "where t1.value = ? and t2.value = ? and t1.id = r.rightTermId and t2.id = r.leftTermId and r.ruleType = rt.id Limit 1";
	
	/**
	 * the term as an ngram version 
	 */
	@SuppressWarnings("unused")
	private static final String FIND_HEAD_RULE_QUERY = 
		  "select t1.value as rhs, rt.ruleName as method, "
		+ "from term t1, rules r, term t2, ruletype rt "
		+ "where t1.id = r.rightTermId and t2.id = r.leftTermId and r.ruleType = rt.id and t2.value = ? and t1.value like ?";
	
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
	WikiLexicalResourceDBServicesIT(String dbConnectionString, String dbUser, String dbPassword, Double cooccurrence_threshold, 
			Set<WikiExtractionType> permittedExtractionTypes) throws LexicalResourceException 
	{		
		super(dbConnectionString, dbUser, dbPassword, cooccurrence_threshold, permittedExtractionTypes);
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
	@Override
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
			// This information is not provided in the Italian Wikipedia
			double rulePrecision = 0.5; //resultSet.getDouble(7);
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
*/			
			if(coocurenceScore > COOCURENCE_THRESHOLD){
				Set<WikiExtractionType> extractionTypes = getExtractionTypes(extractionTypesStr, rulePrecision);
				if (!extractionTypes.isEmpty())
				{
					WikiRuleInfo wikiInfo = new WikiRuleInfo(extractionTypes, coocurenceScore);
					rule = new LexicalRule<WikiRuleInfo>(lhsLemma, NOUN, rhsLemma, NOUN, wikiInfo.getRank(), wikiInfo.getBestExtractionType().toString(), 
							WikiLexicalResourceIT.WIKIPEDIA_RESOURCE_NAME, wikiInfo);
				}
			}
		} catch (SQLException e) {
			throw new LexicalResourceException("SQL Error raised when reading a result set: " + resultSet, e);
		}
		return rule;
	}

	/**
	 * Parse the extractionTypesStr according to its rulePrecisionScore, and retain only the types that are in PERMITTED_EXTRACTION_TYPES
	 * @param extractionTypesStr
	 * @param rulePrecisionScore
	 * @return
	 * @throws LexicalResourceException
	 */
	private Set<WikiExtractionType> getExtractionTypes(String extractionTypesStr, double rulePrecisionScore) throws LexicalResourceException {
		
		System.out.println(this.getClass() + " Extraction type: " + extractionTypesStr);
		
		Set<WikiExtractionType> extractionTypes = WikiExtractionType.parseExtractionTypeDBEntry(extractionTypesStr, rulePrecisionScore);
		// retain only the permitted extraction types
		extractionTypes.retainAll(PERMITTED_EXTRACTION_TYPES);	
		return extractionTypes;
	}
}

