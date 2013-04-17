package eu.excitementproject.eop.distsim.util;

/**
 * The Filter interface determines whether a given item (of a general type T) is relevant or not
 * 
 * @author Meni Adler
 * @since 05/12/2012
 *
 * @param <T> The type of items to be filtered
 */
public interface Filter<T> {
	boolean isRelevant(T item);
}
