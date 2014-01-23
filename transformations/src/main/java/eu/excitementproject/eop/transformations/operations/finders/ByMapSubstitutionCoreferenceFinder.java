package eu.excitementproject.eop.transformations.operations.finders;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.SubstitutionSubtreeSpecification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;




/**
 * This {@linkplain Finder} finds specification about subtrees in a given text tree that
 * can be replaced by other subtrees, based on coreference ExtendedInformation.
 * <P>
 * <B>Note: This class is no longer used.</B> A better approach to co-reference usage is
 * by {@link SubstitutionCoreferenceFinder}.
 * <BR>
 * 
 * @see SubstitutionCoreferenceFinder
 * 
 * @author Asher Stern
 * @since Feb 2, 2011
 *
 */
@Deprecated
public class ByMapSubstitutionCoreferenceFinder extends AbstractCoreferenceFinder  implements Finder<SubstitutionSubtreeSpecification>
{
	public ByMapSubstitutionCoreferenceFinder(
			TreeCoreferenceInformation<ExtendedNode> coreferenceExtendedInformation,
			Map<ExtendedNode, ExtendedNode> mapNodeToOriginal,
			TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree)
	{
		super(coreferenceExtendedInformation, mapNodeToOriginal, textTree);
	}
	
	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}
	
	@Override
	public void find() throws OperationException
	{
		specs = new LinkedHashSet<SubstitutionSubtreeSpecification>();
		try
		{
			findCoreferringNodes();
			for (ExtendedNode nodeInTree : this.getMapNodeToCoreferringNodes().keySet())
			{
				for (ExtendedNode coreferringNode : this.getMapNodeToCoreferringNodes().get(nodeInTree))
				{
					specs.add(new SubstitutionSubtreeSpecification("Coreference substitution", nodeInTree, coreferringNode));
				}
			}
		}
		catch (TreeCoreferenceInformationException e)
		{
			throw new OperationException("Coreference problem. See nested exception.",e);
		}
	}
	
	@Override
	public Set<SubstitutionSubtreeSpecification> getSpecs() throws OperationException
	{
		if (null==specs) throw new OperationException("find() was not called.");
		return specs;
	}






	private Set<SubstitutionSubtreeSpecification> specs = null;
}
