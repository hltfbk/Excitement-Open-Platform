package eu.excitementproject.eop.lap.biu.en.parser;

 
/**
 * Produces a parse-tree (dependency tree) from a sentence.
 * <P>
 * Usage: First call {@link #init()} method, than start parsing
 * any number of sentences required.
 * When the {@linkplain BasicParser} object is no longer to be used, the
 * {@link #cleanUp()} method should be called.
 * <P>
 * To parse sentences: call {@link #setSentence(String)} method,
 * then call {@link #parse()} method, then you can get the parse tree
 * by calling {@link #getParseTree()} method.
 * <P>
 * More sophisticated usage would be making some manipulations on the
 * parse tree <B> after </B> calling the {@link #parse()} method, and
 * <B> before </B> calling the {@link #getParseTree()} method.
 * That usage can be done by calling the methods:
 * <UL>
 * <LI>{@link #getMutableParseTree()} </LI>
 * <LI>{@link #getNodesAsList()} </LI>
 * <LI>{@link #getNodesOrderedByWords()} </LI>
 * </UL>
 * after {@link #parse()} and before {@link #getParseTree()}. 
 * 
 * @author Asher Stern
 */
public interface BasicParser extends AbstractBasicParser
{
	/**
	 * Set the sentence to be parsed later by the {@link #parse()} method.
	 * @param sentence a sentence.
	 */
	public void setSentence(String sentence);


}

