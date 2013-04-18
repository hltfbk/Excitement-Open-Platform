package eu.excitementproject.eop.distsim.items;


/**
  * A generic implementation of the {@link Relation} interface
  * 
* @author Meni Adler
 * @date 20/06/2012
 * 
 * 
 * <p>
 * Thread-safe
 * 
 * @param <T> the type of the relation domain
 */
public class DefaultRelation<T> implements Relation<T> {
	
	private static final long serialVersionUID = 1L;
	

	public DefaultRelation(T value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Relation#getValue()
	 */
	@Override
	public T getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey() {
		return value.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toKey();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toKey().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Externalizable other = (Externalizable) obj;
		try {
			return toKey().equals(other.toKey());
		} catch (UndefinedKeyException e) {
			return false;
		}
	}
	
	protected final T value;
	
}
