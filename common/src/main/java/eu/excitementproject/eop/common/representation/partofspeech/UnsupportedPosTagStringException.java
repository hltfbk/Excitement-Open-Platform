package eu.excitementproject.eop.common.representation.partofspeech;


/**
 * Thrown if an unsupported string-tag of part-of-speech is given to one of the
 * {@link PartOfSpeech} subclasses.
 * @author Asher Stern
 * @since Dec 26, 2010
 *
 */
public class UnsupportedPosTagStringException extends Exception
{
	private static final long serialVersionUID = -4416200456602272846L;

	public UnsupportedPosTagStringException(String message)
	{
		super(message);
	}

	public UnsupportedPosTagStringException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
