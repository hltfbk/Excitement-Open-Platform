package eu.excitementproject.eop.adarte;

/**
 * This class represents a token with the information that is available in the
 * CoNLL-X file produced by the parser, e.g. token id, lemma, form, pos, head, dprel
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since January 2015
 */
public class FToken {

    	//ID Token counter, starting at 1 for each new sentence.
    	private int id;
    	//FORM 	Word form or punctuation symbol.
    	private String form;
    	//LEMMA Lemma or stem (depending on particular data set) of word form, or an underscore if not available. 
    	private String lemma;
    	//STEM; not in CoNLL
    	//private String stem;
    	//PoS
    	private String pos;
    	//HEAD 	Head of the current token, which is either a value of ID or zero ('0'). 
     	//Note that depending on the original treebank annotation, there may be multiple tokens with an ID of zero.
    	private int head;
    	//DPREL Dependency relation to the HEAD. The set of dependency relations depends on the particular language. 
    	//Note that depending on the original treebank annotation, the dependency relation may be meaningful or simply 'ROOT'.
    	private String dprel;
    	//dprel relations from the current token to the root
    	private String dprelRelations;
    	
    	/**
    	 * constructor
    	 * 
    	 * @param id the token id
    	 * @param form the token form
    	 * @param lemma the token lemma
    	 * @param pos the token pos
    	 * @param head the token head
    	 * @param dprel the token dependency relation to the HEAD
    	 * 
    	 */
    	public FToken(int id, String form, String lemma, String pos, int head, String dprel) {
    		
    		this.id = id;
    		this.form = form;
    		this.lemma= lemma;
    		this.pos = pos;
    		//this.stem = stem;
    		this.head = head;
    		this.dprel = dprel;
    		this.dprelRelations = null;
    		if (this.lemma.equals("no"))
    			this.dprel = "neg";
    		
    	}
    	
    	
    	/**
    	 * 
    	 * Get the token id
    	 * 
    	 * @return the token id
    	 */
    	public int getId() {
    		
    		return this.id;
    		
    	}
    	
    	
    	/**
    	 * 
    	 * Get the form of the token
    	 * 
    	 * @return the form
    	 */
    	public String getForm() {
    		
    		return this.form;
    		
    	}
    	
    	
    	/**
    	 * 
    	 * Get the lemma of the token
    	 * 
    	 * @return the lemma
    	 */
    	public String getLemma() {
    		
    		return this.lemma;
    		
    	}
    	
    	
    	/**
    	 * Get the stem
    	 */
    	/*
    	public String getStem() {
    		
    		return this.stem;
    		
    	}
    	*/
    	
    	
    	/**
    	 * 
    	 * Get the POS of the token
    	 * 
    	 * @return the pos
    	 */
    	public String getPOS() {
    		
    		return this.pos;
    		
    	}
    	
    	
    	/**
    	 * 
    	 * Get the head of the token
    	 * 
    	 * @return the head
    	 */
    	public int getHead() {
    		
    		return this.head;
    		
    	}
    	
    	
    	/**
    	 * 
    	 * Get the dprel relation
    	 * 
    	 * @return the dprel relation
    	 */
    	public String getDprel() {
    		
    		return this.dprel;
    		
    	}
    	
    	
    	/**
    	 * 
    	 * set the dprel relations
    	 * 
    	 * @param the dprel relations
    	 */
    	public void setDprelRelations(String dprelRelations) {
	    		
    		this.dprelRelations = dprelRelations;
	    		
    	}
       
       
    	/**
    	 * 
    	 * Get the dprel relations
    	 * 
    	 * @return the dprel relations
    	 */
    	public String getDprelRelations() {
    		
    		//System.err.println("=======================" + this.deprelRelations);
    		return this.dprelRelations;
    		
    	}
    	
    	
    	/**
    	 * 
    	 * Return true when two tokens match; matches can be done considering
    	 * both the lemma and dprel of the tokens or the dprel or lemma only.
    	 * 
    	 * @param token2 the token to be matched with the current one
    	 * @param matchType the type of match: lemma-dprel, dprel or lemma
    	 * 
    	 * @return true when the tokens match; false otherwise
    	 */
    	public boolean match(FToken token2, String matchType) {
    		
    		if (matchType != null && matchType.equals("lemma-dprel"))
    				return (this.lemma.equalsIgnoreCase(token2.getLemma()) &&
    						this.dprel.equals(token2.getDprel()));
    		if (matchType != null && matchType.equals("dprel"))
    				return (this.dprel.equals(token2.getDprel()));
    		else //matchType.equals("lemma")
    			return (this.lemma.equalsIgnoreCase(token2.getLemma()));
    		
    	}
       
       
    	/**
    	 *  Get a description of the token
    	 *  
    	 *  return the description of the token
    	 */
    	public String toString() {
    		
    		return this.id + "__" + 
    			   this.form + "__" + 
    		       this.lemma + "__" + 
    		       this.pos + "__" + 
    			   //this.stem + ":" +
    		       this.head + "__" + 
    			   this.dprel + "__" + 
    		       this.dprelRelations;
    		
    	}
    	
    }
    