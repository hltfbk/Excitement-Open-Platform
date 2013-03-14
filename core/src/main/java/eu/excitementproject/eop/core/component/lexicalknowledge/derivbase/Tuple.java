/*
 * This source file is part of the RECIPE project.
 *
 * It is developed as a student software project at the
 * Department of Computational Linguistics at the University of Heidelberg by
 *
 * - Michael Haas <haas@cl.uni-heidelberg.de>
 * - Hiko Schamoni <schamoni@cl.uni-heidelberg.de>
 * - Tilman Wittl <wittl@cl.uni-heidelberg.de>
 * - Britta Zeller <zeller@cl.uni-heidelberg.de>
 *
 * under the supervision of Nils Reiter <reiter@cl.uni-heidelberg.de>
 *
 * RECIPE stands for "Recipe Event Chain Imperative Processing Engine".
 *
 * For the individual authors of this particular source file,
 * please take a look at the class documentation below.
 *
 * In order to generate HTML files from the embedded documentation,
 * please execute the 'javadoc' ant task:
 * $ ant javadoc
 * or run javadoc manually.
 *
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

import java.io.Serializable;

/**
 * Class used to hold a Tuple.
 *
 * @author Tilman Wittl <wittl@cl.uni-heidelberg.de>
 */
public class Tuple<E> implements Serializable {

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

        Tuple tuple = (Tuple) o;

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
