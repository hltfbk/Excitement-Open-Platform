/**
 * 
 */
package eu.excitementproject.eop.lap.biu.chunker;

import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;


/**
 * Represents a single "chunk", as created by a standard {@link Chunker}.
 * <br>Examples:
 * 
 * <br> {@code [NP Computer/NNP Software/NNP ]}
 * 
 * <br> {@code [VP will/MD be/VB celebrated/VBN ]}
 * 
 * <p>Immutable.
 * 
 *  @see {@linkplain http://sourceforge.net/apps/mediawiki/opennlp/index.php?title=Chunker}
 *  
 * @author Amnon Lotan
 *
 * @since Feb 7, 2011
 */
public class Chunk
{
	private List<PosTaggedToken> posTaggedTokens;
	private String chunkTag;
	private String toString;

	/**
	 * @throws ChunkerException
	 * 
	 */
	public Chunk(String chunkTag, List<PosTaggedToken> posTaggedTokens) throws ChunkerException
	{
		if (chunkTag == null)
			throw new ChunkerException("got null chunkTag");
		if (posTaggedTokens == null)
			throw new ChunkerException("got null posTaggedTokens");
		if (posTaggedTokens.isEmpty())
			throw new ChunkerException("posTaggedTokens is empty");
		
		this.chunkTag = chunkTag;
		this.posTaggedTokens = new ArrayList<PosTaggedToken>(posTaggedTokens);
		
		StringBuffer buf = new StringBuffer();
		for (PosTaggedToken posTaggedToken : posTaggedTokens)
			buf.append(posTaggedToken + " ");
		
		this.toString = "[" + chunkTag + " " + buf + "]";
	}

	/**
	 * @return the posTaggedTokens
	 */
	public ImmutableListWrapper<PosTaggedToken> getPosTaggedTokens()
	{
		return new ImmutableListWrapper<PosTaggedToken>(posTaggedTokens);
	}

	/**
	 * @return the chunkTag
	 */
	public String getChunkTag()
	{
		return chunkTag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((chunkTag == null) ? 0 : chunkTag.hashCode());
		result = prime * result
				+ ((posTaggedTokens == null) ? 0 : posTaggedTokens.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chunk other = (Chunk) obj;
		if (chunkTag == null)
		{
			if (other.chunkTag != null)
				return false;
		} else if (!chunkTag.equals(other.chunkTag))
			return false;
		if (posTaggedTokens == null)
		{
			if (other.posTaggedTokens != null)
				return false;
		} else if (!posTaggedTokens.equals(other.posTaggedTokens))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return toString;
	}
}
