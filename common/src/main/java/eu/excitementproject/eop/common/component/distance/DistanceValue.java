package eu.excitementproject.eop.common.component.distance;

//import java.util.Vector;

/**
 * This type holds the distance calculation result. It has some member 
 * variables and public access functions for the variable. The type is 
 * used to exchange data between the distance calculation components and 
 * the EDAs.
 * [Spec Section 4.5.2] 
 * Note that this abstract class is not immutable. 
 * (To make it immutable, override distanceVector part properly 
 * (i.e. defensive copying, etc) )
 * 
 * <P>
 * Note that "DistanceValue" no longer has a vector. Vector returning capability 
 * has moved into interface ScoringComponent and its method calculateScores(). 
 */

public abstract class DistanceValue {

//	public DistanceValue(double distance, boolean simBased, double rawValue)
//	{
//		this(distance, simBased, rawValue, null); 
//	}

	public DistanceValue(double distance, boolean simBased, double rawValue) 
	{
		this.distance = distance;
		this.simBased = simBased;
		this.unnormalizedValue = rawValue; 
	}

//	public DistanceValue(double distance, boolean simBased, double rawValue, Vector<Double> distanceVector)
//	{
//		this.distance = distance;
//		this.simBased = simBased;
//		this.unnormalizedValue = rawValue; 
//		this.distanceVector = distanceVector; 
//	}
	
	/**
	 * Returns the normalized distance. The maximum value is 1 
	 * (maximally different), and the minimum value is 0 (totally identical).
	 */
	public double getDistance() 
	{ 
		return distance; 
	}
	
	/**
	 * Returns a boolean. This boolean is true if the calculation is based on a 
	 * similarity functions. This boolean is false if the calculation is 
	 * based on distance-based calculations. This value is provided to help the 
	 * interpretation of the unnormalizedValue. Users can ignore this value, if 
	 * they do not use the unnormalizedValue.
	 */
	public boolean isSimBased()
	{
		return simBased; 
	}
	
	/**
	 * Returns unnormalizedValue. This variable holds a distance or similarity 
	 * value that is not normalized. If the value is mapped into the range with 
	 * common normalization, it will produce the value stored at distance. This 
	 * unnormalized value is provided for the users to use some other
	 * methods of normalizations. 
	 */
	public double getUnnormalizedValue()
	{
		return unnormalizedValue; 
	}

	///**
	// * returns distanceVector. This variable holds a set of double values. 
	// * The vector is an optional value that permits the distance calculation 
	// * components to return a set of distance values that is needed, or used to 
	// * generate the main value. If the component does not provide this vector,
	// * this variable should be <code>null</code>.
	// */
	//public Vector<Double> getDistanceVector()
	//{
	//	return distanceVector; 
	//}

	
	private final double distance;
	
	private final boolean simBased; 
	
	private final double unnormalizedValue; 
	
	//private final Vector<Double> distanceVector; 	
}
