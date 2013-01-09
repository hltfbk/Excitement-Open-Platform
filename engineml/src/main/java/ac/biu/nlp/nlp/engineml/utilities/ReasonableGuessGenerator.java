package ac.biu.nlp.nlp.engineml.utilities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.common.utilities.math.GaussianPseudoRandomGenerator;

import ac.biu.nlp.nlp.engineml.classifiers.ClassifierException;
import ac.biu.nlp.nlp.engineml.classifiers.ClassifierFactory;
import ac.biu.nlp.nlp.engineml.classifiers.LabeledSample;
import ac.biu.nlp.nlp.engineml.classifiers.LinearTrainableStorableClassifier;
import ac.biu.nlp.nlp.engineml.rteflow.macro.Feature;
import ac.biu.nlp.nlp.engineml.rteflow.systems.FeatureVectorStructureOrganizer;
import ac.biu.nlp.nlp.engineml.rteflow.systems.RTESystemsUtils;

/**
 * Creates a "reasonable-guess" classifier, which is logistic-regression classifier
 * that is trained on "fake" samples. The "fake" samples are drawn from normal
 * distribution (Gaussian distribution).
 * <P>
 * The reasonable-guess classifier is used for the first iteration in the main
 * loop of the training - to search for the "best" proof ("best" according to
 * this reasonable-guess classifier).
 * 
 * See {@link RTESystemsUtils}
 * 
 * @author Asher Stern
 * @since Jul 26, 2011
 *
 */
public class ReasonableGuessGenerator
{
	public static final Double DEFAULT_HYPOTHESIS_LENGTH = 10.0;
	
	public ReasonableGuessGenerator(
			Map<Integer, MeanAndStandardDeviation> priorNegative,
			Map<Integer, MeanAndStandardDeviation> priorPositive,
			FeatureVectorStructureOrganizer featureVectorStructure,
			int negativeSamplesToGenerate, int positiveSamplesToGenerate) throws TeEngineMlException
	{
		super();
		this.priorNegative = priorNegative;
		this.priorPositive = priorPositive;
		this.featureVectorStructure = featureVectorStructure;
		this.negativeSamplesToGenerate = negativeSamplesToGenerate;
		this.positiveSamplesToGenerate = positiveSamplesToGenerate;
		
		init();
	}
	
	public LinearTrainableStorableClassifier createClassifierByPrior() throws ClassifierException
	{
		Vector<LabeledSample> priorSamples = createPriorSamples();
		LinearTrainableStorableClassifier ret = new ClassifierFactory().getDefaultClassifierForSearch();
		ret.train(priorSamples);
		return ret;
	}
	
	public Vector<LabeledSample> createPriorSamples()
	{
		Vector<LabeledSample> ret = new Vector<LabeledSample>();
		for (int sampleIndex=0;sampleIndex<negativeSamplesToGenerate;++sampleIndex)
		{
			ret.add(new LabeledSample(createNegativePriorSample(), false));
		}
		for (int sampleIndex=0;sampleIndex<positiveSamplesToGenerate;++sampleIndex)
		{
			ret.add(new LabeledSample(createPositivePriorSample(), true));
		}
		return ret;
	}

	/////////////////////// PROTECTED & PRIVATE //////////////////////////
	
	protected void init() throws TeEngineMlException
	{
		gaussianGenerator = new GaussianPseudoRandomGenerator();
		zeroFeatureVector = createZeroFeatureVector();
	}

	protected Map<Integer,Double> createNegativePriorSample()
	{
		return createPriorSample(priorNegative);
	}

	protected Map<Integer,Double> createPositivePriorSample()
	{
		return createPriorSample(priorPositive);
	}

	protected Map<Integer,Double> createPriorSample(Map<Integer, MeanAndStandardDeviation> prior)
	{
		Map<Integer,Double> ret = new LinkedHashMap<Integer, Double>();
		ret.putAll(zeroFeatureVector);
		ret.put(Feature.INVERSE_HYPOTHESIS_LENGTH.getFeatureIndex(), DEFAULT_HYPOTHESIS_LENGTH);
		
		for (Integer featureIndex : prior.keySet())
		{
			ret.put(featureIndex,
					gaussianGenerator.generate(prior.get(featureIndex).getMean(), prior.get(featureIndex).getStandardDeviation()));
		}
		
		return ret;
	}
	
	protected Map<Integer,Double> createZeroFeatureVector() throws TeEngineMlException
	{
		Map<Integer, Double> retZeroFeatureVector = new LinkedHashMap<Integer, Double>();
		
		for (Integer featureIndex : featureVectorStructure.getAllIndexesOfFeatures())
		{
			retZeroFeatureVector.put(featureIndex, 0.0);
		}
		
		
		return retZeroFeatureVector;
	}

	private Map<Integer, MeanAndStandardDeviation> priorNegative;
	private Map<Integer, MeanAndStandardDeviation> priorPositive;
	private FeatureVectorStructureOrganizer featureVectorStructure;
	private int negativeSamplesToGenerate;
	private int positiveSamplesToGenerate;
	
	private GaussianPseudoRandomGenerator gaussianGenerator;
	private Map<Integer,Double> zeroFeatureVector;
}
