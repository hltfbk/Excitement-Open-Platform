package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;

import java.io.File;
import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * @author Amnon Lotan
 *
 * @since 23 Jan 2012
 */
public class SimilarityLexicalResourceDemo {

	/**
	 * @param args
	 * @throws ConfigurationException 
	 * @throws ConfigurationFileDuplicateKeyException 
	 * @throws LexicalResourceException 
	 * @throws UnsupportedPosTagStringException 
	 */
	public static void main(String[] args) throws ConfigurationFileDuplicateKeyException, ConfigurationException, LexicalResourceException, UnsupportedPosTagStringException {
		ConfigurationFile confFile = new ConfigurationFile(new File("B:/Apps/BIUTEE/workdir/biutee_train.xml"));
//		ConfigurationParams params = confFile.getModuleConfiguration("LinDependencySimilarity");
//		AbstractSimilarityLexicalResource lexResource = new LinDistsimLexicalResource(params);
//		ConfigurationParams params = confFile.getModuleConfiguration("bap");
//		AbstractSimilarityLexicalResource lexResource = new Direct1000LexicalResource(params);
		ConfigurationParams params = confFile.getModuleConfiguration("LinDependencySimilarity");
		AbstractSimilarityLexicalResource lexResource = new LinProximityOriginalLexicalResource(params);
		
		String lemma = "left";
		PartOfSpeech pos = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
		List<LexicalRule<? extends RuleInfo>> rules = lexResource.getRulesForRight(lemma, pos);
		
		System.out.println("Found " +rules.size()+" right rules for <"+ lemma+", "+ pos+">");
		for (LexicalRule<? extends RuleInfo> rule : rules)
			System.out.println(rule);

	}

}

