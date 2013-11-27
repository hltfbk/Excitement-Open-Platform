package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

// Component imports
import eu.excitementproject.eop.common.component.Component;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

//other imports
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * This class implements a German Lexical Resource based on derivational information, 
 * DErivBase v1.3. The resource contains groups of lemmas, so-called derivational families,
 * which share a morphologic (and ideally a semantic) relationship, e.g. "sleep, 
 * sleepy, to sleep, sleepless" 
 * 
 * The implementation can access the resource file containing lemma-POS pairs and their 
 * corresponding derivations in two different formats:
 * 1. with confidence scores for each lemma pair within one derivational family; 
 *    example:
      Aalener_Nm: Aalen_Nn 1.00 aalen_V 0.50 Aal_Nn 0.33
 * 2. simply derivational families without information about lemma pair confidences; 
 *    example:
 *    Aalener_Nm: Aalen_Nn aalen_V Aal_Nn 
 * The user has to specify with the constructor call (or in the CommonConfig setting) 
 * if the resource format contains scores (1.) or not (2.). 
 * 
 * Each lemma is considered as entailing as well as being entailed by its corresponding 
 * derivations. The user can restrict this generalization by setting a maximum amount
 * of derivation steps which may be conducted to derive one lemma from another; for
 * details, see the german_resource_test_configuration.xml file.
 * 
 * <P>
 * The implementation supports LexicalResource, but not LexicalResourceWithRelation.
 * 
 * <P>
 * If DErivBase is not found, the component will raise an exception and will not be 
 * initialized. 
 * 
 * @author Britta Zeller 
 * @since Mar 2013
 */

public class DerivBaseResource implements Component, LexicalResource<DerivBaseInfo> {


	/** DerivBase object. */
	private DerivBase derivbase;

	
	/**
	 * Checks if the POS is valid for the DErivBase resource, i.e., if it is
	 * either a noun, verb, or adjective.
	 * 
	 * @param pos the POS to check
	 * @return true if POS is noun/verb/adjective, else false
	 */
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
	

	/**
	 * Creates a new DerivBaseResource instance, and initializes the instance
	 * (basically loads DerivBase file into memory).
	 * 
	 * @param config Configuration for the DerivBaseResource instance
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public DerivBaseResource(CommonConfig config) throws ConfigurationException, ComponentException {
		this(Boolean.parseBoolean(config.getSection("DerivBaseResource").getString("useScores")),
				config.getSection("DerivBaseResource").getInteger("derivatonSteps"));
	}
	
	
	/**
	 * Creates a new DerivBaseResource instance, and initializes the instance
	 * (basically loads DerivBase file into memory). Minimum confidence score
	 * and score flag are indicated and used.
	 * 
	 * @param useScores specifies if confidence scores for each lemma pair should be used 
	 * @param derivSteps specifies the maximum amount of derivational steps that may be 
	 *  proceeded between two lemmas to be considered
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */	
	public DerivBaseResource(boolean useScores, Integer derivSteps) throws ConfigurationException, ComponentException {

		// derivSteps = null is possible if configuration is not filled in completely
		if (derivSteps == null) {
			derivSteps = 10;
		}
		
		Double minScore = 1.0/derivSteps; 
		
		
		try {
			this.derivbase = new DerivBase(useScores, minScore);
		}
		catch (java.io.FileNotFoundException e) {
			throw new DerivBaseNotInstalledException("Path to DErivBase is not correct.", e);
		}
		catch (java.lang.Exception e) {
			throw new ComponentException("Cannot initialize DErivBase"
					+ ". Please check the path and the format of your DErivBase version. "
					+ "Should be: \n1) if you do not use confidence scores:\n" 
					+ "lemma_pos: relatedl1_p1 relatedl2_p2 ..." 
					+ "\n2) if you use confidence scores:\n" 
					+ "lemma_pos: relatedl1_p1 score1 relatedl2_ps score2 ...", e);
		}
	
	}
	
	
	/**
	 * This method provides the (human-readable) name of the component. It is used to 
	 * identify the relevant section in the common configuration for the current component. 
	 * See Spec Section 5.1.2, Overview of the common configuration  and Section 4.9.3, 
	 * Component name and instance name.
	 */
	public String getComponentName()
	{
		return "DerivBaseResource"; 
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
  

	/**
	 * Returns a list of lexical rules whose left side (the head of the lexical relation) matches
	 * the given lemma and POS. An empty list means that no rules were matched. 
	 * For POS == null, the method will retrieve rules for all possible POSes.
	 * 
	 * @param lemma Lemma to be matched on LHS. 
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends DerivBaseInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		// using a set makes the result unique
		Set<LexicalRule<? extends DerivBaseInfo>> result = new HashSet<LexicalRule<? extends DerivBaseInfo>>();
		
		// check POS is valid or not for DerivBase. Note that DerivBase only has noun, verb, and adjective.
		// for POSes unknown to DerivBase, no need to look up: return an empty list.
		if (pos != null && !isValidPos(pos)){  
			return new ArrayList<LexicalRule<? extends DerivBaseInfo>>(result);
		}
		
		
		// if the POS is unspecified (null), retrieve derivationally related lemmas for 
		// all three possible POSes "V", "N", "A".
		if (pos == null) {
			String[] poses = {"V", "N", "A"};
			for (String derivbasePos : poses) {
				result = getDerivRelatedRules(lemma, derivbasePos, result, convertPartOfSpeech(derivbasePos));
			}
			
		} else { // else, convert incoming POS into DErivBase POS: N, A, V
			String derivbasePos = pos.toString().substring(0, 1);		
			result = getDerivRelatedRules(lemma, derivbasePos, result, pos);
		}
		
		return new ArrayList<LexicalRule<? extends DerivBaseInfo>>(result);
	}
	


	/**
	 * Returns a set of lexical rules for a given lemma-POS pair as left hand side.
	 * An empty set means that no rules were matched.
	 * 
	 * @param lemma Lemma to be matched 
	 * @param derivbasePos internal String representation of the lemma's POS
	 * @param result (still empty) set of rules to be filled
	 * @param pos PartOfSpeech corresponding to the internal String representation of the POS
	 * @return a set of rules that matches the given condition. Empty list if there's no match.
	 * @throws LexicalResourceException
	 */
	private Set<LexicalRule<? extends DerivBaseInfo>> getDerivRelatedRules(
			String lemma, String derivbasePos, Set<LexicalRule<? extends DerivBaseInfo>> result, 
			PartOfSpeech pos) throws LexicalResourceException {

		// get related lemma-POS pairs and add them to results LexicalRule
		// --> Get these pairs once for the resource without scores...
		// The information in this List will later be written as additional information into DerivBaseInfo
		ArrayList<Tuple<String>> related = new ArrayList<Tuple<String>>();
		if (!derivbase.entries.isEmpty()) {
			related = derivbase.getRelatedLemmaPosPairs(lemma, derivbasePos);
		}
		// --> ... and once for the resource with scores.
		ArrayList<HashMap<Tuple<String>, Double>> relatedScores = new ArrayList<HashMap<Tuple<String>, Double>>();
		if (!derivbase.entryScores.isEmpty()) {
			relatedScores = derivbase.getRelatedLemmaPosPairsWithScore(lemma, derivbasePos);
		}
		// Note: ONLY ONE of those two ArrayLists will be filled; if no matches found, both lists are empty.
		// System.out.println("which one is empty? related: " + related.isEmpty() + ", or relatedScores: " + relatedScores.isEmpty());
		
		
		if (relatedScores.isEmpty()) { // if scores are available
			result = proceedWithoutScores(related, result, lemma, pos);
			
		} else { // if no scores are available
			result = proceedWithScores(relatedScores, result, lemma, pos);
		}		

		return result;
	}


	/**
	 * Conducts the lookup of a lemma-POS pair in DErivBase if scores are available.
	 * Returns a set of LexicalRules for this lemma-POS pair as left hand side. 
	 * 
	 * @param relatedScores the list of lemmas and corresponding scores which are 
	 *   (in DErivBase) derivationally related to the lemma
	 * @param result the set of LexicalRules which are found for the given lemma-POS pair
	 * @param lemma the given lemma
	 * @param pos the given POS
	 * @return a Set of LexicalRules found for the given lemma-POS pair
	 * @throws LexicalResourceException
	 */
	private Set<LexicalRule<? extends DerivBaseInfo>> proceedWithScores(ArrayList<HashMap<Tuple<String>, Double>> 
			relatedScores, Set<LexicalRule<? extends DerivBaseInfo>> result, String lemma, PartOfSpeech pos) throws LexicalResourceException {
		
		if (relatedScores.size() == 0) {
			return new HashSet<LexicalRule<? extends DerivBaseInfo>>(result);
		}
		
		// access HashMap with related lemma + score for lemma pair
		for (HashMap<Tuple<String>, Double> relatedPairAndScore : relatedScores) {
			for (Map.Entry<Tuple<String>, Double> entry : relatedPairAndScore.entrySet()) {
				
			    Tuple<String> relatedPair = entry.getKey();
			    String relatedLemma = relatedPair.getA();
			    PartOfSpeech relatedPos = convertPartOfSpeech(relatedPair.getB());
			    Double relatedConfScore = entry.getValue();
			    
			    // prepare saving info about the whole derivational family in 
				// DerivBaseInfo  as a Set<String>
				Set<String> derivFamily = new HashSet<String>();
				for (HashMap<Tuple<String>, Double> relMember : relatedScores) {
					// this loop always makes just one loop, since the HM only has 1 entry
					for (Tuple<String> relatedMember : relMember.keySet()) {
						derivFamily.add(relatedMember.getA().concat("_".concat(relatedMember.getB())));
					}
				}
				// prepare saving the resource-internal confidence score
				Double internalScore = (relatedConfScore-0.5)/0.5;
				
			    LexicalRule<? extends DerivBaseInfo> rule = new LexicalRule<DerivBaseInfo>(lemma, pos, 
						relatedLemma, relatedPos, relatedConfScore, "deriv-related", "DErivBase v1.3", new DerivBaseInfo(derivFamily, internalScore));
				
				result.add(rule);	
			}
		}

		// Gil's sanity check for non-zero confidence scores should be unnecessary here: 
		// This is done in the DerivBase resource itself!
		// Gil: check all result, and remove any 0 confidence value 
/*		Iterator<LexicalRule<? extends DerivBaseInfo>> i = result.iterator(); 
		while (i.hasNext()) {
			LexicalRule<? extends DerivBaseInfo> rule = i.next(); 
			if (rule.getConfidence() == 0 ) { // 0 confidence 
				i.remove(); 
			}
		}
	*/	
		return new HashSet<LexicalRule<? extends DerivBaseInfo>>(result);		
		
	}



	/**
	 * Conducts the lookup of a lemma-POS pair in DErivBase if no scores are available.
	 * Returns a set of LexicalRules for this lemma-POS pair as left hand side. 
	 * 
	 * @param related the list of lemmas which are (in DErivBase) derivationally related to the lemma
	 * @param result the set of LexicalRules which are found for the given lemma-POS pair
	 * @param lemma the given lemma
	 * @param pos the given POS
	 * @return a Set of LexicalRules found for the given lemma-POS pair
	 * @throws LexicalResourceException
	 */
	private HashSet<LexicalRule<? extends DerivBaseInfo>> proceedWithoutScores(
			ArrayList<Tuple<String>> related, Set<LexicalRule<? extends DerivBaseInfo>> result, String lemma, PartOfSpeech pos) throws LexicalResourceException {

		// if nothing found for this lemma-pos pair: return empty list
		if (related.size() == 0) {
			return new HashSet<LexicalRule<? extends DerivBaseInfo>>(result);
		}
		
		for (Tuple<String> relatedPair: related) {
			String relatedLemma = relatedPair.getA();
			PartOfSpeech relatedPos = convertPartOfSpeech(relatedPair.getB());
			
			// prepare saving info about the whole derivational family in 
			// DerivBaseInfo  as a Set<String>
			Set<String> derivFamily = new HashSet<String>();
			for (Tuple<String> relatedMember : related) {
				derivFamily.add(relatedMember.getA().concat("_").concat(relatedMember.getB()));
			}			
			LexicalRule<? extends DerivBaseInfo> rule = new LexicalRule<DerivBaseInfo>(lemma, pos, 
					relatedLemma, relatedPos, "deriv-related", "DErivBase v1.3", new DerivBaseInfo(derivFamily));
			
			result.add(rule);	
		}
		
		// Gil's sanity check for non-zero confidence scores is not necessary here: 
		// No scores available.
		
		return new HashSet<LexicalRule<? extends DerivBaseInfo>>(result);
	}
	



	/** Returns a list of lexical rules whose right side (the target of the lexical relation) matches 
	 * the given lemma and POS. An empty list means that no rules were matched.
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends DerivBaseInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		List<LexicalRule<? extends DerivBaseInfo>> result = new ArrayList<LexicalRule<? extends DerivBaseInfo>>();

		List<LexicalRule<? extends DerivBaseInfo>> rules = getRulesForLeft(lemma, pos);
		for (LexicalRule<? extends DerivBaseInfo> rule : rules) {
			result.add(new LexicalRule<DerivBaseInfo>(rule.getRLemma(), rule.getRPos(), 
					rule.getLLemma(), rule.getLPos(), rule.getConfidence(), "deriv-related", "DErivBase v1.3", rule.getInfo()));
		}
		return result;
	}

	
	
	/** 
	 * This method returns a list of lexical rules whose left and right sides match the two given 
	 * pairs of lemma and POS.
	 * 
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends DerivBaseInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException
	{
		
		List<LexicalRule<? extends DerivBaseInfo>> prelimResult;
		prelimResult = this.getRulesForLeft(leftLemma, leftPos);
		
		for (LexicalRule<? extends DerivBaseInfo> rule : prelimResult) {
			if (rule.getRLemma().equals(rightLemma)) {
				ArrayList<LexicalRule<? extends DerivBaseInfo>> result = new ArrayList<LexicalRule<? extends DerivBaseInfo>>();
				result.add(rule);
				return new ArrayList<LexicalRule<? extends DerivBaseInfo>>(result);
			}
		}
		
		return new ArrayList<LexicalRule<? extends DerivBaseInfo>>();
	}


	
	/**
	 * Converts DErivBase-internal POS notation to the project-wide one, i.e.,
	 * converts "A" to "ADJ".
	 * 
	 * @param relatedPair a tuple of lemma and POS, where the POS should be converted
	 * @return
	 */
	private PartOfSpeech convertPartOfSpeech(String internalPos) {
		PartOfSpeech relatedPos = null;
		try {
			// TODO: is it problematic for calling functions that getB 
			// returns only N,V,A, but not NN?
			
			// enlarge "A" to "ADJ" for GermanPartOfSpeech lookup
			if (internalPos.equals("A")) {
				relatedPos = new GermanPartOfSpeech("ADJ");
			} else {
				relatedPos = new GermanPartOfSpeech(internalPos);
			}
		} catch (UnsupportedPosTagStringException e) {
			System.err.println("Problems converting the DErivBase POS string " 
					+ internalPos + " to GermanPartOfSpeech.");
			e.printStackTrace();
		}
		return relatedPos;
	}


	
	@Override
	public void close() throws LexicalResourceCloseException {
		
	}
		
}

