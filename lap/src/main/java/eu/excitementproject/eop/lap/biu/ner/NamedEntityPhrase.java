package eu.excitementproject.eop.lap.biu.ner;

import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;


/**
 * Represents a phrase with a {@link NamedEntity} value assigned to it.
 * <P>
 * If no {@link NamedEntity} was assigned - then the {@link #namedEntity} field
 * is set to <code> null </code>
 * <P>
 * This class is immutable.
 * @author Erel Segal
 * @since 2011-12-08
 */
public final class NamedEntityPhrase {
	/**
	 * Straightforward constructor
	 * @param phrase the phrase
	 * @param namedEntity its {@link NamedEntity} value. May be <code> null </code>
	 * if no such {@link NamedEntity} was set.
	 */
	public NamedEntityPhrase(String phrase, NamedEntity namedEntity) {
		this.phrase = phrase;
		this.namedEntity = namedEntity;
	}

	/**
	 * @return the phrase
	 */
	public String getPhrase() {
		return phrase;
	}
	
	/**
	 * @return the {@link NamedEntity} assigned to that phrase (may be
	 * <code> null </code>).
	 */
	public NamedEntity getNamedEntity() {
		return namedEntity;
	}
	
	@Override public String toString() {
		return "["+namedEntity+" "+phrase+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((namedEntity == null) ? 0 : namedEntity.hashCode());
		result = prime * result + ((phrase == null) ? 0 : phrase.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NamedEntityPhrase other = (NamedEntityPhrase) obj;
		if (namedEntity != other.namedEntity) {
			return false;
		}
		if (phrase == null) {
			if (other.phrase != null) {
				return false;
			}
		} else if (!phrase.equals(other.phrase)) {
			return false;
		}
		return true;
	}

	protected String phrase;
	protected NamedEntity namedEntity;
}
