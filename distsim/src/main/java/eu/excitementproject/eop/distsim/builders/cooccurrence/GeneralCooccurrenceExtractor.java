package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.builders.ConfigurationBasedDataStructureFactory;
import eu.excitementproject.eop.distsim.builders.DataStructureFactory;
import eu.excitementproject.eop.distsim.builders.reader.FileBasedSentenceReader;
import eu.excitementproject.eop.distsim.builders.reader.SentenceReaderException;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.IDBasedCooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.items.UndefinedKeyException;
import eu.excitementproject.eop.distsim.storage.CooccurrenceStorage;
import eu.excitementproject.eop.distsim.storage.CountableIdentifiableStorage;
import eu.excitementproject.eop.distsim.storage.DefaultCooccurrenceStorage;
import eu.excitementproject.eop.distsim.storage.PersistenceDevice;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.CreationException;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.FileUtils;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * A general implementation of the {@link CooccurrencesExtractor} interface,
 * where the behavior of the extraction is determined by given CooccurrenceExtraction and DataStructureFactory objects
 * 
 * @author Meni Adler
 * @since 24/10/2012
 *
 */
@SuppressWarnings("rawtypes")
public class GeneralCooccurrenceExtractor implements CooccurrencesExtractor {
	
	private static Logger logger = Logger.getLogger(GeneralCooccurrenceExtractor.class);
	/*
	public GeneralCooccurrenceExtractor(int iThreadNum, CooccurrenceExtraction cooccurrenceExtraction,FileBasedSentenceReader sentenceReader, DataStructureFactory dataStructureFactory) {
		this.iThreadNum = iThreadNum;
		this.cooccurrenceExtraction = cooccurrenceExtraction;
		this.dataStructureFactory = dataStructureFactory;
	}*/

	public GeneralCooccurrenceExtractor(ConfigurationParams params,DataStructureFactory dataStructureFactory) throws ConfigurationException, CreationException {
		this.iThreadNum = params.getInt(Configuration.THREAD_NUM);
		if (iThreadNum > 1) {
			logger.warn("Multi-threading is temporarilly not supported - a single thread will be applied instead");
		}
		this.confParams = params;
		this.textUnitStorage = dataStructureFactory.createTextUnitsDataStructure();
		this.cooccurrenceStorage = dataStructureFactory.createCooccurrencesDataStucture();
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.cooccurrence.CooccurrencesExtractor#constructCooccurrenceDB(java.io.File)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CooccurrenceStorage constructCooccurrenceDB(File corpus) throws CooccurrenceDBConstructionException {
		
		try {			 
						
			Set<File> files = FileUtils.getFiles(corpus);
			
			//tmp
			System.out.println("total number of files: " + files.size());
			
			int filesPerCollector = files.size() / iThreadNum; 
			if (files.size() % iThreadNum > 0)
				filesPerCollector++;
			Iterator<File> it = files.iterator();
			ExecutorService executor = Executors.newFixedThreadPool(iThreadNum);

			// start the collector tasks
			for (int i=0;i<iThreadNum; i++) {
				// construct a set of unique files for each collector
				Set<File> s = new HashSet<File>();
				for (int j = 0; j < filesPerCollector; j++) {
					if (it.hasNext()) {
						s.add(it.next());
						it.remove();
					}
				}
				// run the collector
				executor.execute(new CooccurrenceCollector(s,i));
			}
			
			
			// wait for terminations of all collectors
			try {
				executor.shutdown();
				while (!executor.awaitTermination(1, TimeUnit.DAYS));
			} catch (InterruptedException e) {
				throw new CooccurrenceDBConstructionException(e);
			}
			
			return new DefaultCooccurrenceStorage(textUnitStorage,cooccurrenceStorage);
		} catch (Exception e) {
			throw new CooccurrenceDBConstructionException(e);
		}
	}
	
	protected final int iThreadNum;	
	protected final ConfigurationParams confParams;
	protected CountableIdentifiableStorage<TextUnit> textUnitStorage;
	protected CountableIdentifiableStorage<IDBasedCooccurrence> cooccurrenceStorage;

	class CooccurrenceCollector implements Runnable {
			
		private final Logger logger = Logger.getLogger(CooccurrenceCollector.class);
		
		CooccurrenceCollector(Set<File> files, int id) throws CreationException, ConfigurationException {
			this.files = files;
			this.id = id;
			this.sentenceReader = (FileBasedSentenceReader) Factory.create(confParams.get(Configuration.SENTENCE_READER_CLASS), confParams);
			this.cooccurrenceExtraction = (CooccurrenceExtraction) Factory.create(confParams.get(Configuration.EXTRACTION_CLASS), confParams);
			
			//tmp
			System.out.println("Thread " + id + ": got " + files.size() + " files");
		}
		
		@SuppressWarnings("unchecked")
		public void run() {
			logger.info("Thread: " + id);
			
			int iSent = 0;
			for (File file : files) {				
				//logger.info("Thread: " + id + ", file: " + file.getName());
				try {
					sentenceReader.setSource(file);
					Pair<?,Long> sentenceAndCount;
					while(true) {
							
						try {
							sentenceAndCount =sentenceReader.nextSentence();
							if (sentenceAndCount == null)
								break;
						} catch (SentenceReaderException e){
							//logger.error(ExceptionUtil.getStackTrace(e));
							logger.error(e.toString());
							continue;
						}
						iSent++;
						try {
							
					
								
							Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence>> pair = cooccurrenceExtraction.extractCooccurrences(sentenceAndCount.getFirst());							
							List<? extends TextUnit> textUnits = pair.getFirst();
							List<? extends Cooccurrence> coOccurrences = pair.getSecond();
							
							//check if all text units fit the given set of
							long count = sentenceAndCount.getSecond();
							for (TextUnit textUnit : textUnits) {
								synchronized (textUnitStorage) {
									TextUnit inserted = textUnitStorage.addData(textUnit,count);
									textUnit.setID(inserted.getID());
								}
							}
							for (Cooccurrence coOccurrence : coOccurrences) {
								cooccurrenceStorage.addData(new IDBasedCooccurrence(coOccurrence.getTextItem1().getID(), coOccurrence.getTextItem2().getID(), coOccurrence.getRelation().getValue()),count);
							}
						} catch (UndefinedKeyException e) {
							logger.info(e.toString() + ". The element/feature is considered insignificant and will be filtered.");
						} catch (Exception e) {
							//logger.error(ExceptionUtils.getStackTrace(e));
							logger.error(e.toString());
						}
							
						
						if(iSent % 10000 == 0)
							logger.info("Sentence: " + iSent + " size of text units: "  + textUnitStorage.size());		
						
						}
					sentenceReader.closeSource();
				} catch (Exception e) {
					logger.error(ExceptionUtils.getStackTrace(e));
				}
			}
		}
		
		protected final CooccurrenceExtraction cooccurrenceExtraction;
		protected Set<File> files;
		protected int id;		
		protected final FileBasedSentenceReader sentenceReader;
	}
	
	public static void main(String[] args) {
		try {
			
			if (args.length != 1) {
				System.err.println("Usage: GeneralCooccurrenceExtractor <configuarion file>");
				System.exit(0);
			}

			ConfigurationFile confFile = new ConfigurationFile(args[0]);
			
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			
			ConfigurationParams extractorParams = confFile.getModuleConfiguration(Configuration.CO_OCCURRENCE_EXTRACTOR);			
			
			CooccurrencesExtractor<?> extractor = (CooccurrencesExtractor<?>)Factory.create(extractorParams.get(Configuration.EXTRACTOR_CLASS), extractorParams, new ConfigurationBasedDataStructureFactory(confFile));
						
			CooccurrenceStorage<?> db = extractor.constructCooccurrenceDB(new File(extractorParams.get(Configuration.CORPUS)));
			
			ConfigurationParams textUnitParams = confFile.getModuleConfiguration(Configuration.TEXT_UNITS_STORAGE_DEVICE);
			PersistenceDevice textUnitsStorage = (PersistenceDevice)Factory.create(textUnitParams.get(Configuration.CLASS), textUnitParams);
			ConfigurationParams coOccurrenceParams = confFile.getModuleConfiguration(Configuration.CO_OCCURENCES_STORAGE_DEVICE);
			PersistenceDevice coOccurrencesStorage = (PersistenceDevice)Factory.create(coOccurrenceParams.get(Configuration.CLASS), coOccurrenceParams);

			textUnitsStorage.open();
			coOccurrencesStorage.open();
			db.saveState(textUnitsStorage,coOccurrencesStorage);
			textUnitsStorage.close();
			coOccurrencesStorage.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
