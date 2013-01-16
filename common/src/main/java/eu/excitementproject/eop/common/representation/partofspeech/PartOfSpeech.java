package eu.excitementproject.eop.common.representation.partofspeech;

import java.io.Serializable;


/**
 * Represents part-of-speech tag.
 * The purpose of this class is to wrap the part-of-speech tag that is given by an external
 * resource (e.g. parser or pos-tagger).
 * <P><B>THIS CLASS IS IMMUTABLE. SUBCLASSES SHOULD BE IMMUTABLE AS WELL!</B><P>
 * This class is abstract, and each sub-class should specify which tags are supported, and
 * how each tag is mapped to a canonical part-of-speech tag: {@link CanonicalPosTag}.
 * For those purposes, each subclass should override the methods {@link #validatePosTagString(String)}
 * and {@link #setCanonicalPosTag()}.
 * <P>
 * <B>Improtant note for all implementations</B>:
 * The validation, from the user's point of view is optional. I.e. the user may write a code like:
 * <code>
 * <pre>
 * MyPartOfSpeech pos = null;
 * try
 * {
 *    MyPartOfSpeech pos = new MyPartOfSpeech("blabla");
 * }
 * catch(UnsupportedPosTagStringException e)
 * {
 *    // do nothing
 * }
 * ...
 * // continue code here, using pos, and assuming pos is not null.
 * </pre>
 * </code>
 * 
 * 
 * @author Asher Stern
 * @since Dec 26, 2010
 *
 */
public abstract class PartOfSpeech implements Serializable
{
	private static final long serialVersionUID = 1130885324993018772L;
	
	/**
	 * Gets a string that represents the part of speech, and constructs a {@link PartOfSpeech}
	 * object based on that string.
	 * <P>
	 * The flow is as follows:
	 * <ul>
	 * <li>The given string is validated. I.e. the given string is tested whether it is one of the
	 * expected strings for the specific {@link PartOfSpeech} subclass.<li>
	 * <li>Then, it is set as the string representation of this {@link PartOfSpeech} object.
	 * It can be later retrieved by the method {@link #getStringRepresentation()}<li>
	 * <li>Then, a canonical-part-of-speech, i.e. one of the predefined part-of-speech
	 * values in {@link CanonicalPosTag} is chosen, based on the given string. That canonical
	 * value can be retrieved later by the method {@link #getCanonicalPosTag()}<li>
	 * </ul>
	 * @param posTagString
	 * @throws UnsupportedPosTagStringException
	 */
	public PartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		UnsupportedPosTagStringException exception = null;
		try
		{
			validatePosTagString(posTagString);
		}
		catch(UnsupportedPosTagStringException e)
		{
			exception = e;
		}
		
		if (null==posTagString)
			posTagString="";
		this.posTagString = posTagString;
		this.canonicalPosTag = null;
		setCanonicalPosTag();
		if (null==this.canonicalPosTag)
		{
			this.canonicalPosTag = CanonicalPosTag.OTHER;
		}
		
		if (exception !=null)
			throw exception;
	}
	
	/**
	 * Returns a canonical representation of this part-of-speech.
	 * @return
	 */
	public CanonicalPosTag getCanonicalPosTag()
	{
		return this.canonicalPosTag;
	}
	
	/**
	 * Returns the string representation of this part-of-speech. This string is merely
	 * the string that was given by the constructor of this part-of-speech.
	 * Usually, the string is given by an external resource (e.g. a parser, of pos-tagger), and
	 * the {@link PartOfSpeech} object wraps it.
	 * @return
	 */
	public String getStringRepresentation()
	{
		return this.posTagString;
	}
	
	/**
	 * Creates a new {@link PartOfSpeech} object, that has the same type of
	 * the callee.
	 * For example: if <code>PennPartOfSpeech.newPartOfSpeech()</code> is called, then a new
	 * <code>PennPartOfSpeech</code> will be created and returned.
	 * @param posTagString
	 * @return
	 */
	public abstract PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getStringRepresentation();
	}
	
	
	@Override
	public int hashCode()
	{
		if (hashCodeSet) return hashCodeValue;
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((canonicalPosTag == null) ? 0 : canonicalPosTag.hashCode());
		result = prime * result
				+ ((posTagString == null) ? 0 : posTagString.hashCode());
		hashCodeValue = result;
		hashCodeSet = true;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartOfSpeech other = (PartOfSpeech) obj;
		if (canonicalPosTag != other.canonicalPosTag)
			return false;
		if (posTagString == null)
		{
			if (other.posTagString != null)
				return false;
		} else if (!posTagString.equals(other.posTagString))
			return false;
		return true;
	}

	/////////////////////////////// PROTECTED AND PRIVATE //////////////////////////////////////
	
	/**
	 * Assuming the field {@link #posTagString} is already set, this function chooses
	 * one of the appropriate values of {@link CanonicalPosTag} enumeration, and <B>sets
	 * the field</B> {@link #canonicalPosTag}.
	 * @throws UnsupportedPosTagStringException if the given string is not supported
	 */
	protected abstract void setCanonicalPosTag();
	
	/**
	 * Validates that the given string is one of the expected strings for that
	 * sub-class of {@link PartOfSpeech}.
	 * @param posTagString a string that represents part-of-speech. Usually, that string
	 * is given by an external resource, e.g. pos-tagger.
	 * @throws UnsupportedPosTagStringException if the string is not supported.
	 */
	protected abstract void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException;
	
	/**
	 * Suppresses the ability to define an empty constructor.
	 */
	@SuppressWarnings("unused")
	private PartOfSpeech(){}
	
	protected String posTagString;
	protected CanonicalPosTag canonicalPosTag;
	
	private transient boolean hashCodeSet = false;
	private transient int hashCodeValue = 0;
}
