package eu.excitementproject.eop.distsim.dependencypath;

import eu.excitementproject.eop.lap.biu.en.parser.*;

import junit.framework.Assert;
import eu.excitementproject.eop.distsim.dependencypath.AbstractNodeDependencyPathsUtils.Direction;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;

//AS General - you write that this class is for testing. If it is indeed solely for
// testing, then it should not be integrated into infrastructure.
/**
 * Utilities that help in testing parsers and parsing-related classes.
 *
 * @author Erel Segal Halevi
 * @since 2012-09-03
 */
public class ParserTestUtils {

	/**
	 * Test the given parser on the given input sentence.
	 */
	public static void parserTest(BasicParser theParser, String theSentence, String theExpectedRegeneratedSentence) throws ParserRunException {
		theParser.setSentence(theSentence);
		theParser.parse();
		String theActualRegeneratedSentence = AbstractNodeStringUtils.toEnglishSentence(theParser.getParseTree());
		String theRegeneratedDependencyPath= AbstractNodeDependencyPathsUtils.toDependencyPath(theParser.getParseTree(), 2, Direction.LEFT_TO_RIGHT, true, true, true);
		System.out.println(
				"\nOriginal sentence: "+theSentence+
				"\nParse tree:\n"+AbstractNodeStringUtils.toIndentedString(theParser.getParseTree())+
				"Nodes ordered by words: "+theParser.getNodesOrderedByWords()+
				"\nRe-generated sentence: "+theActualRegeneratedSentence+
				"\nRe-generated dependency path: "+theRegeneratedDependencyPath);
		Assert.assertEquals(theExpectedRegeneratedSentence, theActualRegeneratedSentence);
	}
	
	//AS A lot of hard coded strings. Did you want this be integrated into infrastructure?
	// I guess you didn't, and this class is just a small private unit test for your
	// own needs. Pleae correct me if I'm wrong.
	/**
	 * @return a new initialized instance of a parser, to be used for testing the dependency paths classes.
	 * Currently returns an EasyFirst parser on port 8081, 
	 * with Stanford POS tagger.  
	 * @throws ParserRunException if the parser cannot be initialized.
	 */
	public static BasicParser defaultParserForTesting() throws ParserRunException {
			final String DEFAULT_PARSER_HOST = "te-srv1";
			final int DEFAULT_PARSER_PORT = 8081;
			final String POS_TAGGER_MODEL = System.getenv("JARS")+"/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger";
			BasicParser parser = new EasyFirstParser(DEFAULT_PARSER_HOST, DEFAULT_PARSER_PORT, POS_TAGGER_MODEL);
			String TEST_SENTENCE = "And there was light"; // just to test that the parser is active.
			try {
				parser.init();
				parser.setSentence(TEST_SENTENCE);
				parser.parse();  // test that the parser is active
			} catch (ParserRunException ex) {
				throw new ParserRunException(
						"Cannot get a working EasyFirst parser! Please check that: \n"+
						" A. EasyFirst server is listening on host "+DEFAULT_PARSER_HOST+", port "+DEFAULT_PARSER_PORT+". If it is not, run:\n"+
						"\tpython "+System.getenv("JARS")+"/easyfirst/biu_distrib_09072011/sdparser_server.py "+DEFAULT_PARSER_PORT+"\n" +
						" B. There is a POS tagger model at "+POS_TAGGER_MODEL);
			}
			return parser;
	}
	
}
