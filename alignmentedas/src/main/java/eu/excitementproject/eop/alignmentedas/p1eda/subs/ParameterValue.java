package eu.excitementproject.eop.alignmentedas.p1eda.subs;

/**
 * A class that represents one Parameter value
 * The type is used in P1EDA as a representation of a parameter value.
 *   
 * @author Tae-Gil Noh
 */

public class ParameterValue extends Value {

	public ParameterValue(double d) {
		super(d);
	}

	public ParameterValue(Enum<?> e) {
		super(e);
	}

	public ParameterValue(Boolean b) {
		super(b);
	}

	// TODO, for all d(double) values, 
	// possibility to add range (max, min) and step (minimal change) 
}
