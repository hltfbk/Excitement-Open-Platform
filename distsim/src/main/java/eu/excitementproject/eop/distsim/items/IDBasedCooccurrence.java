package eu.excitementproject.eop.distsim.items;


/**
 * A light representation of co-occurrence, based on the ids of the text units and the enum value of the relation 
 * 
 * @author Meni Adler
 * @since 19/07/2012
 *
 */
public class IDBasedCooccurrence<R> extends DefaultIdentifiableCountable implements Externalizable {
	
	private static final long serialVersionUID = 1L;

	protected final String DELIMITER = "###IDBasedCooccurrence###";
	
	public IDBasedCooccurrence(int textUnitID1, int textUnitID2, R relation) {
		this.textUnitID1 = textUnitID1;
		this.textUnitID2 = textUnitID2;
		this.relation = relation;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey()  {
		StringBuilder sb = new StringBuilder();
		sb.append(textUnitID1);
		sb.append(DELIMITER);
		sb.append(relation.toString());
		sb.append(DELIMITER);
		sb.append(textUnitID2);
		return sb.toString();
	}
	
	public int getTextUnitID1() {
		return textUnitID1;
	}
	
	public int getTextUnitID2() {
		return textUnitID2;
	}
	
	public R getRelation() {
		return relation;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(textUnitID1);
		sb.append(":");
		sb.append(relation.toString());
		sb.append(":");
		sb.append(textUnitID2);
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
		ArgumentFeature other = (ArgumentFeature) obj;
		return toKey().equals(other.toKey());
	}
	
	int textUnitID1, textUnitID2;
	R relation;

}
