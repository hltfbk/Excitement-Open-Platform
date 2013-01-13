package ac.biu.nlp.nlp.engineml.operations.finders;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstitutionSubtreeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfoGetFields;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;

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
	
	@Override
	public void find() throws OperationException
	{
		try
		{
			specs = new LinkedHashSet<SubstitutionSubtreeSpecification>();
			Set<ExtendedNode> nodes = AbstractNodeUtils.treeToSet(textTree.getTree());
			for (ExtendedNode node : nodes)
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
		for (ExtendedNode node : AbstractNodeUtils.treeToSet(textTree.getTree()))
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
