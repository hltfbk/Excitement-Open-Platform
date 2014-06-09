package eu.excitementproject.eop.transformations.representation;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;



/**
 * 
 * @author Amnon Lotan
 * @since Apr 6, 2011
 *
 */
public class ExtendedConstructionNodeConstructor implements AbstractNodeConstructor<ExtendedInfo, ExtendedConstructionNode>
{
	public static final AdditionalNodeInformation emptyAdditionalNodeInformation = new AdditionalNodeInformation();
	
	public ExtendedConstructionNode newEmptyNode()
	{
		return new ExtendedConstructionNode(new ExtendedInfo(BasicNodeConstructor.EMPTY_NODE, emptyAdditionalNodeInformation));
	}

	public ExtendedConstructionNode newEmptyNode(List<? extends AbstractNode<ExtendedInfo, ?>> children)
	{
		ExtendedConstructionNode ret = newEmptyNode();
		for (AbstractNode<ExtendedInfo, ?> node : children)
		{
			ret.addChild(new ExtendedConstructionNode(node.getInfo()));
		}
		return ret;
	}

	public ExtendedConstructionNode newNode(ExtendedInfo info)
	{
		return new ExtendedConstructionNode(info);
	}

	public ExtendedConstructionNode newNode(ExtendedInfo info, List<? extends AbstractNode<ExtendedInfo, ?>> children)
	{
		ExtendedConstructionNode ret = newNode(info);
		for (AbstractNode<ExtendedInfo, ?> node : children)
		{
			ret.addChild(new ExtendedConstructionNode(node.getInfo()));
		}
		return ret;
	}
	

}
