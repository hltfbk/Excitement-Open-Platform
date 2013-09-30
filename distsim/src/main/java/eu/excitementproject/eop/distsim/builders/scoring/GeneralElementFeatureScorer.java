/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.scoring;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
import eu.excitementproject.eop.distsim.builders.ConfigurationBasedDataStructureFactory;
import eu.excitementproject.eop.distsim.builders.DataStructureFactory;
import eu.excitementproject.eop.distsim.builders.VectorTruncate;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.items.Feature;
import eu.excitementproject.eop.distsim.scoring.element.ElementScoring;
import eu.excitementproject.eop.distsim.scoring.feature.FeatureScoring;
import eu.excitementproject.eop.distsim.storage.BasicMap;
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.DefaultElementFeatureCountStorage;
import eu.excitementproject.eop.distsim.storage.ElementFeatureCountStorage;
import eu.excitementproject.eop.distsim.storage.ElementFeatureJointCounts;
import eu.excitementproject.eop.distsim.storage.FeatureCount;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.SortUtil;


/**
 * A general implementation of the {@link ElementFeatureScorer} interface, where characteristic of the process is determined by a given set of parameters,
 * which defines the scoring method for elements and features, the data structures, the output storage type, the number of concurrent threads,
 * and the feature filtering policy 
 * 
 * @author Meni Adler
 * @since 04/09/2012
 *
 */
public class GeneralElementFeatureScorer implements ElementFeatureScorer {

	private final static Logger logger = Logger.getLogger(GeneralElementFeatureScorer.class);
	
	public GeneralElementFeatureScorer(int iThreadNum, FeatureScoring featureScoring, 
			ElementScoring elementScoring,
			CommonFeatureCriterion commonFeatureCriterion,
			VectorTruncate vectorTruncater,
			DataStructureFactory dataStructureFactory) {
		this.iThreadNum = iThreadNum;
		this.featureScoring = featureScoring;
		this.elementScoring = elementScoring;
		this.commonFeatureCriterion = commonFeatureCriterion;
		this.vectorTruncater = vectorTruncater;
		this.dataStructureFactory = dataStructureFactory;
		
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.scoring.ElementFeatureScorer#scoreElementsFeatures(org.excitement.distsim.storage.ElementFeatureCountStorage, org.excitement.distsim.scoring.feature.FeatureScoring, org.excitement.distsim.scoring.element.ElementScoring)
	 */
	@Override
	public void scoreElementsFeatures(ElementFeatureCountStorage elementFeaturecounts, PersistenceDevice elementFeatureScoreDevice, PersistenceDevice elementScoreDevice) throws ElementFeatureScorerException {

		try {
			ImmutableIterator<ElementFeatureJointCounts> iterator = elementFeaturecounts.getElementFeatureJointCounts();
			
			ExecutorService executor = Executors.newFixedThreadPool(iThreadNum);
			// start the collector tasks
			for (int i=0;i<iThreadNum; i++)
				executor.execute(new ElementFeatureScoringTask(i+1,iterator,elementFeaturecounts,elementFeatureScoreDevice,elementScoreDevice));
			
			// wait for terminations of all collectors
			try {
				executor.shutdown();
				while (!executor.awaitTermination(1, TimeUnit.HOURS)) {
					logger.info("Still waiting for collector threads termination");
				}
			} catch (InterruptedException e) {
				logger.error(e.toString());
			}
			
			logger.info("All scorer threads were terminated");
			
		} catch (Exception e) {
			throw new ElementFeatureScorerException(e);
		}
	}

	
	protected int iThreadNum;
	protected final FeatureScoring featureScoring;
	protected final ElementScoring elementScoring;
	protected final CommonFeatureCriterion commonFeatureCriterion;
	protected final VectorTruncate vectorTruncater;
	protected final DataStructureFactory dataStructureFactory;
	
	
	class ElementFeatureScoringTask implements Runnable {
		
		private final Logger logger = Logger.getLogger(ElementFeatureScoringTask.class);
		
		ElementFeatureScoringTask(int threadID, ImmutableIterator<ElementFeatureJointCounts> iterator,
				ElementFeatureCountStorage elementFeaturecounts,
				PersistenceDevice elementFeatureScoreDevice, 
				PersistenceDevice elementScoreDevice) {
			this.threadID = threadID;
			this.iterator = iterator;
			this.elementFeaturecounts = elementFeaturecounts;
			this.elementFeatureScoreDevice = elementFeatureScoreDevice;
			this.elementScoreDevice = elementScoreDevice;
		}
		
		public void run() {
			
			int loop=1;
			
			while (true) {
			
				if (loop % 100000 == 0) {
					logger.info(loop + " elements were processed");
				}
				loop++;
				
				
				// get next element-feature joint count
				ElementFeatureJointCounts elementFeatureJointCount = null;				
				boolean acquired = false;
				synchronized (iterator) {
					try {
						elementFeatureJointCount = iterator.next();
						acquired = true;
					} catch (NoSuchElementException e) {
					}	
				}		
				if (!acquired) {
					logger.info("Thread " + threadID + " is done");
					return;
				}

				//compute element-feature scores
				try {
					
					Element element = elementFeaturecounts.getElement(elementFeatureJointCount.getElementId());

					ImmutableIterator<FeatureCount> featureCounts = elementFeatureJointCount.getFeatureCounts();
					// measure the score for each feature, based on its counts
					Map<Integer,Double> tmpCommonScoredFeatures = new HashMap<Integer,Double>();
					Map<Integer,Double> tmpScoredFeatures = new HashMap<Integer,Double>();
					while (featureCounts.hasNext()) {
						FeatureCount featureJointCount = featureCounts.next();
						Feature feature = elementFeaturecounts.getFeature(featureJointCount.getFeatureId());
						
						double jointCount = featureJointCount.getCount();				
						double score = featureScoring.score(element, feature, elementFeaturecounts.getTotalElementCount(), jointCount);
						
						//debug
						//System.out.println("Element: " + element.getID() + " " + element.getCount() + ", Feature: " + feature.getID() + " " + feature.getCount() + ", joint count: " + jointCount + ", total element count: " + elementFeaturecounts.getTotalElementCount() + ", Score: " + score);
							
						// @TOTHIN: does score > 0.0 always hold
						if (score > 0.0 && (commonFeatureCriterion == null || commonFeatureCriterion.isCommon(elementFeaturecounts,featureJointCount.getFeatureId()))) 
							tmpCommonScoredFeatures.put(featureJointCount.getFeatureId(), score);
						if (score > 0.0)
							tmpScoredFeatures.put(featureJointCount.getFeatureId(), score);
					}
					// sort features by their scores
					@SuppressWarnings("unchecked")
					LinkedHashMap<Integer,Double> sortedScores = SortUtil.sortMapByValue(tmpCommonScoredFeatures, true);
					
					// truncate the vector, if required
					if (vectorTruncater != null)
						sortedScores = vectorTruncater.truncate(sortedScores);
					
					// store the scorers for the element and the features
					elementFeatureScoreDevice.write(elementFeatureJointCount.getElementId(), sortedScores);
					elementScoreDevice.write(elementFeatureJointCount.getElementId(), elementScoring.score(tmpScoredFeatures.values()));
				} catch (Exception e) {
					logger.error(ExceptionUtil.getStackTrace(e));
				}
			}
		}
		
		int threadID;
		ImmutableIterator<ElementFeatureJointCounts> iterator;
		final ElementFeatureCountStorage elementFeaturecounts;
		final PersistenceDevice elementFeatureScoreDevice; 
		final PersistenceDevice elementScoreDevice;
	}
	
public static void main(String[] args) {
		

/*		PropertyConfigurator.configure("log4j.properties");
		
		try {
			
			if (args.length != 7) {
				System.err.println("Usage: GeneralElementFeatureScorer" +
						"\n\t <threads number> " +
						"\n\t <in elements file> " +
						"\n\t <in features file>" +
						"\n\t <in element-feature-counts file> " +
						"\n\t <in feature-elements file>" +
						"\n\t <out element-feature-score file> " +
						"\n\t <out element-scores file>"
				);
				System.exit(0);
			}

			int iThreadNum = Integer.parseInt(args[0]);
			org.excitement.distsim.storage.File elementsfile = new org.excitement.distsim.storage.File(new File(args[1]),true);
			elementsfile.open();
			org.excitement.distsim.storage.File featuresfile = new org.excitement.distsim.storage.File(new File(args[2]),true);
			featuresfile.open();
			org.excitement.distsim.storage.File elementfeaturesfile = new org.excitement.distsim.storage.File(new File(args[3]),true);
			elementfeaturesfile.open();
			org.excitement.distsim.storage.File featureelementsfile = new org.excitement.distsim.storage.File(new File(args[4]),true);
			featureelementsfile.open();
			
			ElementFeatureCountStorage elementFeatureCountStorage = new DefaultElementFeatureCountStorage (
					//new RedisBasedCountableIdentifiableStorage<Element>(args[1],Integer.parseInt(args[2])),
					//new RedisBasedCountableIdentifiableStorage<Feature>(args[3],Integer.parseInt(args[4])),
					//new RedisBasedIDKeyBasicMap<BasicMap<Integer,Double>>(args[5],Integer.parseInt(args[6])),
					//new RedisBasedIDKeyBasicMap<BasicSet<Integer>>(args[7],Integer.parseInt(args[8])));					
					
					new MemoryBasedCountableIdentifiableStorage<Element>(elementsfile),
					new MemoryBasedCountableIdentifiableStorage<Feature>(featuresfile),
					new TroveBasedIDKeyPersistentBasicMap<BasicMap<Integer,Double>>(elementfeaturesfile),
					new TroveBasedIDKeyPersistentBasicMap<BasicSet<Integer>>(featureelementsfile));

			elementsfile.close();
			featuresfile.close();
			elementfeaturesfile.close();
			featureelementsfile.close();
			logger.info("Finshed loading element-feature count storage");
			
			//FeatureScoring featureScoring = new PMI();
			FeatureScoring featureScoring = new PMI();
			ElementScoring elementScoring = new L1Norm();
			ElementFeatureScorer scorer = new GeneralElementFeatureScorer(iThreadNum, featureScoring, elementScoring);
			ElementFeatureScoreStorage db = scorer.scoreElementsFeatures(elementFeatureCountStorage);
			 
			logger.info("Saving data to file");
			
			// save the element feature counts db
			//Redis elemntFeaturesScores = new Redis(args[9],Integer.parseInt(args[10]));
			//Redis elementScores = new Redis(args[11],Integer.parseInt(args[12]));			
			org.excitement.distsim.storage.File elemntFeaturesScores = new org.excitement.distsim.storage.File(new File(args[5]),false);
			org.excitement.distsim.storage.File elementScores = new org.excitement.distsim.storage.File(new File(args[6]),false);

			elemntFeaturesScores.open();
			elementScores.open();
			db.saveState(elemntFeaturesScores,elementScores);
			elemntFeaturesScores.close();
			elementScores.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
*/		
	
	try {
		
		if (args.length != 1) {
			System.err.println("Usage: GeneralElementFeatureScorer <configuration file>");
			System.exit(0);
		}

		ConfigurationFile confFile = new ConfigurationFile(args[0]);
		
		ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
		PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
					
		ConfigurationParams scorerParams = confFile.getModuleConfiguration(Configuration.ELEMENT_FEATURE_SCORING);			
		int iThreadNum = scorerParams.getInt(Configuration.THREAD_NUM);
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
		
		DataStructureFactory dataStructureFactory = new ConfigurationBasedDataStructureFactory(confFile);
		
		CountableIdentifiableStorage<Element> elements =dataStructureFactory.createElementsDataStucture();
		elements.loadState(elementsDevice); 
		CountableIdentifiableStorage<Feature> features = dataStructureFactory.createFeaturesDataStucture();
		features.loadState(featuresDevice);
		IDKeyPersistentBasicMap<BasicMap<Integer,Double>> elementFeatureCounts =  dataStructureFactory.createElementFeatureCountsDataStructure();
		elementFeatureCounts.loadState(elementFeatureCountsDevice);
		IDKeyPersistentBasicMap<BasicSet<Integer>> featureElements = dataStructureFactory.createFeatureElementsDataStructure();
		featureElements.loadState(featureElementsDevice);
		ElementFeatureCountStorage elementFeatureCountStorage =  			
			new DefaultElementFeatureCountStorage (elements,features,elementFeatureCounts,featureElements);
		elementsDevice.close();
		featuresDevice.close();
		elementFeatureCountsDevice.close();
		featureElementsDevice.close();
		logger.info("Finshed loading element-feature count storage");
		
		//FeatureScoring featureScoring = new PMI();
		FeatureScoring featureScoring = (FeatureScoring)Factory.create(scorerParams.get(Configuration.FEATURE_SCORING_CLASS), scorerParams);
		ElementScoring elementScoring = (ElementScoring)Factory.create(scorerParams.get(Configuration.ELEMENT_SCORING_CLASS), scorerParams);

		CommonFeatureCriterion commonFeatureCriterion = null;
		ConfigurationParams commonFeatureCriterionParams = null;
		try {
			commonFeatureCriterionParams = confFile.getModuleConfiguration(Configuration.COMMON_FEATURE_CRITERION);
		} catch (ConfigurationException e) {}
		if (commonFeatureCriterionParams != null)
			commonFeatureCriterion = (CommonFeatureCriterion)Factory.create(commonFeatureCriterionParams.get(Configuration.CLASS), commonFeatureCriterionParams);

		VectorTruncate vectorTruncater = null;
		ConfigurationParams vectorTruncaterParams = null;
		try {
			vectorTruncaterParams = confFile.getModuleConfiguration(Configuration.VECTOR_TRUNCATE);
		} catch (ConfigurationException e) {
			logger.info("no vector truncter was defined: " + e);
		}
		if (vectorTruncaterParams != null) {
			vectorTruncater = (VectorTruncate)Factory.create(vectorTruncaterParams.get(Configuration.CLASS), vectorTruncaterParams);
			logger.info("vector truncter: "  + vectorTruncater.toString());
		}
		
		ElementFeatureScorer scorer = new GeneralElementFeatureScorer(iThreadNum, featureScoring, elementScoring, commonFeatureCriterion, vectorTruncater, new ConfigurationBasedDataStructureFactory(confFile));
		 
		ConfigurationParams elemntFeaturesScoreParams = confFile.getModuleConfiguration(Configuration.ELEMENT_FEATURE_SCORES_STORAGE_DEVICE);
		PersistenceDevice elemntFeaturesScoresDevice = (PersistenceDevice)Factory.create(elemntFeaturesScoreParams.get(Configuration.CLASS), elemntFeaturesScoreParams);
		ConfigurationParams elementScoreParams = confFile.getModuleConfiguration(Configuration.ELEMENT_SCORES_STORAGE_DEVICE);
		PersistenceDevice elementScoresDevice = (PersistenceDevice)Factory.create(elementScoreParams.get(Configuration.CLASS), elementScoreParams);
		elemntFeaturesScoresDevice.open();
		elementScoresDevice.open();		
		scorer.scoreElementsFeatures(elementFeatureCountStorage,elemntFeaturesScoresDevice,elementScoresDevice);
		elemntFeaturesScoresDevice.close();
		elementScoresDevice.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	}


}

