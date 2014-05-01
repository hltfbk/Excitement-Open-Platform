/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wiktionary;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionaryRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wiktionary.WiktionarySense;
import eu.excitementproject.eop.core.component.lexicalknowledge.SensedRuleInfo;

/**
 * In each {@link WiktionaryLexicalResource}-rule, either the left sense or the right sense may be null! That's cos Wiktionary's relations point from a sense to a lemma (which 
 * has many POSs and senses). Hence the private Ctor and the three static workaround Ctors, that implement this trinity. 
 * <p>
 * <b>Immutable</b>
 * @author Amnon Lotan
 * @since 21/06/2011
 * 
 */
public class WiktionaryRuleInfo implements SensedRuleInfo<WiktionarySense, WiktionaryRelation>
{
	private static final long serialVersionUID = -5296114553097540311L;
	
	private final WiktionarySense leftSense;
	private final WiktionarySense rightSense;
	private final WiktionaryRelation relation;

	/**
	 * private Ctor, that allows null senses
	 * @param relation
	 * @param leftSense
	 * @param leftSense
	 * @throws LexicalResourceException
	 */
	private WiktionaryRuleInfo(WiktionarySense leftSense, WiktionarySense rightSense, WiktionaryRelation relation) throws LexicalResourceException 
	{
		if (relation == null)
			throw new LexicalResourceException("relation is null");
		this.leftSense = leftSense;
		this.rightSense = rightSense;
		this.relation = relation;
	}
	
	/**
	 * @param leftSense
	 * @param rightSense
	 * @param relation
	 * @return
	 * @throws LexicalResourceException
	 */
	public static WiktionaryRuleInfo newWktRuleInfo(WiktionarySense leftSense, WiktionarySense rightSense, WiktionaryRelation relation) throws LexicalResourceException {
		if (leftSense == null)
			throw new LexicalResourceException("Left sense is null");
		if (rightSense == null)
			throw new LexicalResourceException("Right sense is null");
		return new WiktionaryRuleInfo(leftSense, rightSense, relation);
	}
	
	/**
	 * @param relation
	 * @param sense
	 * @return
	 * @throws LexicalResourceException 
	 */
	public static WiktionaryRuleInfo newRightSenseWktRuleInfo(WiktionarySense sense, WiktionaryRelation relation) throws LexicalResourceException {
		if (sense == null)
			throw new LexicalResourceException("Right sense is null");
		return new WiktionaryRuleInfo(null, sense, relation);
	}

	/**
	 * @param relation
	 * @param sense
	 * @return
	 * @throws LexicalResourceException 
	 */
	public static WiktionaryRuleInfo newLeftSenseWktRuleInfo(WiktionarySense sense, WiktionaryRelation relation) throws LexicalResourceException {
		if (sense == null)
			throw new LexicalResourceException("Left sense is null");
		return new WiktionaryRuleInfo(sense, null, relation);
	}
	
	public WiktionarySense getLeftSense() {
		return leftSense;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.SensedRuleInfo#getRSense()
	 */
	public WiktionarySense getRightSense() {
		return rightSense;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.SensedRuleInfo#getTypedRelation()
	 */
	public WiktionaryRelation getTypedRelation() {
		return relation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WiktionaryRuleInfo [lWiktionarySense=" + leftSense + ", rWiktionarySense="
				+ rightSense + ", relation=" + relation + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((leftSense == null) ? 0 : leftSense.hashCode());
		result = prime * result
				+ ((rightSense == null) ? 0 : rightSense.hashCode());
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
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
		WiktionaryRuleInfo other = (WiktionaryRuleInfo) obj;
		if (leftSense == null) {
			if (other.leftSense != null)
				return false;
		} else if (!leftSense.equals(other.leftSense))
			return false;
		if (rightSense == null) {
			if (other.rightSense != null)
				return false;
		} else if (!rightSense.equals(other.rightSense))
			return false;
		if (relation != other.relation)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.SensedRuleInfo#getLeftSenseNo()
	 */
	@Override
	public int getLeftSenseNo() {
		return leftSense.getSenseNo();
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.SensedRuleInfo#getRightSenseNo()
	 */
	@Override
	public int getRightSenseNo() {
		return rightSense.getSenseNo();
	}
}

