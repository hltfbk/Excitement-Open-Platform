package eu.excitementproject.eop.common.representation.parse.representation.basic;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


/**
 * This class is immutable.
 * 
 * @author Asher Stern
 *
 */
public class DefaultSyntacticInfo implements SyntacticInfo
{
	private static final long serialVersionUID = 2529017158634018005L;

	public DefaultSyntacticInfo(PartOfSpeech pos)
	{
		this.partOfSpeech = pos;
	}
	
	


	public PartOfSpeech getPartOfSpeech()
	{
		return this.partOfSpeech;
	}
	






	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getPartOfSpeech() == null) ? 0 : this.getPartOfSpeech().hashCode());
		return result;
	}




	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SyntacticInfo))
			return false;
		SyntacticInfo other = (SyntacticInfo) obj;
		if (this.getPartOfSpeech() == null)
		{
			if (other.getPartOfSpeech() != null)
				return false;
		} else if (!getPartOfSpeech().equals(other.getPartOfSpeech()))
			return false;
		return true;
	}







	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultSyntacticInfo [partOfSpeech=" + partOfSpeech + "]";
	}







	protected PartOfSpeech partOfSpeech;

}
