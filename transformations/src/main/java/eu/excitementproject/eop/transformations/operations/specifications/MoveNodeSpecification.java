package eu.excitementproject.eop.transformations.operations.specifications;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.lap.biu.en.parser.minipar.AbstractMiniparParser;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.NodePrintUtilities;



/**
 * Represents "move on the fly" transformation.
 * 
 * @author Asher Stern
 * 
 *
 */
public class MoveNodeSpecification extends Specification
{
	private static final long serialVersionUID = -4248792002705120924L;
	
	public MoveNodeSpecification(ExtendedNode textNodeToMove,
			ExtendedNode textNodeToBeParent, EdgeInfo newEdgeInfo, ExtendedInfo hypothesisNodeMatchedToChild)
	{
		super();
		this.textNodeToMove = textNodeToMove;
		this.textNodeToBeParent = textNodeToBeParent;
		this.newEdgeInfo = newEdgeInfo;
		this.hypothesisNodeMatchedToChild = hypothesisNodeMatchedToChild;
		this.duplicate = false;
		
	}
	
	/**
	 * Duplicate means that the node will be duplicated, and then the new node will be moved
	 * to the new location.
	 * 
	 * @param original
	 * @param duplicate
	 */
	public MoveNodeSpecification(MoveNodeSpecification original, boolean duplicate)
	{
		super();
		this.textNodeToMove = original.getTextNodeToMove();
		this.textNodeToBeParent = original.getTextNodeToBeParent();
		this.newEdgeInfo = original.getNewEdgeInfo();
		this.hypothesisNodeMatchedToChild = original.getHypothesisNodeMatchedToChild();
		this.duplicate = duplicate;
	}

	
	
	public ExtendedNode getTextNodeToMove()
	{
		return textNodeToMove;
	}
	public ExtendedNode getTextNodeToBeParent()
	{
		return textNodeToBeParent;
	}
	public EdgeInfo getNewEdgeInfo()
	{
		return newEdgeInfo;
	}
	public boolean isDuplicate()
	{
		return duplicate;
	}
	public ExtendedInfo getHypothesisNodeMatchedToChild()
	{
		return hypothesisNodeMatchedToChild;
	}

	@Override
	public Set<ExtendedNode> getInvolvedNodesInTree()
	{
		LinkedHashSet<ExtendedNode> ret = new LinkedHashSet<ExtendedNode>();
		ret.add(getTextNodeToBeParent());
		ret.add(getTextNodeToMove());
		return ret;
	}

	public StringBuffer specString()
	{
		StringBuffer sb = new StringBuffer();
		
		if (duplicate)
			sb.append("Duplicate and move node ");
		else
			sb.append("Move node ");
		sb.append(NodePrintUtilities.nodeDetailsToString(
				textNodeToMove.getInfo().getId(), 
				InfoGetFields.getLemma(textNodeToMove.getInfo())));
//		sb.append('<');
//		sb.append(textNodeToMove.getInfo().getId());
//		sb.append(", \"");
//		sb.append(InfoGetFields.getLemma(textNodeToMove.getInfo()));
		sb.append(" to ");
		if (AbstractMiniparParser.ROOT_NODE_ID.equals(textNodeToBeParent.getInfo().getId()))
			sb.append("the root");
		else
		{
			sb.append(NodePrintUtilities.nodeDetailsToString(
					textNodeToBeParent.getInfo().getId(), 
					InfoGetFields.getLemma(textNodeToBeParent.getInfo())));
//			sb.append('<');
//			sb.append(textNodeToBeParent.getInfo().getId());
//			sb.append(", \"");
//			sb.append(InfoGetFields.getLemma(textNodeToBeParent.getInfo()));
//			sb.append("\">");
		}
		sb.append(" with relation '");
		sb.append(InfoGetFields.getRelation(newEdgeInfo));
		sb.append('\'');
		return sb;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.specifications.Specification#toShortString()
	 */
	@Override
	public String toShortString() {
		StringBuffer sb = new StringBuffer();
		
		if (duplicate)
			sb.append("Duplicate and move ");
		else
			sb.append("Move ");
		sb.append(NodePrintUtilities.nodeDetailsToString(
				textNodeToMove.getInfo().getId(), 
				InfoGetFields.getLemma(textNodeToMove.getInfo())));
//		sb.append('<');
//		sb.append(textNodeToMove.getInfo().getId());
//		sb.append(", \"");
//		sb.append(InfoGetFields.getLemma(textNodeToMove.getInfo()));
//		sb.append("\">");
		return sb.toString();
	}



	private final ExtendedNode textNodeToMove;
	private final ExtendedNode textNodeToBeParent;
	private final EdgeInfo newEdgeInfo;
	private final ExtendedInfo hypothesisNodeMatchedToChild; // "to child" means to the textNodeToMove
	private final boolean duplicate;



}
