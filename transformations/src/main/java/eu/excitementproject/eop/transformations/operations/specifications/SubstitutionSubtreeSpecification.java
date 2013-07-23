package eu.excitementproject.eop.transformations.operations.specifications;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.NodePrintUtilities;



/**
 * This specification specifies an operation of substituting one sub-tree
 * by another sub-tree (usually due to a coreference substitution).
 * 
 * @author Asher Stern
 * @since Jan 24, 2011
 *
 */
public class SubstitutionSubtreeSpecification extends Specification
{
	private static final long serialVersionUID = -597805493074428935L;
	
	public SubstitutionSubtreeSpecification(String source, ExtendedNode subtreeToRemove, ExtendedNode subtreeToAdd, Set<ExtendedNode> subtreesToOmit)
	{
		super();
		this.source = (source!=null)?StringUtil.capitalizeFirstLetter(source):null;
		this.subtreeToRemove = subtreeToRemove;
		this.subtreeToAdd = subtreeToAdd;
		this.subtreesToOmit = subtreesToOmit;
	}

	
	public SubstitutionSubtreeSpecification(ExtendedNode subtreeToRemove, ExtendedNode subtreeToAdd)
	{
		this(null,subtreeToRemove,subtreeToAdd,null);
	}
	
	public SubstitutionSubtreeSpecification(String source, ExtendedNode subtreeToRemove, ExtendedNode subtreeToAdd)
	{
		this(source, subtreeToRemove,subtreeToAdd, null);
	}
	

	

	public ExtendedNode getSubtreeToRemove()
	{
		return subtreeToRemove;
	}
	public ExtendedNode getSubtreeToAdd()
	{
		return subtreeToAdd;
	}
	public Set<ExtendedNode> getSubtreesToOmit()
	{
		return subtreesToOmit;
	}


	@Override
	public Set<ExtendedNode> getInvolvedNodesInTree()
	{
		Set<ExtendedNode> ret = new LinkedHashSet<ExtendedNode>();
		ret.add(getSubtreeToAdd());
		ret.add(getSubtreeToRemove());
		return ret;
	}

	
	@Override
	public StringBuffer specString()
	{
		StringBuffer sb = new StringBuffer();
		if (source!=null)
		{
			sb.append(source);
			sb.append(": replace");
		}
		else
			sb.append("Replace");
		sb.append(" subtree of ");
		sb.append(NodePrintUtilities.nodeDetailsToString(
				subtreeToRemove.getInfo().getId(), 
				InfoGetFields.getLemma(subtreeToRemove.getInfo()), 
				InfoGetFields.getPartOfSpeech(subtreeToRemove.getInfo())));
		sb.append(" with subtree of ");
		sb.append(NodePrintUtilities.nodeDetailsToString(
				subtreeToAdd.getInfo().getId(), 
				InfoGetFields.getLemma(subtreeToAdd.getInfo()), 
				InfoGetFields.getPartOfSpeech(subtreeToAdd.getInfo())));
		return sb;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.specifications.Specification#toShortString()
	 */
	@Override
	public String toShortString() {
		StringBuffer sb = new StringBuffer();
		if (source!=null)
		{
			sb.append(source).append(": ");
			///sb.append(": replace");
		}
		else
		{
			sb.append("Replace subtree: ");
		}
		
		
		sb.append("\"").append(
				InfoGetFields.getLemma(subtreeToRemove.getInfo())).append(
						"\" -> \"").append(
								InfoGetFields.getLemma(subtreeToAdd.getInfo())).append("\"");
		
		return sb.toString();
	}


	private final ExtendedNode subtreeToRemove;
	private final ExtendedNode subtreeToAdd;
	private final Set<ExtendedNode> subtreesToOmit;
	private String source;

}
