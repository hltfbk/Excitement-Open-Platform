package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.scoring.DefaultElementScore;
import eu.excitementproject.eop.distsim.scoring.ElementScore;
import eu.excitementproject.eop.distsim.util.Pair;


/**
 * Implements an iterator for {@link ElementScore} items, based on a given iterator of pairs of id and score 
 *  
 *  @author Meni Adler
 * @since 28/12/2012
 *
 */
public class ElementScoreIterator extends ImmutableIterator<ElementScore> {

	public ElementScoreIterator(ImmutableIterator<Pair<Integer, Double>> elementScores) {
		this.iterator = elementScores;
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public ElementScore next() {
		Pair<Integer, Double> next = iterator.next();
		if (next != null)
			return new DefaultElementScore(next.getFirst(),next.getSecond());
		else 
			throw new NoSuchElementException(); 
	}
	

	protected ImmutableIterator<Pair<Integer, Double>> iterator;

}
