package eu.excitementproject.eop.common.datastructures.dgraph;

/**
 * This class can be used by implementation of {@link DirectedGraph}.
 * 
 * @author Asher Stern
 *
 * @param <N> should be immutable
 */
public final class HeadAndTail<N>
{
	public HeadAndTail(N head, N tail)
	{
		this.head = head;
		this.tail = tail;
	}
	
	public N getHead()
	{
		return head;
	}
	
	public N getTail()
	{
		return tail;
	}
	
	
	
	
	@Override
	public int hashCode()
	{
		// This is thread safe, since the class is immutable, so hashCodeValue
		// can get exactly one value.
		if (hashCodeSet)
			return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result + ((tail == null) ? 0 : tail.hashCode());
		hashCodeValue = result;
		hashCodeSet = true;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		HeadAndTail other = (HeadAndTail) obj;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		if (tail == null) {
			if (other.tail != null)
				return false;
		} else if (!tail.equals(other.tail))
			return false;
		return true;
	}




	private N head;
	private N tail;
	
	transient private boolean hashCodeSet = false;
	transient private int hashCodeValue = 0;

}
