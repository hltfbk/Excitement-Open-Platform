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
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
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
		System.out.println("Type exit to exit.");
		System.out.println("Enter word/pos. Enter * at the beginning of the line, if you want two sides.");
		String line = reader.readLine();
		while (line != null)
		{
			if (line.equalsIgnoreCase("exit"))
				break;
			
			if (line.startsWith("*"))
			{
				System.out.println("Enter right hand side (without *).");
				String line2 = reader.readLine();
				processTwoLines(line.substring(1),line2,catvarResource);
			}
			else
			{
				String[] wap = line.split("/");
				String word = wap[0];
				String pos = wap[1];

				PartOfSpeech posObj = processPos(pos);

				List<LexicalRule<? extends EmptyRuleInfo>> rules = catvarResource.getRulesForLeft(word, posObj);
				displayRules(rules);
			}
			
			System.out.println();
			System.out.println("Enter word/pos. Enter * at the beginning of the line, if you want two sides.");
			line = reader.readLine();
		}
		
	}
	
	private PartOfSpeech processPos(String pos) throws UnsupportedPosTagStringException
	{
		PartOfSpeech posObj = null;
		if (pos.equalsIgnoreCase("V"))
		{
			posObj = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.VERB);
		}
		else if (pos.equalsIgnoreCase("N"))
		{
			posObj = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
		}
		else if (pos.equalsIgnoreCase("ADV"))
		{
			posObj = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.ADVERB);
		}
		else if (pos.equalsIgnoreCase("ADJ"))
		{
			posObj = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.ADJECTIVE);
		}
		else if (pos.equalsIgnoreCase("null"))
		{
			posObj=null;
		}
		else
		{
			System.out.println("Could not recognize part-of-speech. Set to null.");
			posObj=null;
		}
		return posObj;
	}
	
	private void displayRules(List<LexicalRule<? extends EmptyRuleInfo>> rules)
	{
		if (rules.size()==0)
		{
			System.out.println("No rules");
		}
		else
		{
			for (LexicalRule<? extends EmptyRuleInfo> rule : rules)
			{
				System.out.println(
						rule.getLLemma()+"/"+simplerPos(rule.getLPos().getCanonicalPosTag()).name()+
						" ==> "+
						rule.getRLemma()+"/"+simplerPos(rule.getRPos().getCanonicalPosTag()).name());
			}
		}

	}
	
	private void processTwoLines(String line, String line2, CatvarLexicalResource catvarResource) throws UnsupportedPosTagStringException, LexicalResourceException
	{
		String[] wap = line.split("/");
		String word = wap[0];
		String pos = wap[1];
		PartOfSpeech posObj = processPos(pos);

		String[] wap2 = line2.split("/");
		String word2 = wap2[0];
		String pos2 = wap2[1];
		PartOfSpeech posObj2 = processPos(pos2);
		
		List<LexicalRule<? extends EmptyRuleInfo>> rules = catvarResource.getRules(word, posObj, word2, posObj2);
		displayRules(rules);
	}

	private File catvarFile;
}

