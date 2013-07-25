package eu.excitementproject.eop.transformations.generic.truthteller;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * A "dummy" {@link SentenceAnnotator} that does no annotation, and
 * returns the original tree as is.
 * 
 * @author Asher Stern
 * @since Oct 4, 2011
 *
 */
public class DummyTreeAnnotator implements SentenceAnnotator
{
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.annotation.TreeAnnotator#setTree(ac.biu.nlp.nlp.engineml.representation.ExtendedNode)
	 */
	public void setTree(ExtendedNode tree) throws AnnotatorException
	{
		annotated=false;
		if (null==tree)throw new AnnotatorException("Null tree");
		this.tree = tree;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.annotation.TreeAnnotator#annotate()
	 */
	public void annotate() throws AnnotatorException
	{
		mapOriginalToAnnotated = new SimpleBidirectionalMap<ExtendedNode, ExtendedNode>();
		for (ExtendedNode node : TreeIterator.iterableTree(this.tree))
		{
			mapOriginalToAnnotated.put(node, node);
		}
		annotated=true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.annotation.TreeAnnotator#getAnnotatedTree()
	 */
	public ExtendedNode getAnnotatedTree() throws AnnotatorException
	{
		if (!annotated)throw new AnnotatorException("Not annotated.");
		return this.tree;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.annotation.TreeAnnotator#getMapOriginalToAnnotated()
	 */
	public BidirectionalMap<ExtendedNode, ExtendedNode> getMapOriginalToAnnotated()
			throws AnnotatorException
	{
		if (!annotated)throw new AnnotatorException("Not annotated.");
		return mapOriginalToAnnotated;
	}
	
	private ExtendedNode tree=null;
	private BidirectionalMap<ExtendedNode, ExtendedNode> mapOriginalToAnnotated=null;
	
	private boolean annotated=false;
}
