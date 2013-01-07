package ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.builder;

import ac.biu.nlp.nlp.engineml.datastructures.CanonicalLemmaAndPos;
import ac.biu.nlp.nlp.engineml.datastructures.LemmaAndPos;
import ac.biu.nlp.nlp.engineml.operations.rules.lexicalchain.ChainOfLexicalRules;

/**
 * An internal representation of and object that might be either
 * {@link ChainOfLexicalRules} or a single word ({@link LemmaAndPos}).
 * Used by {@link BuilderSingleWord}.
 * 
 * @see BuilderSingleWord
 * 
 * @author Asher Stern
 * @since Jan 19, 2012
 *
 */
public final class ChainOrWord
{
	public ChainOrWord(ChainOfLexicalRules chain)
	{
		this.chain = chain;
		this.word = null;
	}
	
	public ChainOrWord(CanonicalLemmaAndPos word)
	{
		this.word = word;
		this.chain = null;
	}
	
	

	public ChainOfLexicalRules getChain()
	{
		return chain;
	}

	public CanonicalLemmaAndPos getWord()
	{
		return word;
	}



	private final ChainOfLexicalRules chain;
	private final CanonicalLemmaAndPos word;
}
