package eu.excitementproject.eop.distsim.util;

import java.util.Set;

/**
 * An implementation of the Filter interface, which is based on a fixed set of relevant items
 * 
 * @author Meni Adler
 * @since 05/12/2012
 *
 * @param <T> The type of items to be filtered
 */
public class SetBasedFilter<T> implements Filter<T> {

	public SetBasedFilter() {
		relevantItems = null;
	}
	
	@SuppressWarnings("unchecked")
	public SetBasedFilter(T... relevantItems) {
		for (T relevantItem : relevantItems)
			this.relevantItems.add(relevantItem);
	}

	/* (non-Javadoc)
	 * @see org.excitement.util.Filter#isRelevant(java.lang.Object)
	 */
	@Override
	public boolean isRelevant(T item) {
		return relevantItems == null || relevantItems.contains(item);
	}
	
	@Override
	public String toString() {
		return (relevantItems == null ? "all" : relevantItems.toString());
	}
	
	protected Set<T> relevantItems;
}
