package eu.excitementproject.eop.biutee.classifiers;
import jnisvmlight.TrainingParameters;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression.F_alpha_DerivativeCalculator;
import eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression.GradientAscentClassifier;
import eu.excitementproject.eop.biutee.classifiers.hypothesis_normalize.HypothesisNoramlizeLinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.linearimplementations.LinearClassifierByPool;
import eu.excitementproject.eop.biutee.classifiers.linearimplementations.LinearClassifierPool;
import eu.excitementproject.eop.biutee.classifiers.linearimplementations.LogisticRegressionClassifier;
import eu.excitementproject.eop.biutee.classifiers.linearimplementations.SvmLightClassifier;
import eu.excitementproject.eop.biutee.classifiers.scaling.LinearScalingTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemUtils;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;


/**
 * A factory that returns implementations of {@link TrainableClassifier}s. 
 * 
 * @author Asher Stern
 * @since Dec 29, 2010
 *
 */
public class ClassifierFactory
{
	/////////////////////////// PUBLIC //////////////////////////////
	
	public static double SVM_SLACK_COEFFICIENT = BiuteeConstants.SVM_SLACK_COEFFICIENT;
	public static double LOGISTIC_REGRESSION_LEARNING_RATE= 0.005;
	
	public ClassifierFactory(Boolean useF1Classifier)
	{
		this.useF1Classifier = useF1Classifier;
	}
	
	
	public LinearTrainableStorableClassifier getProperClassifier(boolean datasetNatureF1) throws ClassifierException
	{
		return getProperClassifierForSearch(datasetNatureF1);
	}
	
	public LinearTrainableStorableClassifier getProperClassifierForSearch(boolean datasetNatureF1) throws ClassifierException
	{
		boolean f1 = false;
		if (this.useF1Classifier!=null)
		{
			f1 = this.useF1Classifier.booleanValue();
		}
		else
		{
			f1 = datasetNatureF1;
		}
		
		if (f1)
		{
			logger.info("Using F1 optimized classifier");
			return getF1Classifier();
		}
		else
		{
			logger.info("Using accuracy optimized classifier");
			return getByPoolLinearClassifier();
		}
	}
	
	@Deprecated
	public TrainableStorableClassifier getDefaultClassifier() throws ClassifierException
	{
		logger.info("getDefaultClassifier()");
		return getByPoolLinearClassifier();
	}
	
	@Deprecated
	public LinearTrainableStorableClassifier getDefaultClassifierForSearch() throws ClassifierException
	{
		logger.info("getDefaultClassifierForSearch()");
		return getByPoolLinearClassifier();
	}
	
	@Deprecated
	public LinearTrainableStorableClassifier getF1Classifier() throws ClassifierException
	{
		logger.info("getF1Classifier() returns new GradientAscentClassifier(0.0, 0.01, new F_alpha_DerivativeCalculator(Constants.F1_CLASSIFIER_GAMMA_FOR_SIGMOID),Constants.F1_CLASSIFIER_GAMMA_FOR_SIGMOID)");
		GradientAscentClassifier gradientAscentClassifier = new GradientAscentClassifier(0.0, 0.01, new F_alpha_DerivativeCalculator(BiuteeConstants.F1_CLASSIFIER_GAMMA_FOR_SIGMOID),BiuteeConstants.F1_CLASSIFIER_GAMMA_FOR_SIGMOID);
		logger.info("restricting classifier...");
		gradientAscentClassifier.restrict(BiuteeConstants.INCREASE_PARAMETERS_VALUE_IN_F1_CLASSIFIER, SystemUtils.getGlobalFeatureIndexes());
		logger.info("restricting classifier done.");
		LinearTrainableStorableClassifier classifier = new LinearScalingTrainableStorableClassifier(gradientAscentClassifier);
		return classifier;
	}
	

	////////////////////// PRIVATE & PROTECTED ///////////////////////////////
	
	private LinearClassifierByPool<? extends LinearTrainableStorableClassifier> getByPoolLinearClassifier()
	{
		logger.info("getByPoolLinearClassifier()");
		if (BiuteeConstants.USE_HYPOTHESIS_NORMALIZE_CLASSIFIER)
		{
			return new LinearClassifierByPool<HypothesisNoramlizeLinearTrainableStorableClassifier>(poolWithHypothesisNormalization);
		}
		else
		{
			return new LinearClassifierByPool<LinearScalingTrainableStorableClassifier>(pool);	
		}
	}
	
	private static LinearClassifierPool<LinearScalingTrainableStorableClassifier> pool = new LinearClassifierPool<LinearScalingTrainableStorableClassifier>()
	{
		@Override
		protected LinearScalingTrainableStorableClassifier createClassifier() throws ClassifierException
		{
			LinearScalingTrainableStorableClassifier linearScalingClassifier = createDefaultLinearScalingClassifier();
			logger.info("Using LinearScalingClassifier that wraps LogisticRegressionClassifier.");
			return linearScalingClassifier;
		}
	};

	private static LinearClassifierPool<HypothesisNoramlizeLinearTrainableStorableClassifier> poolWithHypothesisNormalization = new LinearClassifierPool<HypothesisNoramlizeLinearTrainableStorableClassifier>()
	{
		@Override
		protected HypothesisNoramlizeLinearTrainableStorableClassifier createClassifier() throws ClassifierException
		{
			LinearScalingTrainableStorableClassifier linearScalingClassifier = createDefaultLinearScalingClassifier();
			logger.info("Using HypothesisNoramlizeLinearClassifier wraps LinearScalingClassifier that wraps LogisticRegressionClassifier.");
			return new HypothesisNoramlizeLinearTrainableStorableClassifier(linearScalingClassifier);
		}
	};

	
//	private static LinearClassifierPool<LinearScalingClassifier> poolScalingWrapsHypothesisNormalize = new LinearClassifierPool<LinearScalingClassifier>()
//	{
//		@Override
//		protected LinearScalingClassifier createClassifier() throws ClassifierException
//		{
//			LogisticRegressionClassifier lrClassifier = createDefaultLogisticRegressionClassifier();
//			HypothesisNoramlizeLinearClassifier hypothesisNoramlizeLinearClassifier = new HypothesisNoramlizeLinearClassifier(lrClassifier);
//			LinearScalingClassifier linearScalingClassifier = new LinearScalingClassifier(hypothesisNoramlizeLinearClassifier);
//			linearScalingClassifier.setDoNotScale(new SingleItemSet<Integer>(Feature.INVERSE_HYPOTHESIS_LENGTH.getFeatureIndex()));
//			logger.info("Using LinearScalingClassifier wraps HypothesisNoramlizeLinearClassifier wraps LogisticRegressionClassifier.");
//			return linearScalingClassifier;
//		}
//	};

	
	protected static LinearScalingTrainableStorableClassifier createDefaultLinearScalingClassifier() throws ClassifierException
	{
		LogisticRegressionClassifier lrClassifier = createDefaultLogisticRegressionClassifier();
		return new LinearScalingTrainableStorableClassifier(lrClassifier);
	}
	
	protected static LogisticRegressionClassifier createDefaultLogisticRegressionClassifier()
	{
		double regularizationFactor = 0.0;
		double learningRate = LOGISTIC_REGRESSION_LEARNING_RATE;

		LogisticRegressionClassifier lrClassifier = new LogisticRegressionClassifier(learningRate,regularizationFactor);
		if (BiuteeConstants.RESTRICT_SEARCH_CLASSIFIER_DURING_TRAINING)
		{
			logger.info("Setting restrictions on weights learned by the classifier (preventing negative weights for most features).");
			lrClassifier.restrict(BiuteeConstants.INCREASE_PARAMETERS_VALUE_IN_SEARCH_CLASSIFIER, SystemUtils.getGlobalFeatureIndexes());
		}
		else
		{
			logger.info("Classifier will not be restricted.");
		}
		return lrClassifier;
	}

	
	
	@SuppressWarnings("unused")
	protected TrainableClassifier getDefaultSvmClassifier() throws ClassifierException
	{
//		TrainingParameters trainingParameters = new TrainingParameters();
		String[] trainingParametersArgs = null;
//		trainingParametersArgs = new String[]{"-t","1","-d","3","-r",STRING_SVM_POLYKERNEL_C_PARAM,"-s","1.0","-c","0.001"};
		trainingParametersArgs = new String[]{"-c",String.format("%.8f", SVM_SLACK_COEFFICIENT)};
		TrainingParameters trainingParameters = new TrainingParameters(trainingParametersArgs);
		
		trainingParameters.getLearningParameters().verbosity=0;
		
		if (logger.isInfoEnabled())
		{
			if (trainingParametersArgs!=null)
			{
				StringBuffer sb = new StringBuffer();
				sb.append("ClassifierFactory.getDefaultSvmClassifier(): Using training parameters:\n");
				for (String args : trainingParametersArgs)
				{
					sb.append(args);
					sb.append(" ");
				}
				logger.info(sb.toString());
			}
			else
			{
				logger.info("ClassifierFactory.getDefaultSvmClassifier(): using default training parameters.");
			}
		}
		
		
		return new SvmLightClassifier(trainingParameters);
	}

	private final Boolean useF1Classifier;
	
	private static Logger logger = Logger.getLogger(ClassifierFactory.class);
}
