package eu.excitementproject.eop.adarte;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;

import treedist.EditScore;
import treedist.Mapping;
import treedist.TreeEditDistance;

import eu.excitementproject.eop.common.component.distance.DistanceCalculation;
import eu.excitementproject.eop.common.component.distance.DistanceComponentException;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;


/**
 * The <code>FixedWeightedTreeEditDistance</code> class implements the DistanceCalculation interface.
 * Given a pair of T-H, each of them represented as a sequences of tokens, the edit distance between 
 * T and H is the minimum number of operations required to convert T to H. 
 * FixedWeightedTreeEditDistance implements the simplest form of weighted edit distance that simply uses a 
 * constant cost for each of the edit operations: match, substitute, insert, delete.
 * 
 * The component uses an implementation of Zhang and Shasha's algorithm [Zhang89] for calculating tree edit distance
 * that is kindle make available by Yuya Unno from this site: https://github.com/unnonouno/tree-edit-distance
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since January 2015
 * 
 */
@SuppressWarnings("deprecation")
public class FixedWeightTreeEditDistance implements DistanceCalculation {

	/**
	 * The alignment component that finds the alignments between the tokens in T and those in H
	 */
	private LexicalAligner aligner;
	/**
	 * The transformations obtained transforming T into H
	 */
    private List<Transformation> transformations;
	/**
	 * weight for match
	 */
    private final double mMatchWeight = 0;
    /**
	 * weight for delete
	 */
    private final double mDeleteWeight = 1;
    /**
	 * weight for insert
	 */
    private final double mInsertWeight = 1;
    /**
	 * weight for substitute
	 */
    private final double mSubstituteWeight = 1;
    /**
	 * the activated instances
	 */
    private String instances;
    /**
     *  words matching considering: lemma-dprel | dprel | lemma
     */
    private String wordMatch;
    
    /**
     * the alignments produced by the alignment components
     */
    private Alignments alignments;
    /**
     * if the punctuation has to be removed from the trees
     */
    private boolean punctuationRemoval;
	/**
	 * verbosity level
	 */
	private String verbosityLevel;
	
    /**
     *  the logger
     */
	private final static Logger logger = Logger.getLogger(FixedWeightTreeEditDistance.class.getName());
    
    
    /**
     * Construct a fixed weight edit distance
     */
	public FixedWeightTreeEditDistance() throws ComponentException {
    	
		logger.info("creating an instance of " + this.getComponentName() + " ...");
		
		// the produced transformations
        this.transformations = null;
        // the activated instances
        this.instances = null;
        // the alignment component
        this.aligner = null;
        // the alignments produced by the alginer components
        this.alignments = null; 
        // matches among words, considering: lemma-dprel | dprel | lemma
        this.wordMatch = "lemma-dprel";
        
        //setting the logger verbosity level
        this.verbosityLevel = "INFO";
		//logger.setUseParentHandlers(false);
		//ConsoleHandler consoleHandler = new ConsoleHandler();
		//consoleHandler.setLevel(Level.parse(this.verbosityLevel));
		//logger.addHandler(consoleHandler);
		//logger.setLevel(Level.parse(this.verbosityLevel));
		
		replaceConsoleHandler(Logger.getLogger(""), Level.ALL);
		logger.setLevel(Level.parse(this.verbosityLevel));
        
		logger.fine("matching words based on:" + this.wordMatch);
		
        // if the stop words have to be removed
        this.punctuationRemoval = true;
        logger.fine("punctuation removal enabled:" + punctuationRemoval);

        // default lexical alignment component
        String componentName = 
        		"eu.excitementproject.eop.core.component.alignment.lexicallink.LexicalAligner";
        
        logger.fine("using:" + componentName + " for creating the alignments");
        
    }

	
    /** 
	 * Constructor used to create this object. 
	 * 
	 * @param config the configuration
	 * 
	 */
    public FixedWeightTreeEditDistance(CommonConfig config) throws ConfigurationException, ComponentException {
    
    	// the produced transformations
    	this.transformations = null;
    	// the activated instances
        this.instances = null;
        // the alignment component
        this.aligner = null;
        // the alignments produce by the aligner
        this.alignments = null; 
        //matches among words, considering: lemma-dprel | dprel | lemma
        this.wordMatch = null;
        // if the stop words have to be removed
        this.punctuationRemoval = false;
        
    	logger.info("creating an instance of " + this.getComponentName() + " ...");
    
        try {
        	
	        //get the component configuration
	    	NameValueTable componentNameValueTable = 
	    			config.getSection(this.getClass().getCanonicalName());
	    	
	    	//get the selected instance
	    	this.instances = componentNameValueTable.getString("instance");
	    	
	    	//get the math type to be used to compare words
	    	this.wordMatch = componentNameValueTable.getString("node-match");
	    	
	    	//get the instance configuration
	    	NameValueTable instanceNameValueTable = 
	    			config.getSubSection(this.getClass().getCanonicalName(), instances);
	    	
			//setting the logger verbosity level
			this.verbosityLevel = instanceNameValueTable.getString("verbosity-level");
			//logger.setUseParentHandlers(false);
			//ConsoleHandler consoleHandler = new ConsoleHandler();
			//consoleHandler.setLevel(Level.parse(this.verbosityLevel));
			//logger.addHandler(consoleHandler);
			//logger.setLevel(Level.parse(this.verbosityLevel));
			
			replaceConsoleHandler(Logger.getLogger(""), Level.ALL);
			logger.setLevel(Level.parse(this.verbosityLevel));
			
			//get the configuration file of the alignment component
			String configurationFile = 
					instanceNameValueTable.getString("configuration-file");
			
	    	//get the alignment component configuration
			String componentName = 
					instanceNameValueTable.getString("alignment-component");
			
			//if the punctuation has to be removed from dependencies trees
			this.punctuationRemoval = Boolean.parseBoolean(instanceNameValueTable.getString("punctuation-removal"));
			
			//create an instance of the alignment component
	    	if (componentName != null && !componentName.equals("")) {
				
				try {
					
					Class<?> componentClass = Class.forName(componentName);
					Constructor<?> componentClassConstructor = componentClass.getConstructor(CommonConfig.class);
					File configFile = new File(configurationFile);
					ImplCommonConfig commonConfig = new ImplCommonConfig(configFile);
					this.aligner = (LexicalAligner) componentClassConstructor.newInstance(commonConfig);
					
				} catch (Exception e) {
					
					throw new ComponentException(e.getMessage());
					
				}
				
			}
	    	
	    	logger.fine("word matching:" + this.wordMatch + "\n" +
	    			"punctuation removal enabled:" + punctuationRemoval + "\n" +
	    			"aligner component:" + componentName);
	    			
	    	
    	} catch (ConfigurationException e) {
    		
    		throw new ComponentException(e.getMessage());
    		
    	} catch (Exception e) {
		
    		throw new ComponentException(e.getMessage());
		
    	}
        
        logger.info("done.");
    	
    }
    
    
    @Override
    public String getComponentName() {
    
    	return "FixedWeightTreeEditDistance";
	
	}
    
    
    @Override
    public String getInstanceName() {
    	
    	return instances;
    	
    }
    
    
    /**
     * Get the transformations used to transform T into H
     * 
     * @return the transformations
     * 
     */
    public List<Transformation> getTransformations() {

    	return this.transformations;
    	
    }
    
    
    /** 
	 * shutdown the component and the used resources
	 */
	public void shutdown()  {
		
		logger.info("shutdown ...");
		
		try {
			if (this.aligner != null)
				this.aligner.close();
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		
		this.transformations =  null;		 
		this.instances = null;
		this.alignments = null;
		this.punctuationRemoval = false;
		
		logger.info("done.");
		
	}
    
    
    @Override
    public DistanceValue calculation(JCas jcas) throws DistanceComponentException {

    	DistanceValue distanceValue = null;
    	
    	try {
    		
    		//get the alignments between T and H produced by the alignment component
    		alignments = new Alignments(aligner, jcas);
    		
    	    //get the Text
	    	JCas tView = jcas.getView(LAP_ImplBase.TEXTVIEW);
	    	//get the dependency tree of Text
	    	String t_tree = DependencyTreeUtils.cas2CoNLLX(tView);
	    	logger.finer("\nThe Tree of Text:\n" + t_tree);
	    	
	    	// check if T contains multiple sentences
	    	if(DependencyTreeUtils.checkMultiSentences(t_tree)) {
	    		logger.finer("This fragment contains multiple sentences. They will be merged!");
	    		t_tree = DependencyTreeUtils.mergeTrees(t_tree);
	    		logger.finer("\nThe Merged Tree of Text:\n" + t_tree);
	    	}
	    	
	    	// check if T contains multiple trees
	    	if(DependencyTreeUtils.checkMultiTree(t_tree)) {
	    		logger.finer("This fragment contains multiple trees. They will be merged!");
	    		t_tree = DependencyTreeUtils.createFakeTree(t_tree);
	    		logger.finer("\nThe Merged Tree of Text:\n" + t_tree);
	    	}
	    	
	    	// check if T contains phrasal verbs
	    	if(DependencyTreeUtils.checkPhrasalVerbs(t_tree)) {
	    		logger.finer("This fragment contains phrasal verbs whose nodes will be merged!");
	    		t_tree = DependencyTreeUtils.mergePhrasalVerbs(t_tree);
	    		logger.finer("\nThe Tree after merging:\n" + t_tree);
	    	}
	    	
	    	// remove punctuation
	    	if (this.punctuationRemoval && DependencyTreeUtils.checkPunctuation(t_tree)) {
	    		logger.finer("The fragment contains some punctuation that will be removed!");
	    		t_tree = DependencyTreeUtils.removePunctuation(t_tree);
	    		logger.finer("\nThe Tree of Text after removing punctuation:\n" + t_tree);
	    	}
	    	
	    	//create the Text fragment
	    	Fragment t_fragment = new Fragment(t_tree);
	    	//get the Hypothesis
	    	JCas hView = jcas.getView(LAP_ImplBase.HYPOTHESISVIEW); 
	    	//the dependency tree of Hypothesis
	    	String h_tree = DependencyTreeUtils.cas2CoNLLX(hView);
	    	logger.finer("\nThe Tree of Hypothesis:\n" + h_tree);

	    	// check if H contains multiple sentences
	    	if(DependencyTreeUtils.checkMultiSentences(h_tree)){
	    		logger.finer("This fragment contains multiple sentences. They will be removed!");
	    		h_tree = DependencyTreeUtils.mergeTrees(h_tree);
	    		logger.finer("\nThe Merged Tree of Hypothesis:\n" + h_tree);
	    	}
	    	
	    	// check if H contains multiple sentences
	    	if(DependencyTreeUtils.checkMultiTree(h_tree) ){
	    		logger.finer("This fragment contains multiple trees. They will be merged!");
	    		h_tree = DependencyTreeUtils.createFakeTree(h_tree);
	    		logger.finer("\nThe Merged Tree of Hypothesis:\n" + h_tree);
	    	}
	
	    	// check if H contains phrasal verbs
	    	if(DependencyTreeUtils.checkPhrasalVerbs(h_tree)){
	    		logger.finer("This fragment contains phrasal verbs whose nodes will be merged!");
	    		h_tree = DependencyTreeUtils.mergePhrasalVerbs(h_tree);
	    		logger.finer("\nThe Tree after merging:\n" + h_tree);
	    	}
	    	
	    	//  remove punctuation
	    	if (this.punctuationRemoval && DependencyTreeUtils.checkPunctuation(h_tree)) {
	    		logger.finer("The fragment contains some punctuationthat will be removed");
		    	h_tree = DependencyTreeUtils.removePunctuation(h_tree);
		    	logger.finer("\nThe Tree of Hypothesis after removing punctuation:\n" + h_tree);
	    	}

	    	//create the Hypothesis fragment
	    	Fragment h_fragment = new Fragment(h_tree);
            //calculate the distance between T and H by using the matches
	    	//provided by the alignment component.
	    	distanceValue = distance(t_fragment, h_fragment, alignments);
	    	
    	} catch (Exception e) {
    		
    		throw new DistanceComponentException(e.getMessage());
    		
    	}
    	
    	return distanceValue;
    	
    }

    
    @Override
    public Vector<Double> calculateScores(JCas jcas) throws ScoringComponentException {
    
    	DistanceValue distanceValue = null;
    	Vector<Double> v = new Vector<Double>();
	    
    	try {
    		
    		//get the alignments between T and H produced by the alignment component
    		alignments = new Alignments(aligner, jcas);
	 	   	// get Text
		    JCas tView = jcas.getView(LAP_ImplBase.TEXTVIEW);
		    //get the dependency tree of Text
		    String t_tree = DependencyTreeUtils.cas2CoNLLX(tView);
		    logger.finer("Text:\n" + t_tree);
		    
		    if(DependencyTreeUtils.checkMultiSentences(t_tree)){
		    	t_tree = DependencyTreeUtils.mergeTrees(t_tree);
		    	logger.finer("Merged text:\n" + t_tree);
		    }
		    
		    if(DependencyTreeUtils.checkPhrasalVerbs(t_tree)){
	    		logger.finer("Warning: phrasal verbs!");
	    		t_tree = DependencyTreeUtils.mergePhrasalVerbs(t_tree);
	    		logger.finer("\nThe Merged Tree of Text:\n" + t_tree);
	    	}
		    
		    //remove punctuation
	    	if (this.punctuationRemoval && DependencyTreeUtils.checkPunctuation(t_tree)) {
				t_tree = DependencyTreeUtils.removePunctuation(t_tree);
				logger.finer("\nThe Cleaned Tree of Text:\n" + t_tree);
	    	}
		    

		    //create the Text fragment
		    Fragment t_fragment = new Fragment(t_tree);
		    //get Hypothesis
		    JCas hView = jcas.getView(LAP_ImplBase.HYPOTHESISVIEW); 
		    //the dependency tree of Hypothesis
		    String h_tree = DependencyTreeUtils.cas2CoNLLX(hView);
		    
		    logger.finer("Hypothesis:\n" + h_tree);
		    
		    if(DependencyTreeUtils.checkMultiSentences(h_tree)){
		    	h_tree = DependencyTreeUtils.mergeTrees(h_tree);
		    	logger.finer("Merged hypothesis:\n" + h_tree);
		    }

		    if(DependencyTreeUtils.checkPhrasalVerbs(h_tree)){
	    		logger.finer("Warning: phrasal verbs!");
	    		h_tree = DependencyTreeUtils.mergePhrasalVerbs(h_tree);
	    		logger.finer("\nThe Merged Tree of Hypothesis::\n" + h_tree);
	    	}
		    
		    //remove punctuation
	    	if (this.punctuationRemoval && DependencyTreeUtils.checkPunctuation(h_tree)) {
				h_tree = DependencyTreeUtils.removePunctuation(h_tree);
				logger.finer("\nThe Cleaned Tree of Hypothesis:\n" + h_tree);
	    	}

		    //create the Hypothesis fragment
		    Fragment h_fragment = new Fragment(h_tree);
		    
	        //calculate the distance between T and H by using the matches
		    //provided by the alignment component.
		    distanceValue = distance(t_fragment, h_fragment, alignments);
		    	
    	} catch (Exception e) {
    		
    		throw new ScoringComponentException(e.getMessage());
    		
    	}
     
    	v.add(distanceValue.getDistance());
	    v.add(distanceValue.getUnnormalizedValue());
    
	    return v;
	     
    }
    
    
    /**
     * Returns the tree edit distance between T and H. During this
     * phase the transformations producing H from T are calculated too.
     * 
     * @param t the text fragment 
     * @param h the hypothesis fragment
     * 
     * @return The edit distance between the sequences of tokens
     * 
     * @throws ArithmeticException
     * 
     */
    public DistanceValue distance(Fragment t, Fragment h, Alignments alignments) throws Exception {

    	//here we need to call the library for calculating tree edit distance
    	double distance = 0.0;
    	double normalizedDistanceValue = 0.0;
    	double norm = 1.0;
    	
    	try {

	    	//Creating the Tree of Text
	    	LabeledTree t_tree = createTree(t);
	        //logger.info("T:" + t_tree);
	    	
	    	//Creating the Tree of Hypothesis
	    	LabeledTree h_tree = createTree(h);
	    	//logger.info("H:" + h_tree);
			
	    	//creating an instance of scoreImpl containing the definition of the 
	    	//the edit distance operations.
	    	ScoreImpl scoreImpl = new ScoreImpl(t_tree, h_tree);
	    	
		    //Create an instance of TreeEditDistance
			TreeEditDistance dist = new TreeEditDistance(scoreImpl);
			
			//This is used for storing the sequence of edit distance operations
			Mapping map = new Mapping(t_tree, h_tree);
			
			//Distance calculation
			distance = dist.calc(t_tree, h_tree, map);
			
		    //cycle through the list of the edit distance operations (i.e. replace -rep, 
	        //insertion -ins, deletion -del)
		    //operations are in the format: rep:2,3 rep:1,1 rep:0,0 ins:2 rep:3,4 rep:4,5
	        //e.g. rep:2,3 means replacing node id_2 with node id_3
		    //List<String> operationSequence = map.getSequence();
		    
		    // calculate the transformations required to transform T into H
		    this.transformations = computeTransformations(t_tree, h_tree, map);
		    
	    	// norm is the distance equivalent to the cost of inserting all the nodes in H and deleting
	    	// all the nodes in T. This value is used to normalize distance values.
	    	norm = (double)(t_tree.size() * this.mDeleteWeight + h_tree.size() * this.mInsertWeight);
	    	
	    	normalizedDistanceValue = distance/norm;
    	
    	} catch (Exception e) { 
    		
    		throw new Exception(e.getMessage());
    		
    	}
    	
    	return new EditDistanceValue(normalizedDistanceValue, false, distance);
	    
    }
    
     
    /**
     * Create a labeled tree
     */
    private LabeledTree createTree(Fragment f) throws Exception {
    	
    	LabeledTree lTree;
    	
    	try {
    	
	    	//the parents of the nodes
	    	int[] parents = new int[f.size()];
	    	//the ids of the nodes (they are the ids of the tokens as assigned by the dependency parser).
	    	int[] ids = new int[f.size()];
	    	//the tokens themselves
	    	FToken[] tokens = new FToken[f.size()];
	    	
	    	//Filling the data structure
	    	Iterator<FToken> iterator = f.getIterator();
	    	int i = 0;
	    	while (iterator.hasNext()) {
	    		FToken token_i = iterator.next();
	    		//we need to subtract -1 given that the tree edit distance library requires that
	        	//the id of the nodes starts from 0 instead 1.
	    		//System.out.println("======" + token_i);
	    		parents[i] = token_i.getHead();
	    		ids[i] = token_i.getId();
	    		tokens[i] = token_i;
	    		i++;
	    	}
	    	
	    	lTree = new LabeledTree ( //
	    			//the parents of the nodes
	    	    	parents,
	    	    	//the ids of the tokens
	    	    	ids,
	    	    	//the tokens with all their information
	    	    	tokens);
    	
    	} catch (Exception e) { 
    		
    		throw new Exception(e.getMessage());
    		
    	}
    	
    	return lTree;
    	
    }
    
    /**
     * This method accepts in input 2 labeled treed (LabeledTree) and the edit operations on the trees needed to transform
     * the tree t_tree into h_tree (map) and return the list of transformations, e.g.
     * 
     * @return the list of transformations
     *
     * @throws Exception
     */
    private List<Transformation> computeTransformations(LabeledTree t_tree, 
    		LabeledTree h_tree, Mapping map) throws Exception {
    	
    	List<Transformation> transformations = new ArrayList<Transformation>();
        
    	try {
    	
		    //cycle through the list of the edit distance operations (i.e. replace -rep, 
	        //insertion -ins, deleletion -del)
		    //operations are in the format: rep:2,3 rep:1,1 rep:0,0 ins:2 rep:3,4 rep:4,5
	        //e.g. rep:2,3 means replacing node id_2 with node id_3
		    List<String> operationSequence = map.getSequence();
		    
		    for (int i = 0; i < operationSequence.size(); i++) {
		    	
		    	String operation_i = (String)operationSequence.get(i);
		    	//System.err.print(operation_i + " ");
		    	String transformationType = operation_i.split(":")[0];
		    	String nodes = operation_i.split(":")[1];
		    	Transformation trans = null;
		    	//case of replace operations; the library we use for tree edit distance doesn't tell us 
		    	//if it was a replace or match operation. Distinguish between replace and match is done in this way: 
		    	//
		    	//match: 
		    	//  -- match between tokens
		    	//  -- positive alignments
		    	//
		    	//replace:
		    	//  -- no matches between tokens
		    	//  -- negative alignments or no alignments
		    	//
		    	if (transformationType.contains(Transformation.REPLACE)) {
		    		
			    	int node1 = Integer.parseInt(nodes.split(",")[0]);
			    	int node2= Integer.parseInt(nodes.split(",")[1]);
			    	FToken t_token = t_tree.getToken(node1);
			    	FToken h_token = h_tree.getToken(node2);
			    	
			    	String[] alignment = alignments.getAlignment(t_token, h_token, wordMatch);
			    	
			    	//i.e. LOCAL_ENTAILMENT, LOCAL_CONTRADICTION, LOCAL_SIMILARITY
			    	String alignmentType = alignment[0];
			    	//e.g. direction:TtoH
			    	String alignmentDirection = alignment[1];
			    	//e.g. hypernym
			    	String alignmentInfo = alignment[2];
			    	
			    	//NO ALIGNMENTS --> REPLACE TRANSFORMATION
			    	if (alignmentType == null) {
			    		trans = new Transformation(Transformation.REPLACE, null, t_token, h_token);
			    	}
			    	else if (alignmentType.equals(Alignments.LOCAL_ENTAILMENT)
			    			&& alignmentDirection.equals(Alignments.DIRECTION_HtoT)) {
			    		//trans = new Transformation(Transformation.REPLACE, alignmentType, t_token, h_token);
			    		//transformations.add(trans);
			    		trans = new Transformation(Transformation.REPLACE, alignmentInfo, t_token, h_token);
			    	}
			    	//CONTRADICTION --> REPLACE TRANSFORMATION
			    	else if (alignmentType.equals(Alignments.LOCAL_CONTRADICTION)) {
			    		//trans = new Transformation(Transformation.REPLACE, null, t_token, h_token);
			    		//transformations.add(trans);
			    		trans = new Transformation(Transformation.REPLACE, alignmentInfo, t_token, h_token);
			    	}
			    	//ENTAILMENT --> MATCH TRANSFORMATION
			    	else if (alignmentType.equals(Alignments.LOCAL_ENTAILMENT) || 
			    			alignmentType.equals(Alignments.LOCAL_SIMILARITY)) {
			    		//trans = new Transformation(Transformation.MATCH, null, t_token, h_token); 
			    		//transformations.add(trans);
			    		trans = new Transformation(Transformation.MATCH, alignmentInfo, t_token, h_token);
			    	}
			    	else {
			    		// trans = new Transformation(Transformation.REPLACE, null, t_token, h_token); 
			    		//transformations.add(trans);
			    		trans = new Transformation(Transformation.REPLACE,alignmentInfo , t_token, h_token);
			    	}
				    transformations.add(trans);
		    	}
		    	//case of insertion transformation
		    	else if (transformationType.contains(Transformation.INSERTION)){
		    		int node = Integer.parseInt(nodes);
			    	FToken token = h_tree.getToken(node);
			    	trans = new Transformation(transformationType, token);
			    	transformations.add(trans);
		    	}
		    	//case of deletion transformation
		    	else {
		    		int node = Integer.parseInt(nodes);
			    	FToken token = t_tree.getToken(node);
			    	trans = new Transformation(transformationType, token);
			    	transformations.add(trans);
		    	}
		    	
		    }
    	
    	} catch (Exception e) {
    		
    		throw new Exception(e.getMessage());
    		
    	}
		    
	    return transformations;
	    
    }
    
    
    /**
     * Replaces the ConsoleHandler for a specific Logger with one that will log
     * all messages. This method could be adapted to replace other types of
     * loggers if desired.
     * 
     * @param logger
     *          the logger to update.
     * @param newLevel
     *          the new level to log.
     */
    private static void replaceConsoleHandler(Logger logger, Level newLevel) {

      // Handler for console (reuse it if it already exists)
      Handler consoleHandler = null;
      // see if there is already a console handler
      for (Handler handler : logger.getHandlers()) {
        if (handler instanceof ConsoleHandler) {
          // found the console handler
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
    
    
    /**
     * The <code>EditDistanceValue</code> class extends the DistanceValue
     * to hold the distance calculation result. 
     */
    private class EditDistanceValue extends DistanceValue {

    	public EditDistanceValue(double distance, boolean simBased, double rawValue)
    	{
    		super(distance, simBased, rawValue); 
    	}
    	
    }
   
    
    /**
	 * The class ScoreImpl defines the method for the tree edit distance operations
	 * with their weights and basic logic.
	 */
    class ScoreImpl implements EditScore {
		
		private final LabeledTree tree1, tree2;
		
		public ScoreImpl(LabeledTree tree1, LabeledTree tree2) {
			
			this.tree1 = tree1;
			this.tree2 = tree2;
			
		}

		@Override
		public double replace(int node1, int node2) {
			
			FToken token_t = tree1.getToken(tree1.getLabel(node1));
			FToken token_h = tree2.getToken(tree2.getLabel(node2));
			// LOCAL-ENTAILMENT, LOCAL-CONTRADICTION, LOCAL_SIMILARITY
			String alignment =  alignments.getAlignment(token_t, token_h, wordMatch)[0];
			if (alignment != null && alignment.equals(Alignments.LOCAL_ENTAILMENT)) {
				return mMatchWeight; //return 0;
			} else if (token_t.getDprel().equals(token_h.getDprel())) 
			{
				return mSubstituteWeight/2;
			} 
			else { //replace
				return mSubstituteWeight; //return 1;
			}
		}
		
		@Override
		public double insert(int node2) {
			//return 3;
			return mInsertWeight; //return 1;
		}

		@Override
		public double delete(int node1) {
			//return 2;
			return mDeleteWeight; //return 1;
		}
		
	}
    
}