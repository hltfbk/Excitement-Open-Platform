package eu.excitementproject.eop.biutee.utilities;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 * @param <T>
 */
public interface Provider<T>
{
	public T get() throws BiuteeException;
}
