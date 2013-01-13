package ac.biu.nlp.nlp.engineml.rteflow.micro.perform.dummy;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.finders.Finder;
import eu.excitementproject.eop.common.datastructures.DummySet;


/**
 * 
 * @author Asher Stern
 * @since Jan 25, 2012
 *
 * @param <T>
 */
public final class DummyFinder<T> implements Finder<T>
{
	public void find() throws OperationException
	{
	}

	public Set<T> getSpecs() throws OperationException
	{
		return new DummySet<T>();
	}
}
