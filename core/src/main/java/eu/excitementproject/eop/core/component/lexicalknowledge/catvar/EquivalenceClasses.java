package eu.excitementproject.eop.core.component.lexicalknowledge.catvar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * @since Feb 27, 2012
 *
 */
public class EquivalenceClasses<T>
{
	@SuppressWarnings("serial")
	public static class EquivalenceClassesException extends Exception
	{
		public EquivalenceClassesException(String message, Throwable cause){super(message, cause);}
		public EquivalenceClassesException(String message){super(message);}
	}
	
	public EquivalenceClasses(){}
	public EquivalenceClasses(boolean allowDuplicates)
	{
		duplicatesAllowed = allowDuplicates;
	}

	public void addEquivalenceClass(Set<T> equivalenceClass) throws EquivalenceClassesException
	{
		for (T t : equivalenceClass)
		{
			if (!duplicatesAllowed){if (map.containsKey(t)) throw new EquivalenceClassesException("An element already exist: "+t.toString());}
			map.put(t, equivalenceClass);
		}
	}
	
	public Set<T> getClassOf(T t) throws EquivalenceClassesException
	{
		if (map.containsKey(t))
		{
			return map.get(t);
		}
		else
		{
			return null;
		}
	}
	
	private Map<T, Set<T>> map = new LinkedHashMap<T, Set<T>>();
	private boolean duplicatesAllowed = false;
}

