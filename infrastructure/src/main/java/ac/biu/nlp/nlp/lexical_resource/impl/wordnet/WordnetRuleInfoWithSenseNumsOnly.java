/**
 * 
 */
package ac.biu.nlp.nlp.lexical_resource.impl.wordnet;

import ac.biu.nlp.nlp.instruments.dictionary.wordnet.EmptySynset;
import ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetRelation;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;

/**
 * A {@link WordnetRuleInfo} which holds the ordinal number of sense
 * of the rule sides only. It is useful as part of the input parameters 
 * for the {@link WordnetLexicalResource#getRulesForRight(String, ac.biu.nlp.nlp.representation.PartOfSpeech)} 
 * @author Eyal Shnarch
 * @since 20/03/2012
 */
public class WordnetRuleInfoWithSenseNumsOnly extends WordnetRuleInfo {

	private static final long serialVersionUID = -4077010286925937939L;

	/**
	 * The only information is the sense numbers. -1 means all senses 
	 * The sysnsets are dummy and empty and the relation was arbitrarily picked and should not be used (it doesn't mean a thing).   
	 * @param leftSynsetNo
	 * @param rightSynsetNo
	 * @throws LexicalResourceException
	 */
	public WordnetRuleInfoWithSenseNumsOnly(int leftSynsetNo, int rightSynsetNo) throws LexicalResourceException {
		super(new EmptySynset(), leftSynsetNo, new EmptySynset(), rightSynsetNo, WordNetRelation.SEE_ALSO);
	}
	
}
