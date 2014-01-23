package eu.excitementproject.eop.transformations.operations.finders;
import java.util.Set;

import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


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
	
	/**
	 * An optional method. Usually - has empty implementation. like:
	 * <code>
	 * public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException {}
	 * </code>
	 * It is not obligatory to implement this method. It might improve runtime, but cannot affect the results.
	 * An implementation would let the finder know what are the parse-tree nodes affected by a previous iteration of the
	 * search algorithm (of BIUTEE), and thus all operations which do not involve these nodes should not be returned.
	 * <BR>
	 * Anyhow, BIUTEE does remove all such operations, so this does not affect the results. However, runtime might be
	 * improved if instead of finding the operations (specifications) and then removing them, the operations are not
	 * found in the first place.
	 * 
	 * @param affectedNodes
	 * @throws OperationException
	 */
	public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException;
	
	public Set<T> getSpecs() throws OperationException;
}
