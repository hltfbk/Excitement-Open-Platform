package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.it;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiExtractionType;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiRuleInfo;


/**
 * Demo for LexResource
 * 
 * @author Amnon Lotan
 * @author Vivi Nastase (FBK)
 * 
 * @since 06/05/2011
 * 
 */
public class WikipediaLexicalResourceDemoIT {


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		System.out.println("Start \n*****************************\n");
	

		String lLemma = "Italia";
		PartOfSpeech pos2 = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
		String rLemma = "Venezia";
		System.out.println("Looking for all rules from \"" + lLemma + "\" to \"" + rLemma + "\"");
		
		// test Wikipedia
		System.out.println("\nFrom the new WikiLexicalResource:");

		Set<WikiExtractionType> extractionTypes = Utils.arrayToCollection(new WikiExtractionType[]{WikiExtractionType.REDIRECT,WikiExtractionType.CATEGORY,
				WikiExtractionType.LEX_ALL_NOUNS,WikiExtractionType.SYNT_ALL_NOUNS}, new HashSet<WikiExtractionType>());
		File stopWordsFile = new File("src/test/resources/stopwords.txt");
//		WikiLexicalResourceIT wikiLexR = new WikiLexicalResourceIT(stopWordsFile, extractionTypes, "jdbc:mysql://nathrezim:3306/wikilexresita","root","nat_2k12", 0.01);
//		WikiLexicalResourceIT wikiLexR = new WikiLexicalResourceIT(stopWordsFile, extractionTypes, "jdbc:mysql://hlt-services4:3306/wikilexresita","root","hlt4my2sql", 0.01);
		WikiLexicalResourceIT wikiLexR = new WikiLexicalResourceIT(stopWordsFile, extractionTypes, "jdbc:mysql://localhost:3306/wikilexresita2","root","my_nor_2k", 0.01);

		
//		ConfigurationFile  confFile = new ConfigurationFile(new File("B:/Apps/BIUTEE/workdir/biutee_train.xml"));
//		WikiLexicalResource wikiLexR = new WikiLexicalResource(confFile.getModuleConfiguration("Wiki"));
		
		
		List<LexicalRule<? extends WikiRuleInfo>> rules2 = wikiLexR.getRulesForLeft(lLemma, pos2 );
		
		System.out.println("Got "+rules2.size() + " for: " + lLemma + ", " + pos2 );
		for (LexicalRule<? extends WikiRuleInfo> rule : rules2)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + rules2.size() );
		System.out.println("\n*****************************\n");
				
		
		
		List<LexicalRule<? extends WikiRuleInfo>> otherRules = wikiLexR.getRules(lLemma, null, rLemma, null);
		System.out.println("Got "+otherRules.size() + " for: " + lLemma + ", " + pos2 + ", "  + rLemma + ", "  + pos2);
		for (LexicalRule<? extends WikiRuleInfo> rule : otherRules)
			System.out.println(rule);
		
		System.out.println(lLemma +" and " + rLemma + " have " + otherRules.size());
				
		// uncomment these lines to compare to the old resource - this requires you to add project dependencies.
				
//		System.out.println("\n************************* Old Wiki **********************\n");
//		
//		String configurationFileName = "b:/Apps/BIUTEE/workdir/biutee_train.xml";
//		ConfigurationFile confFile = new ConfigurationFile(new File(configurationFileName));
//		ConfigurationParams wikiModule = confFile.getModuleConfiguration("Wiki");
//		
//		new ExperimentLoggerNeutralizer().neutralize();
//		// Use the file log4j.properties to initialize log4j
//		PropertyConfigurator.configure("log4j.properties");
////		new LogInitializer(configurationFileName).init();
//		
//		WikipediaLexicalRuleBase oldWiki = new WikipediaLexicalRuleBase(wikiModule);
//		
//		ImmutableSet<ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule> oldRules = oldWiki.getRules(lLemma, pos2);
//		System.out.println("Got "+oldRules.size() + " for: " + lLemma + ", " + pos2 );
//		for (ac.biu.nlp.nlp.engineml.operations.rules.LexicalRule rule : oldRules)
//			System.out.println(rule);
//		
//		System.out.println(lLemma +" has " + oldRules.size() );
//		System.out.println("\n*****************************\n");
				
	}
}
