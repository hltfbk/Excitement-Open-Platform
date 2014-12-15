package eu.excitementproject.eop.lap.btbpipe;

import java.util.ArrayList;
import java.util.List;

//Import from same Package!
//import eu.excitementproject.eop.lap.btbpipe.BTBToken;

/**
 * A class to represent sentences and their properties: begin/end index, list of
 * tokens
 * 
 * @author Iliana Simova (BulTreeBank team)
 */
public class BTBSentence {

	private String sentence;
	private int begin;
	private int end;
	private List<BTBToken> tokens;

	/**
	 * Create a new empty sentence object.
	 */
	public BTBSentence() {
		setSentenceText("");
		setBegin(-1);
		setEnd(-1);
		setTokens(new ArrayList<BTBToken>());
	}
	
	/**
	 * Create a new sentence object.
	 */
	public BTBSentence(String sentence) {
		this();
		setSentenceText(sentence);
	}

	/**
	 * Get the text covered by this sentence object
	 * 
	 * @return sentence as string
	 */
	public String getSentenceText() {
		return sentence;
	}

	/**
	 * Set the text covered by this sentence object
	 * 
	 * @param sent
	 *            new sentence string value
	 */
	public void setSentenceText(String sent) {
		this.sentence = sent;
	}

	/**
	 * Add a token to the list of tokens in this sentence
	 * 
	 * @param t
	 *            new token to add to the list of tokens
	 */
	public void addToken(BTBToken t) {
		tokens.add(t);
	}

	/**
	 * Get the list of tokens of this sentence
	 * 
	 * @return the list of tokens in this sentence
	 */
	public List<BTBToken> getTokens() {
		return tokens;
	}

	/**
	 * Set the list of tokens of this sentence
	 * 
	 * @param tokens
	 *            new list of tokens
	 */
	public void setTokens(List<BTBToken> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Get the start index of this sentence
	 * 
	 * @return start index
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * Set the start index of this sentence
	 * 
	 * @param begin
	 *            new start index
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}

	/**
	 * Get the end index of this sentence
	 * 
	 * @return end index
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Set the end index of this sentence.
	 * 
	 * @param end
	 *            new end index
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	/**
	 * A string representation of this object
	 */
	public String toString() {
		return this.sentence + '\t' + this.begin + '\t' + this.end;
	}
}
