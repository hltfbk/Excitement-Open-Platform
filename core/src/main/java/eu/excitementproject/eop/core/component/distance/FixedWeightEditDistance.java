
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
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiExtractionType;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.it.WikiLexicalResourceIT;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.common.utilities.Utils;


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
 * @author Roberto Zanoli
 * 
 */
public abstract class FixedWeightEditDistance implements DistanceCalculation {

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
    private String instances;
    /**
	 * the resource
	 */
    @SuppressWarnings("rawtypes")
	private List<LexicalResource> lexR;
    /**
	 * stop word removal
	 */
    private boolean stopWordRemoval;
    Set<WordNetRelation> relations = new HashSet<WordNetRelation>();

    static Logger logger = Logger.getLogger(FixedWeightEditDistance.class.getName());
    
    
    /**
     * Construct a fixed weight edit distance with the following constant
     * weights for edits:
     * match weight is 0, substitute, insert and delete weights are
     */
    @SuppressWarnings("rawtypes")
	public FixedWeightEditDistance() {
    	
    	this.mMatchWeight = 0.0;
    	this.mDeleteWeight = 0.0;
    	this.mInsertWeight = 1.0;
    	this.mSubstituteWeight = 1.0;
    	this.lexR = new ArrayList<LexicalResource>();
        
    }

    
    /** 
	 * Constructor used to create this object. 
	 * 
	 * @param config the configuration
	 * 
	 */
    public FixedWeightEditDistance(CommonConfig config) throws ConfigurationException, ComponentException {
    
    	this();
        
    	logger.info("Creating an instance of " + this.getComponentName() + " ...");
    
        try {
        	
	        //get the platform name value table
	        NameValueTable platformNameValueTable = config.getSection("PlatformConfiguration");
	        
	       //get the platform language value
	        String language = platformNameValueTable.getString("language");
	        
	        //get the selected component
	    	NameValueTable componentNameValueTable = config.getSection(this.getClass().getCanonicalName());
	    	
	    	//activate stop word removal
	    	if (componentNameValueTable.getString("stopWordRemoval") != null) {
    			stopWordRemoval = Boolean.parseBoolean(componentNameValueTable.getString("stopWordRemoval"));
    			logger.info("Stop word removal activated.");
    		}
    		else {
    			stopWordRemoval = false;
        		logger.info("Stop word removal deactivated.");
    		}
	    	
	    	//get the selected instances
	    	instances = componentNameValueTable.getString("instances");
	    	
    		String[] instancesList = instances.split(",");
    		
    		for (int i = 0; i < instancesList.length; i++) {
    			
    			String instance = instancesList[i];
    			
    			if (instance.equals("basic")) {
    				//nothing to do
    			}
    			else if (instance.equals("wordnet")) {
	    			
	    	    	//get the parameters from the selected instance
	    	    	NameValueTable instanceNameValueTable = config.getSubSection(this.getClass().getCanonicalName(), instance);
	        	
		    		String multiWordnet = instanceNameValueTable.getString("path");
		    		
		    		if (language.equals("IT") && multiWordnet != null && !multiWordnet.equals("")) {
			    		try {
			    			
			    			initializeWordnet(multiWordnet);
			    			
			    		} catch (LexicalResourceException e) {
			    			throw new ComponentException(e.getMessage());
			    		}
		    		}
			    	else if (language.equals("EN") && multiWordnet != null && !multiWordnet.equals("")) {
			    		try {
			    			
			    			initializeWordnet(multiWordnet);
			    			
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
	    		
	    		}
	    		else if (instance.equals("wikipedia")) {
	    			
	    	    	//get the parameters from the selected instance
	    	    	NameValueTable instanceNameValueTable = config.getSubSection(this.getClass().getCanonicalName(), instance);
	        	
	    			String dbConnection = instanceNameValueTable.getString("dbconnection");
	    			String dbUser = instanceNameValueTable.getString("dbuser");
	    			String dbPasswd = instanceNameValueTable.getString("dbpasswd");
		    		
		    		if (language.equals("IT")) {
			    		try {
			    			
			    			initializeItalianWikipedia(dbConnection, dbUser, dbPasswd);
			    			
			    		} catch (LexicalResourceException e) {
			    			throw new ComponentException(e.getMessage());
			    		}
		    		}
		    		else if (language.equals("EN")) {
			    		try {
			    			
			    			initializeEnglishWikipedia(dbConnection, dbUser, dbPasswd);
			    			
			    		} catch (LexicalResourceException e) {
			    			throw new ComponentException(e.getMessage());
			    		}
		    		}
	    		}
	    		
    		}
    		
    	} catch (ConfigurationException e) {
    		throw new ComponentException(e.getMessage());
    	}
        
        logger.info("done.");
    	
    }
    
    
    /**
	 * set the weight of the match edit distant operation
	 * 
	 * @param mMatchWeight the value of the edit distant operation
	 *
	 * @return
	 */
    public void setmMatchWeight(double mMatchWeight) {
    	
    	this.mMatchWeight = mMatchWeight;
    	
    }
    
    
    /**
	 * set the weight of the delete edit distant operation
	 * 
	 * @param mDeleteWeight the value of the edit distant operation
	 *
	 * @return
	 */
    public void setmDeleteWeight(double mDeleteWeight) {
    	
    	this.mDeleteWeight = mDeleteWeight;
    	
    }
    
    
    /**
	 * set the weight of the insert edit distant operation
	 * 
	 * @param mInsertWeight the value of the edit distant operation
	 *
	 * @return
	 */
    public void setmInsertWeight(double mInsertWeight ) {
    	
    	this.mInsertWeight = mInsertWeight;
    	
    }
    
    
    /**
	 * set the weight of the substitute edit distant operation
	 * 
	 * @param mSubstotuteWeight the value of the edit distant operation
	 *
	 * @return
	 */
    public void setmSubstituteWeight(double mSubstituteWeight) {
    	
    	this.mSubstituteWeight = mSubstituteWeight;
    	
    }
   
    
    @Override
    public abstract String getComponentName();
    
    
    @Override
    public String getInstanceName() {
    	
    	return instances;
    	
    }
    
    
    /** 
	 * shutdown the resources
	 */
	public void shutdown() {
		
		logger.info(this.getComponentName() + " shutting down ...");
		
		try {
			for (int i = 0; i < lexR.size(); i++)
				lexR.get(i).close();
		} catch (Exception e) {
			logger.warning(e.getMessage());
    	}
		
		logger.info("done.");
		
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
    			curr.getPos().getTypeIndexID() != POS.typeIndexID) {
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
    public double getmMatchWeight(Token tMatched) {
    	
        return mMatchWeight;
        
    }
    
    
    /**
     * Returns the constant weight of deleting the specified token.
     *
     * @param tDeleted token deleted.
     * @return weight of deleting token.
     */
    public double getmDeleteWeight(Token tDeleted) {
    	
        return mDeleteWeight;
        
    }

    
    /**
     * Returns the constant weight of inserting the specified token.
     *
     * @param tInserted token inserted.
     * @return weight of inserting token.
     */
    public double getmInsertWeight(Token tInserted) {

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
    public double getmSubstituteWeight(Token tDeleted, Token tInserted) {

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
        	
    	//System.err.println(this.toString());
    	
    	// distanceTable is a table with sizeSource+1 rows and sizeTarget+1 columns
    	double[][] distanceTable = new double[source.size() + 1][target.size() + 1];
    	
    	distanceTable[0][0] = 0;
    	for (int i = 1; i <= source.size(); i++)
    		distanceTable[i][0] = distanceTable[i-1][0] + getmDeleteWeight(source.get(i-1));
    	for (int j = 1; j <= target.size(); j++)
    		distanceTable[0][j] = distanceTable[0][j-1] + getmInsertWeight(target.get(j-1));
 
    	
    	try {
    	
    		for (int i = 1; i <= source.size(); i++)
                for (int j = 1; j <= target.size(); j++) {

                	distanceTable[i][j] = minimum(
                			getTokenBaseForm(source.get(i-1)).equals(getTokenBaseForm(target.get(j-1))) || (
                                       
                					// it doesn't use the PoS to look for the relations in the lexical resource
                					/* lexR != null && 
                					source.get(i-1).getPos().getType().getName().equals(target.get(j-1).getPos().getType().getName()) && 
                					getRulesFromResource(getTokenBaseForm(source.get(i-1)), null,
                                   	getTokenBaseForm(target.get(j-1)), null))
                					 */

                                    // it uses the PoS to look for the relations in the lexical resource
                					lexR != null && lexR.size() > 0 &&
                						source.get(i-1).getPos().getType().getName().equals(target.get(j-1).getPos().getType().getName()) && 
                						getRulesFromResource(getTokenBaseForm(source.get(i-1)), new ByCanonicalPartOfSpeech(source.get(i-1).getPos().getType().getShortName()),
                								getTokenBaseForm(target.get(j-1)), new ByCanonicalPartOfSpeech(target.get(j-1).getPos().getType().getShortName())))
        							
        
                                        ? distanceTable[i - 1][j - 1] + getmMatchWeight(source.get(i-1))
                                    : distanceTable[i - 1][j - 1] + getmSubstituteWeight(source.get(i-1), target.get(j-1)),
                                    distanceTable[i - 1][j] + getmDeleteWeight(source.get(i-1)),
                                    distanceTable[i][j - 1] + getmInsertWeight(target.get(j-1)));
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
     * Returns the token base form. It is the form of the token
     * or its lemma.
     *
     * @param token the token
     * 
     * @return the base form of the token
     * 
     */
    public abstract String getTokenBaseForm(Token token);
    
    
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
     * Initialize English Wordnet
     * 
     * @param path the path of the resource
     * 
     * @throws LexicalResourceException
     */
    private void initializeWordnet(String path) throws LexicalResourceException {
    	
    	logger.info("Wordnet initialization ...");
    	
    	try {
    	
			relations.add(WordNetRelation.SYNONYM);
			//relations.add(WordNetRelation.HYPERNYM);
			
			@SuppressWarnings("rawtypes")
			LexicalResource resource = new WordnetLexicalResource(new File(path), false, false, relations, 3);
			
			lexR.add(resource);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
    	
    	logger.info("done.");
		
    }
    
    
    /**
     * Initialize GermaNet
     * 
     * @param path the path of the resource
     * 
     * @throws LexicalResourceException
     */
    private void initializeGermaNet(String path) throws LexicalResourceException {
    	
    	logger.info("GermaNet initialization ...");
    	
		try {
			
			@SuppressWarnings("rawtypes")
			LexicalResource resource = new GermaNetWrapper(path);
			lexR.add(resource);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
		
		logger.info("done.");
	
	}
    
    
    /**
     * Initialize English Wikipedia
     * 
     * @param dbConnection the db address
     * @param dbUser the user name
     * @param dbPasswd the user passwd
     * 
     * @throws LexicalResourceException
     */
    private void initializeEnglishWikipedia(String dbConnection, String dbUser, String dbPasswd) throws LexicalResourceException {
    	
    	logger.info("English Wikipedia initialization ...");
    	
    	try {
    	
			Set<WikiExtractionType> extractionTypes = Utils.arrayToCollection(new WikiExtractionType[]{WikiExtractionType.REDIRECT,WikiExtractionType.BE_COMP,
					WikiExtractionType.BE_COMP_IDIRECT,WikiExtractionType.ALL_NOUNS_TOP}, new HashSet<WikiExtractionType>());
			//File stopWordsFile = new File("src/test/resources/stopwords.txt");
			File stopWordsFile = File.createTempFile("emptystopwordfile", ".tmp"); 
			stopWordsFile.deleteOnExit();
			
			@SuppressWarnings("rawtypes")
			LexicalResource resource = new WikiLexicalResource(stopWordsFile, extractionTypes, dbConnection, dbUser, dbPasswd, 0.01);
			lexR.add(resource);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
    	
    	logger.info("done.");
	
	}
    
    
    /**
     * Initialize Italian Wikipedia
     * 
     * @param dbConnection the db address
     * @param dbUser the user name
     * @param dbPasswd the user passwd
     * 
     * @throws LexicalResourceException
     */
    private void initializeItalianWikipedia(String dbConnection, String dbUser, String dbPasswd) throws LexicalResourceException {
    	
    	logger.info("Italian Wikipedia initialization ...");
    	
    	try {
    		
    		Set<WikiExtractionType> extractionTypes = Utils.arrayToCollection(new WikiExtractionType[]{WikiExtractionType.REDIRECT,WikiExtractionType.CATEGORY,
    			WikiExtractionType.LEX_ALL_NOUNS,WikiExtractionType.SYNT_ALL_NOUNS}, new HashSet<WikiExtractionType>());
    		
    		//Set<WikiExtractionType> extractionTypes = Utils.arrayToCollection(new WikiExtractionType[]{WikiExtractionType.REDIRECT,WikiExtractionType.BE_COMP,
				//	WikiExtractionType.BE_COMP_IDIRECT,WikiExtractionType.ALL_NOUNS_TOP}, new HashSet<WikiExtractionType>());
    		//File stopWordsFile = new File("src/test/resources/stopwords.txt");
			File stopWordsFile = File.createTempFile("emptystopwordfile", ".tmp"); 
			stopWordsFile.deleteOnExit();
			
			@SuppressWarnings("rawtypes")
			LexicalResource resource =  new WikiLexicalResourceIT(stopWordsFile, extractionTypes, dbConnection, dbUser, dbPasswd, 0.01);
			lexR.add(resource);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
    	
    	logger.info("done.");
	
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
	private boolean getRulesFromResource(String leftLemma, PartOfSpeech leftPos, 
    		String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException {
    	
    	//logger.info("leftLemma:" + leftLemma + " leftPos:" + leftPos + "\t" + "rightLemma:" + rightLemma + " " + "rightPos:" + rightPos);
    	
    	List<LexicalRule<?>> rules = null;
    	
		try {
			
			for (int i = 0; i < lexR.size(); i++) {
				rules = lexR.get(i).getRules(leftLemma, leftPos, rightLemma, rightPos);
				if (rules != null && rules.size() > 0) {
					return true;
				}
			}
			
		} catch (LexicalResourceException e) {
		    logger.severe(e.getMessage());
			//logger.severe("leftLemma:" + leftLemma + " leftPos:" + leftPos + "\t" + "rightLemma:" + rightLemma + " " + "rightPos:" + rightPos);
			//throw new LexicalResourceException(e.getMessage());
    	} catch (Exception e) {
    		logger.severe(e.getMessage());
    		//logger.severe("leftLemma:" + leftLemma + " leftPos:" + leftPos + "\t" + "rightLemma:" + rightLemma + " " + "rightPos:" + rightPos);
    		//throw new LexicalResourceException(e.getMessage());
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
    
}
