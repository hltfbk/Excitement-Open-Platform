/**
 * 
 */
package ac.biu.nlp.nlp.engineml.generic.rule_compiler;
import ac.biu.nlp.nlp.engineml.generic.truthteller.representation.AnnotationRule;
import ac.biu.nlp.nlp.engineml.operations.rules.Rule;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractConstructionNode;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;

/**
 * An interface for some utility methods that help compile both {@link Rule}s and {@link AnnotationRule}s, out of CGX/XML files. The implementations are supposed to 
 * use concrete classes instead of the generics here.
 * 
 * @author Amnon Lotan
 * @since Jun 1, 2011
 * 
 * @param <I>
 * @param <N>
 * @param <CN>
 */
public interface RuleCompileServices<I extends Info, N extends AbstractNode<I, N>, CN extends AbstractConstructionNode<I, CN>> 
{
	/**
	 * Construct a new {@link AbstractConstructionNode} out of the params in the string label, read from a node in a CGX file 
	 * 
	 * @param label the string holding the parameters from which to construct the new node
	 * @param variableID in case the new node will be a variable, this will be its ID
	 * @return
	 * @throws CompilationException
	 */
	public abstract CN label2Node(String label) throws CompilationException;
	
	/**
	 * Return the string representation of the label on full/regular alignment arrows
	 * @return
	 */
	public String getFullAlignmentTypeString();
	
	/**
	 * Construct a new node with the given info.
	 * 
	 * @param info
	 * @return
	 */
	public N newNode(I info);
}
