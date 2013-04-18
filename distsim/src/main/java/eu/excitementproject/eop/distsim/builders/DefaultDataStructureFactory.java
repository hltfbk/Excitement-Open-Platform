/**
 * 
 */
package eu.excitementproject.eop.distsim.builders;

import java.util.LinkedHashMap;

import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.IDBasedCooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.MemoryBasedCountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.PersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.CreationException;

/**
 * A default implementation of the {@link DataStructureFactory} which is based on a given configuration file, which defines the various data structures
 * 
 * @author Meni Adler
 * @since 17/10/2012
 *
 *
 */
public class DefaultDataStructureFactory implements DataStructureFactory {

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createTextUnitsDataStructure()
	 */
	@Override
	public CountableIdentifiableStorage<TextUnit> createTextUnitsDataStructure() throws CreationException {
		return new MemoryBasedCountableIdentifiableStorage<TextUnit>();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createCooccurrencesDataStucture()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public CountableIdentifiableStorage<IDBasedCooccurrence> createCooccurrencesDataStucture() throws CreationException {
		return new MemoryBasedCountableIdentifiableStorage<IDBasedCooccurrence>();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createElementsDataStucture()
	 */
	@Override
	public CountableIdentifiableStorage<Element> createElementsDataStucture() throws CreationException {
		return new MemoryBasedCountableIdentifiableStorage<Element>();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createFeaturesDataStucture()
	 */
	@Override
	public CountableIdentifiableStorage<Feature> createFeaturesDataStucture() throws CreationException {
		return new MemoryBasedCountableIdentifiableStorage<Feature>();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createElementFeatureCountsDataStructure()
	 */
	@Override
	public PersistentBasicMap<BasicMap<Integer, Double>> createElementFeatureCountsDataStructure() throws CreationException {
		return new TroveBasedIDKeyPersistentBasicMap<BasicMap<Integer, Double>>();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createFeatureElementsDataStructure()
	 */
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createFeatureElementsDataStructure()
	 */
	@Override
	public PersistentBasicMap<BasicSet<Integer>> createFeatureElementsDataStructure() throws CreationException {
		return new TroveBasedIDKeyPersistentBasicMap<BasicSet<Integer>>();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createElementFeatureScoresDataStructure()
	 */
	@Override
	public PersistentBasicMap<LinkedHashMap<Integer,Double>> createElementFeatureScoresDataStructure() throws CreationException {
		return new TroveBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>();
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createElementScoresDataStructure()
	 */
	@Override
	public PersistentBasicMap<Double> createElementScoresDataStructure() throws CreationException {
		return new TroveBasedIDKeyPersistentBasicMap<Double>();
	}

}
