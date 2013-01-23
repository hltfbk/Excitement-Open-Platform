package eu.excitementproject.eop.lap.biu.coreference.merge;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * Merges a co-reference resolution system's output, with list of trees.
 * <P>
 * <B>
 * TODO: This class as well as several classes in ac.biu.nlp.nlp.instruments.coreference.merge
 * and its sub packages are not well designed. Re-design (and re-implementation)
 * should be done.
 * </B>
 * <P>
 * The assumption is that the list of the trees, as well as the coreference system's output
 * were given in the constructor.
 * The coreference system's output is given in some format that is not specified here.
 * 
 * The merge result is a {@link TreeCoreferenceInformation}.
 * 
 * @author Asher Stern
 * 
 *
 * @param <T> The type of a tree node. Typically a subclass of {@link AbstractNode}
 */
public interface CorefMerger<T>
{
	public void merge() throws TreeCoreferenceInformationException, CorefMergeException;
	
	public TreeCoreferenceInformation<T> getCoreferenceInformation() throws CorefMergeException;
	

}
