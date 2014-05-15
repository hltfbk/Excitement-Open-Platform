package eu.excitementproject.eop.common.representation.coreference;

public class DockedMention {

	private String text;
	private int charOffsetStart;
	private int charOffsetEnd;
	private String groupTag;

	public DockedMention(String text, int charOffsetStart, int charOffsetEnd,
			String groupTag) {
		this.text = text;
		this.charOffsetStart = charOffsetStart;
		this.charOffsetEnd = charOffsetEnd;
		this.groupTag = groupTag;
	}
	
	public String getText() {
		return text;
	}

	public int getCharOffsetStart() {
		return charOffsetStart;
	}

	public int getCharOffsetEnd() {
		return charOffsetEnd;
	}

	public String getGroupTag() {
		return groupTag;
	}

	public String toString() {
		return String.format("DockedMention(tag=%s, span=<%s..%s>, text=%s)", groupTag, charOffsetStart, charOffsetEnd, text);
	}

}
