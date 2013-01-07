package ac.biu.nlp.nlp.engineml.classifiers.hypothesis_normalize;

import java.util.Vector;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.classifiers.LinearTrainableStorableClassifier;
import ac.biu.nlp.nlp.engineml.classifiers.io.LearningModel;

/**
 * 
 * @author Asher Stern
 * @since Dec 25, 2012
 *
 */
public class HypothesisNoramlizeLinearTrainableStorableClassifier extends HypothesisNoramlizeLinearClassifier implements LinearTrainableStorableClassifier
{
	public HypothesisNoramlizeLinearTrainableStorableClassifier(LinearTrainableStorableClassifier realTrainableClassifier)
	{
		this.realTrainableClassifier = realTrainableClassifier;
		this.realClassifier = this.realTrainableClassifier;
	}
	
	@Override
	public void train(Vector<LabeledSample> samples) throws ClassifierException
	{
		realTrainableClassifier.train(normalizeVectorSamples(samples));
	}
	
	@Override
	public void reset() throws ClassifierException
	{
		realTrainableClassifier.reset();
	}
	
	@Override
	public LearningModel store() throws ClassifierException
	{
		return new LearningModelForHypothesisNormalizeLinearClassifier(HypothesisNoramlizeLinearLoadableClassifier.class.getName(),realTrainableClassifier.store(),descriptionOfTraining());
	}



	
	protected LinearTrainableStorableClassifier realTrainableClassifier;
}
