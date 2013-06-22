package eu.excitementproject.eop.distsim.util;

import java.lang.reflect.Constructor;

/**
 * Instantiation of objects according to a given class name and a list of paramaters for the constructor
 * 
 * @author Meni Adler
 * @since 24/10/2012
 *
 */
public class Factory {
	/**
	 * Instantiation of objects according to a given class name and a list of parameters for the constructor
	 * 
	 * @param className name of the class to be instantiated
	 * @param params a list of parameters for the constructor
	 * @return an instance of the given class name, initialized by the given parameters
	 * @throws CreationException
	 */
	public static Object create(String className, Object... params) throws CreationException {
		try {
		 Class<?> cls = Class.forName(className);

		 if (params.length > 0) {
		    Constructor<?> constructor = getConstructor(cls, params);
	     	return constructor.newInstance(params);
		 } else
			 return cls.newInstance();
		} catch (Exception e)  {
			throw new CreationException(e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected static Constructor<?> getConstructor(Class cls, Object[] params) throws NoSuchMethodException {
		for (Constructor<?> cons : cls.getConstructors()) {
			Class[] consParams = cons.getParameterTypes();
			if (consParams.length == params.length) {
				int iMatchedParams = 0;
				for (int i=0; i< consParams.length; i++) {
					if (!consParams[i].isInstance(params[i]))
						break;
					else 
						iMatchedParams++;
				}
				if (iMatchedParams == consParams.length)
					return cons;
			}
		}
		
		// no coonstructor was found - throw an exception with missed parameter types
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<params.length; i++) {
			sb.append("\n\t");
			sb.append(params[i].getClass());			
		}
		throw new NoSuchMethodException("no constructor of " + cls + " was found for the given parameter types:" + sb.toString());
	}
}
