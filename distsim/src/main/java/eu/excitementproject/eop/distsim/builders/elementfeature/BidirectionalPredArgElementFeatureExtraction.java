/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.elementfeature;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.builders.ConfigurationBasedDataStructureFactory;
import eu.excitementproject.eop.distsim.builders.DataStructureFactory;
import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;
import eu.excitementproject.eop.distsim.items.ArgumentFeature;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.InvalidCountException;
import eu.excitementproject.eop.distsim.items.PredicateElement;
import eu.excitementproject.eop.distsim.items.Relation;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;
import gnu.trove.set.hash.TIntHashSet;


/**
 * Given a co-occurrence of predicate and argument and their dependency relation, extracts the predicate as
 * an element, and the dependency relation with the argument as a feature.
 * In case the argument slot (X or Y) is not relevant, the reversed predicate (denoted by @R@) is defined as an element
 *
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class BidirectionalPredArgElementFeatureExtraction extends IrelevantListBasedElementFeatureExtraction {

	private final static Logger logger = Logger.getLogger(BidirectionalPredArgElementFeatureExtraction.class);
	
	protected static final String REV_PRED = "@R@";
	
	public BidirectionalPredArgElementFeatureExtraction(PredicateArgumentSlots relevantSlot, int minCount) {
		this(relevantSlot, new HashSet<String>(),minCount);
	}

	public BidirectionalPredArgElementFeatureExtraction(PredicateArgumentSlots relevantSlot, Set<String> stopWordsFeatures, int minCount) {
		super(stopWordsFeatures);
		this.relevantSlot = relevantSlot;
		this.minCount = minCount;
	}
		
	public BidirectionalPredArgElementFeatureExtraction(ConfigurationParams params) throws ConfigurationException, IOException {
		super(params);
		this.relevantSlot = PredicateArgumentSlots.valueOf(params.get(Configuration.SLOT));
		try {
			this.minCount = params.getInt(Configuration.MIN_COUNT);
		} catch (ConfigurationException e) {
			this.minCount = 0;
		}
		initElementStorage(params);
	}
		
	protected void initElementStorage(ConfigurationParams params) {
		try {
			DataStructureFactory dataStructureFactory = new ConfigurationBasedDataStructureFactory(params.getConfigurationFile());			
			CountableIdentifiableStorage<Element> elementStorage = dataStructureFactory.createElementsDataStucture();
			ConfigurationParams elementDeviceParams = params.getSisterModuleConfiguration(Configuration.ELEMENTS_STORAGE_DEVICE);
			PersistenceDevice elementsDevice = (PersistenceDevice)Factory.create(elementDeviceParams.get(Configuration.CLASS), elementDeviceParams);
			elementsDevice.open();
			elementStorage.loadState(elementsDevice);
			
			irelevavtElementsForSimilarityCalculation = new TIntHashSet();
			ImmutableIterator<Element> it = elementStorage.iterator();
			while (it.hasNext()) {
				Element element = it.next();
				if (element.getData().toString().endsWith(REV_PRED))
					irelevavtElementsForSimilarityCalculation.add(element.getID());
			}
			
			//debug
			/*System.out.println("irelevavtElementsForSimilarityCalculation" + irelevavtElementsForSimilarityCalculation.size());
			TIntIterator tmpit = irelevavtElementsForSimilarityCalculation.iterator();
			while (tmpit.hasNext()) 
				System.out.print("\t" + tmpit.next());
			System.out.print("\n");*/
			
		} catch (Exception e) {
			logger.info("No element storage is used for determining relevant elements for similarity calculation [" + e.toString() +"]");
		}
	}
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.elementfeature.ElementFeatureExtractor#extractElementsFeatures(org.excitement.distsim.items.Cooccurrence)
	 */
	@Override
	public List<Pair<Element, Feature>> extractElementsFeature(Cooccurrence<?> cooccurrence) throws ElementFeatureExtractionException {
		
		List<Pair<Element, Feature>> ret = new LinkedList<Pair<Element, Feature>>();
		
		if (isStopWordFeature(cooccurrence.getTextItem2(),cooccurrence.getRelation()))
			return ret;
		
		try {
			if (cooccurrence.getTextItem1().getCount() < minCount)
				return ret;
		} catch (InvalidCountException e) {
			throw new ElementFeatureExtractionException(e);
		}
			
		try {
			if (isRelevantSlot(cooccurrence))
				ret.add(new Pair<Element, Feature>(
					new PredicateElement(cooccurrence.getTextItem1().toKey()),
					new ArgumentFeature((PredicateArgumentSlots)cooccurrence.getRelation().getValue(),cooccurrence.getTextItem2().toKey())
					));
			else
				ret.add(new Pair<Element, Feature>(
						new PredicateElement(cooccurrence.getTextItem1().toKey() + REV_PRED),
						new ArgumentFeature(PredicateArgumentSlots.getOpposite((PredicateArgumentSlots)cooccurrence.getRelation().getValue()),cooccurrence.getTextItem2().toKey())
						));
			return ret;
		} catch (UndefinedKeyException e) {			
			throw new ElementFeatureExtractionException(e);
		}
	}

	public boolean isRelevantSlot(Cooccurrence<?> cooccurrence) {
		return relevantSlot == PredicateArgumentSlots.ALL || cooccurrence.getRelation().getValue() == relevantSlot;
	}
	
	protected boolean isStopWordFeature(TextUnit textUnitFeature, Relation<?> rel) {
		return stopWordsFeatures.contains(textUnitFeature.getData().toString() + "\t" + rel.getValue().toString());
	}


	protected PredicateArgumentSlots relevantSlot;
	protected int minCount;
	
}
