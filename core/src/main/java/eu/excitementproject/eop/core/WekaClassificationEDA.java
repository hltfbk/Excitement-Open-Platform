package eu.excitementproject.eop.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.uima.jcas.JCas;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.scoring.BagOfLexesScoring;
import eu.excitementproject.eop.core.component.scoring.BagOfWordsScoring;
import eu.excitementproject.eop.core.component.scoring.ScoringComponent;
import eu.excitementproject.eop.core.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

public class WekaClassificationEDA extends ClassificationEDA {

	public List<ScoringComponent> getComponents() {
		return components;
	}

	public List<String> getAnswerList() {
		return answerList;
	}

	public int getNumOfFeats() {
		return numOfFeats;
	}

	public ArrayList<Attribute> getAttrList() {
		return attrList;
	}

	public String getLanguage() {
		return language;
	}

	public String getModelFile() {
		return modelFile;
	}

	public String getTrainDIR() {
		return trainDIR;
	}

	public String getTestDIR() {
		return testDIR;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	// list of components used in this EDA
	private List<ScoringComponent> components;

	// list of possible answers used in this EDA
	private List<String> answerList;

	// the number of features
	private int numOfFeats;

	// the feature list
	private ArrayList<Attribute> attrList;

	// language flag
	private String language;

	// the model file, consisting of parameter name and value pairs
	private String modelFile;

	// training data directory
	private String trainDIR;

	// testing data directory
	private String testDIR;

	// the classifier
	private Classifier classifier;

	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException {
		super.initialize(config);
		components = new ArrayList<ScoringComponent>();
		components.add(new BagOfWordsScoring());
		components.add(new BagOfLexesScoring());

		answerList = new ArrayList<String>();
		answerList.add(DecisionLabel.Abstain.toString());
		answerList.add(DecisionLabel.Contradiction.toString());
		answerList.add(DecisionLabel.Entailment.toString());
		answerList.add(DecisionLabel.NonEntailment.toString());
		answerList.add(DecisionLabel.Paraphrase.toString());
		answerList.add(DecisionLabel.Unknown.toString());

		numOfFeats = 0;
		for (ScoringComponent component : components) {
			numOfFeats += ((BagOfWordsScoring) component).getNumOfFeats();
		}

		attrList = new ArrayList<Attribute>(numOfFeats + 1);
		for (ScoringComponent component : components) {
			for (int i = 0; i < ((BagOfWordsScoring) component).getNumOfFeats(); i++) {
				Attribute attr = new Attribute(component.getComponentName()
						+ "_" + i);
				attrList.add(attr);
			}
		}
		attrList.add(new Attribute("Answer", answerList));

		language = "EN";

		modelFile = "./src/test/resources/WekaClassificationEDAModel"
				+ language;

		trainDIR = "./target/EN/";

		testDIR = "./target/EN/";

		classifier = new NaiveBayes();

		// deserialize model
		try {
			Classifier cls = (Classifier) SerializationHelper.read(modelFile);
			if (!cls.getClass().equals(classifier.getClass())) {
				throw new ConfigurationException(
						"The classifier specified in the model doesn't match the classifier in the configuration!");
			}
			classifier = cls;
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage());
		}
	}

	@Override
	public ClassificationTEDecision process(JCas aCas) throws EDAException,
			ComponentException {
		String pairId = getPairID(aCas);
		String goldAnswer = getGoldLabel(aCas);
		if (null == goldAnswer) {
			goldAnswer = DecisionLabel.Abstain.toString();
		}

		Instances dataset = new Instances(
				WekaClassificationEDA.class.getName(), attrList, 1);
		dataset.setClassIndex(dataset.numAttributes() - 1);

		Double answer = 0.0d;
		try {
			Instance inst = casToInstance(aCas);
			inst.setDataset(dataset);
			dataset.add(inst);

			answer = classifier.classifyInstance(inst);
		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}

		String result = answerList.get(answer.intValue());

		return new ClassificationTEDecision(DecisionLabel.valueOf(result),
				pairId);
	}

	@Override
	public void shutdown() {
		super.shutdown();
		components.clear();
		answerList.clear();
		numOfFeats = 0;
		attrList.clear();
		language = "";
		modelFile = "";
		trainDIR = "";
		testDIR = "";
		classifier = null;
	}

	@Override
	public void startTraining(CommonConfig c) throws ConfigurationException,
			EDAException, ComponentException {
		try {
			classifier.buildClassifier(readInXmiFiles(trainDIR));

			// serialize model
			SerializationHelper.write(modelFile, classifier);

		} catch (Exception e) {
			throw new EDAException(e.getMessage());
		}
	}

	/**
	 * @param filePath
	 *            the xmi file or directory path of the dataset
	 * @return return the instances of the dataset
	 */
	protected Instances readInXmiFiles(String filePath)
			throws ConfigurationException {
		Instances dataset = null;
		try {
			File dir = new File(filePath);
			Instance inst = null;
			if (dir.isFile()) {
				// create the dataset
				dataset = new Instances(WekaClassificationEDA.class.getName(),
						attrList, 1);
				dataset.setClassIndex(dataset.numAttributes() - 1);

				// add the instance to the dataset
				inst = readInXmiFile(dir.getAbsolutePath());
				inst.setDataset(dataset);
				dataset.add(inst);
			} else if (dir.isDirectory()) {
				// create the dataset
				dataset = new Instances(WekaClassificationEDA.class.getName(),
						attrList, dir.listFiles().length);
				dataset.setClassIndex(dataset.numAttributes() - 1);

				for (File file : dir.listFiles()) {
					// ignore all the non-xmi files
					if (!file.getName().endsWith(".xmi")) {
						continue;
					}
					// add the instance to the dataset
					inst = readInXmiFile(file.getAbsolutePath());
					inst.setDataset(dataset);
					dataset.add(inst);
				}
			}
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage());
		}
		return dataset;
	}

	/**
	 * @param filePath
	 *            the single xmi file path of the dataset
	 * @return return the instance of that file
	 */
	protected Instance readInXmiFile(String filePath)
			throws ConfigurationException {
		Instance inst = null;
		try {
			File xmiFile = new File(filePath);
			inst = casToInstance(PlatformCASProber
					.probeXmi(xmiFile, System.out));
		} catch (LAPException e) {
			throw new ConfigurationException(e.getMessage());
		}
		return inst;
	}

	/**
	 * @param cas
	 *            the JCas object
	 * @return return the data <code>Instance</code> of the input JCas
	 */
	protected Instance casToInstance(JCas cas) throws ConfigurationException {
		Instance inst = null;
		try {
			String goldAnswer = getGoldLabel(cas);
			if (null == goldAnswer) {
				goldAnswer = DecisionLabel.Abstain.toString();
			}
			inst = new DenseInstance(numOfFeats + 1);
			int index = 0;
			for (ScoringComponent component : components) {
				Vector<Double> scoreVector = component.calculateScores(cas);
				for (int i = 0; i < scoreVector.size(); i++) {
					inst.setValue(index + i, scoreVector.get(i));
				}
				index += scoreVector.size();
			}
			inst.setValue(index, answerList.indexOf(goldAnswer));
		} catch (ScoringComponentException e) {
			throw new ConfigurationException(e.getMessage());
		}
		return inst;
	}

}
