package eu.excitementproject.eop.transformations.operations.finders.auxiliary;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

/**
 * 
 * @author Asher Stern
 * @since Dec 29, 2013
 *
 */
public class AllowedRootsByAffectedNodesUtility
{
	public static <I, S extends AbstractNode<I, S>> LinkedHashSet<S> findAllowedRootsByAffectedNodes(TreeAndParentMap<I, S> tree, Set<S> affectedNodes)
	{
		final S treeRoot = tree.getTree();
		final Map<S, S> parentMap = tree.getParentMap();
		LinkedHashSet<S> ret = new LinkedHashSet<>();
		for (S affectedNode : affectedNodes)
		{
			S node = affectedNode;
			while (node != treeRoot)
			{
				ret.add(node);
				node = parentMap.get(node);
			}
			ret.add(node);
		}
		return ret;
	}
	

}
