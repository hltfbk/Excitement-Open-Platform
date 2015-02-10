package eu.excitementproject.eop.adarte;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * This class represents a fragment of text, i.e. the tokens contained in the hypothesis H or text T.
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since January 2015
*/
public class Fragment {
	
	private ArrayList<FToken> tokens;
	
	
	/**
	 * The constructor
	 */
	protected Fragment() {
		
		this.tokens= new ArrayList<FToken>();
		
	}
	
	
	/**
	 * The constructor
	 */
	protected Fragment(ArrayList<FToken> tokens) {
		
		this.tokens=tokens;
		
	}
	
	
	/**
     * This method accepts in input a tree (it has been produced by cas2CoNLLX
     * and it is in CoNLL-X format) and returns a fragment containing all the tokens in the tree
     * 
     * @param dependencyTree
     * 
     * @return the fragment
     *
     * @throws Exception
     */
    public Fragment(String dependencyTree) throws Exception {
    	
    	this();
    	
    	/* here we need to parse the tree CoNLLX format (i.e. dependencyTree)
    	/ and for each line of it we would need to create an object of the class Token
    	/ and put it into the Fragment
    	*/
    	try {
    		
	    	String[] lines = dependencyTree.split("\n");
	    	
	    	for (int i = 0; i < lines.length; i++) {
	    		String[] fields = lines[i].split("\\s");
	    		int tokenId = Integer.parseInt(fields[0]) - 1;
	    		String form = fields[1];	
	    		String lemma = fields[2];	
	    		String pos = fields[3];	
	    		
	    		int head;
	    		if (fields[6].equals("_")) {
	    			head = -1;
	    		}
	    		else
	    			head = Integer.parseInt(fields[6]) - 1;
	    			
	    		String deprel = fields[7];
	    	    //and for each line of it we would need to create an object of the class FToken
	    	    //and then put it into the Fragment
	    		FToken token_i = new FToken(tokenId, form, lemma, pos, head, deprel);
	    		addToken(token_i);
	    	}
	    	
	    } catch (Exception e) {
    		
    		throw new Exception(e.getMessage());
    		
    	}
    	
    }
    
	
	/**
	 * Get the token with id tokenId
	 * 
	 * @param tokenId the token id
	 * 
	 * @return the token
	 */
	protected FToken getToken(int tokenId) {
		
		return tokens.get(tokenId-1);
		
	}
	
	
	/**
	 * Get the number of tokens
	 * 
	 * @return the number of tokens in the fragment
	 */
	protected int size() {
		
		return tokens.size();
		
	}
	
	
	/**
	 * 
	 * Add a new token into the fragment
	 * 
	 * @param the token to be added
	 */
	public void addToken (FToken token) {
		
		this.tokens.add(token);
		
	}
	
	
	/**
	 * Get an iterator over the list of tokens in the fragment
	 * 
	 * @return the iterator
	 */
	public Iterator<FToken> getIterator() {
		
		return tokens.iterator();
		
	}
	
	
	/**
	 * Print the list of the tokens in the fragment
	 * 
	 * @return the list of the tokens
	 */
	public String toString() {
		
		String frg = "";
		for(FToken token:tokens){
			frg = frg + "\n" + token.toString();
		}
		
		return frg;
		
	}
		

}

