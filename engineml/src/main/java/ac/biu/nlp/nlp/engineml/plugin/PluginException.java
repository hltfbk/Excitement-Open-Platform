package ac.biu.nlp.nlp.engineml.plugin;
import ac.biu.nlp.nlp.engineml.utilities.BIUTEEBaseException;

/**
 * Plug-ins are allowed to throw only {@link PluginException}.
 * They are not allowed to throw any other exception, and it is also assumed
 * that they do not throw any runtime-exception.
 * 
 * @author Asher Stern
 * @since Jan 27, 2012
 *
 */
@SuppressWarnings("serial")
public class PluginException extends BIUTEEBaseException
{
	public PluginException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PluginException(String message)
	{
		super(message);
	}
}
