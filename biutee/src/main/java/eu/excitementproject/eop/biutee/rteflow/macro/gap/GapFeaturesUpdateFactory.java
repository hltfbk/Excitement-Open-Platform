package eu.excitementproject.eop.biutee.rteflow.macro.gap;

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
public interface GapFeaturesUpdateFactory<I, S extends AbstractNode<I, S>>
{
	public GapFeaturesUpdate<I, S> createForHypothesis(TreeAndParentMap<I, S> hypothesis) throws GapException;
}
