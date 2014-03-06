package eu.excitementproject.eop.common.component.syntacticknowledge;

import java.util.List;

import eu.excitementproject.eop.common.component.Component;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleMatch;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticResourceException;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * A syntactic resource is a collection of syntactic rules. For syntactic rule collections, 
 * it is not practical to provide simple access interfaces like getLeftForPOS for lexical 
 * knowledge. Due to the exponential number of subtrees in any text and hypothesis, naive 
 * querying based on the input is infeasible. Instead, findMatches() method is defined to 
 * outline common behavior of platform syntactic rules.
 * 
 * <P> Type argument I and S follow that of SyntacticRule (thus, I=Info and S=BasicNode, for BasicNode based syntactic Rule)
 * @author Gil
 */
public interface SyntacticResource<I,S extends AbstractNode<I,S>> extends Component {

	/**
	 * The interface takes in a parse tree (which is represented in common parse tree 
	 * nodes). The rule base must return all possible rules that can be applied into 
	 * the tree, as a list of RuleMatch object. Note that the returned match holds 
	 * information of not only rules (instance of SyntacticRule but also the location 
	 * of the place where the rule should be applied to. The implementation must return 
	 * an empty list (not null), if no applicable rules are found.
	 * @param currentTree a parse tree with BasicNode 
	 * @return a list of SyntacticRule that can be applied to the currentTree  
	 */
	public List<RuleMatch<I,S>> findMatches(S currentTree) throws SyntacticResourceException;
	
	/**
	 * This overloaded version of findMatches method gets two trees instead of one. 
	 * The two trees are text and hypothesis tree, and the method tries to find matches 
	 * such that LHS matches the text tree and RHS matches the hypothesis tree.
	 * <P> The overloaded method is provided for efficiency. Since it might be too expensive 
	 * to find and apply all possible matches, the overloaded method only returns the rules 
	 * that also match the hypothesis. This way, the user of the knowledge base can applies 
	 * rules that directly make the text more similar to the hypothesis. 
	 * The drawback of this approach is that it misses some rules that can make text more 
	 * similar to hypothesis. For example, it will miss cases where subsequently applying two 
	 * or more rules that make text more similar to hypothesis.
	 * @param textTree parse tree of the text 
	 * @param hypothesisTree parse tree of the hypothesis 
	 * @return a list of SyntacticRule that can match between textTree to hypothesisTree
	 */
	public List<RuleMatch<I,S>> findMatches(S textTree, S hypothesisTree) throws SyntacticResourceException;
	
	/**
	 * Performs any necessary cleans ups.
	 * This method should be called only when this resource instance is about to be destroyed.
	 *  
	 * @throws SyntacticResourceCloseException
	 */
	public void close() throws SyntacticResourceCloseException;
	
}
