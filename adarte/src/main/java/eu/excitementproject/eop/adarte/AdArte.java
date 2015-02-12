package eu.excitementproject.eop.adarte;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.lang.reflect.Constructor;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.lap.PlatformCASProber;


/**
 * The <code>AdArte</code> class implements the <code>EDABasic</code> interface.
 * Given a certain configuration, it can be trained over a specific data set in order to optimize its
 * performance. 
 * 
 * This EDA is based on modeling the Entailment Relations (i.e., Entailment, Not-Entailment) as a 
 * classification problem. First texts (T) are mapped into hypothesis (H) by sequences of editing operations
 * (i.e., insertion, deletion, substitution of text portions) needed to transform T into H, where each edit 
 * operation has a cost associated with it. Then, and this is different from the algorithms which use these 
 * operations to calculate a threshold value that best separates the Entailment Relations from the Not-Entailment
 * ones, the proposed algorithm uses the calculated operations as a feature set to feed a Supervised Learning
 * Classifier System being able to classify the relations between T and H. 
 * 
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since January 2015
 * 
 */
public class AdArte<T extends TEDecision>
		implements EDABasic<EditDistanceTEDecision> {
	
	/**
	 * 
	 * EDA's variables section
	 * 
	 */
	
	/**
	 * the edit distance component to be used
	 */
	private FixedWeightTreeEditDistance component; //it calculates tree edit distance among trees
	
	/**
	 * the logger
	 */
	private final static Logger logger = 
			Logger.getLogger(AdArte.class.getName());

	/**
	 * the training data directory
	 */
	private String trainDIR;
	
	/**
	 * the test data directory
	 */
	private String testDIR;
	
	/**
	 * save the training data set into arff format so that one
	 * can do experiments by using the WEKA Explorer too.
	 */
	private String saveDataSetInArffFormat;
	
	/**
	 * if true the transformations involving matches are considered as features
	 */
	private boolean match;
	
	/**
	 * if true the transformations involving insertions are considered as features
	 */
	private boolean insertion;
	
	/**
	 * if true the transformations involving deletions are considered as features
	 */
	private boolean deletion;
	
	/**
	 * if true the transformations involving replacements are considered as features
	 */
	private boolean replacement;
	
	/**
	 * How the transformations that are used as features have to be 
	 * represented (i.e. LeastSpecificForm | IntermediateForm | GeneralForm)
	 */
	private String transformationForm;
	
	/**
	 * verbosity level
	 */
	private String verbosityLevel;
	
	/**
	 * get the component used by the EDA to calculate the transformations
	 * 
	 * @return the component
	 */
	public FixedWeightTreeEditDistance getComponent() {
		
		return this.component;
		
	}
	
	/**
	 * get the type of component
	 * 
	 * @return the type of component
	 */
    protected String getType() {
    	
    	return this.getClass().getCanonicalName();
    	
    }

	/**
	 * get the training data directory
	 * 
	 * @return the training directory
	 */
	public String getTrainDIR() {
		
		return this.trainDIR;
		
	}
	
	/**
	 * set the training data directory
	 * 
	 */
	public void setTrainDIR(String trainDIR) {
		
		this.trainDIR = trainDIR;
		
	}
	
	/**
	 * get the test data directory
	 * 
	 * @return the test directory
	 */
	public String getTestDIR() {
		
		return this.testDIR;
		
	}
	
	/**
	 * set the test data directory
	 * 
	 */
	public void setTestDIR(String testDIR) {
		
		this.testDIR = testDIR;
		
	}
	
	/*
	 *  if we are training the EDA
	 */
	boolean trainingOperation;
	
	
	/**
	 * 
	 * Classifier's variables section; classifier is the
	 * classifier used by the EDA to classifier T/H pairs
	 * 
	 */

	/** 
	 * The actual classifier
	 */
	//private Classifier classifier;
	private MyClassifier classifier;
	
	/** 
	 * The classifier model to be trained during the training phase
	 * and tested during the testing phase.
	 */
	private String classifierModel;
	
	/** 
	 * The feature set used for training and testing
	 */
	//private Map<String,Integer> featuresList;
	
	/** 
	 * The data set (it is build by using the weka data structures), 
	 * for training and test.
	 */
    //private Instances inputDataset;
    
    /** 
     * The annotation classes, e.g. ENTAILMENT, NONENTAILMENT 
     */
    //private FastVector classesList;
    
    /** 
     * Binary vs weighted features
     */
    private boolean binaryFeature;
    
    /** 
     * test examples
     */
    private DataSet testDataSet;
    
    /** 
     * The classifier evaluation; it contains methods for getting
     * a number of measures like precision, recall and F1 measure.
     */
	//private Evaluation evaluation;
	
	/** 
     * number of folds for cross-validation
     */
	//private final int numFolds = 10;
	
	/** 
	 * If an evaluation has to be done on the 
	 * training data set, e.g. cross validation 
	 * */
	private boolean crossValidation;
	
	/**
	 * Construct an TransformationDriven EDA
	 */
	public AdArte() {
    	
		this.component = null;
		this.classifier = null;
		this.crossValidation = false;
		this.binaryFeature = true; //weighted feature is not possible in the current implementation
		//this.evaluation = null;
        this.trainDIR = null;
        this.testDIR = null;
        this.saveDataSetInArffFormat = null;
        //this.classesList = null;
        //this.featuresList = null;
        this.deletion = true;
        this.match = false;
        this.replacement = true;
        this.insertion = true;
        this.trainingOperation = false;
        //this.testDataSet = null;
        
    }

	
	@Override
	public void initialize(CommonConfig config) 
			throws ConfigurationException, EDAException, ComponentException {
		
		try {
        	
        	// checking the configuration file
			checkConfiguration(config);
			
			// getting the name value table of the EDA; it contains the methods
			// for getting the EDA configuration form the configuration file.
			NameValueTable nameValueTable = config.getSection(this.getType());
			
			// setting the logger verbosity level: INFO, FINE, FINER
			if (this.verbosityLevel == null) {
				this.verbosityLevel = nameValueTable.getString("verbosity-level");
				
				//logger.setUseParentHandlers(false);
				//ConsoleHandler consoleHandler = new ConsoleHandler();
				//consoleHandler.setLevel(Level.parse(this.verbosityLevel));
				//logger.addHandler(consoleHandler);
				//logger.setLevel(Level.parse(this.verbosityLevel));
				
				replaceConsoleHandler(Logger.getLogger(""), Level.ALL);
				logger.setLevel(Level.parse(this.verbosityLevel));
				
			}
			
			// setting the training directory
			if (this.trainDIR == null)
				this.trainDIR = nameValueTable.getString("trainDir");
			
			// setting the test directory
			if (this.testDIR == null)
				this.testDIR = nameValueTable.getString("testDir");
			
			// evaluation on the training data set
			if (this.crossValidation == false)
				this.crossValidation = Boolean.parseBoolean(nameValueTable.getString("cross-validation"));
			
			// binary vs weighted features
			if (this.binaryFeature == false)
				this.binaryFeature = Boolean.parseBoolean(nameValueTable.getString("binary-feature"));
			
			// if the training data set has to be saved in arff format to be used with the weka explorer tool,
			// null for non set
			if (this.saveDataSetInArffFormat == null)
				this.saveDataSetInArffFormat = nameValueTable.getString("save-arff-format");
			
			// decide which type of transformations (i.e. match, insertion, deletion, substitution) has to be
			// considered as features. True for considering it, false otherwise
			String enambledTransforations = nameValueTable.getString("transformations");
			if (enambledTransforations.indexOf(Transformation.MATCH) != -1)
				this.match = true;
			
			if (enambledTransforations.indexOf(Transformation.DELETION) != -1)
				this.deletion = true;
			
			if (enambledTransforations.indexOf(Transformation.INSERTION) != -1)
				this.insertion = true;
			
			if (enambledTransforations.indexOf(Transformation.REPLACE) != -1)
				this.replacement = true;
			
			// the transformation representation to be used for representing the transformation
			// i.e. LeastSpecificForm, IntermediateForm, GeneralForm
			this.transformationForm = nameValueTable.getString("transformation-form");
			
			// classifier initialization
			String classifierName = nameValueTable.getString("classifier");
			
			// getting the classifier parameters
			String[] classifierParameters = nameValueTable.getString("classifier-parameters").split(" ");
			
			// the classifier model trained during the training phase and to be used during the test phase. 
			this.classifierModel = nameValueTable.getString("classifier-model");
			
			// initialize the classifier
			if (this.trainingOperation == true) {
				
				classifier = new MyClassifier(classifierName, classifierParameters, this.classifierModel);
				
			}
			//load the classifier built during the training phase
			else {
				
				classifier = new MyClassifier(this.classifierModel);
				
			}
			
			// calling FixedWeightTreeEditDistance with its default configuration
			//component = new FixedWeightTreeEditDistance();
			
			// component initialization through the configuration file
			String componentName = nameValueTable.getString("components");
			
			// componentName  = "eu.excitementproject.eop.core.component.distance.FixedWeightTreeEditDistance";
			if (this.component == null) {
				
				try {
					
					Class<?> componentClass = Class.forName(componentName);
					Constructor<?> componentClassConstructor = componentClass.getConstructor(CommonConfig.class);
					this.component = (FixedWeightTreeEditDistance) componentClassConstructor.newInstance(config);
					
				} catch (Exception e) {
					
					throw new ComponentException(e.getMessage());
					
				}
				
			}
			
			logger.info("EDA configuration:" + "\n" +
					"training directory:" + this.trainDIR + "\n" +
					"testing directory:" + this.testDIR + "\n" +
					"match transformation enabled:" + this.match + "\n" +
					"deletion transformation enabled:" + this.deletion + "\n" +
					"insertion transformation enabled:" + this.insertion + "\n" +
					"replacement transformation enabled:" + this.replacement + "\n" +
					"transformation representation:" + this.transformationForm + "\n" +
					"classifier:" + this.classifier.toString() + "\n" +
					"classifier model name:" + this.classifierModel + "\n" +
					"cross-validation:" + this.crossValidation + "\n" +
					"binary-feature:" + this.binaryFeature + "\n" +
					"tree edit distance component:" + componentName + "\n"
			);

		} catch (ConfigurationException e) {
			
			throw e;
			
		} catch (Exception e) {
            
			throw new EDAException("Initialization error:" + e.getMessage());
			
		}
		
	}
	
	
	@Override
	public EditDistanceTEDecision process(JCas jcas) 
			throws EDAException, ComponentException {
		
		// the predicted class
		String annotationClass = null;
		// the confidence assigned by the classifier to the class
		double confidence = 0.0;
		// the classified T/H pair
		Pair pair = null;
		
		try {
			
			// get the T/H pair
			pair = JCasUtil.selectSingle(jcas, Pair.class);
			logger.info("processing pair: " + pair.getPairID() + "\n" +
		            "Text: " + pair.getText().getCoveredText() + "\n" +
		            "Hypothesis: " + pair.getHypothesis().getCoveredText());
			
			/**
			 * this records the gold standard answer for this pair. If the pair 
			 * represents a training data, this value is the gold standard answer. If 
			 * it is a null value, the pair represents a problem that is yet to be answered.
			*/
			String goldAnswer = pair.getGoldAnswer(); //get gold annotation
			
			// get the distance between T and H
			double distance = component.calculation(jcas).getDistance();
			
			// get the transformations needed to transform T into H
			List<Transformation> transformations = component.getTransformations();
			
			// binary feature
			// HashSet<String> example_i = new HashSet<String>();
			// weighted feature
			HashMap<String,Integer> example_i = new HashMap<String,Integer>();
			
			//save the transformations to be printed into the log file
			StringBuffer loggerTransformationsBuffer = new StringBuffer();
			loggerTransformationsBuffer.append("number of transformations:" + transformations.size());
			loggerTransformationsBuffer.append("\n");
			
			int transformations_counter = 0;
			Iterator<Transformation> iteratorTransformation = transformations.iterator();
			while(iteratorTransformation.hasNext()) {
				
				transformations_counter++;
				
				Transformation transformation_i = iteratorTransformation.next();
				
				String transformation_i_name = 
						transformation_i.print(this.replacement, this.match, this.deletion, this.insertion, this.transformationForm);
				
				loggerTransformationsBuffer.append("transformation " + 
						transformations_counter + ":" + 
						transformation_i + "\n");
				
				if (transformation_i_name == null)
					continue;
				
				
				if (this.classifier.containsFeature(transformation_i_name)) {
					//weighted feature
					int weight = 1;
					if (example_i.keySet().contains(transformation_i_name)) {
						weight = example_i.get(transformation_i_name).intValue() + 1;
					}
					example_i.put(transformation_i_name, new Integer(weight));
				}
				
			}
			
			// data structure for storing gold annotations (e.g. ENTAILMENT)
			ArrayList<String> annotation = new ArrayList<String>();
			
			// data structure for storing the examples to be used for training
            List<HashMap<String,Integer>> examples = new ArrayList<HashMap<String,Integer>>();
            
			// adding example_i into the list of the examples
			examples.add(example_i);
			
			// adding the annotation of the example_i
			if (goldAnswer != null)
				annotation.add(goldAnswer); //the annotation is in the test set
			else
				annotation.add("?"); //the annotation is not in the test set
			
			//initialize the data set (i.e. declare attributes and classes)
			//Instances testDataSet = initDataSet();
			DataSet testDataSet = new DataSet(classifier.getFeaturesList(), classifier.getClassesList());
			
			//fill the data set
			testDataSet.addExamples(examples, annotation);
			
			if (this.saveDataSetInArffFormat != null) {
				if (this.testDataSet ==  null)
					this.testDataSet = new DataSet(classifier.getFeaturesList(), classifier.getClassesList());
				this.testDataSet.addExamples(examples, annotation);
			}
			
			//the classifier returns with a confidence level for each of the possible
			//classes; following we look for the most probable classes and report
			//the confidence assigned to this class by the classifier
			double[] score = classifier.testClassifier(testDataSet);
			int index = 0;
			for (int i = 0; i < score.length; i++) {
				if (score[i] >= confidence) {
					confidence = score[i];
					index = i;
				}
			}
			
			//get the class label (e.g. Entailment)
			annotationClass = testDataSet.getData().attribute("class").value(index);
			//System.err.println("classAttribute:" + testDataSet.getData().classAttribute());
			//System.err.println("firstInstance:" + testDataSet.getData().firstInstance());
			
			logger.fine("gold standard class label: " + goldAnswer + "\n" +
						"predicted class:" + annotationClass + "\n" +
						"calculated distance:" + distance + "\n\n" +
						loggerTransformationsBuffer.toString() + "\n");			
			
			logger.finer("data set format:" + testDataSet);
		
		} catch (Exception e) {
			
			throw new EDAException("Annotating error:" + e.getMessage());
			
		} 
		
		DecisionLabel decisionLabel = DecisionLabel.getLabelFor(annotationClass);
		
		return new EditDistanceTEDecision(decisionLabel, pair.getPairID(), confidence);
		
	}
	
	
	@Override
	public void shutdown() {
		
		logger.info("shutdown ...");
		
		if (component != null) {
			((FixedWeightTreeEditDistance)component).shutdown();
		}
		this.component = null;
		this.classifier = null;
		this.crossValidation = false;
		this.binaryFeature = true;
		//this.evaluation = null;
        this.trainDIR = null;
        this.testDIR = null;
        //this.classesList = null;
        //this.featuresList = null;
        this.deletion = true;
        this.match = false;
        this.replacement = true;
        this.insertion = true;
        
        try {
	        if (this.trainingOperation == false) {
	        	this.testDataSet.saveDataSet(this.saveDataSetInArffFormat + "_test");
	        }
        } catch (Exception e) {
        	logger.warning(e.getMessage());
        }

        this.saveDataSetInArffFormat = null;
        this.trainingOperation = false;
		
        logger.info("done.");
		
	}
	
	
	@Override
	public void startTraining(CommonConfig config) throws ConfigurationException, EDAException, ComponentException {
		
		logger.info("training ...");
		
		//this is a training phase
		this.trainingOperation = true;
		
		try {

			//initialize the EDA
			initialize(config);

			//if there are no files for training
			File f = new File(trainDIR);
			if (f.exists() == false) {
				throw new ConfigurationException(trainDIR + ":" + f.getAbsolutePath() + " not found!");
			}
				
			//data structure for storing gold annotations (e.g. ENTAILMENT)
		    //for each of the example in the data set
			ArrayList<String> annotation = new ArrayList<String>();
			
			//data structure for storing the examples to be used for training
			//binary feature
            //List<HashSet<String>> examples = new ArrayList<HashSet<String>>();
            
            //weighted features
            List<HashMap<String,Integer>> examples = new ArrayList<HashMap<String,Integer>>();
            		
            File[] files = f.listFiles();
            //sort files on the bases of their id
            Arrays.sort(files);
            
			//reading the training data set
			for (File xmi : files) {
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
				
				//System.err.println(xmi.getName());
				//logger.finer("file: " + xmi.getName());
				
				//fileCounter++;
				//if (fileCounter >100)
				//	break;
				
				// The annotated pair is added into the CAS.
				JCas jcas = PlatformCASProber.probeXmi(xmi, null);
				
				//the T/H pair
				Pair pair = JCasUtil.selectSingle(jcas, Pair.class);
				logger.info("processing pair: " + pair.getPairID() + "\n" +
				            "Text: " + pair.getText().getCoveredText() + "\n" +
				            "Hypothesis: " + pair.getHypothesis().getCoveredText());
				
				//the pair annotation
				String goldAnswer = pair.getGoldAnswer(); //get gold annotation
				
				//get the distance between T and H
                double distance = component.calculation(jcas).getDistance();
                
				//get the transformations to transform T into H
				List<Transformation> transformations = component.getTransformations();
				
				//binary feature
				//HashSet<String> example_i = new HashSet<String>();
				//weighted feature 
				HashMap<String,Integer> example_i = new HashMap<String,Integer>();
				
				//save the transformations to be printed into the log file
				StringBuffer loggerTransformationsBuffer = new StringBuffer();
				loggerTransformationsBuffer.append("number of transformations:" + transformations.size());
				loggerTransformationsBuffer.append("\n");
				
				int transformation_counter = 0;
				
				Iterator<Transformation> iteratorTransformation = transformations.iterator();
				while(iteratorTransformation.hasNext()) {
					Transformation transformation_i = iteratorTransformation.next();
					transformation_counter++;
					
					String transformation_i_name = 
							transformation_i.print(this.replacement, this.match, this.deletion, this.insertion, this.transformationForm);
					
					loggerTransformationsBuffer.append("transformation " + transformation_counter + ":" + transformation_i);
					loggerTransformationsBuffer.append("\n");
					
					if (transformation_i_name == null)
						continue;
					
					if (classifier.addFeature(transformation_i_name));
					
					//weighted feature
					int weight = 1;
					if (example_i.keySet().contains(transformation_i_name)) {
						weight = example_i.get(transformation_i_name).intValue() + 1;
					}
					example_i.put(transformation_i_name, new Integer(weight));
					
					//binary feature
					//example_i.add(transformation_i_name);
					
				}
				
				//commentare per rimuovere feature distanza
				//example_i.add("distance:" + (Math.floor(distance * 100.0) / 100.0));
				
				//creating the classes index starting from 0
				classifier.addClass(goldAnswer);
				
				//add the example_i into the list of the examples
				examples.add(example_i);
				//add the annotation of the example_i
				annotation.add(goldAnswer);
				
				logger.fine("gold standard class label: " + goldAnswer + "\n" +
						"calculated distance:" + distance + "\n\n" +
						loggerTransformationsBuffer.toString() + "\n");		
				
			}
			
			//init the data set (i.e. attribute and classes declaration) for
			//training the classifier
			//Instances trainingDataSet = initDataSet();
			DataSet trainingDataSet = new DataSet(classifier.getFeaturesList(), classifier.getClassesList());
			
			//fill the data set for training the classifier
			//fillDataSet(trainingDataSet, examples, annotation);
			trainingDataSet.addExamples(examples, annotation);
			
			logger.finer("data set:" + trainingDataSet);
			
			logger.info("number of examples:" + examples.size() + "\n" + 
			          "number of features:" + (classifier.getFeaturesSetSize()-1) + "\n" + //-1 due to the fake_attribute
			          "number of classes:" + (classifier.getClassesListSize()-1) + "\n" //-1 due to the fake class
			//logger.info("input data set:\n" + inputDataset);//the data set on arff format
			);
			
			//save the data set into arff format
			if (this.saveDataSetInArffFormat != null)
				trainingDataSet.saveDataSet(this.saveDataSetInArffFormat + "_training");
				//this.saveDataSet(trainingDataSet);
			
			//train the classifier
			classifier.trainClassifier(trainingDataSet);
			
            //cross-validation
            if (this.crossValidation == true) {
            	classifier.evaluateModel(trainingDataSet);
            }
           
		} catch (Exception e) {
			
			throw new EDAException("Training error:" + e.getMessage());
			
		} 
		
		logger.info("done.");
		
	}
	
	
	/**
     * Checks the configuration and raise exceptions if the provided
     * configuration is not compatible with this class
     * 
     * param config the configuration
     *
     * @throws ConfigurationException
     */
	private void checkConfiguration(CommonConfig config) 
			throws ConfigurationException {
		
		//if (config == null)
			//throw new ConfigurationException("Configuration file not found.");
		
	}
	
	
	/**
	 * Get a summary description of the classifier evaluation
	 * 
	 * @return the summary
	 */
	/*
	public String toSummaryString() {
		
		return evaluation.toSummaryString();
		
	}
	*/
	
	
	/**
	 * calculate the estimated error rate
	 * 
	 * @return the estimated error rate
	 */
	/*
	public double errorRate() {
		
		return evaluation.errorRate();
		
	}
	*/
	
	
	/**
     * Replaces the ConsoleHandler for a specific Logger with one that will log
     * all messages. 
     * 
     * @param logger the logger to update.
     * @param newLevel the new level to log.
     */
    private static void replaceConsoleHandler(Logger logger, Level newLevel) {

      // Handler for console (reuse it if it already exists)
      Handler consoleHandler = null;
      // see if there is already a console handler
      for (Handler handler : logger.getHandlers()) {
        if (handler instanceof ConsoleHandler) {
          consoleHandler = handler;
          break;
        }
      }

      if (consoleHandler == null) {
        // there was no console handler found, create a new one
        consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);
      }
      // set the console handler to fine:
      consoleHandler.setLevel(newLevel);
    }
	
	
	/*
	public static void main(String args[]) {
		
		TransformationDrivenEDA<EditDistanceTEDecision> tdEDA;
		
		try {
		
			tdEDA = new TransformationDrivenEDA<EditDistanceTEDecision>();
			
			File configFile = new File("./src/main/resources/configuration-file/TransformationDrivenEDA_EN.xml");
			
			CommonConfig config = new ImplCommonConfig(configFile);
			
			//tdEDA.test();
			LAPAccess lap = new MaltParserEN();
			// process TE data format, and produce XMI files.
			// Let's process English RTE3 data (formatted as RTE5+) as an example. 




			File input = new File("/hardmnt/norris0/zanoli/TBMLEDA/dataset/SICK_train.xml");

			File outputDir  = new File("/tmp/training");

			try {

				lap.processRawInputFormat(input, outputDir); // outputDir will have those XMIs

				System.out.println(input);

				lap.processRawInputFormat(input, outputDir); // outputDir will have those XMIs
			} catch (Exception e)
			{
				System.err.println(e.getMessage()); 
			}
			
			tdEDA.startTraining(config);
			
			tdEDA.shutdown();
			System.exit(0);
			
			System.exit(0);
			
			tdEDA.initialize(config);
			
			File f = new File("/home/scolombo/tbmleda/tmpfiles/");
			
			//build up the dataset from training data
			for (File xmi : f.listFiles()) {
				
				if (!xmi.getName().endsWith(".xmi")) {
					continue;
				}
			
				// The annotated pair is added into the CAS.
				JCas jcas = PlatformCASProber.probeXmi(xmi, null);
				EditDistanceTEDecision edtedecision = tdEDA.process(jcas);
				System.err.println(edtedecision.getPairID() + "\t" + 
						edtedecision.getDecision() + " " + 
						edtedecision.getConfidence());
				
			}
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
	}
	*/
	
}
