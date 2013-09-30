/**
 * 
 */
package eu.excitementproject.eop.distsim.items;

import java.io.Serializable;

import eu.excitementproject.eop.distsim.util.Pair;

/**
 * The RelationBasedFeature defines a general feature which is based on some data with relation
 * 
 * The class is abstract, the toKey() method should be implemented given a specific type of T  
 *   
 * Thread-safe
 * @author Meni Adler
 * @since 28/06/2012
 *
 */
public abstract class RelationBasedFeature<R,T   extends Serializable> extends DeafaultFeature<Pair<R,T >> {

 
	private static final long serialVersionUID = 1L;

	public RelationBasedFeature() {
		super();
	}
	
	public RelationBasedFeature(R relation, T data) {
		super(new Pair<R,T>(relation,data));
	}

	public RelationBasedFeature(R relation, T data, AggregatedContext context) {
		super(new Pair<R,T>(relation,data), context);
	}

	public RelationBasedFeature(R relation, T data, int id, long count) {
		super(new Pair<R,T>(relation,data),id,count);
	}
	
	public RelationBasedFeature(R relation, T data, AggregatedContext context, int id, long count) {
		super(new Pair<R,T>(relation,data),context,id,count);
	}	
}
