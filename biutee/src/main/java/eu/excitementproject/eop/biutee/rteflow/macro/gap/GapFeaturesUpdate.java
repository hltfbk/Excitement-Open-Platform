package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * 
 * @author Asher Stern
 * @since Aug 1, 2013
 *
 * @param <I>
 * @param <S>
 */
public interface GapFeaturesUpdate<I, S extends AbstractNode<I, S>>
{
	public Map<Integer, Double> updateForGap(TreeAndParentMap<I, S> tree, Map<Integer,Double> featureVector) throws GapException;
}
