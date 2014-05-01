/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wordnet;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.SensedRuleInfo;


/**
 * <b>Immutable</b>
 * 
 * @author Amnon Lotan
 * @since 28/05/2011
 * 
 */
public class WordnetRuleInfo implements SensedRuleInfo<Synset, WordNetRelation>
{
	private static final long serialVersionUID = -3031883846955372862L;

	private final Synset leftSynset;
	private final Synset rightSynset;
	/**
	 * ordinal of the left synset with regards to the left term of the {@link LexicalRule}
	 */
	private final int leftSynsetNo;
	/**
	 * ordinal of the right synset with regards to the right term of the {@link LexicalRule}
	 */
	private final int rightSynsetNo;
	private final WordNetRelation relation;
	
	/**
	 * Ctor
	 * @param lSynset
	 * @param leftSynsetNo ordinal of the left synset with regards to the left term of the {@link LexicalRule}
	 * @param rSynset
	 * @param rightSynsetNo ordinal of the right synset with regards to the right term of the {@link LexicalRule}
	 * @param relation2
	 * @throws LexicalResourceException
	 */
	public WordnetRuleInfo(Synset lSynset, int leftSynsetNo, Synset rSynset, int rightSynsetNo, WordNetRelation relation2) throws LexicalResourceException
	{
		if (lSynset == null)
			throw new LexicalResourceException("got null left synset");
		if (rSynset == null)
			throw new LexicalResourceException("got null right synset");
		if (relation2 == null)
			throw new LexicalResourceException("got null relation");
		if (leftSynsetNo != -1 && leftSynsetNo < 1)	//-1 represents all senses
			throw new LexicalResourceException("leftSynsetNo must be positive. got " + leftSynsetNo);
		if (rightSynsetNo != -1 && rightSynsetNo < 1)
			throw new LexicalResourceException("rightSynsetNo must be positive. got " + rightSynsetNo);
		
		this.leftSynset = lSynset;
		this.rightSynset = rSynset;
		this.leftSynsetNo = leftSynsetNo;
		this.rightSynsetNo = rightSynsetNo;
		this.relation = relation2;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.WordnetRuleInfo#getLSynset()
	 */
	public Synset getLeftSense() {
		return leftSynset;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.WordnetRuleInfo#getRSynset()
	 */
	public Synset getRightSense() {
		return rightSynset;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.SensedRuleInfo#getTypedRelation()
	 */
	public WordNetRelation getTypedRelation() {
		return relation;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.SensedRuleInfo#getLeftSenseNo()
	 */
	@Override
	public int getLeftSenseNo() {
		return leftSynsetNo;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.rule.SensedRuleInfo#getRightSenseNo()
	 */
	@Override
	public int getRightSenseNo() {
		return rightSynsetNo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WordnetRuleInfo [leftSynset=" + leftSynset + ", rightSynset=" + rightSynset
				+ ", relation=" + relation + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((leftSynset == null) ? 0 : leftSynset.hashCode());
		result = prime * result + leftSynsetNo;
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
		result = prime * result
				+ ((rightSynset == null) ? 0 : rightSynset.hashCode());
		result = prime * result + rightSynsetNo;
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
		WordnetRuleInfo other = (WordnetRuleInfo) obj;
		if (leftSynset == null) {
			if (other.leftSynset != null)
				return false;
		} else if (!leftSynset.equals(other.leftSynset))
			return false;
		if (leftSynsetNo != other.leftSynsetNo)
			return false;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		if (rightSynset == null) {
			if (other.rightSynset != null)
				return false;
		} else if (!rightSynset.equals(other.rightSynset))
			return false;
		if (rightSynsetNo != other.rightSynsetNo)
			return false;
		return true;
	}
}

