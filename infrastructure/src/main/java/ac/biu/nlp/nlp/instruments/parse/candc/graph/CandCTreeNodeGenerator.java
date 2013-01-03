package ac.biu.nlp.nlp.instruments.parse.candc.graph;

import ac.biu.nlp.nlp.instruments.parse.tree.AbstractConstructionNode;

public interface CandCTreeNodeGenerator<T, S extends AbstractConstructionNode<T, S>>
{
	public S generateNode(CCNodeInfo nodeInfo, CCEdgeInfo edgeToParentInfo);
}
