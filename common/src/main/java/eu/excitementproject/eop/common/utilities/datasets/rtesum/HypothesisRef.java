package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.Serializable;
/**
 * Represents information about "cluster B" sentences, that was used
 * to create an hypothesis. <B>It is an unnecessary information</B> that
 * exist in the hypothesis files.
 * @see HypothesisFileReader
 * @author Asher Stern
 *
 */
public final class HypothesisRef implements Serializable
{
	private static final long serialVersionUID = 175045195521883032L;
	
	public HypothesisRef(SentenceIdentifier referenceSentenceIdentifier,
			String referenceText)
	{
		super();
		this.referenceSentenceIdentifier = referenceSentenceIdentifier;
		this.referenceText = referenceText;
	}
	
	
	public SentenceIdentifier getReferenceSentenceIdentifier()
	{
		return referenceSentenceIdentifier;
	}
	public String getReferenceText()
	{
		return referenceText;
	}
	
	


	@Override
	public int hashCode()
	{
		if (hashCodeSet) return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((referenceSentenceIdentifier == null) ? 0
						: referenceSentenceIdentifier.hashCode());
		result = prime * result
				+ ((referenceText == null) ? 0 : referenceText.hashCode());
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
		HypothesisRef other = (HypothesisRef) obj;
		if (referenceSentenceIdentifier == null)
		{
			if (other.referenceSentenceIdentifier != null)
				return false;
		} else if (!referenceSentenceIdentifier
				.equals(other.referenceSentenceIdentifier))
			return false;
		if (referenceText == null)
		{
			if (other.referenceText != null)
				return false;
		} else if (!referenceText.equals(other.referenceText))
			return false;
		return true;
	}




	private final SentenceIdentifier referenceSentenceIdentifier;
	private final String referenceText;
	
	transient private boolean hashCodeSet = false;
	transient private int hashCodeValue = 0;

}
