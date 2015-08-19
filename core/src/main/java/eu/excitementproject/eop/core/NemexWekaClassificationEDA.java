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

import org.apache.log4j.Logger;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibLINEAR;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.scoring.BagOfChunkVectorScoring;
import eu.excitementproject.eop.core.component.scoring.BagOfWordVectorScoring;
import eu.excitementproject.eop.core.component.scoring.NegationScoring;
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
		initializeTrainConfig(config, false); // TODO: check if required
		initializeTestDir(config);

		// initialize the components
		try {
			initializeComponents(config);
		} catch (IOException e) {
			throw new ComponentException(e.getMessage());
		} catch (LexicalResourceException e) {
			throw new ComponentException(e.getMessage());
		}

		// initialize classifier
		initializeClassifier(config);

		// initialize the model
		initializeModel(config, false);

	}

	/**
	 * Initialize which classifier to use, the path for ARFF file for Weka, and
	 * whether data has been split into multiple parts or not.
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
					"Please specify the path for ARFF data file without file extension.");
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
	private void initializeTrainConfig(CommonConfig config, boolean isTrain)
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
				logger.warn("Warning: Please specify the training data directory.");
			}
		}

		this.split = Boolean.parseBoolean(EDA.getString("dataSplit"));

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
			logger.warn("Warning: Please specify the testing data directory.");
		}
	}

	/**
	 * initialize the components
	 * 
	 * @param config
	 *            the configuration
	 * @throws ConfigurationException
	 * @throws ComponentException
	 * @throws IOException
	 * @throws LexicalResourceException
	 */
	private void initializeComponents(CommonConfig config)
			throws ConfigurationException, ComponentException, IOException,
			LexicalResourceException {
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
			} else if (component.equals("BagOfWordVectorScoring")) {
				initializeBOWordVecComp(config);
			} else if (component.equals("BagOfChunkVectorScoring")) {
				initializeBOChunkVecComp(config);
			} else if (component.equals("NegationScoring")) {
				initializeNegComp(config);
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
	 * Initialize component for negation scoring
	 * 
	 * @param config
	 *            Configuration file
	 * @throws IOException
	 */
	private void initializeNegComp(CommonConfig config) throws IOException {
		ScoringComponent comp = null;
		try {
			comp = new NegationScoring(config);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		}
		if (((NegationScoring) comp).getNumOfFeats() > 0) {
			components.add(comp);
		}
	}

	/**
	 * Initialize the BagOfChunkVectorScoring component.
	 * 
	 * @param config
	 *            Configuration file.
	 * @throws IOException
	 * @throws LexicalResourceException
	 */
	private void initializeBOChunkVecComp(CommonConfig config)
			throws IOException, LexicalResourceException {
		ScoringComponent comp = null;
		try {
			comp = new BagOfChunkVectorScoring(config);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
		}
		if (((BagOfChunkVectorScoring) comp).getNumOfFeats() > 0) {
			components.add(comp);
		}

	}

	/**
	 * Initialize the BagOfWordVectorScoring component.
	 * 
	 * @param config
	 *            The configuration file.
	 * @throws IOException
	 */
	private void initializeBOWordVecComp(CommonConfig config)
			throws IOException {
		ScoringComponent comp = null;
		try {
			comp = new BagOfWordVectorScoring(config);
		} catch (ConfigurationException e) {
			logger.warn(e.getMessage());
		}
		if (((BagOfWordVectorScoring) comp).getNumOfFeats() > 0) {
			components.add(comp);
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
			// For training, numOfModelFiles initialized before initializing
			// model files
			for (int i = 0; i < numOfModelFiles; i++) {
				File file = new File(modelFile + String.valueOf(i));
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
			}
		} else {
			// For testing, numOfModelFiles specified via configuration file.
			this.numOfModelFiles = Integer.parseInt(EDA
					.getString("numOfModelFiles"));
			for (int i = 0; i < numOfModelFiles; i++) {
				File file = new File(modelFile);
				if (!file.exists()) {
					throw new ConfigurationException(
							"The model specified in the configuration does NOT exist! Please give the correct file path.");
				}
			}
		}
	}

	/**
	 * Write the headers for all Weka ARFF file, including relation names and
	 * attribute names and types.
	 * 
	 * @param isTrain
	 *            whether it is in training mode
	 * @throws ConfigurationException
	 */
	private void writeArffHeaders(boolean isTrain)
			throws ConfigurationException {

		int numOfWekaFiles; // num of weka data files
		if (isTrain)
			numOfWekaFiles = numOfModelFiles;
		else
			numOfWekaFiles = 1;

		// write headers to all weka files
		for (int i = 0; i < numOfWekaFiles; i++) {
			// Create a new Path
			Path arffFile = Paths.get(wekaArffFile + String.valueOf(i)
					+ ".arff");

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

					if (curComp.getComponentName().equalsIgnoreCase(
							"NemexBagOfWordsScoring")) {
						writeAttributesNumAlignments(writer, "BOW");
						writeAttributesTask(writer, "BOW");
					} else if (curComp.getComponentName().equalsIgnoreCase(
							"NemexBagOfLemmasScoring")) {
						writeAttributesNumAlignments(writer, "BOL");
						writeAttributesTask(writer, "BOL");
					} else if (curComp.getComponentName().equalsIgnoreCase(
							"NemexBagOfChunksScoring")) {
						writeAttributesNumAlignments(writer, "BOChunks");
						writeAttributesTask(writer, "BOChunks");

						int numOfFeats = ((NemexBagOfChunksScoring) curComp)
								.getNumOfFeats();
						if (numOfFeats > 7) {
							String[] coverageFeats = ((NemexBagOfChunksScoring) curComp)
									.getCoverageFeats();

							for (int j = 0; j < coverageFeats.length; j++) {
								writer.newLine();
								writer.append("@ATTRIBUTE NemexBOChunks"
										+ coverageFeats[j] + "Overlap NUMERIC");
							}
						}

					} else if (curComp.getComponentName().equalsIgnoreCase(
							"BagOfWordVectorScoring")) {
						writeAttributesNumAlignments(writer, "BOWVec");
					}

					else if (curComp.getComponentName().equalsIgnoreCase(
							"BagOfChunkVectorScoring")) {
						// writeAttributesNumAlignments(writer, "BOChunkVec");
						writer.newLine();
						writer.append("@ATTRIBUTE BOChunkVecNegAlignment NUMERIC");
						writer.newLine();
						writer.append("@ATTRIBUTE BOChunkVecPositiveAlignment NUMERIC");
					} else if (curComp.getComponentName().equalsIgnoreCase(
							"NegationScoring")) {
						writer.newLine();
						writer.append("@ATTRIBUTE NegationScore NUMERIC");
					}
				}
				writer.newLine();
				writer.append("@ATTRIBUTE class {ENTAILMENT,NONENTAILMENT}");
				writer.newLine();
				writer.newLine();
				writer.append("@DATA");

				writer.flush();
			} catch (IOException exception) {
				System.out.println("Error writing to file");
			}
		}
	}

	/**
	 * Write all 3 attribute features common to all scoring components.
	 * 
	 * @param writer
	 *            BuferedWriter for ARFF file.
	 * @param string
	 *            Specifying whether it is bag of words or lemmas or chunks
	 * @throws IOException
	 */
	private void writeAttributesNumAlignments(BufferedWriter writer,
			String string) throws IOException {
		// 3 default features for all scoring components
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "Score1 NUMERIC");
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "Score2 NUMERIC");
		writer.newLine();
		writer.append("@ATTRIBUTE Nemex" + string + "Score3 NUMERIC");
	}

	/**
	 * Write all 4 attribute features related to concerned task.
	 * 
	 * @param writer
	 *            BuferedWriter for ARFF file.
	 * @param string
	 *            Specifying whether it is bag of words or lemmas or chunks
	 * @throws IOException
	 */
	private void writeAttributesTask(BufferedWriter writer, String string)
			throws IOException {
		// 4 scores for task
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

		String goldAnswer = getGoldLabel(aCas);
		if (null == goldAnswer) {
			goldAnswer = DecisionLabel.Abstain.toString();
		}

		// a quick answer "yes" for identical T-H pair as input
		if (isIdenticalPair(aCas)) {
			return new ClassificationTEDecision(DecisionLabel.Entailment, 1d,
					pairId);
		}

		try {
			writeArffHeaders(false);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String feats = scoreData(aCas);
		feats += goldAnswer; // class label
		writeDatatoArff(feats, wekaArffFile + String.valueOf(0) + ".arff");

		ClassificationTEDecision pred = classifyData(pairId);

		return pred;
	}

	/**
	 * For each component to be used in the EDA, calculate scores for data.
	 * 
	 * @param aCas
	 *            the T/H pair under consideration.
	 * @return string with all scores from all components
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

		return feats;
	}

	/**
	 * Write the calculated scores to Weka ARFF file in required format.
	 * 
	 * @param feats
	 *            string with all scores from all components and actual class
	 */
	private void writeDatatoArff(String feats, String wekaFileName) {

		// Create a new Path
		Path arffFile = Paths.get(wekaFileName);

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
	 * @return classification decision - entailment or nonentailment.
	 */
	private ClassificationTEDecision classifyData(String pairId) {

		int ent = 0, nent = 0; // no. of models predicting entailing and non
								// entailing classes resp.

		// get decision label using all model files
		for (int i = 0; i < numOfModelFiles; i++) {

			Instances testData;

			try {
				testData = loadInstancesFromARFF(
						wekaArffFile + String.valueOf(0) + ".arff", "class");

				// deserialize model
				classifier = (LibLINEAR) weka.core.SerializationHelper
						.read(modelFile + String.valueOf(i));

				double pred = classifier.classifyInstance(testData.instance(0));

				// get the predicted probabilities
				// double[] predThreshold = classifier
				// .distributionForInstance(testData.instance(0));

				String predictedClass = testData.classAttribute().value(
						(int) pred);

				if (predictedClass.equalsIgnoreCase("ENTAILMENT"))
					ent++;
				else if (predictedClass.equalsIgnoreCase("NONENTAILMENT"))
					nent++;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Ensemble learning: Bagging to return most frequently predicted label
		if (ent >= nent)
			return new ClassificationTEDecision(DecisionLabel.Entailment,
					pairId);
		else
			return new ClassificationTEDecision(DecisionLabel.NonEntailment,
					pairId);
	}

	@Override
	public void startTraining(CommonConfig config)
			throws ConfigurationException, EDAException, ComponentException {

		// initialize the EDA
		initializeEDA(config);

		// initialize the data paths
		initializeTrainConfig(config, true);
		initializeTestDir(config); // TODO: check if required.

		// initialize the components
		try {
			initializeComponents(config);
		} catch (IOException e) {
			throw new ComponentException(e.getMessage());
		} catch (LexicalResourceException e) {
			throw new ComponentException(e.getMessage());
		}

		int entNum = 0;
		// Split weka arff into multiple uniformly distributed datasets.
		if (split) {
			// TODO: make this allocation dynamic
			entNum = 897; // no. of entailing pairs in RTE6 dev data.
			int totalNum = 15955; // total no. of pairs in RTE6 dev data

			// num of model files to be generated
			this.numOfModelFiles = (totalNum - entNum) / entNum + 1;
		} else
			this.numOfModelFiles = 1;

		// initialize classifier
		initializeClassifier(config);

		// initialize the models
		initializeModel(config, true);

		// write headers for weka arff file
		writeArffHeaders(true);

		// calculate scores and add the data to weka arff file
		generateTrainingDataArff(entNum);

		// train classifier on each weka data file and store model
		trainClassifier();

	}

	/**
	 * write data to Weka ARFF file for all training pairs.
	 * 
	 * @param entNum
	 *            no. of entailing pairs in total
	 * @throws ConfigurationException
	 * @throws LAPException
	 * @throws ScoringComponentException
	 */
	private void generateTrainingDataArff(int entNum)
			throws ConfigurationException, LAPException,
			ScoringComponentException {

		int curFileNum = 0; // file number to write next nonentailment entry to
		int curNEntNum = 0; // no. of non entailing cases in current file

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

			String goldClass = getGoldLabel(cas);
			feats += goldClass;

			if (1 == numOfModelFiles)
				// no split, only one file to write to.
				writeDatatoArff(feats, wekaArffFile + String.valueOf(0)
						+ ".arff");
			else {
				// entailing cases written to all files
				if (goldClass.equalsIgnoreCase("ENTAILMENT")) {
					for (int i = 0; i < numOfModelFiles; i++)
						writeDatatoArff(feats, wekaArffFile + String.valueOf(i)
								+ ".arff");
				}
				// non entailing cases written to a certain file number
				else {
					writeDatatoArff(feats,
							wekaArffFile + String.valueOf(curFileNum) + ".arff");
					curNEntNum++;
					if (curNEntNum >= entNum) {
						curFileNum++;
						curNEntNum = 0;
					}
				}
			}

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
		for (int i = 0; i < numOfModelFiles; i++) {
			Instances data;
			try {
				data = loadInstancesFromARFF(wekaArffFile, "class");
				java.util.Random rand = new java.util.Random();
				data.randomize(rand);
				classifier = new LibLINEAR();
				((LibLINEAR) classifier).setSVMType(new SelectedTag(
						LibLINEAR.SVMTYPE_L2_LR, LibLINEAR.TAGS_SVMTYPE));
				classifier.buildClassifier(data);

				// serialize model
				weka.core.SerializationHelper.write(
						modelFile + String.valueOf(i), classifier);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		// TODO: shutdown in better way
		components.clear();
		trainDIR = "";
		testDIR = "";
	}

	/**
	 * the logger
	 */
	public final static Logger logger = Logger
			.getLogger(NemexWekaClassificationEDA.class.getName());

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
	 * if training data needs to be split for uniform distribution
	 */
	private boolean split;
	/**
	 * the Arff file path
	 */
	private String wekaArffFile;

	/**
	 * number of arff files in total
	 */
	private int numOfModelFiles;

	/**
	 * File path for model file for classifier
	 */
	private String modelFile;

	/**
	 * Weka classifier object
	 */
	Classifier classifier;
}
