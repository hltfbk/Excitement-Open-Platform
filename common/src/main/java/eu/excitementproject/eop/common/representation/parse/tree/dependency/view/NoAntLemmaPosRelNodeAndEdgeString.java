package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;

public class NoAntLemmaPosRelNodeAndEdgeString implements NodeAndEdgeString<Info>
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

		
		String pos = getPos();
		
		String ret = "";
		if (lemma!=null) ret = ret + lemma;
		
		if (pos!=null) ret = ret + " ["+pos+"]";
		
		return ret;
	}

	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}
	
	protected String getPos()
	{
		String pos = "";
		try{
			pos = InfoGetFields.getPartOfSpeech(node.getInfo());
			if (null==pos) pos = "";
		}catch(NullPointerException e){}
		return pos;

		
	}
	
	protected AbstractNode<? extends Info, ?> node;
	
	
	/////////// NESTED CLASS - VARIANT OF THIS CLASS ///////////
	
	public static class NoAntLemmaCanonicalPosRelNodeAndEdgeString extends NoAntLemmaPosRelNodeAndEdgeString
	{
		@Override
		protected String getPos()
		{
			return SimplerPosTagConvertor.simplerPos(
					InfoGetFields.getCanonicalPartOfSpeech(node.getInfo())).name();
		}
	}
	

}
