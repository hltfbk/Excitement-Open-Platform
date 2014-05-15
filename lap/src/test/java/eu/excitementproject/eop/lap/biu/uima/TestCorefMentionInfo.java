package eu.excitementproject.eop.lap.biu.uima;

public class TestCorefMentionInfo {

	public int begin;
	public int end;

	public TestCorefMentionInfo(int begin, int end) {
		super();
		this.begin = begin;
		this.end = end;
	}

	@Override
	public String toString() {
		return String.format("TestCorefMentionInfo [begin=%s, end=%s]", begin, end);
	}

}
