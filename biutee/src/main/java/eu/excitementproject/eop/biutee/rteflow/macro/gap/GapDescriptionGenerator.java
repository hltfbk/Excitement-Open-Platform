package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * Generates a (free text) description of a gap between a given (text-)parse-tree and the
 * (already known) hypothesis-parse-tree.
 * 
 * @author Asher Stern
 * @since 2013
 *
 * @param <I>
 * @param <S>
 */
public interface GapDescriptionGenerator<I, S extends AbstractNode<I, S>>
{
	public GapDescription describeGap(TreeAndParentMap<I, S> tree, GapEnvironment<I, S> environment) throws GapException;
}
