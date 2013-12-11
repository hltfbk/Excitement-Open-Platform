package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;

/**
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class MarkerToNode<I extends Info, S extends AbstractNode<I, S>>
{
	public MarkerToNode(String entityId, String mentionId,
			Map<S, Integer> depthMap,
			ArrayList<ArkrefOutputWord<I, S>> arkrefOutput, int makerIndexInList)
	{
		super();
		this.entityId = entityId;
		this.mentionId = mentionId;
		this.depthMap = depthMap;
		this.arkrefOutput = arkrefOutput;
		this.makerIndexInList = makerIndexInList;
	}


	public void findNode() throws CoreferenceResolutionException
	{
		validateInput();
		ListIterator<ArkrefOutputWord<I, S>> iterator = arkrefOutput.listIterator(makerIndexInList);
		
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
					if (null==depth) {throw new CoreferenceResolutionException("Bug");}
				}
			}
			else
			{
				S currentNode = word.getAlignedNode();
				if (currentNode != null)
				{
					Integer currentDepth = depthMap.get(currentNode);
					if (null==currentDepth) {throw new CoreferenceResolutionException("Bug");}
					if (depth==null) {throw new CoreferenceResolutionException("Bug");}
					if (currentDepth.intValue()<depth.intValue())
					{
						node = currentNode;
						depth = currentDepth;
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
		ArkrefOutputWord<I, S> word = arkrefOutput.get(makerIndexInList);
		ListIterator<ArkrefOutputWord<I, S>> iterator = arkrefOutput.listIterator(makerIndexInList);
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
	private final int makerIndexInList;
	
	private S node = null;
	private boolean findHasBeenCalled = false;
}
