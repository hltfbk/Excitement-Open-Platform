package eu.excitementproject.eop.distsim.items;

import java.io.Serializable;

/**
 * An implementation of a general text unit with a state of a generic type T
 * 
 * @author Meni Adler
 * @since 20/06/2012
 * 
 * @param <T>: the type of the text unit data
 * 
 */
public abstract class DeafaultTextUnit<T extends Serializable> extends DefaultIdentifiableCountable implements TextUnit {


	private static final long serialVersionUID = 1L;

	public DeafaultTextUnit(T data) {
		super();
		this.data = data;
	} 
	
	public DeafaultTextUnit(T data, long count) {
		super(count);
		this.data = data;
	} 

	public DeafaultTextUnit(T data, int id, long count) {
		super(id,count);
		this.data = data;

	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.TextUnit#getData()
	 */
	@Override
	public T getData() {
		return data;
	}
	
	protected T data;
}

