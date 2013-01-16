package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

public class LemmaPosNerRelNodeAndEdgeString implements NodeAndEdgeString<Info>
{
	public String getEdgeStringRepresentation()
	{
		String ret = "";
		try
		{
			String relation = node.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation();
			if (relation != null)
				ret = relation;
		}
		catch(NullPointerException e)
		{}
		
		return ret;
	}

	public String getNodeStringRepresentation()
	{
		String ret = "";
		try
		{
			String lemma = node.getInfo().getNodeInfo().getWordLemma();
			if (lemma != null)
				ret = ret+lemma;
		}
		catch(NullPointerException e)
		{}
		
		try
		{
			String pos = InfoGetFields.getPartOfSpeech(node.getInfo());
			if (pos != null)
				ret = ret+" ["+pos+"]";
		}
		catch(NullPointerException e)
		{}
		
		try
		{
			NamedEntity ne = node.getInfo().getNodeInfo().getNamedEntityAnnotation();
			if (ne != null)
			{
				String neString = ne.toString();
				if (neString!=null)
				{
					ret = ret+" <"+neString+">";
				}
			}
		}
		catch(NullPointerException e)
		{}
		
		return ret;
	}
	
	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}
	
	protected AbstractNode<? extends Info, ?> node;

}
