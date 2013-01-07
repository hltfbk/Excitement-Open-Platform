package ac.biu.nlp.nlp.engineml.operations.finders;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstitutionSubtreeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;

import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;



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
