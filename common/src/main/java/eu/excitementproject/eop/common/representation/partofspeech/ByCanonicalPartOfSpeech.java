package eu.excitementproject.eop.common.representation.partofspeech;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * @since Feb 10, 2013
 *
 */
public class ByCanonicalPartOfSpeech extends PartOfSpeech
{
	private static final long serialVersionUID = 7276768283081640777L;
	
	public ByCanonicalPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		super(posTagString);
	}

	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		return new ByCanonicalPartOfSpeech(posTagString);
	}

	@Override
	protected void setCanonicalPosTag()
	{
		try
		{
			this.canonicalPosTag = CanonicalPosTag.valueOf(this.posTagString);
		}
		catch(IllegalArgumentException e)
		{
			this.canonicalPosTag = null;
		}
	}

	@Override
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException
	{
		if (!validPosTagStrings.contains(posTagString))
			throw new UnsupportedPosTagStringException("Bad canonical pos tag: \""+posTagString+"\"");
	}
	
	private static final Set<String> validPosTagStrings;
	static
	{
		HashSet<String> validPosTagStrings_ = new HashSet<String>();
		for (CanonicalPosTag canonicalPosTag : CanonicalPosTag.values())
		{
			validPosTagStrings_.add(canonicalPosTag.name());
		}
		
		validPosTagStrings = Collections.unmodifiableSet(validPosTagStrings_);
	}

}
