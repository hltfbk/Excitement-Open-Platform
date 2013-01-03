package ac.biu.nlp.nlp.instrumentscombination;

import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;


/**
 * An {@link AbstractTokenInfo} which can hold only an {@link UnspecifiedPartOfSpeech} 
 * (which stores a {@link CanonicalPosTag})
 * @author Eyal Shnarch
 * @since 14/09/2011
 */
public class UnspecifiedTokenInfo extends TokenInfo{

	private static final long serialVersionUID = -4326639294767149527L;

	private UnspecifiedPartOfSpeech unspecifiedPosTag = null;

	public UnspecifiedTokenInfo(String origStr) {
		super(origStr);
	}

	public UnspecifiedPartOfSpeech getUnspecifiedPosTag() {
		return unspecifiedPosTag;
	}
	
	public void setUnspecifiedPosTag(UnspecifiedPartOfSpeech unspecifiedPosTag){
		this.unspecifiedPosTag = unspecifiedPosTag;
		this.setPosTag(unspecifiedPosTag);
	}

	public void setUnspecifiedPosTag(PartOfSpeech unspecifiedPosTag) throws UnsupportedPosTagStringException{
		this.unspecifiedPosTag = new UnspecifiedPartOfSpeech(unspecifiedPosTag.getCanonicalPosTag());
		this.setPosTag(unspecifiedPosTag);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((unspecifiedPosTag == null) ? 0 : unspecifiedPosTag
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnspecifiedTokenInfo other = (UnspecifiedTokenInfo) obj;
		if (unspecifiedPosTag == null) {
			if (other.unspecifiedPosTag != null)
				return false;
		} else if (!unspecifiedPosTag.equals(other.unspecifiedPosTag))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return origStr + "\t" + namedEntity + "\t" + lemmas + "\t"
				+ unspecifiedPosTag;
	}

	
	
}
