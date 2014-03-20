package eu.excitementproject.eop.distsim.items;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;


/**
 * A simple representation of lemma and part of speech
 *
 * <P>
 * Immutable. Thread-safe
 * 
 * @author Meni Adler
 * @since 19/11/2012
 *
 * 
 */
public class LemmaPos implements Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final String DELIMITER = "#";

	public LemmaPos(String lemma, CanonicalPosTag pos) {
		this.lemma = lemma;
		this.pos = pos;
	}
	
	public LemmaPos() {
		this.lemma = null;;
		this.pos = null;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.items.Externalizable#fromKey(java.lang.String)
	 */
	@Override
	public void fromKey(String key) throws UndefinedKeyException {
		String[] toks = key.split(DELIMITER);
		if (toks.length != 2)
			throw new UndefinedKeyException("Cannot decode key " + key + " to a LemmaPos object, since it contains  one or more serialization delimiters");
		this.lemma = toks[0];
		this.pos = CanonicalPosTag.valueOf(toks[1]);			
	}

	/**
	 * @return the lemma
	 */
	public String getLemma() {
		return lemma;
	}
	
	/**
	 * @return the part-of-speech
	 */
	public CanonicalPosTag getPOS() {
		return pos;	
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Externalizable#toKey()
	 */
	@Override
	public String toKey() throws UndefinedKeyException  {
		if (lemma.contains(DELIMITER))
			throw new UndefinedKeyException("Cannot encode lemma " + lemma + ", since it contains  one or more serialization delimiters");
		StringBuilder sb = new StringBuilder();
		sb.append(lemma);
		sb.append(DELIMITER);
		sb.append(pos == null ? "*" : pos.name());
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Externalizable#toKey()
	 */
	@Override
	public Set<String> toKeys() throws UndefinedKeyException  {
		if (lemma.equals(DELIMITER))
			throw new UndefinedKeyException("Cannot encode lemme " + lemma + ", since it contains  one or more serialization delimiters");
		Set<String> ret = new HashSet<String>();
		if (pos == null) {
			for (CanonicalPosTag relpos : relevantPos)
				ret.add(lemma + DELIMITER + relpos);
		} else
			ret.add(lemma + DELIMITER + pos.name());
		
		return ret;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
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
		LemmaPos other = (LemmaPos) obj;
		if (lemma == null) {
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (pos != other.pos)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(lemma);
		sb.append(":");
		sb.append(pos.name());
		return sb.toString();
	}
	
	protected String lemma;
	protected CanonicalPosTag pos;
	
	protected final static Set<CanonicalPosTag> relevantPos;
	
	static {
		relevantPos = new HashSet<CanonicalPosTag>();
		relevantPos.add(CanonicalPosTag.V);
		relevantPos.add(CanonicalPosTag.N);
		relevantPos.add(CanonicalPosTag.NN);
		relevantPos.add(CanonicalPosTag.ADJ);
		relevantPos.add(CanonicalPosTag.ADV);
	}

}
