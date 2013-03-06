package eu.excitementproject.eop.distsim.items;

/**
 * Element is the basic object of the distributional similarity.
 * An implementation of this interface defines the objects that their similarity should be measured, e.g., two words, two predicate templates, etc.
 * 
 * BasicElements are KeyExternalizable, Identifiable, Countable, and have an AggregatedContext.
 *  
 * @author Meni Adler
 * @since 23/04/2012
 *
 */
public interface Element extends Identifiable, Countable, Externalizable {
	Object getData();
	AggregatedContext getContext() throws NoContextFoundException;	
}
