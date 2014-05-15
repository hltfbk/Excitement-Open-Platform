package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;

/**
 * 
 * @author Asher Stern
 * @since Apr 12, 2011
 *
 */
public class ExtendedNodeNodeAndEdgeString implements NodeAndEdgeStringWithDescription<ExtendedInfo>
{
	//public static final char UNSPECIFIED_PREDICATE_TRUTH_REPRESENTATION = (char)((int)176);
	public static final String UNSPECIFIED_PREDICATE_TRUTH_REPRESENTATION_STRING = "~";
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
		
		String idOfContentAncestor = null;
		ExtendedInfo contentAncestor = node.getInfo().getAdditionalNodeInformation().getContentAncestor();
		if (contentAncestor!=null)
		{
			try{idOfContentAncestor = contentAncestor.getId();}catch(NullPointerException e){idOfContentAncestor="exception";}
			if (null==idOfContentAncestor)idOfContentAncestor="";
			idOfContentAncestor = "["+idOfContentAncestor+"]";
		}

		PredTruth predicateTruth = ExtendedInfoGetFields.getPredTruthObj(node.getInfo());
		String predicateTruthString = null;
		if (predicateTruth!=null)
			predicateTruthString = stringOfPredicateTruth(predicateTruth);
		
		
		String ret = null;
		if (hasAntecedent)
		{
			ret = "["+id+"](^"+antecedentId+") "+(predicateTruthString==null?"":predicateTruthString)+lemma+shortNE()+"/"+pos+"/"+corefGroupId+(idOfContentAncestor==null?"":"(@"+idOfContentAncestor+")");
		}
		else
		{
			ret = "["+id+"] "+(predicateTruthString==null?"":predicateTruthString)+lemma+shortNE()+"/"+pos+"/"+corefGroupId+(idOfContentAncestor==null?"":"(@"+idOfContentAncestor+")");
		}
		return ret;
	}
	
	public String getEdgeStringRepresentation()
	{
		return InfoGetFields.getRelation(node.getInfo());
	}

	static String stringOfPredicateTruth(PredTruth predicateTruth)
	{
		String ret = UNSPECIFIED_PREDICATE_TRUTH_REPRESENTATION_STRING;
		switch(predicateTruth)
		{
		case N:ret="-";
		break;
		case P:ret="+";
		break;
		case U:ret="?";
		break;
		default:
			break;
		}
		return ret;
	}
	
	private String shortNE()
	{
		String ret = "";
		if (node!=null)
		{
			NamedEntity neAnnotation = InfoGetFields.getNamedEntityAnnotation(node.getInfo());
			if (neAnnotation!=null)
			{
				String neName = neAnnotation.name();
				if (neName.length()>0)
				{
					ret = "("+neName.charAt(0)+")";
				}
			}
		}
		return ret;
	}
	
	public String getDescription()
	{
		return "[id](^optional-antededent)predicate-true(optional-NE)/part-of-speech/coreference-group(@optional-id-of-content-ancestor)";
	}
	

	
	private AbstractNode<? extends ExtendedInfo, ?> node;
}
