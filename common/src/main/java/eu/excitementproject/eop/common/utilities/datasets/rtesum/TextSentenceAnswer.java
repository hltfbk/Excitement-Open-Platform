package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.Serializable;

public final class TextSentenceAnswer implements Serializable
{
	private static final long serialVersionUID = 1558002943454953564L;
	public TextSentenceAnswer(SentenceIdentifier identifier,
			String sentenceString)
	{
		super();
		this.identifier = identifier;
		this.sentenceString = sentenceString;
	}
	
	
	public SentenceIdentifier getIdentifier()
	{
		return identifier;
	}
	public String getSentenceString()
	{
		return sentenceString;
	}
	
	


	@Override
	public int hashCode()
	{
		if (hashCodeSet) return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result
				+ ((sentenceString == null) ? 0 : sentenceString.hashCode());
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
		TextSentenceAnswer other = (TextSentenceAnswer) obj;
		if (identifier == null)
		{
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (sentenceString == null)
		{
			if (other.sentenceString != null)
				return false;
		} else if (!sentenceString.equals(other.sentenceString))
			return false;
		return true;
	}




	private final SentenceIdentifier identifier;
	private final String sentenceString;
	
	transient private boolean hashCodeSet = false;
	transient private int hashCodeValue = 0;
}
