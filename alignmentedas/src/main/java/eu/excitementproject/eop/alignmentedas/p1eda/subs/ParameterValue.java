package eu.excitementproject.eop.alignmentedas.p1eda.subs;

/**
 * A class that represents one Parameter value
 * The type is used in P1EDA as a representation of a parameter value.
 *   
 * @author Tae-Gil Noh
 */

public class ParameterValue extends Value {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3934660978437533820L;
	/**
	 * Constructor for initializing a ParameterValue 
	 * with double. Range of the parameter will be given 
	 * from default. (0 ~ 1) 
	 * 
	 * @param d
	 */
	public ParameterValue(double val) {		
		super(val);
		this.rangeMin = DEFAULT_MIN;
		this.rangeMax = DEFAULT_MAX; 
	}
	
	/**
	 * Constructor for initializing a ParameterValue 
	 * with double. This constructor enables you to set 
	 * min/max range of the double value of this parameter. 
	 * 
	 * @param val
	 * @param rangeMin
	 * @param rangeMax
	 */
	public ParameterValue(double val, double rangeMin, double rangeMax)
	{
		super(val); 
		this.rangeMin = rangeMin; 
		this.rangeMax = rangeMax; 
	}

	public ParameterValue(Enum<?> e) {
		super(e);
		rangeMin = null; 
		rangeMax = null; 
	}

	public ParameterValue(Boolean b) {
		super(b);
		rangeMin = null; 
		rangeMax = null; 
	}

	public double getRangeMin()
	{
		return rangeMin; 
	}
	
	public double getRangeMax()
	{
		return rangeMax; 
	}

	// Range (min and max values) of the parameter value
	// the values are only meaningful when getValueType() == ValueType.DOUBLE
	private final Double rangeMin; 
	private final Double rangeMax;

	// Default values 
	private final double DEFAULT_MIN = 0.0; 
	private final double DEFAULT_MAX = 1.0; 
	
}
