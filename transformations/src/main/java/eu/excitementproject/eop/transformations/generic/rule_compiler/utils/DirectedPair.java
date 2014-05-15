/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.utils;
import java.io.Serializable;

import eu.excitementproject.eop.common.datastructures.Pair;


/**
 * An ordered pair of elements, left and right.
 * @author amnon
 *
 */
@SuppressWarnings("serial")
public class DirectedPair<T> extends Pair<T> implements Serializable{

	public DirectedPair(T left, T right) {
		super(left, right);
	}
		
	public T getLeft()
	{
		return this.element1;
	}

	public T getRight()
	{
		return this.element2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectedPair<?> other = (DirectedPair<?>) obj;
		
		boolean eq1to1 = false;
		if (element1 == null)
		{
			if (other.element1 != null)
				eq1to1 =  false;
		}
		else eq1to1 = element1.equals(other.element1);

		boolean eq2to2 = false;
		if (element2 == null)
		{
			if (other.element2 != null)
				eq2to2 =  false;
		}
		else eq2to2 = element2.equals(other.element2);
		
		return ( eq1to1 && eq2to2 ); 
	}
}
