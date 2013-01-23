package eu.excitementproject.eop.lap.biu.postagger;

import java.io.Serializable;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


/**
 * Represents a token and its part-of-speech.
 * This class is used to represent the output of {@link PosTagger}
 * 
 * @see PosTagger
 * 
 * @author Asher Stern
 * @since Jan 10, 2011
 *
 */
public class PosTaggedToken implements Serializable
{
	private static final long serialVersionUID = -1070285914866571743L;
	
	public PosTaggedToken(String token, PartOfSpeech partOfSpeech)
	{
		this.token = token;
		this.partOfSpeech = partOfSpeech;
	}
	
	
	
	public String getToken()
	{
		return token;
	}
	public PartOfSpeech getPartOfSpeech()
	{
		return partOfSpeech;
	}
	
	
	
	



	@Override
	public int hashCode()
	{
		if (hashCodeSet) return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((partOfSpeech == null) ? 0 : partOfSpeech.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		PosTaggedToken other = (PosTaggedToken) obj;
		if (partOfSpeech == null)
		{
			if (other.partOfSpeech != null)
				return false;
		} else if (!partOfSpeech.equals(other.partOfSpeech))
			return false;
		if (token == null)
		{
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return token + "/" + partOfSpeech;
	}

	private final String token;
	private final PartOfSpeech partOfSpeech;
	
	private transient int hashCodeValue = 0;
	private transient boolean hashCodeSet = false;
}
