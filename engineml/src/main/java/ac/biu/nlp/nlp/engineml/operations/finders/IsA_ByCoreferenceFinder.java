package ac.biu.nlp.nlp.engineml.operations.finders;

import java.util.LinkedHashSet;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.IsASpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.InfoObservations;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;

/**
 * 
 * 
 * @author Asher Stern
 * @since Sep 9, 2012
 *
 */
public class IsA_ByCoreferenceFinder implements Finder<IsASpecification>
{
	public IsA_ByCoreferenceFinder(
			TreeCoreferenceInformation<ExtendedNode> coreferenceExtendedInformation,
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree)
	{
		super();
		this.coreferenceExtendedInformation = coreferenceExtendedInformation;
		this.textTree = textTree;
	}

	@Override
	public void find() throws OperationException
	{
		specs = new LinkedHashSet<IsASpecification>();
		try
		{
			for (ExtendedNode node : AbstractNodeUtils.treeToSet(textTree.getTree()))
			{
				Integer corefId = corefIdOfNode(node);
				if (corefId!=null)
				{
					for (ExtendedNode corefNode : coreferenceExtendedInformation.getGroup(corefId))
					{
						if (!corefUniqueCorefOfNode(node).equals(corefUniqueCorefOfNode(corefNode)))
						{
							if ( (!InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase(InfoGetFields.getLemma(corefNode.getInfo()))) &&
									(!isPronoun(node)) && (!isPronoun(corefNode)) &&
									(InfoObservations.infoIsContentWord(node.getInfo())) && (InfoObservations.infoIsContentWord(corefNode.getInfo())) )
							{
								specs.add(new IsASpecification(node,corefNode,null));
								specs.add(new IsASpecification(corefNode,node,null));
							}
						}
					}
				}
			}
		}
		catch (TreeCoreferenceInformationException e)
		{
			throw new OperationException("Failed to get coreference information. See nested exception.",e);
		}
	}

	@Override
	public Set<IsASpecification> getSpecs() throws OperationException
	{
		if (null==specs) throw new OperationException("Caller\'s bug: Find() was not called.");
		return specs;
	}
	
	private static boolean isPronoun(ExtendedNode node)
	{
		boolean ret = true;
		PartOfSpeech pos = InfoGetFields.getPartOfSpeechObject(node.getInfo());
		if (pos!=null)
		{
			if (!CanonicalPosTag.PRONOUN.equals(pos.getCanonicalPosTag()))
			{
				ret = false;
			}
		}
		return ret;
	}
	
	private static Integer corefIdOfNode(ExtendedNode node)
	{
		Integer ret = null;
		if (node!=null)
		{
			if (node.getInfo()!=null)
			{
				ExtendedInfo info = node.getInfo();
				if (info.getAdditionalNodeInformation()!=null)
				{
					ret = info.getAdditionalNodeInformation().getCorefGroupId();
				}
						
			}
		}
		return ret;
	}

	private static Integer corefUniqueCorefOfNode(ExtendedNode node)
	{
		Integer ret = null;
		if (node!=null)
		{
			if (node.getInfo()!=null)
			{
				ExtendedInfo info = node.getInfo();
				if (info.getAdditionalNodeInformation()!=null)
				{
					ret = info.getAdditionalNodeInformation().getUniqueIdForCoref();
				}
						
			}
		}
		return ret;
	}


	private TreeCoreferenceInformation<ExtendedNode> coreferenceExtendedInformation;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	private Set<IsASpecification> specs = null;
}
