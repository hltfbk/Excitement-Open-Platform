package eu.excitementproject.eop.common.utilities;

/**
 * A cache for a single element, with no key. The element will be initialized the first time it is used.
 * @author erelsgl
 * @date 25/08/2011
 */
public abstract class ElementInitializedOnFirstUseWithExpirationTime<T> extends ElementInitializedOnFirstUse<T> {
	private long myExpirationTimeMillis;

	private long myElementLastUpdateTimeMillis;

	public ElementInitializedOnFirstUseWithExpirationTime (long expirationTimeMillis) {
		myExpirationTimeMillis = expirationTimeMillis;
	}
	
	/**
	 * Cleanup the value, before re-initialize.
	 */
	protected abstract void cleanup() throws Throwable;
	
	/**
	 * @return the initial (and final) value of myElement.
	 */
	@Override public synchronized T get() {
		if (element==null || myExpirationTimeMillis < System.currentTimeMillis()-myElementLastUpdateTimeMillis) {
			try {
				if (element!=null)
					cleanup();
				initialize();
				if (element==null) 
					throw new NullPointerException("myElement still null after initialization!");
				myElementLastUpdateTimeMillis = System.currentTimeMillis();
			} catch (Throwable ex) {
				throw new RuntimeException("Cannot initialize myElement", ex);
			}
		}
		return element;
	}

	@Override public synchronized void expire() {
		if (element!=null)
			try {
				cleanup();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		element = null;
	}
}
