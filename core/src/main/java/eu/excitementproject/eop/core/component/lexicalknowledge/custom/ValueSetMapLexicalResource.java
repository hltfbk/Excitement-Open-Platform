package eu.excitementproject.eop.core.component.lexicalknowledge.custom;

import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.excitementproject.eop.core.component.lexicalknowledge.EmptyRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceNothingToClose;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

/**
 * <p>A lexical resource based on a {@link ValueSetMap} (a bidirectional multi-map) of Strings.
 * <p>Each key->value association in the map corresponds to the lemmas in a left->right entailment rule.
 * <p>All rules in a single instance of this class relate to a single part-of-speech, specified in the constructor. 
 *
 * @author Erel Segal Halevi
 * @since 2012-07-24
 */
public class ValueSetMapLexicalResource extends LexicalResourceNothingToClose<RuleInfo> {
	
	/**
	 * Initialize the lexical resource.
	 * @param theMap Each key->value association in the map corresponds to the lemmas in a left->right entailment rule.
	 * @param thePartOfSpeech All rules in this instance relate to this part-of-speech only.
	 * @param theResourceName will be inserted in the rules, for information only. 
	 * @param theRelationName will be inserted in the rules, for information only. 
	 * @throws UnsupportedPosTagStringException if the given part-of-speech is illegal.
	 */
	public ValueSetMapLexicalResource(ValueSetMap<String,String> theMap, SimplerCanonicalPosTag thePartOfSpeech, String theResourceName, String theRelationName) throws UnsupportedPosTagStringException {
		map = theMap;
		setCanonicalPartOfSpeech (thePartOfSpeech);
		resourceName = theResourceName;
		relationName = theRelationName;
	}

	@SuppressWarnings("unchecked")
	@Override public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String leftLemma, PartOfSpeech leftPos) throws LexicalResourceException {
		if (leftPos==null || simplerPos(leftPos.getCanonicalPosTag())==this.canonicalPartOfSpeech) {
			PartOfSpeech posForRule = (leftPos==null? this.partOfSpeech: leftPos); 
			List<LexicalRule<? extends RuleInfo>> theRules = new ArrayList<LexicalRule<? extends RuleInfo>>();
			for (String rightLemma: getMap().get(leftLemma)) {  // NOTE: getMap() is used instead of this.map, to allow overriding in OnlineFileBasedLexicalResource.
				theRules.add(new LexicalRule<RuleInfo>(
						leftLemma, posForRule,
						rightLemma, posForRule,
						relationName, resourceName,	EmptyRuleInfo.getInstance()));
			}
			return theRules;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	@SuppressWarnings("unchecked")
	@Override public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException {
		if (rightPos==null || simplerPos(rightPos.getCanonicalPosTag())==this.canonicalPartOfSpeech) { 
			PartOfSpeech posForRule = (rightPos==null? this.partOfSpeech: rightPos); 
			List<LexicalRule<? extends RuleInfo>> theRules = new ArrayList<LexicalRule<? extends RuleInfo>>();
			for (String leftLemma: getMap().getKeysOf(rightLemma)) { // NOTE: getMap() is used instead of this.map, to allow overriding in OnlineFileBasedLexicalResource.
				theRules.add(new LexicalRule<RuleInfo>(
						leftLemma, posForRule,
						rightLemma, posForRule,
						relationName, resourceName, EmptyRuleInfo.getInstance()));
			}
			return theRules;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	@SuppressWarnings("unchecked")
	@Override public List<LexicalRule<? extends RuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException {
		if (rightPos==null || simplerPos(rightPos.getCanonicalPosTag())==this.canonicalPartOfSpeech) {
			if (leftPos==null || simplerPos(leftPos.getCanonicalPosTag())==this.canonicalPartOfSpeech) {
				ImmutableSet<String> rightLemmas = getMap().get(leftLemma); // NOTE: getMap() is used instead of this.map, to allow overriding in OnlineFileBasedLexicalResource.
				if (rightLemmas.contains(rightLemma)) {
					List<LexicalRule<? extends RuleInfo>> theRules = new ArrayList<LexicalRule<? extends RuleInfo>>();
					theRules.add(new LexicalRule<RuleInfo>(
								leftLemma, (leftPos==null? this.partOfSpeech: leftPos),
								rightLemma, (rightPos==null? this.partOfSpeech: rightPos),
								relationName, resourceName, EmptyRuleInfo.getInstance()));
					return theRules;
				}
			}
		}
		return Collections.EMPTY_LIST;
	}
	
	
	
	/**
	 * @return the map. Each key->value association in the map corresponds to the lemmas in a left->right entailment rule.
	 */
	public ValueSetMap<String, String> getMap()  {
		return map;
	}

	/**
	 * @return the canonicalPartOfSpeech
	 */
	public SimplerCanonicalPosTag getCanonicalPartOfSpeech() {
		return canonicalPartOfSpeech;
	}

	/**
	 * @return the partOfSpeech
	 */
	public PartOfSpeech getPartOfSpeech() {
		return partOfSpeech;
	}

	/**
	 * @param canonicalPartOfSpeech the canonicalPartOfSpeech to set
	 * @throws UnsupportedPosTagStringException 
	 */
	private void setCanonicalPartOfSpeech(SimplerCanonicalPosTag canonicalPartOfSpeech) throws UnsupportedPosTagStringException {
		this.canonicalPartOfSpeech = canonicalPartOfSpeech;
		partOfSpeech = new BySimplerCanonicalPartOfSpeech(canonicalPartOfSpeech);
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @return the relationName
	 */
	public String getRelationName() {
		return relationName;
	}



	
	
	/*
	 * PROTECTED ZONE
	 */
	
	protected ValueSetMap<String,String> map;
	protected SimplerCanonicalPosTag canonicalPartOfSpeech;
	protected PartOfSpeech partOfSpeech;  // for insertion in rules when the input POS is null
	protected String resourceName, relationName; 
	
	
	
	/*
	 * TEST ZONE
	 */
	
	
	protected static void assertCollection(Collection<?> theCollection, int expectedSize) {
		if (expectedSize!=theCollection.size()) {
			System.out.println("ERROR! Expected a collection of size "+expectedSize+", but got "+theCollection);
		}
	}
	
	/**
	 * demo program
	 */
	public static void main(String[] args) throws Exception {
		ValueSetMapLexicalResourceTest.main(args);
	}
}

