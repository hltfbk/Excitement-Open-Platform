/**
 * 
 */
package ac.biu.nlp.nlp.lexical_resource.impl.wikipedia;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ac.biu.nlp.nlp.general.Utils;
import ac.biu.nlp.nlp.lexical_resource.LexicalRule;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;


/**
 * Demo for LexResource
 * 
 * @author Amnon Lotan
 * @since 06/05/2011
 * 
 */
public class WikipediaLexicalResourceDemo {


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		System.out.println("Start \n*****************************\n");
	

		String lLemma = "wednesday";
		PartOfSpeech pos2 = new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN);
		String rLemma = "Microsoft Windows";
		System.out.println("Looking for all rules from \"" + lLemma + "\" to \"" + rLemma + "\"");
		
		// test Wikipedia
		System.out.println("\nFrom the new WikiLexicalResource:");

		Set<WikiExtractionType> extractionTypes = Utils.arrayToCollection(new WikiExtractionType[]{WikiExtractionType.REDIRECT,WikiExtractionType.BE_COMP,
				WikiExtractionType.BE_COMP_IDIRECT,WikiExtractionType.ALL_NOUNS_TOP}, new HashSet<WikiExtractionType>());
		File stopWordsFile = new File("//qa-srv/Data/RESOURCES/Stop Word lists/stopwords-Eyal.txt");
		WikiLexicalResource wikiLexR = new WikiLexicalResource(stopWordsFile, extractionTypes, "jdbc:mysql://qa-srv:3308/wikikb?user=db_readonly", null, null, 0.01);
		
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
