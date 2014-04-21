package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;

/**
 * Class which holds information from a DErivBase resource file.
 * DErivBase contains groups of lemmas, so-called derivational families,
 * which share a morphologic (and ideally a semantic) relationship, e.g.
 * "sleep, sleepy, to sleep, sleepless"
 * 
 * For details, please refer to Zeller et al. 2013, "DErivBase: Inducing 
 * and Evaluating a Derivational Morphology Resource for German"
 * 
 * DErivBase contains POS information for each lemma, and a reliability 
 * score for each lemma pair, computed by the length of the derivational
 * path between the two lemmas.    
 * 
 * @author zeller, kreutzer
 * @since March 2013 
 */
public class DerivBase {
	
	/**
	 * File which keeps the DErivBase resource.
	 */
	File file = null;
	
	/**
	 * Contains all entries of DErivBase, e.g.
	 * {[<abbrechen,v>, <<Abbruch,N>, <abgebrochen,A>>], [<Zündung,N>, <<zünden,V>, <gezündet,A>>],...}
	 * Note that each word which occurs in the Value's ArrayList also occurs 
	 * exactly once as Key of the HashMap. 
	 */
	HashMap<Tuple<String>, ArrayList<Tuple<String>>> entries;

	/**
	 * Each inner HashMap contains exactly one entry of one lemma-pos Tuple and a 
	 * score corresponding to this lemma-pos Tuple and the key lemma-pos Tuple of 
	 * the outer HashMap  
	 */
	HashMap<Tuple<String>, ArrayList<HashMap<Tuple<String>, Double>>> entryScores;
	
	

	/**
	 * Main constructor. If the resource should contain confidence scores for each lemma pair 
	 * within one family, the scores are calculated from the path length, if not, they are omitted.
	 * The minimum score a lemma pair within a family must achieve to be considered in the 
	 * created DerivBase object. 
	 * 
	 * @param useScores indicates if DerivBase file with scores should be used or not
	 * @param score indicates the minimum score a lemma pair must achieve to be considered 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws LexicalResourceException
	 */
	public DerivBase(boolean useScores, Double score) throws FileNotFoundException, IOException, LexicalResourceException {
		this.entries = new HashMap<Tuple<String>, ArrayList<Tuple<String>>>();
    	this.entryScores = new HashMap<Tuple<String>, ArrayList<HashMap<Tuple<String>, Double>>>();
        load("/derivbase/DErivBase-v1.4-rulePaths.txt", useScores, score);  
	}
	
	
    /**
     * Loads the data from a DErivBase into the data object. 
     * The input file has the following format:
     * lemma1_POS lemma2_POS pathLength lemma1_POS derivationalRule lemma2_POS
     * e.g. Abrechnen_Nn abrechnen_V 1 Abrechnen_Nn dNV09> abrechnen_V 
     * Each lemma pair within one derivational family is connected via one shortest derivational rule path.
     * From its length the score can be calculated. 
     * The user can decide whether to use the scores or not.
     * 
     * Every line is saved as an individual HashMap entry, where the key is a tuple of 
     * lemma and POS tag and the value is:
     * 1. an ArrayList of HashMaps, containing lemma-POS tuples and the corresponding score
     * 2. an ArrayList of lemma-POS tuples. 
     * 
     * Note that EITHER the "entries" field or the "entryScores" field are filled, but 
     * never both of them. 
     * If "entryScores" is filled, each lemma pair is assigned a confidence score, according
     * to the relatedness via rules in the DErivBase resource.
     * 
     * Note also that the scores for derivationally related pairs from DErivBase are converted 
     * to another value scale for EXCITEMENT: excitementscore = 0.5*derivbasescore + 0.5
     * Thus, the values for lemmas within the same derivational family which saved in this 
     * DerivBase object range always between 0.5 and 1. 
     * 
     * Attention: the gender information for nouns (e.g. Nm, Nf) will be omitted; we only
     * keep "N". Verbs similarly carry additional information: e.g. "Ven" for "en"-type verbs 
     * (i.e. ending on "en"), "Veln" or "Vern". This information will be omitted as well.
     * 
     * @param inFile the File containing the input DErivBase file
     * @param scoreInfo true if the file has score information, false otherwise
     * @param minScore indicates the minimum score which has to be achieved for a lemma pair to be counted 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws LexicalResourceException
     */
	private void load(String inFile, boolean scoreInfo, Double minScore) throws IOException, FileNotFoundException, LexicalResourceException {

		BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(inFile)));
		String line;

		System.out.print("Loading DErivBase... ");
        while ((line = br.readLine()) != null) {
        	//e.g. line:
        	//Abrechnen_Nn abrechnen_V 1 Abrechnen_Nn dNV09> abrechnen_V
        	String[] splittedLine = line.split(" ");
        	
    		int pathLength = Integer.parseInt(splittedLine[2]); 
    		
    		//rule paths are listed for each pair with their shortest path -> only first and last lemma are needed
    		String start = splittedLine[0]; //lemma_pos which is beginning of rule path    		
    		// omit ".substring(0, 1)" if you want to keep gender information.
    		Tuple<String> head = new Tuple<String>(start.split("_")[0], start.split("_")[1].substring(0, 1));
    		
    		String end = splittedLine[1]; //last lemma_pos
    		// omit ".substring(0, 1)" if you want to keep gender information.
    		Tuple<String> tail = new Tuple<String>(end.split("_")[0], end.split("_")[1].substring(0, 1));
    		
    		if (scoreInfo){
    			// DERIVBASE-INTERNAL CALCULATION, value range 0-1:
    			Double score = 1.0/pathLength; // = score
    			
        		ArrayList<HashMap<Tuple<String>, Double>> scoreTail1 = new ArrayList<HashMap<Tuple<String>, Double>>();
        		ArrayList<HashMap<Tuple<String>, Double>> scoreTail2 = new ArrayList<HashMap<Tuple<String>,Double>>(); //pairs are only mentioned once in file -> two entries, one for each lemma_pos
        		
        		if (score >= minScore) { // save pair only if score has at least defined value.
        			// EXCITEMENT-CONFORMANT RECALCULATION, value range 0.5-1:
        			score = (0.5 * score) + 0.5; // = score
        			if (this.entryScores.containsKey(head)){
	    				scoreTail1 = this.entryScores.get(head);
	    			}
        			if (this.entryScores.containsKey(tail)){
        				scoreTail2 = this.entryScores.get(tail);
        			}
	        		HashMap<Tuple<String>, Double> s1 = new HashMap<>();
	        		HashMap<Tuple<String>, Double> s2 = new HashMap<>();
	        		s1.put(tail, score);
	        		s2.put(head, score);
	        		scoreTail1.add(s1);
	        		scoreTail2.add(s2);
	        		this.entryScores.put(head, scoreTail1); //first entry for head
	        		this.entryScores.put(tail, scoreTail2); //second entry for tail
        		}
    		}
    		else {
    			ArrayList<Tuple<String>> tailList1 = new ArrayList<Tuple<String>>();
    			ArrayList<Tuple<String>> tailList2 = new ArrayList<Tuple<String>>();
    			if (this.entries.containsKey(head)){
    				tailList1 = this.entries.get(head);
    			}
    			if (this.entries.containsKey(tail)){
    				tailList2 = this.entries.get(tail);
    			}
    			tailList1.add(tail);
    			tailList2.add(head);
    			this.entries.put(head, tailList1);
    			this.entries.put(tail, tailList2);
    		}
    		
        }
        br.close();
        System.out.println("done.");
		
	}

	
	/**
	 * Seeks for the list of related lemma-POS pairs for a given input lemma-POS pair.
	 * 
	 * @param lemma The lemma for which related words are searched
	 * @param pos The respective POS tag 
	 * @return an ArrayList of related lemma-POS tuples
	 */
	
	public ArrayList<Tuple<String>> getRelatedLemmaPosPairs(String lemma, String pos) {
		Tuple<String> query = new Tuple<String>(lemma,pos); 
		if (this.entries.keySet().contains(query)) {
			return this.entries.get(query);
		} else {
			return new ArrayList<Tuple<String>>();
		}
	}
	
	public ArrayList<HashMap<Tuple<String>,Double>> getRelatedLemmaPosPairsWithScore(String lemma, String pos) {
		Tuple<String> query = new Tuple<String>(lemma,pos); 
		if (this.entryScores.keySet().contains(query)) {
			return this.entryScores.get(query);
		} else {
			return new ArrayList<HashMap<Tuple<String>, Double>>();
		}
	}


	public HashMap<Tuple<String>, ArrayList<HashMap<Tuple<String>, Double>>> getEntryScores() {
		return entryScores;
	}

	public void setEntryScores(
			HashMap<Tuple<String>, ArrayList<HashMap<Tuple<String>, Double>>> entryScores) {
		this.entryScores = entryScores;
	}
	
	public HashMap<Tuple<String>, ArrayList<Tuple<String>>> getEntries() {
		return entries;
	}

	public void setEntries(HashMap<Tuple<String>, ArrayList<Tuple<String>>> entries) {
		this.entries = entries;
	}
	
	@Override
	public String toString() {
		if (!entries.isEmpty()) 
			return "DErivBase [entries=" + entries + "]";
		else if (!entryScores.isEmpty())
			return "DErivBase [entryScores=" + entryScores + "]";
		else
			return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DerivBasePairs other = (DerivBasePairs) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}
	

}
