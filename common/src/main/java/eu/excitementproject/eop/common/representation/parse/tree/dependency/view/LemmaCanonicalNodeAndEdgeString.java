package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;

/**
 * 
 * @author Asher Stern
 * @since Nov 18, 2013
 *
 */
public class LemmaCanonicalNodeAndEdgeString implements NodeAndEdgeString<Info>
{

	@Override
	public void set(AbstractNode<? extends Info, ?> node)
	{
		this.node = node;
	}

	@Override
	public String getNodeStringRepresentation()
	{
		String lemma = InfoGetFields.getLemma(node.getInfo());
		String pos = Character.toString(SimplerPosTagConvertor.simplerPos(InfoGetFields.getPartOfSpeechObject(node.getInfo()).getCanonicalPosTag()).name().charAt(0));
		return lemma+":"+pos;
	}

	@Override
	public String getEdgeStringRepresentation()
	{
		return InfoGetFields.getRelation(node.getInfo());
	}

	private AbstractNode<? extends Info, ?> node;

}
