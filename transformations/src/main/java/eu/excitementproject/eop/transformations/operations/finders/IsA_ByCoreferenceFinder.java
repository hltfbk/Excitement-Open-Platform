package eu.excitementproject.eop.transformations.operations.finders;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.IsASpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.InfoObservations;

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

	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	@Override
	public void find() throws OperationException
	{
		specs = new LinkedHashSet<IsASpecification>();
		try
		{
			for (ExtendedNode node : TreeIterator.iterableTree(textTree.getTree()))
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
			if (!SimplerCanonicalPosTag.PRONOUN.equals(simplerPos(pos.getCanonicalPosTag())))
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
