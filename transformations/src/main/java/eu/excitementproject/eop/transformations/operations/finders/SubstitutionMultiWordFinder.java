package eu.excitementproject.eop.transformations.operations.finders;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.specifications.SubstituteNodeSpecificationMultiWord;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;


/**
 * Finds multi-word to single-word operations that can be performed on the
 * given text tree with respect to the given hypothesis tree.
 * 
 * @author Asher Stern
 * @since Jan 29, 2012
 *
 */
public class SubstitutionMultiWordFinder implements Finder<SubstituteNodeSpecificationMultiWord>
{
	public SubstitutionMultiWordFinder(SubstitutionMultiWordUnderlyingFinder underlyingFinder, TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree)
	{
		this.underlyingFinder = underlyingFinder;
		this.textTree = textTree;
	}

	@Override public void optionallyOptimizeRuntimeByAffectedNodes(Set<ExtendedNode> affectedNodes) throws OperationException
	{}

	public void find() throws OperationException
	{
		underlyingFinder.find(this.textTree);
		findCalled=true;
	}

	public Set<SubstituteNodeSpecificationMultiWord> getSpecs() throws OperationException
	{
		if (!findCalled) throw new OperationException("Find() was not called.");
		return underlyingFinder.getSpecs();
	}

	private SubstitutionMultiWordUnderlyingFinder underlyingFinder;
	private TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	private boolean findCalled = false;
}
