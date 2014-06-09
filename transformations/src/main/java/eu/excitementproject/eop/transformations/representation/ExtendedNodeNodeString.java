package eu.excitementproject.eop.transformations.representation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator;

/**
 * Used in {@link TreeStringGenerator} for {@link ExtendedNode}.
 * 
 * @author Asher Stern
 * 
 *
 */
public class ExtendedNodeNodeString implements NodeString<ExtendedInfo>
{
	public void set(AbstractNode<? extends ExtendedInfo, ?> node)
	{
		this.node = node;
	}

	public String getStringRepresentation()
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
		String relation = InfoGetFields.getRelation(node.getInfo());
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
			ret = "["+id+"](^"+antecedentId+")"+lemma+"/"+pos+"/"+relation+"/"+corefGroupId+idOfContentAncestor;
		}
		else
		{
			ret = "["+id+"]"+lemma+"/"+pos+"/"+relation+"/"+corefGroupId+idOfContentAncestor;
		}
		return ret;
		
		
	}
	

	private AbstractNode<? extends ExtendedInfo, ?> node;
}
