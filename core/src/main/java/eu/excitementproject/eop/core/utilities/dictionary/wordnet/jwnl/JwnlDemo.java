package eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Dictionary;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.Synset;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetException;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetPartOfSpeech;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl.JwnlDictionaryManager.JwnlDictionaryManagementType;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.jwnl.JwnlDictionaryManager.JwnlDictionarySupportedVersion;



/**
 * A demo program for {@link JwnlDictionaryManager} - the Java WordNet dictionary.
 * @param args a single argument which is the path of WordNet data folder, e.g.
 * 	 /home/erelsgl/workspace/WordNet-3.0/dict
 * @author erelsgl
 */
public class JwnlDemo
{
	public static void main(String[] args)
	{
		System.out.println("starintg.");
		try
		{
			String pathToDict = args.length==0? "d:\\Data\\RESOURCES\\WordNet\\3.0\\dict.wn.orig": args[0];

			JwnlDictionaryManager manager = new JwnlDictionaryManager(JwnlDictionaryManagementType.DISK,JwnlDictionarySupportedVersion.VER_30,new File(pathToDict));
			Dictionary dictionary = manager.newDictionary();
			
			System.out.println("Dictionary loaded. Starting...");
			String word = "work";
			Map<WordNetPartOfSpeech,List<Synset>> mapSynsets = dictionary.getSortedSynsetOf(word);
			System.out.println("dictionary.getSortedSynsetOf("+word+") returned "+mapSynsets.size()+" results");
			for (WordNetPartOfSpeech pos : mapSynsets.keySet()) {
				System.out.println(pos.toString());
				List<Synset> list = mapSynsets.get(pos);
				int i = 1;
				for (Synset synset : list)
				{
					System.out.print(""+i+": ");
					
					Set<String> words = synset.getWords();
					for (String s : words)
					{
						System.out.print(s+"("+synset.getUsageOf(s)+"), ");
					}
					System.out.println();
					
					
					
					int depth = 3;
					System.out.println("Displaying hypernyms to depth " + depth+":");
					displayHypernyms(synset, depth);
					System.out.println();

					WordNetRelation relation = WordNetRelation.DERIVATIONALLY_RELATED;
					System.out.println();
					System.out.println("Displaying "+relation+" neighbors up to depth "+depth+":");
					displayNeighbors(synset, relation, depth);
					System.out.println();
					
					System.out.println();
					System.out.println();
					i++;
				}
			}
			System.out.println("Done!");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
		
	}
	
	
	
	/**
	 * @param synset
	 * @param relation 
	 * @param depth 
	 * @throws WordNetException 
	 */
	private static void displayNeighbors(Synset synset, WordNetRelation relation, int depth) throws WordNetException {
		Set<Synset> hyp = synset.getRelatedSynsets(relation, depth);
		if (hyp!=null)
		{
			for (Synset synHyp : hyp)
			{
				Set<String> words = synHyp.getWords();
				if (words != null)
				{
					for (String word : words)
					{
						System.out.print(word+", ");

					}
				}
			}
			
		}
		
	}



	public static void displayHypernyms(Synset synset, int depth) throws WordNetException
	{
		Set<Synset> hyp = synset.getRelatedSynsets(WordNetRelation.HYPERNYM, depth);
		if (hyp!=null)
		{
			for (Synset synHyp : hyp)
			{
				Set<String> words = synHyp.getWords();
				if (words != null)
				{
					for (String word : words)
					{
						System.out.print(word+", ");

					}
				}
			}
			
		}
		
		
	}

}
