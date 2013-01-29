package eu.excitementproject.eop.common.representation.partofspeech;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;



/**
 * This PartOfSpeech merely stores a {@link CanonicalPosTag}, but not a specific pos-tag-string.
 * 
 * 
 * @author Asher Stern
 * @since Mar 31, 2011
 *
 */
public class UnspecifiedPartOfSpeech extends PartOfSpeech
{
	private static final long serialVersionUID = -4220287183818990303L;

	///////////////////// PUBLIC ////////////////////////////
	
	// PUBLIC STATIC
	
	
	public static final Set<String> CANONICAL_POS_TAGS_STRINGS;
	static
	{
		Set<String> canonicalPosTagsStrings = new HashSet<String>();
		for (CanonicalPosTag pos : CanonicalPosTag.values())
		{
			canonicalPosTagsStrings.add(pos.name());
		}
		for (SimplerCanonicalPosTag pos : SimplerCanonicalPosTag.values())
		{
			canonicalPosTagsStrings.add(pos.name());
		}
		CANONICAL_POS_TAGS_STRINGS = Collections.unmodifiableSet(canonicalPosTagsStrings);
	}
	
	////////////////// PUBLIC CONSTRUCTORS AND METHODS /////////////////////////
	
	public UnspecifiedPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		super(posTagString);
	}
	
	public UnspecifiedPartOfSpeech(CanonicalPosTag canonicalPosTag) throws UnsupportedPosTagStringException
	{
		this(canonicalPosTag.name());
	}

	public UnspecifiedPartOfSpeech(SimplerCanonicalPosTag simplerCanonicalPosTag) throws UnsupportedPosTagStringException
	{
		this(simplerCanonicalPosTag.name());
	}

	

	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		return new UnspecifiedPartOfSpeech(posTagString);
	}

	@Override
	protected void setCanonicalPosTag()
	{
		this.canonicalPosTag = CanonicalPosTag.OTHER;
		try{this.canonicalPosTag = CanonicalPosTag.valueOf(posTagString);}
		catch(RuntimeException e){this.canonicalPosTag = CanonicalPosTag.OTHER;}
		try
		{
			SimplerCanonicalPosTag simpler = SimplerCanonicalPosTag.valueOf(posTagString);
			this.canonicalPosTag = SimplerPosTagConvertor.fromSimplerToCanonical(simpler);
		}
		catch(RuntimeException e){this.canonicalPosTag = CanonicalPosTag.OTHER;}
		
	}

	
	///////////////////// PROTECTED ////////////////////////////
	
	@Override
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException
	{
		if (this.posTagString!=null)
		{
			if (this.posTagString.length()>0)
			{
				if (!CANONICAL_POS_TAGS_STRINGS.contains(posTagString))
					throw new UnsupportedPosTagStringException("The pos tag \""+posTagString+"\" is not in the set of canonical part of speeches");
			}
		}
	}

}
