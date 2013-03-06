package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.scoring.DefaultFeatureScore;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;


/**
 * Implements an iterator for {@link FeatureScore} items, based on a given iterator of pairs of id and score 
 *  
 * @author Meni Adler
 * @since 28/12/2012
 *
 */
public class FeatureScoreIterator extends ImmutableIterator<FeatureScore> {

	public FeatureScoreIterator(LinkedHashMap<Integer, Double> featureScores) {
		// Assumption: The features for each element in the given elemntFeaturesScores are ordered by their scores
		this.iterator = featureScores.entrySet().iterator();
		this.totalFeaturesNum = featureScores.size();
		this.currFeatureOrder = 0;
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
	public FeatureScore next() {
		if (next == null)
			throw new NoSuchElementException();
		FeatureScore ret = next;
		moveNext();
		return ret;
	}
	

	/**
	 * Move to the next item, according to the filter condition
	 */
	protected void moveNext() {
		if (iterator.hasNext()) {
			Entry<Integer, Double> featureScore = iterator.next();
			currFeatureOrder++;
			int featureId = featureScore.getKey();
			double score = featureScore.getValue();
			if (filtered(score))
				next = null; // due to the order assumption
			else
				next = new DefaultFeatureScore(featureId,score);
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
	
	protected Iterator<Entry<Integer, Double>> iterator;
	protected int currFeatureOrder;
	protected final int totalFeaturesNum;
	protected FeatureScore next;

}
