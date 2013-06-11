package eu.excitementproject.eop.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import opennlp.maxent.DoubleStringPair;
import opennlp.maxent.GIS;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.model.AbstractModel;
import opennlp.model.AbstractModelWriter;
import opennlp.model.Event;
import opennlp.model.EventStream;
import opennlp.model.GenericModelReader;
import opennlp.model.ListEventStream;
import opennlp.model.MaxentModel;
import opennlp.model.OnePassRealValueDataIndexer;
import opennlp.model.RealValueFileEventStream;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

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
import eu.excitementproject.eop.core.component.scoring.BagOfLexesPosScoring;
import eu.excitementproject.eop.core.component.scoring.BagOfLexesScoring;
import eu.excitementproject.eop.core.component.scoring.BagOfLexesScoringEN;
import eu.excitementproject.eop.core.component.scoring.BagOfWordsScoring;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

/**
 * The <code>MaxEntClassificationEDA</code> class implements the
 * <code>EDABasic</code> interface.
 * 
 * It uses the OpenNLP MaxEnt package to train a <code>GISModel</code> in order
 * to classify Entailment T-H pairs from Non-Entailment ones. Currently, it
 * works for both English and German.
 * 
 * The compatible components are: 1) components in
 * <code>eu.excitementproject.eop.core.component.distance</code>; 2) components
 * in <code>eu.excitementproject.eop.core.component.scoring</code>; 3) lexical
 * knowledge components: for English, WordNet and VerbOcean; for German,
 * GermaNet and DistSim. Please refer to the specific lexical resources for more
 * details.
 * 
 * The following parameters need to be included in the configuration file: 1)
 * the training data directory (containing XMI files); 2) the testing data
 * directory; 3) the model file path; 4) the component list separated by comma;
 * 5) (optional) settings for the classifier, the maximum number of iterations
 * and the cut-off threshold.
 * 
 * @author Rui
 */
public class MaxEntClassificationEDA implements
		EDABasic<ClassificationTEDecision> {

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
	 * the language flag
	 */
	private String language;

	/**
	 * the model file, consisting of parameter name and value pairs
	 */
	private String modelFile;

	/**
	 * the training data directory
	 */
	private String trainDIR;

	/**
	 * the testing data directory
	 */
	private String testDIR;

	/**
	 * the model
	 */
	private MaxentModel model;

	/**
	 * get the list of components
	 * 
	 * @return
	 */
	public final List<ScoringComponent> getComponents() {
		return components;
	}

	/**
	 * get the language flag
	 * 
	 * @return
	 */
	public final String getLanguage() {
		return language;
	}

	/**
	 * get the model file path
	 * 
	 * @return
	 */
	public final String getModelFile() {
		return modelFile;
	}

	/**
	 * get the training data directory
	 * 
	 * @return
	 */
	public final String getTrainDIR() {
		return trainDIR;
	}

	/**
	 * get the testing data directory
	 * 
	 * @return
	 */
	public final String getTestDIR() {
		return testDIR;
	}

	/**
	 * get the model
	 * 
	 * @return
	 */
	public final MaxentModel getModel() {
		return model;
	}

	@Override
	public final void initialize(CommonConfig config)
			throws ConfigurationException, EDAException, ComponentException {
		// initialize the language
		initializeEDA(config);

		// initialize the model
		initializeModel(config, false);

		// initialize the data paths
		initializeData(config, false);

		// initialize the components
		initializeComponents(config);
	}

	/**
	 * initialize the language flag from the configuration
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
		language = top.getString("language");
		if (null == language) {
			// default language would be EN
			language = "EN";
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

		// to store the list of components used in this EDA
		components = new ArrayList<ScoringComponent>();

		for (String component : componentArray) {
			NameValueTable comp = config.getSection(component);
			if (null == comp) {
				throw new ConfigurationException(
						"Wrong configuation: didn't find the corresponding setting for the component: "
								+ component);
			}
			if (component.equals("BagOfLexesScoring")) {
				if (language.equalsIgnoreCase("DE")) {
					if (null == comp.getString("withPOS")
							|| !Boolean.parseBoolean(comp.getString("withPOS"))) {
						initializeLexCompsDE(config, false);
					} else {
						initializeLexCompsDE(config, true);
					}
				} else {
					initializeLexCompsEN(config);
				}
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
	 * initialize the lexical components
	 * 
	 * @param config
	 *            the configuration
	 * @param withPOS
	 *            whether use POS for the queries to the lexical components
	 * @throws ConfigurationException
	 */
	private void initializeLexCompsDE(CommonConfig config, boolean withPOS)
			throws ConfigurationException {
		try {
			ScoringComponent comp3 = null;
			if (withPOS) {
				comp3 = new BagOfLexesPosScoring(config);
			} else {
				comp3 = new BagOfLexesScoring(config);
			}
			// check the number of features. if it's 0, no instantiation of the
			// component.
			if (((BagOfLexesScoring) comp3).getNumOfFeats() > 0) {
				components.add(comp3);
			}
		} catch (LexicalResourceException e) {
			throw new ConfigurationException(e.getMessage());
		}
	}

	/**
	 * initialize the <b>English</b> lexical components
	 * 
	 * @param comp
	 *            the <code>NameValueTable</code> for the components in the
	 *            configuration
	 * @throws ConfigurationException
	 * @throws ComponentException
	 */
	private void initializeLexCompsEN(CommonConfig config)
			throws ConfigurationException, ComponentException {
		try {
			ScoringComponent comp3 = new BagOfLexesScoringEN(config);
			if (((BagOfLexesScoringEN) comp3).getNumOfFeats() > 0) {
				components.add(comp3);
			}
		} catch (LexicalResourceException e) {
			throw new ComponentException(e.getMessage());
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
				throw new ConfigurationException(
						"The model file exists! Please specify another file name.");
			} else {
				logger.info("The trained model will be stored in "
						+ file.getAbsolutePath());
			}
		} else {
			try {
				model = new GenericModelReader(new File(modelFile)).getModel();
			} catch (IOException e) {
				throw new ConfigurationException(e.getMessage());
			}
		}
	}

	/**
	 * initialize the data, training and/or testing
	 * 
	 * @param config
	 *            the configuration
	 * @param isTrain
	 *            whether it is training or testing
	 * @throws ConfigurationException
	 */
	public final void initializeData(CommonConfig config, boolean isTrain)
			throws ConfigurationException {
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}
		trainDIR = EDA.getString("trainDir");
		if (null == trainDIR) {
			if (isTrain) {
				throw new ConfigurationException(
						"Please specify the training data directory.");
			} else {
				logger.warning("Warning: Please specify the training data directory.");
			}
		}
		testDIR = EDA.getString("testDir");
		if (null == testDIR) {
			logger.warning("Warning: Please specify the testing data directory.");
		}
	}

	@Override
	public final ClassificationTEDecision process(JCas aCas)
			throws EDAException, ComponentException {
		String pairId = getPairID(aCas);
		// String goldAnswer = getGoldLabel(aCas);
		// if (null == goldAnswer) {
		// goldAnswer = DecisionLabel.Abstain.toString();
		// }

		String[] context = constructContext(aCas);
		logger.info(Arrays.asList(context).toString());
		float[] values = RealValueFileEventStream.parseContexts(context);
		double[] ocs = model.eval(context, values);
		int numOutcomes = ocs.length;
		DoubleStringPair[] result = new DoubleStringPair[numOutcomes];
		for (int i = 0; i < numOutcomes; i++) {
			result[i] = new DoubleStringPair(ocs[i], model.getOutcome(i));
		}

		Arrays.sort(result);

		// Print the most likely outcome first, down to the least likely.
		// for (int i = numOutcomes - 1; i >= 0; i--)
		// System.out.print(result[i].stringValue + " "
		// + result[i].doubleValue + " ");
		// System.out.println();

		return new ClassificationTEDecision(
				getAnswerLabel(result[numOutcomes - 1].stringValue),
				result[numOutcomes - 1].doubleValue, pairId);
	}

	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return return the feature vector of the instance, called
	 *         <code>Context</code> in the MaxEnt model
	 */
	protected final String[] constructContext(JCas aCas)
			throws ScoringComponentException {
		List<String> featList = new ArrayList<String>();
		for (ScoringComponent comp : components) {
			Vector<Double> scores = comp.calculateScores(aCas);
			for (int i = 0; i < scores.size(); i++) {
				featList.add(comp.getComponentName() + "_" + i + "="
						+ scores.get(i).floatValue());
			}
		}
		return featList.toArray(new String[featList.size()]);
	}

	@Override
	public final void shutdown() {
		if (null != components) {
			for (ScoringComponent comp : components) {
				try {
					((BagOfWordsScoring) comp).close();
				} catch (ScoringComponentException e) {
					logger.warning(e.getMessage());
				}
			}
			components.clear();
		}
		modelFile = "";
		trainDIR = "";
		testDIR = "";
		model = null;
	}

	@Override
	public final void startTraining(CommonConfig c)
			throws ConfigurationException, EDAException, ComponentException {
		// initialize the language
		initializeEDA(c);

		// initialize the model
		initializeModel(c, true);

		// initialize the data paths
		initializeData(c, true);

		// initialize the components
		initializeComponents(c);

		boolean USE_SMOOTHING = false;

		// commented out, use the default value
		// final double SMOOTHING_OBSERVATION = 0.1;

		String classifier = c.getSection(this.getClass().getName())
				.getString("classifier");
		int max_iteration = 10000; // default value
		int cut_off = 1; // default value
		if (null != classifier && classifier.split(",").length == 2) {
			max_iteration = Integer.parseInt(classifier.split(",")[0]);
			cut_off = Integer.parseInt(classifier.split(",")[1]);
		}
		// double sigma = 1.0;

		File outputFile = new File(modelFile);
		try {
			// GIS.SMOOTHING_OBSERVATION = SMOOTHING_OBSERVATION;
			model = GIS.trainModel(max_iteration,
					new OnePassRealValueDataIndexer(readInXmiFiles(trainDIR),
							cut_off), USE_SMOOTHING);

			AbstractModelWriter writer = new SuffixSensitiveGISModelWriter(
					(AbstractModel) model, outputFile);
			writer.persist();
		} catch (IOException e) {
			throw new ConfigurationException(e.getMessage());
		}
	}

	/**
	 * @param filePath
	 *            the xmi file or directory path of the dataset
	 * @return return the instances of the dataset, represented in
	 *         <code>Event</code>s
	 */
	protected final EventStream readInXmiFiles(String filePath)
			throws ConfigurationException {
		List<Event> eventList = new ArrayList<Event>();
		File dir = new File(filePath);
		if (dir.isFile()) {
			eventList.add(readInXmiFile(dir.getAbsolutePath()));
		} else if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				// ignore all the non-xmi files
				if (!file.getName().endsWith(".xmi")) {
					continue;
				}
				// add the instance to the dataset
				eventList.add(readInXmiFile(file.getAbsolutePath()));
			}
		}
		return new ListEventStream(eventList);
	}

	/**
	 * @param filePath
	 *            the single xmi file path of the dataset
	 * @return return the instance of that file, represented in
	 *         <code>Event</code>
	 */
	protected final Event readInXmiFile(String filePath)
			throws ConfigurationException {
		try {
			File xmiFile = new File(filePath);
			return casToEvent(PlatformCASProber.probeXmi(xmiFile, System.out));
		} catch (LAPException e) {
			throw new ConfigurationException(e.getMessage());
		}
	}

	/**
	 * @param cas
	 *            the <code>JCas</code> object
	 * @return return the instance of the input, represented in
	 *         <code>Event</code>
	 */
	protected final Event casToEvent(JCas cas) throws ConfigurationException {
		String goldAnswer = getGoldLabel(cas);
		if (null == goldAnswer) {
			goldAnswer = DecisionLabel.Abstain.toString();
		}
		try {
			String[] contexts = constructContext(cas);
			float[] values = RealValueFileEventStream.parseContexts(contexts);
			return new Event(goldAnswer, contexts, values);
		} catch (ScoringComponentException e) {
			throw new ConfigurationException(e.getMessage());
		}
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

	/**
	 * @param answer
	 *            the string value of the answer
	 * @return the <code>DecisionLabel</code> of the answer
	 */
	protected final DecisionLabel getAnswerLabel(String answer) {
		if (answer.equalsIgnoreCase("contradiction")) {
			return DecisionLabel.Contradiction;
		} else if (answer.equalsIgnoreCase("entailment")) {
			return DecisionLabel.Entailment;
		} else if (answer.equalsIgnoreCase("nonentailment")) {
			return DecisionLabel.NonEntailment;
		} else if (answer.equalsIgnoreCase("paraphrase")) {
			return DecisionLabel.Paraphrase;
		} else if (answer.equalsIgnoreCase("unknown")) {
			return DecisionLabel.Unknown;
		} else {
			return DecisionLabel.Abstain;
		}
	}

}
