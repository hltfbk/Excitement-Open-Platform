package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.Serializable;

/**
 * 
 * Information about a document in the corpus. That information is not the
 * document's sentences themselves, but other information that can be found
 * in the document XML file.
 * 
 * @see TopicDataSet
 * 
 * 
 * @author Asher Stern
 * @since Jun 6, 2011
 *
 */
public class DocumentMetaData implements Serializable
{
	private static final long serialVersionUID = -8635757912096718088L;
	
	public DocumentMetaData(String docId, String type, String headline,
			String dateline)
	{
		super();
		this.docId = docId;
		this.type = type;
		this.headline = headline;
		this.dateline = dateline;
	}
	
	
	public String getDocId()
	{
		return docId;
	}
	public String getType()
	{
		return type;
	}
	public String getHeadline()
	{
		return headline;
	}
	public String getDateline()
	{
		return dateline;
	}


	private final String docId;
	private final String type;
	private final String headline;
	private final String dateline;
}
