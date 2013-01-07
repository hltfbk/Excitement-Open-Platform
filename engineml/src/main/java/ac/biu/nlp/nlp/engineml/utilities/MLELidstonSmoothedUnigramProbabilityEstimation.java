package ac.biu.nlp.nlp.engineml.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * This class gives an estimation about any word. The estimation is the probability that the
 * given word will appear in a text. It never returns a 0 or negative estimation.
 * The estimation is done by maximum-likelihood estimation with Lidston smoothing.
 * <P>
 * 
 * All estimations were already calculated and saved in a serialization file. This class only
 * reads this file to the memory, and returns the estimations as needed.
 * <P>
 * 
 * The code that creates that serialization file is (currently) not part of the engineml project.
 * It is temporarily stored in Asher's "stuff" project, and based on a text file sent from
 * Shachar to Asher, based on Reuters corpus.
 * 
 * 
 * @author Asher Stern
 * @since Feb 25, 2011
 *
 */
public class MLELidstonSmoothedUnigramProbabilityEstimation implements UnigramProbabilityEstimation
{
	public static MLELidstonSmoothedUnigramProbabilityEstimation fromSerializedFile(File file) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
		try
		{
			@SuppressWarnings("unchecked")
			Map<String,Double> knownWordsEstimation = (Map<String,Double>) inputStream.readObject();
			double lambda = inputStream.readDouble();
			long vocabularyLength = inputStream.readLong();
			long numberOfAllInstances = inputStream.readLong();
			
			return new MLELidstonSmoothedUnigramProbabilityEstimation(knownWordsEstimation,lambda,vocabularyLength,numberOfAllInstances);
		}
		finally
		{
			inputStream.close();
		}
		
	}
	
	
	public MLELidstonSmoothedUnigramProbabilityEstimation(
			Map<String, Double> knownWordsEstimation, double lambda,
			long vocabularyLength, long numberOfAllInstances)
	{
		super();
		this.knownWordsEstimation = knownWordsEstimation;
		this.lambda = lambda;
		this.vocabularyLength = vocabularyLength;
		this.vocabularyLengthDouble = (double)vocabularyLength;
		
		this.numberOfAllInstances = numberOfAllInstances;
		this.numberOfAllInstancesDouble = (double)numberOfAllInstances;
	}

	public double getEstimationFor(String word)
	{
		if (knownWordsEstimation.containsKey(word))
			return knownWordsEstimation.get(word);
		else
		{
			return lambda/(numberOfAllInstancesDouble+lambda*vocabularyLengthDouble);
		}
	}
	
	private Map<String,Double> knownWordsEstimation;
	private double lambda;
	@SuppressWarnings("unused")
	private long vocabularyLength;
	@SuppressWarnings("unused")
	private long numberOfAllInstances;
	private double numberOfAllInstancesDouble;
	
	private double vocabularyLengthDouble;
}
