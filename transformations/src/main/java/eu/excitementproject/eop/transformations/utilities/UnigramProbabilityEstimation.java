package eu.excitementproject.eop.transformations.utilities;


/**
 * 
 * Gives an estimation about every word. The estimation is typically the probability that
 * such a word will appear in a text (Unigram model).
 * It should not return 0 or negative value to any word.
 * 
 * @author Asher Stern
 * @since Feb 25, 2011
 *
 */
public interface UnigramProbabilityEstimation
{
	public double getEstimationFor(String word);

}
