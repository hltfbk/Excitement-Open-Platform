package eu.excitementproject.eop.common.utilities;

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
	
	public void setToken(String token) {
		this.token = token;
	}

	public void setCharOffsetStart(int charOffsetStart) {
		this.charOffsetStart = charOffsetStart;
	}

	public void setCharOffsetEnd(int charOffsetEnd) {
		this.charOffsetEnd = charOffsetEnd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + charOffsetEnd;
		result = prime * result + charOffsetStart;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DockedToken other = (DockedToken) obj;
		if (charOffsetEnd != other.charOffsetEnd) {
			return false;
		}
		if (charOffsetStart != other.charOffsetStart) {
			return false;
		}
		if (token == null) {
			if (other.token != null) {
				return false;
			}
		} else if (!token.equals(other.token)) {
			return false;
		}
		return true;
	}

	public String toString() {
		return String.format("%s[%d:%d]", token, charOffsetStart, charOffsetEnd);
	}
}
