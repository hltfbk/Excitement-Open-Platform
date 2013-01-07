package ac.biu.nlp.nlp.engineml.generic.truthteller;

import ac.biu.nlp.nlp.codeannotations.NotThreadSafe;
import ac.biu.nlp.nlp.engineml.representation.AdditionalNodeInformation;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.general.BidirectionalMap;

/**
 * Annotates a tree - assigns values to the annotation fields in
 * {@link AdditionalNodeInformation} of each node.
 * 
 * <BR>
 * Usage:<BR>
 * <OL>
 * <LI>Call {@link #setTree(ExtendedNode)}</LI>
 * <LI>Call {@link #annotate()}</LI>
 * <LI>Call {@link #getAnnotatedTree()}</LI>
 * </OL>
 * 
 * @author Asher Stern
 * @since Oct 4, 2011
 *
 */
@NotThreadSafe
public interface SentenceAnnotator
{
	/**
	 * Set a tree to annotate. The user should call this method, then call {@link #annotate()},
	 * then call {@link #getAnnotatedTree()}.
	 * 
	 * @param tree
	 * @throws AnnotatorException
	 */
	public void setTree(ExtendedNode tree) throws AnnotatorException;
	
	/**
	 * Annotate the tree given earlier in {@link #setTree(ExtendedNode)}.
	 * @throws AnnotatorException
	 */
	public void annotate() throws AnnotatorException;
	
	/**
	 * Get the annotated tree created by the method {@link #annotate()}.
	 * @return
	 * @throws AnnotatorException
	 */
	public ExtendedNode getAnnotatedTree() throws AnnotatorException;
	
	/**
	 * For the tree created by the method {@link #annotate()}, return a mapping
	 * from each node in the original tree (given in {@link #setTree(ExtendedNode)}) to the
	 * equivalent node in the annotated tree.
	 * 
	 * @return a mapping from the original tree nodes' to the annotated tree nodes.
	 * @throws AnnotatorException
	 */
	public BidirectionalMap<ExtendedNode, ExtendedNode> getMapOriginalToAnnotated() throws AnnotatorException;
}
