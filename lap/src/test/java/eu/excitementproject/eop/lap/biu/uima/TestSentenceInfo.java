package eu.excitementproject.eop.lap.biu.uima;

public class TestSentenceInfo {

	public int begin;
	public int end;
	public String text;
	
	public TestSentenceInfo(int begin, int end, String text) {
		this.begin = begin;
		this.end = end;
		this.text = text;
	}

	@Override
	public String toString() {
		return String.format("TestSentenceInfo [begin=%s, end=%s, text=%s]",
				begin, end, text);
	}

}
