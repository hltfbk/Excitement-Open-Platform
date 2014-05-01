package eu.excitementproject.eop.transformations.representation;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;



/**
 * 
 * @author Asher Stern
 * @since Apr 6, 2011
 *
 */
public class ExtendedNodeConstructor implements AbstractNodeConstructor<ExtendedInfo, ExtendedNode>
{
	public static final AdditionalNodeInformation EMPTY_ADDITIONAL_NODE_INFORMATION = new AdditionalNodeInformation();
	public static final ExtendedInfo EMPTY_EXTENDED_INFO = new ExtendedInfo(BasicNodeConstructor.EMPTY_NODE, EMPTY_ADDITIONAL_NODE_INFORMATION);
	
	public ExtendedNode newEmptyNode()
	{
		return newNode(EMPTY_EXTENDED_INFO,null);
	}

	public ExtendedNode newEmptyNode(List<? extends AbstractNode<ExtendedInfo, ?>> children)
	{
		return newNode(EMPTY_EXTENDED_INFO,children);
	}

	public ExtendedNode newNode(ExtendedInfo info)
	{
		return newNode(info,null);
	}

	public ExtendedNode newNode(ExtendedInfo info, List<? extends AbstractNode<ExtendedInfo, ?>> children)
	{
		ExtendedNode ret = new ExtendedNode(info);
		if (children != null)
		{
			for (AbstractNode<ExtendedInfo, ?> node : children)
			{
				ret.addChild(newNode(node.getInfo(),node.getChildren()));
			}
		}
		return ret;
	}
	

}
