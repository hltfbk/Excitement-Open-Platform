/**
 * 
 */
package eu.excitementproject.eop.distsim.items;



import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * The ArgumentFeature defines a feature which is based on a string with a PredicateArgumentSlots relation
 * 
 * Thread-safe
 * 
 * @author Meni Adler
 * @since 28/06/2012
 *
 */
public class ArgumentFeature extends RelationBasedFeature<PredicateArgumentSlots,String>  {


	private static final long serialVersionUID = 1L;
	
	protected final String DELIMITER = "#";
	
	public ArgumentFeature() {
		super();
	}

	
	public ArgumentFeature(PredicateArgumentSlots relation, String data) {
		super(relation,data);
	}

	public ArgumentFeature(PredicateArgumentSlots relation, String data, AggregatedContext context) {
		super(relation,data, context);
	}

	public ArgumentFeature(PredicateArgumentSlots relation, String data, int id, long count) {
		super(relation,data,id,count);
	}
	
	public ArgumentFeature(PredicateArgumentSlots relation, String data, AggregatedContext context, int id, long count) {
		super(relation,data,context,id,count);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey() throws UndefinedKeyException  {
		String key1 = data.getFirst().name();
		String key2 = data.getSecond();
		if (key1.contains(DELIMITER) || key2.contains(DELIMITER))
			throw new UndefinedKeyException("Cannot encode " + key1 + " and " + key2 + ", since they contain one or more serialization delimiters");
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
		sb.append(data.getFirst().name());
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
			throw new UndefinedKeyException("Cannot decode " + key + " to ArgumentFeature, since it contains  one or more serialization delimiters");
		data = new Pair<PredicateArgumentSlots,String>(PredicateArgumentSlots.valueOf(props[0]),props[1]);		
	}
}
