package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.SyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

public class SimpleNodeString implements NodeString<Info>
{
	private static final String NULL_STRING = "(null)";
	private static final String ANTECEDENT_PREFIX = "antecedent = (";
	private static final String ANTECEDENT_POSTFIX = ")";
	
	
	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}
	
	public String getStringRepresentation()
	{
		String ret = NULL_STRING;
		
		
		String id = NULL_STRING;
		String word = NULL_STRING;
		String lemma = NULL_STRING;
		String partOfSpeechString = NULL_STRING;
		String dependencyRelationString = NULL_STRING;
		if (node != null)
		{
			Info info = node.getInfo();
			if (info != null)
			{
				id = info.getId();
				if (info.getNodeInfo()!=null)
				{
					NodeInfo nodeInfo = info.getNodeInfo();
					if (nodeInfo.getWord()!=null)word = nodeInfo.getWord();
					if (nodeInfo.getWordLemma()!=null)lemma = nodeInfo.getWordLemma();
					SyntacticInfo nodeSyntacticInfo = nodeInfo.getSyntacticInfo();
					if (nodeSyntacticInfo != null)
					{
						if (nodeSyntacticInfo.getPartOfSpeech()!=null)partOfSpeechString = nodeSyntacticInfo.getPartOfSpeech().toString();
					}
				}
				if (info.getEdgeInfo()!=null)
				{
					EdgeInfo edgeInfo = info.getEdgeInfo();
					if (edgeInfo.getDependencyRelation()!=null) dependencyRelationString = edgeInfo.getDependencyRelation().toString();
				}
			}
		}
		
		String antecedentRepresentation = NULL_STRING;
		if (node!=null){if (node.getAntecedent()!=null)
		{
			try
			{
				NodeString<Info> nsAntecedent = this.getClass().newInstance();
				nsAntecedent.set(node.getAntecedent());
				antecedentRepresentation = ANTECEDENT_PREFIX + nsAntecedent.getStringRepresentation() + ANTECEDENT_POSTFIX;
			}
			catch(IllegalAccessException e){}
			catch(InstantiationException e){}
		}}
		ret = "("+
		id
		+" / "+
		word + "[" + lemma + "]"
		+" / "+
		partOfSpeechString
		+" / "+
		dependencyRelationString
		+")";
		
		if (node.getAntecedent()!= null)
			ret = ret + " " + antecedentRepresentation;
		
		return ret;

	}
	
	protected AbstractNode<? extends Info, ?> node;
}

