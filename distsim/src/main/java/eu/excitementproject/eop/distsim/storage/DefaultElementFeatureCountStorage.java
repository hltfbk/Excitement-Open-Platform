/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;

import java.util.Iterator;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.storage.iterators.JointCountIterator;
import eu.excitementproject.eop.distsim.storage.iterators.MinCountFilterIterator;
import eu.excitementproject.eop.distsim.storage.iterators.MinCountJointCountIterator;
import eu.excitementproject.eop.distsim.storage.iterators.MinCountPairIDBasedIterator;
import eu.excitementproject.eop.distsim.storage.iterators.MinCountSingleIDBasedIterator;
import eu.excitementproject.eop.distsim.storage.iterators.PairIDBasedIterator;
import eu.excitementproject.eop.distsim.storage.iterators.SingleIDBasedIterator;
import eu.excitementproject.eop.distsim.util.SerializationException;

/**
 * A general implementation of the ElementFeatureCountStorage interface
 * 
 * @author Meni Adler
 * @since 02/08/2012
 *
 * The class is thread-safe in a case where the given storage fields (elementStorage, featureStorage, elemntFeatureCounts, fesatureElementCounts ) are used in a read-only mode
 */
public class DefaultElementFeatureCountStorage implements ElementFeatureCountStorage {

	private static final Logger logger = Logger.getLogger(DefaultElementFeatureCountStorage.class);
	
	public DefaultElementFeatureCountStorage(
			CountableIdentifiableStorage<Element> elementStorage,
			CountableIdentifiableStorage<Feature> featureStorage,
			IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elemntFeaturesStorage,
			IDKeyPersistentBasicMap<BasicSet<Integer>> fesatureElementsStorage) {
		
		this.elementStorage = elementStorage;
		this.featureStorage = featureStorage;
		this.elemntFeatureCounts = elemntFeaturesStorage;
		this.fesatureElements = fesatureElementsStorage;
		Iterator<Element> it = elementStorage.iterator();
		double tmp = 0;
		while (it.hasNext())
			try {
				tmp += it.next().getCount();
			} catch (InvalidCountException e) {
				logger.error(e.toString());
			}
		totalElementCount = tmp;
	}
		
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getFeature(long)
	 */
	@Override
	public Feature getFeature(int featureId) throws ItemNotFoundException {
		try {
			return featureStorage.getData(featureId);
		} catch (SerializationException e) {
			throw new ItemNotFoundException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getElement(long)
	 */
	@Override
	public Element getElement(int elementId) throws ItemNotFoundException {
		try {
			return elementStorage.getData(elementId);
		} catch (SerializationException e) {
			throw new ItemNotFoundException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getAllElements()
	 */
	@Override
	public ImmutableIterator<Element> getAllElements() {
		return elementStorage.iterator();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getAllElements(int)
	 */
	@Override
	public ImmutableIterator<Element> getAllElements(long minCount) {
		return new MinCountFilterIterator<Element>(elementStorage.iterator(),minCount);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getAllFeatures()
	 */
	@Override
	public ImmutableIterator<Feature> getAllFeatures() {
		return featureStorage.iterator();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getAllFeatures(int)
	 */
	@Override
	public ImmutableIterator<Feature> getAllFeatures(long minCount) {
		return new MinCountFilterIterator<Feature>(featureStorage.iterator(),minCount);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getElementFeatures(long)
	 */
	@Override
	public ImmutableIterator<Feature> getElementFeatures(int elementId) throws NoFeatureFoundException {
		BasicMap<Integer, Double> features;
		try {
			features = elemntFeatureCounts.get(elementId);
		} catch (BasicMapException e) {
			throw new NoFeatureFoundException(e);
		}
		if (features == null)
			throw new NoFeatureFoundException("No featuree was found for element " + elementId);
		return new PairIDBasedIterator<Feature>(features.iterator(),featureStorage);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getElementFeatures(long, int)
	 */
	@Override
	public ImmutableIterator<Feature> getElementFeatures(int elementId, long minCount) throws NoFeatureFoundException {
		BasicMap<Integer, Double> features;
		try {
			features = elemntFeatureCounts.get(elementId);
		} catch (BasicMapException e) {
			throw new NoFeatureFoundException(e);
		}
		if (features == null)
			throw new NoFeatureFoundException("No featuree was found for element " + elementId);
		return new MinCountPairIDBasedIterator<Feature>(features.iterator(),featureStorage,minCount);
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getAllFesatureElements()
	 */
	@Override
	public IDKeyPersistentBasicMap<BasicSet<Integer>> getFeatureElementsMapping() {
		return fesatureElements;
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getFeatureElements(long)
	 */
	@Override
	public ImmutableIterator<Element> getFeatureElements(int featureId) throws NoElementFoundException {
		BasicSet<Integer> elements;
		try {
			elements = fesatureElements.get(featureId);
		} catch (BasicMapException e) {
			throw new NoElementFoundException(e);
		}
		if (elements == null)
			throw new NoElementFoundException("No element was found for feature " + featureId);
		return new SingleIDBasedIterator<Element>(elements.iterator(),elementStorage);	
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getFeatureElements(long, int)
	 */
	@Override
	public ImmutableIterator<Element> getFeatureElements(int featureId, long minCount) throws NoElementFoundException {
		BasicSet<Integer> elements;
		try {
			elements = fesatureElements.get(featureId);
		} catch (BasicMapException e) {
			throw new NoElementFoundException(e);
		}
		if (elements == null)
			throw new NoElementFoundException("No element was found for feature " + featureId);
		return new MinCountSingleIDBasedIterator<Element>(elements.iterator(),elementStorage,minCount);	
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getElementFeatureJointCounts()
	 */
	@Override
	public ImmutableIterator<ElementFeatureJointCounts> getElementFeatureJointCounts() {
		return new JointCountIterator(elemntFeatureCounts.iterator());
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getElementFeatureJointCounts(int)
	 */
	@Override
	public ImmutableIterator<ElementFeatureJointCounts> getElementFeatureJointCounts(long minCount) {
		return new MinCountJointCountIterator(elemntFeatureCounts.iterator(),minCount);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.ElementFeatureCountStorage#getTotalElementCount()
	 */
	@Override
	public double getTotalElementCount() {
		return totalElementCount;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#saveState(org.excitement.distsim.storage.PersistenceDevice[])
	 * 
	 * Assumption: four persistence devices are provided: 
	 *   1. elements
	 *   2. features
	 *   3. element-features
	 *   4. feature-elements
	 */
	@Override
	public void saveState(PersistenceDevice... devices) throws SavingStateException {
		if (devices.length != 4)
			throw new SavingStateException(devices.length + " persistence devices was providied for saving, where four are expected");
		
		logger.info("Saving elements...");
		elementStorage.saveState(devices[0]);
		logger.info("Saving features...");
		featureStorage.saveState(devices[1]);
		logger.info("Saving element-feature-counts...");
		elemntFeatureCounts.saveState(devices[2]);
		logger.info("Saving feature-elements...");
		fesatureElements.saveState(devices[3]);
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.storage.Persistence#loadState(org.excitement.distsim.storage.PersistenceDevice[])
	 * 
	 * Assumption: four persistence devices are provided:
	 *   1. elements
	 *   2. features
	 *   3. element-features
	 *   4. feature-elements
	 */
	@Override
	public void loadState(PersistenceDevice... devices) throws LoadingStateException {
		if (devices.length != 4)
			throw new LoadingStateException(devices.length + " persistence devices was providied for loading, where four are expected");
		elementStorage.loadState(devices[0]);
		featureStorage.loadState(devices[1]);
		elemntFeatureCounts.loadState(devices[2]);
		fesatureElements.loadState(devices[3]);		
	}
	

	protected final CountableIdentifiableStorage<Element> elementStorage;
	protected final CountableIdentifiableStorage<Feature> featureStorage;
	protected final IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elemntFeatureCounts;
	protected final IDKeyPersistentBasicMap<BasicSet<Integer>> fesatureElements;	
	protected final double totalElementCount;


}
