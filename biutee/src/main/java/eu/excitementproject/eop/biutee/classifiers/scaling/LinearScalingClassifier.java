package eu.excitementproject.eop.biutee.classifiers.scaling;
import java.util.Map;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;


/**
 * An appropriate extension of {@link ScalingClassifier} for {@link LinearTrainableStorableClassifier}.
 * 
 * @author Asher Stern
 * @since Jun 16, 2011
 *
 */
public class LinearScalingClassifier extends ScalingClassifier implements LinearClassifier
{
	public ImmutableMap<Integer, Double> getWeights() throws ClassifierException
	{
		return realLinearClassifier.getWeights();
	}



	public double getThreshold() throws ClassifierException
	{
		return realLinearClassifier.getThreshold();
	}



	public double getProduct(Map<Integer, Double> featureVector) throws ClassifierException
	{
		featureVector = getScaledFeatureVector(featureVector);
		return realLinearClassifier.getProduct(featureVector);
	}

	@Override
	public LinearClassifier getRealClassifier()
	{
		return this.realLinearClassifier;
	}





	protected LinearClassifier realLinearClassifier;
}
