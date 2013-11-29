package eu.excitementproject.eop.distsim.items;

import java.util.HashSet;
import java.util.Set;


/**
 * A light representation of co-occurrence, based on the ids of the text units and the enum value of the relation 
 * 
 * @author Meni Adler
 * @since 19/07/2012
 *
 */
@SuppressWarnings("serial")
public class IDBasedCooccurrence<R> extends DefaultIdentifiableCountable implements Externalizable {
	
	protected final String DELIMITER = "#";
	
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
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.items.Externalizable#toKeys()
	 */
	@Override
	public Set<String> toKeys() throws UndefinedKeyException {
		Set<String> ret = new HashSet<String>();
		ret.add(toKey());
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromKey(String key) throws UndefinedKeyException {
		String[] props = key.split(DELIMITER);
		if (props.length != 3)
			throw new UndefinedKeyException("Cannot decode key " + key + " to a LemmaPos object, since it contains  one or more serialization delimiters");
		try {
			textUnitID1 = Integer.parseInt(props[0]);
			relation = (R) props[1];
			textUnitID1 = Integer.parseInt(props[2]);
		} catch (Exception e) {
			throw new UndefinedKeyException(e);
		}
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
		try {
			return toKey().equals(other.toKey());
		} catch (UndefinedKeyException e) {
			return false;
		}
	}
	
	int textUnitID1, textUnitID2;
	R relation;
}
