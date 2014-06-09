package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
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
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;


/**
 * Abstract class with all you need to make up a simple LexResource based on a set of {@code <lemma, lemma, similarity>} style tables (with each 
 * table supposedly matching a simple part of speech).
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
public abstract class AbstractSimilarityLexicalResource extends LexicalResourceNothingToClose<RuleInfo>
{
	protected static final String DIGIT_REPLACEMENT = "@";
	protected static final RuleInfo EMPTY_RULE_INFO = EmptyRuleInfo.getInstance();
	/**
	 * name of {@link ConfigurationParams} int parameter. must be non negative. zero means all rules matching the query will be retrieved. 
	 * A positive value X means that only the top X rules are retrieved.  
	 */
	protected static final String PARAM_RULES_LIMIT = "limit on retrieved rules";
	
	protected final String RESOURCE_NAME = getResourceName();
	/**
	 * the sql clause at the end of a statement that goes " ... limit X"
	 */
	private final String LIMIT_CLAUSE;

	protected final PartOfSpeech ADJECTIVE;
	protected final PartOfSpeech NOUN;
	protected final PartOfSpeech VERB;
	
	// names of table columns
	protected abstract String L_COL();
	protected abstract String R_COL();
	protected abstract String SIM_COL();

	/**
	 * Ctor
	 * @param limitOnRetrievedRules 
	 * @throws LexicalResourceException 
	 */
	public AbstractSimilarityLexicalResource(int limitOnRetrievedRules) throws LexicalResourceException {
		try {
			ADJECTIVE = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.ADJECTIVE);
			NOUN = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
			VERB = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.VERB);
		} catch (UnsupportedPosTagStringException e) {
			throw new LexicalResourceException("Couldn't create UnspecifiedPartOfSpeech", e);
		}
		
		if (limitOnRetrievedRules < 0)
			throw new LexicalResourceException("the limitOnRetrievedRules must be positive, or zero to mean 'no limit'. I got " + limitOnRetrievedRules);
		LIMIT_CLAUSE = (limitOnRetrievedRules > 0 ? " LIMIT " + limitOnRetrievedRules : "");
	}
	/**
	 * replace digits with '@'
	 * 
	 * @param lemma
	 * @return
	 */
	protected String cleanLemma(String lemma) 
	{
		return lemma.replaceAll("\\d", DIGIT_REPLACEMENT);
	}


	/**
	 * In case the user gives <code>null</code> POS, retrieve rules for all possible POSs.
	 * 
	 * @param lemma
	 * @param pos
	 * @param isRHS
	 * @return
	 * @throws LexicalResourceException
	 */
	protected List<LexicalRule<? extends RuleInfo>> getRulesForSide(String lemma,	PartOfSpeech pos, boolean isRHS) throws LexicalResourceException
	{
		lemma = cleanLemma(lemma);
		List<LexicalRule<? extends RuleInfo>> rules = new Vector<LexicalRule<? extends RuleInfo>>();
		
		Set<PreparedStatementAndPos> stmts = posToRulesStmts(pos, isRHS);	// it's possible the pos doesn't match any table, and we have an empty set
		for (PreparedStatementAndPos stmtAndPos : stmts)	
		{
			PreparedStatement stmt = stmtAndPos.getStmt();
			ResultSet rs;
			try 					
			{
				// query for this lemma+pos  
				stmt.setString(1, lemma);
				rs = stmt.executeQuery();	
			} 
			catch (SQLException e) 	{	throw new LexicalResourceException("Error executing the query " + stmt,e);	}
			try 					
			{
				// create a rule for each result
				if(rs.first()) do {
					String otherLemma = rs.getString(1);
					double score;	 
					try 							{score = Double.parseDouble( rs.getString(2) );	} 
					catch (NumberFormatException e) {throw new LexicalResourceException("Database error: this is not a double " + rs.getString(1), e);	}
					PartOfSpeech posFromQuery = stmtAndPos.getPos();
					rules.add( isRHS ? 	new LexicalRule<RuleInfo>(otherLemma, posFromQuery, lemma, posFromQuery, score, null, RESOURCE_NAME, EMPTY_RULE_INFO) 
									:
										new LexicalRule<RuleInfo>(lemma, posFromQuery, otherLemma, posFromQuery, score, null, RESOURCE_NAME, EMPTY_RULE_INFO) ); 
				} while (rs.next());

//				rs.close();
			}
			catch (SQLException e) 	{	throw new LexicalResourceException("Error reading the result set of the query " + stmt,e);	}
		}
		return rules;
	}
	
	/**
	 * Use this template method to construct a query that retrieves rules for a given lhs 
	 * 
	 * @param tableName
	 * @return
	 */
	protected final String getRulesForLeftQueryStr(String tableName)
	{
		return "SELECT " + R_COL() + ", " + SIM_COL() + " FROM " + tableName + " WHERE " + L_COL() + " = ? ORDER BY " + SIM_COL() + " DESC" + LIMIT_CLAUSE;
	}

	/**
	 * Use this template method to construct a query that retrieves rules for a given rhs 
	 * 
	 * @param tableName
	 * @return
	 */
	protected final String getRulesForRightQueryStr(String tableName)
	{
		return "SELECT " + L_COL() + ", " + SIM_COL() + " FROM " + tableName + " WHERE " + R_COL() + " = ? ORDER BY " + SIM_COL() + " DESC" + LIMIT_CLAUSE;
	}

	/**
	 * Use this template method to construct a query that retrieves the score for a given lhs and rhs 
	 * 
	 * @param tableName
	 * @return
	 */
	protected final String getRulesForBothSidesQueryStr(String tableName)
	{
		return "SELECT " + SIM_COL() + " FROM " + tableName + " WHERE " + L_COL() + " = ? AND " + R_COL() + " = ? ORDER BY " + SIM_COL() + " DESC" + LIMIT_CLAUSE;
	}
	
	///////////////////////////////////////////////// PUBLIC /////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException 
	{
		return getRulesForSide(lemma, pos, true);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException 
	{
		return getRulesForSide(lemma, pos, false);
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	public List<LexicalRule<? extends RuleInfo>> getRules(String lLemma, PartOfSpeech lPos, String rLemma, PartOfSpeech rPos) throws LexicalResourceException 
	{
		List<LexicalRule<? extends RuleInfo>> rules = new Vector<LexicalRule<? extends RuleInfo>>();
		
		lLemma = cleanLemma(lLemma);
		rLemma = cleanLemma(rLemma);
		if ((lPos == null) || (rPos == null) || (lPos.equals(rPos)))	// if the pos doesn't match, return an empty list
		{
			PartOfSpeech posForConstructingQuery = lPos != null ? lPos : rPos;	// queries should be made with the more specific (not wildcard) POS. This filters the results 
																	// according to the more specific POS
			Set<PreparedStatementAndPos> stmts = posToScoreStmt(posForConstructingQuery);
			for (PreparedStatementAndPos stmtAndPos : stmts)
			{
				ResultSet rs;
				PreparedStatement stmt = stmtAndPos.getStmt();
				try 					
				{
					// query for this lemma+pos, lemma+pos  
					stmt.setString(1, lLemma);
					stmt.setString(2, rLemma); 
					rs = stmt.executeQuery();
				} 
				catch (SQLException e) 	{	throw new LexicalResourceException("Error executing the query " + stmt,e);	}
				try 					
				{
					// create a rule for each result
					if(rs.first())
					{
						double score;
						try 							{score = Double.parseDouble( rs.getString(1) );	} 
						catch (NumberFormatException e) {throw new LexicalResourceException("Database error: this is not a double " + rs.getString(1), e);	}
						PartOfSpeech posFromQuery = stmtAndPos.getPos();
						rules.add(new LexicalRule<RuleInfo>(lLemma, posFromQuery, rLemma, posFromQuery, score, null, RESOURCE_NAME, EMPTY_RULE_INFO));
						if (rs.next())
							throw new LexicalResourceException("Bug alert! got two different lin rules featuring: " +
									lLemma + ", " + lPos + ", " + rLemma + ", " + rPos + " using this query: " + stmt);
					}
					rs.close();
				}
				catch (SQLException e) 	{	throw new LexicalResourceException("Error reading the result set of the query " + stmt,e);	}
			}
		}
		return rules;
	}

	///////////////////////////////////////////////////////////// protected abstract ///////////////////////////////////////////////////////
	
	/**
	 * get a POS and side-flag and returns the matching PreparedStatement made with {@link #getRulesForLeftQueryStr(String)} or {@link #getRulesForRightQueryStr(String)}
	 * <br>If there is no appropriate PreparedStatement to use, return an empty set
	 * 
	 * @param pos
	 * @param isRHS 
	 * @return
	 */
	protected abstract Set<PreparedStatementAndPos> posToRulesStmts(PartOfSpeech pos, boolean isRHS); 
		
	/**
	 * get a POS and returns the matching PreparedStatement made with {@link #getRulesForBothSidesQueryStr(String)} 
	 * <br>If there is no appropriate PreparedStatement to use, return an empty set
	 * 
	 * @param pos
	 * @return
	 */
	protected abstract Set<PreparedStatementAndPos> posToScoreStmt(PartOfSpeech pos);
	
	/**
	 * get the name of this {@link LexicalResource} as some enum value
	 * @return
	 */
	protected abstract String getResourceName();
}

