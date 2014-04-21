package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * @author zeller
 * @since March 2013 
 */
public class DerivBasePairs {
	
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
	 * within one family, it loads a score-containing file, otherwise a file without scores.
	 * The minimum score a lemma pair within a family must achieve to be considered in the 
	 * created DerivBase object. 
	 * 
	 * @param useScores indicates if DerivBase file with scores should be used or not
	 * @param score indicates the minimum score a lemma pair must achieve to be considered 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws LexicalResourceException
	 */
	public DerivBasePairs(boolean useScores, Double score) throws FileNotFoundException, IOException, LexicalResourceException {
		
		if (useScores) { 
			this.entries = new HashMap<Tuple<String>, ArrayList<Tuple<String>>>();
	    	this.entryScores = new HashMap<Tuple<String>, ArrayList<HashMap<Tuple<String>, Double>>>();
	        load("/derivbase/DErivBase-v1.3-pairs.txt", useScores, score);  
	        
		} else {
			this.entries = new HashMap<Tuple<String>, ArrayList<Tuple<String>>>();
	    	this.entryScores = new HashMap<Tuple<String>, ArrayList<HashMap<Tuple<String>, Double>>>();
	    	load("/derivbase/DErivBase-v1.3-pairsWithoutScore.txt", useScores, score);
		}
	}
	
	
	
    /**
     * Loads the data from a DErivBase into the data object. 
     * The input file can have two different formats: 
     * 1. containing scores for each lemma pair within one derivational family; example:
     *    Aalener_Nm: Aalen_Nn 1.00 aalen_V 0.50 Aal_Nn 0.33 
     * 2. simply containing derivational families without information about lemma pair 
     *    confidences; example:
     *    Aalener_Nm: Aalen_Nn aalen_V Aal_Nn 
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
     * keep "N".
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
        
    		String headString = line.split(":")[0];
    		ArrayList<String> tailList = new ArrayList<String>(Arrays.asList(line.split(": ")[1].split(" ")));
    		
    		// omit ".substring(0, 1)" if you want to keep gender information.
    		Tuple<String> head = new Tuple<String>(headString.split("_")[0], headString.split("_")[1].substring(0, 1));

        	
    		ArrayList<HashMap<Tuple<String>, Double>> scoreTail = new ArrayList<HashMap<Tuple<String>, Double>>();
    		ArrayList<Tuple<String>> tail = new ArrayList<Tuple<String>>();
    		
        	if (scoreInfo) { // if the infile contains scores: load scorer-based format
        		
        		int i = 1;
        		Tuple<String> tailElement = new Tuple<String>() ; // = <lemma, pos>
        		
        		
        		for (String t : tailList) {
        			
        			if (i % 2 == 1) { // for lemmas in the family queue
            			// omit ".substring(0, 1)" if you want to keep gender information.
            			tailElement = new Tuple<String>(t.split("_")[0], t.split("_")[1].substring(0, 1));
            			
        			} else { // for scores in the family queue
        				// DERIVBASE-INTERNAL CALCULATION, value range 0-1:
            			Double score = Double.parseDouble(t); // = score
            			
            			if (score >= minScore) { // save pair only if score has at least defined value.
            				HashMap<Tuple<String>, Double> s = new HashMap<>();
            				// EXCITEMENT-CONFORMANT RECALCULATION, value range 0.5-1:
                			score = (0.5 * score) + 0.5; // = score
                			
                			s.put(tailElement, score);
                			
                			scoreTail.add(s);
            			}
            			
            			tailElement = new Tuple<String>();
        			}
        			i++;
        		}
        		this.setSingleEntryScore(head, scoreTail);
        		
        		
        	} else { // else if infile contains no scores: load base format
        		
        		for (String t : tailList) {
        			// omit ".substring(0, 1)" if you want to keep gender information.
        			Tuple<String> tailElement = new Tuple<String>(t.split("_")[0], t.split("_")[1].substring(0, 1));
        			tail.add(tailElement);
        		}
        		this.setSingleEntry(head, tail);
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
	
	public ArrayList<Tuple<String>> getSingleEntry(Tuple<String> head) {
		if (this.entries.containsKey(head)) {
			return entries.get(head);			
		} else {
			return null;
		}		
	}
	

	public ArrayList<HashMap<Tuple<String>, Double>> getSingleEntryScore(Tuple<String> head) {
		if (this.entryScores.containsKey(head)) {
			return entryScores.get(head);			
		} else {
			return null;
		}		
	}
	
	
	
	public void setSingleEntry(Tuple<String> head, ArrayList<Tuple<String>> tailEntries) throws LexicalResourceException {
		if (this.entries.containsKey(head)) {
			throw new LexicalResourceException("Failure: entry for " + head + " already exists.");
		}
		this.entries.put(head, tailEntries);
	}
	

	public void setSingleEntryScore(Tuple<String> head, ArrayList<HashMap<Tuple<String>, Double>> 
			tailEntries) throws LexicalResourceException {
		
		if (this.entryScores.containsKey(head)) {
			throw new LexicalResourceException("Failure: entry for " + head + " already exists.");
		}
		this.entryScores.put(head, tailEntries);
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
