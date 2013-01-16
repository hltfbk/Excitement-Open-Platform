package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

/**
 * 
 * @author Asher Stern
 * @since Mar 29, 2012
 *
 * @param <T> typically this is {@link PairResult}
 */
public class ResultWithScores<T>
{
	public ResultWithScores(T result, double cost, double costPlusThreshold,
			double classifierForSearchScore, double classificationScore)
	{
		super();
		this.result = result;
		this.cost = cost;
		this.costPlusThreshold = costPlusThreshold;
		this.classifierForSearchScore = classifierForSearchScore;
		this.classificationScore = classificationScore;
	}
	
	
	
	public T getResult()
	{
		return result;
	}
	
	/**
	 * Returns weight-vector * feature-vector.
	 * This is meaningless.
	 * @return
	 */
	public double getCost()
	{
		return cost;
	}
	
	/**
	 * 
	 * Returns weight-vector * feature-vector + threshold
	 * @return
	 */
	public double getCostPlusThreshold()
	{
		return costPlusThreshold;
	}
	
	/**
	 * sigmoid(weight-vector * feature-vector + threshold)
	 * @return
	 */
	public double getClassifierForSearchScore()
	{
		return classifierForSearchScore;
	}
	
	/**
	 * classification score by predictions-classifier.
	 * @return
	 */
	public double getClassificationScore()
	{
		return classificationScore;
	}



	private final T result;
	
	/**
	 * weight-vector * feature-vector.
	 * This is meaningless.
	 */
	private final double cost;
	
	/**
	 * weight-vector * feature-vector + threshold
	 */
	private final double costPlusThreshold;
	
	/**
	 * sigmoid(weight-vector * feature-vector + threshold)
	 */
	private final double classifierForSearchScore;
	
	/**
	 * classification score by predictions-classifier.
	 */
	private final double classificationScore;
}
