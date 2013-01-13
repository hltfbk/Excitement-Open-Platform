package eu.excitementproject.eop.core.utilities.dictionary.wordnet;

/**
 * Information about a lexicographer file.
 * 
 * @see Synset#getLexicographerFileInformation()
 * 
 * @author Asher Stern
 * @since Sep 27, 2012
 *
 */
public final class LexicographerFileInformation
{
	public LexicographerFileInformation(String id, String filename, String contents)
	{
		super();
		this.id = id;
		this.filename = filename;
		this.contents = contents;
	}

	
	public String getId()
	{
		return id;
	}
	public String getFilename()
	{
		return filename;
	}
	public String getContents()
	{
		return contents;
	}
	
	
	



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contents == null) ? 0 : contents.hashCode());
		result = prime * result
				+ ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		LexicographerFileInformation other = (LexicographerFileInformation) obj;
		if (contents == null)
		{
			if (other.contents != null)
				return false;
		} else if (!contents.equals(other.contents))
			return false;
		if (filename == null)
		{
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	///////////////////// PRIVATE ///////////////////// 
	





	private final String id;
	private final String filename;
	private final String contents;
}
