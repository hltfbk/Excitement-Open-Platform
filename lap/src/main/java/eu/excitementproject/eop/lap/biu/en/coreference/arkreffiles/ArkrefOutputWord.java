package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * Represents a single word in ArkRef output. Each word might belong to a mention,
 * or mentions, or not.
 * This class contains not only the word, but also the begin markers that appear
 * before the word, and the end markers that appear immediately after the word.
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class ArkrefOutputWord<I extends Info, S extends AbstractNode<I, S>>
{
	public ArkrefOutputWord(String word, List<ArkrefMarker> beginMarkers,
			List<ArkrefMarker> endMarkers)
	{
		super();
		this.word = word;
		this.beginMarkers = beginMarkers;
		this.endMarkers = endMarkers;
	}
	
	
	
	
	public String getWord()
	{
		return word;
	}
	public List<ArkrefMarker> getBeginMarkers()
	{
		return beginMarkers;
	}
	public List<ArkrefMarker> getEndMarkers()
	{
		return endMarkers;
	}
	


	public S getAlignedNode()
	{
		return alignedNode;
	}
	public void setAlignedNode(S alignedNode)
	{
		this.alignedNode = alignedNode;
	}






	private final String word;
	private final List<ArkrefMarker> beginMarkers;
	private final List<ArkrefMarker> endMarkers;
	
	private S alignedNode = null;
}
