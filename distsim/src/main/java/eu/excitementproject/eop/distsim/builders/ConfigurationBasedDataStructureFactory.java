package eu.excitementproject.eop.distsim.builders;

import java.util.LinkedHashMap;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.IDBasedCooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.CreationException;
import eu.excitementproject.eop.distsim.util.Factory;

/**
 * Defines memory-based data structure as the default implementation for the {@link DataStructureFactory} interface 
 *
 * @author Meni Adler
 * @since 17/10/2012
 *
 */
public class ConfigurationBasedDataStructureFactory implements DataStructureFactory {

	
	public ConfigurationBasedDataStructureFactory(ConfigurationFile confFile) throws ConfigurationException {
		textUnitParams = confFile.getModuleConfiguration(Configuration.TEXT_UNITS_DATA_STRUCTURE);
		coOccurrenceParams = confFile.getModuleConfiguration(Configuration.CO_OCCURRENCES_DATA_STRUCTURE);
		elementParams = confFile.getModuleConfiguration(Configuration.ELEMENTS_DATA_STRUCTURE);
		featureParams = confFile.getModuleConfiguration(Configuration.FEATURES_DATA_STRUCTURE);
		elementFeatureCountsParams = confFile.getModuleConfiguration(Configuration.ELEMENT_FEATURE_COUNTS_DATA_STRUCTURE);
		featureElementsParams = confFile.getModuleConfiguration(Configuration.FEATURE_ELEMENTS_DATA_STRUCTURE);
		elementFeatureScoresParams = confFile.getModuleConfiguration(Configuration.ELEMENT_FEATURE_SCORES_DATA_STRUCTURE);
		elementElementScoresParams = confFile.getModuleConfiguration(Configuration.ELEMENT_SCORES_DATA_STRUCTURE);
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createTextUnitsDataStructure()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CountableIdentifiableStorage<TextUnit> createTextUnitsDataStructure() throws CreationException {		
		try {
			return (CountableIdentifiableStorage<TextUnit>) Factory.create(textUnitParams.get(Configuration.CLASS),textUnitParams);
		} catch (Exception e) {
			throw new CreationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createCooccurrencesDataStucture()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public CountableIdentifiableStorage<IDBasedCooccurrence> createCooccurrencesDataStucture()  throws CreationException {
		try {
			return (CountableIdentifiableStorage<IDBasedCooccurrence>) Factory.create(coOccurrenceParams.get(Configuration.CLASS),coOccurrenceParams);
		} catch (Exception e) {
			throw new CreationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createElementsDataStucture()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CountableIdentifiableStorage<Element> createElementsDataStucture()  throws CreationException {
		try {
			return (CountableIdentifiableStorage<Element>) Factory.create(elementParams.get(Configuration.CLASS),elementParams);
		} catch (Exception e) {
			throw new CreationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createFeaturesDataStucture()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CountableIdentifiableStorage<Feature> createFeaturesDataStucture()  throws CreationException {
		try {
			return (CountableIdentifiableStorage<Feature>) Factory.create(featureParams.get(Configuration.CLASS),featureParams);
		} catch (Exception e) {
			throw new CreationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createElementFeatureCountsDataStructure()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IDKeyPersistentBasicMap<BasicMap<Integer, Double>> createElementFeatureCountsDataStructure()  throws CreationException {
		try {
			return (IDKeyPersistentBasicMap<BasicMap<Integer, Double>>) Factory.create(elementFeatureCountsParams.get(Configuration.CLASS),elementFeatureCountsParams);
		} catch (Exception e) {
			throw new CreationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createFeatureElementsDataStructure()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IDKeyPersistentBasicMap<BasicSet<Integer>> createFeatureElementsDataStructure()  throws CreationException {
		try {
			return (IDKeyPersistentBasicMap<BasicSet<Integer>>) Factory.create(featureElementsParams.get(Configuration.CLASS),featureElementsParams);
		} catch (Exception e) {
			throw new CreationException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createElementFeatureScoresDataStructure()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>> createElementFeatureScoresDataStructure()  throws CreationException {
		try {
			return (IDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>) Factory.create(elementFeatureScoresParams.get(Configuration.CLASS),elementFeatureScoresParams);
		} catch (Exception e) {
			throw new CreationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.DataStructureFactory#createElementScoresDataStructure()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IDKeyPersistentBasicMap<Double> createElementScoresDataStructure()  throws CreationException {
		try {
			return (IDKeyPersistentBasicMap<Double>) Factory.create(elementElementScoresParams.get(Configuration.CLASS),elementElementScoresParams);
		} catch (Exception e) {
			throw new CreationException(e);
		}
	}

	ConfigurationParams textUnitParams;
	ConfigurationParams coOccurrenceParams;
	ConfigurationParams elementParams;
	ConfigurationParams featureParams;
	ConfigurationParams elementFeatureCountsParams;
	ConfigurationParams featureElementsParams;
	ConfigurationParams elementFeatureScoresParams;
	ConfigurationParams elementElementScoresParams;
	
	

}
