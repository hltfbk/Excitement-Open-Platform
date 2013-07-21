package eu.excitementproject.eop.common.representation.partofspeech;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * @since Feb 10, 2013
 *
 */
public class BySimplerCanonicalPartOfSpeech extends PartOfSpeech
{
	private static final long serialVersionUID = 6024275791801867868L;
	
	public static final Set<String> SIMPLER_CANONICAL_POS_TAG_STRINGS;
	
	public BySimplerCanonicalPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		super(posTagString);
	}
	
	public BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag simpler) throws UnsupportedPosTagStringException
	{
		this(simpler.name());
	}

	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		return new BySimplerCanonicalPartOfSpeech(posTagString);
	}

	@Override
	protected void setCanonicalPosTag()
	{
		try
		{
			this.canonicalPosTag = mapSimpleToCanonical.get(SimplerCanonicalPosTag.valueOf(this.posTagString));
		}
		catch (IllegalArgumentException e)
		{
			this.canonicalPosTag = CanonicalPosTag.OTHER;
		}
		
	}

	@Override
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException
	{
		if (!SIMPLER_CANONICAL_POS_TAG_STRINGS.contains(posTagString))
		{
			throw new UnsupportedPosTagStringException("Invalid pos tag string: \""+posTagString+"\"");	
		}
	}
	
	
	private static final Map<SimplerCanonicalPosTag, CanonicalPosTag> mapSimpleToCanonical;
	static
	{
		HashSet<String> validPosTagStrings_ = new HashSet<String>();
		for (SimplerCanonicalPosTag simpler : SimplerCanonicalPosTag.values())
		{
			validPosTagStrings_.add(simpler.name());
		}
		
		SIMPLER_CANONICAL_POS_TAG_STRINGS = Collections.unmodifiableSet(validPosTagStrings_);
		
		LinkedHashMap<SimplerCanonicalPosTag, CanonicalPosTag> mapSimpleToCanonical_ = new LinkedHashMap<SimplerCanonicalPosTag, CanonicalPosTag>();
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.NOUN,CanonicalPosTag.N);
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.VERB,CanonicalPosTag.V);
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.ADJECTIVE,CanonicalPosTag.ADJ);
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.ADVERB,CanonicalPosTag.ADV);
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.PREPOSITION,CanonicalPosTag.PP);
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.DETERMINER,CanonicalPosTag.ART);
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.PRONOUN,CanonicalPosTag.PR);
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.PUNCTUATION,CanonicalPosTag.PUNC);
		mapSimpleToCanonical_.put(SimplerCanonicalPosTag.OTHER,CanonicalPosTag.OTHER);
		
		mapSimpleToCanonical = Collections.unmodifiableMap(mapSimpleToCanonical_);
	}
}
