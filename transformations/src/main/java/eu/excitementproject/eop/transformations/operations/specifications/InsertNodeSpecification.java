package eu.excitementproject.eop.transformations.operations.specifications;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.lap.biu.en.parser.minipar.AbstractMiniparParser;
import eu.excitementproject.eop.transformations.datastructures.SingleItemSet;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.NodePrintUtilities;


/**
 * Represents an "on the fly" transformation of insertion.
 * 
 * @author Asher Stern
 * 
 *
 */
public class InsertNodeSpecification extends Specification
{
	private static final long serialVersionUID = 6058962056140138520L;
	
	public InsertNodeSpecification(ExtendedNode hypothesisNodeToInsert,
			ExtendedNode textNodeToBeParent)
	{
		super();
		this.hypothesisNodeToInsert = hypothesisNodeToInsert;
		this.textNodeToBeParent = textNodeToBeParent;
	}
	
	
	public ExtendedNode getHypothesisNodeToInsert()
	{
		return hypothesisNodeToInsert;
	}
	public ExtendedNode getTextNodeToBeParent()
	{
		return textNodeToBeParent;
	}
	
	@Override
	public Set<ExtendedNode> getInvolvedNodesInTree()
	{
		return new SingleItemSet<ExtendedNode>(getTextNodeToBeParent());
	}
	
	public void setExistInPair(boolean existInPair)
	{
		this.existInPair = existInPair;
		// this.addDescription("Exists in text pair");
	}


	/**
	 * E.g. Insert hypo node <4, "not", RB, neg> under <27, "support", VERB>
	 * @see eu.excitementproject.eop.transformations.operations.specifications.Specification#toString()
	 */
	public StringBuffer specString()
	{
		StringBuffer sb = new StringBuffer();
		if (this.existInPair)
		{
			sb.append("Existing-Word-Insert ");			
		}
		else
		{
			sb.append("Insert ");	
		}
		sb.append(NodePrintUtilities.nodeDetailsToString(
				hypothesisNodeToInsert.getInfo().getId(), 
				InfoGetFields.getLemma(hypothesisNodeToInsert.getInfo()), 
				InfoGetFields.getPartOfSpeech(hypothesisNodeToInsert.getInfo()),
				InfoGetFields.getRelation(hypothesisNodeToInsert.getInfo())));
		sb.append(" under ");
		if (AbstractMiniparParser.ROOT_NODE_ID.equals(textNodeToBeParent.getInfo().getId()))
			sb.append("the root");
		else
		{
			sb.append(NodePrintUtilities.nodeDetailsToString(
					textNodeToBeParent.getInfo().getId(), 
					InfoGetFields.getLemma(textNodeToBeParent.getInfo()), 
					InfoGetFields.getPartOfSpeech(textNodeToBeParent.getInfo())));
		}
		return sb;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.specifications.Specification#toShortString()
	 */
	@Override
	public String toShortString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Insert ");
		sb.append(NodePrintUtilities.nodeDetailsToString(
				hypothesisNodeToInsert.getInfo().getId(), 
				InfoGetFields.getLemma(hypothesisNodeToInsert.getInfo()), 
				InfoGetFields.getPartOfSpeech(hypothesisNodeToInsert.getInfo()),
				InfoGetFields.getRelation(hypothesisNodeToInsert.getInfo())));
		return sb.toString();
	}

	private final ExtendedNode hypothesisNodeToInsert;
	private final ExtendedNode textNodeToBeParent;

	private boolean existInPair = false;
}
