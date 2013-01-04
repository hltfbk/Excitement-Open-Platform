package eu.excitementproject.eop.common.representation.parsetree;

import eu.excitementproject.eop.common.exception.BaseException;

/**
 * <P> [DELETEME_LATER: Imported from BIUTEE, for PartOfSpeech interface.] </P> 
 * 
 * Thrown if an unsupported string-tag of part-of-speech is given to one of the
 * {@link PartOfSpeech} subclasses.
 * @author Asher Stern 
 */

public class UnsupportedPosTagStringException extends BaseException
{

	private static final long serialVersionUID = 7907574991609029615L;

	public UnsupportedPosTagStringException(String message)
	{
		super(message);
	}

	public UnsupportedPosTagStringException(String message, Throwable cause)
	{
		super(message, cause);
	}
}