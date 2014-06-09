package eu.excitementproject.eop.distsim.util;

import java.util.Comparator;

public class InversedComparator<T extends Comparable<T>> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		return -1 * o1.compareTo(o2);
	}

}
