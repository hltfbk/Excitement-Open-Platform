package eu.excitementproject.eop.transformations.operations.specifications;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;



/**
 * This specification is a special case of {@link SubstituteNodeSpecification},
 * in which the original node or the new node is a multi word expression (i.e.
 * their lemmas).
 * <P>
 * Note that such cases occur only if the parse tree nodes may contain
 * multi-word lemmas. In Minipar there are nodes with multi-word lemmas, but
 * in Easy-First no such node exists.
 * <P>
 * Also note that this is not a multi-NODE substitution: still only a single
 * node is replaced by another single node. The special case here is that
 * at least one of these nodes (the old and the new) represents a multi-word
 * expression. This does not affect, the GenerationOperation (it is a normal
 * substitution), but it does affect the feature vector, since multi-word
 * expressions affect a different feature(s), and value(s) to be added to the
 * feature(s) should be calculated in a specific way. 
 * 
 * @author Asher Stern
 * @since Feb 25, 2011
 *
 */
public class SubstituteNodeSpecificationMultiWord extends SubstituteNodeSpecification
{
	private static final long serialVersionUID = 9092946251924905496L;
	
	public SubstituteNodeSpecificationMultiWord(ExtendedNode textNodeToBeSubstituted, NodeInfo newNodeInfo,
			AdditionalNodeInformation additionalNodeInformation, List<String> textWords, List<String> hypothesisWords)
	{
		super(textNodeToBeSubstituted, newNodeInfo, additionalNodeInformation);
		this.textWords = textWords;
		this.hypothesisWords = hypothesisWords;
	}
	
	
	
	public List<String> getTextWords()
	{
		return textWords;
	}
	public List<String> getHypothesisWords()
	{
		return hypothesisWords;
	}



	private List<String> textWords;
	private List<String> hypothesisWords;

}
