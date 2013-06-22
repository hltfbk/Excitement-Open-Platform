/**
 * 
 */
package eu.excitementproject.eop.distsim.items;




/**
 * The RelationBasedFeature defines a general feature which is based on a lemma-pos data with relation
 *   
 * Thread-safe
 * 
 * @author Meni Adler
 * @since 28/06/2012
 *
 */
public class RelationBasedLemmaPosFeature extends RelationBasedFeature<String,LemmaPos>  {


	private static final long serialVersionUID = 1L;
	
	protected final String DELIMITER = "###RelationBasedLemmaPosFeature###";
	
	public RelationBasedLemmaPosFeature(String relation, LemmaPos data) {
		super(relation,data);
	}

	public RelationBasedLemmaPosFeature(String relation, LemmaPos data, AggregatedContext context) {
		super(relation,data, context);
	}

	public RelationBasedLemmaPosFeature(String relation, LemmaPos data, int id, long count) {
		super(relation,data,id,count);
	}
	
	public RelationBasedLemmaPosFeature(String relation, LemmaPos data, AggregatedContext context, int id, long count) {
		super(relation,data,context,id,count);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey()  {
		return data.getFirst() + DELIMITER + data.getSecond().toKey();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(data.getFirst());
		sb.append(DELIMITER);
		sb.append(data.getSecond());
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
}
