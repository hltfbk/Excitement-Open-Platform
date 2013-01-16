package eu.excitementproject.eop.biutee.rteflow.macro.search.kstaged;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * @since Aug 14, 2011
 *
 * @param <T>
 */
public interface StateCalculator<T>
{
	public boolean isGoal(T state) throws KStagedAlgorithmException;
	
	public List<T> generateChildren(T state, Set<T> closedList) throws KStagedAlgorithmException;
}
