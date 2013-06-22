package eu.excitementproject.eop.lap.biu.en.ner.stanford;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.excitementproject.eop.lap.biu.en.tokenizer.MaxentTokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityWord;

public class Demo3
{

	/**
	 * @param args First argument is NER model file.
	 */
	public static void main(String[] args)
	{
		try
		{
			new Demo3(args).go();
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}
	}
	
	public Demo3(String[] args)
	{
		super();
		this.args = args;
	}



	public void go() throws NamedEntityRecognizerException, TokenizerException, IOException
	{
		Tokenizer tokenizer = new MaxentTokenizer();
		tokenizer.init();
		NamedEntityRecognizer ner = new StanfordNamedEntityRecognizer(new File(args[0]));
		ner.init();
		
		System.out.println("Write sentences, and get them back processed.");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = reader.readLine().trim();
		while (!line.equals("exit"))
		{
			ner.setSentence(line, tokenizer);
			ner.recognize();
			for (NamedEntityWord neWord : ner.getAnnotatedSentence())
			{
				System.out.print(neWord.getWord()+"/");
				if (neWord.getNamedEntity()!=null)
				{
					System.out.print(neWord.getNamedEntity().name());
				}
				else
				{
					System.out.print("-");
				}
				System.out.print(" ");
			}
			System.out.println();
			
			line = reader.readLine().trim();
		}
		
		
	}

	private final String[] args;
}
