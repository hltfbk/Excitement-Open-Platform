package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jmwn;

import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;


/**
 * 
 * Demo class for testing JMultiWordNet
 * 
 * The parameter is the config file required by the API
 * 
 * @author nastase
 *
 */

public class JmwnDemo {
	public static void main(String[] args)
	{
		System.out.println("starting.");
		try
		{

			String configFile = "/hardmnt/destromath0/home/nastase/Projects/EXC/JMWN_maven/src/main/config/multiwordnet.properties";
			JmwnDictionaryManager manager = new JmwnDictionaryManager(configFile);
			Dictionary dictionary = manager.newDictionary();
			
			System.out.println("Dictionary loaded. Starting...");
			String word = "mela";
			Map<WordNetPartOfSpeech,Set<Synset>> mapSynsets = dictionary.getSynsetOf(word);   //getSortedSynsetOf(word);
			
			if (mapSynsets != null) {
			
				System.out.println("dictionary.getSortedSynsetOf(" + word + ") returned " + mapSynsets.size() + " results:");
				for (WordNetPartOfSpeech pos : mapSynsets.keySet()) {
					System.out.println(pos.toString());
					Set<Synset> list = mapSynsets.get(pos);
					int i = 1;
					for (Synset synset : list)
					{
						System.out.print("\n_____________________________________________\nSynset "+i+": ");
					
						Set<String> words = synset.getWords();
						for (String s : words)
						{
							System.out.print(s+"("+synset.getUsageOf(s)+"), ");
						}
						System.out.println();
										
						int depth = 1;
						System.out.println("Displaying hypernyms to depth " + depth+":");
						displaySynset(synset.getRelatedSynsets(WordNetRelation.HYPERNYM, depth));
						System.out.println();

						WordNetRelation relation = WordNetRelation.PART_MERONYM;    //.DERIVATIONALLY_RELATED;
						System.out.println("Displaying "+relation+" neighbors up to depth "+depth+":");
						displaySynset(synset.getRelatedSynsets(relation, depth));
						System.out.println();
						
						System.out.println("Displaying meronyms");
						displaySynset(synset.getMeronyms());
						System.out.println();

						System.out.println("Displaying holonyms");
						displaySynset(synset.getHolonyms());
						System.out.println();

						System.out.println();
						i++;
					}
				}
				System.out.println("Done!");
			} else {
				System.out.println("No synsets found!");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
		
	}
	
	
	private static void displaySynset(Set<Synset> hyp) throws WordNetException{
		if (hyp!=null) {
			int i = 0;
			for (Synset synHyp : hyp) {
				Set<String> words = synHyp.getWords();
				System.out.print("synset " + i + " : ");
				if (words != null && !words.isEmpty()) {
					for (String word : words) {
						System.out.print(word+", ");
					}
					System.out.println();
				} else {
					System.out.println(" synset " + synHyp + "is empty!");
				}
				i++;
			}
		}
	}
	
	
	
	
	
}
