package eu.excitementproject.eop.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.RelationType;
import eu.excitementproject.eop.core.component.scoring.BagOfLexesPosScoring;
import eu.excitementproject.eop.core.component.scoring.BagOfLexesScoring;
import eu.excitementproject.eop.core.component.scoring.BagOfLexesScoringEN;
import eu.excitementproject.eop.core.component.scoring.BagOfWordsScoring;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
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

	public List<ScoringComponent> getComponents() {
		return components;
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

	public MaxentModel getModel() {
		return model;
	}

	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException {
		// initialize the language
		initializeLanguage(config);
		
		// initialize the model
		initializeModel(config, false);
		
		// initialize the data paths
		initializeData(config, false, false);

		// initialize the components
		initializeComponents(config);
	}
	
	private void initializeLanguage(CommonConfig config) throws ConfigurationException {
		NameValueTable top = config.getSection("PlatformConfiguration");
		language = top.getString("language");
		if (null == language) {
			// default language would be EN
			language = "EN";
		}
	}
	
	private void initializeComponents(CommonConfig config) throws ConfigurationException, ComponentException {
		NameValueTable EDA = config.getSection(MaxEntClassificationEDA.class.getName());
		String tempComps = EDA.getString("Components");
		if (null == tempComps || 0 == tempComps.trim().length()) {
			throw new ConfigurationException("Wrong configuation: no components contained in the EDA!");
		}
		String[] componentArray = tempComps.split(",");
		
		// to store the list of components used in this EDA
		components = new ArrayList<ScoringComponent>();

		for (String component : componentArray) {
			NameValueTable comp = config.getSection(component);
			if (null == comp) {
				throw new ConfigurationException("Wrong configuation: didn't find the corresponding setting for the component: " + component);
			}
			if (component.equals("BagOfLexesScoring")) {
				if (language.equalsIgnoreCase("DE")) {
					if (comp.getInteger("withPOS") == 1) {
						initializeLexCompsDE(config, true);
					} else {
						initializeLexCompsDE(config, false);
					}
				} else {
					initializeLexCompsEN(comp);
				}
			} else {
				try {
					@SuppressWarnings("unchecked")
					Class<? extends ScoringComponent> comp1 = (Class<? extends ScoringComponent>) Class.forName("eu.excitementproject.eop.core.component.scoring."+component);
					components.add(comp1.newInstance());
				} catch (Exception e) {
					throw new ConfigurationException(e.getMessage());
				}
			}
		}
	}
	
	private void initializeLexCompsDE(CommonConfig config, boolean withPOS) throws ConfigurationException {
		try {
			ScoringComponent comp3 = null;
			if (withPOS) {
				comp3 = new BagOfLexesPosScoring(config);
			} else {
				comp3 = new BagOfLexesScoring(config);
			}
			// check the number of features. if it's 0, no instantiation of the component.
			if (((BagOfLexesScoring)comp3).getNumOfFeats() > 0) {
				components.add(comp3);
			}
		} catch (LexicalResourceException e) {
			throw new ConfigurationException(e.getMessage());
		}
	}
	
	private void initializeLexCompsEN(NameValueTable comp) throws ConfigurationException, ComponentException {
		if (null == comp.getString("WordNetRelations") && null == comp.getString("VerbOceanRelations")) {
			throw new ConfigurationException("Wrong configuation: didn't find any lexical resources for the BagOfLexesScoring component");
		}		
		
		// these five boolean values control the lexical resources used.
		// they refer to whether to use WordNet relations hypernym, synonym, and VerbOcean relations, StrongerThan, CanResultIn, Similar
		boolean isWNHypernym = false;
		boolean isWNSynonym = false;
		boolean isWNHolonym = false;
		boolean isVOStrongerThan = false;
		boolean isVOCanResultIn = false;
		boolean isVOSimilar = false;
		if (null != comp.getString("WordNetRelations")) {
			String[] WNRelations = comp.getString("WordNetRelations").split(",");
			if (null == WNRelations || 0 == WNRelations.length) {
				throw new ConfigurationException("Wrong configuation: didn't find any relations for the WordNet");
			}
			for (String relation : WNRelations) {
				if (relation.equalsIgnoreCase("HYPERNYM")) {
					isWNHypernym = true;
				} else if (relation.equalsIgnoreCase("SYNONYM")) {
					isWNSynonym = true;
				} else if (relation.equalsIgnoreCase("PART_HOLONYM")) {
					isWNHolonym = true;
				} else {
					logger.warning("Warning: wrong relation names for the WordNet");
				}
			}
		}
		if (null != comp.getString("VerbOceanRelations")) {
			String[] VORelations = comp.getString("VerbOceanRelations").split(",");
			if (null == VORelations || 0 == VORelations.length) {
				throw new ConfigurationException("Wrong configuation: didn't find any relations for the VerbOcean");
			}
			for (String relation : VORelations) {
				if (relation.equalsIgnoreCase("strongerthan")) {
					isVOStrongerThan = true;
				} else if (relation.equalsIgnoreCase("canresultin")) {
					isVOCanResultIn = true;
				} else if (relation.equalsIgnoreCase("similar")) {
					isVOSimilar = true;
				} else {
					logger.warning("Warning: wrong relation names for the VerbOcean");
				}
			}
		}
		
		 Set<WordNetRelation> wnRelSet = new HashSet<WordNetRelation>();
		 if (isWNHypernym) {
			 wnRelSet.add(WordNetRelation.HYPERNYM);
		 }
		 if (isWNSynonym) {
			 wnRelSet.add(WordNetRelation.SYNONYM);
		 }
		 if (isWNHolonym) {
			 wnRelSet.add(WordNetRelation.PART_HOLONYM);
		 }
		 
		 Set<RelationType> voRelSet = new HashSet<RelationType>();
		 if (isVOStrongerThan) {
			 voRelSet.add(RelationType.STRONGER_THAN);
		 }
		 if (isVOCanResultIn) {
			 voRelSet.add(RelationType.CAN_RESULT_IN);
		 }
		 if (isVOSimilar) {
			 voRelSet.add(RelationType.SIMILAR);
		 }
		 
		 try {
			 ScoringComponent comp3 = new BagOfLexesScoringEN(wnRelSet, voRelSet);
			 components.add(comp3);
		 } catch (LexicalResourceException e) {
			 throw new ComponentException(e.getMessage());
		 }
	}

	private void initializeModel(CommonConfig config, boolean isTrain) throws ConfigurationException {
		NameValueTable EDA = config.getSection(MaxEntClassificationEDA.class.getName());
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

	public void initializeData(CommonConfig config, boolean isTrain, boolean isTestingBatch) throws ConfigurationException {
		NameValueTable EDA = config.getSection(MaxEntClassificationEDA.class.getName());		
		trainDIR = EDA.getString("trainDir");
		if (null == trainDIR) {
			if (isTrain) {
				throw new ConfigurationException("Please specify the training data directory.");
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
	public ClassificationTEDecision process(JCas aCas) throws EDAException,
			ComponentException {		
		String pairId = getPairID(aCas);
		String goldAnswer = getGoldLabel(aCas);
		if (null == goldAnswer) {
			goldAnswer = DecisionLabel.Abstain.toString();
		}

		String[] context = constructContext(aCas);
		logger.info(Arrays.asList(context).toString());
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
	public void startTraining(CommonConfig c) throws ConfigurationException,
			EDAException, ComponentException {
		// initialize the language
		initializeLanguage(c);
		
		// initialize the model
		initializeModel(c, true);
		
		// initialize the data paths
		initializeData(c, true, false);
		
		// initialize the components
		initializeComponents(c);
		
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
