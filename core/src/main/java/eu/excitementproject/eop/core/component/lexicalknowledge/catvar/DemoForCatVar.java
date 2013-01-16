package eu.excitementproject.eop.core.component.lexicalknowledge.catvar;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import eu.excitementproject.eop.core.component.lexicalknowledge.EmptyRuleInfo;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnspecifiedPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.Utils;

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
				posObj = new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.VERB);
			}
			else if (pos.equalsIgnoreCase("N"))
			{
				posObj = new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.NOUN);
			}
			else if (pos.equalsIgnoreCase("ADV"))
			{
				posObj = new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.ADVERB);
			}
			else if (pos.equalsIgnoreCase("ADJ"))
			{
				posObj = new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.ADJECTIVE);
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
					System.out.println(rule.getRLemma()+"/"+simplerPos(rule.getRPos().getCanonicalPosTag()).name());
				}
			}

			line = reader.readLine();
		}
		
	}

	private File catvarFile;
}

