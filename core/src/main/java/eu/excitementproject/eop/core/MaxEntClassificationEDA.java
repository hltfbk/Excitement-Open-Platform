package eu.excitementproject.eop.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.core.component.scoring.BagOfLemmasScoring;
import eu.excitementproject.eop.core.component.scoring.BagOfLexesScoring;
import eu.excitementproject.eop.core.component.scoring.BagOfWordsScoring;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

/**
 * The <code>MaxEntClassificationEDA</code> class implements the
 * <code>EDABasic</code> interface.
 * 
 * It uses the OpenNLP MaxEnt package to train a <code>GISModel</code>.
 * 
 * @author Rui
 */
public class MaxEntClassificationEDA implements
		EDABasic<ClassificationTEDecision> {

	static Logger logger = Logger.getLogger(MaxEntClassificationEDA.class
			.getName());

	// whether it's training or testing
	protected boolean isTrain;

	// list of components used in this EDA
	protected List<ScoringComponent> components;

	// language flag
	protected String language;

	// the model file, consisting of parameter name and value pairs
	protected String modelFile;

	// training data directory
	protected String trainDIR;

	// testing data directory
	protected String testDIR;

	// the model
	protected MaxentModel model;

	public boolean isTrain() {
		return isTrain;
	}

	public void setTrain(boolean isTrain) {
		this.isTrain = isTrain;
	}

	public List<ScoringComponent> getComponents() {
		return components;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	public MaxentModel getModel() {
		return model;
	}

	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException {

		components = new ArrayList<ScoringComponent>();
		ScoringComponent comp1 = new BagOfWordsScoring();
		components.add(comp1);

		ScoringComponent comp2 = new BagOfLemmasScoring();
		components.add(comp2);

		// these five boolean values control the lexical resources used.
		// they refer to whether to use GermanDistSim, GermaNetRelation.causes, GermaNetRelation.entails, GermaNetRelation.has_hypernym, and GermaNetRelation.has_synonym
		boolean isGDS = true;
		boolean isGNRcauses = true;
		boolean isGNRentails = true;
		boolean isGNRhypernym = true;
		boolean isGNRsynonym = true;
		
		if (language.equals("DE") && (isGDS || isGNRcauses || isGNRentails || isGNRhypernym || isGNRsynonym)) {
			ScoringComponent comp3 = new BagOfLexesScoring(isGDS, isGNRcauses, isGNRentails, isGNRhypernym, isGNRsynonym);
			components.add(comp3);
		}

		modelFile = "./src/test/resources/MaxEntClassificationEDAModel"
				+ language;

		trainDIR = "./target/" + language + "/dev/";
		testDIR = "./target/" + language + "/test/";

		// initialize the model: if it's training, check the model file exsits;
		// if it's testing, read in the model
		initializeModel(config);
	}

	private void initializeModel(CommonConfig config)
			throws ConfigurationException {
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

	@Override
	public ClassificationTEDecision process(JCas aCas) throws EDAException,
			ComponentException {
		String pairId = getPairID(aCas);
		String goldAnswer = getGoldLabel(aCas);
		if (null == goldAnswer) {
			goldAnswer = DecisionLabel.Abstain.toString();
		}

		String[] context = constructContext(aCas);
//		System.out.println(Arrays.asList(context));
		float[] values = RealValueFileEventStream.parseContexts(context);
		double[] ocs = model.eval(context, values);
		int numOutcomes = ocs.length;
		DoubleStringPair[] result = new DoubleStringPair[numOutcomes];
		for (int i = 0; i < numOutcomes; i++) {
			result[i] = new DoubleStringPair(ocs[i], model.getOutcome(i));
		}

		java.util.Arrays.sort(result);

		// Print the most likely outcome first, down to the least likely.
		// for (int i = numOutcomes - 1; i >= 0; i--)
		// System.out.print(result[i].stringValue + " "
		// + result[i].doubleValue + " ");
		// System.out.println();

		return new ClassificationTEDecision(
				getAnswerLabel(result[numOutcomes - 1].stringValue), result[numOutcomes - 1].doubleValue, pairId);
	}

	/**
	 * @param aCas
	 *            the <code>JCas</code> object
	 * @return return the feature vector of the instance, called
	 *         <code>Context</code> in the MaxEnt model
	 */
	protected String[] constructContext(JCas aCas)
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
	public void shutdown() {
		components.clear();
		modelFile = "";
		trainDIR = "";
		testDIR = "";
		model = null;
	}

	@Override
	public void startTraining(CommonConfig c) throws ConfigurationException,
			EDAException, ComponentException {
		boolean USE_SMOOTHING = false;
		double SMOOTHING_OBSERVATION = 0.1;

		// boolean real = false;
		// String type = "maxent";
		int maxit = 100;
		int cutoff = 1;
		// double sigma = 1.0;

		File outputFile = new File(modelFile);
		try {
			GIS.SMOOTHING_OBSERVATION = SMOOTHING_OBSERVATION;
			model = GIS.trainModel(maxit, new OnePassRealValueDataIndexer(
					readInXmiFiles(trainDIR), cutoff), USE_SMOOTHING);

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
	protected EventStream readInXmiFiles(String filePath)
			throws ConfigurationException {
		List<Event> eventList = new ArrayList<Event>();
		try {
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
		} catch (Exception e) {
			throw new ConfigurationException(e.getMessage());
		}
		return new ListEventStream(eventList);
	}

	/**
	 * @param filePath
	 *            the single xmi file path of the dataset
	 * @return return the instance of that file, represented in
	 *         <code>Event</code>
	 */
	protected Event readInXmiFile(String filePath)
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
	 * @return return the instance of the input, represented in <code>Event</code>
	 */
	protected Event casToEvent(JCas cas) throws ConfigurationException {
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
	protected String getPairID(JCas aCas) {
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
	protected String getGoldLabel(JCas aCas) {
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
	protected DecisionLabel getAnswerLabel(String answer) {
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
