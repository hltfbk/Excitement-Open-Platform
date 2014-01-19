package eu.excitementproject.eop.distsim.items;


import java.util.Set;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;

/**
 * Instantiation of {@link DefaultTextUnit} with a state of type string
 * 
 * Thread-safe
 * @author Meni Adler
 * @since 20/06/2012
 * 
 */
public class LemmaPosTextUnit extends DeafaultTextUnit<LemmaPos> {

	private static final long serialVersionUID = 1L;


	public LemmaPosTextUnit() {
		this(new LemmaPos());
	}

	public LemmaPosTextUnit(String lemma, CanonicalPosTag pos) {
		this(new LemmaPos(lemma,pos));
	}

	public LemmaPosTextUnit(LemmaPos data) {
		super(data);
	}
	
	public LemmaPosTextUnit(LemmaPos data, int id, long count) {
		super(data, id,count);
	}
	
	@Override
	public TextUnit copy() {
		return new LemmaPosTextUnit(data, id,(long)count);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.KeyExternalizable#toKey()
	 */
	@Override
	public String toKey() throws UndefinedKeyException  {
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
		LemmaPosTextUnit other = (LemmaPosTextUnit)obj;		
		return data.equals(other.data);
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
	
}

