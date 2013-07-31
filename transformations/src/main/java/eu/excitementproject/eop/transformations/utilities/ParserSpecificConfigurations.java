package eu.excitementproject.eop.transformations.utilities;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
//import eu.excitementproject.eop.transformations.rteflow.systems.SystemInitialization;

/**
 * 
 * Methods to maintain a static global state of parser-mode, which indicates which
 * parser is used in the system, globally.
 * <P>
 * At the very beginning of every flow in the system, the parser mode should be stated. This
 * is done by calling (as early as possible, and only once) to the static method {@link #changeParser(PARSER)}.
 * <BR>
 * This is done by {@link SystemInitialization#init()} method.
 * <P>
 * The reason I don't like this solution is that misusing this class, as well as forgetting
 * to use it at all, is an error that cannot be caught in compilation time, and in some
 * circumstances not even in run-time.
 * Actually it is also conceptually bad, since it breaks the program structure. It is not
 * structurally part of the system flow (this class is just some static methods that "fly" in
 * the system, not structurally connected to anything), and not object oriented.
 * However, currently I have to use this ad-hoc bad solution.
 * 
 * @author Asher Stern
 * @since Aug 8, 2011
 *
 */
@ParserSpecific({"minipar","easyfirst"})
public class ParserSpecificConfigurations
{
	
	// TODO (comment by Asher Stern):
	/// This enum, the flag and the static function changeParser
	// are far away from good software engineering standards.
	// They are here as an ad-hoc solution, but should be replaced
	// by a better mechanism.
	public static enum PARSER{MINIPAR,EASYFIRST}
	
	private static PARSER MODE_PARSER=PARSER.EASYFIRST;
	private static boolean DO_NOT_APPLY_LEXICALLY_LEXICAL_MULTI_WORDS = (PARSER.EASYFIRST==MODE_PARSER?Constants.DO_NOT_APPLY_LEXICALLY_LEXICAL_MULTI_WORD_WHEN_EASYFIRST:false);
	
	public static void changeParser(PARSER parser) throws TeEngineMlException
	{
		if (null==parser)throw new TeEngineMlException("Null parser mode");
		MODE_PARSER = parser;
		
		
		DO_NOT_APPLY_LEXICALLY_LEXICAL_MULTI_WORDS = (PARSER.EASYFIRST==MODE_PARSER?Constants.DO_NOT_APPLY_LEXICALLY_LEXICAL_MULTI_WORD_WHEN_EASYFIRST:false);
	}
	
	@Deprecated
	public static PARSER getParserMode()
	{
		return MODE_PARSER;
	}

	public static boolean doNotApplyLexicallyLexicalMultiWordRules()
	{
		return DO_NOT_APPLY_LEXICALLY_LEXICAL_MULTI_WORDS;
	}
}
