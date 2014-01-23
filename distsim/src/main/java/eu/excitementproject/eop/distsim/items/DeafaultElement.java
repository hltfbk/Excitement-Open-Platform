package eu.excitementproject.eop.distsim.items;

/**
 * An implementation of a general element with a state of a generic type T
 * 
 * @author Meni Adler
 * @since 20/06/2012
 * 
 * @param <T>: the type of the element data
 * 
 */
public abstract class DeafaultElement<T> extends DefaultIdentifiableCountable implements Element {

	
	private static final long serialVersionUID = 1L;
	
	public DeafaultElement() {
		super();
		this.data = null;
		this.context = null;
	}
	
	public DeafaultElement(T data) {
		super();
		this.data = data;
		this.context = null;
	}

	public DeafaultElement(T data, AggregatedContext context) {
		super();
		this.data = data;
		this.context = context;
	}

	public DeafaultElement(T data, int id, long count) {
		super(id,count);
		this.data = data;
		this.context = null;

	}
	
	public DeafaultElement(T data, AggregatedContext context, int id, long count) {
		super(id,count);
		this.data = data;
		this.context = context;
	}

	/**
	 * Get the element data
	 * 
	 * @return the element data
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

