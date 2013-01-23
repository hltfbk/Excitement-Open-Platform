package eu.excitementproject.eop.lap.biu.postagger;

import java.util.List;

/**
 * Part-Of-Speech tagger.
 * The pos-tagger assumes the input is list of tokens, returned by some "tokenizer"
 * (e.g. <A href="http://www.cis.upenn.edu/~treebank/tokenization.html"> Penn-TreeBank tokenization </A>).
 * <P>
 * The input may be list of tokens, represented by java.util.List<String>, or merely a space-delimited string,
 * such that each item in that string is a token (e.g. "I 'm gon na").
 * <P>
 * <B>Note:</B> before starting to work with the pos-tagger, call {@link #init()}. When the
 * pos-tagger is not to be used any-more, call {@link #cleanUp()}. 
 * <P><B>Thread safety: PosTagger is not thread safe. Don't use the same
 * PosTagger instance in two threads.</B>

 * 
 * 
 * @author Asher Stern
 * @since Jan 10, 2011
 *
 */
public interface PosTagger
{
	public void init() throws PosTaggerException;
	public boolean isInitialized();
	
	public void setTokenizedSentence(String sentence) throws PosTaggerException;
	public void setTokenizedSentence(List<String> sentenceTokens) throws PosTaggerException;
	
	public void process() throws PosTaggerException;
	
	public List<PosTaggedToken> getPosTaggedTokens() throws PosTaggerException;
	
	public void cleanUp();
}
