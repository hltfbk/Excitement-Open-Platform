package eu.excitementproject.eop.distsim.storage.iterators;

import java.util.LinkedHashMap;


import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.scoring.ElementFeatureScore;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.domains.FilterType;

/**
 * Implements an iterator for {@link ElementFeatureScore} items, based on a given iterator of pairs of id and score map, 
 * with filtering functionality  
 *  
 * @author Meni Adler
 * @since 16/08/2012
 *
 */
public class FilterElementFeatureScoreIterator extends ElementFeatureScoreIterator {

	public FilterElementFeatureScoreIterator(ImmutableIterator<Pair<Integer, LinkedHashMap<Integer, Double>>> iterator, FilterType filterType, double filterVal) {
		// Assumption: The features for each element in the given elemntFeaturesScores are ordered by their scores
		super(iterator);
		this.filterType = filterType;
		this.filterVal = filterVal;
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.iterators.ElementFeatureScoreIterator#filtered(java.lang.Double)
	 */
	@Override
	protected boolean filtered(double val) {
		switch (filterType) {
			case MIN_VAL:
				return val < filterVal;
			case TOP_N:
				return currFeatureOrder > filterVal;
			case TOP_PRECENT:
				return currFeatureOrder > currElemetTotalFeaturesNum * filterVal;
			default:				
				return false;
		}
	}
	
	protected FilterType filterType;
	protected double filterVal;

}
