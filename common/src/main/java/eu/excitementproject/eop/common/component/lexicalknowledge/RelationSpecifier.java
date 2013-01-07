package eu.excitementproject.eop.common.component.lexicalknowledge;

/**
 * <P> 
 * This is an empty interface where it defines no methods. Extending or implementing this 
 * interface means that the class is to be used as the parameter R of 
 * LexicalResourceWithRelation. This interface is the top of the relation specifier hierarchy, 
 * and intentionally left empty. 
 * <P>
 * This means that you can design a  special RelationSpecifier that can be used in 
 * LexicalResourceWithRelation if needed. However, in most cases, you will implement one or 
 * both of the its known sub-interfaces. (see CanonicalRelationSpecifier and OwnRelationSpecifier). 
 * 
 * @author Gil
 *
 */
public interface RelationSpecifier {

}
