package ac.biu.nlp.nlp.engineml.rteflow.macro.search.kstaged;

import java.util.Comparator;

/**
 * 
 * @author Asher Stern
 * @since Aug 14, 2011
 *
 * @param <T>
 */
public interface ByIterationComparator<T> extends Comparator<T>
{
	public void setIteration(int iteration);
}
