package eu.excitementproject.eop.distsim.items;

import java.io.Serializable;

/**
 * An implementation of a general feature with a state of a generic type T
 * 
 * @author Meni Adler
 * @since 20/06/2012
 * 
 * @param <T>: the type of the feature data
 * 
 */
public abstract class DeafaultFeature<T extends Serializable> extends DefaultIdentifiableCountable implements Feature {

	private static final long serialVersionUID = 1L;
	
	public DeafaultFeature() {
		super();
		this.data = null;
		this.context = null;
	}
	
	public DeafaultFeature(T data) {
		super();
		this.data = data;
		this.context = null;
	}

	public DeafaultFeature(T data, AggregatedContext context) {
		super();
		this.data = data;
		this.context = context;
	}

	public DeafaultFeature(T data, int id, long count) {
		super(id,count);
		this.data = data;
		this.context = null;

	}
	
	public DeafaultFeature(T data, AggregatedContext context, int id, long count) {
		super(id,count);
		this.data = data;
		this.context = context;
	}

	
	/**
	 * Get the feature data
	 * 
	 * @return the feature data
	 */
	@Override
	public T getData() {
		return data;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Element#getContext()
	 */
	@Override
	public AggregatedContext getContext() throws NoContextFoundException {
		return context;
	}
	
	protected T data;
	protected final AggregatedContext context;

}

