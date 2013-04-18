package eu.excitementproject.eop.distsim.items;

import java.io.Serializable;

/**
 * A simple implementation of the Identifiable and the Countable<Long> interfaces
 * 
 * @author Meni Adler
 * @since 20/06/2012
 * 
 * <p>
 * Thread-safe
 */
public class DefaultIdentifiableCountable implements Identifiable, Countable, Serializable {


	private static final long serialVersionUID = 1L;
	
	public DefaultIdentifiableCountable() {
		this.id = -1;
		this.count = 0;
	}
	

	public DefaultIdentifiableCountable(double count) {
		this.id = -1;
		this.count = count;
	}
	
	public DefaultIdentifiableCountable(int id, double count) {
		this.id = id;
		this.count = count;
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Identifiable#getID()
	 */
	@Override
	public synchronized int getID() throws InvalidIDException {
		if (id < 0)
			throw new InvalidIDException();
		return id;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Identifiable#setID(int)
	 */
	@Override
	public synchronized void setID(int id) {
		this.id = id;
	}

	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Countable#getCount()
	 */
	@Override
	public synchronized double getCount() throws InvalidCountException{
		if (count < 0)
			throw new InvalidCountException();		
		return count;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Countable#incCount(java.lang.Number)
	 */
	@Override
	public synchronized void incCount(double val) throws InvalidCountException{
		if (count < 0)
			throw new InvalidCountException(this.toString());		
		count += val;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.items.Countable#setCount(double)
	 */
	@Override
	public synchronized void setCount(double count) {
		this.count = count;		
	}
	
	protected int id;
	protected double count;
	
}
