package eu.excitementproject.eop.core.component.lexicalknowledge;

import java.io.Serializable;

import eu.excitementproject.eop.core.representation.parsetree.PartOfSpeech;

/**
 * <P> [DELETEME_LATER: Imported and extended from BIUTEE 2.4.1, with one modification - a field with TERuleRelation added ] </P> 
 * 
 * This type represents a generalization of lexical relationships between two 
 * words, which are provided by resources like WordNet, VerbOcean and 
 * distributional resources. It has two parts: a left hand side and a right 
 * hand side. The basic arrangement of lexical resource is that the left hand 
 * side has a relationship to the right hand side (like entailments, or nonentailment)
 * Additional pieces of information includes confidence, relation, and resource name. 
 * Lexical rules are parameterized by a type I which allows the type to hold 
 * additional resource-specific properties.
 *
 * <b>Immutable, with hashCode() and equal()</b>
 * @param <I> type of the additional information of the rule
 */

public final class LexicalRule<I extends RuleInfo> implements Serializable
{
	private static final long serialVersionUID = -5588732603979427544L;

	public static final double DEFAULT_CONFIDENCE = 0.5;
	
	private final String leftLemma;
	private final PartOfSpeech leftPos;
	private final String rightLemma;
	private final PartOfSpeech rightPos;
	private final I info;
	private final TERuleRelation relation; 
	private final String originalRelation;
	private final String resourceName;
	private final double confidence;

	/**
	 * Constructor with all parameters 
	 * @param leftLemma
	 * @param leftPos
	 * @param rightLemma
	 * @param rightPos
	 * @param confidence the confidence score of the rule, in [0,1]. If now meaningful confidence score is available, the default is 0.5
	 * @param relation the canonical relation (Entailment / NonEntailment) of LHS to RHS. 
	 * @param originalRelation If the resource uses real relations (like Wordnet or Wiktionary), it's a String name of the relevant relation. Else, null
	 * @param resourceName the resource's name
	 * @param info the additional information of the rule
	 * @throws LexicalResourceException
	 */
	public LexicalRule(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, double confidence, TERuleRelation relation, String originalRelation, String resourceName, I info) 
		throws LexicalResourceException 
	{
		if (leftLemma == null || leftLemma.length() == 0)
			throw new LexicalResourceException("leftLemma is null");
		if (leftPos == null)
			throw new LexicalResourceException("leftPos is null");
		if (rightLemma == null || rightLemma.length() == 0)
			throw new LexicalResourceException("rightLemma is null");
		if (rightPos == null)
			throw new LexicalResourceException("rightPos is null");
		if (confidence < 0 || confidence > 1)
			throw new LexicalResourceException("score must be in the interval [0,1]");
		if (originalRelation != null && originalRelation.length() == 0)
			throw new LexicalResourceException("orgRelation is an empty string");
		if (resourceName == null || resourceName.length() == 0)
			throw new LexicalResourceException("resourceName is null");
		if (info == null)
			throw new LexicalResourceException("info is null");
		
		this.leftLemma = leftLemma;
		this.leftPos = leftPos;
		this.rightLemma = rightLemma;
		this.rightPos = rightPos;
		this.confidence = confidence;
		this.relation = relation;
		this.originalRelation = originalRelation; 
		this.resourceName = resourceName;
		this.info = info;
	}
	
	/**
	 * Constructor with {@link #DEFAULT_CONFIDENCE}
	 * @param leftLemma
	 * @param leftPos
	 * @param rightLemma
	 * @param rightPos
	 * @param relation canonical relation of LHS to RHS. 
	 * @param originalRelation If the resource uses real relations (like Wordnet or Wiktionary), it's a String name of the relevant relation. Else, null 
	 * represents the resource's name
	 * @param resourceName the resource's name
	 * @param info the additional information of the rule
	 * @throws LexicalResourceException
	 */
	public LexicalRule(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos, TERuleRelation relation, String originalRelation, String resourceName, I info) throws LexicalResourceException
	{
		this(leftLemma, leftPos, rightLemma, rightPos, DEFAULT_CONFIDENCE, relation, originalRelation, resourceName, info);
	}
	
	
	public String getLLemma() {
		return leftLemma;
	}

	public String getRLemma() {
		return rightLemma;
	}

	public PartOfSpeech getLPos() {
		return leftPos;
	}

	public PartOfSpeech getRPos() {
		return rightPos;
	}

	/**
	 * Get the additional information of the rule
	 * @return
	 */
	public I getInfo() {
		return info;
	}
	
	/**
	 * Returns the canonical relation from LHS to RHS (Entailment or NonEntailment) 
	 * @return relation enum 
	 */
	public TERuleRelation getRelation()
	{
		return relation;
	}
	
	
	/**
	 * If the resource uses real relations (like Wordnet or Wiktionary), return a String name of the relevant relation. Else, return null 
	 * the resource's name.<br>
	 * Note that, if applicable for the implemented {@link LexicalResource}, the {@link RuleInfo} object holds a {@code getTypedRelation()} method that returns the relation as an enum 
	 * @return relation name
	 */
	public String getOriginalRelation()
	{
		return originalRelation;
	}
	
	/**
	 * Return the resource's name
	 * @return
	 */
	public String getResourceName()
	{
		return resourceName;
	}
	
	/**
	 * get the confidence score of the rule, in [0,1]. If now meaningful confidence score is available, the default is 0.5
	 * @return
	 */
	public double getConfidence()
	{
		return confidence;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LexRule [leftLemma=" + leftLemma + ", leftPos=" + leftPos + ", rightLemma="
				+ rightLemma + ", rightPos=" + rightPos + ", relation="	+ relation + ", info=" + 
				info  + ", resourceName=" + resourceName + ", confidence="	+ confidence + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(confidence);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((leftLemma == null) ? 0 : leftLemma.hashCode());
		result = prime * result + ((leftPos == null) ? 0 : leftPos.hashCode());
		result = prime * result + ((rightLemma == null) ? 0 : rightLemma.hashCode());
		result = prime * result + ((rightPos == null) ? 0 : rightPos.hashCode());
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
		result = prime * result
				+ ((originalRelation == null) ? 0 : originalRelation.hashCode());
		result = prime * result
				+ ((resourceName == null) ? 0 : resourceName.hashCode());
		return result;
	}

	/**
	 * Equivalence of rules cares about the implementation specific rule info
	 * 
	 *  (non-Javadoc)
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
		@SuppressWarnings("rawtypes")
		LexicalRule other = (LexicalRule) obj;
		if (Double.doubleToLongBits(confidence) != Double
				.doubleToLongBits(other.confidence))
			return false;
		// the implementation specific rule info counts
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (leftLemma == null) {
			if (other.leftLemma != null)
				return false;
		} else if (!leftLemma.equals(other.leftLemma))
			return false;
		if (leftPos == null) {
			if (other.leftPos != null)
				return false;
		} else if (!leftPos.equals(other.leftPos))
			return false;
		if (rightLemma == null) {
			if (other.rightLemma != null)
				return false;
		} else if (!rightLemma.equals(other.rightLemma))
			return false;
		if (rightPos == null) {
			if (other.rightPos != null)
				return false;
		} else if (!rightPos.equals(other.rightPos))
			return false;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		if (originalRelation == null) {
			if (other.originalRelation != null)
				return false;
		} else if (!originalRelation.equals(other.originalRelation))
			return false;
		if (resourceName == null) {
			if (other.resourceName != null)
				return false;
		} else if (!resourceName.equals(other.resourceName))
			return false;
		return true;
	}
}
