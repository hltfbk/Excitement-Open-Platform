package eu.excitementproject.eop.core;

import java.io.File;
import java.util.Collections;
import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

public class TestWordNet
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			TestWordNet app = new TestWordNet(args[0]);
			app.go();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	public TestWordNet(String wordNetDir)
	{
		this.wordNetDir = wordNetDir;
	}

	public void go() throws LexicalResourceException, UnsupportedPosTagStringException
	{
		
		LexicalResource<? extends RuleInfo> resource =
				new WordnetLexicalResource(new File(wordNetDir), true,true, Collections.singleton(WordNetRelation.HYPERNYM));
		
		List<? extends LexicalRule<?>> rules =
				resource.getRulesForLeft("boy", new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN));
		
		for (LexicalRule<?> rule : rules)
		{
			System.out.println(rule.getRLemma());
		}
	}

	private String wordNetDir;
}
