package eu.excitementproject.eop.lap.biu.en.pasta.nomlex;



/**
 * Contains information about the relation of an argument to its nominal predicate.
 * This information is like it appears in the nomlex corpus. In the nomlex corpus, the relation between an argument
 * to its predicate is some generalization of a syntactic edge label - which might be something like "DET-POSS", "N-N-MOD", etc.
 * In addition, it might also contain a preposition ("by", "of", "with", etc.), and in this case the "place" is usually "PP".
 * 
 * So - {@link #place} is the edge-label ("DET-POSS", "N-N-MOD", "PP", etc.) and the optional field {@link #preposition} is "by", "of",
 * "with", etc.
 * It is all defined in the Nomlex corpus. See also {@link PlaceToRelationMap}, for mapping the {@link #place} to Stanford-dependencies
 * relations.
 * 
 * 
 * 
 * @author Asher Stern
 * @since Oct 14, 2012
 *
 */
public class NomlexArgument
{
	public NomlexArgument(String place)
	{
		super();
		this.place = place;
		this.preposition = null;
	}

	public NomlexArgument(String place, String preposition)
	{
		super();
		this.place = place;
		this.preposition = preposition;
	}

	
	
	public String getPlace()
	{
		return place;
	}
	public String getPreposition()
	{
		return preposition;
	}
	
	@Override
	public String toString()
	{
		String preposition_ = getPreposition();
		return getPlace()+
				((preposition_!=null)?"{"+preposition_+"}":"");
	}
	
	
	




	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		result = prime * result
				+ ((preposition == null) ? 0 : preposition.hashCode());
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
		NomlexArgument other = (NomlexArgument) obj;
		if (place == null)
		{
			if (other.place != null)
				return false;
		} else if (!place.equals(other.place))
			return false;
		if (preposition == null)
		{
			if (other.preposition != null)
				return false;
		} else if (!preposition.equals(other.preposition))
			return false;
		return true;
	}







	private final String place;
	private final String preposition;
}
