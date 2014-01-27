package eu.excitementproject.eop.lexicalminer.instrumentscombination;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


/**
 * An {@link AbstractTokenInfo} with an additional {@link PartOfSpeech} field 
 * @author Eyal Shnarch
 * @since 14/09/2011
 */
public class TokenInfo extends AbstractTokenInfo {

	private static final long serialVersionUID = 4807717737368406081L;

	public TokenInfo(String origStr) {
		super(origStr);
	}
		
	private PartOfSpeech posTag = null;
	
	
	public PartOfSpeech getPosTag() {
		return posTag;
	}
	public void setPosTag(PartOfSpeech posTag) {
		this.posTag = posTag;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((posTag == null) ? 0 : posTag.hashCode());
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
		TokenInfo other = (TokenInfo) obj;
		if (posTag == null) {
			if (other.posTag != null)
				return false;
		} else if (!posTag.equals(other.posTag))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return origStr + "\t" + namedEntity + "\t" + (lemmas != null && lemmas.size() == 1? lemmas.get(0): lemmas) + "\t" + posTag;
	}
}
