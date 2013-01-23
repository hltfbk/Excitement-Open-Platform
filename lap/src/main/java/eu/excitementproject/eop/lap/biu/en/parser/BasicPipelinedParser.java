package eu.excitementproject.eop.lap.biu.en.parser;

import java.util.List;

import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;


/**
 * Extends {@link AbstractBasicParser} with the ability of providing the input sentence
 * as a list of {@link PosTaggedToken}s.
 * 
 * @author Asher Stern
 * @since Jan 2, 2013
 *
 */
public interface BasicPipelinedParser extends AbstractBasicParser
{
	/**
	 * Sets the sentence, as a list of {@link PosTaggedToken}s,
	 * to be parsed by a subsequent call to {@link #parse()}.
	 * 
	 * @param posTaggedSentence The sentence to be parsed, as a list
	 * of {@link PosTaggedToken}s.
	 */
	public void setSentence(List<PosTaggedToken> posTaggedSentence);

}
