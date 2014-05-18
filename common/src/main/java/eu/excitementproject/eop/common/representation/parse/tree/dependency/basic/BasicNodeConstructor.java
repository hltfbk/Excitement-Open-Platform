package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;


/**
 * 
 * @author Asher Stern
 * 
 *
 */
public class BasicNodeConstructor implements AbstractNodeConstructor<Info, BasicNode>
{
	public final static Info EMPTY_NODE = new DefaultInfo("EMPTY", new DefaultNodeInfo(null, null, 0, null, new DefaultSyntacticInfo(null)), new DefaultEdgeInfo(null));
	public BasicNode newEmptyNode()
	{
		return new BasicNode(EMPTY_NODE);
	}
	public BasicNode newEmptyNode(List<? extends AbstractNode<Info, ?>> children)
	{
		return newNode(EMPTY_NODE, children);
	}



	public BasicNode newNode(Info info)
	{
		return new BasicNode(info);
	}

	public BasicNode newNode(Info info, List<? extends AbstractNode<Info, ?>> children)
	{
		BasicNode ret = new BasicNode(info);
		if (children!=null)
		{
			for (AbstractNode<Info, ?> child : children)
			{
				//ret.addChild(new EnglishNode(child.getInfo()));
				ret.addChild(newNode(child.getInfo(),child.getChildren()));
			}
		}
		return ret;
	}



	

}
