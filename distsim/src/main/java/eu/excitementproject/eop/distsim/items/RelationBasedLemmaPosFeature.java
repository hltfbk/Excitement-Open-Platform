/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;




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
	
	protected final String DELIMITER = "#";
	
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
	public String toKey() throws UndefinedKeyException  {
		String key1 = data.getFirst();
		String key2 = data.getSecond().toKey();
		if (key1.equals(DELIMITER) || key2.equals(DELIMITER))
			throw new UndefinedKeyException("Cannot encode " + key1 + " and " + key2);
		return key1 + DELIMITER + key2;
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
		Externalizable other = (Externalizable) obj;
		try {
			return toKey().equals(other.toKey());
		} catch (UndefinedKeyException e) {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.items.Externalizable#fromKey(java.lang.String)
	 */
	@Override
	public void fromKey(String key) throws UndefinedKeyException {
		String[] props = key.split(DELIMITER);
		if (props.length != 2)
			throw new UndefinedKeyException("Cannot decode " + key + " to ArgumentFeature");
		data.setFirst(props[0]);
		LemmaPos lemmapos = new LemmaPos();
		lemmapos.fromKey(props[1]);
		data.setSecond(lemmapos);
		
	}

}
