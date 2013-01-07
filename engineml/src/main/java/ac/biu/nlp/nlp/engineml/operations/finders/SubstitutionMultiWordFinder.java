package ac.biu.nlp.nlp.engineml.operations.finders;

import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstituteNodeSpecificationMultiWord;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;

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
