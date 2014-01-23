package eu.excitementproject.eop.biutee.rteflow.macro.gap.baseline;

import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapEnvironment;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.InfoObservations;

/**
 * 
 * @author Asher Stern
 * @since Sep 1, 2013
 *
 * @param <I>
 * @param <S>
 */
public class GapBaselineV1Calculator
{
	public GapBaselineV1Calculator(TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesisTree,
			GapEnvironment<ExtendedInfo, ExtendedNode> environment,
			AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria)
	{
		super();
		this.textTree = textTree;
		HypothesisTree = hypothesisTree;
		this.environment = environment;
		this.alignmentCriteria = alignmentCriteria;
	}

	public void calculate()
	{
		uncoveredNodesNamedEntities = new LinkedList<>();
		uncoveredNodesNotNamedEntities = new LinkedList<>();
		uncoveredNodesNonContentWords = new LinkedList<>();
		uncoveredEdges = new LinkedList<>();
		buildMap();
		
		for (ExtendedNode hypothesisNode : TreeIterator.iterableTree(HypothesisTree.getTree()))
		{
			if (InfoObservations.infoHasLemma(hypothesisNode.getInfo()))
			{
				boolean namedEntity = nodeIsNamedEntity(hypothesisNode);
				boolean contentWord = nodeIsContentWord(hypothesisNode);
				if (hasMapped(hypothesisNode))
				{
					if (parentMapped(hypothesisNode))
					{
						// Everything is OK.
					}
					else
					{
						uncoveredEdges.add(hypothesisNode);
					}
				}
				else
				{
					if (namedEntity)
					{
						uncoveredNodesNamedEntities.add(hypothesisNode);
					}
					else if (contentWord)
					{
						uncoveredNodesNotNamedEntities.add(hypothesisNode);
					}
					else
					{
						uncoveredNodesNonContentWords.add(hypothesisNode);
					}
					uncoveredEdges.add(hypothesisNode);
				}
			}
		}
	}
	
	
	
	
	
	public List<ExtendedNode> getUncoveredNodesNamedEntities()
	{
		return uncoveredNodesNamedEntities;
	}

	public List<ExtendedNode> getUncoveredNodesNotNamedEntities()
	{
		return uncoveredNodesNotNamedEntities;
	}

	public List<ExtendedNode> getUncoveredEdges()
	{
		return uncoveredEdges;
	}
	
	public List<ExtendedNode> getUncoveredNodesNonContentWords()
	{
		return uncoveredNodesNonContentWords;
	}
	
	
	////////////// PRIVATE //////////////


	private void buildMap()
	{
		mapHypothesisNodesToText = new SimpleValueSetMap<>();
		for (ExtendedNode hypothesisNode : TreeIterator.iterableTree(HypothesisTree.getTree()))
		{
			for (ExtendedNode textNode : TreeIterator.iterableTree(textTree.getTree()))
			{
				if (lemmaOfNode_lowerCase(hypothesisNode).equals(lemmaOfNode_lowerCase(textNode)))
				{
					mapHypothesisNodesToText.put(hypothesisNode, textNode);
				}
			}
		}
	}
	
	private boolean parentMapped(ExtendedNode hypothesisNode)
	{
		boolean ret = false;
		ExtendedNode hypothesisParent = HypothesisTree.getParentMap().get(hypothesisNode);
		if (hypothesisParent!=null)
		{
			if (hasMapped(hypothesisParent))
			{
				ImmutableSet<ExtendedNode> mappedToHypothesisParent = mapHypothesisNodesToText.get(hypothesisParent);
				for (ExtendedNode textNode : mapHypothesisNodesToText.get(hypothesisNode))
				{
					ExtendedNode textParent = textTree.getParentMap().get(textNode);
					if ( (textParent!=null) && (mappedToHypothesisParent.contains(textParent)) )
					{
						ret = true;
						break;
					}
				}
			}
			else
			{
				// nothing is mapped to hypothesis parent, so obviously an edge is missing.
				ret = false;
			}
		}
		else
		{
			// hypothesis node has no parent - so no edge is missing.
			ret = true;
		}
		
		return ret;
	}
	
	private String lemmaOfNode_lowerCase(ExtendedNode node)
	{
		return InfoGetFields.getLemma(node.getInfo()).toLowerCase();
	}
	
	private boolean nodeIsNamedEntity(ExtendedNode node)
	{
		return (InfoGetFields.getNamedEntityAnnotation(node.getInfo())!=null);
	}
	
	private boolean nodeIsContentWord(ExtendedNode node)
	{
		return InfoObservations.infoIsContentWord(node.getInfo());
	}
	
	private boolean hasMapped(ExtendedNode hypothesisNode)
	{
		boolean ret = false;
		if (mapHypothesisNodesToText.containsKey(hypothesisNode))
		{
			ImmutableSet<ExtendedNode> mapped = mapHypothesisNodesToText.get(hypothesisNode);
			if (mapped!=null)
			{
				if (mapped.size()>0)
				{
					ret = true;
				}
			}
		}
		return ret;
	}

	// input
	protected final TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree;
	protected final TreeAndParentMap<ExtendedInfo, ExtendedNode> HypothesisTree;
	protected final GapEnvironment<ExtendedInfo, ExtendedNode> environment;
	protected final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;

	
	// internals
	private ValueSetMap<ExtendedNode, ExtendedNode> mapHypothesisNodesToText;
	
	// output
	private List<ExtendedNode> uncoveredNodesNamedEntities;
	private List<ExtendedNode> uncoveredNodesNotNamedEntities;
	private List<ExtendedNode> uncoveredNodesNonContentWords;
	private List<ExtendedNode> uncoveredEdges;
}
