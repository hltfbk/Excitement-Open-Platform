package eu.excitementproject.eop.common.representation.parse.tree;

import java.util.List;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNodeConstructor;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;


/**
 * Creates a new node ({@link AbstractNode}) of type <code>S</code>
 * @author Asher Stern
 * 
 * @see BasicNodeConstructor
 * @see BasicConstructionNodeConstructor
 *
 * @param <T>
 * @param <S>
 */
public interface AbstractNodeConstructor<T,S extends AbstractNode<T,S>>
{
	public S newEmptyNode();
	public S newEmptyNode(List<? extends AbstractNode<T,?>> children);
	
	public S newNode(T info);
	public S newNode(T info,List<? extends AbstractNode<T,?>> children);
}
