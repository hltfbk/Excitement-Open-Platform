
package eu.excitementproject.eop.common.representation.parsetree;



public class GermanPartOfSpeech extends PartOfSpeech {

	private static final long serialVersionUID = 6295826063805145349L;

	public GermanPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		super(posTagString);
	}


	public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException {
		return new GermanPartOfSpeech(posTagString);
	}

	/**
	 * Assuming the field {@link #posTagString} is already set, this function chooses
	 * one of the appropriate values of {@link CanonicalPosTag} enumeration, and <B>sets
	 * the field</B> {@link #canonicalPosTag}.
	 * @throws UnsupportedPosTagStringException if the given string is not supported
	 */
	protected void setCanonicalPosTag() {
		assert posTagString != null && !posTagString.equals("");

		try {
			canonicalPosTag = CanonicalPosTag.valueOf(posTagString.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			canonicalPosTag = CanonicalPosTag.OTHER;
		}
	}

	/**
	 * Validates that the given string is one of the expected strings for that
	 * sub-class of {@link PartOfSpeech}.
	 * @param posTagString a string that represents part-of-speech. Usually, that string
	 * is given by an external resource, e.g. pos-tagger.
	 * @throws UnsupportedPosTagStringException if the string is not supported.
	 */
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException {
		try {
			CanonicalPosTag.valueOf(posTagString.toUpperCase());
		}
		catch (NullPointerException e) {
			throw new UnsupportedPosTagStringException("posTagString must not be null");
		}
		catch (IllegalArgumentException e) {
			throw new UnsupportedPosTagStringException("posTagString is not a valid GermanPartOfSpeech specifier");
		}

	}
}

