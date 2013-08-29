
package eu.excitementproject.eop.core.component.lexicalknowledge.dewakdistributional;

// Component imports
import eu.excitementproject.eop.common.component.Component;
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

// LexicalResource imports


// other imports
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
//import java.io.File;
//import java.io.FileFilter;
//import java.io.FileInputStream;
//import java.io.InputStream;

/**
 * This class implements a German lexical resource based on corpus term distribution.  
 * It uses the distance vectors that is gathered from DeWac, on 10k most frequent 
 * German words observed in the corpus. 
 * 
 * @author Jan Pawellek 
 * @since Nov. 2012 
 */
public class GermanDistSim implements Component, LexicalResource<GermanDistSimInfo> {

	/** Stores similarity values: measurename -&gt; LHS word -&gt; RHS word -&gt; similarityvalue */
	private Map<String, Map<String, Map<String, Float>>> sims = new HashMap<String, Map<String, Map<String, Float>>>();

	/** Stores similarity values: measurename -&gt; RHS word -&gt; LHS word -&gt; similarityvalue */
	private Map<String, Map<String, Map<String, Float>>> reverse_sims = new HashMap<String, Map<String, Map<String, Float>>>();

	/**
	 * Creates a new GermanDistSim instance, and initializes the instance
	 * (basically loads similarity files into memory).
	 * 
	 * @param config		Configuration for the GermanDistSim instance
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	public GermanDistSim(CommonConfig config) throws ConfigurationException, ComponentException {
		this(); // GermanDistSim no longer supports using "different" files. we only use 10k data now. 
//		this(config.getSection("GermanDistSim").getString("similarityFilesPath"));
//		this(config.getSection(getComponentName()).getString("similarityFilesPath"));
	}
	
	
	public GermanDistSim() throws ConfigurationException
	{
		
		// load up the resource into memory. 		
//		  drwxr-xr-x  10 tailblues  staff     340 Apr 28 16:02 ..
//		  -rw-r--r--   1 tailblues  staff  641983 Apr 28 16:02 sdewac.bow.s10k.d10k.freq.w10.sim.balapinc
//		  -rw-r--r--   1 tailblues  staff  133724 Apr 28 16:02 sdewac.bow.s10k.d10k.freq.w10.sim.dice
//		  -rw-r--r--   1 tailblues  staff  157370 Apr 28 16:02 sdewac.bow.s10k.d10k.freq.w10.sim.jaccard
//		  -rw-r--r--   1 tailblues  staff  894564 Apr 28 16:02 sdewac.bow.s10k.d10k.freq.w10.sim.lin
//		  -rw-r--r--   1 tailblues  staff  894564 Apr 28 16:02 sdewac.bow.s10k.d10k.freq.w10.sim.linOpt

		String[] listResource = new String[5]; 
		listResource[0] = "/dewakdistributional-data-10k/sdewac.bow.s10k.d10k.freq.w10.sim.balapinc"; 
		listResource[1] = "/dewakdistributional-data-10k/sdewac.bow.s10k.d10k.freq.w10.sim.dice"; 
		listResource[2] = "/dewakdistributional-data-10k/sdewac.bow.s10k.d10k.freq.w10.sim.jaccard"; 
		listResource[3] = "/dewakdistributional-data-10k/sdewac.bow.s10k.d10k.freq.w10.sim.lin"; 
		listResource[4] = "/dewakdistributional-data-10k/sdewac.bow.s10k.d10k.freq.w10.sim.linOpt"; 
		
//		InputStream s = this.getClass().getResourceAsStream("/desc/DummyAE.xml"); // This AE does nothing, but holding all types.

		try {
			for (String simresource : listResource) {
				Scanner scanner = new Scanner(this.getClass().getResourceAsStream(simresource), "UTF-8");
				Map<String, Map<String, Float>> maps = new HashMap<String, Map<String, Float>>();
				Map<String, Map<String, Float>> reverse_maps = new HashMap<String, Map<String, Float>>();

				while (scanner.hasNextLine()) {
					String[] parts = scanner.nextLine().split("\\s");
					// split er|sie|es lemmas
					for (String lhspart : parts[0].split("\\|")) {
						String lhs = lhspart.intern();
						for (String rhspart : parts[1].split("\\|")) {
							String rhs = rhspart.intern();
							Float similarity = Float.parseFloat(parts[2]);
							if (!maps.containsKey(lhs)) maps.put(lhs, new HashMap<String, Float>());
							maps.get(lhs).put(rhs, similarity);

							if (!reverse_maps.containsKey(rhs)) reverse_maps.put(rhs, new HashMap<String, Float>());
							reverse_maps.get(rhs).put(lhs, similarity);
						}
					}
				}
				sims.put(simresource.substring(simresource.lastIndexOf('.') + 1), maps);
				reverse_sims.put(simresource.substring(simresource.lastIndexOf('.') + 1), reverse_maps);
				scanner.close();
			}
		}
		catch (java.lang.Exception e) {
			throw new GermanDistSimNotInstalledException("Cannot load similarity file: " + e.getMessage(), e);
		}

	}
	
	/**
	 * Creates a new GermanDistSim instance, and initializes the instance
	 * (basically loads similarity files into memory).
	 * 
	 * @param similarityFilesPath		Path to similarity files.
	 * @throws ConfigurationException
	 */
	@Deprecated 
	public GermanDistSim(String similarityFilesPath) throws ConfigurationException {
		
		// We no longer give the choice of "resource". 
		// This resource now only loads 10k fixed data. That's why we ignore the string and only calls this(). 
		this(); 
		
//		// Read all similarity files
//		FileFilter filter = new FileFilter() {
//			public boolean accept(File f) {
//				return !f.isDirectory() && f.canRead() && f.getPath().contains(".sim.");
//			}
//		};
//		
//		File[] simfiles = (new File(similarityFilesPath)).listFiles(filter); 
//
//		try {
//			for (File simfile : simfiles) {
//				Scanner scanner = new Scanner(new FileInputStream(simfile), "UTF-8");
//				Map<String, Map<String, Float>> maps = new HashMap<String, Map<String, Float>>();
//				Map<String, Map<String, Float>> reverse_maps = new HashMap<String, Map<String, Float>>();
//
//				while (scanner.hasNextLine()) {
//					String[] parts = scanner.nextLine().split("\\s");
//					// split er|sie|es lemmas
//					for (String lhspart : parts[0].split("\\|")) {
//						String lhs = lhspart.intern();
//						for (String rhspart : parts[1].split("\\|")) {
//							String rhs = rhspart.intern();
//							Float similarity = Float.parseFloat(parts[2]);
//							if (!maps.containsKey(lhs)) maps.put(lhs, new HashMap<String, Float>());
//							maps.get(lhs).put(rhs, similarity);
//
//							if (!reverse_maps.containsKey(rhs)) reverse_maps.put(rhs, new HashMap<String, Float>());
//							reverse_maps.get(rhs).put(lhs, similarity);
//						}
//					}
//				}
//				sims.put(simfile.getPath().substring(simfile.getPath().lastIndexOf('.') + 1), maps);
//				reverse_sims.put(simfile.getPath().substring(simfile.getPath().lastIndexOf('.') + 1), reverse_maps);
//				scanner.close();
//			}
//		}
//		catch (java.lang.Exception e) {
//			throw new GermanDistSimNotInstalledException("Cannot read similarity file: " + e.getMessage(), e);
//		}
	}
	
	/**
	 * This method provides the (human-readable) name of the component. It is used to 
	 * identify the relevant section in the common configuration for the current component. 
	 * See Spec Section 5.1.2, "Overview of the common configuration " and Section 4.9.3, 
	 * "Component name and instance name".
	 */
	public String getComponentName()
	{
		return "GermanDistSim"; // TODO: change to some official name
	}
	
	
	/** This method provides the (human-readable) name of the instance. It is used to 
	 * identify the relevant subsection in the common configuration for the current component. 
	 * See Spec Section 5.1.2, "Overview of the common configuration " and Section 4.9.3, 
	 * "Component name and instance name". Note that this method can return null value, if 
	 * and only if all instances of the component shares the same configuration.
	 */
	public String getInstanceName() {
		return null; // TODO: change 
        }
  
	/** Returns all rules that can be derived from a given lemma. Depending on the kind of map
	* provided as second argument, this returns either LHS or RHS rule derivations.
	* @param lemma Lemma to be matched.
	* @param map Map containing rules.
	* @return List containing all derivable rules.
	*/
	private List<LexicalRule<? extends GermanDistSimInfo>> getFromMap(String lemma, Map<String, Map<String, Map<String, Float>>> map) throws LexicalResourceException
	{
		// using a set makes the result unique
                Set<LexicalRule<? extends GermanDistSimInfo>> result = new HashSet<LexicalRule<? extends GermanDistSimInfo>>();

		// convert cardinals to @card@, ordinals to @ord@
		if (lemma.matches("\\d+[.]")) lemma = "@ord@";
		if (lemma.matches("\\d+")) lemma = "@card@";
		lemma = lemma.intern();

		for (String measure : map.keySet()) {
			if (map.get(measure).containsKey(lemma)) {
				for (String rhs : map.get(measure).get(lemma).keySet()) {
					float score = map.get(measure).get(lemma).get(rhs);
					if (measure.equals("hindle")) {
						score = Float.parseFloat("0." + Float.toString(score).replace(".", ""));
					}
					// returns OTHER as POS info since we don't know anything about POS
					GermanPartOfSpeech pos1 = null; 
					GermanPartOfSpeech pos2 = null; 
					try {
						pos1 = new GermanPartOfSpeech("OTHER");
						pos2 = new GermanPartOfSpeech("OTHER");
					}
					catch (UnsupportedPosTagStringException e) {
						// pos stay null
					}

					LexicalRule<? extends GermanDistSimInfo> lexrule = new LexicalRule<GermanDistSimInfo>(lemma, pos1, rhs, pos2, score, measure, "GermanDistSim", new GermanDistSimInfo());
					result.add(lexrule);
                                }
                        }
                }

                return new ArrayList<LexicalRule<? extends GermanDistSimInfo>>(result);
	}

	/**
	 * Returns a list of lexical rules whose left side (the head of the lexical relation) matches
	 * the given lemma and POS. An empty list means that no rules were matched. If the user 
	 * gives null POS, the class will retrieve rules for all possible POSes.
	 * @param lemma Lemma to be matched on LHS. 
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermanDistSimInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		return getFromMap(lemma, sims);
	}
	
	
	/**an overloaded method for getRulesForLeft. In addition to the previous method, this method 
	 * also matches the relation field of LexicalRule with the argument.
	 * @param lemma Lemma to be matched on LHS
	 * @param pos POS to be matched on LHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return A list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermanDistSimInfo>> getRulesForLeft(String lemma, PartOfSpeech pos, TERuleRelation relation) throws LexicalResourceException
	{
		// DistSim can only return Entailment
		if (relation == TERuleRelation.NonEntailment) return new ArrayList<LexicalRule<? extends GermanDistSimInfo>>();

		return getFromMap(lemma, sims);
	}
	
	
	/** Returns a list of lexical rules whose right side (the target of the lexical relation) matches 
	 * the given lemma and POS. An empty list means that no rules were matched.
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */
	public List<LexicalRule<? extends GermanDistSimInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException
	{
		return getFromMap(lemma, reverse_sims);
	}
	
	/** An overloaded method for getRulesForRight. In addition to the previous method, 
	 * this method also matches the relation field of LexicalRule with the argument.
	 * @param lemma Lemma to be matched on RHS. 
	 * @param pos POS to be matched on RHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return a list of rules that matches the given condition. Empty list if there's no match. 
	 */	
	public List<LexicalRule<? extends GermanDistSimInfo>> getRulesForRight(String lemma, PartOfSpeech pos, TERuleRelation relation) throws LexicalResourceException
        {
		if (relation == TERuleRelation.NonEntailment) return new ArrayList<LexicalRule<? extends GermanDistSimInfo>>();
		return getFromMap(lemma, reverse_sims);
        }
	
	
	/** This method returns a list of lexical rules whose left and right sides match the two given pairs of lemma and POS.
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermanDistSimInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException
	{
                return this.getRules(leftLemma, leftPos, rightLemma, rightPos, TERuleRelation.Entailment);
	}
	
	
	/** An overloaded method for getRules. In addition to the previous method, this method also matches the relation field of LexicalRule with the argument.
	 * @param leftLemma Lemma to be matched on LHS
	 * @param leftPos POS to be matched on LHS. null means "don't care". 
	 * @param rightLemma Lemma to be matched on RHS. 
	 * @param rightPos POS to be matched on RHS. null means "don't care". 
	 * @param relation The canonical relation of the rule (from LHS to RHS, TERuleRelation.Entailment or .Nonentailment)
	 * @return a list of rules that matches the given condition. Empty list if there's no match.
	 */
	public List<LexicalRule<? extends GermanDistSimInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, TERuleRelation relation) throws LexicalResourceException
	{
		// convert cardinals to @card@, ordinals to @ord@
                if (leftLemma.matches("\\d+[.]")) leftLemma = "@ord@";
                if (leftLemma.matches("\\d+")) leftLemma = "@card@";
                if (rightLemma.matches("\\d+[.]")) rightLemma = "@ord@";
                if (rightLemma.matches("\\d+")) rightLemma = "@card@";

		List<LexicalRule<? extends GermanDistSimInfo>> result = new ArrayList<LexicalRule<? extends GermanDistSimInfo>>();
		for (LexicalRule<? extends GermanDistSimInfo> rule : getRulesForLeft(leftLemma, leftPos, relation)) {
			if (rule.getRLemma().equals(rightLemma) && (rightPos == null || rule.getRPos().equals(rightPos))) {
				result.add(rule);
			}
		}
		return result;
	}

	@Override
	public void close() throws LexicalResourceCloseException
	{
		// TODO Auto-generated method stub
		
	}

}

