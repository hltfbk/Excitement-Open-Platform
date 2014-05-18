package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;

/**
 * Just prints the lemma and POS
 * @author Amnon Lotan
 * @since Apr 12, 2011
 *
 */
public class ExtendedLemmaPosNodeAndEdgeString implements NodeAndEdgeStringWithDescription<ExtendedInfo>
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
		String idOfContentAncestor = null;
		ExtendedInfo contentAncestor = node.getInfo().getAdditionalNodeInformation().getContentAncestor();
		if (contentAncestor!=null)
		{
			try{idOfContentAncestor = contentAncestor.getId();}catch(NullPointerException e){idOfContentAncestor="exception";}
			if (null==idOfContentAncestor)idOfContentAncestor="";
			idOfContentAncestor = "["+idOfContentAncestor+"]";
		}

		PredTruth predicateTruth = ExtendedInfoGetFields.getPredTruthObj(node.getInfo());
		if (predicateTruth!=null) {
		}
		String predicateTruthString = null;
		if (predicateTruth!=null)
			predicateTruthString = ExtendedNodeNodeAndEdgeString.stringOfPredicateTruth(predicateTruth);
		
		String ret = null;
		if (hasAntecedent)
		{
			ret = (predicateTruthString==null?"":predicateTruthString)+lemma+"/"+pos;
		}
		else
		{
			ret = (predicateTruthString==null?"":predicateTruthString)+lemma+"/"+pos;
		}
		return ret;
	}
	
	public String getEdgeStringRepresentation()
	{
		return InfoGetFields.getRelation(node.getInfo());
	}
	
	public String getDescription()
	{
		return null;
	}


	private AbstractNode<? extends ExtendedInfo, ?> node;
}
