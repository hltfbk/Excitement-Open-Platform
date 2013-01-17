package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;

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
