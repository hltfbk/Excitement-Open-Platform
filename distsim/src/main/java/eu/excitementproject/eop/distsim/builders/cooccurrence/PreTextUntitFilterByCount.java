package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.distsim.builders.reader.FileBasedSentenceReader;
import eu.excitementproject.eop.distsim.builders.reader.SentenceReaderException;
import eu.excitementproject.eop.distsim.items.Cooccurrence;
import eu.excitementproject.eop.distsim.items.TextUnit;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.FileUtils;
import eu.excitementproject.eop.distsim.util.Pair;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

public class PreTextUntitFilterByCount {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		try {
			
			if (args.length != 1) {
				System.err.println("Usage: GeneralCooccurrenceExtractor <configuarion file>");
				System.exit(0);
			}
	
			//ConfigurationFile confFile = new ConfigurationFile(args[0]);
			ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
		
			ConfigurationParams loggingParams = confFile.getModuleConfiguration(Configuration.LOGGING);
			PropertyConfigurator.configure(loggingParams.get(Configuration.PROPERTIES_FILE));
			Logger logger = Logger.getLogger(PreTextUntitFilterByCount.class);
			
			ConfigurationParams filterParams = confFile.getModuleConfiguration(Configuration.CO_OCCURRENCE_EXTRACTOR);
			
			File corpus = new File(filterParams.get(Configuration.CORPUS));
			Set<File> files = FileUtils.getFiles(corpus);
			CooccurrenceExtraction cooccurrenceExtraction = (CooccurrenceExtraction) Factory.create(filterParams.get(Configuration.EXTRACTION_CLASS), filterParams);			
			FileBasedSentenceReader sentenceReader = (FileBasedSentenceReader)Factory.create(filterParams.get(Configuration.SENTENCE_READER_CLASS), filterParams);
			long minCount = filterParams.getLong(Configuration.MIN_COUNT);
			TObjectLongMap<String> countMap = new TObjectLongHashMap<String>();
			PrintStream out = new PrintStream(new FileOutputStream(filterParams.get(Configuration.OUTFILE)),false,filterParams.get(Configuration.ENCODING));
			
			int iSent = 0;
			for (File file : files) {				
				try {
					sentenceReader.setSource(file);
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
						iSent++;
						try {
							Pair<? extends List<? extends TextUnit>, ? extends List<? extends Cooccurrence>> pair = cooccurrenceExtraction.extractCooccurrences(sentenceAndCount.getFirst());
							List<? extends TextUnit> textUnits = pair.getFirst();
							for (TextUnit textUnit: textUnits) {
								String key = textUnit.toKey();
								long count = countMap.get(key);
								countMap.put(key, count + sentenceAndCount.getSecond());
							}
						} catch (Exception e) {
							logger.error(ExceptionUtil.getStackTrace(e));
						}
							
						
						if(iSent % 1000 == 0)
							logger.info("Sentence: " + iSent + " size of text units: "  + countMap.size());						
					}
					sentenceReader.closeSource();
				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
			
			
			TObjectLongIterator<String> it = countMap.iterator();
			int i=0;
			while (it.hasNext()) {
				it.advance();
				if (it.value() > minCount)
					out.println(it.key());
			}
			out.close();
			logger.info(i + " text units were left out of total " + countMap.size() + " (based on minimal count " + minCount + ")");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
