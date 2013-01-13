package ac.biu.nlp.nlp.engineml.utilities.preprocess;
import java.util.Collection;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;

import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;

/**
 * Given a collection of parse trees, this class creates {@link TreeCoreferenceInformation}
 * from the information in these parse-trees.
 * <P>
 * This is the opposite direction of the usual computation which put information from
 * {@link TreeCoreferenceInformation} into the trees. Here it is the opposite.
 * 
 * @deprecated Wrong idea. No longer used.
 * 
 * @author Asher Stern
 * @since Jul 30, 2012
 *
 */
@Deprecated
public class CoreferenceFromTreesCreator
{
	///////////////// PUBLIC /////////////////
	
	public CoreferenceFromTreesCreator(Collection<ExtendedNode> trees)
	{
		super();
		this.trees = trees;
	}
	
	public void create() throws TreeCoreferenceInformationException
	{
		coreferenceInformation = new TreeCoreferenceInformation<ExtendedNode>();
		Integer max = findMax();
		if (max!=null)
		{
			initializeCoreferenceInformation(max);
			insertNodesToCoreferenceInformation();
		}
		done = true;
	}
	

	public TreeCoreferenceInformation<ExtendedNode> getCoreferenceInformation() throws TeEngineMlException
	{
		if (!done) throw new TeEngineMlException("CoreferenceFromTreesCreator - create() was not called.");
		return coreferenceInformation;
	}



	///////////////// PRIVATE /////////////////
	
	private Integer findMax()
	{
		Integer max = null; 
		for (ExtendedNode tree : trees)
		{
			for (ExtendedNode node : AbstractNodeUtils.treeToSet(tree))
			{
				Integer corefId = getCorefIdOfNode(node);
				if (corefId!=null)
				{
					if (null==max) max = corefId;
					else
					{
						if (max.intValue()<corefId.intValue())
						{
							max = corefId;
						}
					}
				}
				
			}
		}
		return max;
	}
	
	
	private void initializeCoreferenceInformation(Integer maxIdOfGroup)
	{
		if (maxIdOfGroup!=null)
		{
			Integer group = coreferenceInformation.createNewGroup();
			while (group.intValue()<maxIdOfGroup.intValue())
			{
				group = coreferenceInformation.createNewGroup();
			}
		}
		
	}
	

	
	private void insertNodesToCoreferenceInformation() throws TreeCoreferenceInformationException
	{
		for (ExtendedNode tree : trees)
		{
			for (ExtendedNode node : AbstractNodeUtils.treeToSet(tree))
			{
				Integer corefId = getCorefIdOfNode(node);
				if (corefId!=null)
				{
					coreferenceInformation.addNodeToGroup(corefId,node);
				}
			}
		}
	}

	
	private static Integer getCorefIdOfNode(ExtendedNode node)
	{
		Integer ret = null;
		if (node!=null)
		{
			if (node.getInfo()!=null)
			{
				if (node.getInfo().getAdditionalNodeInformation()!=null)
				{
					ret = node.getInfo().getAdditionalNodeInformation().getCorefGroupId();
				}
			}
		}
		return ret;
	}
	
	private Collection<ExtendedNode> trees;
	
	private TreeCoreferenceInformation<ExtendedNode> coreferenceInformation;
	private boolean done = false;
}
