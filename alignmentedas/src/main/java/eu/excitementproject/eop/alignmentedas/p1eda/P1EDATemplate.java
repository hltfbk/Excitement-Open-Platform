/**
 * 
 */
package eu.excitementproject.eop.alignmentedas.p1eda;

import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.alignmentedas.p1eda.classifiers.EDABinaryClassifierFromWeka;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.ClassifierException;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.DecisionLabelWithConfidence;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.EDAClassifierAbstraction;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.FeatureValue;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.LabeledInstance;
import eu.excitementproject.eop.alignmentedas.p1eda.subs.ParameterValue;
import eu.excitementproject.eop.common.DecisionLabel;
//import eu.excitementproject.eop.alignmentedas.p1eda.subs.ParameterValue;
import eu.excitementproject.eop.common.EDABasic;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.PlatformCASProber;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import static eu.excitementproject.eop.lap.PlatformCASProber.probeCas; 

/**
 * This is a template, abstract class for P1 EDA. 
 * 
 * <H3> What this template class is for? </H3>
 * <P> 
 * It provides basic "flow" for process and training of alignment-based EDA. 
 * The template is an "abstract" class, and supposed to be extended to become 
 * an actual Entailment Decision Algorithm (EDA) class.  
 * 
 * <P> 
 * The following two methods *must* be extended (override) 
 * <UL>
 *   <LI> addAlignment() 
 *   <LI> evaluateAlignments() 
 * </UL>
 * 
 * <P>
 * The following methods are optional to be extended (EDA can work and classify 
 * Entailment, even though they are not overridden). They provide optional capabilities.   
 * 
 * <UL>
 *   <LI> TODO write 
 *   <LI> 
 * </UL>
 * 
 * <P> It is recommended to check actual example codes to see how you make 
 * an alignment-based EDA by extending this abstract class. Please check the following 
 * classes: {@link ClassName} TODO update 
 * 
 * <H3> Classifier capability is embedded within the template </H3> 
 * <P> 
 * Classification capability of this template is provided by using Weka 
 * classifier. Changing the classifier and option within Weka is simple: 
 * TODO change this, and this. 
 * 
 * If you want to use some other classifiers; 
 * TODO check and write this. (e.g. one with Rui's classifier) 
 * 
 * Please see the following document for more info: 
 * TODO fill in URL 
 * 
 * @author Tae-Gil Noh
 *
 */

public abstract class P1EDATemplate implements EDABasic<TEDecisionWithAlignment> {
	
	/**
	 * the language
	 */
	protected String language;

	/**
	 * the training data directory
	 */
	protected String trainDIR = null;

	/**
	 * the test data directory
	 */
	protected String testDIR = null;


	/**
	 * the model file 
	 */
	protected String modelFile = null;
	
	/**
	 * The default, no argument constructor for this abstract class. Does nothing 
	 * but initializing two mandatory final fields. They are: logger and classifier. 
	 *  
	 * This constructor does not set evaluateAlignmentParameters. (it will be set as null) 
	 * If your evaluateAlignment override *does not* require parameters (e.g. simple feature
	 * extractors that does not require parameters); then using this constructor is enough. 
	 * (evaluateAlignments() will be always called with null). 
	 * 
	 * For example, see SimpleWordCoverageP1EDA. 
	 * 
	 */
	public P1EDATemplate() throws EDAException 
	{
		this(null); 
	}
	
	
	/**
	 * 
	 * The main constructor for this abstract class. 
	 * 
	 * It does two things: initializing logger + classifier, and store Parameter value 
	 * 
	 * The constructor gets one Vector of parameters: This parameter vector is so called 
	 * "Feature Extractor parameters" or "Evaluate Alignment parameters" -- and the value 
	 * will be passed to evaluateAlignments(). 
	 * 
	 * If your evaluateAlignment override *require* parameters (e.g. weights for each 
	 * aligner, etc), then you have to use this constructor. 
	 * 
	 * @param evaluateAlignmentParameter
	 * @throws EDAException
	 */
	public P1EDATemplate(Vector<ParameterValue> evaluateAlignmentParameter) throws EDAException
	{
		this.logger = Logger.getLogger(getClass()); 
		this.classifier = prepareClassifier();  
		this.evaluateAlignmentParameters = evaluateAlignmentParameter; 
	}
		
	public TEDecisionWithAlignment process(JCas eopJCas) throws EDAException 
	{
		// Here's the basic step of P1 EDA's process(), outlined by the template.  
		// Note that, the template assumes that you override each of the step-methods. 
		// (although you are free to override any, including this process()). 
		
		logger.debug("process() has been called with CAS " + eopJCas);
		
		// Step 0. check JCas: a correct one with all needed annotations? 
		checkInputJCas(eopJCas); 
		String pairID = getTEPairID(eopJCas); 
		logger.info("processing pair with ID: " + pairID); 

		// Step 1. add alignments. The method will add various alignment.Link instances
		// Once this step is properly called, the JCas holds alignment.Link data in it. 
		logger.debug("calling addAlignments"); 
		addAlignments(eopJCas); 
				
		// Step 2. (this is an optional step.) The method will interact / output 
		// the added alignment links for debug / analysis purpose. (for Tracer)  
		logger.debug("calling visualizeAlignments"); 
		visualizeAlignments(eopJCas); 
		
		// Step 3. 
		logger.debug("calling evaluateAlignments"); 
		Vector<FeatureValue> featureValues = evaluateAlignments(eopJCas, evaluateAlignmentParameters); 
		logger.debug("evaluateAlignments returned feature vector as of; "); 
		logger.debug(featureValues.toString()); 
		
		// Step 4. (this is also an optional step.) 
		logger.debug("calling evaluateAlignments"); 
		visualizeEdaInternals(); 
		
		// Step 5. 
		// Classification. 
		logger.debug("calling classifyEntailment"); 
		DecisionLabelWithConfidence result = classifyEntailment(featureValues); 
		
		// Finally, return a TEDecision object with CAS (which holds alignments) 
		logger.debug("TEDecision object generated and being returned: " + result.getLabel() + ", " + result.getConfidence()); 
		return new TEDecisionWithAlignment(result.getLabel(), result.getConfidence(), pairID, eopJCas); 
	}
	
	public void initialize(CommonConfig conf) throws EDAException
	{
<<<<<<< HEAD
		//getting the name value table of the EDA
		NameValueTable nameValueTable;
		try {
			nameValueTable = conf.getSection(this.getClass().getCanonicalName());	
			
			//setting the training directory
			if (this.trainDIR == null)
				this.trainDIR = nameValueTable.getString("trainDir");
		
			//setting the test directory
			if (this.testDIR == null)
				this.testDIR = nameValueTable.getString("testDir");
			
			// setting the model file
			if (this.modelFile == null)
				this.modelFile = nameValueTable.getString("modelFile");
			
			initialize(new File(modelFile));
			
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

=======
		NameValueTable nameValueTable;
		try {
			nameValueTable = conf.getSection(this.getClass().getCanonicalName());	
			File modelFile = nameValueTable.getFile("modelFile"); 
			initialize(modelFile);
		} catch (ConfigurationException e) {
			throw new EDAException ("Reading configuration data failed: " + e.getMessage(), e); 
		}
>>>>>>> upstream/master
	}
	
	public void initialize(File classifierModelFile) throws EDAException
	{		
		try 
		{
			classifier.loadClassifierModel(classifierModelFile); 
		}
		catch (ClassifierException ce)
		{
			throw new EDAException("Loading classifier model and/or parameter failed: ", ce); 
		}
	}

	public void startTraining(CommonConfig conf) throws EDAException 
	{
<<<<<<< HEAD
		// TODO read from common config, and call argument version,
=======
		// read from common config, and call argument version,
>>>>>>> upstream/master
		NameValueTable nameValueTable;
		try {
			nameValueTable = conf.getSection(this.getClass().getCanonicalName());	
			
<<<<<<< HEAD
			//setting the training directory
			if (this.trainDIR == null)
				this.trainDIR = nameValueTable.getString("trainDir");
		
			//setting the test directory
			if (this.testDIR == null)
				this.testDIR = nameValueTable.getString("testDir");
			
			// setting the model file
			if (this.modelFile == null)
				this.modelFile = nameValueTable.getString("modelFile");
			
			startTraining(new File(trainDIR), new File(modelFile));
			
		} catch (ConfigurationException | EDAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

=======
			File trainDir = nameValueTable.getFile("trainDir"); 
			File modelFileToCreate = nameValueTable.getFile("modelFile"); 
			
			startTraining(trainDir, modelFileToCreate); 
		} catch (ConfigurationException ce) {
			throw new EDAException("Reading configuration from CommonConfig failed: " + ce.getMessage(), ce); 
		}	
>>>>>>> upstream/master
	}
	
	
	// TODO: for parameter optimization. Once parameter optimization comes in, 
	// training-sub methods will be provided. 
	// "Training Classifier"
	// "Train Classifier With Parameter Optimizations" 
	
	public void startTraining(File dirTrainingDataXMIFiles, File classifierModelToStore) throws EDAException 
	{
		
		// This work method will read Xmi files and convert them to labeled feature vectors
		// what we call as "LabeledInstance":
		// The method does so, by calling "addAlignments", "evaluateAlignments" on 
		// each of the annotated training(gold) data. 
		
		
		List<LabeledInstance> trainingSet = makeLabeledInstancesFromXmiFiles(dirTrainingDataXMIFiles); 
		
		// finally, calling classifier abstract to train a model 
		try
		{
			classifier.createClassifierModel(trainingSet); 
		}
		catch (ClassifierException ce)
		{
			throw new EDAException("Underlying classifier thrown exception while training a model", ce); 
		}
		
		// and store the model and parameters 
		try 
		{
			classifier.storeClassifierModel(classifierModelToStore);
		}
		catch (ClassifierException ce)
		{
			throw new EDAException("Underlying classifier thrown exception while deserializing a model", ce); 
		}
	}
	
	/**
	 * A method to be used for evaluation. The method reads XMI files (with labels) and 
	 * use the current model (loaded or trained) and evaluate it over the give XMI files. 
	 * 
	 * returns a List of double values. They are: (accuracy, f1, prec, recall, true positive ratio, true negative ratio) 
	 * 
	 * TODO: CONSIDER: this needs to be broken down into two methods.... or not?
	 * (decision - for now, as is)   
	 * 
	 * Hmm. Let's say, what is the common step in "optimize" 
	 *  
	 * [PRE]
	 * 1) Load XMIS (ONCE) 
	 * 2) Add alignments (also ONCE) 
	 * 
	 * [MAIN-LOOP] while exploring (search) best parameters (parameter population, one individual parameter) 
	 * 3) Make a list of labeled instances, with one-individual parameter, and calling evaluateAlignments (MULTIPLE TIMES) 
	 * 4) Train a model with the set, evaluate it. 
	 * 
	 * [POST] 
	 * 5) report the best result, with best individual parameter 
	 * 
	 * Okay, can we reuse, share more from the above? (maybe not. let's worry later. This method itself is almost free.) 
	 * 
	 * @return	a double list: (accuracy, f1, prec, recall, true positive ratio, true negative ratio) 

	 */
	public List<Double> evaluateModelWithGoldXmis(File annotatedXmiFiles) throws EDAException
	{		
		// read annotatedXmiFiles, and make a set of labeled instances, 
		// by calling the utility method (the same one used in startTraining()) 
		List<LabeledInstance> goldData = makeLabeledInstancesFromXmiFiles(annotatedXmiFiles); 


		// ask the classifier to evaluate it (with current loaded/trained model) on the provided labeled data 
		List<Double> evaluationResult = null;
		try {
			evaluationResult = classifier.evaluateClassifier(goldData); 
		}
		catch (ClassifierException ce)
		{
			throw new EDAException ("The classifier was not ready for evalute (make sure a compatible model properly trained and loaded..)", ce); 
		}
		
		return evaluationResult; 
	}
	
	public void shutdown()
	{
		// This template itself has nothing to close down. 
	}
			
	
	/*
	 * Mandatory methods (steps) that should be overridden. 
	 */
	
	/**
	 * @param input
	 * @throws EDAException
	 */
	protected abstract void addAlignments(JCas input) throws EDAException; 
		
	/**
	 * @param aJCas
	 * @return
	 * @throws EDAException
	 */
	protected abstract Vector<FeatureValue> evaluateAlignments(JCas aJCas, Vector<ParameterValue> featureExtractorParameters) throws EDAException; 
	
	
//	/**
//	 * 
//	 * @return
//	 */
//	protected abstract Vector<ParameterValue> prepareEvaluateAlignmentParameters(); 

	
	/* 
	 *  Optional methods (steps) that can be overridden.    
	 */  
	
	protected void visualizeAlignments(JCas CASWithAlignments)
	{
		// Template default is doing nothing. 
	}
	
	protected void visualizeEdaInternals() 
	{
		// Template default is doing nothing. 
	}
		
	/**
	 * 	Optional methods (steps) that can be overridden. --- but these 
	 *  methods provide default functionalities. You can extend if you want. 
	 *  But default implementations would also work as well.  
	 */
	
	protected EDAClassifierAbstraction prepareClassifier() throws EDAException
	{
		try {
			return new EDABinaryClassifierFromWeka(); 
		}
		catch (ClassifierException ce)
		{
			throw new EDAException("Preparing an instance of Classifier for EDA failed: underlying Classifier raised an exception: ", ce); 
		}
	}	
	
	/**
	 * This method will be used to check input CAS for P1EDA flow. 
	 * As is, it will do the basic check of CAS as EOP CAS input, via PlatformCASProber.probeCAS(). 
	 * 
	 * If you want to do additional checks, such as existence of specific LAP annotations, 
	 * You can extend this class to do the checks. 
	 * 
	 * EXTENSION of this method is optional, and not mandatory.  
	 * 
	 * @param input JCas that is given to your EDA  
	 * @throws EDAException If the given JCas was not well-formed for your EDA, you should throw this exception
	 */
	protected void checkInputJCas(JCas input) throws EDAException 
	{
		try {
			probeCas(input, null); 
		}
		catch (LAPException e)
		{
			throw new EDAException("Input CAS is not well-formed CAS as EOP EDA input.", e); 
		}
	}

	protected DecisionLabelWithConfidence classifyEntailment(Vector<FeatureValue> fValues) throws EDAException
	{		
		DecisionLabelWithConfidence dl = null; 
		try {
			dl = classifier.classifyInstance(fValues); 
		}
		catch (ClassifierException ce)
		{
			throw new EDAException("underlying classifier throw exception", ce); 
		}
		
		return dl; 
	}
	
	//	
	// utility methods 
	
	protected List<LabeledInstance> makeLabeledInstancesFromXmiFiles(File xmiDir) throws EDAException
	{
		List<LabeledInstance> labeledData = new ArrayList<LabeledInstance>(); 
		
		// walk each XMI files in the Directory ... 
		File[] files =  xmiDir.listFiles(); 
		if (files == null)
		{
			throw new EDAException("Path " + xmiDir.getAbsolutePath() + " does not hold XMI files"); 
		}
		Arrays.sort(files); 
		
		for (File f : files)
		{
			// is it a XMI file?
			// 
			logger.info("Working with file " + f.getName()); 
			if(!f.isFile()) 
			{	// no ... 
				logger.warn(f.toString() + " is not a file... ignore this"); 
				continue; 
			}
			if(!f.getName().toLowerCase().endsWith("xmi")) // let's trust name, if it does not end with XMI, pass
			{
				logger.warn(f.toString() + " is not a XMI file... ignoring this"); 
				continue; 
			}
			
			// So, we have an XMI file. Load in to CAS 
			JCas aTrainingPair = null; 
			try {
				 aTrainingPair = PlatformCASProber.probeXmi(f, null);
			}
			catch (LAPException le)
			{
				logger.warn("File " + f.toString() + " looks like XMI file, but its contents are *not* proper EOP EDA JCas"); 
				throw new EDAException("failed to read XMI file into a JCas", le); 
			}
			String pairID = getTEPairID(aTrainingPair); 
			logger.info("processing pair with ID: " + pairID); 
			
			// convert it into one LabeledInstance by calling 
			// addAlignments and evaluateAlignments on each of them 
			logger.debug("adding alignments..."); 
			addAlignments(aTrainingPair);
			
			logger.debug("evaluating alignments..."); 
			Vector<FeatureValue> fv = evaluateAlignments(aTrainingPair, evaluateAlignmentParameters); 
			DecisionLabel l = getGoldLabel(aTrainingPair); 
			if (l == null)
			{
				throw new EDAException("Gold data has been given to be used as a Labeled Instance: However, the CAS holds no Gold Label!"); 
			}
			
			LabeledInstance ins = new LabeledInstance(l, fv); 
		
			logger.debug("a labeled instance has been generated from XMI file " + f.getName()); 
			logger.debug(fv.toString() + ", " + l.toString()); 
			labeledData.add(ins); 	
		}
		
		return labeledData; 
	}	

	
	
	/**
	 * 
	 * Get Pair ID from a JCas 
	 * 
	 * @param aJCas
	 * @return
	 * @throws EDAException
	 */
	protected String getTEPairID(JCas aJCas) throws EDAException
	{
		String id = null; 
		
		// check entailment pair, 
		FSIterator<TOP> iter = aJCas.getJFSIndexRepository().getAllIndexedFS(Pair.type); 
		if (iter.hasNext())
		{
			Pair p = (Pair) iter.next(); 
			id = p.getPairID(); 
			
			if (iter.hasNext())
			{
				logger.warn("This JCas has more than one TE Pairs: This P1EDA template only processes single-pair inputs. Any additional pairs are being ignored, and only the first Pair will be processed.");
			}
			return id; 
		}
		else
		{
			throw new EDAException("Input CAS is not well-formed CAS as EOP EDA input: missing TE pair"); 
		}
	}
	
	/**
	 * get Gold Label from an annotated JCas with Entailment.Pair 
	 * 
	 * @param aJCas
	 * @return
	 * @throws EDAException
	 */
	protected DecisionLabel getGoldLabel(JCas aJCas) throws EDAException 
	{
		String labelString; 
		DecisionLabel labelEnum; 
		
		FSIterator<TOP> iter = aJCas.getJFSIndexRepository().getAllIndexedFS(Pair.type); 
		if (iter.hasNext())
		{
			Pair p = (Pair) iter.next(); 
			labelString = p.getGoldAnswer(); 
			
			if (labelString == null) // there is no gold answer annotated in this Pair
				return null; 
			
			labelEnum = DecisionLabel.getLabelFor(labelString); 
			
			if (iter.hasNext())
			{
				logger.warn("This JCas has more than one TE Pairs: This P1EDA template only processes single-pair inputs. Any additional pairs are being ignored, and only the first Pair will be processed.");
			}
			return labelEnum; 
		}
		else
		{
			throw new EDAException("Input CAS is not well-formed CAS as EOP EDA input: missing TE pair"); 
		}
	}
	
	/**
	 * A check utility method. 
	 * A static utility method for checking, if or not, the CAS has a specific 
	 * type of annotation instances, or not. 
	 * 
	 * e.g. haveCASAnnotationType(aJCas, 
	 * 
	 * @param aJCas
	 * @param annot
	 * @return
	 */
	protected static <T extends Annotation> boolean haveCASAnnotationType(JCas aJCas, Class<T> type) throws CASException
	{
		// returns false, if both of the views do not have the requested type. 
		JCas hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);

		if (JCasUtil.select(hypoView, type).size() > 0)
			return true; 

		JCas textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);

		if (JCasUtil.select(textView, type).size() > 0)
			return true; 

		return false; 
	}

	

	/**
	 * This is the vector of parameters, that will be stored at the init time 
	 * (at initialize() for processing, and start_training() time for training) and 
	 * that will be passed to internal method evaluateAlignments().  
	 * 
	 * Within this P1EDA Template, this value is a fixed value, where it is set as 
	 * a configuration. For "Optimizing" this value ... TODO see this extended template 
	 * 
	 */
	protected Vector<ParameterValue> evaluateAlignmentParameters; 
	
	
	/**
	 * This is a vector of parameters, that has been "trained" in one startTraining() session. 
	 * This is internal parameters that and external "parameter" optimizer 
	 * 
	 */
	protected Vector<ParameterValue> internalParameters = null; 
	
	//
	// private final fields... 
	//
	private final EDAClassifierAbstraction classifier; 
	protected final Logger logger; 
	
	// some constants 
//	public final String CLASSIFIER_MODEL_POSTFIX = ".classifier.model"; 
//	public final String PARAMETER_SER_POSTFIX = ".parameters.ser"; 
	
	
}
