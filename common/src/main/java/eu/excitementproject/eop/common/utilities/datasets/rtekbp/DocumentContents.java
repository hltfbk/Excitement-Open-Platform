package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

import java.io.Serializable;

/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public class DocumentContents implements Serializable
{
	private static final long serialVersionUID = -1535720898260706227L;
	
	public DocumentContents(String docId, String source, String docType,
			String dateTime, String headLine, String sentences)
	{
		super();
		this.docId = docId;
		this.source = source;
		this.docType = docType;
		this.dateTime = dateTime;
		this.headLine = headLine;
		this.sentences = sentences;
	}
	
	
	
	
	public String getDocId()
	{
		return docId;
	}
	public String getSource()
	{
		return source;
	}
	public String getDocType()
	{
		return docType;
	}
	public String getDateTime()
	{
		return dateTime;
	}
	public String getHeadLine()
	{
		return headLine;
	}
	public String getSentences()
	{
		return sentences;
	}
	
	
	




	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((docId == null) ? 0 : docId.hashCode());
		result = prime * result + ((docType == null) ? 0 : docType.hashCode());
		result = prime * result
				+ ((headLine == null) ? 0 : headLine.hashCode());
		result = prime * result
				+ ((sentences == null) ? 0 : sentences.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		DocumentContents other = (DocumentContents) obj;
		if (dateTime == null)
		{
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (docId == null)
		{
			if (other.docId != null)
				return false;
		} else if (!docId.equals(other.docId))
			return false;
		if (docType == null)
		{
			if (other.docType != null)
				return false;
		} else if (!docType.equals(other.docType))
			return false;
		if (headLine == null)
		{
			if (other.headLine != null)
				return false;
		} else if (!headLine.equals(other.headLine))
			return false;
		if (sentences == null)
		{
			if (other.sentences != null)
				return false;
		} else if (!sentences.equals(other.sentences))
			return false;
		if (source == null)
		{
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}







	private final String docId;
	private final String source;
	private final String docType;
	private final String dateTime;
	
	
	private final String headLine;
	/**
	 * Note the sentences do not contain the head line!
	 */
	private final String sentences;
	

}
