package eu.excitementproject.eop.common.representation.parse.representation.basic;

import java.io.Serializable;

import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;


/**
 * Represents the syntactic information of a node, which is part
 * of the {@link Info} hold by that node.
 * 
 * <P>
 * <B>All implementations must be immutable!!!</B>
 * 
 * @author Asher Stern
 *
 */
public interface SyntacticInfo extends Serializable
{
	public PartOfSpeech getPartOfSpeech();
	
	public boolean equals(Object obj);
	public int hashCode();
	
	
	
	
}
