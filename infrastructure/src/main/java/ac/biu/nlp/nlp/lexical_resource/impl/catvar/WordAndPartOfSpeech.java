package ac.biu.nlp.nlp.lexical_resource.impl.catvar;

import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * Equals and hashCode - using canonical
 * @author Asher Stern
 * @since Feb 27, 2012
 *
 */
final class WordAndPartOfSpeech
{
	public WordAndPartOfSpeech(String word, PartOfSpeech pos) throws UnsupportedPosTagStringException
	{
		super();
		this.word = word;
		this.pos = ((null==pos)?new UnspecifiedPartOfSpeech(CanonicalPosTag.OTHER):pos);
	}
	
	public String getWord()
	{
		return word;
	}
	public PartOfSpeech getPos()
	{
		return pos;
	}


	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos.getCanonicalPosTag() == null) ? 0 : pos.getCanonicalPosTag().hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		WordAndPartOfSpeech other = (WordAndPartOfSpeech) obj;
		if (pos.getCanonicalPosTag() == null)
		{
			if (other.pos.getCanonicalPosTag() != null)
				return false;
		} else if (!pos.getCanonicalPosTag().equals(other.pos.getCanonicalPosTag()))
			return false;
		if (word == null)
		{
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}



	public String toString()
	{
		return getWord()+"/"+getPos();
	}

	private final String word;
	private final PartOfSpeech pos;
}
