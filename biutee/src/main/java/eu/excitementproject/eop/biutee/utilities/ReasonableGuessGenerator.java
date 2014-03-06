package eu.excitementproject.eop.biutee.utilities;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierFactory;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.systems.FeatureVectorStructureOrganizer;
import eu.excitementproject.eop.common.utilities.math.GaussianPseudoRandomGenerator;
import eu.excitementproject.eop.transformations.utilities.MeanAndStandardDeviation;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Creates a "reasonable-guess" classifier, which is logistic-regression classifier
 * that is trained on "fake" samples. The "fake" samples are drawn from normal
 * distribution (Gaussian distribution).
 * <P>
 * The reasonable-guess classifier is used for the first iteration in the main
 * loop of the training - to search for the "best" proof ("best" according to
 * this reasonable-guess classifier).
 * 
 * See {@link ReasonableGuessCreator}
 * 
 * @author Asher Stern
 * @since Jul 26, 2011
 *
 */
public class ReasonableGuessGenerator
{
	public static final Double DEFAULT_HYPOTHESIS_LENGTH = BiuteeConstants.DEFAULT_HYPOTHESIS_LENGTH_FOR_TRAINING_REASONABLE_GUESS;
	public static final long RANDOM_SEED_FOR_GAUSSIAN_GENERATOR = BiuteeConstants.RANDOM_SEED_FOR_GAUSSIAN_GENERATOR_FOR_REASONABLE_GUESS_TRAINING; 
	
	
	public ReasonableGuessGenerator(
			ClassifierFactory classifierFactory,
			Map<Integer, MeanAndStandardDeviation> priorNegative,
			Map<Integer, MeanAndStandardDeviation> priorPositive,
			FeatureVectorStructureOrganizer featureVectorStructure,
			int negativeSamplesToGenerate, int positiveSamplesToGenerate) throws TeEngineMlException
	{
		super();
		this.classifierFactory = classifierFactory;
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
		LinearTrainableStorableClassifier ret = classifierFactory.getProperClassifierForSearch(false);
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
		gaussianGenerator = new GaussianPseudoRandomGenerator(RANDOM_SEED_FOR_GAUSSIAN_GENERATOR);
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

	private final ClassifierFactory classifierFactory;
	private Map<Integer, MeanAndStandardDeviation> priorNegative;
	private Map<Integer, MeanAndStandardDeviation> priorPositive;
	private FeatureVectorStructureOrganizer featureVectorStructure;
	private int negativeSamplesToGenerate;
	private int positiveSamplesToGenerate;
	
	private GaussianPseudoRandomGenerator gaussianGenerator;
	private Map<Integer,Double> zeroFeatureVector;
}
