package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic;

import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;


/**
 * {@linkplain TreeAndParentMap} for {@linkplain BasicNode}
 * @author Asher Stern
 * @since Feb 26, 2011
 *
 */
public class BasicTreeAndParentMap extends TreeAndParentMap<Info, BasicNode>
{
	private static final long serialVersionUID = 618429485815765313L;

	public BasicTreeAndParentMap(BasicNode tree, Map<BasicNode, BasicNode> parentMap) throws TreeAndParentMapException
	{
		super(tree, parentMap);
	}

	public BasicTreeAndParentMap(BasicNode tree) throws TreeAndParentMapException
	{
		super(tree);
	}
}
