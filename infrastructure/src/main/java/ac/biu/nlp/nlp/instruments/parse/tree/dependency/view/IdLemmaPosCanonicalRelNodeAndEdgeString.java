package ac.biu.nlp.nlp.instruments.parse.tree.dependency.view;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;

/**
 * 
 * @author Asher Stern
 * @since Oct 4, 2012
 *
 */
public class IdLemmaPosCanonicalRelNodeAndEdgeString extends IdLemmaPosRelNodeAndEdgeString
{
	@Override
	protected String getPosString()
	{
		
		return InfoGetFields.getPartOfSpeech(node.getInfo()) + " ("+InfoGetFields.getCanonicalPartOfSpeech(node.getInfo()).name()+")";
	}

}
