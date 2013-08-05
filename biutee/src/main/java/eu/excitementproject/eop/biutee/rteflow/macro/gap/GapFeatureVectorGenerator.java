package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.LinkedHashMap;
import java.util.Map;


import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
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
	public Map<Integer, Double> createFeatureVector(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> text,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> hypothesis
			) throws GapException
	{
		LinkedHashMap<Integer, Double> ret = new LinkedHashMap<Integer, Double>();
		return ret;
	}

}
