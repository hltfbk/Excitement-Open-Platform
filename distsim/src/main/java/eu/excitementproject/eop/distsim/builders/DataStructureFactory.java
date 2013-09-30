package eu.excitementproject.eop.distsim.builders;

import java.util.LinkedHashMap;

import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.IDBasedCooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.CreationException;

/**
 * Defines various kind of data structure for storage of common data
 * 
 * @author Meni Adler
 * @since 24/10/2012
 *
 */

public interface DataStructureFactory {
	CountableIdentifiableStorage<TextUnit> createTextUnitsDataStructure() throws CreationException;
	@SuppressWarnings("rawtypes")
	CountableIdentifiableStorage<IDBasedCooccurrence> createCooccurrencesDataStucture()  throws CreationException;
	CountableIdentifiableStorage<Element> createElementsDataStucture() throws CreationException;
	CountableIdentifiableStorage<Feature> createFeaturesDataStucture() throws CreationException;
	IDKeyPersistentBasicMap<BasicMap<Integer,Double>> createElementFeatureCountsDataStructure() throws CreationException;
	IDKeyPersistentBasicMap<BasicSet<Integer>> createFeatureElementsDataStructure() throws CreationException;	
	IDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>> createElementFeatureScoresDataStructure() throws CreationException;
	IDKeyPersistentBasicMap<Double> createElementScoresDataStructure() throws CreationException;
}
