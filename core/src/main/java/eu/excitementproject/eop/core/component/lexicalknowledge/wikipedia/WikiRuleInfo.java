/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;

/**
 * This record holds a lot if wikipedia relation information.
 * 
 * @author Amnon Lotan
 *
 * @since Dec 4, 2011
 */
public class WikiRuleInfo implements RuleInfo {
	private static final long serialVersionUID = 4421952141328084094L;

	private final ImmutableSetWrapper<WikiExtractionType> extractionTypes;
	
	private final double coocurenceScore;		

	/**
	 * the extraction type with the highest rank
	 */
	private WikiExtractionType bestExtractionType;

	/**
	 * Ctor
	 * @param extractionTypes
	 * @param coocurenceScore
	 * @throws LexicalResourceException 
	 */
	public WikiRuleInfo(Set<WikiExtractionType> extractionTypes, double coocurenceScore) throws LexicalResourceException {
		
		if (extractionTypes == null)
			throw new LexicalResourceException("got null extractionTypes");
		if (extractionTypes.isEmpty())
			throw new LexicalResourceException("got empty extractionTypes");
		this.extractionTypes = new ImmutableSetWrapper<WikiExtractionType>( new LinkedHashSet<WikiExtractionType>(extractionTypes));
		
		this.coocurenceScore = coocurenceScore;
		
		setBestExtractionType(extractionTypes);
	}

	/**
	 * @return the extractionTypes
	 */
	public ImmutableSet<WikiExtractionType> getExtractionTypes() {
		return extractionTypes;
	}

	/**
	 * @return the coocurenceScore
	 */
	public double getCoocurenceScore() {
		return coocurenceScore;
	}
	
	/**
	 * @return the bestExtractionType
	 */
	public WikiExtractionType getBestExtractionType() {
		return bestExtractionType;
	}

	public double getRank()	{
		return bestExtractionType.getRank();
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WikiRuleInfo [coocurenceScore=" + coocurenceScore
				+ ", extractionTypes=" + extractionTypes + "]";
	}
	
	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(coocurenceScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((extractionTypes == null) ? 0 : extractionTypes.hashCode());
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
		WikiRuleInfo other = (WikiRuleInfo) obj;
		if (Double.doubleToLongBits(coocurenceScore) != Double
				.doubleToLongBits(other.coocurenceScore))
			return false;
		if (extractionTypes == null) {
			if (other.extractionTypes != null)
				return false;
		} else if (!extractionTypes.equals(other.extractionTypes))
			return false;
		return true;
	}

	////////////////////////////////////////////////////////// PRIVATE	///////////////////////////////////////////////////
	/**
	 * Find the extraction type with the maximal rank 
	 * @param extractionTypes
	 */
	private void setBestExtractionType(Set<WikiExtractionType> extractionTypes) {
		WikiExtractionType bestExtractionType = WikiExtractionType.WORST_EXTRACTION_TYPE;
		for (WikiExtractionType extractionType : extractionTypes)
			if (extractionType.getRank() > bestExtractionType.getRank())
				bestExtractionType = extractionType;
		this.bestExtractionType = bestExtractionType;
	}	
}

