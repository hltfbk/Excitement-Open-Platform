package eu.excitementproject.eop.common.utilities.corpora;

import java.util.List;

/**
 * Represents a document, and information about it.
 * This information includes the {@link DocumentReader} (which can read
 * the document contents), the document-relative-path - which is a path
 * relative to the corpus main directory, and a free string of description.
 * <P>
 * This class is used by {@link CorpusReader}.
 * 
 * @see CorpusReader
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
