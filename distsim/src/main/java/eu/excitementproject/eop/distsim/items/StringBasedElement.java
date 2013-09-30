/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

import java.util.HashSet;
import java.util.Set;


/**
 * Instantiation of {@link eu.excitementproject.eop.distsim.items.DefaultElement} with a state of type string
 * 
 * Thread-safe
 * 
 * @author Meni Adler
 * @since 28/06/2012
 *
 */
public class StringBasedElement extends DeafaultElement<String> {


	private static final long serialVersionUID = 1L;

	public StringBasedElement() {
		super();
	}
	
	public StringBasedElement(String data) {
		super(data);
	}

	public StringBasedElement(String data, AggregatedContext context) {
		super(data, context);
	}

	public StringBasedElement(String data, int id, long count) {
		super(data,id,count);

	}
	
	public StringBasedElement(String data, AggregatedContext context, int id, long count) {
		super(data,context,id,count);
	}	
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey() {
		return data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(data);
		sb.append(",");
		sb.append(id);
		sb.append(",");
		sb.append(count);
		sb.append(">");
		return sb.toString();
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

	@Override
	public Set<String> toKeys() throws UndefinedKeyException {
		Set<String> ret = new HashSet<String>();
		ret.add(data);
		return ret;
	}

	@Override
	public void fromKey(String key) throws UndefinedKeyException {
		data = key;		
	}

}
