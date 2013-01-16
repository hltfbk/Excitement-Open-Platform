package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * 
 * @author Asher Stern
 *
 */
public class WordAndNerNodeString implements NodeString<Info>
{
	public final String WORD_DEFAULT = "(-)";
	public final String NER_DEFAULT = "(-)";

	public String getStringRepresentation()
	{
		
		String word = null;
		String ner = null;
		try{word=node.getInfo().getNodeInfo().getWord();}catch(Exception e){}
		try{ner=node.getInfo().getNodeInfo().getNamedEntityAnnotation().toString();}catch(Exception e){}
		if (null==word) word = WORD_DEFAULT;
		if (null==ner) ner = NER_DEFAULT;

		if (node.getAntecedent()!=null)
		{
			word="^";
			try{word="^"+node.getAntecedent().getInfo().getNodeInfo().getWord();}catch(Exception e){}
		}
		return word+" / "+ner; 
	}

	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}
	
	protected AbstractNode<? extends Info, ?> node;

}
