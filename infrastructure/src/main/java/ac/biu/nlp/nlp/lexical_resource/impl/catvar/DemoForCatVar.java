package ac.biu.nlp.nlp.lexical_resource.impl.catvar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import ac.biu.nlp.nlp.general.Utils;
import ac.biu.nlp.nlp.lexical_resource.EmptyRuleInfo;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
import ac.biu.nlp.nlp.lexical_resource.LexicalRule;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

public class DemoForCatVar
{
	public static void main(String[] args)
	{
		try
		{
			String catvarFileName = args[0];
			DemoForCatVar app = new DemoForCatVar(catvarFileName);
			app.go();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	public DemoForCatVar(String catvarFileName)
	{
		catvarFile = new File(catvarFileName);
	}
	
	public void go() throws LexicalResourceException, IOException, UnsupportedPosTagStringException
	{
		CatvarLexicalResource catvarResource = new CatvarLexicalResource(catvarFile);
		System.out.println("Memory used: "+Utils.stringMemoryUsedInMB());
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = reader.readLine();
		while (line != null)
		{
			if (line.equalsIgnoreCase("exit"))
				break;
			
			String[] wap = line.split("/");
			String word = wap[0];
			String pos = wap[1];
			PartOfSpeech posObj = null;
			if (pos.equalsIgnoreCase("V"))
			{
				posObj = new UnspecifiedPartOfSpeech(CanonicalPosTag.VERB);
			}
			else if (pos.equalsIgnoreCase("N"))
			{
				posObj = new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN);
			}
			else if (pos.equalsIgnoreCase("ADV"))
			{
				posObj = new UnspecifiedPartOfSpeech(CanonicalPosTag.ADVERB);
			}
			else if (pos.equalsIgnoreCase("ADJ"))
			{
				posObj = new UnspecifiedPartOfSpeech(CanonicalPosTag.ADJECTIVE);
			}
			
			List<LexicalRule<? extends EmptyRuleInfo>> rules = catvarResource.getRulesForLeft(word, posObj);
			if (rules.size()==0)
			{
				System.out.println("No rules");
			}
			else
			{
				for (LexicalRule<? extends EmptyRuleInfo> rule : rules)
				{
					System.out.println(rule.getRLemma()+"/"+rule.getRPos().getCanonicalPosTag().name());
				}
			}

			line = reader.readLine();
		}
		
	}

	private File catvarFile;
}
