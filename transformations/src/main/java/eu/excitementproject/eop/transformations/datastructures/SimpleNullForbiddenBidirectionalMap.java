package eu.excitementproject.eop.transformations.datastructures;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;

/**
 * A {@link BidirectionalMap} that does not allow <code>null</code> values (for both left
 * and right values).
 * <P>
 * This class is currently not in use.
 * 
 * @author Asher Stern
 * @since February  2011
 *
 * @param <L>
 * @param <R>
 */
public class SimpleNullForbiddenBidirectionalMap<L, R> extends SimpleBidirectionalMap<L, R>
{
	private static final long serialVersionUID = 7261381512948887493L;

	public static class NullForbiddenException extends RuntimeException
	{
		private static final long serialVersionUID = 331280410196441619L;
		public NullForbiddenException(String message){super(message);}
	}
	
	@Override
	public void put(L left, R right)
	{
		if (null==left)
			throw new NullForbiddenException("left is null");
		if (null==right)
			throw new NullForbiddenException("right is null");
		
		super.put(left, right);
	}
}
