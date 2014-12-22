package eu.excitementproject.eop.lap.btbpipe;

/**
 * A class to represent tokens and their characteristics: begin/end index with 
 * respect to the original input document; BTB-tag, a morphosyntactic tag from
 * the BTB-tagset; mapping of each of these tags to their corresponding 
 * universal tags ("A Universal Part-of-Speech Tagset", Petrov et al, 2012);
 * the lowercased lemma of this token;
 * 
 * @author Iliana Simova (BulTreeBank team)
 */
public class BTBToken {

	private int begin;
	private int end;
	private String token;
	private String lemma;
	private String btbPOS;
	private String uPOS;

	/**
	 * Create a new empty token object.
	 */
	public BTBToken() {
		setBegin(-1);
		setEnd(-1);
		setTokenText("");
		setLemma("");
		setBtbPOS("");
		setuPOS("");
	}

	/**
	 * Create a new token object.
	 * 
	 * @param begin
	 *            start index wrf the original document
	 * @param end
	 *            end index wrt the original document
	 * @param token
	 *            token as string
	 */
	public BTBToken(int begin, int end, String token) {
		this();
		setBegin(begin);
		setEnd(end);
		setTokenText(token);
	}
	
	/**
	 * Create a new token object.
	 * 
	 * @param token
	 *            token as string
	 */
	public BTBToken(String token) {
		this();
		setTokenText(token);
	}

	/**
	 * Create a new token object.
	 * 
	 * @param begin
	 *            start index wrf the original document
	 * @param end
	 *            end index wrt the original document
	 * @param token
	 *            token as string
	 * @param lemma
	 *            lemma of the token
	 * @param btbPOS
	 *            BTB-tag of the token
	 * @param uPOS
	 *            universal tag of the token
	 */
	public BTBToken(int begin, int end, String token, String lemma, String btbPOS, String uPOS) {
		setBegin(begin);
		setEnd(end);
		setTokenText(token);
		setLemma(lemma);
		setBtbPOS(btbPOS);
		setuPOS(uPOS);
	}

	/**
	 * Get the text covered by this token object
	 * 
	 * @return the token
	 */
	public String getTokenText() {
		return token;
	}

	/**
	 * Set the text covered by this token object
	 * 
	 * @param textValue
	 *            the new text value
	 */
	public void setTokenText(String textValue) {
		this.token = textValue;
	}

	/**
	 * Get the start intex of this token with respect to the input document
	 * 
	 * @return start index of this token
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * Set the start intex of this token with respect to the input document
	 * 
	 * @param begin
	 *            the new start index
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}

	/**
	 * Get the end intex of this token with respect to the input document
	 * 
	 * @return end index of this token
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Set the end index of this token with respect to the input document
	 * 
	 * @param end
	 *            the new end index
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * Get the lemma of this token
	 * 
	 * @return lemma
	 */
	public String getLemma() {
		return lemma;
	}

	/**
	 * Set the lemma of this token
	 * 
	 * @param lemma
	 *            new lemma value
	 */
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	/**
	 * Get the BTB-tag of this token
	 * 
	 * @return BTB-tag of this token
	 */
	public String getBtbPOS() {
		return btbPOS;
	}

	/**
	 * Set the BTB-tag of this token
	 * 
	 * @param btbPos
	 *            new BTB-tag value
	 */
	public void setBtbPOS(String btbPos) {
		this.btbPOS = btbPos;
	}

	/**
	 * Get the universal pos tag for this token
	 * 
	 * @return universal tag
	 */
	public String getuPOS() {
		return uPOS;
	}

	/**
	 * Set the universal pos tag for this token
	 * 
	 * @param uPOS
	 *            new universal tag value
	 */
	public void setuPOS(String uPOS) {
		this.uPOS = uPOS;
	}
	
	/**
	 * A string representation of this object
	 */
	public String toString() {
		return this.token + '\t' + this.begin + '\t' + this.end;
	}
}
