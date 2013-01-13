package eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl;
import java.util.List;

import de.tudarmstadt.ukp.wiktionary.api.Language;
import de.tudarmstadt.ukp.wiktionary.api.WikiString;
import de.tudarmstadt.ukp.wiktionary.api.Wiktionary;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;



/**
 * This demo code comes with the JWKTL package
 * @author Amnon Lotan
 *
 * @since Dec 23, 2011
 */
public class JwktlDemoGettingStarted {

	/**
	 * Simple example which parses an English dump file and prints the entries for the word <i>Wiktionary</i>
	 * @param args name of the dump file, output directory for parsed data, ISO language code of the Wiktionary entry language (en/de), boolean value that specifies if existing parsed data should be deleted
	 */
	public static void main(String[] args) {
		args = new String[]{"D:/data/RESOURCES/Wiktionary/enwiktionary-20110618-pages-articles.xml",
				"D:/data/RESOURCES/Wiktionary/parse", "en", "true"};
		
		if(args.length != 4) {
			throw new IllegalArgumentException("Too few arguments. Required arguments: <DUMP_FILE> <OUTPUT_DIRECTORY> <ISO_LANGUAGE_CODE (en/de)> <OVERWRITE_EXISTING_DATA>");
		}
		String outputDirectory = args[1];
		
		// parse dump file
		// uncomment this if you want to prepare a wiktionary dump file for use (one off operation)
//		Wiktionary.parseWiktionaryDump(dumpFile, outputDirectory, languageIsoCode, overwriteExisting);
		
		// create new Wiktionary object using the parsed data
		Wiktionary wkt = new Wiktionary(outputDirectory);
		
		wkt.setAllowedWordLanguage(Language.ENGLISH);
		wkt.setAllowedEntryLanguage(Language.ENGLISH);
		wkt.setIsCaseSensitive(true);
		
		// get entries for "Wiktionary"
		List<WordEntry> entries = wkt.getWordEntries("tiger");
		
		// print information of entries
		System.out.println(Wiktionary.getEntryInformation(entries));
		
		System.out.println("\n************************************************************\n");
		System.out.println("\n************************************************************\n");
		System.out.println("\n************************************************************\n");
		
		
		System.out.println(entries.get(0).getDetailedInformation());
		
		System.out.println("\n************************************************************\n");
		System.out.println("\n************************************************************\n");
		System.out.println("\n************************************************************\n");
		
		for (WikiString gloss : entries.get(0).getGlosses())
			System.out.println(gloss.getPlainText());
		
		System.out.println("\n************************************************************\n");
		System.out.println("\n************************************************************\n");
		System.out.println("\n************************************************************\n");
		
		System.out.println(entries.get(0).getGloss(1));
		
		
//		int numSenses = entries.get(0).getNumberOfSenses();
//		String word = entries.get(0).getWord();
//		List<String> relatedWords = entries.get(0).getAllRelatedWords(RelationType.ANTONYM);
//		WikiString gloss = entries.get(0).getGloss(0);
		
		// close Wiktionary object
		wkt.close();	
	}

}
