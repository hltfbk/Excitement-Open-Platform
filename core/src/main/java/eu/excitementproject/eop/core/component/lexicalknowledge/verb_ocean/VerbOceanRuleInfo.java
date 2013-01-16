/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

/**
 * Holds the VerbOcean score and {@link RelationType} of a rule 
 * @author Amnon Lotan
 *
 * @since Dec 25, 2011
 */
public class VerbOceanRuleInfo implements RuleInfo {

	private static final long serialVersionUID = 7381560881333674211L;
	
	private final RelationType relationType;

	private final double score;

	/**
	 * Ctor
	 * @param relationType
	 * @param score 
	 * @throws LexicalResourceException 
	 */
	public VerbOceanRuleInfo(RelationType relationType, double score) throws LexicalResourceException {
		super();
		if (relationType == null)
			throw new LexicalResourceException("relationType is null");
		if (score <= 0)
			throw new LexicalResourceException("score must be positive, I got " + score);
		this.relationType = relationType;
		this.score = score;
	}
	
	/**
	 * @return the relationType
	 */
	public RelationType getRelationType() {
		return relationType;
	}
	
	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VerbOceanRuleInfo [relationType=" + relationType + ", score="
				+ score + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((relationType == null) ? 0 : relationType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		VerbOceanRuleInfo other = (VerbOceanRuleInfo) obj;
		if (relationType != other.relationType)
			return false;
		if (Double.doubleToLongBits(score) != Double
				.doubleToLongBits(other.score))
			return false;
		return true;
	}
	
	

}

