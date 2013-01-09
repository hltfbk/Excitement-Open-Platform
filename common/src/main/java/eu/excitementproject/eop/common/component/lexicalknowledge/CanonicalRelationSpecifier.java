package eu.excitementproject.eop.common.component.lexicalknowledge;

import eu.excitementproject.eop.common.component.lexicalknowledge.RelationSpecifier;
import eu.excitementproject.eop.common.component.lexicalknowledge.TERuleRelation;

/**
 * See LexicalResourceWithRelation and RelationSpecifier for the general 
 * usage of relation specifier.  
 * 
 * @author Gil 
 */
public interface CanonicalRelationSpecifier extends RelationSpecifier {

	/**
	 * The method returns the canonical relation (as enum TERuleRelation) 
	 * where it means this canonical relation is being queried upon. This 
	 * return value can be null, which means "don't care" (the query don't 
	 * care about canonical relation).
	 * @return TERuleRelation 
	 */
	public TERuleRelation getCanonicalRelation(); 
	
}
