package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.SubstitutionSubtreeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * Finds a set of {@link SubstitutionSubtreeSpecification}s based on
 * coreference information.
 * <P>
 * This class utilizes the coreference information
 * on the nodes ({@link ExtendedNode}s may contain that information). This
 * compared to {@link ByMapSubstitutionCoreferenceFinder} that utilizes the
 * coreference information that is given by a {@link TreeCoreferenceInformation}.
 * <BR>
 * This class' approach is better than
 * {@link ByMapSubstitutionCoreferenceFinder}'s approach.
 * 
 * 
 * @author Asher Stern
 * @since Apr 11, 2011
 *
 */
public class SubstitutionCoreferenceFinder implements Finder<SubstitutionSubtreeSpecification>
{
	public SubstitutionCoreferenceFinder(
			TreeAndParentMap<ExtendedInfo, ExtendedNode> textTree,
			TreeCoreferenceInformation<ExtendedNode> coreferenceExtendedInformation
			)
	{
		super();
		this.textTree = textTree;
		this.coreferenceExtendedInformation = coreferenceExtendedInformation;
	}
	
	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	@Override
	public void find() throws OperationException
	{
		try
		{
			specs = new LinkedHashSet<SubstitutionSubtreeSpecification>();
			for (ExtendedNode node : TreeIterator.iterableTree(textTree.getTree()))
			{
				if (node.getInfo().getAdditionalNodeInformation().getCorefGroupId()!=null)
				{
					ImmutableSet<ExtendedNode> corefGroup = coreferenceExtendedInformation.getGroup(node.getInfo().getAdditionalNodeInformation().getCorefGroupId());
					for (ExtendedNode corefNode : corefGroup)
					{
//						if (corefNode!=node)
						if (!node.getInfo().getAdditionalNodeInformation().getUniqueIdForCoref().equals(corefNode.getInfo().getAdditionalNodeInformation().getUniqueIdForCoref()))
						{
							specs.add(new SubstitutionSubtreeSpecification("coreference-subtree-substitution", node, corefNode));
						}
					}

				}
			}
		}
		catch(TreeCoreferenceInformationException e)
		{
			try{logger.error(printCoreferenceDiagnosis().toString());}
			catch(Exception x){logger.error("Cannot print diagnosis");}
			
			throw new OperationException("Coreference information problem.",e);
		}
	}
	
	@Override
	public Set<SubstitutionSubtreeSpecification> getSpecs() throws OperationException
	{
		if (null==specs) throw new OperationException("find() was not called.");
		return specs;
	}

	
	
	private StringBuffer printCoreferenceDiagnosis() throws TreeCoreferenceInformationException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Printing coreference information...");
		for (Integer corefGroup : coreferenceExtendedInformation.getAllExistingGroupIds())
		{
			sb.append("\n");
			sb.append("Coref group: ").append(corefGroup).append('\n');
			for (ExtendedNode node : coreferenceExtendedInformation.getGroup(corefGroup))
			{
				sb.append('[').append(node.getInfo().getId()).append(']');
				sb.append(InfoGetFields.getLemma(node.getInfo())).append(", ");
			}
		}
		sb.append("\n");
		sb.append("Printing coref information in the nodes...\n");
		for (ExtendedNode node : TreeIterator.iterableTree(textTree.getTree()))
		{
			sb.append('[').append(node.getInfo().getId()).append(']');
			sb.append(InfoGetFields.getLemma(node.getInfo()));
			sb.append(": ");
			sb.append(ExtendedInfoGetFields.getCorefGroupId(node.getInfo()));
			sb.append("\n");
		}
		return sb;
	}
	

	private TreeCoreferenceInformation<ExtendedNode> coreferenceExtendedInformation;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	private Set<SubstitutionSubtreeSpecification> specs = null;
	
	private final static Logger logger = Logger.getLogger(SubstitutionCoreferenceFinder.class);
}
