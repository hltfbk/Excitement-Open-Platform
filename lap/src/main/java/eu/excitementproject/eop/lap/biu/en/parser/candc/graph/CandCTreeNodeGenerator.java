package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractConstructionNode;

public interface CandCTreeNodeGenerator<T, S extends AbstractConstructionNode<T, S>>
{
	public S generateNode(CCNodeInfo nodeInfo, CCEdgeInfo edgeToParentInfo);
}
