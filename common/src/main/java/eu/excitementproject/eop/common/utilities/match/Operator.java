package eu.excitementproject.eop.common.utilities.match;

public interface Operator<T,U>
{
	public void set(T lhs, U rhs);
	public void makeOperation();
}
