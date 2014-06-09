package eu.excitementproject.eop.common.utilities.match;

/**
 * Performs operations for matches, detected by {@link Matcher}.
 * @see Matcher
 * 
 * @author Asher Stern
 *
 * @param <T>
 * @param <U>
 */
public interface Operator<T,U>
{
	public void set(T lhs, U rhs);
	public void makeOperation();
}
