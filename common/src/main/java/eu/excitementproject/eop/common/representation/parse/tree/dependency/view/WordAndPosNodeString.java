package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;


/**
 * @author Asher Stern
 *
 */
public class WordAndPosNodeString implements NodeString<Info>
{
	protected final static String NULL_STRING = "(null)";
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.tree.dependency.NodeString#set(ac.biu.nlp.nlp.instruments.parse.tree.dependency.AbstractNode)
	 */
	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}


	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.tree.dependency.NodeString#getStringRepresentation()
	 */
	public String getStringRepresentation()
	{
		String word = NULL_STRING;
		String pos = NULL_STRING;
		if (node!=null)
		{
			if (node.getInfo()!=null)
			{
				Info nodeAndEdgeInfo = node.getInfo();
				NodeInfo nodeInfo = nodeAndEdgeInfo.getNodeInfo();
				if (nodeInfo !=null)
				{
					if (nodeInfo.getWord()!=null)
						word = nodeInfo.getWord();
					try{pos = InfoGetFields.getPartOfSpeech(nodeInfo);}catch(NullPointerException e){}
				}
			}
		}
		return word + " ["+pos+"]";
		
		

	}
	
	protected AbstractNode<? extends Info, ?> node;


}
