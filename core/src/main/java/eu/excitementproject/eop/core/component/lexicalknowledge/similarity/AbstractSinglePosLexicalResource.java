/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;
import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


/**
 * This class handles the operations common to the similarity based lexical resources that have only one part of speech.
 * <p>
 *  The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved. 
 * @author Amnon Lotan
 * @since 23/05/2011
 * 
 */
public abstract class AbstractSinglePosLexicalResource extends AbstractSimilarityLexicalResource 
{
	/**
	 * Ctor
	 * <p>
 *  The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved.
	 * @param limitOnRetrievedRules
	 * @throws LexicalResourceException
	 */
	public AbstractSinglePosLexicalResource(int limitOnRetrievedRules)	throws LexicalResourceException {
		super(limitOnRetrievedRules);
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#getRulesForRight(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String lemma,	PartOfSpeech pos) throws LexicalResourceException {
		return changePosToOriginal(super.getRulesForRight(lemma, getDEFAULT_POS()),pos);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#getRulesForLeft(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return changePosToOriginal(super.getRulesForLeft(lemma, getDEFAULT_POS()),pos);
	}
	
	/**
	 * Convenience method for retrieving rules without specifying the parts of speech - as all methods of this class ignore them. 
	 * 
	 * @param lemma
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String lemma) throws LexicalResourceException 
	{
		return getRulesForRight(lemma, getDEFAULT_POS());
	}

	/**
	 * Convenience method for retrieving rules without specifying the parts of speech - as all methods of this class ignore them.
	 * 
	 * @param lemma
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String lemma) throws LexicalResourceException 
	{
		return getRulesForLeft(lemma, getDEFAULT_POS());
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends RuleInfo>> getRules(String lLemma, PartOfSpeech lPos, String rLemma, PartOfSpeech rPos) throws LexicalResourceException 
	{
		return changePosToOriginal(getRules(lLemma, rLemma),lPos,rPos);
	}
	
	/**
	 *  Convenience method. Returns the (zero or one) rules that match the two lemmas
	 * 
	 * @param lLemma
	 * @param rLemma
	 * @return
	 * @throws LexicalResourceException
	 */
	public List<LexicalRule<? extends RuleInfo>> getRules(String lLemma, String rLemma) throws LexicalResourceException
	{
		return super.getRules(lLemma, getDEFAULT_POS(), rLemma, getDEFAULT_POS());	
	}
	
	///////////////////////////////////////////////// PROTECTED	//////////////////////////////////////////////////////////////////

	protected List<LexicalRule<? extends RuleInfo>> changePosToOriginal(List<LexicalRule<? extends RuleInfo>> rules, PartOfSpeech originalPos) throws LexicalResourceException
	{
		return changePosToOriginal(rules,originalPos,originalPos);
	}

	protected List<LexicalRule<? extends RuleInfo>> changePosToOriginal(List<LexicalRule<? extends RuleInfo>> rules, PartOfSpeech originalLeftPos, PartOfSpeech originalRightPos) throws LexicalResourceException
	{
		List<LexicalRule<? extends RuleInfo>> ret = new ArrayList<LexicalRule<? extends RuleInfo>>(rules.size());
		for (LexicalRule<? extends RuleInfo> rule : rules)
		{
			LexicalRule<? extends RuleInfo> newRule = new LexicalRule<RuleInfo>(rule.getLLemma(), originalLeftPos, rule.getRLemma(), originalRightPos, rule.getConfidence(), rule.getRelation(), rule.getResourceName(), rule.getInfo());
			ret.add(newRule);
		}
		return ret;
	}
	
	protected abstract PartOfSpeech getDEFAULT_POS();
}

