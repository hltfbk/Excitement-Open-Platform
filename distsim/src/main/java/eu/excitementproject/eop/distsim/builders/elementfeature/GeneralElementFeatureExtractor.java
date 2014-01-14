/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.elementfeature;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.builders.ConfigurationBasedDataStructureFactory;
import eu.excitementproject.eop.distsim.builders.DataStructureFactory;
import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.BasicCooccurrenceStorage;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.DefaultElementFeatureCountStorage;
import eu.excitementproject.eop.distsim.storage.DeviceBasedCooccurrenceStorage;
import eu.excitementproject.eop.distsim.storage.ElementFeatureCountStorage;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.storage.TroveBasedBasicIntSet;
import eu.excitementproject.eop.distsim.storage.TroveBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.CreationException;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;


/**
 * A general implementation of the {@link ElementFeatureExtractor} interface, where the process characteristic is determined by a given set of parameteers,
 * which define the extraction method, the number of concurrent threads, the data structures, and the output storage type 
 * 
 * @author Meni Adler
 * @since 01/08/2012
 *
 */
@SuppressWarnings("rawtypes")
public class GeneralElementFeatureExtractor implements ElementFeatureExtractor {

	private static Logger logger = Logger.getLogger(GeneralElementFeatureExtractor.class);

	public GeneralElementFeatureExtractor(int iThreadNum,DataStructureFactory dataStructureFactory, ConfigurationParams confParams) {
		this.iThreadNum = iThreadNum;
		this.dataStructureFactory = dataStructureFactory;
		this.confParams = confParams;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.elementfeature.ElementsFeaturesExtractor#constructElementFeatureDB(org.excitement.distsim.storage.CooccurrenceStorage)
	 */
	@Override 
	public ElementFeatureCountStorage constructElementFeatureDB(BasicCooccurrenceStorage cooccurrenceDB) throws ElementFeatureCountsDBConstructionException {
		try {
			CountableIdentifiableStorage<Element> elementStorage = dataStructureFactory.createElementsDataStucture();
			CountableIdentifiableStorage<Feature> featureStorage = dataStructureFactory.createFeaturesDataStucture();
			return constructElementFeatureDB(cooccurrenceDB,elementStorage,featureStorage);
		} catch (Exception e) {
			throw new ElementFeatureCountsDBConstructionException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.elementfeature.ElementsFeaturesExtractor#constructElementFeatureDB(org.excitement.distsim.storage.CooccurrenceStorage)
	 */
	@SuppressWarnings("unchecked")
	@Override 
	public ElementFeatureCountStorage constructElementFeatureDB(
			BasicCooccurrenceStorage cooccurrenceDB,
			CountableIdentifiableStorage elementStorage,
			CountableIdentifiableStorage featureStorage) throws ElementFeatureCountsDBConstructionException {
		
		try {
			
			IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elemntFeatureCounts = dataStructureFactory.createElementFeatureCountsDataStructure();
			IDKeyPersistentBasicMap<BasicSet<Integer>> fesatureElements  = dataStructureFactory.createFeatureElementsDataStructure();	
	
			ImmutableIterator<Cooccurrence<PredicateArgumentSlots>> it = cooccurrenceDB.getCooccurrenceInstances();
			
			
			ExecutorService executor = Executors.newFixedThreadPool(iThreadNum);
			// start the collector tasks
			for (int i=0;i<iThreadNum; i++)
				executor.execute(new ElementsFeaturesCollectionTask(i+1,it,elementStorage,featureStorage,elemntFeatureCounts,fesatureElements));
			
			// wait for terminations of all collectors
			try {
				executor.shutdown();
				while (!executor.awaitTermination(1, TimeUnit.HOURS)) {
					logger.info("Still waiting for collector threads termination");
				}
			} catch (InterruptedException e) {
				logger.error(e.toString());
			}
			
			logger.info("All collector threads were terminated");
			
			return new DefaultElementFeatureCountStorage(elementStorage, featureStorage,elemntFeatureCounts,fesatureElements);
		} catch (Exception e) {
			throw new ElementFeatureCountsDBConstructionException(e);
		}
	}
	
	protected final int iThreadNum;
	protected final DataStructureFactory dataStructureFactory;
	protected final ConfigurationParams confParams;
	
	class ElementsFeaturesCollectionTask implements Runnable {
		
		private final Logger logger = Logger.getLogger(ElementsFeaturesCollectionTask.class);
		
		ElementsFeaturesCollectionTask(
				int threadID,
				ImmutableIterator<Cooccurrence<PredicateArgumentSlots>> iterator,
				CountableIdentifiableStorage<Element> elementStorage,
				CountableIdentifiableStorage<Feature> featureStorage,
				IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elemntFeatureCounts,
				IDKeyPersistentBasicMap<BasicSet<Integer>> fesatureElements) throws CreationException, ConfigurationException {
			
			this.threadID = threadID;
			this.iterator = iterator;
			this.elementStorage = elementStorage;
			this.featureStorage = featureStorage;
			this.elemntFeatureCounts = elemntFeatureCounts;
			this.fesatureElements = fesatureElements;
			
			this.elementFeatureExtraction = (ElementFeatureExtraction)Factory.create(confParams.get(Configuration.EXTRACTION_CLASS),confParams);


		}
		
		@Override
		public void run() {
			
				logger.info("Thread " + threadID + " starts running");
				
				int loop=1;
				
				int c=0;
				
				while (true) {

					if (loop % 100000 == 0) {
						logger.info("Loop: " + loop);
					}
					loop++;
						
					// get next co-occurence
					Cooccurrence<PredicateArgumentSlots> coOccurrence = null;
					boolean acquired = false;
					
					synchronized (iterator) {
						//try {
							if (iterator.hasNext()) {
								coOccurrence = iterator.next();
								acquired = true;
								c++;
							}
						//} catch (NoSuchElementException e) {
							//debug
							//logger.error(ExceptionUtil.getStackTrace(e));
						//}	
					}
					if (!acquired) {
						logger.info("Thread " + threadID + " is done");
						break;
					}


					// extract element and feature from the given co-occurrence, and store their counts
					try {
											
						for (Pair<Element,Feature> elementFeaturePair :  elementFeatureExtraction.extractElementsFeature(coOccurrence)) {
	
							Element element = elementFeaturePair.getFirst();
							Feature feature = elementFeaturePair.getSecond();
								
							// inc element count
							element = elementStorage.addData(element, coOccurrence.getCount());
							
							//inc feature count
							feature = featureStorage.addData(feature, coOccurrence.getCount());
	
							// inc element-feature count
							synchronized (elemntFeatureCounts) {
								BasicMap<Integer, Double> featureCounts = elemntFeatureCounts.get(element.getID());
								if (featureCounts == null) {
									featureCounts = new TroveBasedIDKeyPersistentBasicMap<Double>();
									elemntFeatureCounts.put(element.getID(), featureCounts);
								}
								Double count = featureCounts.get(feature.getID());
								if (count == null)
									count = 0.0;
								featureCounts.put(feature.getID(), count+coOccurrence.getCount());
							}
						
							// add element to the list of elements for the given feature
							synchronized (fesatureElements) {
								// add element to the feature's element list
								BasicSet<Integer> elements = fesatureElements.get(feature.getID());
								if (elements == null) {
									elements = new TroveBasedBasicIntSet();
									fesatureElements.put(feature.getID(),elements);
								}
								elements.add(element.getID());
							}
						}
					} catch (UndefinedKeyException e) {
						logger.info(e.toString() + ". The element/feature is considered insignificant and will be filtered.");
					} catch (ElementFeatureExtractionException e) {
						System.out.println(e.toString());						
					} catch (Exception e) {
						logger.error(ExceptionUtil.getStackTrace(e));
					}				
				}
				
				System.out.println(c + " cooccurrences were processed by extractor " + threadID);
								
		}
				
		final int threadID;
		final ImmutableIterator<Cooccurrence<PredicateArgumentSlots>> iterator;
		final CountableIdentifiableStorage<Element> elementStorage;
		final CountableIdentifiableStorage<Feature> featureStorage;
		final IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elemntFeatureCounts;
		final IDKeyPersistentBasicMap<BasicSet<Integer>> fesatureElements;	
		final ElementFeatureExtraction elementFeatureExtraction;

	}		
	
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
				
		
		try {
			
			if (args.length != 1) {
				System.err.println("Usage: GeneralElementsFeaturesExtractor <configuarion file>");
				System.exit(0);
			}

			//ConfigurationFile confFile = new ConfigurationFile(args[0]);
			ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
			
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			logger = Logger.getLogger(GeneralElementFeatureExtractor.class);
						    
			ConfigurationParams extractorParams = confFile.getModuleConfiguration(Configuration.ELEMENT_FEATURE_EXTRACTOR);			
			
			int iThreadNum = extractorParams.getInt(Configuration.THREAD_NUM);
			ConfigurationParams textUnitParams = confFile.getModuleConfiguration(Configuration.TEXT_UNITS_STORAGE_DEVICE);
			PersistenceDevice textUnitDevice = (PersistenceDevice)Factory.create(textUnitParams.get(Configuration.CLASS), textUnitParams);
			ConfigurationParams coOccurrenceParams = confFile.getModuleConfiguration(Configuration.CO_OCCURENCES_STORAGE_DEVICE);
			PersistenceDevice coOccurrenceDevice = (PersistenceDevice)Factory.create(coOccurrenceParams.get(Configuration.CLASS), coOccurrenceParams);
			textUnitDevice.open();
			coOccurrenceDevice.open();
			
			DataStructureFactory dataStructureFactory = new ConfigurationBasedDataStructureFactory(confFile);
			
			CountableIdentifiableStorage<TextUnit> textUnitStorage = dataStructureFactory.createTextUnitsDataStructure();
			textUnitStorage.loadState(textUnitDevice);
			BasicCooccurrenceStorage cooccurrenceDB = new DeviceBasedCooccurrenceStorage(textUnitStorage,coOccurrenceDevice);
			
			logger.info("co-occurrence storage was loaded");
			
			ElementFeatureExtractor<?> extractor = new GeneralElementFeatureExtractor(iThreadNum,dataStructureFactory,extractorParams);

			ConfigurationParams prevElementDeviceParams = null, prevFeatureDeviceParams=null;
			ElementFeatureCountStorage db = null;
			try {
				prevElementDeviceParams = confFile.getModuleConfiguration(Configuration.PREV_ELEMENTS_STORAGE_DEVICE);
				prevFeatureDeviceParams = confFile.getModuleConfiguration(Configuration.PREV_FEATURES_STORAGE_DEVICE);
			} catch (ConfigurationException e) {
				//debug
				if (prevElementDeviceParams == null)
					logger.info("No previous elements");
				if (prevFeatureDeviceParams == null)
					logger.info("No previous features");

			}
			
			
			if (prevElementDeviceParams != null && prevFeatureDeviceParams != null) {
				PersistenceDevice elementsDevice = (PersistenceDevice)Factory.create(prevElementDeviceParams.get(Configuration.CLASS), prevElementDeviceParams);
				elementsDevice.open();
				CountableIdentifiableStorage<Element> elements = dataStructureFactory.createElementsDataStucture();
				elements.loadState(elementsDevice);
				elementsDevice.close();
				elements.resetCounts();
				PersistenceDevice featuresDevice = (PersistenceDevice)Factory.create(prevFeatureDeviceParams.get(Configuration.CLASS), prevFeatureDeviceParams);
				featuresDevice.open();
				CountableIdentifiableStorage<Feature> features = dataStructureFactory.createFeaturesDataStucture();
				features.loadState(featuresDevice);
				featuresDevice.close();
				features.resetCounts();
				
				logger.info("Using previous elements and features");
				
				db = extractor.constructElementFeatureDB(cooccurrenceDB, elements, features);
			} else
				db = extractor.constructElementFeatureDB(cooccurrenceDB);
			
			textUnitDevice.close();
			coOccurrenceDevice.close();
			
			logger.info("Saving data...");
			
			// save the element feature counts db
			ConfigurationParams elementDeviceParams = confFile.getModuleConfiguration(Configuration.ELEMENTS_STORAGE_DEVICE);
			PersistenceDevice elementsDevice = (PersistenceDevice)Factory.create(elementDeviceParams.get(Configuration.CLASS), elementDeviceParams);
			ConfigurationParams featureDeviceParams = confFile.getModuleConfiguration(Configuration.FEATURES_STORAGE_DEVICE);
			PersistenceDevice featuresDevice = (PersistenceDevice)Factory.create(featureDeviceParams.get(Configuration.CLASS), featureDeviceParams);
			ConfigurationParams elementFeatureCountsDeviceParams = confFile.getModuleConfiguration(Configuration.ELEMENT_FEATURE_COUNTS_STORAGE_DEVICE);
			PersistenceDevice elementFeatureCountsDevice = (PersistenceDevice)Factory.create(elementFeatureCountsDeviceParams.get(Configuration.CLASS), elementFeatureCountsDeviceParams);
			ConfigurationParams featureElementsDeviceParams = confFile.getModuleConfiguration(Configuration.FEATURE_ELEMENTS_STORAGE_DEVICE);
			PersistenceDevice featureElementsDevice = (PersistenceDevice)Factory.create(featureElementsDeviceParams.get(Configuration.CLASS), featureElementsDeviceParams);

			elementsDevice.open();
			featuresDevice.open();
			elementFeatureCountsDevice.open();
			featureElementsDevice.open();
			
			db.saveState(elementsDevice,featuresDevice,elementFeatureCountsDevice,featureElementsDevice);
			
			elementsDevice.close();
			featuresDevice.close();
			elementFeatureCountsDevice.close();
			featureElementsDevice.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
 
