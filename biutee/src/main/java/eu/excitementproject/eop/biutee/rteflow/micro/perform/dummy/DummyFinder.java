package eu.excitementproject.eop.biutee.rteflow.micro.perform.dummy;
import java.util.Set;

import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.Finder;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 * @param <T>
 */
public final class DummyFinder<T> implements Finder<T>
{
	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	public void find() throws OperationException
	{
	}

	public Set<T> getSpecs() throws OperationException
	{
		return new DummySet<T>();
	}
}
