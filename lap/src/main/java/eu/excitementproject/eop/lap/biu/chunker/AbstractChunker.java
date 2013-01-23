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
 * <p>These usage restrictions are enforced in {@link AbstractChunker}
 * 
 * @author Amnon Lotan
 *
 * @since 26/01/2011
 */
public abstract class AbstractChunker implements Chunker
{
	private boolean initialized = false;
	private boolean inputSet = false;
	private boolean outputReady = false;

	///////////////////////////////////////////////// public interface //////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#init()
	 */
	public void init() throws ChunkerException
	{
		initImpl();
		
		initialized = true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#setTaggedTokens(java.util.List)
	 */
	public void setTaggedTokens(List<PosTaggedToken> posTaggedTokens) throws ChunkerException
	{
		setTaggedTokensImpl(posTaggedTokens);
		
		inputSet = true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#chunk()
	 */
	public void chunk() throws ChunkerException
	{
		if (!initialized )
			throw new ChunkerException("You must call init() before calling chunk()");
		if (!inputSet )
			throw new ChunkerException("You must call setTaggedTokens() before calling chunk()");
		
		chunkImpl();
		
		inputSet = false;
		outputReady  = true;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#getChunks()
	 */
	public List<Chunk> getChunks() throws ChunkerException
	{
		if (!outputReady)
			throw new ChunkerException("You must call chunk() before calling getchunks()");
		outputReady = false;
		
		return getchunksImpl();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#cleanUp()
	 */
	public abstract void cleanUp();
	
	///////////////////////////////////////////////// protected abstract implementation //////////////////////////////////////////////////

	/**
	 * @throws ChunkerException
	 */
	protected abstract void initImpl() throws ChunkerException;
	
	/**
	 * @param posTaggedTokens a list of the tokens or words of the sequence, and their POS tags.
	 * 
	 * @throws ChunkerException
	 */
	protected abstract void setTaggedTokensImpl(List<PosTaggedToken> posTaggedTokens) throws ChunkerException;
	
	/**
	 *  Generates chunk tags for the given sequence 
	 * @throws ChunkerException
	 */
	protected abstract void chunkImpl() throws ChunkerException;

	/**
	 * return the result in a list
	 * @return a list of chunks
	 * @throws ChunkerException
	 */
	protected abstract List<Chunk> getchunksImpl() throws ChunkerException;
}