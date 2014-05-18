package eu.excitementproject.eop.distsim.storage;

import java.util.ArrayList;

import java.util.LinkedHashMap;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.scoring.ElementFeatureScores;
import eu.excitementproject.eop.distsim.scoring.ElementScore;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;
import eu.excitementproject.eop.distsim.scoring.feature.DefaultElementFeatureData;
import eu.excitementproject.eop.distsim.scoring.feature.ElementFeatureData;
import eu.excitementproject.eop.distsim.storage.iterators.ElementFeatureScoresIterator;
import eu.excitementproject.eop.distsim.storage.iterators.ElementScoreIterator;
import eu.excitementproject.eop.distsim.storage.iterators.FeatureScoreIterator;
import eu.excitementproject.eop.distsim.storage.iterators.FilterFeatureScoreIterator;
import eu.excitementproject.eop.distsim.domains.FilterType;

/**
 * A general implementation of the ElementFeatureScoreStorage interface
 * 
 * @author Meni Adler
 * @since 16/08/2012
 *
 * The class is thread-safe in a case where the given storage fields (elemntFeaturesScores, elementScores) are used in a read-only mode
 */
public class DefaultElementFeatureScoreStorage implements ElementFeatureScoreStorage {

	public DefaultElementFeatureScoreStorage(
			
		//Assumptions: 
		// - The features for each element in the given elemntFeaturesScores are ordered descendingly by their scores
		// - The elements in the given elemntScores are ordered descendingly by their scores			
		IDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>> elemntFeaturesScores,
		IDKeyPersistentBasicMap<Double> elemntScores,
		IDKeyPersistentBasicMap<BasicSet<Integer>> featureElements) {
		this.elemntFeaturesScores = elemntFeaturesScores;
		this.elementScores = elemntScores;
		this.fesatureElements = featureElements;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getElementFeatureScore(int, int)
	 */
	@Override
	public double getElementFeatureScore(int elementId, int featureId) throws NoScoreFoundException {
		LinkedHashMap<Integer, Double> featureScores;
		try {
			featureScores = elemntFeaturesScores.get(elementId);
		} catch (BasicMapException e) {
			throw new NoScoreFoundException(e);
		}
		if (featureScores != null) {
			Double score = featureScores.get(featureId);
			if (score != null)
				return score;
		}
		throw new NoScoreFoundException();
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getElementFeatureData(int, int)
	 */
	@Override
	public ElementFeatureData getElementFeatureData(int elementId, int featureId) throws NoScoreFoundException {
		LinkedHashMap<Integer, Double> featureScores;
		try {
			featureScores = elemntFeaturesScores.get(elementId);
		} catch (BasicMapException e) {
			throw new NoScoreFoundException(e);
		}
		if (featureScores != null) {
			//@TODO: re-implement. more efficient, based on more suitable data structure
			//       which gives the order of the rerieved item
			/*int i=1;
			for (Entry<Integer,Double> entry : featureScores.entrySet()) {
				if (entry.getKey() == featureId) {
					Double score = entry.getValue();
					return new DefaultElementFeatureData(score,i,featureScores.size());
				}
				i++;
			}*/
			if (featureScores != null) {
				Double score = featureScores.get(featureId);
				if (score != null) {
					ArrayList<Integer> lst = new ArrayList<Integer>(featureScores.keySet());
					return new DefaultElementFeatureData(score,lst.indexOf(featureId),featureScores.size());
				}
			}
			
		}
		throw new NoScoreFoundException();
	}


	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getElementFeatureScores(int)
	 */
	@Override
	public ImmutableIterator<FeatureScore> getElementFeatureScores(int elementId) throws NoScoreFoundException {
		LinkedHashMap<Integer, Double> featureScores;
		try {
			featureScores = elemntFeaturesScores.get(elementId);
		} catch (BasicMapException e) {
			throw new NoScoreFoundException(e);
		}
		if (featureScores == null)
			throw new NoScoreFoundException("No feature score was found for element " + elementId);
		return new FeatureScoreIterator(featureScores);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getElementFeatureScores(int, org.excitement.distsim.domains.FilterType, int)
	 */
	@Override
	public ImmutableIterator<FeatureScore> getElementFeatureScores(int elementId, FilterType filterType, double filterVal) throws NoScoreFoundException {
		LinkedHashMap<Integer, Double> featureScores;
		try {
			featureScores = elemntFeaturesScores.get(elementId);
		} catch (BasicMapException e) {
			throw new NoScoreFoundException(e);
		}
		if (featureScores == null)
			throw new NoScoreFoundException("No feature score was found for element " + elementId);
		return new FilterFeatureScoreIterator(featureScores, filterType, filterVal);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getElementsFeatureScores()
	 */
	@Override
	public ImmutableIterator<ElementFeatureScores> getElementsFeatureScores() {
		return new ElementFeatureScoresIterator(elemntFeaturesScores.iterator(),elemntFeaturesScores.size());
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getElementsFeatureScores(org.excitement.distsim.domains.FilterType, int)
	 */
	@Override
	public ImmutableIterator<ElementFeatureScores> getElementsFeatureScores(FilterType filterType, double filterVal) {
		return new ElementFeatureScoresIterator(elemntFeaturesScores.iterator(), filterType, filterVal, elemntFeaturesScores.size());
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getElementScore(int)
	 */
	@Override
	public double getElementScore(int elementId) throws NoScoreFoundException {
		Double ret;
		try {
			ret = elementScores.get(elementId);
		} catch (BasicMapException e) {
			throw new NoScoreFoundException(e);
		}
		if (ret == null)
			throw new NoScoreFoundException("No score was found for element " + elementId);
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getElementScores()
	 */
	@Override
	public ImmutableIterator<ElementScore> getElementScores() {
		return new ElementScoreIterator(elementScores.iterator());
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#saveState(org.excitement.distsim.storage.PersistenceDevice[])
	 * 
	 * Assumption: two persistence devices are provided: 
	 *   1. element-feature scores
	 *   2. element scores
	 */
	@Override
	public void saveState(PersistenceDevice... devices) throws SavingStateException {
		if (devices.length != 2)
			throw new SavingStateException(devices.length + " persistence devices was providied for saving, where two are expected");
		elemntFeaturesScores.saveState(devices[0]);
		elementScores.saveState(devices[1]);		
		
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#loadState(org.excitement.distsim.storage.PersistenceDevice[])
	 * 
	 * Assumption: two persistence devices are provided:
	 *   1. element-feature scores
	 *   2. element scores
	 */
	@Override
	public void loadState(PersistenceDevice... devices) throws LoadingStateException {
		if (devices.length != 2)
			throw new LoadingStateException(devices.length + " persistence devices was providied for loading, where two are expected");
		elemntFeaturesScores.loadState(devices[0]);
		elementScores.loadState(devices[1]);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureScoreStorage#getFeatureElements(int)
	 */
	@Override
	public BasicSet<Integer> getFeatureElements(int featureId) throws BasicMapException  {
		return fesatureElements.get(featureId);
	}
	
	// Assumption: The features of each element are ordered descendingly by their scores
	protected final IDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>> elemntFeaturesScores;
	protected final IDKeyPersistentBasicMap<BasicSet<Integer>> fesatureElements;
	protected final IDKeyPersistentBasicMap<Double> elementScores;
}
