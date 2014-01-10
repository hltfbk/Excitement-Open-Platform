
package eu.excitementproject.eop.core.component.lexicalknowledge.transDm;

//import eu.excitementproject.eop.common.component.Component;
//import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
//import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.TERuleRelation;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;





// other imports
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;


/**
 * This class implements a German lexical resource based on cross- and multilingual 
 * corpus term distribution. It takes advantage of huge available corpara in English
 * by mapping them into another language.
 * 
 * The resource is based on a standard English syntax-based distributional resource,
 * Baroni and Lenci’s Distributional Memory, which is "translated" into German using
 * a simple translation lexicon, and complements it with co-occurrence information 
 * gathered from a German corpus.
 *   
 * More specifically, the resource uses 
 * 1. the WxLW matrix of Baroni and Lenci's three-dimensional Distributional Memory
 * 2. the co-occurrence information from the German sdeWaC corpus
 * For each word pair, the similarity table contains the maximum of the similarities 
 * in 1. and 2.
 *   
 * We consider only the 2 million word pairs per similarity measure with the highest 
 * similarity values achieved with this method. Implemented similarity measures are:
 * <li>cosine</li>
 * <li>balAPinc</li>
 * They can be used in combination or individually (cosine, balapinc, all).
 * 
 * 
 * The two similarity measure files are loaded within 20 seconds on a computer with 
 * four 2.5GHz cores and 8GB RAM.
 * 
 * 
 * @author Britta Zeller <zeller@cl.uni-heidelberg.de> 
 * @since Nov. 2013 
 */
public class GermanTransDmResource implements LexicalResource<GermanTransDmInfo> {

	
	/** Stores information if the call is made for normal or reverse sims, thus: 
	 *  in which way the resulting List of Rules should be filled. */
	boolean isReverseMap = false;
	
	/** per-relation output confidences */
	private final List<Enum<SimMeasure>> simMeasures= new ArrayList<Enum<SimMeasure>>();	
	
	/** Stores similarity values: measurename -&gt; LHS word-pos -&gt; RHS word-pos -&gt; similarityvalue */
	private Map<String, Map<String, Map<String, Float>>> sims = new HashMap<String, Map<String, Map<String, Float>>>();

	/** Stores similarity values: measurename -&gt; RHS word-pos -&gt; LHS word-pos -&gt; similarityvalue 
	 *  e.g. <cosine, <Flora-n, <Fauna-n, 0.1234>>>
	 */	
	private Map<String, Map<String, Map<String, Float>>> reverse_sims = new HashMap<String, Map<String, Map<String, Float>>>();

	
	
	/**
	 * Creates a new GermanTransDm instance via a common config file.
	 * 
	 * @param config		Configuration for the GermanTransDm instance
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public GermanTransDmResource(CommonConfig config) throws ConfigurationException, ComponentException {
		this(config.getSection("GermanTransDm").getString("simMeasure"));		
	}
	
	
	/**
	 * Creates a new GermanTransDM instance according to the similarity measure(s) parameter
	 * handed in. Depending on this parameter, either one or two lists of pair similarities
	 * are loaded.
	 * 
	 * @param simMeasure the similarity measure to be used. Choices: cosine, balapinc, 
	 *        all (= both cosine and balapinc)
	 * @throws ConfigurationException
	 */
	public GermanTransDmResource(String simMeasure) throws ConfigurationException
	{ 
		
		ArrayList<String> listResource = new ArrayList<String>();
		
		if (simMeasure.equals("all")) {
			simMeasures.add(SimMeasure.BALAPINC);
			simMeasures.add(SimMeasure.COSINE);			
			
		} else if (simMeasure.equals("cosine")) {
			simMeasures.add(SimMeasure.COSINE);
			
		} else if (simMeasure.equals("balapinc")) {
			simMeasures.add(SimMeasure.BALAPINC);
			
		} else {
			throw new GermanTransDmException("'" + simMeasure + "' is no valid similarity measure name.");
		}
		
		for (Enum<SimMeasure> sim : simMeasures) {
			listResource.add("/transDm-data/sdewac.synt.transdm.10k." + sim.toString());///transDm-data/sdewac.synt.transdm.2mil." + sim.toString());
		}
		
		try {
			for (String simresource : listResource) {
				Scanner scanner = new Scanner(this.getClass().getResourceAsStream(simresource), "UTF-8");
				Map<String, Map<String, Float>> maps = new HashMap<String, Map<String, Float>>();
				Map<String, Map<String, Float>> reverse_maps = new HashMap<String, Map<String, Float>>();

				while (scanner.hasNextLine()) {
					String[] parts = scanner.nextLine().split("\\s");
							String lhs = parts[0].intern(); // = "lemma-pos"
							String rhs = parts[1].intern(); // = "lemma-pos"		
							Float similarity = Float.parseFloat(parts[2]);
							
							if (!maps.containsKey(lhs)) maps.put(lhs, new HashMap<String, Float>());
							maps.get(lhs).put(rhs, similarity);

							if (!reverse_maps.containsKey(rhs)) reverse_maps.put(rhs, new HashMap<String, Float>());
							reverse_maps.get(rhs).put(lhs, similarity);
				}
				/** 
				 * IMPORTANT !!! 
				 * Make sure that the input files end with distinct strings after the last ".",
				 * otherwise the following doesn't work.
				 */
				sims.put(simresource.substring(simresource.lastIndexOf('.') + 1), maps);
				reverse_sims.put(simresource.substring(simresource.lastIndexOf('.') + 1), reverse_maps);
				scanner.close();
			}
		}
		catch (java.lang.Exception e) {
			throw new GermanTransDmException("Cannot load similarity file: " + e.getMessage(), e);
		}
	}
	
	
	/**
	 * This method provides the (human-readable) name of the component. It is used to 
	 * identify the relevant section in the common configuration for the current component. 
	 * See Spec Section 5.1.2, "Overview of the common configuration " and Section 4.9.3, 
	 * "Component name and instance name".
	 */
	public String getComponentName()
	{
		return "GermanTransDM"; 
	}
	
	
	/** This method provides the (human-readable) name of the instance. It is used to 
	 * identify the relevant subsection in the common configuration for the current component. 
	 * See Spec Section 5.1.2, "Overview of the common configuration " and Section 4.9.3, 
	 * "Component name and instance name". Note that this method can return null value, if 
	 * and only if all instances of the component shares the same configuration.
	 */
	public String getInstanceName() {
		return null;	
	}
	
	
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
	 * Returns all rules that can be derived from a given lemma. Depending on the kind of map
	 * provided as second argument, this returns either LHS or RHS rule derivations.
	 * 
	 * Note that, if there are various similarity measures in the corresponding map, the  
	 * resulting List may contain entries for various similarity measures (and thus, various 
	 * entries for the same LHS/RHS lemma pair, but with different scores for different 
	 * similarity measures).   
	 * 
	 * @param lemma Lemma to be matched.
	 * @param pos the corresponding POS.
	 * @param map Map containing rules.
	 * @return List containing all derivable rules.
	 * @throws LexicalResourceException
	 */
	private List<LexicalRule<? extends GermanTransDmInfo>> getFromMap(String lemma, PartOfSpeech pos, Map<String, Map<String, Map<String, Float>>> map) throws LexicalResourceException
	{
		// using a set makes the result unique
		Set<LexicalRule<? extends GermanTransDmInfo>> result = new HashSet<LexicalRule<? extends GermanTransDmInfo>>();
      
		// first check if we have a POS we can say something about with this resource,
		// i.e., verbs, nouns, adjectives. Else, return emtpy list.
		if (!isValidPos(pos)) {
			return new ArrayList<LexicalRule<? extends GermanTransDmInfo>>(result);
		}
		
		// transDM gives information about lemma-pos pairs -> merge lemma and pos
		// input so that it matches with resource format (cat-n, eat-v, tiny-j)
		String transDmPos = "";
		if (pos.toString().equals("ADJ")) {
			transDmPos = "j".intern();
		} else {
			transDmPos = pos.toString().substring(0, 1).toLowerCase().intern();
		}
		lemma = lemma.intern();		
		String lemmapos = lemma.concat("-").concat(transDmPos);
		lemmapos = lemmapos.intern();
		

		for (String measure : map.keySet()) {
			if (map.get(measure).containsKey(lemmapos)) {
				for (String rhs : map.get(measure).get(lemmapos).keySet()) {
					float score = map.get(measure).get(lemmapos).get(rhs);

					// Use POS information from the resource.
					GermanPartOfSpeech pos1 = null; 
					GermanPartOfSpeech pos2 = null; 
					try {
						if (lemmapos.substring(lemmapos.lastIndexOf("-") + 1).intern() == "v") {
							pos1 = new GermanPartOfSpeech("V");
						} else if (lemmapos.substring(lemmapos.lastIndexOf("-") + 1).intern() == "n") {
							pos1 = new GermanPartOfSpeech("N");
						} else if (lemmapos.substring(lemmapos.lastIndexOf("-") + 1).intern() == "j") {
							pos1 = new GermanPartOfSpeech("ADJ");
						} 
						
						if (rhs.substring(rhs.lastIndexOf("-") + 1).intern() == "v") {
							pos2 = new GermanPartOfSpeech("V");
						} else if (rhs.substring(rhs.lastIndexOf("-") + 1).intern() == "n") {
							pos2 = new GermanPartOfSpeech("N");
						} else if (rhs.substring(rhs.lastIndexOf("-") + 1).intern() == "j") {
							pos2 = new GermanPartOfSpeech("ADJ");
						} 
						
					}
					catch (UnsupportedPosTagStringException e) {
						// pos stay null
					}

					
					LexicalRule<? extends GermanTransDmInfo> lexrule;
					if (isReverseMap) { // turn lhs and rhs around.
						lexrule = new LexicalRule<GermanTransDmInfo>(rhs.substring(0, rhs.lastIndexOf("-")), pos2, 
								lemma, pos1, score, measure, "GermanTransDm", new GermanTransDmInfo());						
					} else { // keep lhs and rhs as it is in this method.
						lexrule = new LexicalRule<GermanTransDmInfo>(lemma, pos1, rhs.substring(0, rhs.lastIndexOf("-")), 
								pos2, score, measure, "GermanTransDm", new GermanTransDmInfo());						
					}
					result.add(lexrule);
					
				}			
			}		
		}
		return new ArrayList<LexicalRule<? extends GermanTransDmInfo>>(result);
	}

	
	/**
	 * Returns a list of lexical rules whose left side (the head of the lexical relation) matches
	 * the given lemma and POS. An empty list means that no rules were matched. If the user 
	 * gives null POS, the class will retrieve rules for all possible POSes.
	 * @param lemma Lemma to be matched on LHS. 
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermanTransDmInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{	
		isReverseMap = false;
		return getFromMap(lemma, pos, sims);
	}
	
	
	/**an overloaded method for getRulesForLeft. In addition to the previous method, this method 
	 * also matches the relation field of LexicalRule with the argument.
	 * @param lemma Lemma to be matched on LHS
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return A list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermanTransDmInfo>> getRulesForLeft(String lemma, PartOfSpeech pos, TERuleRelation relation) throws LexicalResourceException
	{
		// DistSim can only return Entailment
		if (relation == TERuleRelation.NonEntailment) return new ArrayList<LexicalRule<? extends GermanTransDmInfo>>();

		isReverseMap = false;
		return getFromMap(lemma, pos, sims);
	}
	
	
	/** Returns a list of lexical rules whose right side (the target of the lexical relation) matches 
	 * the given lemma and POS. An empty list means that no rules were matched.
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermanTransDmInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		isReverseMap = true;
		return getFromMap(lemma, pos, reverse_sims);
	}
	
	/** An overloaded method for getRulesForRight. In addition to the previous method, 
	 * this method also matches the relation field of LexicalRule with the argument.
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */	
	public List<LexicalRule<? extends GermanTransDmInfo>> getRulesForRight(String lemma, PartOfSpeech pos, TERuleRelation relation) throws LexicalResourceException 
	{		
		// DistSim can only return Entailment
		if (relation == TERuleRelation.NonEntailment) return new ArrayList<LexicalRule<? extends GermanTransDmInfo>>();

		isReverseMap = true;
		return getFromMap(lemma, pos, reverse_sims);
	}
	
	
	/** This method returns a list of lexical rules whose left and right sides match the two given 
	 * pairs of lemma and POS.
	 * Note that the resulting List of LexicalRules can contain two entries with the same 
	 * similarity measure for the two given lemma-pos pairs (l1 on LHS and l2 on RHS, 
	 * and l2 on LHS and l1 on RHS).
	 * Note also that the values of these two entries of the same measure need not be identical (in
	 * case of asymmetrical measures like balapinc).
	 * 
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermanTransDmInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException
	{
		return this.getRules(leftLemma, leftPos, rightLemma, rightPos, TERuleRelation.Entailment);
	}
	
	
	/** 
	 * An overloaded method for getRules. In addition to the previous method, this method also matches the 
	 * relation field of LexicalRule with the argument.
	 * 
	 * This method returns a list of lexical rules whose left and right sides match the two given 
	 * pairs of lemma and POS for a specific the TeRuleRelation.
	 * 
	 * Note that the resulting List of LexicalRules can contain two entries with the same 
	 * similarity measure for the two given lemma-pos pairs (l1 on LHS and l2 on RHS, 
	 * and l2 on LHS and l1 on RHS).
	 * Note also that the values of these two entries of the same measure need not be identical (in
	 * case of asymmetrical measures like balapinc).
	 * 
	 * 
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermanTransDmInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, TERuleRelation relation) throws LexicalResourceException
	{		
		
        GermanPartOfSpeech rightPosShort = null;
        try {
	        if (rightPos.toString().startsWith("V")) {
	        	rightPosShort = new GermanPartOfSpeech ("V");
	        } else if (rightPos.toString().startsWith("N")) {
	        	rightPosShort = new GermanPartOfSpeech ("N");
	        } else if (rightPos.toString().equals("ADJ")) {
	        	rightPosShort = new GermanPartOfSpeech ("ADJ");
	        } 
	        // in all other cases, rightPosShort stays null! But then, there won't
	        // be information in the resource anyway.
	        
        } catch (UnsupportedPosTagStringException e) {
        }
                
		List<LexicalRule<? extends GermanTransDmInfo>> result = new ArrayList<LexicalRule<? extends GermanTransDmInfo>>();
		for (LexicalRule<? extends GermanTransDmInfo> rule : getRulesForLeft(leftLemma, leftPos, relation)) {
			// if rightPos is null, any lemma-matching result is accepted.
			// if rightPos is not null, the GermanPartOfSpeech-converted POS from the resource must match the
			//   short version of the rightPos type.
			if (rule.getRLemma().equals(rightLemma) && (rightPos == null || rule.getRPos().equals(rightPosShort))) {
				result.add(rule);
			}
		}	
		return result;
	}


	@Override
	public void close() throws LexicalResourceCloseException {
		this.sims = null;
		this.reverse_sims = null;
	}

}

