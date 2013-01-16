package eu.excitementproject.eop.transformations.datastructures;
import java.io.Serializable;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * Encapsulation of lemma and part-of-speech.
 * This class implements hashCode() and equals()
 * 
 * @author Asher Stern
 * @since Feb 9, 2011
 *
 */
public final class LemmaAndPos implements Serializable
{

	private static final long serialVersionUID = -7089979798068924366L;
	
	public LemmaAndPos(String lemma, PartOfSpeech partOfSpeech) throws TeEngineMlException
	{
		if (null==lemma)throw new TeEngineMlException("Null lemma");
		if (null==partOfSpeech)throw new TeEngineMlException("Null PartOfSpeech");
		
		this.lemma = lemma;
		this.partOfSpeech = partOfSpeech;
	}
	
	
	public String getLemma()
	{
		return lemma;
	}
	
	public PartOfSpeech getPartOfSpeech()
	{
		return partOfSpeech;
	}
	

	




	@Override
	public int hashCode()
	{
		if (hashCodeSet)return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result
				+ ((partOfSpeech == null) ? 0 : partOfSpeech.hashCode());
		// This is thread-safe - think about it.
		hashCodeValue=result;
		hashCodeSet=true;
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
		LemmaAndPos other = (LemmaAndPos) obj;
		if (lemma == null)
		{
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (partOfSpeech == null)
		{
			if (other.partOfSpeech != null)
				return false;
		} else if (!partOfSpeech.equals(other.partOfSpeech))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return getLemma() + ":" + getPartOfSpeech();
	}




	private final String lemma;
	private final PartOfSpeech partOfSpeech;
	
	private transient boolean hashCodeSet = false;
	private transient int hashCodeValue = 0;
}
