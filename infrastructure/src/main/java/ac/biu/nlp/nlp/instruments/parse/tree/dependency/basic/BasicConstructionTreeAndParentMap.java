package ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic;

import java.util.Map;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

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
