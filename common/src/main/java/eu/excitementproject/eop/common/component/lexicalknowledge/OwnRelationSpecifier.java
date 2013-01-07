package eu.excitementproject.eop.common.component.lexicalknowledge;

import eu.excitementproject.eop.common.component.lexicalknowledge.RelationSpecifier;

/**
 * See LexicalResourceWithRelation and RelationSpecifier for the general 
 * usage of relation specifier.  
 * 
 * @author Gil 
 */

public interface OwnRelationSpecifier<E> extends RelationSpecifier {

	/** 
	 * <P> The method returns the resource-specific relation (as E which is defined 
	 * by a generic parameter). The returned E will be the resource specific relation 
	 * that is being queried upon. This return value can be null, which means 
	 * "don't care".
	 * 
	 * <P> The parameter E is to be defined by each resource implementer to represent 
	 * resource specific relations.
	 * 
	 * @return E, a resource specific relation 
	 */
	public E getOwnRelation(); 
	
}
