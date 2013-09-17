package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

public interface GapDescriptionGenerator<I, S extends AbstractNode<I, S>>
{
	public GapDescription describeGap(TreeAndParentMap<I, S> tree, GapEnvironment<I, S> environment) throws GapException;
}
