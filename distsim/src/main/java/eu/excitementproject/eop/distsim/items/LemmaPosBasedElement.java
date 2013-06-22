/**
 * 
 */
package eu.excitementproject.eop.distsim.items;


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
	public String toKey() {
		return data.toKey();
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
		LemmaPosBasedElement other = (LemmaPosBasedElement) obj;
		return data.equals(other.data);
	}

}
