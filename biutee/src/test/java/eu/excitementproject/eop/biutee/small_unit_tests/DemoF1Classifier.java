package eu.excitementproject.eop.biutee.small_unit_tests;
import java.io.File;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.classifiers.LabeledSample;
import eu.excitementproject.eop.biutee.classifiers.LinearTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression.F_alpha_DerivativeCalculator;
import eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression.GradientAscentClassifier;
import eu.excitementproject.eop.biutee.classifiers.f1_logicstic_regression.RecallPrecisionF1;
import eu.excitementproject.eop.biutee.classifiers.scaling.LinearScalingTrainableStorableClassifier;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemInitialization;
import eu.excitementproject.eop.biutee.rteflow.systems.SystemUtils;
import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.script.ScriptFactory;
import eu.excitementproject.eop.biutee.utilities.ConfigurationParametersNames;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamples;
import eu.excitementproject.eop.biutee.utilities.safemodel.SafeSamplesUtils;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @author Asher Stern
 * @since Mar 18, 2012
 *
 */
public class DemoF1Classifier
{
	public static double GAMMA = 100.0;
	
	public static void main(String[] args)
	{
		try
		{
			new LogInitializer(args[0]).init();
			
			DemoF1Classifier app = new DemoF1Classifier(args[0],args[1],args[2]);
			app.run();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	
	public DemoF1Classifier(String configurationFileName, String labeledSamplesFileName, String labeledSamplesTestFileName)
	{
		super();
		this.configurationFileName = configurationFileName;
		this.labeledSamplesFileName = labeledSamplesFileName;
		this.labeledSamplesTestFileName = labeledSamplesTestFileName;
	}


	public void run() throws ClassifierException, OperationException, ConfigurationFileDuplicateKeyException, MalformedURLException, TeEngineMlException, PluginAdministrationException, ConfigurationException, LemmatizerException, IOException, ClassNotFoundException
	{
		logger.info("Init...");
		ExposingSystem sys = new ExposingSystem(configurationFileName, ConfigurationParametersNames.RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME);
		sys.init();
		logger.info("Init done.");
		
		logger.info("Init Script...");
		ScriptFactory scriptFactory = new ScriptFactory(sys.getConfigurationFile(),sys.getTESystemEnvironment().getPluginRegistry(),sys.getTESystemEnvironment());
		OperationsScript<Info, BasicNode> script = scriptFactory.getDefaultScript();
		script.init();
		logger.info("Init Script done.");
		try
		{
			
			GradientAscentClassifier gradientAscentClassifier = new GradientAscentClassifier(0.0, 0.01, new F_alpha_DerivativeCalculator(GAMMA),GAMMA);
			// gradientAscentClassifier.restrict(Constants.INCREASE_PARAMETERS_VALUE_IN_SEARCH_CLASSIFIER, RTESystemsUtils.getGlobalFeatureIndexes());
			// // gradientAscentClassifier.restrict(0.000005, new DummySet<Integer>());
			gradientAscentClassifier.restrict(0.000005, SystemUtils.getGlobalFeatureIndexes());
			LinearTrainableStorableClassifier classifier = new LinearScalingTrainableStorableClassifier(gradientAscentClassifier);
			classifier.setFeaturesNames( ClassifierUtils.extendFeatureNames(Feature.toMapOfNames(), script.getRuleBasesNames()));
			logger.info("Loading training data...");
			SafeSamples safeSamples = SafeSamplesUtils.load(new File(labeledSamplesFileName),  sys.getTESystemEnvironment().getFeatureVectorStructureOrganizer());
			logger.info("Loading training data done.");
			logger.info("Training classifier...");
			classifier.train(safeSamples.getSamples());
			logger.info("Training classifier done.");
			
			logger.info(classifier.descriptionOfTraining());
			
			logger.info("Accuracy (not F1) is: "+String.format("%-4.4f",ClassifierUtils.accuracyOf(classifier, safeSamples.getSamples())));

			logger.info("Results on training: "+getRecallPrecisionF1(classifier, safeSamples.getSamples()).getAllValues());
			
			SafeSamples safeSamplesTest =  SafeSamplesUtils.load(new File(labeledSamplesTestFileName),  sys.getTESystemEnvironment().getFeatureVectorStructureOrganizer());
			logger.info("Results on test: "+getRecallPrecisionF1(classifier, safeSamplesTest.getSamples()).getAllValues());
			
			logger.info("Now calculating F1 with different regularization factors...");
			Map<Double,RecallPrecisionF1> mapRegularizationToF1 = new LinkedHashMap<Double, RecallPrecisionF1>();
			Map<Double,RecallPrecisionF1> mapRegularizationToTestF1 = new LinkedHashMap<Double, RecallPrecisionF1>();
			for (double regularization = 0.01; regularization<= 0.03; regularization+=0.01)
			{
				logger.info("Calculating for regularization factor: "+String.format("%-4.4f",regularization));
				gradientAscentClassifier = new GradientAscentClassifier(regularization, 0.01, new F_alpha_DerivativeCalculator(GAMMA),GAMMA);
				// gradientAscentClassifier.restrict(Constants.INCREASE_PARAMETERS_VALUE_IN_SEARCH_CLASSIFIER, RTESystemsUtils.getGlobalFeatureIndexes());
				gradientAscentClassifier.restrict(0.000005, SystemUtils.getGlobalFeatureIndexes());
				classifier = new LinearScalingTrainableStorableClassifier(gradientAscentClassifier);
				logger.info("Training classifier...");
				classifier.train(safeSamples.getSamples());
				logger.info("Training classifier done.");
				 
				mapRegularizationToF1.put(regularization, getRecallPrecisionF1(classifier, safeSamples.getSamples()));
				
				mapRegularizationToTestF1.put(regularization, getRecallPrecisionF1(classifier, safeSamplesTest.getSamples()));
				
			}
			
			for (Double regKey : mapRegularizationToF1.keySet())
			{
				logger.info(String.format("For regularization %-4.4f: the F1 is %s", regKey,mapRegularizationToF1.get(regKey).toString()));
				logger.info(String.format("For regularization %-4.4f: the test F1 is %s", regKey,mapRegularizationToTestF1.get(regKey).toString()));
			}
			

		}
		finally
		{
			script.cleanUp();
		}
	}
	
	public RecallPrecisionF1 getRecallPrecisionF1(LinearTrainableStorableClassifier classifier, List<LabeledSample> samples) throws ClassifierException
	{
		int truepositive = 0;
		int falsepositive = 0;
		int truenegative = 0;
		int falsenegative = 0;
		for (LabeledSample sample : samples)
		{
			boolean c = classifier.classifyBoolean(sample.getFeatures());
			if (c&&sample.getLabel())
			{
				truepositive++;
			}
			else if ((!c)&&sample.getLabel())
			{
				falsenegative++;
			}
			else if (c&&(!sample.getLabel()))
			{
				falsepositive++;
			}
			else if ((!c)&&(!sample.getLabel()))
			{
				truenegative++;
			}
		}
		
		double precision = ((double)truepositive)/((double)(truepositive+falsepositive));
		double recall = ((double)truepositive)/((double)(truepositive+falsenegative));
		double f1 = 2*recall*precision/(recall+precision);

		return new RecallPrecisionF1(recall,precision,f1,truepositive,falsepositive,truenegative,falsenegative);
	}
	
	private static class ExposingSystem extends SystemInitialization
	{
		public ExposingSystem(String configurationFileName, String configurationModuleName)
		{
			super(configurationFileName, configurationModuleName);
		}
		
		@Override
		public void init() throws ConfigurationFileDuplicateKeyException, MalformedURLException, TeEngineMlException, PluginAdministrationException, ConfigurationException, LemmatizerException, IOException
		{
			super.init();
		}
		
		public ConfigurationFile getConfigurationFile()
		{
			return this.configurationFile;
		}
		
		public TESystemEnvironment getTESystemEnvironment()
		{
			return this.teSystemEnvironment;
		}
	}


	private String configurationFileName;
	private String labeledSamplesFileName;
	private String labeledSamplesTestFileName;
	
	private static final Logger logger = Logger.getLogger(DemoF1Classifier.class);
}
