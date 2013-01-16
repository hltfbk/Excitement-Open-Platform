package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;



/**
 * <p>Used to construct an an EnglishConstructionNode.
 * <p>Singleton class.
 * @see BasicNodeConstructor
 * @author Erel Segal
 * @since 25/12/2011
 */
public class BasicConstructionNodeConstructor implements AbstractNodeConstructor<Info, BasicConstructionNode> {
	public final static BasicConstructionNodeConstructor INSTANCE = new BasicConstructionNodeConstructor();
	private BasicConstructionNodeConstructor() { /* singleton - prevent instantiation */ }

	public final static Info emptyInfo = BasicNodeConstructor.EMPTY_NODE;

	public BasicConstructionNode newNode(Info info) {
		return new BasicConstructionNode(info);
	}

	public BasicConstructionNode newNode(Info info, List<? extends AbstractNode<Info, ?>> children) {
		BasicConstructionNode ret = new BasicConstructionNode(info);
		if (children!=null) {
			for (AbstractNode<Info, ?> child : children) {
				ret.addChild(newNode(child.getInfo(),child.getChildren()));
			}
		}
		return ret;
	}

	public BasicConstructionNode newEmptyNode() {
		return newNode(emptyInfo);
	}

	public BasicConstructionNode newEmptyNode(List<? extends AbstractNode<Info, ?>> children) {
		return newNode(emptyInfo, children);
	}
}
