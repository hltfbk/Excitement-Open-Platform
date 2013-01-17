package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

public class IdLemmaPosRelNodeString implements NodeString<Info>
{

	public String getStringRepresentation()
	{
		String id = "";
		String lemma = InfoGetFields.getLemma(node.getInfo());
		String pos = InfoGetFields.getPartOfSpeech(node.getInfo());
		String rel = InfoGetFields.getRelation(node.getInfo());
		String var = null;

		try
		{
			String id_ = node.getInfo().getId();
			if (id_!=null) id = id_;
		}
		catch(NullPointerException e){}
		try
		{
			if (node.getInfo().getNodeInfo().isVariable())
			{
				// lemma = "*"+node.getInfo().getNodeInfo().getVariableId().toString();
				lemma = "*";
			}
		}
		catch(NullPointerException e){} 
		
		try
		{
			if (node.getInfo().getNodeInfo().isVariable())
			{
				var = node.getInfo().getNodeInfo().getVariableId().toString();
			}
		}
		catch(NullPointerException e){}
		
		String ret = null;
		if (null==var)
			ret = id+":"+lemma+"/["+pos+"]"+"/<"+rel+">";
		else
			ret = id+":"+lemma+"{X_"+var+"}"+"/["+pos+"]"+"/<"+rel+">";
			
		
		return ret;
		
	}

	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}
	
	protected AbstractNode<? extends Info, ?> node;

}
