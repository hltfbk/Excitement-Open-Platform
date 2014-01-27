/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

import java.util.HashSet;
import java.util.Set;


/**
 * Represents a predicate of some proposition (composed of a predicate and some arguments).
 *
 * @author Meni Adler
 * @since 28/06/2012
 *
 *   
 */
public class Predicate extends DefaultIdentifiableCountable implements TextUnit {

	
	private static final long serialVersionUID = 1L;

	public Predicate(String predicate) {
		super(1);
		this.predicate = predicate;
	}
	
	public Predicate(String predicate, int id) {
		super(id,1);
		this.predicate = predicate;
	}
	
	public Predicate(String predicate, int id, long count) {
		super(id,count);
		this.predicate = predicate;
	}
	
	@Override
	public TextUnit copy() {
		return new Predicate(predicate, id,(long)count);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.TextUnit#getData()
	 */
	@Override
	public String getData() {
		return predicate;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey() {
		return predicate;
	}

	/**
	 * Get the predicate string, represented by this object
	 * 
	 * @return the predicate represented by this object
	 */
	public String getPredicate() {
		return predicate;
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
	
	protected String predicate;

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.items.Externalizable#toKeys()
	 */
	@Override
	public Set<String> toKeys() throws UndefinedKeyException {
		Set<String> ret = new HashSet<String>();
		ret.add(toKey());
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.items.Externalizable#fromKey(java.lang.String)
	 */
	@Override
	public void fromKey(String key) throws UndefinedKeyException {
		predicate = key;
		
	}
}
