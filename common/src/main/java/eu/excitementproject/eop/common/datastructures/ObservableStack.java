package eu.excitementproject.eop.common.datastructures;

import java.io.Serializable;
import java.util.EmptyStackException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;


/**
 * Like <code>java.util.Stack</code> but with {@link #getAsList()}.
 * @author Asher Stern
 *
 * @param <E>
 */
public interface ObservableStack<E> extends Serializable
{
	/**
	 * Tests if this stack is empty. 
	 * @return <tt>true</tt> if and only if this stack contains no items; <tt>false</tt> otherwise.
	 */
	public boolean empty();
	
	/**
	 * Looks at the object at the top of this stack without removing it from the stack.
	 *  
	 * @return the object at the top of this stack. 
	 * @throws EmptyStackException if this stack is empty.
	 */
	public E peek() throws EmptyStackException;
	
	/**
	 * Removes the object at the top of this stack and returns that object as the value of this function.
	 *  
	 * @return The object at the top of this stack
	 * @throws EmptyStackException if this stack is empty.
	 */
	public E pop() throws EmptyStackException;
	
	/**
	 * Pushes an item onto the top of this stack.
	 * 
	 * @param item the item to be pushed onto this stack. 
	 * @return the <code>item</code> argument.
	 */
	public E push(E item);
	
	/**
	 * Returns a list of the elements in the stack.
	 * The first element is the element in the
	 * stack's bottom. The last element is the
	 * element in the stack's top.
	 * 
	 * @return a list of the elements in the stack.
	 */
	public ImmutableList<E> getAsList();
	

}
