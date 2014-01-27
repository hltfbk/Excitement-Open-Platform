package eu.excitementproject.eop.transformations.operations.finders.auxiliary;

import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;

/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2013
 *
 */
public class PosRelPos
{
	public PosRelPos(SimplerCanonicalPosTag posParent, String relation,
			SimplerCanonicalPosTag pos)
	{
		super();
		this.posParent = posParent;
		this.relation = relation;
		this.pos = pos;
	}
	
	
	
	public SimplerCanonicalPosTag getPosParent()
	{
		return posParent;
	}
	public String getRelation()
	{
		return relation;
	}
	public SimplerCanonicalPosTag getPos()
	{
		return pos;
	}



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result
				+ ((posParent == null) ? 0 : posParent.hashCode());
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
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
		PosRelPos other = (PosRelPos) obj;
		if (pos != other.pos)
			return false;
		if (posParent != other.posParent)
			return false;
		if (relation == null)
		{
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return (posParent!=null?posParent.name():"null")+"/"+
				relation+"/"+
				(pos!=null?pos.name():"null");
	}



	private final SimplerCanonicalPosTag posParent;
	private final String relation;
	private final SimplerCanonicalPosTag pos;
}
