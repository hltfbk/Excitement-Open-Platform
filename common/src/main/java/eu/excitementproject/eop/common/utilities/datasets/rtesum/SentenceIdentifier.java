package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.Serializable;

/**
 * Represents a sentence identifier, which is:
 * <OL>
 * <LI>document id</LI>
 * <LI>sentence id (0,1,2,...)</LI>
 * </OL>
 * 
 * @author Asher Stern
 *
 */
public final class SentenceIdentifier implements Serializable
{
	private static final long serialVersionUID = -8211053067482612794L;
	
	public SentenceIdentifier(String documentId, String sentenceId)
	{
		super();
		this.documentId = documentId;
		this.sentenceId = sentenceId;
	}
	
	public String getDocumentId()
	{
		return documentId;
	}
	public String getSentenceId()
	{
		return sentenceId;
	}

	


	@Override
	public int hashCode()
	{
		if (hashCodeSet) return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((documentId == null) ? 0 : documentId.hashCode());
		result = prime * result
				+ ((sentenceId == null) ? 0 : sentenceId.hashCode());
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
		SentenceIdentifier other = (SentenceIdentifier) obj;
		if (documentId == null)
		{
			if (other.documentId != null)
				return false;
		} else if (!documentId.equals(other.documentId))
			return false;
		if (sentenceId == null)
		{
			if (other.sentenceId != null)
				return false;
		} else if (!sentenceId.equals(other.sentenceId))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return getDocumentId()+"/"+getSentenceId();
	}



	private final String documentId;
	private final String sentenceId;
	
	transient private boolean hashCodeSet = false;
	transient private int hashCodeValue = 0;

}
