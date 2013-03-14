package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

// Component imports
import eu.excitementproject.eop.common.component.Component;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.configuration.CommonConfig;
//import eu.excitementproject.eop.common.configuration.NameValueTable;
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
//import java.util.Iterator;

/**
 * This class implements a German Lexical Resource based on derivational information, 
 * DErivBase 1.3. The implementation accesses the major resource file directly, which 
 * contains lemmas and their corresponding derivations. Each lemma is considered as 
 * entailing as well as being entailed by its corresponding derivations. 
 * 
 * <P>
 * The implementation supports LexicalResource, but not LexicalResourceWithRelation.
 * 
 * <P> For a later version, the implementation will additionally offer:
 * <li> scores for each lemma pair</li>
 * <li> unspecified POS tags (i.e. "null")</li> 
 *  
 * <P>
 * If DErivBase is not found, the component will raise an exception and will not be 
 * initialized. 
 * 
 * @author Britta Zeller 
 * @since Mar 2013
 */

public class DerivBaseResource implements Component, LexicalResource<DerivBaseInfo> {

	/** confidences; not yet used, tbd in later version.*/
	//private final Map<Enum<?>, Double> CONFIDENCES = new HashMap<Enum<?>, Double>();

	/** DerivBase object. */
	private DerivBase derivbase;

	
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
	 * @param config		Configuration for the GermaNetWrapper instance
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public DerivBaseResource(CommonConfig config) throws ConfigurationException, ComponentException {
		this(config.getSection("DerivBaseResource").getString("derivBaseFilePath"),
				config.getSection("DerivBaseResource").getDouble("confidences"));
	}
	
	/**
	 * Creates a new DerivBaseResource instance, and initializes the instance
	 * (basically loads DerivBase file into memory).
	 * Sets the default value 1.0 as confidence value for all relations.
	 * 
	 * @param germaNetFilesPath			Path to GermaNet XML files
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public DerivBaseResource(String derivBaseFilesPath) throws ConfigurationException, ComponentException {
		this(derivBaseFilesPath, 1.0);
	}
	
	/**
	 * Creates a new DerivBaseResource instance, and initializes the instance
	 * (basically loads DerivBase file into memory).
	 * 
	 * @param dbasePath		Path to DerivBase file
	 *@param confidences	LATER: add scores.
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public DerivBaseResource(String dbasePath, Double confidences) throws ConfigurationException, ComponentException {
		try {
			this.derivbase = new DerivBase(dbasePath);
		}
		catch (java.io.FileNotFoundException e) {
			throw new DerivBaseNotInstalledException("Path to DErivBase is not correct.", e);
		}
		catch (java.lang.Exception e) {
			throw new ComponentException("Cannot initialize DErivBase. Please check the path" 
					+ " and the format of your DErivBase version. "
					+ "Should be: \nlemma_pos: relatedl1_p1 relatedl2_p2 ...", e);
		}
				
		/*if (confidences == null)
			CONFIDENCES.put(ConRel.causes, 0.0);
		else
			CONFIDENCES.put(ConRel.causes, confidences);
			*/
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
	 * the given lemma and POS. An empty list means that no rules were matched. If the user 
	 * gives null POS, the class will retrieve rules for all possible POSes.
	 * @param lemma Lemma to be matched on LHS. 
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends DerivBaseInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		// using a set makes the result unique
		Set<LexicalRule<? extends DerivBaseInfo>> result = new HashSet<LexicalRule<? extends DerivBaseInfo>>();
		//List<LexicalRule<? extends DerivBaseInfo>> result = new ArrayList<LexicalRule<? extends DerivBaseInfo>>();
		
		// check POS is valid or not for DerivBase. Note that DerivBase only has noun, verb, and adjective.
		// for POSes unknown to DerivBase, no need to look up: return an empty list.
		//TODO: if necessary, change later to "pos != null &&" and add logic for unspecified POS 
		if (pos == null || !isValidPos(pos)){  
			return new ArrayList<LexicalRule<? extends DerivBaseInfo>>(result);
		}
		
		// convert incoming POS into DErivBase POS: N, A, V
		String derivbasePos = pos.toString().substring(0, 1);

		// get related lemma-POS pairs and add them to results LexicalRule
		ArrayList<Tuple<String>> related = new ArrayList<Tuple<String>>();
		related = derivbase.getRelatedLemmaPosPairs(lemma, derivbasePos);
		
		// if nothing found for this lemma-pos pair: return empty list
		if (related.size() == 0) {
			return new ArrayList<LexicalRule<? extends DerivBaseInfo>>(result);
		}
		
		for (Tuple<String> relatedPair: related) {
			String relatedLemma = relatedPair.getA();
			PartOfSpeech relatedPos = null;
			try {
				// TODO: is it problematic for calling functions that getB 
				// returns only N,V,A, but not NN?
				
				// enlarge "A" to "ADJ" for GermanPartOfSpeech lookup
				if (relatedPair.getB().equals("A")) {
					relatedPos = new GermanPartOfSpeech("ADJ");
				} else {
					relatedPos = new GermanPartOfSpeech(relatedPair.getB());
				}
			} catch (UnsupportedPosTagStringException e) {
				System.err.println("Problems converting the DErivBase POS string " 
						+ relatedPair.getB() + " to GermanPartOfSpeech.");
				e.printStackTrace();
			}
			
			LexicalRule<? extends DerivBaseInfo> rule = new LexicalRule<DerivBaseInfo>(lemma, pos, 
					relatedLemma, relatedPos, "derivational", "DErivBase", new DerivBaseInfo());
			
			result.add(rule);	
		}

		//TODO: the following check is only necessary when we use confidence scores!
		/*
		// Gil: check all result, and remove any 0 confidence value 
		Iterator<LexicalRule<? extends DerivBaseInfo>> i = result.iterator(); 
		while (i.hasNext()) {
			LexicalRule<? extends DerivBaseInfo> rule = i.next(); 
			if (rule.getConfidence() == 0 ) // 0 confidence 
				i.remove(); 
		}
		*/
		
		return new ArrayList<LexicalRule<? extends DerivBaseInfo>>(result);
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
					rule.getLLemma(), rule.getLPos(), "derivational", "DErivBase", new DerivBaseInfo()));
		}
		return result;
	}

	
	
	/** This method returns a list of lexical rules whose left and right sides match the two given pairs of lemma and POS.
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
			/*//TODO: is this security check necessary?
			if (rule.getRLemma().equals(rightLemma)) {
				if (rule.getRPos().equals(rightPos) 
						|| (rule.getRPos().toString().equals("NN") && rightPos.toString().equals("N"))
						|| (rule.getRPos().toString().equals("N") && rightPos.toString().equals("NN"))) {*/
					ArrayList<LexicalRule<? extends DerivBaseInfo>> result = new ArrayList<LexicalRule<? extends DerivBaseInfo>>();
					result.add(rule);
					return new ArrayList<LexicalRule<? extends DerivBaseInfo>>(result);
				//}
			//}
		}
		
		return new ArrayList<LexicalRule<? extends DerivBaseInfo>>();
	}


	@Override
	public void close() throws LexicalResourceCloseException {
		
	}

}

