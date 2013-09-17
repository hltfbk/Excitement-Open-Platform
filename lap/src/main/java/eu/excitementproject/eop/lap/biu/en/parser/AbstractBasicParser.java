package eu.excitementproject.eop.lap.biu.en.parser;

import java.util.ArrayList;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;




/**
 * Produces a parse-tree (dependency tree) from a sentence.
 * This interface is a base interface for {@link BasicParser}, and
 * {@link BasicPipelinedParser}.
 * <P>
 * Usage: First call {@link #init()} method, than start parsing
 * any number of sentences required.
 * When the {@linkplain BasicParser} object is no longer to be used, the
 * {@link #cleanUp()} method should be called.
 * <P>
 * To parse sentences: the sentence has to be set. This is done by some
 * setXXX() method in one of the sub-interfaces.
 * Then call {@link #parse()} method, then you can get the parse tree
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
public interface AbstractBasicParser
{
	/**
	 * A constant string, sometimes used to assign node-id to tree-root.
	 */
	public static final String ROOT_NODE_ID = "ROOT";
	
	/**
	 * Call this method once, and only once, before starting
	 * parsing any sentence.
	 * After calling this method, any number of sentences can be parsed.
	 * @throws ParserRunException if initialization failed
	 */
	public void init() throws ParserRunException;
	
	
	/**
	 * Creates the parse tree.
	 * @throws ParserRunException any parser failure (i.e. technical
	 * failure)
	 */
	public void parse() throws ParserRunException;
	
	/**
	 * Get the root of the parse tree.
	 * <P>
	 * Getting parse tree is just getting the root node of that parse tree.
	 * <P>
	 * This method returns a mutable parse tree, i.e. the nodes are
	 * {@linkplain BasicConstructionNode}s. It is used to make manipulations
	 * on the parse tree, before calling {@link #getParseTree()} method.
	 * Later, when you call {@link #getParseTree()}, it will return the
	 * modified parse tree (i.e. not the original parse tree create by
	 * {@link #parse()}, but the modified parse tree.
	 * 
	 * @return The root node of the parse tree, as {@link BasicConstructionNode}
	 * @throws ParserRunException any failure.
	 */
	public BasicConstructionNode getMutableParseTree() throws ParserRunException;
	
	/**
	 * Returns the parse tree. That parse tree cannot be modified later,
	 * since {@link BasicNode} class is immutable.
	 * <P>
	 * Getting parse tree is just getting the root node of that parse tree.
	 * 
	 * @return The root node of the parse tree.
	 * @throws ParserRunException any failure.
	 */
	public BasicNode getParseTree() throws ParserRunException;
	
	/**
	 * Returns list of {@linkplain BasicConstructionNode}s, which are nodes
	 * that hold the words of the original sentence.
	 * <P>
	 * Since dependency tree includes also nodes that are not
	 * corresponding to words but to clauses, or other staff, to
	 * retrieve only words' nodes, this method should be called.
	 * <P>
	 * The returned list is ordered list, and the order is by the
	 * order of words in the original sentence.
	 * @return List of nodes of the parse tree, that
	 * hold the sentence's words.
	 * @throws ParserRunException any failure.
	 */
	public ArrayList<BasicConstructionNode> getNodesOrderedByWords() throws ParserRunException;
	
	/**
	 * Returns the all parse tree nodes as list.
	 * @return list of all of the parse tree nodes.
	 * @throws ParserRunException any failure.
	 */
	public ArrayList<BasicConstructionNode> getNodesAsList() throws ParserRunException;
	
	
	/**
	 * Reset the parser.
	 * After calling this method the parser returns back to its state as it was
	 * before the first call to {@link #setSentence(String)}.
	 * <P>
	 * Call this method just to free memory.
	 * When the parser is not to be used anymore - call {@link #cleanUp()}.
	 * 
	 */
	public void reset();

	
	/**
	 * Cleans up any resources that were used by the parser.
	 * <P>
	 * Call this method when the parser is no longer to be used.
	 */
	public void cleanUp();

}
