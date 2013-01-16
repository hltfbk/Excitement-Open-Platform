package eu.excitementproject.eop.common.utilities;

/**
 * A cache for a single element, with no key. The element will be initialized the first time it is used.
 * @author erelsgl
 * @date 25/08/2011
 */
public abstract class ElementInitializedOnFirstUse<T> {
	protected T element = null;
	
	/**
	 * Put the initial (and final) value into myElement.
	 */
	protected abstract void initialize() throws Throwable;
	
	/**
	 * @return the initial (and final) value of myElement.
	 */
	public synchronized T get() {
		if (element==null) {
			try {
				initialize();
				if (element==null) 
					throw new NullPointerException("element still null after initialization!");
			} catch (Throwable ex) {
				throw new RuntimeException("Cannot initialize element", ex);
			}
		}
		return element;
	}


	public synchronized void expire() {
		element = null;
	}
}
