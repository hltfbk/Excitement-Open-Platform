/**
 * 
 */
package eu.excitementproject.eop.lap.biu.chunker;

import java.util.List;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;

/**
 * Chunker: Generates chunk tags for the given sequence of words and POS tags, returning the result in a list of "chunks"
 * For example, the {@link OpenNlpChunker} would return <i>I'm gonna go</i> as:
 * <i><br>B-NP:PRP:I
 * <br>B-VP:VBP:'m
 * <br>I-VP:VBG:gon
 * <br>I-VP:TO:na
 * <br>I-VP:VB:go</i>
 * 
 * <P>
 * Usage:
 * <OL>
 * <LI>Call {@link #init()} </LI>
 * <LI>Set the word by either {@link #setTaggedTokens(String)} or {@link #setTaggedTokens(String, PartOfSpeech)} </LI>
 * <LI>Call {@link #chunk()} </LI>
 * <LI>Get the chunks with {@link #getChunks()}  </LI>
 * <LI>Repeat steps 2 - 4 as many times as you want.</LI>
 * <LI>When you are done - call {@link #cleanUp()}, and then don't use the chunker object
 * any more (You can construct a new chunker object if required). </LI>
 * </OL>
 * 
 * <p>These usage restrictions are enforced in {@link AbstractChunker}, which all implementations should extend
 * 
 * @author Amnon Lotan
 *
 * @since 26/01/2011
 */
public interface Chunker
{
	/**
	 * @throws ChunkerException
	 */
	public void init() throws ChunkerException;

	/**
	 * @param posTaggedTokens a list of the tokens or words of the sequence, and their POS tags.
	 * 
	 * @throws ChunkerException
	 */
	public void setTaggedTokens(List<PosTaggedToken> posTaggedTokens) throws ChunkerException;

	/**
	 *  Generates chunk tags for the given sequence 
	 * @throws ChunkerException
	 */
	public void chunk() throws ChunkerException;
	
	/**
	 * return the result in a list
	 * @return a list of {@link Chunk}s
	 * @throws ChunkerException
	 */
	public List<Chunk> getChunks() throws ChunkerException;

	/**
	 * 
	 */
	public abstract void cleanUp();
}
