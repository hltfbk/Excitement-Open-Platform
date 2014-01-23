package eu.excitementproject.eop.lexicalminer.definition.Common;

import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;

/**
 * <p>Several utils related to {@link EnglishSingleTreeParser}.
 * <p>
 * @author Erel Segal		(copied from parse project of Erel Segal)
 * @since 03/01/2012
 */
public class ParserUtils {
	/**
	 * The function checks if the parser works
	 * @param postagger
	 * @param parser
	 * @param TEST_SENTENCE
	 * @throws ParserRunException
	 */
	private static void initAndCheckParser(EnglishSingleTreeParser parser,
			String parserHost, int parserPort
			)
			throws ParserRunException {
		String TEST_SENTENCE = "And there was light"; // just to test that the parser is active.
		try {
			parser.init();
			parser.setSentence(TEST_SENTENCE);
			parser.parse();  // test that the parser is active
		} catch (ParserRunException ex) {
			throw new ParserRunException(
					"Cannot get a working EasyFirst parser! Please check that: \n"+
					" A. EasyFirst server is listening on host "+parserHost+", port "+parserPort+". If it is not, run:\n"+
					"\tpython "+System.getenv("JARS")+"/easyfirst/biu_distrib_09072011/sdparser_server.py "+parserPort+"\n" +
					" B. The tagger that was sent doesn't work correctly.");				
		}
	}
	
	/**
	 * @return a new initialized instance of our standard parser.
	 * Currently returns an EasyFirst parser on port 8081, 
	 * with A POStagger and tokenizer it gets.  
	 * @throws ParserRunException if the parser cannot be initialized.
	 */
	public static EnglishSingleTreeParser getInitializedStandardParser(PosTagger posTagger, Tokenizer tokenizer, String parserHost, int parserPort ) throws ParserRunException {
		EnglishSingleTreeParser parser = new EasyFirstParser(parserHost, parserPort, tokenizer, posTagger);
		initAndCheckParser(parser, parserHost, parserPort);
		return parser;
	}	
}
