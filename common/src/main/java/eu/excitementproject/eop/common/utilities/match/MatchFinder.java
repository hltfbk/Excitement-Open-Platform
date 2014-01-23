package eu.excitementproject.eop.common.utilities.match;

/**
 * Detected whether two entities match. Used by {@link Matcher}.
 * @see Matcher
 * 
 * @author Asher Stern
 *
 * @param <T>
 * @param <U>
 */
public interface MatchFinder<T,U>
{
	public void set(T lhs, U rhs);
	public boolean areMatch();
}
