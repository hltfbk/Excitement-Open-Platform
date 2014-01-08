package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;

/**
 * Finds the parse-tree node which corresponds to the given mention, which is
 * given as by the ArkRef-marker.
 * <P>
 * Explanation: The mention in ArkRef output is surrounded by a begin-marker
 * and an end-marker.
 * These two markers wrap several words, each corresponds to a parse-tree node.
 * Now, the task is to choose <B>one</B> parse-tree-node for the mention (one from
 * all nodes aligned to the wrapped words).
 * This class performs this task. It chooses one parse-tree-node.
 * This is done by taking the parse-tree-node which has the lowest depth (i.e.,
 * closest to the parse-tree-root).
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class MarkerToNode<I extends Info, S extends AbstractNode<I, S>>
{
	public MarkerToNode(String entityId, String mentionId,
			Map<S, Integer> depthMap,
			ArrayList<ArkrefOutputWord<I, S>> arkrefOutput, int markerIndexInList)
	{
		super();
		this.entityId = entityId;
		this.mentionId = mentionId;
		this.depthMap = depthMap;
		this.arkrefOutput = arkrefOutput;
		this.markerIndexInList = markerIndexInList;
	}


	public void findNode() throws CoreferenceResolutionException
	{
		validateInput();
		ListIterator<ArkrefOutputWord<I, S>> iterator = arkrefOutput.listIterator(markerIndexInList);
		
		node = null;
		Integer depth = null; // infinity
		boolean endEncountered = false;
		
		while ( (iterator.hasNext()) && (!endEncountered) )
		{
			ArkrefOutputWord<I, S> word = iterator.next();
			if (node==null)
			{
				node = word.getAlignedNode();
				if (node!=null)
				{
					depth = depthMap.get(node);
					if (null==depth)
					{
						node = null;
						depth = null;
					}
				}
			}
			else
			{
				S currentNode = word.getAlignedNode();
				if (currentNode != null)
				{
					Integer currentDepth = depthMap.get(currentNode);
					if (currentDepth!=null)
					{
						if (depth==null) {throw new CoreferenceResolutionException("Bug");}
						if (currentDepth.intValue()<depth.intValue())
						{
							node = currentNode;
							depth = currentDepth;
						}
					}
				}
			}
			
			List<ArkrefMarker> endMarkers = word.getEndMarkers();
			for (ArkrefMarker endMarker : endMarkers)
			{
				if ( (endMarker.getEntityId().equals(entityId)) && (endMarker.getMentionId().equals(mentionId)) )
				{
					endEncountered = true;
				}
				//else if (endMarker.getEntityId().equals(entityId)) {throw new CoreferenceResolutionException("Malformed ArkRef output. Entity mentions of entity "+entityId+" overlap.");}
				//It is OK that mentions of the same entity overlap. For example: <mention mentionid="1" entityid="1_2">ECB spokeswoman , <mention mentionid="2" entityid="1_2">Regina Schueller</mention> ,</mention>
			}
		}
		if (!endEncountered) {throw new CoreferenceResolutionException("Malformed Arkref output. Marker "+mentionId+"/"+entityId+" has no closing tag.");}
		
		findHasBeenCalled = true;
	}
	
	
	public S getNode() throws CoreferenceResolutionException
	{
		if (!findHasBeenCalled) throw new CoreferenceResolutionException("Find has not been called.");
		return node;
	}






	private void validateInput() throws CoreferenceResolutionException
	{
		ArkrefOutputWord<I, S> word = arkrefOutput.get(markerIndexInList);
		ListIterator<ArkrefOutputWord<I, S>> iterator = arkrefOutput.listIterator(markerIndexInList);
		if (iterator.next()!=word) {throw new CoreferenceResolutionException("Bug");}
		
		List<ArkrefMarker> beginMarkers = word.getBeginMarkers();
		boolean found = false;
		for (ArkrefMarker marker : beginMarkers)
		{
			if ( (marker.getEntityId().equals(entityId)) && (marker.getMentionId().equals(mentionId)) )
			{
				if (marker.isBegin()) {} else {throw new CoreferenceResolutionException("Bug");}
				found = true;
				break;
			}
		}
		if (!found) {throw new CoreferenceResolutionException("Bug");}
	}
	
	
	
	private final String entityId;
	private final String mentionId;
	private final Map<S, Integer> depthMap;
	private final ArrayList<ArkrefOutputWord<I, S>> arkrefOutput;
	private final int markerIndexInList;
	
	private S node = null;
	private boolean findHasBeenCalled = false;
}
