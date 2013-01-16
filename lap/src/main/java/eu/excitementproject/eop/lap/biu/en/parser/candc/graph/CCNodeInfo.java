package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;

public class CCNodeInfo
{
	
	
	
	public CCNodeInfo(String word, String lemma, String partOfSpeech) {
		super();
		this.word = word;
		this.lemma = lemma;
		this.partOfSpeech = partOfSpeech;
	}
	
	
	
	public String getWord() {
		return word;
	}
	public String getLemma() {
		return lemma;
	}
	public String getPartOfSpeech() {
		return partOfSpeech;
	}
	
	
	



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result
				+ ((partOfSpeech == null) ? 0 : partOfSpeech.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCNodeInfo other = (CCNodeInfo) obj;
		if (lemma == null) {
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (partOfSpeech == null) {
			if (other.partOfSpeech != null)
				return false;
		} else if (!partOfSpeech.equals(other.partOfSpeech))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}






	String word;
	String lemma;
	String partOfSpeech;

}
