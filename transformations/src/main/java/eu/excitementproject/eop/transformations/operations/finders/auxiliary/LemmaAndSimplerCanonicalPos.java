package eu.excitementproject.eop.transformations.operations.finders.auxiliary;

import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;

/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2013
 *
 */
public class LemmaAndSimplerCanonicalPos
{
	public LemmaAndSimplerCanonicalPos(String lemma, SimplerCanonicalPosTag pos)
	{
		super();
		this.lemma = lemma;
		this.pos = pos;
	}
	
	
	
	
	public String getLemma()
	{
		return lemma;
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
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
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
		LemmaAndSimplerCanonicalPos other = (LemmaAndSimplerCanonicalPos) obj;
		if (lemma == null)
		{
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (pos != other.pos)
			return false;
		return true;
	}


	public String toString()
	{
		return lemma+"/"+
				(pos!=null?pos.name():"null");
	}



	private final String lemma;
	private final SimplerCanonicalPosTag pos;
}
