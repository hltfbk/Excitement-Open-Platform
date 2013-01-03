package ac.biu.nlp.nlp.general.match;

public interface Operator<T,U>
{
	public void set(T lhs, U rhs);
	public void makeOperation();
}
