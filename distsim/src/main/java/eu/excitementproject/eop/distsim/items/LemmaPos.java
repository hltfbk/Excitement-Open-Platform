package eu.excitementproject.eop.distsim.items;

import java.io.Serializable;

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
public class LemmaPos implements Serializable, Externalizable {

	private static final long serialVersionUID = 1L;

	protected final String DELIMITER = "###LemmaPos###";

	public LemmaPos(String lemma, CanonicalPosTag pos) {
		this.lemma = lemma;
		this.pos = pos;
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
	public String toKey()  {
		StringBuilder sb = new StringBuilder();
		sb.append(lemma);
		sb.append(DELIMITER);
		sb.append(pos == null ? "*" : pos.name());
		return sb.toString();
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

}
