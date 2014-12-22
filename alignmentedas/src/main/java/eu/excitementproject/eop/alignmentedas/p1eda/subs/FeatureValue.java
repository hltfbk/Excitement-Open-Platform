package eu.excitementproject.eop.alignmentedas.p1eda.subs;



/**
 * A class that represents one Feature value, for classifier abstraction 
 * Relies on Value class. 
 * 
 * @author Tae-Gil Noh
 */
public class FeatureValue extends Value {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1413295719580599625L;

	/**
	 * Constructor for a feature value with a double value. 
	 * 
	 * @param featureName the name of the feature; not really used by the classifier, and does not affect classification result. (just a name). But the value will be displayed for human readers when they run classification inspector for detailed information of a classification. 
	 * @param d a double value which will be kept as the feature for this instance. 
	 */
	public FeatureValue(String featureName, double d)
	{
		super(d); 
		this.featureName = featureName; 
	}
	
	/**
	 * Constructor for a feature value with a enum value. (nominal feature) 
	 * @param featureName the name of the feature; not really used by the classifier, and does not affect classification result. (just a name). But the value will be displayed for human readers when they run classification inspector for detailed information of a classification.
	 * @param e a enum value which will be kept as the feature for this instance. 
	 */
	public FeatureValue(String featureName, Enum<?> e)
	{
		super(e); 
		this.featureName = featureName; 
	}

	/**
	 * Constructor for a feature value with a boolean value. (binary feature) 
	 * @param featureName the name of the feature; not really used by the classifier, and does not affect classification result. (just a name). But the value will be displayed for human readers when they run classification inspector for detailed information of a classification.
	 * @param e a Boolean value which will be kept as the feature for this instance. 
	 */
	public FeatureValue(String featureName, Boolean b)
	{
		super(b); 
		this.featureName = featureName; 
	}
	
	/**
	 * (Deprecated constructor --- please use FeatureValue(String, double) instead of this constructor. ) 
	 * @param d
	 */
	@Deprecated
	public FeatureValue(double d) {
		super(d);
		featureName = "(NoName)"; 
	}

	/**
	 * (Deprecated constructor --- please use FeatureValue(String, enum) instead of this constructor. ) 
	 * @param e
	 */
	@Deprecated
	public FeatureValue(Enum<?> e) {
		super(e);
		featureName = "(NoName)"; 

	}

	/**
	 * 	 * (Deprecated constructor --- please use FeatureValue(String, Boolean) instead of this constructor. ) 
	 * @param b
	 */
	@Deprecated 
	public FeatureValue(Boolean b) {
		super(b);
		featureName = "(NoName)"; 
	}

	public String getFeatureName()
	{
		return this.featureName; 
	}
	
	@Override
	public String toString()
	{
		return featureName + ": " + super.toString(); 
	}
	
	private final String featureName; 
}
