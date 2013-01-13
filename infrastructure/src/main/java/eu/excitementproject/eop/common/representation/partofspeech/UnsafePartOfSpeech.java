package eu.excitementproject.eop.common.representation.partofspeech;



/**
 * 
 * 
 * This PartOfSpeech makes no validation on the input string in its constructor,
 * and set the  {@link CanonicalPosTag} as OTHER for any input.
 * 
 * Don't use this class. It is used for some deprecated classes.
 * 
 * @deprecated Used by deprecated classes
 * 
 * @author Asher Stern
 * @since Mar 31, 2011
 *
 */
@Deprecated
public class UnsafePartOfSpeech extends PartOfSpeech
{
	private static final long serialVersionUID = 4593115819756356652L;

	public UnsafePartOfSpeech(String posTagString)
			throws UnsupportedPosTagStringException
	{
		super(posTagString);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString)
			throws UnsupportedPosTagStringException
	{
		return new UnsafePartOfSpeech(posTagString);
	}

	@Override
	protected void setCanonicalPosTag()
	{
		this.canonicalPosTag = CanonicalPosTag.OTHER;
	}

	@Override
	protected void validatePosTagString(String posTagString)
			throws UnsupportedPosTagStringException
	{
		// do nothing
	}
	

}
