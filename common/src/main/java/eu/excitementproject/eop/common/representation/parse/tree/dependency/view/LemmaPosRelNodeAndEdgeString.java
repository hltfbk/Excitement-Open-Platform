package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;

public class LemmaPosRelNodeAndEdgeString implements NodeAndEdgeString<Info>
{

	public String getEdgeStringRepresentation()
	{
		String ret = "";
		try{
			ret = node.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation();
			if (null==ret) ret = "";
		}catch(NullPointerException e) {}

		return ret;
	}

	public String getNodeStringRepresentation()
	{
		AbstractNode<? extends Info,?> relevantNode = node;
		if (node.getAntecedent()!=null)
			relevantNode = AbstractNodeUtils.weakGetDeepAntecedentOf(node);
		
		String lemma = "";
		try{
			lemma = node.getInfo().getNodeInfo().getWordLemma();
			if (null==lemma) lemma = "";
		}catch(NullPointerException e) {}
		
		if ( (lemma.equals("")) && (node.getAntecedent()!=null))
		{
			try{
				lemma = relevantNode.getInfo().getNodeInfo().getWordLemma();
				if (null==lemma) lemma = "";
			}catch(NullPointerException e) {}
		}

		
		String pos = "";
		try{
			pos = InfoGetFields.getPartOfSpeech(node.getInfo());
			if (null==pos) pos = "";
		}catch(NullPointerException e){}
		
		String antecedent = "";
		try{
			if (node.getAntecedent()!=null)
				antecedent = "(^ )";
		}
		catch(NullPointerException e){}
		
		
		String ret = "";
		if (antecedent!=null) ret = ret + antecedent; 
		if (lemma!=null) ret = ret + lemma;
		
		if (pos!=null) ret = ret + " ["+pos+"]";
		
		return ret;
	}

	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}
	
	private AbstractNode<? extends Info, ?> node;
	
	

}
