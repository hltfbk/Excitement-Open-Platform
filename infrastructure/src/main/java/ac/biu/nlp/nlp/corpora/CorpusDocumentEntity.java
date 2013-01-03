package ac.biu.nlp.nlp.corpora;

import java.util.List;

/**
 * 
 * @author Asher Stern
 * @since Oct 25, 2012
 *
 */
public class CorpusDocumentEntity<D extends DocumentReader>
{
	public CorpusDocumentEntity(D documentReader,
			List<String> documentRelativePath, String documentDescription)
	{
		super();
		this.documentReader = documentReader;
		this.documentRelativePath = documentRelativePath;
		this.documentDescription = documentDescription;
	}
	
	
	
	public D getDocumentReader()
	{
		return documentReader;
	}
	public List<String> getDocumentRelativePath()
	{
		return documentRelativePath;
	}
	public String getDocumentDescription()
	{
		return documentDescription;
	}



	private final D documentReader;
	private final List<String> documentRelativePath;
	private final String documentDescription;
}
