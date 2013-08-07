/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

import java.util.Set;


/**
 * Instantiation of {@link eu.excitementproject.eop.distsim.items.DefaultElement} with a state of a type string
 * 
 * @author Meni Adler
 * @since 28/06/2012
 *
 * Thread-safe
 */
public class LemmaPosBasedElement extends DeafaultElement<LemmaPos> {


	private static final long serialVersionUID = 1L;


	public LemmaPosBasedElement() {
		super();
		data = new LemmaPos();
	}
	
	public LemmaPosBasedElement(LemmaPos data) {
		super(data);
	}

	public LemmaPosBasedElement(LemmaPos data, AggregatedContext context) {
		super(data, context);
	}

	public LemmaPosBasedElement(LemmaPos data, int id, long count) {
		super(data,id,count);

	}
	
	public LemmaPosBasedElement(LemmaPos data, AggregatedContext context, int id, long count) {
		super(data,context,id,count);
	}	
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey() throws UndefinedKeyException {
		return data.toKey();
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.items.Externalizable#toKeys()
	 */
	@Override
	public Set<String> toKeys() throws UndefinedKeyException {
		return data.toKeys();
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.items.Externalizable#fromKey(java.lang.String)
	 */
	@Override
	public void fromKey(String key) throws UndefinedKeyException {
		data.fromKey(key);
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
		try {
			return toKey().hashCode();
		} catch (UndefinedKeyException e) {
			return 0;
		}
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
		LemmaPosBasedElement other = (LemmaPosBasedElement) obj;
		return data.equals(other.data);
	}



}
