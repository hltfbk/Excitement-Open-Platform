package eu.excitementproject.eop.lap.biu.coreference;

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
	
	public String toString() {
		return String.format("DockedMention(tag=%s, span=<%s..%s>, text=%s)", groupTag, charOffsetStart, charOffsetEnd, text);
	}

}
