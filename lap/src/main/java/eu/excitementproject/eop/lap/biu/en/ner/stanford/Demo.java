package eu.excitementproject.eop.lap.biu.en.ner.stanford;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityWord;



/**
 * Demo of {@link NamedEntityRecognizer} using {@link StanfordNamedEntityRecognizer}.
 * @author Asher Stern
 *
 */
public class Demo
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//String pathToNER = "E:\\asher\\data\\ner\\stanford\\old_stanford-ner-2009-01-16\\stanford-ner-2009-01-16\\classifiers\\ner-eng-ie.crf-3-all2008-distsim.ser.gz";
		String pathToNER = System.getenv("JARS")+"/stanford-ner-2009-01-16/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz";
		try
		{
			if (args.length==0) 
				throw new Exception("First argument must be the name of the file to analyze");
			StanfordNamedEntityRecognizer ner = new StanfordNamedEntityRecognizer( new File(pathToNER));
			ner.init();
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			try
			{
				String s = br.readLine();
				for (;s!=null;s=br.readLine())
				{
					LinkedList<String> sl = new LinkedList<String>();
					for (String word : s.split(" "))
					{
						sl.add(word);
					}
					ner.setSentence(sl);
					ner.recognize();
					List<NamedEntityWord> result = ner.getAnnotatedSentence();

					for (NamedEntityWord neWord : result)
					{
						System.out.println(neWord.getWord()+" ["+neWord.getNamedEntity()+"]");
					}
				}
				ner.cleanUp();
			}
			finally
			{
				br.close();
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}



	}

}
