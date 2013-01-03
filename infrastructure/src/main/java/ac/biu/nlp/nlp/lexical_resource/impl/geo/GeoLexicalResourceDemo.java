/**
 * 
 */
package ac.biu.nlp.nlp.lexical_resource.impl.geo;

import java.util.List;

import ac.biu.nlp.nlp.lexical_resource.EmptyRuleInfo;
import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
import ac.biu.nlp.nlp.lexical_resource.LexicalRule;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * @author Amnon Lotan
 *
 * @since 16 Jan 2012
 */
public class GeoLexicalResourceDemo {

	/**
	 * @param args
	 * @throws LexicalResourceException 
	 * @throws UnsupportedPosTagStringException 
	 */
	public static void main(String[] args) throws LexicalResourceException, UnsupportedPosTagStringException {

		System.out.println("Start \n*****************************\n");

		String lLemma = "San Jose";
		PartOfSpeech pos2 = new UnspecifiedPartOfSpeech(CanonicalPosTag.NOUN);
		String rLemma = "United Kingdom";
		System.out.println("Looking for all rules from \"" + lLemma + "\" to \"" + rLemma + "\"");
		
		GeoLexicalResource GEOLexR = new  GeoLexicalResource("jdbc:mysql://qa-srv:3308/geo?user=db_readonly","tipster"); 

		List<LexicalRule<? extends EmptyRuleInfo>> rules2 = GEOLexR.getRulesForLeft(lLemma, pos2 );
		
		System.out.println("Got "+rules2.size() + " for: " + lLemma + ", " + pos2 );
		for (LexicalRule<? extends EmptyRuleInfo> rule : rules2)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + rules2.size() + " relations");
		System.out.println("\n*****************************\n");
				
		List<LexicalRule<? extends EmptyRuleInfo>> otherRules = GEOLexR.getRules(lLemma, null, rLemma, null);
		System.out.println("Got "+otherRules.size() + " for: " + lLemma + ", " + pos2 + ", "  + rLemma + ", "  + pos2 );
		for (LexicalRule<? extends EmptyRuleInfo> rule : otherRules)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + otherRules.size() + " relations");
				
		
		System.out.println("\n\n************************  Old resource	**********************");
		
		
		
	}

}
