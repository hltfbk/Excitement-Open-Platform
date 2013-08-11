package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

public interface GapDescription<I, S extends AbstractNode<I, S>>
{
	public String describeGap(TreeAndParentMap<I, S> tree, GapEnvironment<I, S> environment) throws GapException;
}
