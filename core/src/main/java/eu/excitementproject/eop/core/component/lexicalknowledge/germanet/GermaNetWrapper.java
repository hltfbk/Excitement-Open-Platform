package eu.excitementproject.eop.core.component.lexicalknowledge.germanet;

// Component imports
import eu.excitementproject.eop.common.component.Component;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceWithRelation;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.TERuleRelation;
import eu.excitementproject.eop.common.configuration.CommonConfig;
//import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
//import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

// LexicalResource imports

// GermaNet imports
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;
import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.LexRel;

//import java.util.Iterator;
// other imports
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

//import com.aliasi.util.Iterators.Array;

/**
 * This class implements a German Lexical Resource based on GermaNet 8.0, which is 
 * the German WordNet. The implementation accesses GermaNet via GermaNet API.
 * (It uses GermaNet API each time it is being called upon).
 * 
 * <P>
 * The implementation supports both LexicalResource and LexicalResourceWithRelation. 
 * For the relation, it supports both OwnRelationSpecifier (with GermaNetRelation) and CanonicalRelationSpecifier.
 * 
 * <P> It has a few configurable values. Basically, it needs path to GermaNet data itself, 
 * and a set of double values that indicates "confidence" for each own relation when they are 
 * treated as "entailment". See the main constructor for the detailed parameter info.
 *  
 * <P>
 * Note that EXCITEMENT project cannot and do not redistribute GermaNet, and the
 * user of this component must get it with a proper license agreement from Tuebingen 
 * University. If the GermaNet is not found, the component will raise an exception and
 * will not be initialized. 
 * 
 * @author Jan Pawellek, Britta Zeller, Julia Kreutzer
 * @since Nov 2012 
 */

public class GermaNetWrapper implements Component, LexicalResourceWithRelation<GermaNetInfo, GermaNetRelation> {
	/** relations indicating (non-/)entailment */
	private final GermaNetRelation[] REL_E = {GermaNetRelation.causes, GermaNetRelation.entails, GermaNetRelation.has_hypernym, GermaNetRelation.has_hyponym, GermaNetRelation.has_synonym };
	private final GermaNetRelation[] REL_NE = {GermaNetRelation.has_antonym};
	/** relations indicating entailment for rule direction "right" or "left" */
	private final GermaNetRelation[] REL_E_LEFT = {GermaNetRelation.has_synonym, GermaNetRelation.has_hypernym, GermaNetRelation.causes, GermaNetRelation.entails };
	private final GermaNetRelation[] REL_E_RIGHT = {GermaNetRelation.has_hyponym, GermaNetRelation.has_synonym};

	/** per-relation output confidences */
	private final Map<Enum<?>, Double> CONFIDENCES = new HashMap<Enum<?>, Double>();
	
	private GermaNet germanet;
	
	/**
	 * Creates a new GermaNetWrapper instance, and initializes the instance
	 * (basically loads GermaNet files into memory).
	 * 
	 * @param config		Configuration for the GermaNetWrapper instance
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public GermaNetWrapper(CommonConfig config) throws ConfigurationException, ComponentException {
		this(config.getSection("GermaNetWrapper").getString("germaNetFilesPath"),
				config.getSection("GermaNetWrapper").getDouble("causesConfidence"),
				config.getSection("GermaNetWrapper").getDouble("entailsConfidence"),
				config.getSection("GermaNetWrapper").getDouble("hypernymConfidence"),
				config.getSection("GermaNetWrapper").getDouble("hyponymConfidence"),
				config.getSection("GermaNetWrapper").getDouble("synonymConfidence"),
				config.getSection("GermaNetWrapper").getDouble("antonymConfidence"));
	}
	
	/**
	 * Creates a new GermaNetWrapper instance, and initializes the instance
	 * (basically loads GermaNet files into memory).
	 * Sets the default value of LexicalRule (0.5) as confidence value for all relations.
	 * 
	 * @param germaNetFilesPath			Path to GermaNet XML files
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public GermaNetWrapper(String germaNetFilesPath) throws ConfigurationException, ComponentException {
		this(germaNetFilesPath, LexicalRule.DEFAULT_CONFIDENCE, LexicalRule.DEFAULT_CONFIDENCE, 
				LexicalRule.DEFAULT_CONFIDENCE, LexicalRule.DEFAULT_CONFIDENCE, LexicalRule.DEFAULT_CONFIDENCE, LexicalRule.DEFAULT_CONFIDENCE);
	}
	
	/**
	 * Creates a new GermaNetWrapper instance, and initializes the instance
	 * (basically loads GermaNet files into memory).
	 * 
	 * @param germaNetFilesPath		Path to GermaNet XML files
	 * @param causesConfidence		Confidence to be set for "causes" relations
	 * @param entailsConfidence		Confidence to be set for "entails" relations
	 * @param hypernymConfidence	Confidence to be set for "hypernym" relations
	 * @param synonymConfidence		Confidence to be set for "synonym" relations
	 * @param hyponymConfidence     Confidence to be set for "hyponym" relations
	 * @param antonymConfidence     Confidence to be set for "antonym" relations
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public GermaNetWrapper(String germaNetFilesPath, Double causesConfidence, Double entailsConfidence,
			Double hypernymConfidence, Double synonymConfidence, Double hyponymConfidence , Double antonymConfidence)  
			throws ConfigurationException, ComponentException {
		
		try {
			this.germanet = new GermaNet(germaNetFilesPath);
		}
		catch (java.io.FileNotFoundException e) {
			throw new GermaNetNotInstalledException("Path to GermaNet is not set correctly.", e);
		}
		catch (java.lang.Exception e) {
			throw new ComponentException("Cannot initialize GermaNet.", e);
		}
				
		// the Double values can be null, if they are from CommonConfig XML files. 
		if (causesConfidence == null)
			CONFIDENCES.put(ConRel.causes, LexicalRule.DEFAULT_CONFIDENCE);
		else
			CONFIDENCES.put(ConRel.causes, causesConfidence);
		
		if (entailsConfidence == null)
			CONFIDENCES.put(ConRel.entails, LexicalRule.DEFAULT_CONFIDENCE);
		else
			CONFIDENCES.put(ConRel.entails, entailsConfidence);
		
		if (hypernymConfidence == null)
			CONFIDENCES.put(ConRel.has_hypernym, LexicalRule.DEFAULT_CONFIDENCE);
		else
			CONFIDENCES.put(ConRel.has_hypernym, hypernymConfidence);
		
		if (hyponymConfidence == null)
			CONFIDENCES.put(ConRel.has_hyponym, LexicalRule.DEFAULT_CONFIDENCE);
		else
			CONFIDENCES.put(ConRel.has_hyponym, hyponymConfidence);
		
		if (synonymConfidence == null)
			CONFIDENCES.put(LexRel.has_synonym, LexicalRule.DEFAULT_CONFIDENCE);
		else
			CONFIDENCES.put(LexRel.has_synonym, synonymConfidence);	
		
		if (antonymConfidence == null)			
			CONFIDENCES.put(LexRel.has_antonym, LexicalRule.DEFAULT_CONFIDENCE);
		else
			CONFIDENCES.put(LexRel.has_antonym, antonymConfidence);
	}
	
	/**
	 * This method provides the (human-readable) name of the component. It is used to 
	 * identify the relevant section in the common configuration for the current component. 
	 * See Spec Section 5.1.2, Overview of the common configuration  and Section 4.9.3, 
	 * Component name and instance name.
	 */
	public String getComponentName()
	{
		return "GermaNetWrapper"; 
	}
	
	
	/** This method provides the (human-readable) name of the instance. It is used to 
	 * identify the relevant subsection in the common configuration for the current component. 
	 * See Spec Section 5.1.2, Overview of the common configuration  and Section 4.9.3, 
	 * Component name and instance name. Note that this method can return null value, if 
	 * and only if all instances of the component shares the same configuration.
	 */
	public String getInstanceName() {
		return null; 
        }

	@Override
	public void close() throws LexicalResourceCloseException
	{
	}

	/**
	 * checks whether the given PartOfSpeech is valid
	 * 
	 * @param pos
	 * @return true: valid, false: not valid
	 */
	private boolean isValidPos(PartOfSpeech pos) {
		
		switch(pos.getCanonicalPosTag()) {
			case ADJ:	
			case N:
			case NN:
			case V:
			case NP: 	// Named Entities
				return true; 
			default:
				return false; 
		}
	}
	
	/**
	 * Maps EOP PosTags onto the GermaNet equivalent
	 * 
	 * @param pos given POS tag (EOP)
	 * @return WordCategory which is equivalent
	 * @throws LexicalResourceException if POS is not compatible
	 */
	private WordCategory posToWordCategory(PartOfSpeech pos) throws LexicalResourceException {
		switch (pos.getCanonicalPosTag()) {
			case ADJ:
				return WordCategory.adj;
			case N:
			case NN:
				return WordCategory.nomen;
			case NP:		// Named Entities
				return WordCategory.nomen;
			case V:
				return WordCategory.verben;
			default:
				throw new LexicalResourceException("Integrity failure; non-compatible POS shouldn't be passed to this point. "); 
		} 
	}
	
	/**
	 * Maps a GermaNet WordCategory onto the equivalent EOP POS tag
	 * 
	 * @param wc GermaNet WordCategory, e.g. "adj" or "nomen"
	 * @return equivalent GermanPartOfSpeech, or "OTHER" if no matching POS tag is found
	 */
	private GermanPartOfSpeech wordCategoryToPos(WordCategory wc) {
		try {
			switch (wc) {
				case adj:
					return new GermanPartOfSpeech("ADJ");
				case nomen: 
					return new GermanPartOfSpeech("N");
				case verben:
					return new GermanPartOfSpeech("V");
				default:
					return new GermanPartOfSpeech("OTHER");
			}
		}
		catch (UnsupportedPosTagStringException e) {
			return null;
		}
	}

	
	/**
	 * This method will return LexicalRules for a given LHS lemma, pos and GermaNetRelation.
	 * LHS and RHS of the resulting rules are connected by the given relation.
	 * The method can be called with any GermaNetRelation, also indicating non-entailment.
	 * Note that relations that cannot be followed in GermaNet from LHS to RHS will lead to empty result lists.
	 * This is the basic getRulesForLeft method that all other getRules/getRulesForLeft methods call.
	 * 
	 * @param lemma Lemma to be matched on LHS
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @param relation The GermaNet relation of the rule (from LHS to RHS, e.g. GermaNetRelation.has_hypernym))
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForLeft(
			String lemma, PartOfSpeech pos, GermaNetRelation relation)
			throws LexicalResourceException {
		
		// using a set makes the result unique
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
			
		// check POS is valid or not for GermaNet. Note that GermaNet only has noun, verb, and adjective. -> if invalid, return empty list
		if ( pos != null && !isValidPos(pos))	{ return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result); } 
	
		// check relation is valid or not for this type of rule -> if not, return empty list
		if ( relation != null && relation != GermaNetRelation.has_antonym && !Arrays.toString(REL_E_LEFT).contains(relation.toGermaNetString())) {		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result); 	} 
		
		// fetch synsets for lemma
		List<Synset> syns = pos == null ? germanet.getSynsets(lemma) : germanet.getSynsets(lemma, posToWordCategory(pos));
		
		// if pos not specified, set to "other"
		if (pos == null) {
			try {
				pos = new GermanPartOfSpeech("OTHER");
			}
			catch (UnsupportedPosTagStringException e) {}
		}
		
		// follow given GN relation
		for (Synset syn: syns) {
			// if conceptual relation
			if (ConRel.isConRel(relation.toGermaNetString())){
				Set<LexicalRule<? extends GermaNetInfo>> a = collectConceptualRules("left",lemma, pos, syn, relation);
				result.addAll(collectConceptualRules("left",lemma, pos, syn, relation));	
				if (relation == GermaNetRelation.entails) {
					for (LexicalRule<? extends GermaNetInfo> la : a){
						System.out.println(la.toString());
					}
				}
			}

			// if lexical relation
			else if (LexRel.isRel(relation.toGermaNetString())) {
				try {
					result.addAll(collectLexicalRules("left", lemma, pos, syn, relation));
				} catch (UnsupportedPosTagStringException e) {}
			}
		}
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}

	/** 	 
	 * This method will return LexicalRules for a given RHS lemma, pos and GermaNetRelation.
	 * LHS and RHS of the resulting rules are connected by the given relation.
	 * The method can be called with any GermaNetRelation, also indicating non-entailment.
	 * Note that relations that cannot be followed in GermaNet from RHS to LHS will lead to empty result lists.
	 * This is the basic getRulesForRight method that all other getRulesForRight methods call.
	 * 
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @param relation The GermaNet relation of the rule (from RHS to LHS, e.g. GermaNetRelation.has_hyponym)
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */	
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForRight(
			String lemma, PartOfSpeech pos, GermaNetRelation relation)
			throws LexicalResourceException {

		// using a set makes the result unique
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
		
		// check POS is valid or not for GermaNet. Note that GermaNet only has noun, verb, and adjective. -> if invalid, return empty list
		if ( pos != null && !isValidPos(pos))	{ return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result); } 
	
		// check relation is valid or not for this type of rule -> if not, return empty list
		if ( relation != null && relation != GermaNetRelation.has_antonym && !Arrays.toString(REL_E_RIGHT).contains(relation.toGermaNetString()))	{ return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result); } 
		
		// fetch synsets for lemma
		List<Synset> syns = pos == null ? germanet.getSynsets(lemma) : germanet.getSynsets(lemma, posToWordCategory(pos));
		
		// if pos not specified, set to "other"
		if (pos == null) {
			try {
				pos = new GermanPartOfSpeech("OTHER");
			}
			catch (UnsupportedPosTagStringException e) {}
		}
		
		// follow given GN relation
		for (Synset syn: syns) {
			// if conceptual relation
			if (ConRel.isConRel(relation.toGermaNetString())){
				result.addAll(collectConceptualRules("right", lemma, pos, syn, relation));
			}

			// if lexical relation
			if (LexRel.isRel(relation.toGermaNetString())) {
				try {
					result.addAll(collectLexicalRules("right", lemma, pos, syn, relation));
				} catch (UnsupportedPosTagStringException e) {}
			}
		}
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}
	
	/**
	 * This method returns the list of LexicalRules that match with the given LHS lemma, pos and canonical relation (NonEntailment or Entailment).
	 * For NonEntailment it collects rules for all GermaNetRelations indicating non-entailment.
	 * For Entailment it collects rules for all GermaNetRelations indicating entailment.
	 * 
	 * @param lemma lemma to be matched on the rule's LHS
	 * @param pos POS of the LHS lemma
	 * @param relation canonical relation that defines whether NonEntailment or Entailment rules are extracted
	 * @return list of lexical rules
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForLeft(	
			String lemma, PartOfSpeech pos, TERuleRelation relation)	
			throws LexicalResourceException {
		
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
		
		for (GermaNetRelation GNrel : relation == TERuleRelation.Entailment ? REL_E : REL_NE){
			result.addAll(getRulesForLeft(lemma, pos, GNrel));
			}
		
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}
	
	/**
	 * This method returns the list of LexicalRules that match with the given RHS lemma, pos and canonical relation (NonEntailment or Entailment).
	 * For NonEntailment it collects rules for all GermaNetRelations indicating non-entailment.
	 * For Entailment it collects rules for all GermaNetRelations indicating entailment.
	 * 
	 * @param lemma lemma to be matched on the rule's RHS
	 * @param pos POS of the RHS lemma
	 * @param relation canonical relation that defines whether NonEntailment or Entailment rules are extracted
	 * @return list of lexical rules
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForRight(	
			String lemma, PartOfSpeech pos, TERuleRelation relation)	
			throws LexicalResourceException {
		
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
		
		for (GermaNetRelation GNrel : relation == TERuleRelation.Entailment ? REL_E : REL_NE){
			result.addAll(getRulesForRight(lemma, pos, GNrel));
			}
		
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}
	
	/**
	 * Returns a list of lexical rules whose left side (the head of the lexical relation) matches the given lemma and POS. 
	 * An empty list means that no rules were matched. 
	 * If the user gives null POS, the class will retrieve rules for all possible POSes.
	 * Rules for all valid GermaNetRelations indicating Entailment from LHS to RHS will be collected.
	 * 
	 * @param lemma Lemma to be matched on LHS. 
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForLeft(
			String lemma, PartOfSpeech pos) throws LexicalResourceException {

		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
	
		for (GermaNetRelation rel : REL_E_LEFT){
			result.addAll(this.getRulesForLeft(lemma, pos, rel));
		}
		
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}

	/** 
	 * Returns a list of lexical rules whose right side (the target of the lexical relation) matches the given lemma and POS. 
	 * An empty list means that no rules were matched. 
	 * If the user gives null POS, the class will retrieve rules for all possible POSes.
	 * Rules for all valid GermaNetRelations indicating Entailment from RHS to LHS will be collected.	 
	 *  
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForRight(
			String lemma, PartOfSpeech pos) throws LexicalResourceException {

		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();

		for (GermaNetRelation rel : REL_E_RIGHT){
			result.addAll(getRulesForRight(lemma, pos, rel));
		}
			
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}

	/** 
	 * This method returns a list of lexical rules whose left and right sides match the two given pairs of lemma and POS.
	 * Rules for all valid GermaNetRelations indicating Entailment for the given pair will be collected.	 
	 * 
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRules(String leftLemma,
			PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos)
			throws LexicalResourceException {
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
		List<LexicalRule<? extends GermaNetInfo>> rules = getRulesForLeft(leftLemma, leftPos);
		
		for (LexicalRule<? extends GermaNetInfo> rule : rules) {
			
			// in case of "NN" as POS input: match this also to "N" outputs from GermaNet
			if (!(rightPos == null || rule.getRPos() == null)) {
				if (rightPos.toString().equals("NN") && rule.getRPos().toString().equals("N")) {
					try {
						rightPos = new GermanPartOfSpeech("N");
					} catch (UnsupportedPosTagStringException e) {
						e.printStackTrace();
					}
				}
			}
			// accept only results where the GermaNet output lemma and POS correspond to input
			if (rule.getRLemma().equals(rightLemma) && (rightPos == null || rule.getRPos().equals(rightPos))) {
				result.add(rule);
			}
		}
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}
	
	/** 
	 * This method returns a list of lexical rules whose left and right sides match the two given pairs of lemma and POS and the given GermaNetRelation.
	 * 
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @param relation The GermaNet relation of the rule (from LHS to RHS, e.g. GermaNetRelation.has_hyponym)
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRules(String leftLemma,
			PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos,
			GermaNetRelation relation) throws LexicalResourceException {
		
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
		List<LexicalRule<? extends GermaNetInfo>> rules = this.getRulesForLeft(leftLemma, leftPos, relation);
		
		for (LexicalRule<? extends GermaNetInfo> rule : rules) {
			
			// in case of "NN" as POS input: match this also to "N" outputs from GermaNet
			if (!(rightPos == null || rule.getRPos() == null)) {
				if (rightPos.toString().equals("NN") && rule.getRPos().toString().equals("N")) {
					try {
						rightPos = new GermanPartOfSpeech("N");
					} catch (UnsupportedPosTagStringException e) {
						e.printStackTrace();
					}
				}
			}
			// accept only results where the GermaNet output lemma and POS correspond to input
			if (rule.getRLemma().equals(rightLemma) && (rightPos == null || rule.getRPos().equals(rightPos))) {
				result.add(rule);
			}
		}
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}
	
	/** 
	 * This method returns a list of lexical rules whose left and right sides match the two given pairs of lemma and POS,
	 * for a given canonical relation (Entailment or NonEntailment).
	 * 
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @param relation TERuleRelation that defines the relation between LHS and RHS: Entailment or NonEntailment
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRules(	
			String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, TERuleRelation relation)	
			throws LexicalResourceException {
		
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
		
		for (GermaNetRelation GNrel : relation == TERuleRelation.Entailment ? REL_E : REL_NE){ //get rules for all (Non-/)Entailment relations
			result.addAll(getRules(leftLemma, leftPos, rightLemma, rightPos, GNrel));
			}
		
		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}
	
	/**
	 * Collect conceptual related synsets and the resulting rules for a given pos, synset and a conceptual relation.
	 * 
	 * @param direction "left" or "right" depending on function (getRulesForRight/Left) where method was called
	 * @param lemma left lemma of all the rules
	 * @param pos PartOfSpeech of the given synset 
	 * @param syn synset the relations and rules are extracted from
	 * @param conrel GermaNetRelation defining the relation of the extracted rules
	 * @return a set of lexical rules
	 * @throws LexicalResourceException 
	 */
	private Set<LexicalRule<? extends GermaNetInfo>> collectConceptualRules(	
			String direction, String lemma, PartOfSpeech pos, Synset syn, GermaNetRelation conrel) 	
			throws LexicalResourceException{	
		
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
				
		// if relation is not transitive, e.g. for entails and causes
		if (!conrel.toConRel().isTransitive()){
			List<Synset> syns = syn.getRelatedSynsets(conrel.toConRel());
			for (Synset rightsyn : syns){
				for (LexUnit lu : rightsyn.getLexUnits()){
					GermanPartOfSpeech luPos = wordCategoryToPos(lu.getWordCategory());	
					// set pos of lexical unit to "NP" if named entity
					if (lu.isNamedEntity()){
						try {
							luPos = new GermanPartOfSpeech("NP");
							}
						catch (UnsupportedPosTagStringException e) {}
						}
					// iterate over all orthographic forms of a lexical unit
					for (String rightorth : rightsyn.getAllOrthForms()) {
						
						// create new lexical rule and add it to the results
						LexicalRule<? extends GermaNetInfo> lexrule = null;
						if (direction.equals("right")){ 						// if called for RHS, swap sides for lemma and pos
							lexrule = new LexicalRule<GermaNetInfo>(rightorth, luPos, lemma, pos, CONFIDENCES.get(conrel.toConRel()), conrel.toGermaNetString(), "GermaNet", new GermaNetInfo(rightsyn.getId(), syn.getId()));
						}
						else{
							lexrule = new LexicalRule<GermaNetInfo>(lemma, pos, rightorth, luPos, CONFIDENCES.get(conrel.toConRel()), conrel.toGermaNetString(), "GermaNet", new GermaNetInfo(syn.getId(), rightsyn.getId()));
						}
						
						// exclude lemma from results, as well as GNROOT
						if (!(lexrule.getLLemma().equals(lexrule.getRLemma())) || (lexrule.getRLemma().equals("GNROOT")) || !(lexrule == null) ) {
							result.add(lexrule);
						}
					}
				}
			}
		}
		
		// iteration maximum defines limit of levels to go up: two levels for nouns, one for adjectives and verbs
		int max = 2; // for nouns
		if ((pos.toString().equals("ADJ")) || pos.toString().equals("V")){ max = 1; } // adj and verbs
		
		// get transitive closure with given relation
		//GNversion 7: List<List<Synset>> transClosure = syn.getTransRelations(conrel.toConRel());
		List<List<Synset>> transClosure = syn.getTransRelatedSynsets(conrel.toConRel()); 
		
		// iterate over levels of transitive closure (up till maximum)
		for (int i=1; i<transClosure.size() && i<=max; i++){ // ignore first level (the synset itself)
			// iterate over synsets at each level
			for (Synset rightsyn :  transClosure.get(i)) {
				
				//iterate over all lexical units of a synset
				for (LexUnit lu : rightsyn.getLexUnits()){
					
					GermanPartOfSpeech luPos = wordCategoryToPos(lu.getWordCategory());	
					// set pos of lexical unit to "NP" if named entity
					if (lu.isNamedEntity()){
						try {
							luPos = new GermanPartOfSpeech("NP");
							}
						catch (UnsupportedPosTagStringException e) {}
						}
					// iterate over all orthographic forms of a lexical unit
					for (String rightorth : rightsyn.getAllOrthForms()) {
						
						// create new lexical rule and add it to the results
						LexicalRule<? extends GermaNetInfo> lexrule = null;
						if (direction.equals("right")){ 						// if called for RHS, swap sides for lemma and pos
							lexrule = new LexicalRule<GermaNetInfo>(rightorth, luPos, lemma, pos, CONFIDENCES.get(conrel.toConRel()), conrel.toGermaNetString(), "GermaNet", new GermaNetInfo(rightsyn.getId(), syn.getId()));
						}
						else{
							lexrule = new LexicalRule<GermaNetInfo>(lemma, pos, rightorth, luPos, CONFIDENCES.get(conrel.toConRel()), conrel.toGermaNetString(), "GermaNet", new GermaNetInfo(syn.getId(), rightsyn.getId()));
						}
						
						// exclude lemma from results, as well as GNROOT
						if (!(lexrule.getLLemma().equals(lexrule.getRLemma())) || (lexrule.getRLemma().equals("GNROOT")) || !(lexrule == null) ) {
							result.add(lexrule);
						}
					}
				}
			}
		}
		return result;
		}
	
	/**
	 * Collect lexical related lexical units and the resulting rules for a given pos, synset and a lexical relation.
	 * 
	 * @param direction "left" or "right" depending the function that called this method
	 * @param lemma left lemma of all the rules
	 * @param pos PartOfSpeech of the given synset 
	 * @param syn synset the relations and rules are extracted from
	 * @param lexrel GermaNetRelation defining the relation of the extracted rules
	 * @return a set of lexical rules
	 * @throws LexicalResourceException 
	 */
	private Set<LexicalRule<? extends GermaNetInfo>> collectLexicalRules(
			String direction, String lemma, PartOfSpeech pos, Synset syn, GermaNetRelation lexrel) 
			throws LexicalResourceException, UnsupportedPosTagStringException{
		
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
		
		// iterate over lexically related units
		for (LexUnit lex : syn.getLexUnits()) {
			// get related lexical units
			List<LexUnit> lexright = lex.getRelatedLexUnits(lexrel.toLexRel());
			// iterate over lexical units
			for (LexUnit lu : lexright) {
				GermanPartOfSpeech luPos = wordCategoryToPos(lu.getWordCategory());	
				// set pos of lexical unit to "NP" if named entity
				if (lu.isNamedEntity()){
					try {
						luPos = new GermanPartOfSpeech("NP");
						}
					catch (UnsupportedPosTagStringException e) {}
					}
				// iterate over lexical unit's orthographic forms
			    for (String orth : lu.getOrthForms()) {
			    	
					// create new lexical rule and add it to the results
			    	LexicalRule<? extends GermaNetInfo> lexrule = null;
					if (direction.equals("right")){ 					// if called for RHS, swap sides for lemma and pos
						lexrule = new LexicalRule<GermaNetInfo>(orth, luPos, lemma, pos, CONFIDENCES.get(lexrel.toLexRel()), lexrel.toGermaNetString(), "GermaNet", new GermaNetInfo(lu.getSynset().getId(), syn.getId()));
					}
					else{
						lexrule = new LexicalRule<GermaNetInfo>(lemma, pos, orth, luPos, CONFIDENCES.get(lexrel.toLexRel()), lexrel.toGermaNetString(), "GermaNet", new GermaNetInfo(syn.getId(), lu.getSynset().getId()));
					}
					
					// exclude lemma from results, as well as GNROOT
					if (!( lexrule.getLLemma().equals(lexrule.getRLemma()) || lexrule.getRLemma().equals("GNROOT") || lexrule == null )) {
						result.add(lexrule);
					}
				}
			}
		}
		return result;
	}
	
}