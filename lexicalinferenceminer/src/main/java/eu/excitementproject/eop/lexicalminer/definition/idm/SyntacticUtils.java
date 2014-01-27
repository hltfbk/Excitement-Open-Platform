package eu.excitementproject.eop.lexicalminer.definition.idm;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.InitException;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;


public interface SyntacticUtils {

	/**
	 * Return a string represent the pattern of that rule
	 * In this Implementation is just returns a the path to that word, 
	 * with the POS, lemma, and relation of all the path is in it's way.
	 * It also gets other parameters that can be useful for other pattern implemention of the function
	 * @param sentTree - the parse tree of the sentence
	 * @param path - the path from the head of the sentence to the current word
	 * @param wordId	-Id (in the sentence) of the current word
	 * @param isNP	- Is it a NP rule or regular rule
	 * @return
	 */
	Pattern getPatternStrings(BasicNode sentTree,
			List<BasicNode> path, int wordId, boolean isNP, String leftLemma,
			String rightLemma);

	/**
	 * The function is used to return a NP lemma for that sub-tree (if exist)
	 * @param full_tree
	 * @param current_tree
	 * @return
	 * @throws InitException 
	 */
	String getNPRuleForNoun(BasicNode full_tree,
			BasicNode current_tree) throws InitException;
	
	/**
	 * The function returns a instance of the parser that fit
	 * 
	 * @return
	 * @throws ParserRunException
	 * @throws InstrumentCombinationException
	 */
	BasicParser getParserInstance() throws ParserRunException;

}