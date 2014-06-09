package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * prints the lemma, POS and annotations 
 * @author Asher Stern
 * @since Apr 12, 2011
 *
 */
public class AnnotatedLemmaPosExtendedNodeAndEdgeString implements NodeAndEdgeStringWithDescription<ExtendedInfo>
{
	public void set(AbstractNode<? extends ExtendedInfo, ?> node)
	{
		this.node = node;
	}

	public String getNodeStringRepresentation()
	{
		if (node.getInfo()!=null) {
		}
		boolean hasAntecedent = false;
		if (node.getAntecedent()!=null)
		{
			hasAntecedent=true;
			Info infoAntecedent = node.getAntecedent().getInfo();
			if (infoAntecedent!=null) {
			} else {
			}
		}
		
		String lemma = InfoGetFields.getLemma(node.getInfo());
		String pos = InfoGetFields.getPartOfSpeech(node.getInfo());
		String idOfContentAncestor = "";
		ExtendedInfo contentAncestor = node.getInfo().getAdditionalNodeInformation().getContentAncestor();
		if (contentAncestor!=null)
		{
			try{idOfContentAncestor = contentAncestor.getId();}catch(NullPointerException e){idOfContentAncestor="exception";}
			if (null==idOfContentAncestor)idOfContentAncestor="";
			idOfContentAncestor = "["+idOfContentAncestor+"]";
		}

		String nu 		= ExtendedInfoGetFields.getNegationAndUncertainty(node.getInfo());
		String signature = ExtendedInfoGetFields.getPredicateSignature(node.getInfo());
		String ct 		= ExtendedInfoGetFields.getClauseTruth(node.getInfo());
		String pt 		= ExtendedInfoGetFields.getPredTruth(node.getInfo());
	
		String ret = null;
		if (hasAntecedent)
		{
			ret = lemma+"/"+pos;
		}
		else
		{
			ret = lemma+"/"+pos;
		}
		if (!AdditionalInformationServices.hasNullAnnotation(node.getInfo().getAdditionalNodeInformation()))
			ret += "\n" + signature +"|"+ nu +"|"+ ct +"|"+ pt;
		
		
		return ret;
	}

	public String getEdgeStringRepresentation()
	{
		return InfoGetFields.getRelation(node.getInfo());
	}
	
	public String getDescription()
	{
		return "Second line is signature | negation-uncertainty | clause-truth | predicate-truth";
	}

	
	private AbstractNode<? extends ExtendedInfo, ?> node;
	

}
