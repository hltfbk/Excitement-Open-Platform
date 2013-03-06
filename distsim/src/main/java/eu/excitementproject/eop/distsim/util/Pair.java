package eu.excitementproject.eop.distsim.util;

import java.io.Serializable;

/**
 * An ordered pair of elements of two types.
 * <P>
 * 
 * @author Meni Adler
 * @since March 24, 2011
 * 
 * @param <T1>
 *            The type of the first element
 * @param <T2>
 *            The type of the second element
 */
public class Pair<T1, T2> implements Serializable  {

	
	private static final long serialVersionUID = 1L;
	
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the first item of the pair
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * @return the second item of the pair
	 */
	public T2 getSecond() {
		return second;
	}

	/**
	 * set the first item of the pair
	 */
	public void setFirst(T1 data) {
		first = data;
	}

	/**
	 * set the second item of the pair
	 */
	public void setSecond(T2 data) {
		second = data;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(getFirst());
		sb.append(",");
		sb.append(getSecond());
		sb.append(">");
		return sb.toString();
	}

	protected T1 first;
	protected T2 second;

}
