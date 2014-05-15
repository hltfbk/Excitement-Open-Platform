package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.LinkedHashMap;

import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.scoring.DefaultElementFeatureScores;
import eu.excitementproject.eop.distsim.scoring.ElementFeatureScores;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.domains.FilterType;
/**
 * Implements an iterator for {@link ElementFeatureScores} items, based on a given iterator of pairs of id and scoring map 
 * 
 * @author Meni Adler
 * @since 16/08/2012
 *
 */
public class ElementFeatureScoresIterator extends ImmutableIterator<ElementFeatureScores> {

	public ElementFeatureScoresIterator(ImmutableIterator<Pair<Integer, LinkedHashMap<Integer, Double>>> immutableIterator,int elementFeaturesNum) {
		// Assumption: The features for each element in the given elemntFeaturesScores are ordered by their scores
		this(immutableIterator,FilterType.ALL,0.0,elementFeaturesNum);
		
	}
	
	public ElementFeatureScoresIterator(ImmutableIterator<Pair<Integer, LinkedHashMap<Integer, Double>>> immutableIterator, FilterType filterType, double filterVal, int elementFeaturesNum) {
		// Assumption: The features for each element in the given elemntFeaturesScores are ordered by their scores
		this.iterator = immutableIterator;
		this.filterType = filterType;
		this.filterVal = filterVal;
		this.elementFeaturesNum = elementFeaturesNum;
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
	public ElementFeatureScores next() {
		if (iterator.hasNext()) {
			Pair<Integer, LinkedHashMap<Integer, Double>> pair = iterator.next();
			if (filterType == FilterType.ALL)
				return new DefaultElementFeatureScores(pair.getFirst(),pair.getSecond());
			else
				return new DefaultElementFeatureScores(pair.getFirst(),new FilterFeatureScoreIterator(pair.getSecond(),filterType,filterVal),elementFeaturesNum);

		} else
			throw new NoSuchElementException();
	}
	

	/**
	 * Determine whether a given item should be filtered
	 * 
	 * @param count a count of some item
	 * @return true if the item should be filtered, i.e., the item does not stand the minimal required criteria 
	 */
	protected boolean filtered(double count) {
		return false;
	}
	
	protected final ImmutableIterator<Pair<Integer, LinkedHashMap<Integer, Double>>> iterator;
	protected final FilterType filterType;
	protected final double filterVal;
	protected final int elementFeaturesNum;
}
