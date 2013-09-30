package eu.excitementproject.eop.lexicalminer.instrumentscombination;

import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;


/**
 * A data type to hold the following information about a term:
 * <ul>
 * <li>original String as found in the text
 * <li>named entity indication
 * <li>list of its possible lemmata (if a POS-tag is provided this list contains a single lemma)
 * </ul> 
 * Since this data structure does not contain a part-of-speech field it will rarely be used,
 * rather other classes which extend it and add the part-of-speech field should be used 
 * (e.g. see {@link TokenInfo} and {@link UnspecifiedTokenInfo}).   	 
 * @author Eyal Shnarch
 * @since 05/07/2011
 */
public class AbstractTokenInfo implements Serializable {

	private static final long serialVersionUID = -3093612147195528062L;

	protected String origStr;
	protected NamedEntity namedEntity = null;
	protected ImmutableList<String> lemmas = null;
	
	public AbstractTokenInfo(String origStr){
		this.origStr = origStr;
	}
		
	public String getOrigStr() {
		return origStr;
	}
	public void setOrigStr(String origStr) {
		this.origStr = origStr;
	}
	/**
	 * if no pos-tag provided, all lemmas for all pos-tags can be stored
	 */
	public ImmutableList<String> getLemmas() {
		return lemmas;
	}
	public String getLemma() throws InstrumentCombinationException{
		if(lemmas != null && lemmas.size() == 1){
			return lemmas.get(0);
		}else{
			throw new InstrumentCombinationException("this token has more than a single lemma");
		}
	}
	/**
	 * if no pos-tag provided, all lemmas for all pos-tags can be stored
	 */
	public void setLemmas(ImmutableList<String> lemmas) {
		this.lemmas = lemmas;
	}
	
	public NamedEntity getNamedEntity() {
		return namedEntity;
	}
	public void setNamedEntity(NamedEntity namedEntity) {
		this.namedEntity = namedEntity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lemmas == null) ? 0 : lemmas.hashCode());
		result = prime * result
				+ ((namedEntity == null) ? 0 : namedEntity.hashCode());
		result = prime * result + ((origStr == null) ? 0 : origStr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractTokenInfo other = (AbstractTokenInfo) obj;
		if (lemmas == null) {
			if (other.lemmas != null)
				return false;
		} else if (!lemmas.equals(other.lemmas))
			return false;
		if (namedEntity != other.namedEntity)
			return false;
		if (origStr == null) {
			if (other.origStr != null)
				return false;
		} else if (!origStr.equals(other.origStr))
			return false;
		return true;
	}
	
	

}
