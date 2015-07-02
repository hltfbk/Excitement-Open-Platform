package eu.excitementproject.eop.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibLINEAR;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;

import com.aliasi.util.Math;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.scoring.NemexBagOfChunksScoring;
import eu.excitementproject.eop.core.component.scoring.NemexBagOfLemmasScoring;
import eu.excitementproject.eop.core.component.scoring.NemexBagOfWordsScoring;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

public class NemexWekaClassificationEDA implements
		EDABasic<ClassificationTEDecision> {

	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException {

		// initialize the EDA
		initializeEDA(config);

		// initialize the data paths
		initializeTrainDir(config, false);
		initializeTestDir(config);

		// initialize the components
		initializeComponents(config);
 
		// initialize the model
		initializeModel(config, false);

		// initialize classifier
		initializeClassifier(config);

	}

	/**
	 * Initialize which classifier to use, and the path for ARFF file for Weka.
	 * 
	 * @param config
	 *            the configuration
	 * @throws ConfigurationException
	 */
	private void initializeClassifier(CommonConfig config)
			throws ConfigurationException {
		NameValueTable comp = config.getSection(this.getClass().getName());

		wekaArffFile = comp.getString("wekaArffFile");

		if (null == wekaArffFile) {
			throw new ConfigurationException(
					"Please specify the path for ARFF data file.");
		}

		// TODO: check for which classifier to use and initialize accordingly.
	}

	/**
	 * Check if correct EDA is specified in configuration
	 * 
	 * @param config
	 *            the configuration
	 * @throws ConfigurationException
	 */
	private void initializeEDA(CommonConfig config)
			throws ConfigurationException {
		NameValueTable top = config.getSection("PlatformConfiguration");
		if (null == top
				|| !top.getString("activatedEDA").equals(
						this.getClass().getName())) {
			throw new ConfigurationException(
					"Please specify the (correct) EDA.");
		}
		// language = top.getString("language");
		// if (null == language) {
		// // default language would be EN
		// language = "EN";
		// }
	}

	/**
	 * initialize the training data
	 * 
	 * @param config
	 *            the configuration
	 * @param isTrain
	 *            whether it is training or testing
	 * @return
	 * @throws ConfigurationException
	 */
	private void initializeTrainDir(CommonConfig config, boolean isTrain)
			throws ConfigurationException {
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}
		this.trainDIR = EDA.getString("trainDir");
		if (null == trainDIR) {
			if (isTrain) {
				throw new ConfigurationException(
						"Please specify the training data directory.");
			} else {
				logger.warning("Warning: Please specify the training data directory.");
			}
		}

	}

	/**
	 * initialize the testing data
	 * 
	 * @param config
	 *            the configuration
	 * @throws ConfigurationException
	 */
	private void initializeTestDir(CommonConfig config)
			throws ConfigurationException {
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}

		this.testDIR = EDA.getString("testDir");
		if (null == testDIR) {
			logger.warning("Warning: Please specify the testing data directory.");
		}
	}

	/**
	 * initialize the components
	 * 
	 * @param config
	 *            the configuration
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	private void initializeComponents(CommonConfig config)
			throws ConfigurationException, ComponentException {
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}
		String tempComps = EDA.getString("Components");
		if (null == tempComps || 0 == tempComps.trim().length()) {
			throw new ConfigurationException(
					"Wrong configuation: no components contained in the EDA!");
		}
		String[] componentArray = tempComps.split(",");

		// components used in this EDA
		this.components = new ArrayList<ScoringComponent>();

		for (String component : componentArray) {
			NameValueTable comp = config.getSection(component);
			if (null == comp) {
				throw new ConfigurationException(
						"Wrong configuation: didn't find the corresponding setting for the component: "
								+ component);
			}
			if (component.equals("NemexBagOfWordsScoring")) {
				initializeNemexBOWComp(config);
			} else if (component.equals("NemexBagOfLemmasScoring")) {
				initializeNemexBOLComp(config);
			} else if (component.equals("NemexBagOfChunksScoring")) {
				initializeNemexBOChunksComp(config);
			} else {
				try {
					@SuppressWarnings("unchecked")
					Class<? extends ScoringComponent> comp1 = (Class<? extends ScoringComponent>) Class
							.forName("eu.excitementproject.eop.core.component.scoring."
									+ component);
					components.add(comp1.newInstance());
				} catch (Exception e) {
					throw new ConfigurationException(e.getMessage());
				}
			}
		}
	}

	/**
	 * Initialize the Nemex BagOfChunks scoring component.
	 * 
	 * @param config
	 *            the configuration
	 */
	private void initializeNemexBOChunksComp(CommonConfig config) {
		ScoringComponent comp3 = null;
		try {
			comp3 = new NemexBagOfChunksScoring(config);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			logger.info("Configration Exception while initializing Nemex BOChunks Component");
			e.printStackTrace();
		}
		if (((NemexBagOfChunksScoring) comp3).getNumOfFeats() > 0) {
			components.add(comp3);
		}
	}

	/**
	 * Initialize the Nemex BagOfLemmas scoring component.
	 * 
	 * @param config
	 *            the configuration
	 */
	private void initializeNemexBOLComp(CommonConfig config) {
		ScoringComponent comp2 = null;
		try {
			comp2 = new NemexBagOfLemmasScoring(config);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			logger.info("Configration Exception while initializing Nemex BOL Component");
			e.printStackTrace();
		}
		if (((NemexBagOfLemmasScoring) comp2).getNumOfFeats() > 0) {
			components.add(comp2);
		}

	}

	/**
	 * Initialize the Nemex BagOfWords scoring component.
	 * 
	 * @param config
	 *            the configuration
	 */
	private void initializeNemexBOWComp(CommonConfig config) {
		ScoringComponent comp1 = null;
		try {
			comp1 = new NemexBagOfWordsScoring(config);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			logger.info("Configration Exception while initializing Nemex BOW Component");
			e.printStackTrace();
		}
		if (((NemexBagOfWordsScoring) comp1).getNumOfFeats() > 0) {
			components.add(comp1);
		}

	}

	/**
	 * initialize the model
	 * 
	 * @param config
	 *            the configuration
	 * @param isTrain
	 *            whether it is training or testing
	 * @throws ConfigurationException
	 */
	private void initializeModel(CommonConfig config, boolean isTrain)
			throws ConfigurationException {
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}
		modelFile = EDA.getString("modelFile");
		if (isTrain) {
			File file = new File(modelFile);
			if (file.exists()) {
				// keep backup of one old model
				String oldModelFile = modelFile + "_old";
				File oldFile = new File(oldModelFile);
				if (oldFile.exists())
					oldFile.delete();
				file.renameTo(oldFile);
			} else {
				logger.info("The trained model will be stored in "
						+ file.getAbsolutePath());
			}
		} else {
			File file = new File(modelFile);
			if (!file.exists()) {
				throw new ConfigurationException(
						"The model specified in the configuration does NOT exist! Please give the correct file path.");
			}
		}
	}

	/**
	 * Write the headers for Weka ARFF file, including relation names and
	 * attribute names and types.
	 * 
	 * @throws ConfigurationException
	 */
	private void writeArffHeaders() throws ConfigurationException {

		// Create a new Path
		Path arffFile = Paths.get(wekaArffFile);

		try {

			Files.deleteIfExists(arffFile);

			arffFile = Files.createFile(arffFile);

		} catch (IOException ex) {

			System.out.println("Error creating file");

		}

		// Writing to ARFF file
		try (BufferedWriter writer = Files.newBufferedWriter(arffFile,
				Charset.defaultCharset())) {
			writer.append("@RELATION TextEntailment");
			writer.newLine();

			for (ScoringComponent curComp : components) {

				if (curComp.getComponentName() == "NemexBagOfWordsScoring") {
					writeAttributes(writer, "BOW");
				} else if (curComp.getComponentName() == "NemexBagOfLemmasScoring") {
					writeAttributes(writer, "BOL");
				} else if (curComp.getComponentName() == "NemexBagOfChunksScoring") {
					writeAttributes(writer, "BOChunks");

					int numOfFeats = ((NemexBagOfChunksScoring) curComp)
							.getNumOfFeats();
					if (numOfFeats > 7) {
						String[] coverageFeats = ((NemexBagOfChunksScoring) curComp)
								.getCoverageFeats();

						for (int i = 0; i < coverageFeats.length; i++) {
							writer.newLine();
							writer.append("@ATTRIBUTE NemexBOChunks"
									+ coverageFeats[i] + "Overlap NUMERIC");
						}
					}

				}
			}
			writer.newLine();
			writer.append("@ATTRIBUTE class {ENTAILMENT,NONENTAILMENT,ABSTAIN}");
			writer.newLine();
			writer.newLine();
			writer.append("@DATA");

			writer.flush();
		} catch (IOException exception) {
			System.out.println("Error writing to file");
		}

	}

	/**
	 * Write all 7 attribute features common to all scoring components.
	 * 
	 * @param writer
	 *            BuferedWriter for ARFF file.
	 * @param string
	 *            Specifying whether it is bag of words or lemmas or chunks
	 * @throws IOException
	 */
	private void writeAttributes(BufferedWriter writer, String string)
			throws IOException {
		// 7 default features for NemexBOW, NemexBOL, and NemexBOChunks scoring
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "Score1 NUMERIC");
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "Score2 NUMERIC");
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "Score3 NUMERIC");
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "TaskIE NUMERIC");
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "TaskIR NUMERIC");
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "TaskQA NUMERIC");
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "TaskSum NUMERIC");

	}

	@Override
	public ClassificationTEDecision process(JCas aCas) throws EDAException,
			ComponentException {

		String pairId = getPairID(aCas);

		// String goldAnswer = getGoldLabel(aCas);
		// if (null == goldAnswer) {
		// goldAnswer = DecisionLabel.Abstain.toString();
		// }

		// a quick answer "yes" for identical T-H pair as input
		if (isIdenticalPair(aCas)) {
			return new ClassificationTEDecision(DecisionLabel.Entailment, 1d,
					pairId);
		}

		try {
			writeArffHeaders();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String feats = scoreData(aCas);
		writeDatatoArff(feats);

		ClassificationTEDecision pred = classifyData(pairId);

		return pred;
	}

	/**
	 * For each component to be used in the EDA, calculate scores for data.
	 * 
	 * @param aCas
	 *            the T/H pair under consideration.
	 * @return string with all scores from all components and actual class
	 *         label.
	 * @throws ScoringComponentException
	 */
	private String scoreData(JCas aCas) throws ScoringComponentException {

		String feats = new String();
		for (ScoringComponent comp : components) {
			logger.info("Current component:" + comp.getComponentName());
			Vector<Double> scores = comp.calculateScores(aCas);
			for (int i = 0; i < scores.size(); i++) {
				feats += scores.get(i).floatValue() + ",";
			}
		}

		feats += getGoldLabel(aCas);

		return feats;
	}

	/**
	 * Write the calculated scores to Weka ARFF file in required format.
	 * 
	 * @param feats
	 *            string with all scores from all components and actual class
	 */
	private void writeDatatoArff(String feats) {
		// Create a new Path
		Path arffFile = Paths.get(wekaArffFile);

		// Writing to ARFF file
		try (BufferedWriter writer = Files.newBufferedWriter(arffFile,
				Charset.defaultCharset(), StandardOpenOption.APPEND)) {
			writer.newLine();
			writer.append(feats);
		} catch (IOException exception) {
			System.out.println("Error writing to file");
		}
	}

	/**
	 * Classify given T and H pair using trained model file.
	 * 
	 * @param pairId
	 *            ID for pair to classify
	 * @return classification decision - entailment, nonentailment or abstain.
	 */
	private ClassificationTEDecision classifyData(String pairId) {
		Instances testData;

		try {
			testData = loadInstancesFromARFF(wekaArffFile, "class");

			// deserialize model
			classifier = (LibLINEAR) weka.core.SerializationHelper
					.read(modelFile);

			// Not using for loop: for one cas pair, there should be only one
			// instance
			// for (int i = 0; i < testData.numInstances(); i++) {
			double pred = classifier.classifyInstance(testData.instance(0));

			// get the predicted probabilities
			double[] predThreshold = classifier
					.distributionForInstance(testData.instance(0));

			String predictedClass = testData.classAttribute().value((int) pred);

			// System.out.print("actual: "
			// + testData.classAttribute().value(
			// (int) testData.instance(0).classValue()));
			// System.out.println("predicted: " + predictedClass);

			if (predictedClass.equalsIgnoreCase("ENTAILMENT")) {
				return new ClassificationTEDecision(DecisionLabel.Entailment,
						Math.maximum(predThreshold), pairId); // TODO: check if
																// maximum works
			} else if (predictedClass.equalsIgnoreCase("NONENTAILMENT")) {
				return new ClassificationTEDecision(
						DecisionLabel.NonEntailment,
						Math.maximum(predThreshold), pairId);
			} else if (predictedClass.equalsIgnoreCase("ABSTAIN")) {
				return new ClassificationTEDecision(DecisionLabel.Abstain,
						Math.maximum(predThreshold), pairId);
			}

			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void startTraining(CommonConfig config)
			throws ConfigurationException, EDAException, ComponentException {

		// initialize the EDA
		initializeEDA(config);

		// initialize the data paths
		initializeTrainDir(config, true);
		initializeTestDir(config);

		// initialize the components
		initializeComponents(config);

		// initialize the model
		initializeModel(config, true);

		// initialize classifier
		initializeClassifier(config);

		// write headers for weka arff file
		writeArffHeaders();

		// calculate scores and add the data to weka arff file
		generateTrainingDataArff();

		// train the classifier on obtained weka arff data file amd store the
		// model
		trainClassifier();

	}

	/**
	 * write data to Weka ARFF file for all training pairs.
	 * 
	 * @throws ConfigurationException
	 * @throws LAPException
	 * @throws ScoringComponentException
	 */
	private void generateTrainingDataArff() throws ConfigurationException,
			LAPException, ScoringComponentException {

		File f = new File(trainDIR);
		if (f.exists() == false) {
			throw new ConfigurationException("trainDIR:" + f.getAbsolutePath()
					+ " not found!");
		}

		int filesCounter = 0;
		for (File xmi : f.listFiles()) {
			if (!xmi.getName().endsWith(".xmi")) {
				continue;
			}

			JCas cas = PlatformCASProber.probeXmi(xmi, null);
			String feats = scoreData(cas);
			writeDatatoArff(feats);

			filesCounter++;
		}

		if (filesCounter == 0)
			throw new ConfigurationException("trainDIR:" + f.getAbsolutePath()
					+ " empty!");

	}

	/**
	 * Train the required classifier with generated Weka ARFF file.
	 * 
	 */
	private void trainClassifier() {
		Instances data;
		try {
			data = loadInstancesFromARFF(wekaArffFile, "class");
			classifier = new LibLINEAR();
			((LibLINEAR) classifier).setSVMType(new SelectedTag(
					LibLINEAR.SVMTYPE_L2_LR, LibLINEAR.TAGS_SVMTYPE));
			classifier.buildClassifier(data);

			// serialize model
			weka.core.SerializationHelper.write(modelFile, classifier);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Load data instances from Weka ARFF file to memory for classification
	 * training or testing.
	 * 
	 * @param filename
	 *            Weka ARFF data file path.
	 * @param className
	 *            name used in data to denote attribute which is the class
	 * @return data instances
	 * @throws IOException
	 */
	private Instances loadInstancesFromARFF(String filename, String className)
			throws IOException {

		DataSource source;
		try {
			source = new DataSource(filename);
			Instances data = source.getDataSet();
			Attribute classAttribute = data.attribute(className);
			data.setClass(classAttribute);
			return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return return the pairID of the T-H pair
	 */
	protected final String getPairID(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		return p.getPairID();
	}

	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return return whether it is an identical pair or not
	 */
	protected final boolean isIdenticalPair(JCas aCas) {
		FSIterator<TOP> iter = aCas.getJFSIndexRepository().getAllIndexedFS(
				Pair.type);
		if (iter.hasNext()) {
			Pair p = (Pair) iter.next();
			String text = p.getText().getCoveredText();
			String hypothesis = p.getHypothesis().getCoveredText();
			if (text.trim().equals(hypothesis.trim())) {
				return true;
			}
		}
		// empty pair
		return false;
	}

	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return if the T-H pair contains the gold answer, return it; otherwise,
	 *         return null
	 */
	protected final String getGoldLabel(JCas aCas) {
		FSIterator<TOP> pairIter = aCas.getJFSIndexRepository()
				.getAllIndexedFS(Pair.type);
		Pair p = (Pair) pairIter.next();
		if (null == p.getGoldAnswer() || p.getGoldAnswer().equals("")
				|| p.getGoldAnswer().equals("ABSTAIN")) {
			return null;
		} else {
			return p.getGoldAnswer();
		}
	}

	public List<ScoringComponent> getComponents() {
		return components;
	}

	public String getTrainDIR() {
		return trainDIR;
	}

	public String getTestDIR() {
		return testDIR;
	}

	public String getArffFilePath() {
		return wekaArffFile;
	}

	@Override
	public void shutdown() {
		//TODO: shutdown in better way
		components.clear();
		trainDIR = "";
		testDIR = "";
	}

	/**
	 * the logger
	 */
	public final static Logger logger = Logger
			.getLogger(MaxEntClassificationEDA.class.getName());

	/**
	 * list of components used in this EDA
	 */
	private List<ScoringComponent> components;

	/**
	 * the training data directory
	 */
	private String trainDIR;

	/**
	 * the testing data directory
	 */
	private String testDIR;

	/**
	 * the Arff file path
	 */
	private String wekaArffFile;

	/**
	 * File path for model file for classifier
	 */
	private String modelFile;

	/**
	 * Weka classifier object
	 */
	Classifier classifier;
}
