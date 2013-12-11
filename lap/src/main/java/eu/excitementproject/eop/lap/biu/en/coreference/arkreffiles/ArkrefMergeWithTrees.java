package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;

/**
 * Merges ArkRef output with parse-trees, such that each mention is mapped to
 * a parse-tree node.
 * The mapping is given as a {@link BidirectionalMap}. A {@link TreeCoreferenceInformation}
 * can be built based on this map, as done in {@link ArkrefFilesCoreferenceResolver}.
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 * @param <I>
 * @param <S>
 */
public class ArkrefMergeWithTrees<I extends Info, S extends AbstractNode<I, S>>
{
	public ArkrefMergeWithTrees(List<S> trees,
			ArrayList<ArkrefOutputWord<I, S>> arkrefOutput)
	{
		super();
		this.trees = trees;
		this.arkrefOutput = arkrefOutput;
	}


	public void merge() throws CoreferenceResolutionException
	{
		init();
		mergedOutput = new SimpleBidirectionalMap<>();
		S currentTree = trees.iterator().next();
		ListIterator<ArkrefOutputWord<I,S>> arkrefOutputIterator = arkrefOutput.listIterator();
		while (arkrefOutputIterator.hasNext())
		{
			ArkrefOutputWord<I,S> word = arkrefOutputIterator.next();
			
			S node = word.getAlignedNode();
			if (node!=null)
			{
				currentTree = mapNodeToItsTree.get(node);
			}
			else
			{
				//logger.warn("Arkref openning word has no aligned parse-tree node.");
			}
			if (currentTree==null) {throw new CoreferenceResolutionException("Bug");}
			
			List<ArkrefMarker> beginMarkers = word.getBeginMarkers();
			if (beginMarkers!=null)
			{
				for (ArkrefMarker beginMarker : beginMarkers)
				{
					MarkerToNode<I, S> markerToNode = new MarkerToNode<I, S>(
							beginMarker.getEntityId(),beginMarker.getMentionId(),
							depthMaps.get(currentTree),arkrefOutput,
							arkrefOutputIterator.nextIndex()-1
							);
					markerToNode.findNode();
					S foundNode = markerToNode.getNode();
					if (foundNode != null)
					{
						if (!mergedOutput.leftContains(foundNode))
						{
							mergedOutput.put(foundNode, new ArkrefMention(beginMarker.getMentionId(), beginMarker.getEntityId()));
						}
						else
						{
							//logger.warn("A parse-tree node has been detected as a representator of two different entities.");
						}
					}
				}
			}
		}
	}

	
	public BidirectionalMap<S, ArkrefMention> getMergedOutput()
	{
		return mergedOutput;
	}

	
	
	private void init() throws CoreferenceResolutionException
	{
		if (trees.size()==0) {throw new CoreferenceResolutionException("No trees");}
		mapNodeToItsTree = new LinkedHashMap<>();
		for (S tree : trees)
		{
			for (S node : TreeIterator.iterableTree(tree))
			{
				mapNodeToItsTree.put(node, tree);
			}
		}
		
		depthMaps = new LinkedHashMap<>();
		for (S tree : trees)
		{
			depthMaps.put(tree, ArkreffilesUtils.mapNodesToDepth(tree));
		}
	}
	


	// input
	private final List<S> trees;
	private final ArrayList<ArkrefOutputWord<I,S>> arkrefOutput;
	
	// internals
	private Map<S, S> mapNodeToItsTree;
	private Map<S, Map<S, Integer>> depthMaps;
	
	// output
	private BidirectionalMap<S, ArkrefMention> mergedOutput;
}
