package eu.excitementproject.eop.transformations.operations.specifications;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.transformations.datastructures.SingleItemSet;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.NodePrintUtilities;



/**
 * Represents a transformation of substituting a single node.
 * 
 * @author Asher Stern
 * @since Jan 18, 2011
 *
 */
public class SubstituteNodeSpecification extends Specification
{
	private static final long serialVersionUID = 7111068084064620425L;

	public SubstituteNodeSpecification(ExtendedNode textNodeToBeSubstituted, NodeInfo newNodeInfo, AdditionalNodeInformation additionalNodeInformation)
	{
		this(textNodeToBeSubstituted, newNodeInfo, additionalNodeInformation, null);
	}

	public SubstituteNodeSpecification(ExtendedNode textNodeToBeSubstituted, NodeInfo newNodeInfo, AdditionalNodeInformation additionalNodeInformation, String specificationName)
	{
		super();
		this.textNodeToBeSubstituted = textNodeToBeSubstituted;
		this.newNodeInfo = newNodeInfo;
		this.additionalNodeInformation = additionalNodeInformation;
		this.specificationName = specificationName;
	}
	
	public ExtendedNode getTextNodeToBeSubstituted()
	{
		return textNodeToBeSubstituted;
	}
	public NodeInfo getNewNodeInfo()
	{
		return newNodeInfo;
	}
	
	public AdditionalNodeInformation getNewAdditionalNodeInformation()
	{
		return additionalNodeInformation;
	}
	
	@Override
	public Set<ExtendedNode> getInvolvedNodesInTree()
	{
		return new SingleItemSet<ExtendedNode>(getTextNodeToBeSubstituted());
	}

	
	public StringBuffer specString()
	{
		StringBuffer ret = new StringBuffer();
		
		String id = textNodeToBeSubstituted.getInfo().getId();
		String lemma = InfoGetFields.getLemma(textNodeToBeSubstituted.getInfo());
		String pos = InfoGetFields.getPartOfSpeech(textNodeToBeSubstituted.getInfo());
		
		String newLemma = InfoGetFields.getLemma(newNodeInfo);
		String newPos = InfoGetFields.getPartOfSpeech(newNodeInfo);
		
		if (this.specificationName!=null)
		{
			ret.append(this.specificationName).append("-");
		}
		ret.append("SubstituteNodeSpecification: ");
		ret.append("substitute node: ");
		ret.append(id);
		ret.append(": \"");
		ret.append(lemma);
		ret.append("\"(");
		ret.append(pos);
		ret.append(")");
		ret.append(" to: \"");
		ret.append(newLemma);
		ret.append("\"(");
		ret.append(newPos);
		ret.append(")");
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.specifications.Specification#toShortString()
	 */
	@Override
	public String toShortString() {
		StringBuffer ret = new StringBuffer();
		
		String id = textNodeToBeSubstituted.getInfo().getId();
		String lemma = InfoGetFields.getLemma(textNodeToBeSubstituted.getInfo());
		String pos = InfoGetFields.getPartOfSpeech(textNodeToBeSubstituted.getInfo());
		
//		String newLemma = InfoGetFields.getLemma(newNodeInfo);
//		String newPos = InfoGetFields.getPartOfSpeech(newNodeInfo);
		
		if (this.specificationName!=null)
		{
			ret.append(this.specificationName).append(" ");
		}
		else
		{
			ret.append("Substitute ");
		}
		ret.append(NodePrintUtilities.nodeDetailsToString(id, lemma, pos));
//		ret.append(id);
//		ret.append(", \"");
//		ret.append(lemma);
//		ret.append("\", ");
//		ret.append(pos);
//		ret.append('>');
		
		return ret.toString();
	}



	protected final ExtendedNode textNodeToBeSubstituted;
	protected final NodeInfo newNodeInfo;
	protected final AdditionalNodeInformation additionalNodeInformation;
	protected final String specificationName;

	
}
