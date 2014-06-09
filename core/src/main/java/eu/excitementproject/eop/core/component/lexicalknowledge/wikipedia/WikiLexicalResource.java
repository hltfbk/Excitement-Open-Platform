package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.file.FileUtils;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceNothingToClose;


/**
 * A {@link LexicalResource} for wikipedia. It is backed by a DB table: <code>wikikb.rules_new, wikikb.rules_stats </code>and <code>wikikb.terms</code>
 * <p>
 * Wiki supports only nouns. In case the user gives a POS that is not a noun nor null, the class returns a an empty list (not null).
 * <p>
 * Each rule has a non empty list of {@link WikiExtractionType}s, with a pointer to the one with the highest 'rank', and also has a coocurrence score for 
 * its lhs and rhs.
 * <p>
 * Retrieved rules are sorted according to their rank, in a descending order.
 * 
 * @author Amnon Lotan
 *
 * @since Dec 4, 2011
 */
public class WikiLexicalResource extends LexicalResourceNothingToClose<WikiRuleInfo> {

	public static final String WIKIPEDIA_RESOURCE_NAME = "WIKIPEDIA";
	
	protected static final String NAME = "name";

	protected static final String PARAM_STOP_WORDS = "stop words";

	protected static final String PARAM_EXTRACTION_TYPES = "extraction types to keep";

	protected static final String PARAM_DB_CONN_STRING = "wikiKB DB url";

	protected static final String PARAM_COOCURRENCE_THRESHOLD = "cooccurrence threshold";

	protected Double COOCURENCE_THRESHOLD;
	protected Set<String> STOP_WORDS;
	protected final WikiLexicalResourceDBServicesThreadSafe wikiDbServices;

	///////////////////////////////////////////// PUBLIC	////////////////////////////////////////////////////////////////////////////////
	
	public WikiLexicalResource() {
		STOP_WORDS = null;
		COOCURENCE_THRESHOLD = 0.0;
		wikiDbServices = null;
	}
	
	public WikiLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		this(
				params.getFile(PARAM_STOP_WORDS),
				WikiExtractionType.parseExtractionTypeListOfStrings(params.getString(PARAM_EXTRACTION_TYPES)),
				params.getString(PARAM_DB_CONN_STRING),	null, null,
				params.getDouble(PARAM_COOCURRENCE_THRESHOLD));
	}
	
	/**
	 * Ctor
	 * @param stopWordsFile	e.g. "//qa-srv/Data/RESOURCES/stopwords-Eyal.txt"
	 * @param permittedExtractionTypes	Only rules with these extraction types will be retrieved
	 * @param dbConnectionString	e.g. "jdbc:mysql://qa-srv:3308/wikikb?user=db_readonly"
	 * @param dbUser	optional
	 * @param dbPassword	optional
	 * @param coocurrenceThreshold Only rules with a larger coocurrence score will be retrieved - used in DB queries - used to get more accurate rules, improves run time
	 * @throws LexicalResourceException
	 */
	public WikiLexicalResource(File stopWordsFile, Set<WikiExtractionType> permittedExtractionTypes, String dbConnectionString, String dbUser, String dbPassword, 
			Double coocurrenceThreshold) throws LexicalResourceException {
		if (stopWordsFile == null)
			throw new LexicalResourceException("stop words file is null");
		if (!stopWordsFile.exists())
			throw new LexicalResourceException(stopWordsFile + " doesn't exist");
		
		try {	STOP_WORDS = new LinkedHashSet<String>(FileUtils.loadFileToList(stopWordsFile));	}
		catch (IOException e) {	throw new LexicalResourceException("error reading " + stopWordsFile);	}

		if (permittedExtractionTypes == null)
			throw new LexicalResourceException("got null extraction types");
		if (permittedExtractionTypes.size() == 0)
			throw new LexicalResourceException("Got no wiki extraction types");
		
		if (dbConnectionString == null)
			throw new LexicalResourceException("got null connection string");
		
		if (coocurrenceThreshold != null && coocurrenceThreshold < 0)
			throw new LexicalResourceException("coocorrenceThreshold must be positive, or null. I got " + coocurrenceThreshold);
		this.COOCURENCE_THRESHOLD  = coocurrenceThreshold;
		
		wikiDbServices = new WikiLexicalResourceDBServicesThreadSafe(dbConnectionString, dbUser, dbPassword, COOCURENCE_THRESHOLD, permittedExtractionTypes);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexicalResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends WikiRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRulesForSide(lemma, pos, true);
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexicalResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends WikiRuleInfo>> getRulesForLeft(String lemma,	PartOfSpeech pos) throws LexicalResourceException {
		return getRulesForSide(lemma, pos, false);
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.LexicalResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends WikiRuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) 
			throws LexicalResourceException {
		
		// sanity
		
		if (leftLemma == null)	throw new LexicalResourceException("got null leftLemma");
		if (rightLemma == null)	throw new LexicalResourceException("got null rightLemma");
		//Wiki supports only nouns. pos can be null
		if(leftPos  != null && !SimplerCanonicalPosTag.NOUN.equals( simplerPos(leftPos.getCanonicalPosTag()))) 	return new Vector<LexicalRule<? extends WikiRuleInfo>>();
		if(rightPos != null && !SimplerCanonicalPosTag.NOUN.equals(simplerPos(rightPos.getCanonicalPosTag()))) 	return new Vector<LexicalRule<? extends WikiRuleInfo>>();
		
		List<LexicalRule<? extends WikiRuleInfo>> rules = wikiDbServices.getRulesFromDb(leftLemma, rightLemma);
		filterRules(rules, false);
		return rules;
	}
	
	////////////////////////////////////////////////////////////////// PRIVATE	//////////////////////////////////////////
	
	/**
	 * @param lemma
	 * @param pos
	 * @param getRuleForRight
	 * @return
	 * @throws LexicalResourceException 
	 */
	private List<LexicalRule<? extends WikiRuleInfo>> getRulesForSide(String lemma, PartOfSpeech pos, boolean getRuleForRight) throws LexicalResourceException {
		if (lemma == null)
			throw new LexicalResourceException("got null lemma");
		//Wiki supports only nouns. pos can be null
		if(pos != null && !SimplerCanonicalPosTag.NOUN.equals(simplerPos(pos.getCanonicalPosTag())))
			return new Vector<LexicalRule<? extends WikiRuleInfo>>();
		//		avoid rules with a stop word as one of their sides
		if (STOP_WORDS.contains(lemma))
			return new Vector<LexicalRule<? extends WikiRuleInfo>>();

		List<LexicalRule<? extends WikiRuleInfo>> rules = wikiDbServices.getRulesForSideImpl(lemma, getRuleForRight);
		filterRules(rules, getRuleForRight);
		return rules;
	}

	/**
	 * @param rules
	 * @param getRuleForRight 
	 */
	private void filterRules(List<LexicalRule<? extends WikiRuleInfo>> rules, boolean getRuleForRight) 
	{
		for (Iterator<LexicalRule<? extends WikiRuleInfo>> ruleIterator = rules.iterator(); ruleIterator.hasNext(); )
			if (filterRule(ruleIterator.next(), getRuleForRight))
				ruleIterator.remove();
	}
	
	/**
	 * Accept or reject a rule
	 * @param lexicalRule 
	 * @param getRuleForRight 
	 * @param lemmaOnTheRight 
	 * @param lhsLemma 
	 * @param rhsLemma 
	 * @param coocurenceScore 
	 * @param extractionTypes 
	 * @return
	 */
	private boolean filterRule(LexicalRule<? extends WikiRuleInfo> rule, boolean getRuleForRight) {
	  if (rule != null) {
		String lhsLemma = rule.getLLemma();
		String rhsLemma = rule.getRLemma();
		double coocurenceScore = rule.getInfo().getCoocurenceScore();
		ImmutableSet<WikiExtractionType> extractionTypes = rule.getInfo().getExtractionTypes();
		
		return  (
				//	reject rules with no valid extraction types
				(extractionTypes.isEmpty())	|| 

				//	avoid rules with a stop word as one of their sides. The source lemma is supposed to be checked earlier on.
				(getRuleForRight ? STOP_WORDS.contains(lhsLemma) : STOP_WORDS.contains(rhsLemma)) ||

				//	avoid rules whose RHS is part of the LHS (e.g. French revolution --> revolution)
				leftWordsContainsRightWords(lhsLemma, rhsLemma)	||
				
				//	filter rules whose co-occurrence score is below a threshold
				(COOCURENCE_THRESHOLD != null && coocurenceScore < COOCURENCE_THRESHOLD)	||
				
				//	Eyal 3.2.10 - reject rules entailing "name", cos it's regarded as a "transparent head" 
				(!getRuleForRight && rhsLemma.equals(NAME))
			);
	  }
	  return false;
	}
	
	/**
	 * check if all the words on the right are contained in the left words set. 
	 * @param leftExpression
	 * @param rightExpression
	 * @return
	 */
	private boolean leftWordsContainsRightWords(String leftExpression, String rightExpression) 
	{
		String[] rightWords = smartSplit(rightExpression); 
		String[] leftWords = smartSplit(leftExpression);
		boolean match = true;
		for (int j = 0; j < rightWords.length && match; j++) 
		{
			match = false;
			for (int i = 0; i < leftWords.length && !match ; i++) 
				if (leftWords[i].equalsIgnoreCase(rightWords[j])) 
					match = true;
		}
		return match;
	}
	
	private String[] smartSplit(String wikiTitle) {
		wikiTitle = wikiTitle.replace("(", "");
		wikiTitle = wikiTitle.replace(")", "");
		String[] words = wikiTitle.split("\\s");
		return words;
	}
}

