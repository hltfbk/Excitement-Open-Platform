package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic;

import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;


/**
 * {@linkplain TreeAndParentMap} for {@linkplain BasicConstructionNode}
 * @author Asher Stern
 * @since Feb 26, 2011
 *
 */
public class BasicConstructionTreeAndParentMap extends TreeAndParentMap<Info, BasicConstructionNode>
{
	private static final long serialVersionUID = 618429485815765313L;

	public BasicConstructionTreeAndParentMap(BasicConstructionNode tree, Map<BasicConstructionNode, BasicConstructionNode> parentMap) throws TreeAndParentMapException
	{
		super(tree, parentMap);
	}

	public BasicConstructionTreeAndParentMap(BasicConstructionNode tree) throws TreeAndParentMapException
	{
		super(tree);
	}
}
