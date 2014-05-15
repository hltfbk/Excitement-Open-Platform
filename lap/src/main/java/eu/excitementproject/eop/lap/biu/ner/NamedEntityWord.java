package eu.excitementproject.eop.lap.biu.ner;

import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;


/**
 * Represents a word with a {@link NamedEntity} value assigned to it.
 * <P>
 * If no {@link NamedEntity} was assigned - then the {@link #namedEntity} field
 * is set to <code> null </code>
 * <P>
 * This class is immutable.
 * @author Asher Stern
 *
 */
public final class NamedEntityWord
{
	/**
	 * Straightforward constructor
	 * @param word the word
	 * @param namedEntity its {@link NamedEntity} value. May be <code> null </code>
	 * if no such {@link NamedEntity} was set.
	 */
	public NamedEntityWord(String word, NamedEntity namedEntity)
	{
		this.word = word;
		this.namedEntity = namedEntity;
	}

	/**
	 * Returns the word
	 * @return the word
	 */
	public String getWord()
	{
		return word;
	}
	
	
	/**
	 * Returns the {@link NamedEntity} assigned to that word
	 * @return the {@link NamedEntity} assigned to that word (may be
	 * <code> null </code>).
	 */
	public NamedEntity getNamedEntity()
	{
		return namedEntity;
	}
	
	public String toString() {
		return word + "/" + namedEntity;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((namedEntity == null) ? 0 : namedEntity.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		NamedEntityWord other = (NamedEntityWord) obj;
		if (namedEntity != other.namedEntity) {
			return false;
		}
		if (word == null) {
			if (other.word != null) {
				return false;
			}
		} else if (!word.equals(other.word)) {
			return false;
		}
		return true;
	}

	protected String word;
	protected NamedEntity namedEntity;
}
