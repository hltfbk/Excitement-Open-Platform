package ac.biu.nlp.nlp.instruments.coreference;

import java.util.Set;

import ac.biu.nlp.nlp.general.immutable.ImmutableSet;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;

public class TreeCoreferenceInformationUtils
{
	public static <I,T extends AbstractNode<I,T>> void removeNestedTags(TreeCoreferenceInformation<T> information) throws TreeCoreferenceInformationException
	{
		ImmutableSet<Integer> allGroupIds = information.getAllExistingGroupIds();
		for (Integer id : allGroupIds)
		{
			ImmutableSet<T> nodes = information.getGroup(id).getImmutableSetCopy();
			for (T node : nodes)
			{
				Set<T> nodesAsSet = AbstractNodeUtils.treeToSet(node);
				for (T nestedNode : nodesAsSet)
				{
					if (nestedNode==node) ;
					else
					{
						if (information.isNodeExist(nestedNode))
						{
							if (information.getIdOf(nestedNode).equals(id))
								information.remove(nestedNode);
						}
					}
				}
			}
		}
		
	}

}
