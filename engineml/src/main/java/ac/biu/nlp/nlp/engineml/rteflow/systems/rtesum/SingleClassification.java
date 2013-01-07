package ac.biu.nlp.nlp.engineml.rteflow.systems.rtesum;

/**
 * 
 * @author Asher Stern
 * @since Jun 8, 2011
 *
 */
public class SingleClassification
{
	public SingleClassification(double score, boolean classification)
	{
		super();
		this.score = score;
		this.classification = classification;
	}
	
	public double getScore()
	{
		return score;
	}
	public boolean isClassification()
	{
		return classification;
	}


	private final double score;
	private final boolean classification;
}
