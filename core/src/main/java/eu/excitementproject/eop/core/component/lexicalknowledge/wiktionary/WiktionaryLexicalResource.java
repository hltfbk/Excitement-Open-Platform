/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wiktionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceNothingToClose;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryEntry;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl.JwktlEntry;

/**
 * <b>ERROR:</b> right now there is a slight defect, that makes the {@link #getRulesForLeft(String, PartOfSpeech)} and {@link #getRulesForRight(String, PartOfSpeech)}
 * return different rules that are effectively equal, except for one containing a certain {@link WktSense}, and the other containing that sense's {@link JwktlEntry}.
 * For some reason the {@link LexicalRule}{@link #equals(Object)} correctly returns true for two such rules, but the {@link Set} the two are in does not 
 * filter them.
 * <p>
 * 
 * 
 * A {@link LexicalResource} that extracts rules out of Wiktionary, the community-based dictionary. In wiktionary, <lemma, pos> tuples have 
 * distinct senses, and each sense can point at another tuple, not to another sense.
 * <p>
 * Note that Wiktionary links <lemma, POS> terms, or senses of terms, to lemmas. That means that, in opposed to {@link WordnetLexicalResource},
 * most rules obtained through this class ignore either their left or right sense number parameter, since that side of the rule is a bare lemma, 
 * with no determined sense, nor even a determined POS. The 'queried' side's POS is naively assumed to be equal to the queried side's POS. 
 * This also means that the rules you get back predominately lack either their right {@link WktSense} or left {@link WktSense}. Only
 * in relatively few cases of using a {@link #getRules()} method, where Wiktionary happens to hold the same relationship  
 * between the two queried terms in both directions, will you get rules with {@link WktSense}s for both sides of the rule.    
 * @author Amnon Lotan
 * @since 22/06/2011
 * @see http://en.wiktionary.org/wiki/Wiktionary:Main_Page
 * 
 */
public class WiktionaryLexicalResource extends LexicalResourceNothingToClose<WiktionaryRuleInfo> {
	
	/**
	 * entering this value as a sense number will yield results for all relevant senses
	 */
	public static final int ALL_SENSES = -1;
	public static final int NO_SENSES = -2;
	
	private static final String RESOURCE_NAME = "WIKTIONARY";
	private static final String PARAM_WKT_DIRECTORY = "wiktionaryDir";
	private static final String PARAM_POS_TAGGER_MODEL_FILE = "posTaggerModelFile";
	private static final String PARAM_LEFT_SENSE = "leftSense";
	private static final String PARAM_RIGHT_SENSE = "rightSense";
	private static final String PARAM_USE_ENTRY_INFO = "useEntryInfo";
	private static final String PARAM_RELATIONS = "relations";

	
	/////////////////////////////////////////////// CONSTRUCTORS ////////////////////////////////////////////////////////////////////
	
	public WiktionaryLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		this(
				params.getString(PARAM_WKT_DIRECTORY), params.getString(PARAM_POS_TAGGER_MODEL_FILE), 
				params.getInt(PARAM_LEFT_SENSE),
				params.getInt(PARAM_RIGHT_SENSE), 
				params.getBoolean(PARAM_USE_ENTRY_INFO), 
				params.getEnumSet(WiktionaryRelation.class, PARAM_RELATIONS));
	}
	
	/**
	 * Ctor
	 * @param wiktionaryDir e.g. "\\\\nlp-srv/data/RESOURCES/Wiktionary/parse"
	 * @param posTaggerModelFile e.g. "jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger"
	 * @throws LexicalResourceException
	 */
	public WiktionaryLexicalResource(String wiktionaryDir, String posTaggerModelFile) throws LexicalResourceException 
	{
		this(wiktionaryDir, posTaggerModelFile, null, null, true, null);
	}
	
	/**
	 * Ctor
	 * @param wiktionaryDir e.g. "\\\\nlp-srv/data/RESOURCES/Wiktionary/parse"
	 * @param Relations The relations set used to filter the retrieved rules. may be null (to leave undefined) but not empty
	 * @param posTaggerModelFile e.g. "jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger"
	 * @throws LexicalResourceException
	 */
	public WiktionaryLexicalResource(String wiktionaryDir, String posTaggerModelFile, Set<WiktionaryRelation> Relations) throws LexicalResourceException 
	{
		this(wiktionaryDir, posTaggerModelFile, null, null, true, Relations);
	}
	
	/**
	 * Ctor
	 * @param wiktionaryDir e.g. "\\\\nlp-srv/data/RESOURCES/Wiktionary/parse"
	 * @param LeftSense must be either non-negative or null (for undefined)
	 * @param RightSense must be either non-negative or null (for undefined)
	 * @param posTaggerModelFile e.g. "jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger"
	 * @throws LexicalResourceException
	 */
	public WiktionaryLexicalResource(String wiktionaryDir,  String posTaggerModelFile, int LeftSense, int RightSense) throws LexicalResourceException 
	{
		this(wiktionaryDir, posTaggerModelFile, LeftSense, RightSense, true,  null);
	}
	
	/**
	 * Ctor
	 * @param wiktionaryDir e.g. "\\\\nlp-srv/data/RESOURCES/Wiktionary/parse"
	 * @param posTaggerModelFile e.g. "jars/stanford-postagger-2008-09-28/bidirectional-wsj-0-18.tagger"
	 * @param leftSense must be either non-negative or null (for undefined)
	 * @param rightSense must be either non-negative or null (for undefined)
	 * @param useEntryInfo	says whether or not to add the {@link WiktionaryEntry}'s data when querying about any of its senses
	 * @param relations The relations set used to filter the retrieved rules. may be null (to leave undefined) but not empty
	 * @throws LexicalResourceException
	 */
	public WiktionaryLexicalResource(String wiktionaryDir, String posTaggerModelFile, Integer leftSense, Integer rightSense,
			boolean useEntryInfo, Set<WiktionaryRelation> relations) throws LexicalResourceException 
	{
		this.wktServices = new WiktionaryLexicalResourceServices(wiktionaryDir, posTaggerModelFile, RESOURCE_NAME);
		if (leftSense != null )			setLeftSense(leftSense);
		if (rightSense != null )		setRightSense(rightSense);
		setUseEntryInfo(useEntryInfo);
		if (relations != null)		setRelationSet(relations);
		
	}
	
	///////////////////////////////////////////// PUBLIC ////////////////////////////////////////////////////////////////////
	
	public List<LexicalRule<? extends WiktionaryRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		if (rightSense == null) throw new LexicalResourceException("No right sense set. You must call setRSense() first");
		if (relations == null) throw new LexicalResourceException("No relations set set. You must call setRelationSet() first");
		return wktServices.getRulesForSide(lemma, pos, rightSense, relations, useEntryData, true);
	}
	
	public List<LexicalRule<? extends WiktionaryRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		if (leftSense == null) throw new LexicalResourceException("No left sense set. You must call setLSense() first");
		if (relations == null) throw new LexicalResourceException("No relations set set. You must call setRelationSet() first");
		return wktServices.getRulesForSide(lemma, pos, leftSense, relations, useEntryData, false);
	}

	public List<LexicalRule<? extends WiktionaryRuleInfo>> getRules(String leftLemma, PartOfSpeech lPos, String rightLemma, PartOfSpeech rPos)
			throws LexicalResourceException {
		if (lPos != null && rPos != null && !lPos.equals(rPos))			
			throw new LexicalResourceException("Both POSs in a Wiktionary rule must be equal or null (even if it's a derivation realation). I got "+lPos+" and "+rPos);
		return getRules(leftLemma, lPos, rightLemma);
	}
	
	/**
	 * Retrieves rules matching the given set of {leftLemma, pos, rightLemma, pos}, with the preset relations set, 
	 * along with either the preset-left-sense-number on the left, or the preset right-sense-number on the right. <br>
	 * This is a convenience method that differs from {@link #getRules(String, PartOfSpeech, String, PartOfSpeech)} by specifying the bilateral 
	 * POS only once. <br>
	 * In case the user gives null POS, retrieve rules for all possible POSs.<br>
	 * If no rules are matched against the above criteria, an empty list is returned.
	 * @param leftLemma
	 * @param pos
	 * @param rightLemma
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends WiktionaryRuleInfo>> getRules(String leftLemma, PartOfSpeech pos, String rightLemma)
			throws LexicalResourceException {
		if (relations == null) 	throw new LexicalResourceException("No relations set set. You must call setRelationSet() first");
		if (leftSense == null) throw new LexicalResourceException("No left sense set. You must call setLSense() first");
		if (rightSense == null) throw new LexicalResourceException("No right sense set. You must call setRSense() first");
		
		List<LexicalRule<? extends WiktionaryRuleInfo>> rules = new Vector<LexicalRule<? extends WiktionaryRuleInfo>>();
		rules.addAll(wktServices.getRulesWithOneSidedSense(leftLemma, pos, rightLemma, leftSense, useEntryData, relations, false));
		rules.addAll(wktServices.getRulesWithOneSidedSense(leftLemma, pos, rightLemma, rightSense, useEntryData, relations, true));
		rules = WiktionaryLexicalResourceServices.consolidateWktRules(rules);
		return rules;
	}

	/**
	 * Returns the rule matching this specific pair of lemmas, and the sense number of the left lemma+pos. <br>
	 * In case the user gives null POS, retrieve rules for all possible POSs.
	 * @param leftLemma
	 * @param pos
	 * @param rightLemma
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends WiktionaryRuleInfo>> getRulesWithSenseOnTheLeft(String leftLemma, PartOfSpeech pos, 
			String rightLemma) throws LexicalResourceException 
	{
		if (relations == null) throw new LexicalResourceException("No relations set set. You must call setRelationSet() first");
		return wktServices.getRulesWithOneSidedSense(leftLemma, pos, rightLemma, leftSense, useEntryData, relations, false);
	}
	
	/**
	 * Returns the rule matching this specific pair of lemmas, and the sense number of the left lemma+pos. <br>
	 * In case the user gives null POS, retrieve rules for all possible POSs.
	 * @param leftLemma
	 * @param pos
	 * @param rightLemma
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends WiktionaryRuleInfo>> getRulesWithSenseOnTheRight(String leftLemma, PartOfSpeech pos, String rightLemma
			) throws LexicalResourceException 
	{
		if (relations == null) throw new LexicalResourceException("No relations set set. You must call setRelationSet() first");
		return wktServices.getRulesWithOneSidedSense(leftLemma, pos, rightLemma, rightSense, useEntryData, relations, true);
	}
	
	/**
	 * set the new  left sense, to be used in all queries. 1-based index into the <lemma, pos> term's synsets
	 * @param newLeftSense the new  left sense, to be used in all queries. 1-based index into the <lemma, pos> term's synsets
	 * @throws LexicalResourceException
	 */
	public void setLeftSense(int newLeftSense) throws LexicalResourceException {
		setSenseNum(newLeftSense, false);
	}

	/**
	 * set the new  right sense, to be used in all queries. 1-based index into the <lemma, pos> term's synsets
	 * @param newSense the new  left sense, to be used in all queries. 1-based index into the <lemma, pos> term's synsets
	 * @param newRightSense
	 * @throws LexicalResourceException 
	 */
	public void setRightSense(int newRightSense) throws LexicalResourceException {
		setSenseNum(newRightSense, true);
	}
	
	/**
	 * set the new  set of relations, to be used in all queries that don't specify it explicitly
	 * @param newRelations the new  set of relations, to be used in all queries that don't specify it explicitly
	 * @throws LexicalResourceException
	 */
	public void setRelationSet(Set<WiktionaryRelation> newRelations) throws LexicalResourceException {
		if (newRelations == null)
			throw new LexicalResourceException("Got null relation set");
		if (newRelations.isEmpty())
			throw new LexicalResourceException("The relations set cannot be empty");
		this.relations = new HashSet<WiktionaryRelation>(newRelations);
	}
	
	/**
	 * @param useEntryInfo
	 * @throws LexicalResourceException 
	 */
	public void setUseEntryInfo(boolean useEntryInfo) throws LexicalResourceException {
		if (leftSense  != null && leftSense == NO_SENSES)
			throw new LexicalResourceException("You cannot set useEntryInfo to false when LeftSense is set to NO_SENSES" );
		if (rightSense  != null && rightSense == NO_SENSES)
			throw new LexicalResourceException("You cannot set useEntryInfo to false when RightSense is set to NO_SENSES" );
		this.useEntryData = useEntryInfo;
	}

	/////////////////////////////////////////////// PROTECTED ////////////////////////////////////////////////////////////////////
	
	protected void setSenseNum(int newSenseNum, boolean isRight) throws LexicalResourceException
	{
		if (newSenseNum == NO_SENSES && !this.useEntryData)
			throw new LexicalResourceException("You cannot set the sense number to NO_SENSES when useEntryData is set to false");
		if (newSenseNum == ALL_SENSES || newSenseNum == NO_SENSES)
			;
		else
		{
			if (newSenseNum < 1) throw new LexicalResourceException("A sense number must be positive, or one of the static constants. I got " + newSenseNum);
			newSenseNum = newSenseNum - 1;	// shift to 0-based indices
		}
		if (isRight)
			this.rightSense = newSenseNum;
		else
			this.leftSense = newSenseNum;
	}
	
	
	protected WiktionaryLexicalResourceServices wktServices;
	protected Integer leftSense = null;
	protected Integer rightSense = null;
	/**
	 * iff true, the queries will also add rules based on entry level data, not just sense level data 
	 */
	private boolean useEntryData = true;
	protected Set<WiktionaryRelation> relations = null;
}

