package eu.excitementproject.eop.lap.biu.ae;

import java.util.Arrays;

public class TestTokenInfo {
	
	public int id;
	public int begin;
	public int end;
	public String text;
	public String lemma;
	public String posType;
	public String posValue;
	public TestDependencyInfo[] dependencies;

	
	public TestTokenInfo(int id, int begin, int end, String text, String lemma, 
			String posType, String posValue, TestDependencyInfo[] dependencies) {
		this.id = id;
		this.begin = begin;
		this.end = end;
		this.text = text;
		this.lemma = lemma;
		this.posType = posType;
		this.posValue = posValue;
		this.dependencies = dependencies;
	}


	@Override
	public String toString() {
		return String
				.format("TestTokenInfo [id=%s, begin=%s, end=%s, text=%s, lemma=%s, posType=%s, posValue=%s, dependencies=%s]",
						id, begin, end, text, lemma, posType, posValue,
						Arrays.toString(dependencies));
	}

	
}
