package eu.excitementproject.eop.core.utilities.dictionary.wiktionary.jwktl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import de.tudarmstadt.ukp.wiktionary.api.Wiktionary;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;

/**
 * Offers a command line interface to Wiktionary. 
 * You can type a word and after pressing &lt;enter&gt; the information of corresponding word entries will be printed.
 * In order to quit the interface just hit enter;    
 *
 */
public class JwktlDemoWiktionaryCli {

	/**
	 * @param args path to parsed Wiktionary data
	 */
	public static void main(String[] args) {
		final String PROMPT = " > ";
		final String END = "";
		String wktPath = args[0];
		
		Wiktionary wkt = new Wiktionary(wktPath);
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print(PROMPT);
		String line = null;
		try {
			while((line=reader.readLine())!=null) {
				if(line.equals(END))
					break;
				List<WordEntry> wordEntries = wkt.getWordEntries(line);
				
				if(wordEntries==null || wordEntries.size()==0) {
					System.out.println("is not in Wiktionary");

				} else {
					System.out.println(Wiktionary.getEntryInformation(wordEntries));
				}
				
				System.out.print(PROMPT);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			wkt.close();
		}
		System.out.println("exit");


	}

}
