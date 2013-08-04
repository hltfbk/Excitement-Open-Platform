package eu.excitementproject.eop.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ConverterUtils.DataSource;
import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.scoring.DKProSimilarityScoring;
import eu.excitementproject.eop.lap.PlatformCASProber;


/**
 * More advanced EDA based on DKPro Similarity. Supports multiple text similarity
 * measures (i.e. {@link DKProSimilarityScoring} components) in parallel. The
 * combination of the measures is done using a machine learning classifier from
 * the Weka toolkit. For illustration purposes, we currently use a simple Naive
 * Bayes classifier. 
 */
public class DKProSimilarityClassificationEDA
	extends DKProSimilarityEDA_ImplBase<TEDecision>
{
	private final static Logger logger = Logger.getLogger(DKProSimilarityClassificationEDA.class.getName());
	
	private File trainDir;
	private File modelDir;
	
	private Set<String> trainGoldAnswers;
	
	private Classifier classifier;
	
	/**
	 * Initialization for the test setting
	 */
	@Override
	public void initialize(CommonConfig config)
		throws ConfigurationException, EDAException, ComponentException
	{
		super.initialize(config);
		initializeEDA(config);
		initializeModel(config);
	}
	
	/**
	 * Initialization for the train setting
	 */
	public void initializeTrain(CommonConfig config)
		throws ConfigurationException, EDAException, ComponentException
	{
		super.initialize(config);
		initializeEDA(config);
	}
	
	/**
	 * Common initialization for the EDA
	 */
	private void initializeEDA(CommonConfig config)
		throws ConfigurationException, ComponentException
	{
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage() + " No EDA configuration section.");
		}
		
		trainDir = EDA.getDirectory("trainDir");
		modelDir = EDA.getDirectory("modelDir");
		
		trainGoldAnswers = new HashSet<String>();
	}
	
	/**
	 * Builds the classifier for the given training model
	 */
	private void initializeModel(CommonConfig config)
		throws ConfigurationException
	{
		// Train the classifier
		logger.info("Training the classifier...");
		
		File arffFile = new File(modelDir + "/" + this.getClass().getSimpleName() + ".arff");
		
		classifier = new NaiveBayes();
		try {
			Instances data = DataSource.read(arffFile.getAbsolutePath());
			data.setClassIndex(data.numAttributes() - 1);
			
			classifier.buildClassifier(data);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
	
	@Override
	public ClassificationTEDecision process(JCas jcas)
		throws EDAException, ComponentException
	{
		Pair pair = JCasUtil.selectSingle(jcas, Pair.class);
		
		// Read gold answers
		File goldAnswersFile = new File(modelDir + "/" + this.getClass().getSimpleName() + "_goldAnswers.txt");
		FastVector goldAnswers;
		try {
			List<String> lines = FileUtils.readLines(goldAnswersFile);
			goldAnswers = new FastVector(lines.size());
			
			for (String line : lines)
				goldAnswers.addElement(line);
			
		} catch (IOException e) {
			throw new EDAException(e);
		}
		
		// Generate the similarity features
		List<Double> scores = getFeatures(jcas);
		
		// Define the attributes
		FastVector attrs = new FastVector();

		for (int i = 0; i < getComponents().size(); i++)
		{
			ScoringComponent component = getComponents().get(i);
			attrs.addElement(new Attribute(component.getComponentName()));
		}
		attrs.addElement(new Attribute("gold", goldAnswers));
		
		// Build up the "dataset" which contains a single instance
		Instances instances = new Instances("EOP", attrs, 1);
		instances.setClassIndex(getComponents().size());

		Instance instance = new SparseInstance(scores.size() + 1);
		for (int i = 0; i < scores.size(); i++)
		{
			Double score = scores.get(i);
			instance.setValue((Attribute) attrs.elementAt(i), score);
		}
		instance.setValue((Attribute) attrs.elementAt(attrs.size() - 1), 0);	// gold
		instances.add(instance);

		// Classify the unlabeled instance (i.e. the given text pair)
		double result;
		try {
			result = classifier.classifyInstance(instances.firstInstance());
		} catch (Exception e) {
			throw new EDAException(e);
		}
		
		// Determine the classifier's confidence with the decision
		Double confidence = -1.0;
		try {
			confidence = Math.max(
					classifier.distributionForInstance(instances.firstInstance())[0],
					classifier.distributionForInstance(instances.firstInstance())[1]);
		} catch (Exception e) {
			throw new EDAException(e);
		}
		
		// Determine the result label
		String label = instances.firstInstance().classAttribute().value(new Double(result).intValue()).toLowerCase();
		
		// Convert to a DecisionLabel instance
		DecisionLabel dLabel;
		if (label.toUpperCase().equals(DecisionLabel.Entailment.toString().toUpperCase()))
			dLabel = DecisionLabel.Entailment;
		else
			dLabel = DecisionLabel.NonEntailment;
		
		return new ClassificationTEDecision(dLabel,
				confidence,
				pair.getPairID());
	}
	
	/**
	 * Computes text similarity for all components by passing the current JCas to
	 * each of the components.
	 */
	private List<Double> getFeatures(JCas jcas)
		throws ScoringComponentException
	{
		List<Double> scores = new ArrayList<Double>();
		
		for (ScoringComponent component : getComponents())
		{
			Vector<Double> subscores = component.calculateScores(jcas);
			
			// Each DKProSimilarityScoring component will only return a single score
			scores.addAll(subscores);
		}
		
		return scores;
	}

	@Override
	public void shutdown()
	{
		// nothing to do
	}

	/**
	 * Trains the classifier. Therefore, text similarity features are generated
	 * for all training documents, and finally stored as an ARFF file which will
	 * then be read in the test phase to build the classifier.
	 */
	@Override
	public void startTraining(CommonConfig config)
		throws ConfigurationException, EDAException, ComponentException
	{
		logger.info("Starting training...");
		
		// TODO: Why do we have to initialize here?? Imho this is a strange architectural decision...
		initializeTrain(config);
		
		// Process all training files
		Map<Integer, List<Double>> features = new HashMap<Integer, List<Double>>();
		Map<Integer, String> goldAnswers = new HashMap<Integer, String>();
		
		for (File xmi : FileUtils.listFiles(trainDir, new String[] { "xmi" }, false))
		{
			JCas jcas = PlatformCASProber.probeXmi(xmi, null);
			
			// Compute similarity scores
			List<Double> scores = getFeatures(jcas);
			
			// Get gold annotation
			Pair pair = JCasUtil.selectSingle(jcas, Pair.class);
			String goldAnswer = pair.getGoldAnswer();
			
			// Store feature vector in the feature map
			features.put(Integer.parseInt(pair.getPairID()), scores);
			
			// Store gold answer
			goldAnswers.put(Integer.parseInt(pair.getPairID()), goldAnswer);
			trainGoldAnswers.add(goldAnswer);
		}
		
		// Convert to ARFF file
		File arffFile = new File(modelDir + "/" + this.getClass().getSimpleName() + ".arff");
		
		String arff = toArffString(features, goldAnswers);
		try {
			FileUtils.writeStringToFile(arffFile, arff);
		} catch (IOException e) {
			throw new EDAException(e);
		}
		
		// Store class labels
		File goldAnswersFile = new File(modelDir + "/" + this.getClass().getSimpleName() + "_goldAnswers.txt");
		
		StringBuilder sb = new StringBuilder();
		for (String goldAnswer : trainGoldAnswers)
			sb.append(goldAnswer + LF);
		
		try {
			FileUtils.writeStringToFile(goldAnswersFile, sb.toString());
		} catch (IOException e) {
			throw new EDAException(e);
		}
	}
	
	private String toArffString(Map<Integer, List<Double>> featureMap,
			Map<Integer, String> goldAnswers)
	{
		// Create the Arff header
		StringBuilder arff = new StringBuilder();
		arff.append("@relation EOP" + LF);
		arff.append(LF);
		
		// // Add the attributes to the Arff header
		for (int featureNo = 0; featureNo < featureMap.get(1).size(); featureNo++)
		{
			String feature = getComponents().get(featureNo).getComponentName();
			arff.append("@attribute " + feature + " numeric" + LF);
		}
		
		// Add gold attribute to attribute list in header
		// We also need to do this for unlabeled data
		arff.append("@attribute gold { ");
		List<String> trainGoldAnswersList = new ArrayList<String>(trainGoldAnswers);		
		for (int i = 0; i < trainGoldAnswersList.size(); i++)
		{
			arff.append(trainGoldAnswersList.get(i));
			if (i < trainGoldAnswersList.size() - 1)
				arff.append(", ");
		}
		arff.append(" }" + LF);
		arff.append(LF);
		
		// Add the data
		arff.append("@data" + LF);
				
		for (int i = 1; i <= featureMap.keySet().size(); i++)
		{		
			String dataItem = StringUtils.join(featureMap.get(i), ",");
			
			dataItem += "," + goldAnswers.get(i);
			
			arff.append(dataItem + LF);
		}
				
		return arff.toString();
	}
}
