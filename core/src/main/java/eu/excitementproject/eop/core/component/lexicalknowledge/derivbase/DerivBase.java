package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;

/**
 * Class which holds information from a DErivBase resource file.
 * 
 * @author zeller
 *
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
	 * Constructor for file path as String.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws LexicalResourceException
	 */
	public DerivBase(String path) throws FileNotFoundException, IOException, LexicalResourceException {
		this(new File(path));
	}
	
	/**
	 * Constructor for file path as File.
	 * Loads the data from the file.
	 * 
	 * @param inFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws LexicalResourceException
	 */
    public DerivBase(File inFile) throws FileNotFoundException, IOException, LexicalResourceException {
    	this.entries = new HashMap<Tuple<String>, ArrayList<Tuple<String>>>();

    	file = inFile;
    	
        if (file.isDirectory()) {
        	throw new LexicalResourceException("Please indicate the DErivBase resource file, not a directory.");
        } else {
            this.file = inFile;
        }

        load(inFile);        
    }
    
    
    /**
     * Loads the data from the DErivBase file into the data object. 
     * Every line is saved as an individual HashMap entry, where the key is a tuple of 
     * lemma and POS tag and the value is an ArrayList of lemma-POS tuples.
     * Attention: the gender information for nouns (e.g. Nm, Nf) will be omitted; we only
     * keep "N".
     * 
     * @param inFile
     * @throws IOException
     * @throws FileNotFoundException
     * @throws LexicalResourceException
     */
	private void load(File inFile) throws IOException, FileNotFoundException, LexicalResourceException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
        String line;

		System.out.print("Loading DErivBase... ");
        while ((line = br.readLine()) != null) {
        
    		String headString = line.split(":")[0];
    		ArrayList<String> tailList = new ArrayList<String>(Arrays.asList(line.split(": ")[1].split(" ")));
    		
    		// omit ".substring(0, 1)" if you want to keep gender information.
    		Tuple<String> head = new Tuple<String>(headString.split("_")[0], headString.split("_")[1].substring(0, 1));
    		ArrayList<Tuple<String>> tail = new ArrayList<Tuple<String>>();
    		
    		for (String t : tailList) {
    			// omit ".substring(0, 1)" if you want to keep gender information.
    			Tuple<String> tailElement = new Tuple<String>(t.split("_")[0], t.split("_")[1].substring(0, 1));
    			tail.add(tailElement);
    		}
    		this.setSingleEntry(head, tail);
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
	public void setSingleEntry(Tuple<String> head, ArrayList<Tuple<String>> tailEntries) throws LexicalResourceException {
		if (this.entries.containsKey(head)) {
			throw new LexicalResourceException("Failure: entry for " + head + " already exists.");
		}
		this.entries.put(head, tailEntries);
	}
	
	
	
	
	
	@Override
	public String toString() {
		return "DerivBase [entries=" + entries + "]";
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
		DerivBase other = (DerivBase) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}
	
	
	

}
