package eu.excitementproject.eop.alignmentedas.p1eda.subs;

public class ClassifierException extends Exception {

	/**
	 * An exception designed to be thrown from EDAClassifierAbstraction implementations 
	 */
	private static final long serialVersionUID = 1408025289983238534L;

	public ClassifierException(String message) {
		super(message);
	}

	public ClassifierException(Throwable cause) {
		super(cause);
	}

	public ClassifierException(String message, Throwable cause) {
		super(message, cause);
	}


}
