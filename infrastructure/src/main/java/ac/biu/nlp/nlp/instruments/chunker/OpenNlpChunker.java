/**
 * 
 */
package ac.biu.nlp.nlp.instruments.chunker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.chunker.ChunkerME;
import ac.biu.nlp.nlp.instruments.postagger.PosTaggedToken;
import ac.biu.nlp.nlp.representation.PennPartOfSpeech.PennPosTag;



/**
 * Wraps {@link opennlp.tools.chunker.Chunker} in {@code JARS\opennlp-tools-1.3.0.jar} to generate chunk tags for the given sequence of words and 
 * POS tags, returning the result as a list of {@link Chunk}s.
 * <br>The tag set used by the english pos model is the Penn Treebank tag set.
 * 
 * @see {@linkplain http://sourceforge.net/apps/mediawiki/opennlp/index.php?title=Chunker}
 * 
 * @author Amnon Lotan
 *
 * @since 26/01/2011
 */
public class OpenNlpChunker extends AbstractChunker
{
	private static final char OPEN_AND_CLOSE = 'O';
	private static final char BEGIN = 'B';
	private static final char INSIDE = 'I';
	private static final String CHUNK_TAG_DELIMETER = "-";

	private opennlp.tools.chunker.Chunker chunker;
	private File chunkerModelFile;
	private List<String> tokens;
	private List<String> strPosTags;
	private List<Chunk> chunks;
	private List<PosTaggedToken> posTaggedTokens;
	
	/**
	 * @param chunkerModelFile e.g. new File("JARS\opennlp-tools-1.3.0\models\english\chunker\EnglishChunk.bin.gz")
	 * @throws ChunkerException 
	 * 
	 */
	public OpenNlpChunker(File chunkerModelFile) throws ChunkerException
	{
		// load chunker
		if(chunkerModelFile == null)
			throw new ChunkerException("no chunker model file specified");
		if (!chunkerModelFile.exists())
			throw new ChunkerException(chunkerModelFile + " doesn't exist");
	
		this.chunkerModelFile = chunkerModelFile;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#initImpl()
	 */
	protected void initImpl() throws ChunkerException
	{
		MaxentModel chunkerModel;
		try {
			chunkerModel = new SuffixSensitiveGISModelReader(chunkerModelFile).getModel();
		} catch (IOException e) {
			throw new ChunkerException("Error constructing a SuffixSensitiveGISModelReader with " + chunkerModelFile + ". See nested.", e);
		}
		chunker = new ChunkerME(chunkerModel);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#set(java.util.List)
	 */
	protected void setTaggedTokensImpl(List<PosTaggedToken> posTaggedTokens) throws ChunkerException
	{
		if(posTaggedTokens == null)
			throw new ChunkerException("null input");
		
		this.posTaggedTokens = new ArrayList<PosTaggedToken>(posTaggedTokens);	// save a copy, use it with the output
		
		tokens = new ArrayList<String>();
		strPosTags = new ArrayList<String>();
		
		for (PosTaggedToken posTaggedToken : posTaggedTokens)
		{
			tokens.add(posTaggedToken.getToken());
			try
			{
				strPosTags.add(PennPosTag.valueOf(posTaggedToken.getPartOfSpeech().toString()).toString());
			} catch (Exception e)
			{
				throw new  ChunkerException("This POS isn't in the PennPosTag set: " + 
						posTaggedToken.getPartOfSpeech().getStringRepresentation() + ", " + posTaggedToken.getToken());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#chunk()
	 */
	protected void chunkImpl() throws ChunkerException
	{
		@SuppressWarnings("unchecked")
		List<String> longChunkTags = chunker.chunk(tokens, strPosTags);
		
		// parse and group the chunk tags into the bigger Chunks they describe
		String openChunkTag = null;
		List<PosTaggedToken> openPosTaggedTokens = null;
		chunks = new ArrayList<Chunk>();
		boolean inAChunk = false;
		
		for(int i = 0; i < tokens.size(); i++)
		{
			// split the raw longChunkTag into chunk tag type and chunk tag
			String[] parts = longChunkTags.get(i).split(CHUNK_TAG_DELIMETER);
			char sequenceChar = parts[0].toUpperCase().charAt(0);
			
			// in case there's nothing to split, it must be a OPEN_AND_CLOSE
			String chunkTag = (parts.length > 1 ) ? parts[1] : new String(new char[]{OPEN_AND_CLOSE});	  
			
			// sanity
			if (!longChunkTags.get(i).contains(CHUNK_TAG_DELIMETER) && sequenceChar != OPEN_AND_CLOSE)
				throw new ChunkerException("bug alert! found a chunk tag that has no '" + CHUNK_TAG_DELIMETER + "' and isn't an '" + 
						OPEN_AND_CLOSE + "', pertaining to " + posTaggedTokens.get(i));
			
			try
			{
				switch(sequenceChar)
				{
					case OPEN_AND_CLOSE:	// close the open chunk (if any) and open and close a new chunk
						if (inAChunk)
							chunks.add(new Chunk(openChunkTag, openPosTaggedTokens));
						
						List<PosTaggedToken> onePosTaggedTokenInAList = new ArrayList<PosTaggedToken>();
						onePosTaggedTokenInAList.add(posTaggedTokens.get(i));
						chunks.add( new Chunk(chunkTag, onePosTaggedTokenInAList) );
						inAChunk = false;				
						break;
						
					case BEGIN:	// close the open chunk (if any) and open a new chunk
						if (inAChunk)
							chunks.add(new Chunk(openChunkTag, openPosTaggedTokens));
						
						openChunkTag = chunkTag;
						openPosTaggedTokens = new ArrayList<PosTaggedToken>();
						openPosTaggedTokens.add(posTaggedTokens.get(i));
						inAChunk = true;
						break;
						
					case INSIDE:	// continue a chunk
						if (!inAChunk)
							throw new ChunkerException("Found a " + INSIDE + " without a " + BEGIN + " tag to open it: " + tokens.get(i) + ", " + chunkTag + ", " + openChunkTag);
						if (!chunkTag.equals(openChunkTag))
							throw new ChunkerException("Found two different chunk tags in the same chunk: " + tokens.get(i) + ", " + chunkTag + ", " + openChunkTag);
					
						openPosTaggedTokens.add(posTaggedTokens.get(i));
						break;
						
					default:
						throw new ChunkerException("Found an unknown chunk tag sequence letter: '" + sequenceChar + "', pertaining to " + tokens.get(i) + ", " + chunkTag + ", " + openChunkTag);
							
				}
			} catch (Exception e)
			{
				throw new ChunkerException("Chunking error occured around " + tokens.get(i) + ", " + chunkTag + ", " + openChunkTag + ". See nested", e);
			}
		}
		// add the last chunk in the pipe
		if (inAChunk)
			chunks.add(new Chunk(openChunkTag, openPosTaggedTokens));
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#getchunks()
	 */
	protected List<Chunk> getchunksImpl() throws ChunkerException
	{
		return chunks;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.chunker.Chunker#cleanUp()
	 */
	public void cleanUp()	{	}
}