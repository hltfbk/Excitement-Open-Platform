package ac.biu.nlp.nlp.representation;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Asher Stern
 * @since Feb 26, 2011
 *
 */
public class MiniparPartOfSpeech extends PartOfSpeech
{
	private static final long serialVersionUID = -2009995217195169308L;
	
	public static final Map<String, CanonicalPosTag> MINIPAR_POS_TAGS;
	static
	{
		MINIPAR_POS_TAGS = new HashMap<String, CanonicalPosTag>();
		MINIPAR_POS_TAGS.put("V",CanonicalPosTag.VERB);
		MINIPAR_POS_TAGS.put("N",CanonicalPosTag.NOUN);
		MINIPAR_POS_TAGS.put("Aux",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("AUX",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("VBE",CanonicalPosTag.VERB);
		MINIPAR_POS_TAGS.put("A",CanonicalPosTag.ADJECTIVE);
		MINIPAR_POS_TAGS.put("Det",CanonicalPosTag.DETERMINER);
		MINIPAR_POS_TAGS.put("det",CanonicalPosTag.DETERMINER);
		MINIPAR_POS_TAGS.put("DET",CanonicalPosTag.DETERMINER);
		MINIPAR_POS_TAGS.put("PostDet",CanonicalPosTag.DETERMINER);
		MINIPAR_POS_TAGS.put("PreDet",CanonicalPosTag.DETERMINER);
		MINIPAR_POS_TAGS.put("Prep",CanonicalPosTag.PREPOSITION);
		MINIPAR_POS_TAGS.put("prep",CanonicalPosTag.PREPOSITION);
		MINIPAR_POS_TAGS.put("PREP",CanonicalPosTag.PREPOSITION);
		MINIPAR_POS_TAGS.put("PpSpec",CanonicalPosTag.PREPOSITION);
		MINIPAR_POS_TAGS.put("U",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put(" ",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("C",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("be",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("have",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("COMP",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("SentAdjunct",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("CN",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("THAT",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("Subj",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("SaidX",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("XSaid",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("Q",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("YNQ",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("EC",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("As",CanonicalPosTag.OTHER);
		MINIPAR_POS_TAGS.put("QBE",CanonicalPosTag.OTHER);
		
	}
	

	public MiniparPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		super(posTagString);
	}
	
	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		MiniparPartOfSpeech ret = new MiniparPartOfSpeech(posTagString);
		return ret;
	}

	
	
	

	@Override
	protected void setCanonicalPosTag()
	{
		if (MINIPAR_POS_TAGS.containsKey(this.posTagString))
			this.canonicalPosTag = MINIPAR_POS_TAGS.get(this.posTagString);
		else
		{
			if (posTagString!=null)
			{
				String upperCasePosTagString = posTagString.toUpperCase();
				if (MINIPAR_POS_TAGS.containsKey(upperCasePosTagString))
					this.canonicalPosTag = MINIPAR_POS_TAGS.get(upperCasePosTagString);
			}
			if (this.canonicalPosTag==null)
				this.canonicalPosTag = CanonicalPosTag.OTHER;
		}
	}

	@Override
	protected void validatePosTagString(String posTagString)
			throws UnsupportedPosTagStringException
	{
		// Do nothing.
		// Minipar part-of-speech is not documented.
		// We don't know the valid list of part-of-speech tags.
		
	}





	
	 
	

}

