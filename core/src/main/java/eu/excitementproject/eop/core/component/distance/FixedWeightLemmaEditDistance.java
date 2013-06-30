
package eu.excitementproject.eop.core.component.distance;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ART;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.CARD;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.CONJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.PP;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.O;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.PUNC;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitementproject.eop.common.component.distance.DistanceCalculation;
import eu.excitementproject.eop.common.component.distance.DistanceComponentException;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordnetDictionaryImplementationType;


/**
 * The <code>FixedWeightedEditDistance</code> class implements the DistanceCalculation interface.
 * Given a pair of T-H, each of them represented as a sequences of tokens, the edit distance between 
 * T and H is the minimum number of operations required to convert T to H. 
 * FixedWeightedEditDistance implements the simplest form of weighted edit distance that simply uses a 
 * constant cost for each of the edit operations: match, substitute, insert, delete.
 * 
 * <h4>Relation to Simple Edit Distance</h4>
 * Weighted edit distance agrees with edit distance as a distance assuming the following weights:
 * match weight is 0, substitute, insert and delete weights are <code>1</code>.
 *
 * <h4>Symmetry</h4>
 * If the insert and delete costs of a character are equal, then weighted edit distance will be 
 * symmetric.  
 * 
 * <h4>Metricity</h4>
 * If the match weight of all tokens is zero, then the distance between a token sequence
 * and itself will be zero.  
 * 
 * Some parts of this code have been pulled from the EDITS software: http://edits.fbk.eu/.
 *
 * <B>Not thread safe!</B>
 * 
 * @author  Roberto Zanoli
 * @version 0.1
 */
public class FixedWeightLemmaEditDistance implements DistanceCalculation {

	/**
	 * weight for match
	 */
    private double mMatchWeight;
    /**
	 * weight for delete
	 */
    private double mDeleteWeight;
    /**
	 * weight for insert
	 */
    private double mInsertWeight;
    /**
	 * weight for substitute
	 */
    private double mSubstituteWeight;
    /**
	 * the activated instance
	 */
    private String instance;
    /**
	 * the resource
	 */
    @SuppressWarnings("rawtypes")
	private LexicalResource lexR;
    /**
	 * stop word removal
	 */
    private boolean stopWordRemoval;
    Set<WordNetRelation> relations = new HashSet<WordNetRelation>();

    static Logger logger = Logger.getLogger(FixedWeightLemmaEditDistance.class.getName());
    
    /**
     * Construct a fixed weight edit distance with the following constant
     * weights for edits:
     * match weight is 0, substitute, insert and delete weights are
     */
    public FixedWeightLemmaEditDistance() {
    	
    	mMatchWeight = 0.0;
        mDeleteWeight = 0.0;
        mInsertWeight = 1.0;
        mSubstituteWeight = 1.0;
        lexR = null;
        
    }

    
    /** 
	 * Constructor used to create this object. 
	 * 
	 * @param config the configuration
	 * 
	 */
    public FixedWeightLemmaEditDistance(CommonConfig config) throws ConfigurationException, ComponentException {
    
        
        logger.info(getComponentName());
        
        initializeWeights(config);
        
        try {
        
	        //get the platform language value
	        NameValueTable platformNameValueTable = config.getSection("PlatformConfiguration");
	        //get the selected component instance
	    	NameValueTable componentNameValueTable = config.getSection(this.getClass().getCanonicalName());
	    	instance = componentNameValueTable.getString("instances");
	    	//get the parameters from the selected instance
	    	NameValueTable instanceNameValueTable = config.getSubSection(this.getClass().getCanonicalName(), instance);
    	
    		String language = platformNameValueTable.getString("language");
    		logger.info("language:" + language);
    		String multiWordnet = instanceNameValueTable.getString("path");
    		
    		//initialise WordNet
    		if (language.equals("IT") && multiWordnet != null && !multiWordnet.equals("")) {
	    		try {
	    			initializeItalianWordnet(multiWordnet);
	    		} catch (LexicalResourceException e) {
	    			throw new ComponentException(e.getMessage());
	    		}
    		}
	    	else if (language.equals("EN") && multiWordnet != null && !multiWordnet.equals("")) {
	    		try {
	    			initializeEnglishWordnet(multiWordnet);
	    		} catch (LexicalResourceException e) {
	    			throw new ComponentException(e.getMessage());
	    		}
	    	}
	    	else if (language.equals("DE") && multiWordnet != null && !multiWordnet.equals("")) {
	    		try {
	    			initializeGermaNet(multiWordnet);
	    		} catch (LexicalResourceException e) {
	    			throw new ComponentException(e.getMessage());
	    		}
	    	}
    		
    		if (instanceNameValueTable.getString("stopWordRemoval") != null) {
    			stopWordRemoval = Boolean.parseBoolean(instanceNameValueTable.getString("stopWordRemoval"));
    			logger.info("stop word removal activated");
    		}
    		else {
    			stopWordRemoval = false;
        		logger.info("stop word removal deactivated");
    		}
    			
    	} catch (ConfigurationException e) {
    		throw new ComponentException(e.getMessage());
    	}
    	
    }
    
    
    
    @Override
    public String getComponentName() {
    	
    	return "FixedWeightLemmaEditDistance";
    	
    }
    
    
    @Override
    public String getInstanceName() {
    	
    	return instance;
    	
    }
    
    
    /** 
	 * shutdown the resources
	 */
	public void shutdown() {
		
		logger.info("shutdown()");
		
		try {
			if (lexR != null)
				lexR.close();
		} catch (Exception e) {
			logger.warning(e.getMessage());
    	}
		
		
	}
    
    
    @Override
    public DistanceValue calculation(JCas jcas) throws DistanceComponentException {
    	
    	DistanceValue distanceValue = null;
    	
    	try {
    	    // get Text
	    	JCas tView = jcas.getView("TextView");
	    	List<Token> tTokensSequence = getTokenSequences(tView);
	    	
	    	// get Hypothesis
	    	JCas hView = jcas.getView("HypothesisView"); 
	    	List<Token> hTokensSequence = getTokenSequences(hView);

	    	distanceValue = distance(tTokensSequence, hTokensSequence);
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new DistanceComponentException(e.getMessage());
    	}
    	
    	return distanceValue;
    	
    }

    
    @Override
    public Vector<Double> calculateScores(JCas jcas) throws ScoringComponentException {
    	
    	DistanceValue distanceValue = null;
    	Vector<Double> v = new Vector<Double>(); 
    	
    	try {
    	    // get Text
	    	JCas tView = jcas.getView("TextView");
	    	List<Token> tTokensSequence = getTokenSequences(tView);
	    	
	    	// get Hypothesis
	    	JCas hView = jcas.getView("HypothesisView"); 
	    	List<Token> hTokensSequence = getTokenSequences(hView);
	    	
	    	distanceValue = distance(tTokensSequence, hTokensSequence);
	    	
    	} catch (Exception e) {
    		throw new ScoringComponentException(e.getMessage());
    	}
    	
    	v.add(distanceValue.getDistance()); 
    	v.add(distanceValue.getUnnormalizedValue()); 
    	
    	return v;
    }

    
    /** 
	 * Returns a list of tokens contained in the specified CAS. Stop words can be removed.
	 * 
     * @param jcas the CAS
     * 
     * @return the list of tokens in the CAS.
     *
	 */
    private List<Token> getTokenSequences(JCas jcas) {
    	
    	List<Token> tokensList = new ArrayList<Token>();
    	
    	AnnotationIndex<Annotation> tokenIndex = jcas.getAnnotationIndex(Token.type);
    	
    	Iterator<Annotation> tokenIter = tokenIndex.iterator();
    	
    	while(tokenIter.hasNext()) {
    		
    		Token curr = (Token) tokenIter.next();
    		
    		// stop word removal
    		if (stopWordRemoval) {
    			if(curr.getPos().getTypeIndexID() != PP.typeIndexID &&
    			curr.getPos().getTypeIndexID() != PUNC.typeIndexID &&
    			curr.getPos().getTypeIndexID() != ART.typeIndexID &&
    			curr.getPos().getTypeIndexID() != CONJ.typeIndexID &&
    			curr.getPos().getTypeIndexID() != CARD.typeIndexID &&
    			curr.getPos().getTypeIndexID() != O.typeIndexID &&
    			curr.getPos().getTypeIndexID() != POS.typeIndexID &&
    			!curr.getLemma().getValue().equals("have") && 
    			!curr.getLemma().getValue().equals("be") &&
    			!curr.getLemma().getValue().equals("avere") && 
    			!curr.getLemma().getValue().equals("essere") &&
    			!curr.getLemma().getValue().equals("haben") && 
    			!curr.getLemma().getValue().equals("sein")) {
    				tokensList.add(curr);
    			}
    		}
    		else
    			tokensList.add(curr);
    	}
    	
    	return tokensList;
    	
    }
    

    /**
     * Returns a string based representation of the fixed weight
     * edit distance's parameters.
     *
     * @return string-based representation of this distance.
     */
    public String toString() {
    	
        StringBuilder sb = new StringBuilder();
        
        sb.append("costs:");
        sb.append("  match weight=" + mMatchWeight);
        sb.append("  insert weight=" + mInsertWeight);
        sb.append("  delete weight=" + mDeleteWeight);
        sb.append("  substitute weight=" + mSubstituteWeight);
        
        return sb.toString();
        
    }


    /**
     * Returns the constant weight of matching the specified token.
     *
     * @param tMatched token matched.
     * @return weight of matching token.
     */
    public double matchWeight(Token tMatched) {
    	
        return mMatchWeight;
        
    }
    
    
    /**
     * Returns the constant weight of deleting the specified token.
     *
     * @param tDeleted token deleted.
     * @return weight of deleting token.
     */
    public double deleteWeight(Token tDeleted) {
    	
        return mDeleteWeight;
        
    }

    
    /**
     * Returns the constant weight of inserting the specified token.
     *
     * @param tInserted token inserted.
     * @return weight of inserting token.
     */
    public double insertWeight(Token tInserted) {

        return mInsertWeight;
        
    }

    
    /**
     * Returns the constant weight of substituting the inserted token for
     * the deleted token.
     *
     * @param tDeleted deleted token.
     * @param tInserted inserted token.
     * @return the weight of substituting the inserted token for
     * the deleted token.
     */
    public double substituteWeight(Token tDeleted, Token tInserted) {

        return mSubstituteWeight;
        
    }
    
    
    /**
     * Returns the weighted edit distance between the specified
     * token sequences. The first argument is considered to be the
     * input and the second argument the output
     *
     * @param source first token sequence
     * @param target second token sequence
     * 
     * @return The edit distance between the sequences of tokens
     * 
     * @throws ArithmeticException
     * 
     */
    public DistanceValue distance(List<Token> source, List<Token> target ) throws ArithmeticException {
        	
    	// distanceTable is a table with sizeSource+1 rows and sizeTarget+1 columns
    	double[][] distanceTable = new double[source.size() + 1][target.size() + 1];
    	
    	distanceTable[0][0] = 0;
    	for (int i = 1; i <= source.size(); i++)
    		distanceTable[i][0] = distanceTable[i-1][0] + deleteWeight(source.get(i-1));
    	for (int j = 1; j <= target.size(); j++)
    		distanceTable[0][j] = distanceTable[0][j-1] + insertWeight(target.get(j-1));
 
    	
    	try {
    	
    		for (int i = 1; i <= source.size(); i++)
                for (int j = 1; j <= target.size(); j++) {

                	distanceTable[i][j] = minimum(
                			source.get(i-1).getLemma().getValue().equals(target.get(j-1).getLemma().getValue()) || (
                                       
                					// it doesn't use the PoS to look for the relations in the lexical resource
                					lexR != null && 
                					source.get(i-1).getPos().getType().getName().equals(target.get(j-1).getPos().getType().getName()) && 
                					getRulesFromWordnet(source.get(i-1).getLemma().getValue(), null,
                                   	target.get(j-1).getLemma().getValue(), null))
                					 

                                    // it uses the PoS to look for the relations in the lexical resource
                                   	/*
                					lexR != null && 
                						source.get(i-1).getPos().getType().getName().equals(target.get(j-1).getPos().getType().getName()) && 
                						getRulesFromWordnet(source.get(i-1).getLemma().getValue(), new ByCanonicalPartOfSpeech(source.get(i-1).getPos().getType().getShortName()),
                							target.get(j-1).getLemma().getValue(), new ByCanonicalPartOfSpeech(target.get(j-1).getPos().getType().getShortName())))
        							*/
        
                                        ? distanceTable[i - 1][j - 1] + matchWeight(source.get(i-1))
                                    : distanceTable[i - 1][j - 1] + substituteWeight(source.get(i-1), target.get(j-1)),
                                    distanceTable[i - 1][j] + deleteWeight(source.get(i-1)),
                                    distanceTable[i][j - 1] + insertWeight(target.get(j-1)));
                }

	    	
    	
    	} catch(Exception e) {
    		e.printStackTrace();
    		throw new ArithmeticException(e.getMessage());
    	}
    	
    	// distance is the the edit distance between source and target
    	double distance = distanceTable[source.size()][target.size()];
    	// norm is the distance equivalent to the cost of inserting the target token sequence and deleting
    	// the entire source sequence. It is used to normalize distance values.
    	double norm = distanceTable[source.size()][0] + distanceTable[0][target.size()];
    	// the normalizedDistanceValue score has a range from 0 (when source is identical to target), to 1
    	// (when source is completely different form target).
    	double normalizedDistanceValue = distance/norm;
    	    	
    	return new EditDistanceValue(normalizedDistanceValue, false, distance);
                
     }
    
    
    /**
     * Returns the smaller of three double values
     *
     * @param a the 1st number
     * @param b the 2nd number
     * @param c the 3rd number
     * 
     * @return the smaller of three double values
     * 
     */
    private double minimum(double a, double b, double c) {
    	
    	return Math.min(Math.min(a, b), c);
    	
    }
    
    
    /**
     * Initialize Italian Wordnet
     * 
     * @throws LexicalResourceException
     */
    private void initializeItalianWordnet(String path) throws LexicalResourceException {
		
    	logger.info("initializeItalianWordnet");
    	
    	try {
    	
			relations.add(WordNetRelation.SYNONYM);
			relations.add(WordNetRelation.HYPERNYM);
			
			lexR = new WordnetLexicalResource(new File(path), false, false, relations, 3, WordnetDictionaryImplementationType.JMWN);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
		
    }
    
    
    /**
     * Initialize English Wordnet
     * 
     * @param path the path of the resource
     * 
     * @throws LexicalResourceException
     */
    private void initializeEnglishWordnet(String path) throws LexicalResourceException {
    	
    	logger.info("initializeEnglishWordnet()");
    	
    	try {
    	
			relations.add(WordNetRelation.SYNONYM);
			relations.add(WordNetRelation.HYPERNYM);
			
			lexR = new WordnetLexicalResource(new File(path), false, false, relations, 3);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
		
    }
    
    
    /**
     * Initialize GermaNet
     * 
     * @param path the path of the resource
     * 
     * @throws LexicalResourceException
     */
    private void initializeGermaNet(String path) throws LexicalResourceException {
    	
    	logger.info("initializeGermaNet()");
    	
		try {
			
			lexR = new GermaNetWrapper(path);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
	
	}
    
    
    /**
     * Return true if it exists a relation between leftLemma and rightLemma
     * in the lexical resource.
     * 
     * @param leftLemma
     * @param leftPos
     * @param rightLemma
     * @param rightPos
     * 
     * @return true if the rule exists; false otherwise
     * 
     * @throws LexicalResourceException
     */
    @SuppressWarnings("unchecked")
	private boolean getRulesFromWordnet(String leftLemma, PartOfSpeech leftPos, 
    		String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException {
    	
    	List<LexicalRule<? extends WordnetRuleInfo>> rules = null;
    	
		try {
			
			rules = lexR.getRules(leftLemma, leftPos, rightLemma, rightPos);
			
		} catch (LexicalResourceException e) {
			throw new LexicalResourceException(e.getMessage());
    	} catch (Exception e) {
    		logger.severe("leftLemma:" + leftLemma + " leftPos:" + leftPos + "\t" + "rightLemma:" + rightLemma + " " + "rightPos:" + rightPos);
    		throw new LexicalResourceException(e.getMessage());
    	}
		
		if (rules != null && rules.size() >0) {
			return true;
		}
		
		return false;
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
     * Initialize the weights of the edit distance operations
   
     * @param config the configuration
     * 
     */
    private void initializeWeights(CommonConfig config) {

    	try{ 
    		
    		NameValueTable weightsTable = config.getSubSection(this.getClass().getCanonicalName(), "weights");
    		mMatchWeight = weightsTable.getDouble("match");
    		mDeleteWeight = weightsTable.getDouble("delete");
    		mInsertWeight = weightsTable.getDouble("insert");
    		mSubstituteWeight = weightsTable.getDouble("substitute");
    		
    	} catch (ConfigurationException e) {
    		
    		logger.info("Could not find weights section in configuration file, using defaults");
    		mMatchWeight = 0.0;
    		mDeleteWeight = 0.0;
    		mInsertWeight = 1.0;
    		mSubstituteWeight = 1.0;
    		
    	}
    }
    
}
