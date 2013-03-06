package eu.excitementproject.eop.distsim.items;


/**
 * The similarity measurement between elements is usually determined by the similarity of their features. The Feature interface defines an element feature.
 * 
 * BasicFeatures are KeyExternalizable, Identifiable, Countable, and assigned to an AggregatedContext.
 * @author Meni Adler
 * @since 23/05/2012
 *
 *  
 */
public interface Feature  extends Identifiable, Countable, Externalizable {
	Object getData();
	AggregatedContext getContext() throws NoContextFoundException;
}
