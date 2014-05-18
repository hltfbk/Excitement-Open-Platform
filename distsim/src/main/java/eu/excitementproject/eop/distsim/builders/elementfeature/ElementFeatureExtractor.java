package eu.excitementproject.eop.distsim.builders.elementfeature;



import eu.excitementproject.eop.distsim.builders.Builder;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.storage.BasicCooccurrenceStorage;
import eu.excitementproject.eop.distsim.storage.CooccurrenceStorage;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.ElementFeatureCountStorage;

/**
 * The ElementFeatureExtractor interface defines the basic functionality of extracting elements and features from a given corpus, represented by {@link CooccurrenceStorage}
 * The overall outcome is an {@link ElementFeatureCountStorage} object, composed of all extracted elements, features and their joint.    

 * @author Meni Adler
 * @since 23/05/2012
 *
 * @param <R> the domain of the relation, as defined by {@link eu.excitementproject.eop.distsim.items.Relation} interface
 * 
 */
public interface ElementFeatureExtractor<R> extends Builder {
	/**
	 * The method extract elements and features from a given corpus, represented by co-occurrence instances DB.
	 * 
	 * @param cooccurrenceDB a data base of co-occurrences
	 * @return An ElementFeaturesDB composed of the extracted elements and features 
	 */
	ElementFeatureCountStorage constructElementFeatureDB(BasicCooccurrenceStorage<R> cooccurrenceDB) throws ElementFeatureCountsDBConstructionException;
	
	/**
	 * The method extract elements and features from a given corpus, represented by co-occurrence instances DB.
	 * 
	 * @param cooccurrenceDB a data base of co-occurrences
	 * @param elementStorage existing elements storage
	 * @return An ElementFeaturesDB composed of the extracted elements and features 
	 */
	ElementFeatureCountStorage constructElementFeatureDB(BasicCooccurrenceStorage<R> cooccurrenceDB,
			CountableIdentifiableStorage<Element> elementStorage) throws ElementFeatureCountsDBConstructionException;
	/**
	 * The method extract elements and features from a given corpus, represented by co-occurrence instances DB.
	 * 
	 * @param cooccurrenceDB a data base of co-occurrences
	 * @param elementStorage existing elements storage
	 * @param featureStorage existing featureStroage
	 * @return An ElementFeaturesDB composed of the extracted elements and features 
	 */
	ElementFeatureCountStorage constructElementFeatureDB(BasicCooccurrenceStorage<R> cooccurrenceDB,
			CountableIdentifiableStorage<Element> elementStorage,
			CountableIdentifiableStorage<Feature> featureStorage) throws ElementFeatureCountsDBConstructionException;

}
