package eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.InitException;

/**
 * @author Amnon Lotan
 *
 * @since 25 Dec 2011
 */
public class VerbOceanLexicalResourceDemo {

	/**
	 * @param args
	 * @throws UnsupportedPosTagStringException 
	 * @throws LexicalResourceException 
	 * @throws ConfigurationException 
	 * @throws TeEngineMlException 
	 * @throws InitException 
	 */
	public static void main(String[] args) throws UnsupportedPosTagStringException, LexicalResourceException, ConfigurationException, InitException {
		System.out.println("Start \n*****************************\n");

		String lLemma = "abandon";
		PartOfSpeech pos2 = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
		String rLemma = "reconsider";
		System.out.println("Looking for all rules from \"" + lLemma + "\" to \"" + rLemma + "\"");
		
		Set<RelationType> allowedRelationTypes = Utils.arrayToCollection(new RelationType[]{RelationType.STRONGER_THAN, RelationType.HAPPENS_BEFORE
				, RelationType.CAN_RESULT_IN}, new LinkedHashSet<RelationType>());
		VerbOceanLexicalResource verbOceanLexR = new  VerbOceanLexicalResource(1, new File("//qa-srv/Data/RESOURCES/VerbOcean/verbocean.unrefined.2004-05-20.txt"), 
				allowedRelationTypes);
		verbOceanLexR = new VerbOceanLexicalResource(verbOceanLexR);

		System.out.println("the max score is " + verbOceanLexR.maxScore);
		// check for HYPERNYM, and also SYNONYM, DERIVATION
		
		List<LexicalRule<? extends VerbOceanRuleInfo>> rules2 = verbOceanLexR.getRulesForLeft(lLemma, pos2 );
		
		System.out.println("Got "+rules2.size() + " for: " + lLemma + ", " + pos2 );
		for (LexicalRule<? extends VerbOceanRuleInfo> rule : rules2)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + rules2.size() + " relations");
		System.out.println("\n*****************************\n");
				
		List<LexicalRule<? extends VerbOceanRuleInfo>> otherRules = verbOceanLexR.getRules(lLemma, null, rLemma, null);
		System.out.println("Got "+otherRules.size() + " for: " + lLemma + ", " + pos2 + ", "  + rLemma + ", "  + pos2 );
		for (LexicalRule<? extends VerbOceanRuleInfo> rule : otherRules)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + otherRules.size() + " relations");
				
		// uncomment these lines to compare to the old resource - this requires you to add project dependencies.
				// and possibly edit the home directory of your 
				
//		System.out.println("\n\n************************** Old resource	************************\n");
//		String configurationFileName = "b:/Apps/BIUTEE/workdir/biutee_train.xml";
//		ConfigurationFile confFile = new ConfigurationFile(new File(configurationFileName));
//		ConfigurationParams verbOceanModule = confFile.getModuleConfiguration("VerbOcean");
//		
//		new ExperimentLoggerNeutralizer().neutralize();
//		// Use the file log4j.properties to initialize log4j
//		PropertyConfigurator.configure("log4j.properties");
//
//		
//		VerbOceanManager manager = new VerbOceanManager();
//		manager.init(verbOceanModule);
//		Iterable<String> entailedVerbs = manager.getVerbsEntailedByVerb(lLemma);
//		for (String entailedVerb : entailedVerbs)
//			System.out.println("+++ "+lLemma+" --> "+entailedVerb);
		
	}

}

