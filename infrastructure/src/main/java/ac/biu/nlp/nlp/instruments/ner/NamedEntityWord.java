package ac.biu.nlp.nlp.instruments.ner;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.NamedEntity;


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
	
	protected String word;
	protected NamedEntity namedEntity;
}
