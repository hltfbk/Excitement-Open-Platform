package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.builders.ConfigurationBasedDataStructureFactory;
import eu.excitementproject.eop.distsim.builders.DataStructureFactory;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.IDBasedCooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;
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
	
	//private static Logger logger = Logger.getLogger(GeneralCooccurrenceExtractor.class);
	
	public GeneralCooccurrenceExtractor(int iThreadNum, CooccurrenceExtraction cooccurrenceExtraction,StreamBasedSentenceReader sentenceReader, DataStructureFactory dataStructureFactory) {
		this.iThreadNum = iThreadNum;
		this.cooccurrenceExtraction = cooccurrenceExtraction;
		this.sentenceReader = sentenceReader;
		this.dataStructureFactory = dataStructureFactory;
	}

	public GeneralCooccurrenceExtractor(ConfigurationParams params,DataStructureFactory dataStructureFactory) throws ConfigurationException, CreationException {
		this.iThreadNum = params.getInt(Configuration.THREAD_NUM);
		this.cooccurrenceExtraction = (CooccurrenceExtraction) Factory.create(params.get(Configuration.EXTRACTION_CLASS), params);
		this.sentenceReader = (StreamBasedSentenceReader) Factory.create(params.get(Configuration.SENTENCE_READER_CLASS), params);
		this.dataStructureFactory = dataStructureFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.builders.cooccurrence.CooccurrencesExtractor#constructCooccurrenceDB(java.io.File)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CooccurrenceStorage constructCooccurrenceDB(File corpus) throws CooccurrenceDBConstructionException {
		
		try {
			CountableIdentifiableStorage<TextUnit> textUnitStorage = dataStructureFactory.createTextUnitsDataStructure();
			CountableIdentifiableStorage<IDBasedCooccurrence> cooccurrenceStorage = (dataStructureFactory.createCooccurrencesDataStucture());
			 
			Set<File> files = FileUtils.getFiles(corpus);
			int filesPerCollector = files.size() / iThreadNum; 
			if (files.size() % iThreadNum > 0)
				filesPerCollector++;
			Iterator<File> it = files.iterator();
			ExecutorService executor = Executors.newFixedThreadPool(iThreadNum);
			
			// start the collector tasks
			for (int i=0;i<iThreadNum; i++) {
				Set<File> s = new HashSet<File>();
				for (int j = 0; j < filesPerCollector; j++) {
					if (it.hasNext()) {
						s.add(it.next());
						it.remove();
					}
				}
				executor.execute(new CooccurrenceCollector(textUnitStorage,cooccurrenceStorage,s,i));
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
	protected final CooccurrenceExtraction cooccurrenceExtraction;
	protected final StreamBasedSentenceReader sentenceReader;
	protected final DataStructureFactory dataStructureFactory;
	
	class CooccurrenceCollector implements Runnable {
			
		private final Logger logger = Logger.getLogger(CooccurrenceCollector.class);
		
		CooccurrenceCollector(CountableIdentifiableStorage<TextUnit> textUnitStorage, CountableIdentifiableStorage<IDBasedCooccurrence> cooccurrenceStorage,Set<File> files, int id) {
			this.textUnitStorage = textUnitStorage;
			this.cooccurrenceStorage = cooccurrenceStorage;
			this.files = files;
			this.id = id;
		}
		
		@SuppressWarnings("unchecked")
		public void run() {
			logger.info("Thread: " + id);
			//System.out.println(files);
			for (File file : files) {				
				//logger.info("Thread: " + id + ", file: " + file.getName());
				try {
					sentenceReader.setSource(new FileInputStream(file));
					int i = 0;
					Pair<?,Long> sentenceAndCount;
					while(true) {
						try {
							sentenceAndCount =sentenceReader.nextSentence();
							if (sentenceAndCount == null)
								break;
						} catch (SentenceReaderException e){
							logger.error(e.toString());
							continue;
						}
						try {
							Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence>> pair = cooccurrenceExtraction.extractCooccurrences(sentenceAndCount.getFirst());
							List<? extends TextUnit> textUnits = pair.getFirst();
							List<? extends Cooccurrence> coOccurrences = pair.getSecond();
							long count = sentenceAndCount.getSecond();
							for (TextUnit textUnit : textUnits) {
								TextUnit inserted = textUnitStorage.addData(textUnit,count);
								textUnit.setID(inserted.getID());
							}
							for (Cooccurrence coOccurrence : coOccurrences) {
								cooccurrenceStorage.addData(new IDBasedCooccurrence(coOccurrence.getTextItem1().getID(), coOccurrence.getTextItem2().getID(), coOccurrence.getRelation().getValue()),count);
							}
						} catch (Exception e) {
							logger.error(ExceptionUtil.getStackTrace(e));
						}
							
						
						if(i % 1000000 == 0)
							logger.info("File: " + file.getName() + ", line: " + i + " size of text units: "  + textUnitStorage.size());
						i++;
					}
					logger.info("File: " + file.getName() + ", total number of lines: " + i + " total number of text units: "  + textUnitStorage.size());
					sentenceReader.close();
				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
		}
		
		protected CountableIdentifiableStorage<TextUnit> textUnitStorage;
		protected CountableIdentifiableStorage<IDBasedCooccurrence> cooccurrenceStorage;
		protected Set<File> files;
		protected int id;		
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
			
			CooccurrencesExtractor<?> extractor = (CooccurrencesExtractor<?>)Factory.create(extractorParams.get("extractor-class"), extractorParams, new ConfigurationBasedDataStructureFactory(confFile));
			
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
