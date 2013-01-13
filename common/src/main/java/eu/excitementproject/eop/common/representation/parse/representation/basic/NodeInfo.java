package eu.excitementproject.eop.common.representation.parse.representation.basic;

import java.io.Serializable;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;



/**
 * Represents the information of the contents of a {@linkplain BasicNode}.
 * <P>
 * The information in this object does not contains information about the edge that connects
 * the node to its parent. That kind of information is given by {@linkplain EdgeInfo}
 * <P>
 * <B>All implementations must be immutable!!!</B>
 * 
 * It is recommended to use the default implementation: {@link DefaultNodeInfo}
 * 
 * @see DefaultNodeInfo
 * @see Info
 * 
 * @author Asher Stern
 *
 */
public interface NodeInfo extends Serializable
{
	
	/**
	 * 
	 * @return the word. or null if this node does not represent a word.
	 */
	public String getWord();
	
	/**
	 * Returns the lemma (root form) of the node.
	 * For example, "was" has lemma "be". "went" has lemma "go".
	 * Note, that whether that information exists depends on the parser.
	 * <code>null</code> is legal here.
	 * <P>
	 * My advice, if the parser does not returns this information,
	 * Use another tool (hopefully you have another tool for that)
	 * on the {@linkplain BasicConstructionNode}s returned by
	 * the {@linkplain BasicParser} to add this information.
	 * @return the lemma (root form) of the node
	 */
	public String getWordLemma();
	
	/**
	 * 
	 * @return the serial (location in the sentence) of the word
	 * represented by this node.
	 * The returned value is <B>undefined</B> if the node does not represent a word.
	 * (You can know whether the node represents a node by testing
	 * whether {@link #getWord()} returns null).
	 */
	public int getSerial();
	
	public SyntacticInfo getSyntacticInfo();
	
	/**
	 * Returns the Named-Entity annotation of the node.
	 * I'm sorry for the method name. It should have been
	 * <code>getNamedEntityAnnotation()</code>.
	 * @return
	 */
	public NamedEntity getNamedEntityAnnotation();
	
	public boolean isVariable();
	public Integer getVariableId();
	
	public boolean isEqualTo(NodeInfo other);
	
	public int hashCode();
	public boolean equals(Object obj);

}
