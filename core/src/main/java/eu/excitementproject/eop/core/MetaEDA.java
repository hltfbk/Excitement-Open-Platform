package eu.excitementproject.eop.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

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
 * It uses multiple EDABasic instances and their classifying results as features to makes its own
 * decision.  
 * It has two modes:
 * 1) voting: each EDA's DecisionLabel counts as vote for NonEntailment or Entailment.
 * 	MetaEDA goes with the majority. In case of a tie, it decides NonEntailment. 
 *  Note that there is no training in this mode.
 * 2) confidences as features: each EDA's confidence on a decision is taken as a feature
 * 	for a classifier which is trained on the input pairs.
 * 	The trained model is stored to enable loading it again. 
 * 
 * Training in mode 2) is performed with a weka classifier.
 * @author Julia Kreutzer
 *
 */
public class MetaEDA implements EDABasic<MetaTEDecision>{
	
	/**
	 * the logger
	 */
	public final static Logger logger = Logger.getLogger(MaxEntClassificationEDA.class.getName());
	
	/**
	 * the mode: use the EDABasics' confidence scores as features for training (2) or just decide via majority vote (1)
	 */
	private boolean confidenceAsFeature; //if true: 2nd mode
	
	/**
	 * contains all internal basic EDAs
	 */
	private ArrayList<EDABasic<? extends TEDecision>> edas; 

	/**
	 * the language
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
	private Classifier classifier;
	
	/**
	 * when true: in training
	 */
	private boolean isTrain = false; 
	
	/**
	 * when true: in testing
	 */
	private boolean isTest = false; 
	//both isTest and isTrain needed, as initialization can take place before testing or training is defined
	
	public String getModelFile() {
		return modelFile;
	}


	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}


	public boolean isTest() {
		return isTest;
	}


	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}

	/**
	 * when true: overwrites existing models while training, false: appends "_old" to existing model file
	 */
	private boolean overwrite = false;

	/**
	 * Constructs a new MetaEDA instance with a list of already initialized 
	 * basic EDAs.
	 * @param edas list of already initialized EDABasic instances
	 */
	public MetaEDA(ArrayList<EDABasic<? extends TEDecision>> edas){
		this.edas = edas;
		logger.info("new MetaEDA with "+edas.size()+" internal EDABasics");
	}


	@Override
	public void initialize(CommonConfig config) throws ConfigurationException,
			EDAException, ComponentException {
		logger.info("initialize MetaEDA");
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
	 * initialize the language flag from the configuration,
	 * check that the correct activated EDA is specified in config,
	 * and set the mode (use confidence as features or not).
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
			throw new ConfigurationException("Please specify MetaEDA's mode: use confidence scores as features or not.");
		}
	}

	/**
	 * initialize the model
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
				throw new ConfigurationException("Please specify MetaEDA's overwrite mode.");
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
					this.classifier = (Classifier) ois.readObject();
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
	 * initialize the data, for training and/or testing
	 * @param config the CommonConfig configuration
	 * @throws ConfigurationException
	 */
	public final void initializeData(CommonConfig config)
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
				logger.warning("Warning: Please specify the training data directory.");
			}
		}
		this.testDir = EDA.getString("testDir");
		if (null == testDir) {
			if (this.isTest && !this.isTrain) {
				throw new ConfigurationException("Please specify the testing data directory.");
			} else {
				logger.warning("Warning: Please specify the testing data directory.");
			}
		}
	}

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
			
//			//files in training directory
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
				String goldAnswer = pair.getGoldAnswer(); //get gold annotation
				
				//get features from BasicEDAs' confidence scores
				ArrayList<Double> scores = getFeatures(jcas);
								
				//Store gold answer
				goldAnswers.add(goldAnswer);
				
				//add new instance to dataset
				Instance instance = new Instance(scores.size());
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
			
			// Train the classifier
			logger.info("Training the classifier...");
			
			this.classifier = new NaiveBayes();
			try {
				classifier.buildClassifier(instances);
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

	@Override
	public MetaTEDecision process(JCas aCas) throws EDAException,
			ComponentException {
		
		//generate the confidence features
		List<Double> features = getFeatures(aCas);
		
		Pair pair = JCasUtil.selectSingle(aCas, Pair.class);
		
		DecisionLabel dLabel;
		
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
			
//			System.out.println(instance.toString());
			
			//classify instance
			double result = 0.0;
			try {
				result = this.classifier.classifyInstance(instances.firstInstance());
				instances.firstInstance().setClassValue(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Determine the result label
			String label = instances.firstInstance().classAttribute().value((int)result);
//			System.out.println("label "+label);
			
			// Convert to a DecisionLabel instance
			if (label.toUpperCase().equals(DecisionLabel.Entailment.toString().toUpperCase()))
				dLabel = DecisionLabel.Entailment;
			else
				dLabel = DecisionLabel.NonEntailment;
//			System.out.println("dLabel "+dLabel);
		} else {
			//majority vote
			int decision = 0;
			for (Double feature : features) {
				if (feature < 0){
					decision -= 1;
				} else {
					decision += 1;
				}
			}
			if (decision < 0){
				dLabel = DecisionLabel.NonEntailment;
			} else {
				dLabel = DecisionLabel.Entailment;
			}
		}
		return new MetaTEDecision(dLabel, pair.getPairID());
	}

	@Override
	public void shutdown() {
		// disengage resources
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
	 * get the attributes for all internal EDAs, n internal EDAs -> n attributes
	 * the attributes are named after the internal EDAs and an additional index
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
	 * Retrieves confidence scores from all BasicEDAs for one jcas
	 * @param jcas the JCas to process
	 * @return an ArrayList of features for the given JCas. N edas -> N features.
	 */
		private ArrayList<Double> getFeatures(JCas jcas) {
			ArrayList<Double> features = new ArrayList<Double>();
			for (int i=0; i<this.edas.size(); i++){
				EDABasic<? extends TEDecision> eda = this.edas.get(i);
				//process aCas and get confidence
				TEDecision decision = null;
				try {
					decision = eda.process(jcas);
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
			}
	//		logger.info("MetaEDA features from EDABasic confidences: "+features.toString());
			return features;
		}

	public boolean isConfidenceAsFeature() {
		return confidenceAsFeature;
	}

	public void setConfidenceAsFeature(boolean confidenceAsFeature) {
		this.confidenceAsFeature = confidenceAsFeature;
	}

	public ArrayList<EDABasic<? extends TEDecision>> getEdas() {
		return edas;
	}

	public void setEdas(ArrayList<EDABasic<? extends TEDecision>> edas) {
		this.edas = edas;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getModelDir() {
		return modelFile;
	}

	public void setModelDir(String modelDir) {
		this.modelFile = modelDir;
	}

	public String getTrainDir() {
		return trainDir;
	}

	public void setTrainDir(String trainDir) {
		this.trainDir = trainDir;
	}

	public String getTestDir() {
		return testDir;
	}

	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public boolean isTrain() {
		return isTrain;
	}

	public void setTrain(boolean isTrain) {
		this.isTrain = isTrain;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public static Logger getLogger() {
		return logger;
	}

}
