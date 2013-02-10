package eu.excitementproject.eop.core.component.lexicalknowledge.catvar;

import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;

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
		this.pos = ((null==pos)?new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.OTHER):pos);
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
		result = prime * result + ((simplerPos(pos.getCanonicalPosTag()) == null) ? 0 : simplerPos(pos.getCanonicalPosTag()).hashCode());
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
		if (simplerPos(pos.getCanonicalPosTag()) == null)
		{
			if (simplerPos(other.pos.getCanonicalPosTag()) != null)
				return false;
		} else if (!simplerPos(pos.getCanonicalPosTag()).equals(simplerPos(other.pos.getCanonicalPosTag())))
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

