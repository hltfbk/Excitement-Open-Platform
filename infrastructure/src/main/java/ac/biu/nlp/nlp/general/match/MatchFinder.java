package ac.biu.nlp.nlp.general.match;

public interface MatchFinder<T,U>
{
	public void set(T lhs, U rhs);
	public boolean areMatch();
}
