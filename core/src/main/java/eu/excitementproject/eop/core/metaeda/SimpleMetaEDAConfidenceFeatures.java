package eu.excitementproject.eop.core.metaeda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

//import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.*;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;

/**
 * The <code>MetaEDA</code> performs as a higher level EDA. It implements the
 * <code>EDABasic</code> interface. 
 * 
 * It uses multiple initialized EDABasic instances and their classifying results as features to make its own
 * decision.  
 * It has two modes:
 * 1) voting: each EDA's DecisionLabel counts as vote for NonEntailment or Entailment.
 * 	SimpleMetaEDAConfidenceFeatures goes with the majority. In case of a tie, it decides NonEntailment. 
 *  Note that there is no training in this mode.
 * 2) confidences as features: each EDA's decision and its confidence on this decision is taken as a feature
 * 	for a classifier which is then trained on the input pairs. 
 *  If the decision is "NonEntailment", the numerical feature is the confidence*(-1), if it is "Entailment", the feature is simply the confidence. 
 * 	The trained model is stored and can be loaded again to use it for classifying new data.
 *  Training is performed with a weka classifier.
 *  
 *  SimpleMetaEDAConfidenceFeatures is initialized with a configuration file, where the following parameters need to be set:
 *  - "activatedEDA": the activated EDA, has to be eu.excitementproject.eop.core.SimpleMetaEDAConfidenceFeatures
 *  - "language": "EN", "DE" or any other language supported in internal EDABasics
 *  - "confidenceAsFeature": defines the mode (1 or 2), see above
 *  - "overwrite": whether to overwrite an existing model with the same name or not
 *  - "modelFile": path to model file
 *  - "trainDir": path to training data directory
 *  - "testDir": path to test data directory
 *  A sample configuration file can be found in core/src/test/resources/configuration-file/MetaEDATest1_DE.xml
 *  
 *  Alternatively, it can be initialized with the parameters parameters listed above directly,
 *  calling <code>initialize(String language, boolean confidenceAsFeatures, boolean overwrite, String modelFile, String trainDir, String testDir)</code>.
 *  Note that we assume here that the activatedEDA is this SimpleMetaEDAConfidenceFeatures and does therefore not require passing the parameter.
 *  
 * Please note that the following steps need to be done before initializing a SimpleMetaEDAConfidenceFeatures instance:
 *  1) All EDABasic instances used for the MetaEDA must have been initialized correctly. 
 *     The MetaEDA does not check whether they are correctly initialized.
 *     Details about how to initialize an EDABasic correctly can be found in their documentation.
 *  2) Calling process() or startTraining() requires LAP annotations on test and training data (specified in testDir and trainDir) for the given EDABasic instances. 
 *     Again, the MetaEDA does not check whether the required annotation layers are there.
 *     For details about the annotation layers required by each EDABasic, refer to the specific EDABasic's documentation.
 *  
 * For usage examples see <code>SimpleMetaEDAConfidenceFeaturesUsageExample.java</code>.
 * 
 * @author Julia Kreutzer
 *
 */
public class SimpleMetaEDAConfidenceFeatures implements EDABasic<TEDecision>{
	
	/**
	 * the logger, "info" level just reports EDA statuses like "initializing", "training", etc.;
	 * "debug" level also reports TEDecisions from EDABasic instances and the SimpleMetaEDAConfidenceFeatures's decisions for classified data
	 */
	public final static Logger logger = Logger.getLogger(SimpleMetaEDAConfidenceFeatures.class.getName());
		
	/**
	 * Constructs a new SimpleMetaEDAConfidenceFeatures instance with a list of already initialized 
	 * basic EDAs.
	 * @param edas list of already initialized EDABasic instances
	 */
	public SimpleMetaEDAConfidenceFeatures(ArrayList<EDABasic<? extends TEDecision>> edas){
		this.edas = edas;
		logger.info("new SimpleMetaEDAConfidenceFeatures with "+edas.size()+" internal EDABasics");
	}

	/**
	 * returns the classification results
	 * @return
	 */
	public HashMap<Integer,double[]> getResults(){
		return this.results;
	}

	/**
	 * returns the classifier
	 * @return
	 */
	public Logistic getClassifier(){
		return this.classifier;
	}
	
	/**
	 * Initializes a SimpleMetaEDAConfidenceFeatures instance with a configuration file, 
	 * where training and decision mode, overwrite mode,
	 * path to model file, training data and test data directory are defined
	 * @param config a CommonConfig where parameters and directories for SimpleMetaEDAConfidenceFeatures are defined
	 */
	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException {
		logger.info("initialize SimpleMetaEDAConfidenceFeatures with configuration file");
		initializeEDA(config);
		initializeData(config);
		if (!this.confidenceAsFeature){
			// mode 1: do nothing
		}
		else {
			// mode 2:
			// load and initialize pre-trained model
			initializeModel(config);
		}
	}
	
	/**
	 * Initializes a SimpleMetaEDAConfidenceFeatures instance with parameters (without configuration file) 
	 * that define training and decision mode, overwrite mode,
	 * path to model file, training data and test data directory
	 * @param language String, e.g. "EN" or "DE"
	 * @param confidenceAsFeatures if true: use confidence features, do majority vote otherwise
	 * @param modelFile String path to model file
	 * @param trainDir String path to training data directory
	 * @param testDir String path to test data directory
	 */
	public void initialize(String language, boolean confidenceAsFeatures, boolean overwrite, String modelFile, String trainDir, String testDir) throws ConfigurationException,
			EDAException, ComponentException {
		logger.info("initialize SimpleMetaEDAConfidenceFeatures with given parameters");
		initializeEDA(language, confidenceAsFeatures);
		initializeData(trainDir, testDir);
		if (!this.confidenceAsFeature){
			// mode 1: do nothing
		}
		else {
			// mode 2:
			// load and initialize pre-trained model
			initializeModel(overwrite, modelFile);
		}
	}
	

	/**
	 * Starts training on the EDABasic instances' confidence features with the given configuration.
	 * SimpleMetaEDAConfidenceFeatures initialization is included in this method.
	 * Note that training is only performed in mode 2 (confidence as features).
	 * In mode 2) a Logistic classifier is trained on the EDABasic decisions and confidences.
	 * Training and testing data directories are defined in the configuration file.
	 */
	@Override
	public void startTraining(CommonConfig c) throws EDAException, LAPException {
		this.isTrain = true; //set train flag
		this.isTest = false;
		try {
			this.initialize(c);
		} catch (ConfigurationException | EDAException | ComponentException e) {
			e.printStackTrace();
		}
		if (!this.confidenceAsFeature){
			//do nothing: no training in mode 1
			return;
		}
		else {
			//mode 2
			logger.info("Start training with confidences from EDABasic instances as features.");
			
			ArrayList<String> goldAnswers = new ArrayList<String>(); //stores gold answers
			
			//xmi files in training directory
			File [] xmis = new File(this.trainDir).listFiles();
			
			//create attributes: for each EDABasic instance use their name and index as attribute name
			FastVector attrs = getAttributes();
			
			//build up the dataset from training data
			Instances instances = new Instances("EOP", attrs, xmis.length);  
			
			for (File xmi : xmis) {
			    if (!xmi.getName().endsWith(".xmi")) {
			      continue;
			    }
			    // The annotated pair is added into the CAS.
			    JCas jcas = PlatformCASProber.probeXmi(xmi, null);
				Pair pair = JCasUtil.selectSingle(jcas, Pair.class);
				logger.debug("processing pair "+pair.getPairID());
				int pairID = Integer.parseInt(pair.getPairID());
				
				String goldAnswer = pair.getGoldAnswer(); //get gold annotation
				logger.debug("gold answer: "+goldAnswer);
				
				//get features from BasicEDAs' confidence scores
				ArrayList<Double> scores = getFeatures(jcas, pairID);
								
				//Store gold answer
				goldAnswers.add(goldAnswer);
				
				//add new instance to dataset
				Instance instance = new DenseInstance(scores.size());
				instance.setDataset(instances);
				for (int j = 0; j < scores.size(); j++){
					Double score = scores.get(j);
					instance.setValue((Attribute) attrs.elementAt(j), score);
				}
				instances.add(instance);
			}
			
			//last attribute is class prediction (either nonentailment or entailment)
			FastVector values = new FastVector(); 
		    values.addElement("NONENTAILMENT");          
		    values.addElement("ENTAILMENT");
		    Attribute gold = new Attribute("gold", values);
		    instances.insertAttributeAt(gold, instances.numAttributes());	
			instances.setClassIndex(instances.numAttributes()-1); // set class attribute -> last attribute (gold label)
			
			//set gold labels for instances
			logger.info(instances.numInstances()+" training instances loaded with "+instances.numAttributes()+" attributes");
			for (int k = 0; k<instances.numInstances(); k++){
				instances.instance(k).setValue(instances.numAttributes()-1, goldAnswers.get(k).toUpperCase());
			}
			
			//train the classifier
			logger.info("Training the classifier...");
			
			//classifier is a Logistic classifier with default options and parameters
			this.classifier = new Logistic();

			try {
				//train the classifier on training data set
				this.classifier.buildClassifier(instances);

				//print the classifiers coefficients on debug level
				logger.debug(Arrays.deepToString(this.classifier.coefficients()));
				
				//serialize and store classifier in model file
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.modelFile));
				oos.writeObject(this.classifier);
				oos.flush();
				oos.close();
				logger.info("Serialized model and stored as "+this.modelFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("training done.");
		}
	}

	/**
	* Starts training on the EDABasic instances' confidence features with the given parameters.
	* SimpleMetaEDAConfidenceFeatures initialization is included in this method.
	* Note that training is only performed in mode 2 (confidence as features).
	* In mode 2) a Logistic classifier is trained on the EDABasic decisions and confidences.
	* Training and testing data directories are defined in the configuration file.
	*/
	public void startTraining(String language, boolean confidenceAsFeatures, boolean overwrite, String modelFile, String trainDir, String testDir) throws EDAException, LAPException {
		this.isTrain = true; //set train flag
		this.isTest = false;
		try {
			this.initialize(language, confidenceAsFeatures, overwrite, modelFile, trainDir, testDir);
		} catch (ConfigurationException | EDAException | ComponentException e) {
			e.printStackTrace();
		}
		if (!this.confidenceAsFeature){
			//do nothing: no training in mode 1
			return;
		}
		else {
			//mode 2
			logger.info("Start training with confidences from EDABasic instances as features.");
	
			ArrayList<String> goldAnswers = new ArrayList<String>(); //stores gold answers
	
			//xmi files in training directory
			File [] xmis = new File(this.trainDir).listFiles();
			
			//create attributes: for each EDABasic instance use their name and index as attribute name
			FastVector attrs = getAttributes();
	
			//build up the dataset from training data
			Instances instances = new Instances("EOP", attrs, xmis.length);
			
			for (File xmi : xmis) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				// The annotated pair is added into the CAS.
				JCas jcas = PlatformCASProber.probeXmi(xmi, null);
				Pair pair = JCasUtil.selectSingle(jcas, Pair.class);
				int pairID = Integer.parseInt(pair.getPairID());
				logger.debug("processing pair "+pairID);
				String goldAnswer = pair.getGoldAnswer(); //get gold annotation
				logger.debug("gold answer: "+goldAnswer);
				//get features from BasicEDAs' confidence scores
				ArrayList<Double> scores = getFeatures(jcas, pairID);
				
				//Store gold answer
				goldAnswers.add(goldAnswer);
	
				//add new instance to dataset
				Instance instance = new DenseInstance(scores.size());
				instance.setDataset(instances);
				for (int j = 0; j < scores.size(); j++){
					Double score = scores.get(j);
					instance.setValue((Attribute) attrs.elementAt(j), score);
				}
				instances.add(instance);
			}
	
			//last attribute is class prediction (either nonentailment or entailment)
			FastVector values = new FastVector();
			values.addElement("NONENTAILMENT");
			values.addElement("ENTAILMENT");
			Attribute gold = new Attribute("gold", values);
			instances.insertAttributeAt(gold, instances.numAttributes());	
			instances.setClassIndex(instances.numAttributes()-1); // set class attribute -> last attribute (gold label)
			
			//set gold labels for instances
			logger.info(instances.numInstances()+" training instances loaded with "+instances.numAttributes()+" attributes");
			for (int k = 0; k<instances.numInstances(); k++){
				instances.instance(k).setValue(instances.numAttributes()-1, goldAnswers.get(k).toUpperCase());
			}
	
			//train the classifier
			logger.info("Training the classifier...");
	
			//classifier is a BayesianLogisticRegression classifier with default options and parameters
			this.classifier = new Logistic();
			try {
				//train the classifier on training data set
				this.classifier.buildClassifier(instances);
				
				//print the classifiers coefficients on debug level
				logger.debug("logistic classifier's coefficients: "+Arrays.deepToString(this.classifier.coefficients()));
				
				//serialize and store classifier in model file
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.modelFile));
				oos.writeObject(this.classifier);
				oos.flush();
				oos.close();
				logger.info("Serialized model and stored as "+this.modelFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("training done.");
		}
	}
	
	/**
	 * Processes a given JCas:
	 * -> in mode 1) just collect decisions from EDABasic instances and go with the majority (or NonEntailment in case of a tie)
	 * -> in mode 2) collect features from EDABasic instances for the JCas text and classify the data with this SimpleMetaEDAConfidenceFeatures's  trained weka classifier
	 * @param aCas the JCas to process
	 * @return a MetaTEDecision with decision label, confidence, and pairID for the classified input JCas
	 */
	@Override
	public MetaTEDecision process(JCas aCas) throws EDAException,
			ComponentException {
		
		Pair pair = JCasUtil.selectSingle(aCas, Pair.class);
		int pairID = Integer.parseInt(pair.getPairID());
		
		//generate the confidence features
		List<Double> features = getFeatures(aCas, pairID);
		
		
		DecisionLabel dLabel;	
		double[] distribution = new double[2]; //at index 0: probability for NonEntailment, index 1: probability for Entailment

		
		//mode 2: classify on features collected from BasicEDAs' decisions
		if (this.confidenceAsFeature){
			//create attributes: for each EDABasic instance use their name and index as attribute name
			FastVector attrs = getAttributes();

			//build up the dataset, here only a single instance
			Instances instances = new Instances("EOP", attrs, 1);  
			
			//last attribute is class prediction (either nonentailment or entailment)
			FastVector values = new FastVector(); 
		    values.addElement("NONENTAILMENT");          
		    values.addElement("ENTAILMENT");
		    instances.insertAttributeAt(new Attribute("prediction", values), instances.numAttributes());	
			instances.setClassIndex(edas.size()); // set class attribute -> last attribute which is prediction
			
			//add new instance to dataset
			Instance instance = new SparseInstance(features.size() + 1);
			instance.setDataset(instances);
			for (int i = 0; i < features.size(); i++){
				Double score = features.get(i);
				instance.setValue((Attribute) attrs.elementAt(i), score);
			}
			instances.add(instance);
			
			logger.debug("classifying pair no. "+pairID);
			
			//classify instance
			double result = 0.0;
			try {
				result = this.classifier.classifyInstance(instances.firstInstance());
				distribution = this.classifier.distributionForInstance(instances.firstInstance());				
				instances.firstInstance().setClassValue(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//determine the result label
			String label = instances.firstInstance().classAttribute().value((int)result);
			
			//convert to a DecisionLabel instance
			if (label.toUpperCase().equals(DecisionLabel.Entailment.toString().toUpperCase()))
				dLabel = DecisionLabel.Entailment;
			else
				dLabel = DecisionLabel.NonEntailment;
			
			logger.debug("DecisionLabel: "+dLabel);
		
		} else {
			//mode 1: majority vote
			int decision = 0;
			int nonEntCount = 0; //count how often NonEntailment is voted
			int entCount = 0; //same for Entailment
			for (Double feature : features) {
				if (feature < 0){
					decision -= 1;
					nonEntCount += 1;
				} else {
					decision += 1;
					entCount += 1;
				}
			}
			distribution[0]=nonEntCount/features.size();
			distribution[1]=entCount/features.size();

			if (decision <= 0){
				dLabel = DecisionLabel.NonEntailment;
			} else {
				dLabel = DecisionLabel.Entailment;
			}
			logger.debug("DecisionLabel after voting: "+dLabel);
		}
		double confidence = 0;
		if (dLabel == DecisionLabel.Entailment){
			confidence = distribution[1];
			this.results.get(pairID)[edas.size()+1] = confidence; //on last position in array comes meta decision
		}
		else if (dLabel == DecisionLabel.NonEntailment){
			confidence = distribution[0];
			this.results.get(pairID)[edas.size()+1] = -1* confidence;
		}
		
		return new MetaTEDecision(dLabel, confidence, pair.getPairID());
	}		

	/**
	 * shuts down SimpleMetaEDAConfidenceFeatures and disengage all resources
	 */
	@Override
	public void shutdown() {
		//disengage resources or reset to default value
		this.confidenceAsFeature = false;
		this.edas = null;
		this.language = "";
		this.modelFile = "";
		this.trainDir = "";
		this.testDir = "";
		this.classifier = null;
		this.isTrain = false;
		this.isTest = false;
		this.overwrite = false;
	}

	/**
	 * get the model file
	 * @return the path to the modelFile (String)
	 */
	public String getModelFile() {
		return modelFile;
	}

	//both isTest and isTrain needed, as initialization can take place before testing or training is defined
	/**
	 * whether SimpleMetaEDAConfidenceFeatures is in testing mode or not
	 * @return true if SimpleMetaEDAConfidenceFeatures is used for testing, false otherwise
	 */
	public boolean isTest() {
		return isTest;
	}
	
	/**
	 * whether SimpleMetaEDAConfidenceFeatures is in training mode or not
	 * @return true if SimpleMetaEDAConfidenceFeatures is used for training, false otherwise
	 */
	public boolean isTrain() {
		return isTrain;
	}
	
	/**
	 * wether SimpleMetaEDAConfidenceFeatures is used in mode 2) (use EDABasics' decision confidences for training)
	 * @return true if SimpleMetaEDAConfidenceFeatures is in mode 2) (confidence as features), false otherwise
	 */
	public boolean isConfidenceAsFeature() {
		return confidenceAsFeature;
	}

	/**
	 * get the list of initialized EDABasic instances this SimpleMetaEDAConfidenceFeatures is based on
	 * @return ArrayList of EDABasic instances
	 */
	public ArrayList<EDABasic<? extends TEDecision>> getEdas() {
		return edas;
	}

	/**
	 * get the language, e.g. "DE" for German, "EN" for English
	 * @return language String
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * whether SimpleMetaEDAConfidenceFeatures is in "overwrite" mode, i.e. if model file does already exist, it is overwritten
	 * @return true if already existing model files should get overwritten, false otherwise
	 */
	public boolean isOverwrite() {
		return overwrite;
	}

	/**
	 * set the isTest parameter to distinguish between test and training mode
	 * @param true or false
	 */
	public void setTest(boolean b) {
		this.isTest = b;
	}

	/**
	 * get the path to the test data directory 
	 * @return path to test data directory (String)
	 */
	public String getTestDir() {
		return this.testDir;
	}
	
	/**
	 * get the path to the training data directory 
	 * @return path to training data directory (String)
	 */
	public String getTrainDir() {
		return this.trainDir;
	}

	/**
	 * the mode: use the EDABasics' confidence scores as features for training (2) or just decide via majority vote (1)
	 */
	private boolean confidenceAsFeature; //if true: 2nd mode
	
	/*
	 * store classification results in hashmap, one entry for each pair, especially useful for testing
	 */
	private HashMap<Integer,double[]> results = new HashMap<Integer,double[]>();

	/**
	 * contains all internal basic EDAs
	 */
	private ArrayList<EDABasic<? extends TEDecision>> edas; 

	/**
	 * the language, e.g. "EN" for English
	 */
	private String language;

	/**
	 * the path to model file
	 */
	private String modelFile; 

	/** 
	 * the path to training data
	 */
	private String trainDir;

	/**
	 * the path to testing data
	 */
	private String testDir;
	
	/**
	 * the weka classifier
	 */
	private Logistic classifier;
	
	//both isTest and isTrain needed, as initialization can take place before testing or training is defined
	/**
	 * when true: in training mode
	 */
	private boolean isTrain = false; 
	
	/**
	 * when true: in testing mode
	 */
	private boolean isTest = false; 
	
	/**
	 * when true: overwrites existing models while training, false: appends "_old" to existing model file
	 */
	private boolean overwrite = false;

	/**
	 * Initializes the EDA:
	 * initializes the language flag from the configuration,
	 * checks that the correct activated EDA is specified in config,
	 * and sets the mode (use confidence as features or not).
	 * @param config the CommonConfig configuration
	 * @throws ConfigurationException
	 */
	private void initializeEDA(CommonConfig config)
			throws ConfigurationException {
		NameValueTable top = config.getSection("PlatformConfiguration");
		if (null == top	|| !top.getString("activatedEDA").equals(this.getClass().getName())) {
			throw new ConfigurationException("Please specify the (correct) EDA.");
		}
		this.language = top.getString("language");
		if (!(this.language != null)) {
			this.language = "EN"; // default language is English
		}
		
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()	+ " No EDA section.");
		}
	
		if (EDA.getString("confidenceAsFeature") != null){ //default is false
			this.confidenceAsFeature = Boolean.parseBoolean(EDA.getString("confidenceAsFeature"));
			if (this.confidenceAsFeature){
				logger.info("mode 2: use confidence scores as features");
			}
			else {
				logger.info("mode 1: majority vote");
			}
		}
		else {
			throw new ConfigurationException("Please specify SimpleMetaEDAConfidenceFeatures's mode: use confidence scores as features or not.");
		}
	}

	/**
	 * Initializes the EDA:
	 * initializes the language flag 
	 * and sets the mode (use confidence as features or not).
	 * @param language the language used, e.g. "DE"
	 * @param confidenceAsFeatures mode 2 (using confidence features for training) if true, mode 1 (majority vote) otherwise
	 */
	private void initializeEDA(String language, boolean confidenceAsFeature){
		this.language = language;
		if (!(this.language != null)) {
			this.language = "EN"; // default language is English
		}
		this.confidenceAsFeature = confidenceAsFeature;
		if (this.confidenceAsFeature){
			logger.info("mode 2: use confidence scores as features");
		}
		else {
			logger.info("mode 1: majority vote");
		}
	}

	
	/**
	 * Initializes the model by either reading existing model or creating a new model file.
	 * If "overwrite" is set to true and SimpleMetaEDAConfidenceFeatures is in training mode, an existing model file with the same name is overwritten.
	 * If it is set to false, the old model file is renamed with the ending "_old".
	 * 
	 * @param config the CommonConfig configuration
	 * @throws ConfigurationException
	 */
	private void initializeModel(CommonConfig config)
			throws ConfigurationException {
		
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}
		
		if (EDA.getString("overwrite") != null){ //default is false
			this.overwrite = Boolean.parseBoolean(EDA.getString("overwrite"));
		}
		else {
			if (this.isTrain){
				throw new ConfigurationException("Please specify SimpleMetaEDAConfidenceFeatures's overwrite mode.");
			}
		}
		
		this.modelFile = EDA.getString("modelFile");
		if (null == this.modelFile){
			throw new ConfigurationException("No model directory specified in config file.");
		}
		
		File file = new File(modelFile);
		
		if (file.exists()){
			if (this.isTrain && !this.isTest){
				if (this.overwrite){
					logger.info("The existing model will be overwritten.");
				}
				else{
					String oldModelFile = modelFile + "_old";
					logger.info("The existing model is renamed to "+file.getAbsolutePath()+"_old");
					File oldFile = new File(oldModelFile);
					if (oldFile.exists())
						oldFile.delete();
					file.renameTo(oldFile);
				}
			}
			else if (this.isTest){
				logger.info("Reading model from "+file.getAbsolutePath());
				 //deserialize model to classifier 
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(new FileInputStream(this.modelFile));
					this.classifier = (Logistic) ois.readObject();
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			if (this.isTrain && !this.isTest){
				logger.info("The trained model will be stored in "+ file.getAbsolutePath());
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if (this.isTest){
				throw new ConfigurationException("The model specified in the configuration does NOT exist! Please give the correct file path.");
			}
		}
	}

	/**
	 * Initializes the model by either reading existing model or creating a new model file.
	 * If "overwrite" is set to true and SimpleMetaEDAConfidenceFeatures is in training mode, an existing model file with the same name is overwritten.
	 * If it is set to false, the old model file is renamed with the ending "_old".
	 * 
	 * @param overwrite if true: overwrite possibly existing model file with same name, rename it otherwise
	 * @param modelFile String path to model file
	 * @throws ConfigurationException
	 */
	private void initializeModel(boolean overwrite, String modelFile)
			throws ConfigurationException {
		
		this.overwrite = overwrite;
		this.modelFile = modelFile;
		if (null == this.modelFile){
			throw new ConfigurationException("No model directory specified.");
		}
		
		File file = new File(modelFile);
		
		if (file.exists()){
			if (this.isTrain && !this.isTest){
				if (this.overwrite){
					logger.info("The existing model will be overwritten.");
				}
				else{
					String oldModelFile = modelFile + "_old";
					logger.info("The existing model is renamed to "+file.getAbsolutePath()+"_old");
					File oldFile = new File(oldModelFile);
					if (oldFile.exists())
						oldFile.delete();
					file.renameTo(oldFile);
				}
			}
			else if (this.isTest){
				logger.info("Reading model from "+file.getAbsolutePath());
				 //deserialize model to classifier 
				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(new FileInputStream(this.modelFile));
					this.classifier = (Logistic) ois.readObject();
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			if (this.isTrain && !this.isTest){
				logger.info("The trained model will be stored in "+ file.getAbsolutePath());
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if (this.isTest){
				throw new ConfigurationException("The model specified in the configuration does NOT exist! Please give the correct file path.");
			}
		}
	}

	
	/**
	 * Initializes the data, for training and/or testing
	 * @param config the CommonConfig configuration
	 * @throws ConfigurationException
	 */
	private void initializeData(CommonConfig config)
			throws ConfigurationException {
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}
		this.trainDir = EDA.getString("trainDir");
		if (null == trainDir) {
			if (this.isTrain && !this.isTest) {
				throw new ConfigurationException("Please specify the training data directory.");
			} else {
				logger.warn("Warning: Please specify the training data directory.");
			}
		}
		this.testDir = EDA.getString("testDir");
		if (null == testDir) {
			if (this.isTest && !this.isTrain) {
				throw new ConfigurationException("Please specify the testing data directory.");
			} else {
				logger.warn("Warning: Please specify the testing data directory.");
			}
		}
	}
	
	/**
	 * Initializes the data, for training and/or testing
	 * @throws ConfigurationException 
	 * @param trainDir directory for training data
	 * @param testDir directory for test data
	 */
	private void initializeData(String trainDir, String testDir) throws ConfigurationException {
		this.trainDir = trainDir;
		if (null == trainDir) {
			if (this.isTrain && !this.isTest) {
				throw new ConfigurationException("Please specify the training data directory.");
			} else {
				logger.warn("Warning: Please specify the training data directory.");
			}
		}
		this.testDir = testDir;
		if (null == testDir) {
			if (this.isTest && !this.isTrain) {
				throw new ConfigurationException("Please specify the testing data directory.");
			} else {
				logger.warn("Warning: Please specify the testing data directory.");
			}
		}
		
	}


	/**
	 * Gets the attributes for all internal EDAs (n internal EDAs -> n attributes).
	 * The attributes are named after the internal EDAs and an additional index 
	 * to prevent ambiguities if more than one EDABasic of the same type is used.
	 * @return a FastVector with the attributes
	 */
	private FastVector getAttributes(){
		FastVector attrs = new FastVector();
		for (int i = 0; i < this.edas.size(); i++){
			EDABasic<? extends TEDecision> eda = this.edas.get(i);
			attrs.addElement(new Attribute(eda.getClass().getSimpleName()+i)); 
		}
		return attrs;
	}

	/**
	 * Retrieves confidence scores from all BasicEDAs for one given JCas.
	 * Each BasicEDA instance produces one feature each. N edas -> N features.
	 * @param jcas the JCas to process
	 * @param pairID the according pairID
	 * @return an ArrayList of features for the given JCas.
	 */
		private ArrayList<Double> getFeatures(JCas jcas, int pairID) {
			ArrayList<Double> features = new ArrayList<Double>();
			double[] resultsForPair = new double[this.edas.size()+2];
			for (int i=0; i<this.edas.size(); i++){
				EDABasic<? extends TEDecision> eda = this.edas.get(i);
				//process aCas and get confidence
				TEDecision decision = null;
				try {
					decision = eda.process(jcas);
					logger.debug(eda.getClass().getSimpleName()+i+"'s decision: "+decision.getDecision()+" "+decision.getConfidence());
					
					if (decision.equals(null)){
						throw new EDAException("The internal EDA "+eda.getClass().getSimpleName()+i+"could not process the data." +
								"Please check the internal EDA's configuration");
					}
				} catch (EDAException | ComponentException e) {
					e.printStackTrace();
				}
				double confidence = decision.getConfidence();
				DecisionLabel label = decision.getDecision();
				
				//use a negative sign for non entailment confidence scores
				if (label == DecisionLabel.NonEntailment){
					confidence = confidence * -1;
				}
				features.add(confidence);
				resultsForPair[i+1]= confidence;
				this.results.put(pairID, resultsForPair);
			}
			logger.debug("SimpleMetaEDAConfidenceFeatures features from EDABasic confidences: "+features.toString());
			return features;
		}

}
