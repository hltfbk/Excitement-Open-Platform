package eu.excitementproject.eop.lap.biu.coreference.merge;

/**
 * Represents a word and a co-reference tag.
 * Both are represented as Strings.
 * 
 * @author Asher Stern
 *
 */
public final class WordWithCoreferenceTag
{
	public WordWithCoreferenceTag(String word, String coreferenceTag)
	{
		super();
		this.word = word;
		this.coreferenceTag = coreferenceTag;
	}
	
	
	
	public String getWord()
	{
		return word;
	}
	public String getCoreferenceTag()
	{
		return coreferenceTag;
	}
	
	



	@Override
	public int hashCode()
	{
		if (hashCodeSet) return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coreferenceTag == null) ? 0 : coreferenceTag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		hashCodeValue = result;
		hashCodeSet = true;
		return result;
	}



	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordWithCoreferenceTag other = (WordWithCoreferenceTag) obj;
		if (coreferenceTag == null)
		{
			if (other.coreferenceTag != null)
				return false;
		} else if (!coreferenceTag.equals(other.coreferenceTag))
			return false;
		if (word == null)
		{
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}


	public String toString() {
		return word + ":" + coreferenceTag;
	}


	private final String word;
	private final String coreferenceTag;
	
	transient private int hashCodeValue;
	transient private boolean hashCodeSet = false;
}
