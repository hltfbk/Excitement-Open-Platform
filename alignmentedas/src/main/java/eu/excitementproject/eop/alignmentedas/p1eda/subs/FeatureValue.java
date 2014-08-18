package eu.excitementproject.eop.alignmentedas.p1eda.subs;



/**
 * A class that represents one Feature value, for classifier abstraction 
 * Relies on Value class. 
 * 
 * @author Tae-Gil Noh
 */
public class FeatureValue extends Value {

	public FeatureValue(double d) {
		super(d);
	}

	public FeatureValue(Enum<?> e) {
		super(e);
	}

	public FeatureValue(Boolean b) {
		super(b);
	}

}
