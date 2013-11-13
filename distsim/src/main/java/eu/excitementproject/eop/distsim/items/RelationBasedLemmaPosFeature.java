/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.distsim.util.Pair;




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
	
	protected final String DELIMITER = "~";
	
	public RelationBasedLemmaPosFeature() {
		super();
	}
	
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
		if (key1.contains(DELIMITER) || key2.contains(DELIMITER))
			throw new UndefinedKeyException("Cannot encode " + key1 + " and " + key2 +", since they contain  one or more serialization delimiters");
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
			throw new UndefinedKeyException("Cannot decode " + key + " to RelationBasedLemmaPosFeature, since it contains  one or more serialization delimiters");
		LemmaPos lemmapos = new LemmaPos();
		lemmapos.fromKey(props[1]);
		data = new Pair<String,LemmaPos>(props[0],lemmapos);
		
	}

}
