package eu.excitementproject.eop.distsim.items;


/**
 * A default implementation of the {@link Cooccurrence} interface for lexical composed of two text units 
 * and a relation of a generic type R
 *
 * @author Meni Adler
 * @since 20/06/2012
 * 
 * 
 */
public class DefaultCooccurrence<R> extends DefaultIdentifiableCountable implements Cooccurrence<R> {


	private static final long serialVersionUID = 1L;
	
	public DefaultCooccurrence(TextUnit textItem1, TextUnit textItem2, Relation<R> relation,int id, double count) {
		super(id,count);
		this.textItem1 = textItem1;
		this.textItem2 = textItem2;
		this.relation = relation;
	}

	
	public DefaultCooccurrence(TextUnit textItem1, TextUnit textItem2, Relation<R> relation) {
		this.textItem1 = textItem1;
		this.textItem2 = textItem2;
		this.relation = relation;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Cooccurrence#getRelation()
	 */
	@Override
	public Relation<R> getRelation() {
		return relation;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Cooccurrence#getTextItem1()
	 */
	@Override
	public TextUnit getTextItem1() {
		return textItem1;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Cooccurrence#getTextItem2()
	 */
	@Override
	public TextUnit getTextItem2() {
		return textItem2;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Serializable#toKey()
	 */
	@Override
	public String toKey() throws UndefinedKeyException {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(textItem1.getID());
			sb.append(DELIMITER);
			sb.append(relation.toKey());
			sb.append(DELIMITER);
			sb.append(textItem2.getID());
		} catch (InvalidIDException e) {
			throw new UndefinedKeyException();
		}
		return sb.toString(); 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(textItem1);
		sb.append(":");
		sb.append(relation);
		sb.append(":");
		sb.append(textItem2);
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
			return -1;
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
	
	protected static final String DELIMITER = "###DefaultCooccurrence###";
	
	protected TextUnit textItem1;
	protected TextUnit textItem2;
	protected Relation<R> relation;

}
