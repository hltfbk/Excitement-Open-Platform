package eu.excitementproject.eop.biutee.rteflow.macro.gap;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.biutee.rteflow.macro.InitializationTextTreesProcessor;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * Stores information required to calculate gap between a tree to the hypothesis tree.
 * The {@link GapEnvironment} is specific of a given T-H pair. It is created in
 * {@link InitializationTextTreesProcessor}.
 * 
 * @see InitializationTextTreesProcessor
 * @see GapFeaturesUpdate
 * @see GapHeuristicMeasure
 * 
 * @author Asher Stern
 * @since Aug 11, 2013
 *
 * @param <I>
 * @param <S>
 */
public class GapEnvironment<I, S extends AbstractNode<I, S>>
{
	public GapEnvironment(List<S> surroundingsContext, Set<String> wholeTextLemmas)
	{
		super();
		this.surroundingsContext = surroundingsContext;
		this.wholeTextLemmas = wholeTextLemmas;
	}

	
	public List<S> getSurroundingsContext()
	{
		return surroundingsContext;
	}
	

	public Set<String> getWholeTextLemmas()
	{
		return wholeTextLemmas;
	}
	
	
	public boolean identical(GapEnvironment<I,S> other)
	{
		if (getWholeTextLemmas().equals(other.getWholeTextLemmas()))
		{
			if (listTreesIdentical(getSurroundingsContext(),other.getSurroundingsContext()))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean listTreesIdentical (List<S> givenList, List<S> otherList)
	{
		Iterator<S> givenIterator = givenList.iterator();
		Iterator<S> otherIterator = otherList.iterator();
		while ( (givenIterator.hasNext()) && (otherIterator.hasNext()) )
		{
			S given = givenIterator.next();
			S other = otherIterator.next();
			if (given!=other)
			{
				return false;
			}
		}
		if ( (givenIterator.hasNext()) || (otherIterator.hasNext()) )
		{
			return false;
		}
		return true;
	}





	/**
	 * Contains the text parse trees, as well as other parse-trees which
	 * are considered as the text context.
	 * In RTE-Sum, it is usually the parse-tree of the given sentence, and 
	 * the parse-tree of the preceeding sentence, and titles of all documents
	 * in the topic.<BR>
	 * <B>This field might be null.</B>
	 */
	private final List<S> surroundingsContext;
	
	private final Set<String> wholeTextLemmas;
}
