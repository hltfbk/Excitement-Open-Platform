package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

import java.io.Serializable;

/**
 * Contains: pair-id and answer which is an {@link EntailmentAnnotation}.
 * @author Asher Stern
 * @since Aug 23, 2010
 *
 */
public class PairAnswer implements Serializable
{
	private static final long serialVersionUID = -7491968496789026352L;
	
	public PairAnswer(String pairId, EntailmentAnnotation annotation)
	{
		super();
		this.pairId = pairId;
		this.annotation = annotation;
	}
	
	
	
	public String getPairId()
	{
		return pairId;
	}
	public EntailmentAnnotation getAnnotation()
	{
		return annotation;
	}


	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotation == null) ? 0 : annotation.hashCode());
		result = prime * result + ((pairId == null) ? 0 : pairId.hashCode());
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
		PairAnswer other = (PairAnswer) obj;
		if (annotation != other.annotation)
			return false;
		if (pairId == null)
		{
			if (other.pairId != null)
				return false;
		} else if (!pairId.equals(other.pairId))
			return false;
		return true;
	}




	private final String pairId;
	private final EntailmentAnnotation annotation;
}
