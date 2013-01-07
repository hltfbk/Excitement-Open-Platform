package ac.biu.nlp.nlp.engineml.operations.finders;

import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.Specification;

/**
 * Finder finds set of operations of a certain type, that can be done on the text tree.
 * Usually, the finder returns a set of {@link Specification}s, that describe the operations
 * found by the finder.
 * <P>
 * <B>THREAD SAFETY ASSUMPTION:
 * IT IS SAFE TO USE TWO FINDERS OF THE SAME TYPE IN TWO DIFFERENT THREADS.
 * </B>
 * It does not mean that the same finder can be used by two threads concurrently. But two threads
 * that instantiate two finders of the same type are OK. 
 * @author Asher Stern
 * @since Feb 17, 2011
 *
 */
public interface Finder<T>
{
	public void find() throws OperationException;
	
	public Set<T> getSpecs() throws OperationException;
}
