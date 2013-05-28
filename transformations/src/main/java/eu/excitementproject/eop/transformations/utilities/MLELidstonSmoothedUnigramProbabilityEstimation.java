package eu.excitementproject.eop.transformations.utilities;
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
 * The usual was to instantiate this class is by calling the static method
 * {@link #fromSerializedFile(File)}.
 * Excitement customers can get the input file from the run-time environment of
 * BIUTEE (which is shipped along with EOP distribution).
 * <P>
 * Bar-Ilan students can get the input file from \\qa-srv\data\RESOURCES\UnigramProbabilities.
 * Either "Reuters-PPD_unigram.ser" or "unigram_new.ser" can be used.
 * <BR>
 * The code by which "unigram_new.ser" was created is in "biutee" project
 * (the class is eu.excitementproject.eop.biutee.utilities.unigram.UnigramCreator).
 * The input was Reuters corpus (CD1 and CD2).
 * <BR>
 * The code that created "Reuters-PPD_unigram.ser" is (currently) not part of EOP.
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
