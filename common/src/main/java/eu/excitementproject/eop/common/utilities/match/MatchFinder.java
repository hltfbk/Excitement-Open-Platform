package eu.excitementproject.eop.common.utilities.match;

public interface MatchFinder<T,U>
{
	public void set(T lhs, U rhs);
	public boolean areMatch();
}
