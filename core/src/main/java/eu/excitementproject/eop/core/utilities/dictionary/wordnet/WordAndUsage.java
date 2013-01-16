package eu.excitementproject.eop.core.utilities.dictionary.wordnet;

/**
 * Immutable
 * <P>
 * Holds a word (which is a string), and a number that
 * indicates its usage in that synset, according to WordNet's data.
 * @author Asher Stern
 *
 */
public class WordAndUsage
{
	public WordAndUsage(String word, long usage)
	{
		this.word = word;
		this.usage = usage;
	}
	
	
	public String getWord()
	{
		return word;
	}
	public long getUsage()
	{
		return usage;
	}
	
	
	


	@Override
	public int hashCode()
	{
		if (hashValueSet)
			return hashValue;
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (usage ^ (usage >>> 32));
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		hashValue = result;
		hashValueSet = true;
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
		WordAndUsage other = (WordAndUsage) obj;
		if (usage != other.usage)
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}





	private String word;
	private long usage;
	
	
	
	private int hashValue = 0;
	private boolean hashValueSet = false;
	

}
