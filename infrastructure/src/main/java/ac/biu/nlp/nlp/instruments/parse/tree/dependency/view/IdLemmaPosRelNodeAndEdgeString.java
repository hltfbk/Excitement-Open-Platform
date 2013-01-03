package ac.biu.nlp.nlp.instruments.parse.tree.dependency.view;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode;

/**
 * 
 * @author Asher Stern
 *
 */
public class IdLemmaPosRelNodeAndEdgeString implements NodeAndEdgeString<Info>
{
	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}

	public String getEdgeStringRepresentation()
	{
		return InfoGetFields.getRelation(node.getInfo());
	}

	public String getNodeStringRepresentation()
	{
		String id = getIdOfInfo(node.getInfo());
		String lemma = InfoGetFields.getLemma(node.getInfo());
		String pos = getPosString();
		String variable = InfoGetFields.getVariable(node.getInfo());
		String idAntecedent = null;
		if (node.getAntecedent()!=null)
		{
			idAntecedent = getIdOfInfo(node.getAntecedent().getInfo());
		}
		if (null==idAntecedent)
			return id+":"+lemma+variable+":"+pos;
		else
			return id+":"+lemma+variable+":"+pos+"(^"+idAntecedent+")";
	}
	
	protected String getPosString()
	{
		return InfoGetFields.getPartOfSpeech(node.getInfo());
	}


	private static String getIdOfInfo(Info info)
	{
		if (null==info)
			return "";
		else
			return info.getId();
	}
	
	protected AbstractNode<? extends Info, ?> node;
	

}
