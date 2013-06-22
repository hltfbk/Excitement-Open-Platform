package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

import java.io.Serializable;

/**
 * Class used to hold a Tuple of two items.
 *
 * @author Tilman Wittl <wittl@cl.uni-heidelberg.de>
 */
public class Tuple<E> implements Serializable {

    /**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -982546336836905784L;
	
	/**
     * the first value.
     */
    E a;
    /**
     * the second value.
     */
    E b;


    
    /**
     * 
     * @param a First value
     * @param b Second value
     */
    public Tuple(E a, E b) {
        this.a = a;
        this.b = b;
    }

    
    /**
     * 
     * @param a First value
     * @param b Second value
     */
    public Tuple() {
    }

    
    
    /**
     * Getter method for the first value.
     *
     * @return first value
     */
    public E getA() {
        return this.a;
    }

    /**
     * Getter method for the second value.
     *
     * @return second value
     */
    public E getB() {
        return this.b;
    }

    /**
     * Setter method for the first value.
     * @param a new first value
     */
    public void setA(E a) {
        this.a = a;
    }

    /**
     * Setter method for the second value.
     * @param b a new second value
     */
    public void setB(E b) {
        this.b = b;
    }
    
    @Override
    public String toString() {
        return this.a.toString() + ", " + this.b.toString();
    }

    /**
     * Returns a new tuples with the values swapped.
     *
     * @return swapped tuple
     */
    public Tuple<E> permutate() {
        return new Tuple<E>(this.b, this.a);
    }

    /**
     * Equals method for tuples.
     *
     * @param tup Tuple to be compare
     * @return true or false
     */
    public boolean equals(Tuple<E> tup) {
        return ((tup.getA().equals(this.a)) && (tup.getB().equals(this.b)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        @SuppressWarnings("unchecked") // added to suppress warning - gil 
		Tuple<E> tuple = (Tuple<E>) o;
        
        if (this.a != null ? !a.equals(tuple.a) : tuple.a != null) {
            return false;
        }
        if (this.b != null ? !b.equals(tuple.b) : tuple.b != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (this.a != null ? this.a.hashCode() : 0);
        result = 31 * result + (this.b != null ? this.b.hashCode() : 0);
        return result;
    }
}
