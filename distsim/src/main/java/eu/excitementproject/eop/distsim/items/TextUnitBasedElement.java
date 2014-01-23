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
public class TextUnitBasedElement extends DeafaultElement<Integer> {


	private static final long serialVersionUID = 1L;


	public TextUnitBasedElement(int textUnitID) {
		super(textUnitID);
	}

	public TextUnitBasedElement(int textUnitID, AggregatedContext context) {
		super(textUnitID, context);
	}

	public TextUnitBasedElement(int textUnitID, int id, long count) {
		super(textUnitID,id,count);

	}
	
	public TextUnitBasedElement(int textUnitID, AggregatedContext context, int id, long count) {
		super(textUnitID,context,id,count);
	}	
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey() {
		return Integer.toString(data);
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
		return data;
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
		TextUnitBasedElement other = (TextUnitBasedElement) obj;
		return data == other.data;
	}

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
		data = Integer.parseInt(key);
		
	}

}
