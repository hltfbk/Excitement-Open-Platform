package eu.excitementproject.eop.transformations.alignment;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * Given two parse-trees, one of the text (or generated from the text) and one of
 * the hypothesis, this class calculates which nodes, edges and "triples" of the
 * hypothesis parse tree exist (aligned to) in the text-parse-tree.
 * 
 * 
 * @author Asher Stern
 * @since May 28, 2012
 *
 */
@NotThreadSafe
public abstract class AbstractAlignmentCalculator<T, S extends AbstractNode<T, S>>
{
	public AbstractAlignmentCalculator(
			AlignmentCriteria<T, S> alignmentCriteria,
			TreeAndParentMap<T, S> textTree,
			TreeAndParentMap<T, S> hypothesisTree)
	{
		super();
		this.alignmentCriteria = alignmentCriteria;
		this.textTree = textTree;
		this.hypothesisTree = hypothesisTree;
	}

	public ValueSetMap<S, S> getMapAlignedTriplesFromHypothesisToText()
	{
		if (null==mapAlignedTriplesFromHypothesisToText)
		{
			createMapAlignedTriplesAndMissingTriples();
		}
		return mapAlignedTriplesFromHypothesisToText;
	}

	public Set<S> getMissingTriples()
	{
		if (null==missingTriples)
		{
			createMapAlignedTriplesAndMissingTriples();
		}
		return missingTriples;
	}

	public ValueSetMap<S, S> getMapSimilarNodesFromHypothesisToText()
	{
		if (null==mapSimilarNodesFromHypothesisToText)
		{
			createMapSimilarNodesAndMissingSimilarNodes();
		}
		return mapSimilarNodesFromHypothesisToText;
	}

	public Set<S> getMissingSimilarNodes()
	{
		if (null==missingSimilarNodes)
		{
			createMapSimilarNodesAndMissingSimilarNodes();
		}
		return missingSimilarNodes;
	}
	
	public ValueSetMap<S, S> getMapAlignedNodesFromHypothesisToText()
	{
		if (null==mapAlignedNodesFromHypothesisToText)
		{
			createMapAlignedNodesAndMissingAlignedNodes();
		}
		return mapAlignedNodesFromHypothesisToText;
	}

	public Set<S> getMissingAlignedNodes()
	{
		if (null==missingAlignedNodes)
		{
			createMapAlignedNodesAndMissingAlignedNodes();
		}
		return missingAlignedNodes;
	}
	
	
	//////////////////////////////// PRIVATE ///////////////////////////////////
	

	private void createMapAlignedTriplesAndMissingTriples()
	{
		mapAlignedTriplesFromHypothesisToText = new SimpleValueSetMap<S, S>();
		missingTriples = new LinkedHashSet<S>();

		Set<S> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesisTree.getTree());
		Set<S> textNodes = AbstractNodeUtils.treeToLinkedHashSet(textTree.getTree());
		
		for (S hypothesisNode : hypothesisNodes)
		{
			boolean foundAligned = false;
			for (S textNode : textNodes)
			{
				if (alignmentCriteria.triplesAligned(textTree, hypothesisTree, textNode, hypothesisNode))
				{
					foundAligned = true;
					mapAlignedTriplesFromHypothesisToText.put(hypothesisNode, textNode);
				}
			}
			if (!foundAligned)
			{
				missingTriples.add(hypothesisNode);
			}
		}
	}
	

	
	
	
	private void createMapSimilarNodesAndMissingSimilarNodes()
	{
		mapSimilarNodesFromHypothesisToText = new SimpleValueSetMap<S, S>();
		missingSimilarNodes = new LinkedHashSet<S>();

		Set<S> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesisTree.getTree());
		Set<S> textNodes = AbstractNodeUtils.treeToLinkedHashSet(textTree.getTree());
		
		for (S hypothesisNode : hypothesisNodes)
		{
			boolean foundSimilar = false;
			for (S textNode : textNodes)
			{
				if (alignmentCriteria.nodesSimilar(textTree, hypothesisTree, textNode, hypothesisNode))
				{
					foundSimilar = true;
					mapSimilarNodesFromHypothesisToText.put(hypothesisNode, textNode);
				}
			}
			if (!foundSimilar)
			{
				missingSimilarNodes.add(hypothesisNode);
			}
		}
	}


	private void createMapAlignedNodesAndMissingAlignedNodes()
	{
		mapAlignedNodesFromHypothesisToText = new SimpleValueSetMap<S, S>();
		missingAlignedNodes = new LinkedHashSet<S>();

		Set<S> hypothesisNodes = AbstractNodeUtils.treeToLinkedHashSet(hypothesisTree.getTree());
		Set<S> textNodes = AbstractNodeUtils.treeToLinkedHashSet(textTree.getTree());
		
		for (S hypothesisNode : hypothesisNodes)
		{
			boolean foundSimilar = false;
			for (S textNode : textNodes)
			{
				if (alignmentCriteria.nodesAligned(textTree, hypothesisTree, textNode, hypothesisNode))
				{
					foundSimilar = true;
					mapAlignedNodesFromHypothesisToText.put(hypothesisNode, textNode);
				}
			}
			if (!foundSimilar)
			{
				missingAlignedNodes.add(hypothesisNode);
			}
		}
	}

	
	protected AlignmentCriteria<T, S> alignmentCriteria;
	protected TreeAndParentMap<T, S> textTree;
	protected TreeAndParentMap<T, S> hypothesisTree;
	
	private ValueSetMap<S, S> mapAlignedTriplesFromHypothesisToText = null;
	private Set<S> missingTriples = null;
	private ValueSetMap<S, S> mapSimilarNodesFromHypothesisToText = null;
	private Set<S> missingSimilarNodes = null;
	private ValueSetMap<S, S> mapAlignedNodesFromHypothesisToText = null;
	private Set<S> missingAlignedNodes = null;

}
