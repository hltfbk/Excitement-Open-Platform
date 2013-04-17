package eu.excitementproject.eop.distsim.util;

import java.util.NoSuchElementException;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableIterator;


/**
 * Implements an array-based iterator for double values
 * 
 * @author Meni Adler
 * @since 28/12/2012
 *
 */
public class DoubleArrayIterator extends ImmutableIterator<Double> {

	protected int currentIndex;
	protected final double[] arr;
	 
	public DoubleArrayIterator(double[] arr)   {
		currentIndex = 0;
	    this.arr = arr;
	}      
	 
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Double next() { 
		if (currentIndex == arr.length)  
			throw new NoSuchElementException(); 
		else 
        	return arr[currentIndex++]; 
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
    public boolean hasNext() { 
       return (currentIndex < arr.length);
    }

}
