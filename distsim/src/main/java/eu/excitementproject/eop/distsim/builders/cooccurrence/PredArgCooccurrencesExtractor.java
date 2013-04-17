/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.cooccurrence;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.distsim.builders.DefaultDataStructureFactory;
import eu.excitementproject.eop.distsim.domains.relation.PredicateArgumentSlots;
import eu.excitementproject.eop.distsim.storage.CooccurrenceStorage;

/**
 * @author Meni Adler
 * @since 18/07/2012
 *
 * A memory-based, multi-threaded implementation of the CooccurrencesExtractor interface, 
 * for predicate-argument co-occurrences, given by tuple files
 */
public class PredArgCooccurrencesExtractor  {

	public static void main(String[] args) {
		try {
			
			if (args.length != 4) {
				System.err.println("Usage: PredArgCooccurrencesExtractor <corpus dir/file> <threads number> <out text-units file> <out co-occurrences file>");
				System.exit(0);
			}

			
			PropertyConfigurator.configure("log4j.properties");
			@SuppressWarnings("unchecked")
			CooccurrencesExtractor<PredicateArgumentSlots> extractor = new GeneralCooccurrenceExtractor(Integer.parseInt(args[1]), new TupleBasedPredArgCooccurrenceExtraction(), new LineBasedStringCountSentenceReader(),new DefaultDataStructureFactory());
			CooccurrenceStorage<PredicateArgumentSlots> db = extractor.constructCooccurrenceDB(new File(args[0]));
			
			eu.excitementproject.eop.distsim.storage.File textUnitsStorage = new eu.excitementproject.eop.distsim.storage.File(new File(args[2]),false);
			eu.excitementproject.eop.distsim.storage.File coOccurrencesStorage = new eu.excitementproject.eop.distsim.storage.File(new File(args[3]),false);

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

