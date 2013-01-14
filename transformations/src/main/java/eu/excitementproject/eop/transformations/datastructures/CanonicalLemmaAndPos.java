package eu.excitementproject.eop.transformations.datastructures;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * Like {@link LemmaAndPos}, but the {@link #equals(Object)} and {@link #hashCode()}
 * functions use the canonical part-of-speech tag.
 * 
 * @author Asher Stern
 * @since Feb 13, 2012
 *
 */
public final class CanonicalLemmaAndPos
{
	public CanonicalLemmaAndPos(String lemma, PartOfSpeech pos) throws TeEngineMlException
	{
		super();
		if (null==lemma) throw new TeEngineMlException("null lemma");
		if (null==pos) throw new TeEngineMlException("null pos");
		if (null==simplerPos(pos.getCanonicalPosTag())) throw new TeEngineMlException("null canonical pos");
		this.lemma = lemma;
		this.pos = pos;
	}
	
	public String getLemma()
	{
		return lemma;
	}
	public PartOfSpeech getPartOfSpeech()
	{
		return pos;
	}






	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((simplerPos(pos.getCanonicalPosTag()) == null) ? 0 : simplerPos(pos.getCanonicalPosTag()).hashCode());
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
		CanonicalLemmaAndPos other = (CanonicalLemmaAndPos) obj;
		if (lemma == null)
		{
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (simplerPos(pos.getCanonicalPosTag()) == null)
		{
			if (simplerPos(other.pos.getCanonicalPosTag()) != null)
				return false;
		} else if (!simplerPos(pos.getCanonicalPosTag()).equals(simplerPos(other.pos.getCanonicalPosTag())))
			return false;
		return true;
	}




	private final String lemma;
	private final PartOfSpeech pos;
}
