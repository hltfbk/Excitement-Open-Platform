package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.LinkedHashMap;
import java.util.Map;


import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.alignment.AlignmentCalculator;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * 
 * @author Asher Stern
 * @since Aug 5, 2013
 *
 */
public class GapFeatureVectorGenerator
{
	public GapFeatureVectorGenerator(AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria)
	{
		this.alignmentCriteria = alignmentCriteria;
	}
	
	
	public Map<Integer, Double> createFeatureVector(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis
			) throws GapException
	{
		LinkedHashMap<Integer, Double> ret = new LinkedHashMap<Integer, Double>();
		ret.put(Feature.GAP_COUNT_MISSING_NODES.getFeatureIndex(),
				(double)(-countMissingNodes(text,hypothesis)));
		return ret;
	}
	
	private int countMissingNodes(TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis)
	{
		AlignmentCalculator alignmentCalculator = new AlignmentCalculator(alignmentCriteria,text,hypothesis);
		return alignmentCalculator.getMissingAlignedNodes().size();
	}

	private final AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria;
}
