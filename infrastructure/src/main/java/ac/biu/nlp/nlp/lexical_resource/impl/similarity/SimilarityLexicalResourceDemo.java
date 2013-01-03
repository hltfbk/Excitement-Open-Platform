/**
 * 
 */
package ac.biu.nlp.nlp.lexical_resource.impl.similarity;

import java.io.File;
import java.util.List;

import ac.biu.nlp.nlp.general.configuration.ConfigurationException;
import ac.biu.nlp.nlp.general.configuration.ConfigurationFile;
import ac.biu.nlp.nlp.general.configuration.ConfigurationFileDuplicateKeyException;
import ac.biu.nlp.nlp.general.configuration.ConfigurationParams;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
import ac.biu.nlp.nlp.lexical_resource.LexicalRule;
import ac.biu.nlp.nlp.lexical_resource.RuleInfo;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

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
		PartOfSpeech pos = new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN);
		List<LexicalRule<? extends RuleInfo>> rules = lexResource.getRulesForRight(lemma, pos);
		
		System.out.println("Found " +rules.size()+" right rules for <"+ lemma+", "+ pos+">");
		for (LexicalRule<? extends RuleInfo> rule : rules)
			System.out.println(rule);

	}

}
