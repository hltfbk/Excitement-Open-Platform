package eu.excitementproject.eop.transformations.generic.truthteller;
import eu.excitementproject.eop.common.codeannotations.ThreadSafe;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;

/**
 * Wraps {@link SentenceAnnotator} such that the annotation is done as an
 * atomic operation in a thread safe manner.
 * This class can be shared among threads, while {@link SentenceAnnotator} is not
 * thread safe.
 * 
 * @author Asher Stern
 * @since Nov 7, 2011
 *
 */
@ThreadSafe
public class SynchronizedAtomicAnnotator
{
	public SynchronizedAtomicAnnotator(SentenceAnnotator annotator)
	{
		super();
		this.annotator = annotator;
	}

	public synchronized AnnotatedTreeAndMap annotate(ExtendedNode tree) throws AnnotatorException
	{
		annotator.setTree(tree);
		annotator.annotate();
		AnnotatedTreeAndMap ret = 
			new AnnotatedTreeAndMap(annotator.getAnnotatedTree(), annotator.getMapOriginalToAnnotated());
		
		return ret;
	}
	
	private SentenceAnnotator annotator;
}
