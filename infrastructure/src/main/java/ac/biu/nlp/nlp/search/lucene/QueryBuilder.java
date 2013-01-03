package ac.biu.nlp.nlp.search.lucene;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import ac.biu.nlp.nlp.search.lucene.LuceneSearchManagerExample.DocField;

/**
 * This class holds loads of methods that return a few popular formulae for {@link Query}s, based on {@code lucene-core-3.0.1.jar}.
 * <p>
 * <b>Note about proximity search and its distance parameter:</b> it's weird. A zero or negative distance means that all the words of a phrase
 * must be adjacent. However, the order in which the words appear in the doc matters! Moreover, required distance is <i>0</i> if the last word in the doc 
 * appears just before the first_word_of_the_doc in the search phrase, but the distance is <i>great</i> if the first_word_of_the_doc appears 
 * before the last_word_of_the_doc in the search phrase.
 * <p>
 * <b>Adding POS:</b> I think that in order to add a dimension of POS to searches, you need a DocReader that pos tags the text files it gets,
 * so lucene indexes space-separated word_POS tokens. Ergo, all queries, or all text submitted to these here methods, must use that format.  
 * <p>
 * <b>Performance note:</b> In cases where u want to run a complex query, with many clauses, it should be much faster to use a naive query 
 * instead, and use a {@link TermsFilter} when searching. See {@linkplain http://lucene.apache.org/java/3_0_1/api/all/org/apache/lucene/search/TermsFilter.html} 
 * 
 * @see More Query syntax features
 * <br>http://lucene.apache.org/java/3_0_1/queryparsersyntax.html
 * @author Amnon Lotan
 *
 * @since 10/03/2011
 */
public class QueryBuilder
{
	public static final String DEAFAULT_DOC_FIELD = "TEXT";

	/**
	 * Ctor with default {@link DocField} and {@link StandardAnalyzer}, with its built-in stopwords list
	 */
	public QueryBuilder() 
	{
		this(DEAFAULT_DOC_FIELD);
	}
	
	/**
	 * Ctor with {@link StandardAnalyzer}, and its built-in stopwords list.
	 * @param defaultField in the queries
	 */
	public QueryBuilder(String defaultField) 
	{
		this(defaultField, new StandardAnalyzer(LuceneSearchManagerExample.LUCENE_CURRENT_VERSION));
	}
	
	/**
	 * Ctor with {@link StandardAnalyzer}, and its built-in stopwords list.
	 * @param defaultField in the queries
	 * @param stopwords 
	 * @throws IOException in case there's a problem reading the stopwords file 
	 */
	public QueryBuilder(String defaultField, File stopwords) throws IOException 
	{
		this(defaultField, new StandardAnalyzer(LuceneSearchManagerExample.LUCENE_CURRENT_VERSION, stopwords));
	}
	
	/**
	 * Ctor that takes a default-field name, and an analyzer
	 * @param defaultField in the queries
	 * @param analyzer of the {@link QueryParser}
	 */
	public QueryBuilder(String defaultField, Analyzer analyzer) 
	{
		queryParser  = new QueryParser(LuceneSearchManagerExample.LUCENE_CURRENT_VERSION, defaultField, analyzer);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param conjointClauses 	A list of clauses (words, phrases) that must occur. Each clause must occur
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query andQuery(List<String> conjointClauses) throws IrException
	{
		checkListParam(conjointClauses);
		List<List<String>> conjointWordsInAList = new Vector<List<String>>();
		conjointWordsInAList.add(conjointClauses);
		return buildQuery(conjointWordsInAList, null, DISTANCE_DISABLED, false);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param conjointClauses 	A list of clauses (words, phrases) that must occur. Each clause must occur
	 * @param distance			An occurrence of a word in the mustOccurWords counts, only if it's found {@code distance} words away from another 
	 * 							listed word 
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query andQuery(List<String> conjointClauses, int distance) throws IrException
	{
		checkListParam(conjointClauses, distance);
		List<List<String>> conjointWordsInAList = new Vector<List<String>>();
		conjointWordsInAList.add(conjointClauses);
		return buildQuery(conjointWordsInAList, null, distance, false);
	}

	/**
	 * Return a query specified by the parameters
	 * 
	 * @param conjointClauses 	A list of clauses (words, phrases) that must occur. Each clause must occur
	 * @param mustNotOccurClauses	A blacklist of clauses, of which ALL must NOT occur
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query andQuery(List<String> conjointClauses, List<String> mustNotOccurClauses) throws IrException
	{
		checkListParam(conjointClauses);
		checkListParam(mustNotOccurClauses);
		List<List<String>> conjointWordsInAList = new Vector<List<String>>();
		conjointWordsInAList.add(conjointClauses);
		return buildQuery(conjointWordsInAList, mustNotOccurClauses, DISTANCE_DISABLED, false);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param conjointClauses 		A list of clauses (words, phrases) that must occur. Each clause must occur
	 * @param mustNotOccurClauses	A blacklist of clauses, of which ALL must NOT occur
	 * @param distance				An occurrence of a word in the conjointClauses counts, only if it's found {@code distance} words away from 
	 * 								another listed word. Doesn't apply to the mustNotOccurClauses.
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query andQuery(List<String> conjointClauses, List<String> mustNotOccurClauses, int distance) throws IrException
	{
		checkListParam(conjointClauses, distance);
		checkListParam(mustNotOccurClauses);
		List<List<String>> conjointWordsInAList = new Vector<List<String>>();
		conjointWordsInAList.add(conjointClauses);
		return buildQuery(conjointWordsInAList, mustNotOccurClauses, distance, false);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param disjointClauses 	A list of words that must occur. At least one word must occur
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query orQuery(List<String> disjointClauses) throws IrException
	{
		checkListParam(disjointClauses);
		List<List<String>> disjointWordsInAList = new Vector<List<String>>();
		disjointWordsInAList.add(disjointClauses);
		return buildQuery(disjointWordsInAList, null, DISTANCE_DISABLED, true);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param disjointClauses 	A list of words that must occur. At least one word must occur
	 * @param mustNotOccurClauses	a blacklist of words, of which ALL must NOT occur
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query orQuery(List<String> disjointClauses, List<String> mustNotOccurClauses) throws IrException
	{
		checkListParam(disjointClauses);
		checkListParam(mustNotOccurClauses);
		List<List<String>> disjointWordsInAList = new Vector<List<String>>();
		disjointWordsInAList.add(disjointClauses);
		return buildQuery(disjointWordsInAList, mustNotOccurClauses, DISTANCE_DISABLED, true);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param mustOccurClauses 	a list of lists of words that must occur. Each word in at least one list must occur
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query orAndQuery(List<List<String>> mustOccurClauses) throws IrException
	{
		checkListParam(mustOccurClauses);
		return buildQuery(mustOccurClauses, null, DISTANCE_DISABLED, false);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param mustOccurClauses 	A list of lists of words that must occur. Each word in at least one list must occur
	 * @param distance			An occurrence of a word in the mustOccurWords counts, only if it's found {@code distance} words away from another 
	 * 							listed word
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query orAndQuery(List<List<String>> mustOccurClauses, int distance) throws IrException
	{
		checkListParam(mustOccurClauses, distance);
		return buildQuery(mustOccurClauses, null, distance, false);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param mustOccurClauses 	a list of lists of words that must occur. Each word in at least one list must occur
	 * @param mustNotOccurClauses	a blacklist of words, of which ALL must NOT occur
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query orAndQuery(List<List<String>> mustOccurClauses, List<String> mustNotOccurClauses) throws IrException
	{
		checkListParam(mustOccurClauses);
		checkListParam(mustNotOccurClauses);
		return buildQuery(mustOccurClauses, mustNotOccurClauses, DISTANCE_DISABLED, false);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param mustOccurClauses 	A list of lists of words that must occur. Each word in at least one list must occur
	 * @param mustNotOccurClauses	A blacklist of words, of which ALL must NOT occur
	 * @param distance			An occurrence of a word in the mustOccurWords counts, only if it's found {@code distance} words away from another 
	 * 							listed word. Doesn't apply to the mustNotOccurClauses
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query orAndQuery(List<List<String>> mustOccurClauses, List<String> mustNotOccurClauses, int distance) throws IrException
	{
		checkListParam(mustOccurClauses, distance);
		checkListParam(mustNotOccurClauses);
		return buildQuery(mustOccurClauses, mustNotOccurClauses, distance, false);
	}

	/**
	 * Return a query specified by the parameters
	 * 
	 * @param mustOccurClauses 	a list of lists of words that must occur. At least one word out of each inner list must occur
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query andOrQuery(List<List<String>> mustOccurClauses) throws IrException
	{
		checkListParam(mustOccurClauses);
		return buildQuery(mustOccurClauses, null, DISTANCE_DISABLED, true);
	}
	
	/**
	 * Return a query specified by the parameters
	 * 
	 * @param mustOccurClauses 	a list of lists of words that must occur. At least one word out of each inner list must occur
	 * @param mustNotOccurClauses	a blacklist of words, of which ALL must NOT occur
	 * @return the Query
	 * @throws IrException 
	 */
	public  Query andOrQuery(List<List<String>> mustOccurClauses, List<String> mustNotOccurClauses) throws IrException
	{
		checkListParam(mustOccurClauses);
		checkListParam(mustNotOccurClauses);
		return buildQuery(mustOccurClauses, mustNotOccurClauses, DISTANCE_DISABLED, true);
	}
	
	/**
	 * Return a query specified by the parameters. Input is checked in the public methods.
	 * 
	 * @param mustOccurWords 	A list of lists of words that must occur. either (At least one word in each list must occur) or 
	 * 							(each word in at least one list must occur), depending on andOr
	 * @param mustNotOccurClauses	A blacklist of words, of which ALL must NOT occur
	 * @param distance			An occurrence of a word in the mustOccurWords counts, only if it's found {@code distance} words away from another 
	 * 							listed word. Doesn't apply to the mustNotOccurClauses
	 * 
	 * @param andOr				Determines how to interpret mustOccurWords, as a conjunction of disjunctions, or vice versa
	 * @return the Query
	 * @throws IrException 
	 */
	private Query buildQuery(List<List<String>> mustOccurClauses, List<String> mustNotOccurClauses, int distance, boolean andOr)
		throws IrException
	{
		StringBuffer query = new StringBuffer();
		String innerOperator = andOr ? OR : AND;
		String outerOperator = andOr ? AND : OR;
		
		//  insert all the mustOccurWords, in an and-or or an or-and boolean construction
		if (mustOccurClauses != null && !mustOccurClauses.isEmpty())
		{
			query.append(OPEN_BRACKET);
			for (List<String> clauses : mustOccurClauses)
			{
				query.append(OPEN_BRACKET);
				if (andOr || distance == DISTANCE_DISABLED)	// write an inner (con/dis)junction, in normal "clause OP clause OP..." format
				{
					for (String clause : clauses)
						query.append(cookClause(clause) + innerOperator);
					query.delete(query.length() - innerOperator.length(), query.length());	// trim the last innerOperator
				}
				else	// write an inner conjunction as a list of words between spaces, terminated by a non-negative proximity value
				{
					query.append(QUOTE);
					for (String clause : clauses)
						query.append(clause + SPACE);
					query.append(QUOTE + PROXIMITY + distance);
				}
				query.append(CLOSE_BRACKET);	// proximity search can apply only to disjunctions of terms
				query.append(outerOperator);
			}
			query.delete(query.length() - outerOperator.length(), query.length());	// trim the last outerOperator
			query.append(CLOSE_BRACKET);
		}
		
		// append the mustNotOccurClauses
		if (mustNotOccurClauses != null && !mustNotOccurClauses.isEmpty())
		{
			query.append(PROHIBIT + OPEN_BRACKET);
			for (String clause : mustNotOccurClauses)
				query.append(cookClause(clause) + AND);
			query.delete(query.length() - AND.length(), query.length());	// trim the last AND
			query.append(CLOSE_BRACKET);
		}
		
		try
		{
			return queryParser.parse(query.toString());
		} catch (ParseException e)
		{
			throw new IrException("Error parsing the query " + query.toString(), e);
		}		
	}

	/**
	 * sanity check the clause, and add quotes if appropriate
	 * 
	 * @param clause
	 * @return
	 * @throws IrException 
	 */
	private static String cookClause(String clause) throws IrException 
	{
		if (clause == null)
			throw new IrException("read a null clause");
		if (clause.equals(""))
			throw new IrException("read an empty clause");
		
		String cookedClause;
		String[] words = clause.split(WHITE_CHAR);
		if (words.length == 1)
			cookedClause = clause;
		else
		{
			// either there's a field-specifier-prefix, followed by quotes, or neither, or there's an error
			// in other words, if there's a colon, (before the end of the word), then it's a field specifier, and the next char must open a quote, 
			//		that closes at the end of the last word
			int colonNdx = words[0].indexOf(COLON);
			if (0 < colonNdx && colonNdx < (words[0].length() - 1))
			{
				if((words[0].charAt(words[0].indexOf(COLON) + 1) != QUOTE_CHAR) || !clause.endsWith(QUOTE))
					throw new IrException("This clause opens with a field specifier but doesn't open and close a quote like it should: " + clause);
				cookedClause = clause;
			}
			else
				cookedClause = QUOTE + clause + QUOTE;	// several words, but not fielded. Surround with quotes
		}
			
		return cookedClause;
	}

	/**
	 * @param conjointClauses
	 * @param distance
	 * @throws IrException 
	 */
	private static void checkListParam(List<? extends Object> conjointClauses, int distance) throws IrException
	{
		checkListParam(conjointClauses);
		if (distance < 0)
			throw new IrException("The distance param must be non negative");
	}
	
	/**
	 * @param list
	 * @throws IrException
	 */
	private static void checkListParam(List<? extends Object> list) throws IrException
	{
		if (list == null || list.isEmpty())
			throw new IrException("Got empty or null clause list parameter");
	}
	
	private static final String OR = " OR ";
	private static final String AND = " AND ";
	private static final String PROHIBIT = " NOT ";
	private static final String QUOTE = "\"";
	private static final char QUOTE_CHAR = '\"';
	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";
	private static final String PROXIMITY = "~";
	private static final String SPACE = " ";
	private static final String WHITE_CHAR = "\\s";	//regex
	private static final String COLON = ":";
	private static final int DISTANCE_DISABLED = -1;
	
	private QueryParser queryParser;
}
