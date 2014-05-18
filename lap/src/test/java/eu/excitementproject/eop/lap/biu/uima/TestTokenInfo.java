package eu.excitementproject.eop.lap.biu.uima;

import java.util.Arrays;

public class TestTokenInfo {
	
	public int id;
	public int begin;
	public int end;
	public String text;
	public String lemma;
	public String posType;
	public String posValue;
	public String nerType;
	public TestDependencyInfo[] dependencies;

	
	public TestTokenInfo(int id, int begin, int end, String text, String lemma, 
			String posType, String posValue, String nerType, TestDependencyInfo[] dependencies) {
		this.id = id;
		this.begin = begin;
		this.end = end;
		this.text = text;
		this.lemma = lemma;
		this.posType = posType;
		this.posValue = posValue;
		this.nerType = nerType;
		this.dependencies = dependencies;
	}


	@Override
	public String toString() {
		return String
				.format("TestTokenInfo [id=%s, begin=%s, end=%s, text=%s, lemma=%s, posType=%s, posValue=%s, nerType=%s, dependencies=%s]",
						id, begin, end, text, lemma, posType, posValue, nerType,
						Arrays.toString(dependencies));
	}

	
}
