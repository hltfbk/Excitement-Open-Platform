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

// other imports
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * This class implements a German Lexical Resource based on GermaNet 7.0, which is 
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
 * TODO: Jan, is there any additional assumptions or conditions that a user might need to know? 
 * 
 * @author Jan Pawellek 
 * @since Nov 2012 
 */
public class GermaNetWrapper implements Component, LexicalResourceWithRelation<GermaNetInfo, GermaNetRelation> {

	/** conceptual relations indicating entailment */
	private final ConRel[] CONREL_E = { ConRel.causes, ConRel.entails, ConRel.has_hypernym }; 

	/** conceptual relations indicating non-entailment */
	private final ConRel[] CONREL_NE = { }; 

	/** lexical relations indicating entailment */
	private final LexRel[] LEXREL_E = { LexRel.has_synonym };

	/** lexical relations indicating non-entailment */
	private final LexRel[] LEXREL_NE = { LexRel.has_antonym };

	/** per-relation output confidences */
	private final Map<Enum<?>, Double> CONFIDENCES = new HashMap<Enum<?>, Double>();

	private String fineGrainedRelation = "";

	private GermaNet germanet;

	private boolean isValidPos(PartOfSpeech pos) {
		
		switch(pos.getCanonicalPosTag()) {
			case ADJ:	
			case N:
			case NN:
			case V:
				return true; 
			default:
				return false; 
		}
	}
	
	private WordCategory posToWordCategory(PartOfSpeech pos) throws LexicalResourceException {
		switch (pos.getCanonicalPosTag()) {
			case ADJ:
				return WordCategory.adj;
			case N:
			case NN:
				return WordCategory.nomen;
			case V:
				return WordCategory.verben;
			default:
				//throw new LexicalResourceException("Part-of-Speech " + pos.getStringRepresentation() + " is not covered by GermaNet.");
				throw new LexicalResourceException("Integrity failure; non-compatible POS shouldn't be passed to this point. "); 
		} 
	}

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
	 * Creates a new GermaNetWrapper instance, and initializes the instance
	 * (basically loads GermaNet files into memory).
	 * 
	 * @param config		Configuration for the GermaNetWrapper instance
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public GermaNetWrapper(CommonConfig config) throws ConfigurationException, ComponentException {
		// TODO CommonConfig not implemented yet -- this is how it MIGHT work. Change it later!
		this(config.getSection("GermaNetWrapper").getString("germaNetFilesPath"),
				config.getSection("GermaNetWrapper").getDouble("causesConfidence"),
				config.getSection("GermaNetWrapper").getDouble("entailsConfidence"),
				config.getSection("GermaNetWrapper").getDouble("hypernymConfidence"),
				config.getSection("GermaNetWrapper").getDouble("synonymConfidence"),
				config.getSection("GermaNetWrapper").getDouble("antonymConfidence"));
		// TODO Remove the following line, if done.
		throw new ComponentException("This method is not implemented yet.");
	}
	
	/**
	 * Creates a new GermaNetWrapper instance, and initializes the instance
	 * (basically loads GermaNet files into memory).
	 * Sets the default value 1.0 as confidence value for all relations.
	 * 
	 * @param germaNetFilesPath			Path to GermaNet XML files
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public GermaNetWrapper(String germaNetFilesPath) throws ConfigurationException, ComponentException {
		this(germaNetFilesPath, 1, 1, 1, 1, 1);
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
	 * @param antonymConfidence		Confidence to be set for "antonym" relations
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public GermaNetWrapper(String germaNetFilesPath, double causesConfidence, double entailsConfidence,
			double hypernymConfidence, double synonymConfidence, double antonymConfidence)
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

		CONFIDENCES.put(ConRel.causes, causesConfidence);
		CONFIDENCES.put(ConRel.entails, entailsConfidence);
		CONFIDENCES.put(ConRel.has_hypernym, hypernymConfidence);
		CONFIDENCES.put(LexRel.has_synonym, synonymConfidence);
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
		return "GermaNetWrapper"; // TODO: change to some official name
	}
	
	
	/** This method provides the (human-readable) name of the instance. It is used to 
	 * identify the relevant subsection in the common configuration for the current component. 
	 * See Spec Section 5.1.2, Overview of the common configuration  and Section 4.9.3, 
	 * Component name and instance name. Note that this method can return null value, if 
	 * and only if all instances of the component shares the same configuration.
	 */
	public String getInstanceName() {
		return null; // TODO: change 
        }
  
	/**
	 * Returns a list of lexical rules whose left side (the head of the lexical relation) matches
	 * the given lemma and POS. An empty list means that no rules were matched. If the user 
	 * gives null POS, the class will retrieve rules for all possible POSes.
	 * @param lemma Lemma to be matched on LHS. 
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		// concatenate Entailment and NonEntailment rules and return
		List<LexicalRule<? extends GermaNetInfo>> result;
		result = this.getRulesForLeft(lemma, pos, TERuleRelation.Entailment);
		result.addAll(this.getRulesForLeft(lemma, pos, TERuleRelation.NonEntailment));
		return result;
	}
	
	
	/**an overloaded method for getRulesForLeft. In addition to the previous method, this method 
	 * also matches the relation field of LexicalRule with the argument.
	 * @param lemma Lemma to be matched on LHS
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return A list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForLeft(String lemma, PartOfSpeech pos, TERuleRelation relation) throws LexicalResourceException
	{
		// using a set makes the result unique
		Set<LexicalRule<? extends GermaNetInfo>> result = new HashSet<LexicalRule<? extends GermaNetInfo>>();
		
		// check POS is valid or not for GermaNet. Note that GermaNet only has noun, verb, and adjective.
		if ( pos != null && !isValidPos(pos))
		{
			// POS class that GermaNet knows not.  
			// No need to look up, return an empty list.  
			return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
		}

		// Fetch synsets for lemma
		List<Synset> syns = pos == null ? germanet.getSynsets(lemma) : germanet.getSynsets(lemma, posToWordCategory(pos));

		for (Synset syn : syns) {
			// Follow relevant Conceptual relations
			for (ConRel conrel : relation == TERuleRelation.Entailment ? CONREL_E : CONREL_NE) {
				if (!(this.fineGrainedRelation == null || this.fineGrainedRelation.equals("") || this.fineGrainedRelation.equals(conrel.toString()))) continue;
				// Get transitive closure
				List<List<Synset>> transClosure = syn.getTransRelations(conrel);

				// Add to result
				int i = 0;
				for (List<Synset> level : transClosure) {
					// ignore first level (the synset itself)
					if (i == 0) { 
						i++;
						continue;
					}
					i++;
					for (Synset rightsyn : level) {
						for (String orth : rightsyn.getAllOrthForms()) {
							GermaNetInfo info = new GermaNetInfo(syn.getId(), rightsyn.getId());
							//LexicalRule<? extends GermaNetInfo> lexrule = new LexicalRule<GermaNetInfo>(lemma, pos, orth, wordCategoryToPos(rightsyn.getWordCategory()), CONFIDENCES.get(conrel), relation, conrel.toString(), "GermaNet", info);
							
							if (pos == null) {
								try {
									pos = new GermanPartOfSpeech("OTHER");
								}
								catch (UnsupportedPosTagStringException e) {}
							}
							LexicalRule<? extends GermaNetInfo> lexrule = new LexicalRule<GermaNetInfo>(lemma, pos, orth, wordCategoryToPos(rightsyn.getWordCategory()), CONFIDENCES.get(conrel), conrel.toString(), "GermaNet", info);
							
							result.add(lexrule);
						}
					}
				}
			}

			// Follow relevant Lexical relations
			for (LexUnit lex : syn.getLexUnits()) {
				for (LexRel lexrel : relation == TERuleRelation.Entailment ? LEXREL_E : LEXREL_NE) {
					if (!(this.fineGrainedRelation == null || this.fineGrainedRelation.equals("") || this.fineGrainedRelation.equals(lexrel.toString()))) continue;
					List<LexUnit> lr = lex.getRelatedLexUnits(lexrel);

					// Add to result
					for (LexUnit lu : lr) {
					    for (String orth : lu.getOrthForms()) {
						GermaNetInfo info = new GermaNetInfo(syn.getId(), lu.getSynset().getId());
						//LexicalRule<? extends GermaNetInfo> lexrule = new LexicalRule<GermaNetInfo>(lemma, pos, orth, wordCategoryToPos(lu.getSynset().getWordCategory()), CONFIDENCES.get(lexrel), relation, lexrel.toString(), "GermaNet", info);
						
						if (pos == null) {
							try {
								pos = new GermanPartOfSpeech("OTHER");
							}
							catch (UnsupportedPosTagStringException e) {}
						}
						LexicalRule<? extends GermaNetInfo> lexrule = new LexicalRule<GermaNetInfo>(lemma, pos, orth, wordCategoryToPos(lu.getSynset().getWordCategory()), CONFIDENCES.get(lexrel), lexrel.toString(), "GermaNet", info);

						result.add(lexrule);
					    }
					}
				}
			}
		}

		return new ArrayList<LexicalRule<? extends GermaNetInfo>>(result);
	}
	
	
	/** Returns a list of lexical rules whose right side (the target of the lexical relation) matches 
	 * the given lemma and POS. An empty list means that no rules were matched.
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		throw new LexicalResourceException("Unsupported operation for GermaNet.");
	}
	
	/** An overloaded method for getRulesForRight. In addition to the previous method, 
	 * this method also matches the relation field of LexicalRule with the argument.
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */	
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForRight(String lemma, PartOfSpeech pos, TERuleRelation relation) throws LexicalResourceException
        {
                throw new LexicalResourceException("Unsupported operation for GermaNet.");
        }
	
	
	/** This method returns a list of lexical rules whose left and right sides match the two given pairs of lemma and POS.
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException
	{
                // concatenate Entailment and NonEntailment rules and return
                List<LexicalRule<? extends GermaNetInfo>> result;
                result = this.getRules(leftLemma, leftPos, rightLemma, rightPos, TERuleRelation.Entailment);
                result.addAll(this.getRules(leftLemma, leftPos, rightLemma, rightPos, TERuleRelation.NonEntailment));
                return result;
	}
	
	
	/** An overloaded method for getRules. In addition to the previous method, this method also matches the relation field of LexicalRule with the argument.
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermaNetInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, TERuleRelation relation) throws LexicalResourceException
	{
		List<LexicalRule<? extends GermaNetInfo>> result = new ArrayList<LexicalRule<? extends GermaNetInfo>>();
		for (LexicalRule<? extends GermaNetInfo> rule : getRulesForLeft(leftLemma, leftPos, relation)) {
			if (rule.getRLemma().equals(rightLemma) && (rightPos == null || rule.getRPos().equals(rightPos))) {
				result.add(rule);
			}
		}
		return result;
	}

	public List<LexicalRule<? extends GermaNetInfo>> getRulesForLeft(String lemma, PartOfSpeech pos, GermaNetRelation fineGrainedRelation) throws LexicalResourceException {
		this.fineGrainedRelation = fineGrainedRelation.toGermaNetString();
		List<LexicalRule<? extends GermaNetInfo>> result = getRulesForLeft(lemma, pos);
		this.fineGrainedRelation = "";
		return result;
	}
	
	public List<LexicalRule<? extends GermaNetInfo>> getRulesForRight(String lemma, PartOfSpeech pos, GermaNetRelation fineGrainedRelation) throws LexicalResourceException {
		this.fineGrainedRelation = fineGrainedRelation.toGermaNetString();
                List<LexicalRule<? extends GermaNetInfo>> result = getRulesForRight(lemma, pos);
                this.fineGrainedRelation = "";
                return result;
	}

	public List<LexicalRule<? extends GermaNetInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, GermaNetRelation fineGrainedRelation) throws LexicalResourceException {
		this.fineGrainedRelation = fineGrainedRelation.toGermaNetString();
                List<LexicalRule<? extends GermaNetInfo>> result = getRules(leftLemma, leftPos, rightLemma, rightPos);
                this.fineGrainedRelation = "";
                return result;
	}

	@Override
	public void close() throws LexicalResourceCloseException
	{
		// TODO Auto-generated method stub
		
	}

}

