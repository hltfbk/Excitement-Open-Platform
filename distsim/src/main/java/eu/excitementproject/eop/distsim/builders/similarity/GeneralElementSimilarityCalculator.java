/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.similarity;

import java.io.IOException;
import java.util.LinkedHashMap;
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
import eu.excitementproject.eop.distsim.builders.elementfeature.ElementFeatureExtraction;
import eu.excitementproject.eop.distsim.scoring.ElementFeatureScores;
import eu.excitementproject.eop.distsim.scoring.FeatureScore;
import eu.excitementproject.eop.distsim.scoring.feature.DefaultElementFeatureData;
import eu.excitementproject.eop.distsim.scoring.similarity.DefaultFeatureValueConstructor;
import eu.excitementproject.eop.distsim.scoring.similarity.ElementFeatureValueConstructor;
import eu.excitementproject.eop.distsim.scoring.similarity.ElementSimilarityScoring;
import eu.excitementproject.eop.distsim.storage.BasicSet;
import eu.excitementproject.eop.distsim.storage.DefaultElementFeatureScoreStorage;
import eu.excitementproject.eop.distsim.storage.ElementFeatureScoreStorage;
import eu.excitementproject.eop.distsim.storage.IDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.NoScoreFoundException;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.CreationException;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.SerializationException;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;


/**
 * A general implementation of the {@link ElementSimilarityCalculator} interface. The characteristics of the process is determined by a given set of parameters,
 * which define the similarity method, the number of concurrent threads, the data structures, the output storage, and more
 * 
 * @author Meni Adler
 * @since 11/09/2012
 *
 */
public class GeneralElementSimilarityCalculator implements ElementSimilarityCalculator {

	private final static Logger logger = Logger.getLogger(GeneralElementSimilarityCalculator.class);
	
	public GeneralElementSimilarityCalculator(final ConfigurationParams similarityCalculatorParams) throws ConfigurationException, CreationException {
		this.iThreadNum = similarityCalculatorParams.getInt(Configuration.THREAD_NUM);
		this.elementSimilarityScoringFactory = new ElementSimilarityScoringFactory() { 
			public ElementSimilarityScoring create() throws CreationException { 
				try { 
					return (ElementSimilarityScoring)Factory.create(similarityCalculatorParams.get(Configuration.SIMILARITY_SCORING_CLASS),similarityCalculatorParams);
				} catch (Exception e) {
					throw new CreationException(e);
				}
			}
		};

		try {
			this.entailingElementFeatureValueConstructor = (ElementFeatureValueConstructor)Factory.create(similarityCalculatorParams.get(Configuration.ENTAILING_ELEMENT_FEATURE_CONSTRUCTOR));
		} catch (ConfigurationException e) {
			this.entailingElementFeatureValueConstructor = new DefaultFeatureValueConstructor();
		}
		try {
			this.entailedElementFeatureValueConstructor = (ElementFeatureValueConstructor)Factory.create(similarityCalculatorParams.get(Configuration.ENTAILED_ELEMENT_FEATURE_CONSTRUCTOR));
		} catch (ConfigurationException e) {
			this.entailedElementFeatureValueConstructor = new DefaultFeatureValueConstructor();
		}

		try {
			ConfigurationParams extractionParams = similarityCalculatorParams.getSisterModuleConfiguration(similarityCalculatorParams.get(Configuration.ELEMENT_FEATURE_EXTRACTION_MODULE));
			this.elementFilter = (ElementFeatureExtraction)Factory.create(extractionParams.get(Configuration.CLASS),extractionParams);
		} catch (ConfigurationException e) {
			this.elementFilter = null;
		}
		
		vectorTruncater = null;
		ConfigurationParams vectorTruncaterParams = null;
		try {
			vectorTruncaterParams = similarityCalculatorParams.getSisterModuleConfiguration(Configuration.VECTOR_TRUNCATE);
		} catch (ConfigurationException e) {
			logger.info("no vector truncter was defined: " + e);
		}
		if (vectorTruncaterParams != null) {
			vectorTruncater = (VectorTruncate)Factory.create(vectorTruncaterParams.get(Configuration.CLASS), vectorTruncaterParams);
			logger.info("vector truncter: "  + vectorTruncater.toString());
		}


		
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.similarity.ElementSimilarityCalculator#measureElementSimilarity(org.excitement.distsim.storage.ElementFeatureScoreStorage, org.excitement.distsim.scoring.similarity.ElementSimilarityScoring)
	 */
	@Override
	public void measureElementSimilarity(ElementFeatureScoreStorage elementFeatureScores, PersistenceDevice outR2LDevice, PersistenceDevice outL2RDevice) throws ElementSimilarityException {

		//leftElemntSimilarities = new TIntObjectHashMap<TIntDoubleMap>();
		
		ImmutableIterator<ElementFeatureScores> iterator = elementFeatureScores.getElementsFeatureScores();
		
		ExecutorService executor = Executors.newFixedThreadPool(iThreadNum);
		// start the collector tasks
		for (int i=0;i<iThreadNum; i++)
			executor.execute(new ElementSimilarityTask(i+1,iterator,elementFeatureScores,outR2LDevice,outL2RDevice));
		
		// wait for terminations of all collectors
		try {
			executor.shutdown();
			while (!executor.awaitTermination(1, TimeUnit.HOURS)) {
				logger.info("Still waiting for collector threads termination");
			}
		} catch (InterruptedException e) {
			//logger.error(e.toString());
			ExceptionUtil.getStackTrace(e);
		}
		
		
		logger.info("All similarity calculation threads were terminated");
	}

	
//	protected abstract void measureElementSimilarity(ElementFeatureScoreStorage elementFeatureScores, ConfigurationParams storageDeviceParams) throws ElementSimilarityException;
	
//	protected abstract void addSimilarity(int elementId1, int elementId2, double similarityScore);

	protected final int iThreadNum;
	protected final ElementSimilarityScoringFactory elementSimilarityScoringFactory;
	protected ElementFeatureExtraction elementFilter;
	protected ElementFeatureValueConstructor entailingElementFeatureValueConstructor;
	protected ElementFeatureValueConstructor entailedElementFeatureValueConstructor;
	protected VectorTruncate vectorTruncater;
	
	class ElementSimilarityTask implements Runnable {
		
		private final Logger logger = Logger.getLogger(ElementSimilarityTask.class);
		
		ElementSimilarityTask(
				int threadID,
				ImmutableIterator<ElementFeatureScores> iterator,
				ElementFeatureScoreStorage elementFeatureScoreStorage,
				PersistenceDevice outR2LDevice,
				PersistenceDevice outL2RDevice) {
			
			this.threadID = threadID;
			this.iterator = iterator;
			this.elementFeatureScoreStorage = elementFeatureScoreStorage;
			this.outR2LDevice = outR2LDevice;
			this.outL2RDevice = outL2RDevice;

		}
		
		public void run() {
			
			logger.info("Thread " + threadID + " starts running");
			
			int loop=1;
			while (true) {
			
				if (loop % 100 == 0) 
					logger.info(loop + " elements were processed");
				loop++;
					
				// get next element-feature score
				ElementFeatureScores elementFeatureScores = null;
				boolean acquired = false;
				synchronized (iterator) {
					try {
						elementFeatureScores = iterator.next();
						acquired = true;
					} catch (NoSuchElementException e) {
					}	
				}
				if (!acquired) {
					logger.info("Thread " + threadID + " is done");
					return;
				} 					
				
				try {
					int element1Id = elementFeatureScores.getElementId();
					
					if (elementFilter == null || elementFilter.isRelevantElementForCalculation(element1Id)) {
						//debug
						//logger.info("entailedElementId: " + entailedElementId);
						
						double element1Score = elementFeatureScoreStorage.getElementScore(element1Id);
						if (element1Score > 0) { // @TOCHECK: whether it is always true
							TIntObjectMap<ElementSimilarityScoring> entailingElementID2SimilarityScoring = new TIntObjectHashMap<ElementSimilarityScoring>();
							TIntObjectMap<ElementSimilarityScoring> entailedElementID2SimilarityScoring = new TIntObjectHashMap<ElementSimilarityScoring>();
							ImmutableIterator<FeatureScore> featureScores = elementFeatureScores.getFeatureScores();
							int featureNum = elementFeatureScores.getFeatureScoresNum();
							int featureRank = 1;
							while (featureScores.hasNext()) {
								FeatureScore featureScore = featureScores.next();
								int featureId = featureScore.getFeatureId();
								double score1 = entailingElementFeatureValueConstructor.constructFeatureValue(
										new DefaultElementFeatureData(featureScore.getScore(), featureRank,featureNum));
								
								if (score1 > 0) {
									BasicSet<Integer> featureElements = elementFeatureScoreStorage.getFeatureElements(featureId);	
									ImmutableIterator<Integer> it = featureElements.iterator();
									while (it.hasNext()) {
										int element2Id = it.next();
										if (element2Id != element1Id) {
											double score2 = 0;
											try {
												score2 = entailingElementFeatureValueConstructor.constructFeatureValue(
														elementFeatureScoreStorage, element2Id, featureId);
												//logger.info("score " + entailingScore + " for featureId " + featureId + ", entailingElementId " + entailingElementId);
											} catch (Exception e) {
												//logger.error("score 0 for featureId " + featureId + ", entailingElementId " + entailingElementId);
											}
											if (score2 > 0) {
												
												if (outR2LDevice != null) {
													ElementSimilarityScoring entailingSimilarityScoring = entailingElementID2SimilarityScoring.get(element2Id);
													if (entailingSimilarityScoring == null) {
														entailingSimilarityScoring = elementSimilarityScoringFactory.create();
														entailingElementID2SimilarityScoring.put(element2Id,entailingSimilarityScoring);
													}
													entailingSimilarityScoring.addElementFeatureScore(score2, score1);
												}
												
												ElementSimilarityScoring entailedSimilarityScoring = entailedElementID2SimilarityScoring.get(element2Id);
												if (entailedSimilarityScoring == null) {
													entailedSimilarityScoring = elementSimilarityScoringFactory.create();
													entailedElementID2SimilarityScoring.put(element2Id,entailedSimilarityScoring);
												}
												entailedSimilarityScoring.addElementFeatureScore(score1,score2);

											}
										}
									}
									featureRank++;
								}
							}
							
							if (outR2LDevice !=  null)
								writeSimnilarity(element1Id,element1Score,entailingElementID2SimilarityScoring.iterator(),true);
							writeSimnilarity(element1Id,element1Score,entailedElementID2SimilarityScoring.iterator(),false);
						}
					}
				} catch (Exception e) {
					//debug
					logger.error(ExceptionUtil.getStackTrace(e));
				}				
			}
		}
				
		protected void writeSimnilarity(int element1Id, double element1Score, TIntObjectIterator<ElementSimilarityScoring> it, boolean bR2L) throws SerializationException, IOException, NoScoreFoundException {
			TIntDoubleMap elementsScores = new TIntDoubleHashMap();
			while (it.hasNext()) {
				it.advance();
				int element2Id = it.key();
				if (elementFilter == null || elementFilter.isRelevantElementForCalculation(element2Id)) {
					double element2Score = elementFeatureScoreStorage.getElementScore(element2Id);
					ElementSimilarityScoring similarityScoring = it.value();
					double similarityScore = bR2L ?
								similarityScoring.getSimilarityScore(element2Score, element1Score) :
								similarityScoring.getSimilarityScore(element1Score, element2Score);
					if (similarityScore > 0)
						elementsScores.put(element2Id, similarityScore);
				}
			}
			
			LinkedHashMap<Integer, Double> sortedElementScores = SortUtil.sortMapByValue(elementsScores,true);
			
			if (vectorTruncater != null) 
				sortedElementScores = vectorTruncater.truncate(sortedElementScores);
				
			if (bR2L)
				outR2LDevice.write(element1Id, sortedElementScores);
			else
				outL2RDevice.write(element1Id, sortedElementScores);
		}
		
		final int threadID;
		final ImmutableIterator<ElementFeatureScores> iterator;
		final ElementFeatureScoreStorage elementFeatureScoreStorage;
		final PersistenceDevice outR2LDevice;
		final PersistenceDevice outL2RDevice;
	}
	
	
	public static void main(String[] args) {
		

/*		PropertyConfigurator.configure("log4j.properties");
		
		try {			
			if (args.length != 5) {
				System.err.println("Usage: GeneralElementSimilarityCalculator" +
						"\n\t <threads number> " +
						"\n\t <in element-feature scores file> " +
						"\n\t <in element score file>" +
						"\n\t <in feature element file> " +
						"\n\t <out left element similarity file>" 
				);
				System.exit(0);
			}

			int iThreadNum = Integer.parseInt(args[0]);
			ElementSimilarityScoringFactory elementSimilarityScoringFactory = new ElementSimilarityScoringFactory() { public ElementSimilarityScoring create() { return new Lin(); }};				
			
			// build the element score storage
			org.excitement.distsim.storage.File elementfeaturescorefile = new org.excitement.distsim.storage.File(new File(args[1]),true);
			elementfeaturescorefile.open();
			org.excitement.distsim.storage.File elementscorefile = new org.excitement.distsim.storage.File(new File(args[2]),true);
			elementscorefile.open();
			org.excitement.distsim.storage.File featureelementfile = new org.excitement.distsim.storage.File(new File(args[3]),true);
			featureelementfile.open();
			ElementFeatureScoreStorage elementFeatureScoreStorage = new DefaultElementFeatureScoreStorage (
					new TroveBasedIDKeyPersistentBasicMap<LinkedHashMap<Integer,Double>>(elementfeaturescorefile),
					new TroveBasedIDKeyPersistentBasicMap<Double>(elementscorefile),
					new TroveBasedIDKeyPersistentBasicMap<BasicSet<Integer>>(featureelementfile));
			elementfeaturescorefile.close();
			elementscorefile.close();
			featureelementfile.close();			
			logger.info("Finshed loading storage");
			ElementSimilarityCalculator similator = new GeneralElementSimilarityCalculator(iThreadNum,elementSimilarityScoringFactory);
					
			// claculate similarities
			BasicMap<Integer,TIntDoubleMap> leftSimilarities = similator.measureElementSimilarity(elementFeatureScoreStorage);
			 			
			// save the element similarity db
			logger.info("Saving data to file");
			org.excitement.distsim.storage.File leftSimilaritiesFile = new org.excitement.distsim.storage.File(new File(args[4]),false);
			leftSimilaritiesFile.open();
			ImmutableIterator<Pair<Integer, TIntDoubleMap>> it = leftSimilarities.iterator();
			while (it.hasNext()) {
				Pair<Integer, TIntDoubleMap> pair = it.next();
				leftSimilaritiesFile.write(pair.getFirst(), SortUtil.sortMapByValue(pair.getSecond(),true));
			}			
			leftSimilaritiesFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		} */
		
		try {			
			if (args.length != 1) {
				System.err.println("Usage: GeneralElementSimilarityCalculator <configuration file>");
				System.exit(0);
			}

			ConfigurationFile confFile = new ConfigurationFile(args[0]);
			
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			//final Logger logger = Logger.getLogger(GeneralElementSimilarityCalculator.class);
						
			final ConfigurationParams similarityCalculatorParams = confFile.getModuleConfiguration(Configuration.ELEMENT_SIMILARITY_CLACULATOR);			
			
			DataStructureFactory dataStructureFactory = new ConfigurationBasedDataStructureFactory(confFile);

			
			// build the element score storage
			ConfigurationParams elemntFeaturesScoreParams = confFile.getModuleConfiguration(Configuration.ELEMENT_FEATURE_SCORES_STORAGE_DEVICE);
			PersistenceDevice elemntFeaturesScoresDevice = (PersistenceDevice)Factory.create(elemntFeaturesScoreParams.get(Configuration.CLASS), elemntFeaturesScoreParams);
			ConfigurationParams elementScoreParams = confFile.getModuleConfiguration(Configuration.ELEMENT_SCORES_STORAGE_DEVICE);
			PersistenceDevice elementScoresDevice = (PersistenceDevice)Factory.create(elementScoreParams.get(Configuration.CLASS), elementScoreParams);
			ConfigurationParams featureElementsDeviceParams = confFile.getModuleConfiguration(Configuration.FEATURE_ELEMENTS_STORAGE_DEVICE);
			PersistenceDevice featureElementsDevice = (PersistenceDevice)Factory.create(featureElementsDeviceParams.get(Configuration.CLASS), featureElementsDeviceParams);
			elemntFeaturesScoresDevice.open();
			elementScoresDevice.open();
			featureElementsDevice.open();

			IDKeyPersistentBasicMap<LinkedHashMap<Integer, Double>> elementFeatureScores = dataStructureFactory.createElementFeatureScoresDataStructure();
			elementFeatureScores.loadState(elemntFeaturesScoresDevice);
			IDKeyPersistentBasicMap<Double> elementScores = dataStructureFactory.createElementScoresDataStructure();
			elementScores.loadState(elementScoresDevice);
			IDKeyPersistentBasicMap<BasicSet<Integer>> featureElements = dataStructureFactory.createFeatureElementsDataStructure();
			featureElements.loadState(featureElementsDevice);
			ElementFeatureScoreStorage elementFeatureScoreStorage = new DefaultElementFeatureScoreStorage (
					elementFeatureScores,elementScores,featureElements);
			
			elemntFeaturesScoresDevice.close();
			elementScoresDevice.close();
			featureElementsDevice.close();
			
			logger.info("Finshed loading storage");
						
			GeneralElementSimilarityCalculator similator = (GeneralElementSimilarityCalculator)Factory.create(similarityCalculatorParams.get(Configuration.CLASS), similarityCalculatorParams);
			//new GeneralElementSimilarityCalculator(iThreadNum,elementSimilarityScoringFactory);
					
			// claculate similarities
			PersistenceDevice similaritiesOutR2LDevice = null;
			try {
				ConfigurationParams storageR2LDeviceParams = confFile.getModuleConfiguration(Configuration.ELEMENTS_SIMILARITIES_R2L_STORAGE_DEVICE);			
				similaritiesOutR2LDevice = (PersistenceDevice)Factory.create(storageR2LDeviceParams.get(Configuration.CLASS), storageR2LDeviceParams);
				similaritiesOutR2LDevice.open();
			} catch (ConfigurationException e) {
				logger.info("L2R device is not defined");
			}
			ConfigurationParams storageL2RDeviceParams = confFile.getModuleConfiguration(Configuration.ELEMENTS_SIMILARITIES_L2R_STORAGE_DEVICE);			
			PersistenceDevice similaritiesOutL2RDevice = (PersistenceDevice)Factory.create(storageL2RDeviceParams.get(Configuration.CLASS), storageL2RDeviceParams);
			similaritiesOutL2RDevice.open();
			similator.measureElementSimilarity(elementFeatureScoreStorage,similaritiesOutR2LDevice,similaritiesOutL2RDevice);
			if (similaritiesOutR2LDevice != null)
				similaritiesOutR2LDevice.close();
			similaritiesOutL2RDevice.close();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
