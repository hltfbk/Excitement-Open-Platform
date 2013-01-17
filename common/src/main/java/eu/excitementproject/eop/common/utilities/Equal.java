package eu.excitementproject.eop.common.utilities;

/**
 * <p>A null-safe way to check if two objects are equal.
 * <p>Makes "equals" methods about 5 times shorter.
 * <p>For usage example, see {@link ac.biu.nlp.nlp.datasets.TextHypothesisAnnotation#equals}, and compare with {@link ac.biu.nlp.nlp.datasets.TextHypothesisPair#equals}. 
 * @author erelsgl
 * @date 15/09/2011
 */
public class Equal {
	public static boolean are(Object a, Object b) {
		return (a==null? b==null: a.equals(b));
	}

	
	/**
	 * main program for testing only
	 * @param args
	 */
	public static void main(String[] args) {
		assert (Equal.are(new Integer(5), new Integer(5)));
		assert (!Equal.are(new Integer(5), new Integer(6)));
		assert (!Equal.are(new Integer(5), null));
		assert (!Equal.are(null, new Integer(6)));
		assert (Equal.are(null, null));
	}
}
