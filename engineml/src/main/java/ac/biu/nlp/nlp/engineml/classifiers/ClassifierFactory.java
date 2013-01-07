package ac.biu.nlp.nlp.engineml.classifiers;

import jnisvmlight.TrainingParameters;

import org.apache.log4j.Logger;

import ac.biu.nlp.nlp.engineml.classifiers.f1_logicstic_regression.F_alpha_DerivativeCalculator;
import ac.biu.nlp.nlp.engineml.classifiers.f1_logicstic_regression.GradientAscentClassifier;
import ac.biu.nlp.nlp.engineml.classifiers.hypothesis_normalize.HypothesisNoramlizeLinearTrainableStorableClassifier;
import ac.biu.nlp.nlp.engineml.classifiers.linearimplementations.LinearClassifierByPool;
import ac.biu.nlp.nlp.engineml.classifiers.linearimplementations.LinearClassifierPool;
import ac.biu.nlp.nlp.engineml.classifiers.linearimplementations.LogisticRegressionClassifier;
import ac.biu.nlp.nlp.engineml.classifiers.linearimplementations.SvmLightClassifier;
import ac.biu.nlp.nlp.engineml.classifiers.scaling.LinearScalingTrainableStorableClassifier;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.engineml.rteflow.systems.RTESystemsUtils;

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
	
	public static double SVM_SLACK_COEFFICIENT = Constants.SVM_SLACK_COEFFICIENT;
	public static double LOGISTIC_REGRESSION_LEARNING_RATE= 0.005;
	
	public TrainableStorableClassifier getDefaultClassifier() throws ClassifierException
	{
		logger.info("getDefaultClassifier()");
		return getByPoolLinearClassifier();
	}
	
	public LinearTrainableStorableClassifier getDefaultClassifierForSearch() throws ClassifierException
	{
		logger.info("getDefaultClassifierForSearch()");
		return getByPoolLinearClassifier();
	}
	
	
	public LinearTrainableStorableClassifier getF1Classifier() throws ClassifierException
	{
		logger.info("getF1Classifier() returns new GradientAscentClassifier(0.0, 0.01, new F_alpha_DerivativeCalculator(Constants.F1_CLASSIFIER_GAMMA_FOR_SIGMOID),Constants.F1_CLASSIFIER_GAMMA_FOR_SIGMOID)");
		GradientAscentClassifier gradientAscentClassifier = new GradientAscentClassifier(0.0, 0.01, new F_alpha_DerivativeCalculator(Constants.F1_CLASSIFIER_GAMMA_FOR_SIGMOID),Constants.F1_CLASSIFIER_GAMMA_FOR_SIGMOID);
		logger.info("restricting classifier...");
		gradientAscentClassifier.restrict(Constants.INCREASE_PARAMETERS_VALUE_IN_F1_CLASSIFIER, RTESystemsUtils.getGlobalFeatureIndexes());
		logger.info("restricting classifier done.");
		LinearTrainableStorableClassifier classifier = new LinearScalingTrainableStorableClassifier(gradientAscentClassifier);
		return classifier;
	}
	

	////////////////////// PRIVATE & PROTECTED ///////////////////////////////
	
	private LinearClassifierByPool<? extends LinearTrainableStorableClassifier> getByPoolLinearClassifier()
	{
		logger.info("getByPoolLinearClassifier()");
		if (Constants.USE_HYPOTHESIS_NORMALIZE_CLASSIFIER)
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
		if (Constants.RESTRICT_SEARCH_CLASSIFIER_DURING_TRAINING)
		{
			logger.info("Setting restrictions on weights learned by the classifier (preventing negative weights for most features).");
			lrClassifier.restrict(Constants.INCREASE_PARAMETERS_VALUE_IN_SEARCH_CLASSIFIER, RTESystemsUtils.getGlobalFeatureIndexes());
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
	
	private static Logger logger = Logger.getLogger(ClassifierFactory.class);

}
