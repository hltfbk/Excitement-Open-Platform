package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * 
 * @author Asher Stern
 * @since Apr 12, 2011
 *
 */
public class AnnotatedExtendedNodeAndEdgeString implements NodeAndEdgeStringWithDescription<ExtendedInfo>
{
	public void set(AbstractNode<? extends ExtendedInfo, ?> node)
	{
		this.node = node;
	}

	public String getNodeStringRepresentation()
	{
		String id = "";
		if (node.getInfo()!=null)
			id = node.getInfo().getId();
		boolean hasAntecedent = false;
		String antecedentId = null;
		if (node.getAntecedent()!=null)
		{
			hasAntecedent=true;
			Info infoAntecedent = node.getAntecedent().getInfo();
			if (infoAntecedent!=null)
				antecedentId = node.getAntecedent().getInfo().getId();
			else
				antecedentId = "?";
		}
		
		String lemma = InfoGetFields.getLemma(node.getInfo());
		String pos = InfoGetFields.getPartOfSpeech(node.getInfo());
		String corefGroupId = ExtendedInfoGetFields.getCorefGroupId(node.getInfo());
		
		String idOfContentAncestor = "";
		ExtendedInfo contentAncestor = node.getInfo().getAdditionalNodeInformation().getContentAncestor();
		if (contentAncestor!=null)
		{
			try{idOfContentAncestor = contentAncestor.getId();}catch(NullPointerException e){idOfContentAncestor="exception";}
			if (null==idOfContentAncestor)idOfContentAncestor="";
			idOfContentAncestor = "["+idOfContentAncestor+"]";
		}

	
		String ret = null;
		if (hasAntecedent)
		{
			ret = "["+id+"](^"+antecedentId+") "+lemma+"/"+pos+"/"+corefGroupId+idOfContentAncestor;
		}
		else
		{
			ret = "["+id+"] "+lemma+"/"+pos+"/"+corefGroupId+idOfContentAncestor;
		}
		if (!AdditionalInformationServices.hasNullAnnotation(node.getInfo().getAdditionalNodeInformation()))
		{
			String nu 		= ExtendedInfoGetFields.getNegationAndUncertainty(node.getInfo());
			String predType = ExtendedInfoGetFields.getPredicateSignature(node.getInfo());
			String ct 		= ExtendedInfoGetFields.getClauseTruth(node.getInfo());
			String pt 		= ExtendedInfoGetFields.getPredTruth(node.getInfo());
			ret += "\n"+ predType + "|" + nu  +"|"+ ct +"|"+ pt;
		}
		
		
		return ret;
	}

	public String getEdgeStringRepresentation()
	{
		return InfoGetFields.getRelation(node.getInfo());
	}
	
	public String getDescription()
	{
		return "Second line is signature/negation/clause-truth/predicate-truth";
	}

	
	private AbstractNode<? extends ExtendedInfo, ?> node;
}
