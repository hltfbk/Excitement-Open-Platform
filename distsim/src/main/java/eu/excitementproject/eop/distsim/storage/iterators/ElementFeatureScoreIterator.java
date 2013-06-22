package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.scoring.DefaultElementFeatureScore;
import eu.excitementproject.eop.distsim.scoring.ElementFeatureScore;
import eu.excitementproject.eop.distsim.util.Pair;


/**
 * Implements an iterator for {@link ElementFeatureScore} items, based on a given iterator of pairs of id and scoring map 
 * 
 * @author Meni Adler
 * @since 16/08/2012
 *
 */
public class ElementFeatureScoreIterator extends ImmutableIterator<ElementFeatureScore> {

	public ElementFeatureScoreIterator(ImmutableIterator<Pair<Integer, LinkedHashMap<Integer, Double>>> immutableIterator) {
		// Assumption: The features for each element in the given elemntFeaturesScores are ordered by their scores
		this.iterator = immutableIterator;
		moveNext();
	}
	
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return next != null;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public ElementFeatureScore next() {
		if (next == null)
			throw new NoSuchElementException();
		ElementFeatureScore ret = next;
		moveNext();
		return ret;
	}
	

	/**
	 * Move to the next item, according to the filter condition
	 */
	protected void moveNext() {
		if (!currFeatureScores.hasNext()) {
			if (iterator.hasNext()) {
				Pair<Integer, LinkedHashMap<Integer, Double>> tmp = iterator.next();
				currElementId = tmp.getFirst();
				currFeatureScores = tmp.getSecond().entrySet().iterator();
				currElemetTotalFeaturesNum = tmp.getSecond().size();
				currFeatureOrder = 0;
			}
		}
		if (currFeatureScores.hasNext()) {
			Entry<Integer, Double> featureScore = currFeatureScores.next();
			currFeatureOrder++;
			int featureId = featureScore.getKey();
			double score = featureScore.getValue();
			if (filtered(score))
				next = null; // due to the order assumption
			else
				next = new DefaultElementFeatureScore(currElementId, featureId,score);
		} else
			next = null;
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
	protected int currElementId;
	protected Iterator<Entry<Integer, Double>> currFeatureScores;
	protected int currFeatureOrder;
	protected int currElemetTotalFeaturesNum;
	protected ElementFeatureScore next;
}
