package ac.biu.nlp.nlp.general;

import java.util.List;

/**
 * Represents a token that is a part of some text. We save the token's content as a string,
 * and also its start and end offsets (counting characters) in the text.<br>
 * <br>
 * For example, in the sentence: <tt>The dog ate my lunch</tt><br>
 * We would expect one of the DockedTokens to be <tt>dog[4:7]</tt><br>
 * <br>
 * This class is used in the return value of {@link StringUtil#getTokensOffsets(String, List, boolean)}
 * 
 * @author Ofer Bronstein
 * @since 9.8.2012
 *
 */
public class DockedToken {
	private String token;
	private int charOffsetStart;
	private int charOffsetEnd;
	
	public DockedToken(String token, int charOffsetStart, int charOffsetEnd) {
		this.token = token;
		this.charOffsetStart = charOffsetStart;
		this.charOffsetEnd = charOffsetEnd;
	}

	public String getToken() {
		return token;
	}

	public int getCharOffsetStart() {
		return charOffsetStart;
	}

	public int getCharOffsetEnd() {
		return charOffsetEnd;
	}
	
	public String toString() {
		return String.format("%s[%d:%d]", token, charOffsetStart, charOffsetEnd);
	}
}
