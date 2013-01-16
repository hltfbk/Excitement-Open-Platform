package eu.excitementproject.eop.transformations.representation.srl_informations;
import java.io.Serializable;

/**
 * 
 * @author Asher Stern
 * @since Dec 27, 2011
 *
 */
public final class SrlPredicateId implements Serializable
{
	private static final long serialVersionUID = -3377729420139738619L;

	public SrlPredicateId(String s)
	{
		super();
		this.s = s;
	}
	
	public String get()
	{
		return s;
	}
	
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
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
		SrlPredicateId other = (SrlPredicateId) obj;
		if (s == null)
		{
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		return true;
	}



	private final String s;
}
