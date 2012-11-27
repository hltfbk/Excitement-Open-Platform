package eu.excitementproject.eop.core.component.lexicalknowledge.germanet;

// Component imports
import eu.excitementproject.eop.common.Component;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;

// LexicalResource imports
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceWithRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.TERuleRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.representation.parsetree.GermanPartOfSpeech;
import eu.excitementproject.eop.core.representation.parsetree.PartOfSpeech;
import eu.excitementproject.eop.core.representation.parsetree.UnsupportedPosTagStringException;

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
				throw new LexicalResourceException("Part-of-Speech " + pos.getStringRepresentation() + " is not covered by GermaNet.");
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
	 * This method will be called by the component user as the signal for initializing 
	 * the component. All initialization (including connecting and preparing resources) 
	 * should be done within this method. Implementations must check the configuration and 
	 * raise exceptions if the provided configuration is not compatible with the implementation.
	 * 
	 * @param config a common configuration object. This configuration object holds the platform- wide configuration. An implementation should process the object to retrieve relevant configuration values for the component. 
	 */
	public void initialize(CommonConfig config) throws ConfigurationException, ComponentException
	{
		try {
			this.germanet = new GermaNet("/resources/ontologies/germanet-7.0/GN_V70/GN_V70_XML/"); // TODO: Read path from config!
		}
		catch (java.io.FileNotFoundException e) {
			throw new GermaNetNotInstalledException("Path to GermaNet is not set correctly.", e);
		}
		catch (java.lang.Exception e) {
			throw new ComponentException("Cannot initialize GermaNet.", e);
		}

		// TODO read confidence values from config
		CONFIDENCES.put(ConRel.causes, 0.5);
		CONFIDENCES.put(ConRel.entails, 0.8);
		CONFIDENCES.put(ConRel.has_hypernym, 0.7);
		CONFIDENCES.put(LexRel.has_synonym, 0.8);
		CONFIDENCES.put(LexRel.has_antonym, 0.8);
	}
	
	
	/**
	 * This method provides the (human-readable) name of the component. It is used to 
	 * identify the relevant section in the common configuration for the current component. 
	 * See Spec Section 5.1.2, ���Overview of the common configuration ��� and Section 4.9.3, 
	 * ���Component name and instance name���.
	 */
	public String getComponentName()
	{
		return "GermaNetWrapper"; // TODO: change to some official name
	}
	
	
	/** This method provides the (human-readable) name of the instance. It is used to 
	 * identify the relevant subsection in the common configuration for the current component. 
	 * See Spec Section 5.1.2, ���Overview of the common configuration ��� and Section 4.9.3, 
	 * ���Component name and instance name���. Note that this method can return null value, if 
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

}
