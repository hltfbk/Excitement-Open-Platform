package eu.excitementproject.eop.lap.biu.en.ner.stanford;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.lap.biu.ner.NamedEntityWord;


/**
 * @author erelsgl
 * @date 20/01/2011
 */
public class Demo2 {

	
	/**
	 * A demo program for Stanford NER.
	 * @param args a single argument which is the path of a classifier from the Stanford NER package, e.g.
	 * 	 ${env_var:JARS}/stanford-ner-2009-01-16/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length==0) 
			throw new IllegalArgumentException("First argument must be the path to a Stanford NER classifier (with .ser.gz extension)");
		String pathToNER = args[0];
		StanfordNamedEntityRecognizer ner = new StanfordNamedEntityRecognizer( new File(pathToNER));
		ner.init();
		String testString = "The Israeli PM Binyamin Netanyahu said that Israel will accept the Eitan W. Shishinsky recommendations";
		//String testString = "In December 2004 the state sold 18.4% of its equity in Air France-KLM. The state's shareholding in Air France-KLM subsequently fell to just under 20%.";
		LinkedList<String> testStringList = new LinkedList<String>();
		for (String word : testString.split(" "))
			testStringList.add(word);
		ner.setSentence(testStringList);
		ner.recognize();

		System.out.println("\nNEs detected:");
		System.out.println(ner.getAnnotatedEntities());

		System.out.println("\nNE tag for each word: ");
		List<NamedEntityWord> list = ner.getAnnotatedSentence();
		for (NamedEntityWord neWord: list)
			System.out.println(neWord.getWord()+" ["+neWord.getNamedEntity()+"]");
		ner.cleanUp();
	}

}
