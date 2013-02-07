
package eu.excitementproject.eop.core.component.distance;


import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;
//import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ART;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.CARD;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.CONJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.PP;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.O;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.PUNC;
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

//import edu.northwestern.at.utils.corpuslinguistics.postagger.PartOfSpeechTagger;
import eu.excitementproject.eop.common.component.distance.DistanceCalculation;
import eu.excitementproject.eop.common.component.distance.DistanceComponentException;
import eu.excitementproject.eop.common.component.distance.DistanceValue;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
//import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
//import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
//import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
//import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
//import eu.excitementproject.eop.common.representation.partofspeech.UnspecifiedPartOfSpeech;
//import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordnetDictionaryImplementationType;


//import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
//import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
//import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.DKProPartOfSpeech;
//import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
//import eu.excitementproject.eop.common.representation.partofspeech.UnspecifiedPartOfSpeech;
//import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
//import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
//import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
//import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;
//import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordnetDictionaryImplementationType;






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
public class FixedWeightTokenEditDistance implements DistanceCalculation {

	// weight for match
    private final double mMatchWeight;
    // weight for delete
    private final double mDeleteWeight;
    // weight for insert
    private final double mInsertWeight;
    // weight for substitute
    private final double mSubstituteWeight;
    // wordnet
    private WordnetLexicalResource lexR;
    // removing stop words
    private boolean stopWordRemoval;
    Set<WordNetRelation> relations = new HashSet<WordNetRelation>();

    static Logger logger = Logger.getLogger(FixedWeightTokenEditDistance.class.getName());
    
    /**
     * Construct a fixed weight edit distance with the following constant
     * weights for edits:
     * match weight is 0, substitute, insert and delete weights are
     */
    public FixedWeightTokenEditDistance() {
    	
    	mMatchWeight = 0.0;
        mDeleteWeight = 2.0;
        mInsertWeight = 2.0;
        mSubstituteWeight = 1.0;
        lexR = null;
        //boolean stopWordRemoval = false;
        
    }

    
    /* 
	 * @see DistanceCalculation#initialize()
	 */
    public FixedWeightTokenEditDistance(CommonConfig config) throws ConfigurationException, ComponentException {
    
    	mMatchWeight = 0.0;
        mDeleteWeight = 1.0;
        mInsertWeight = 1.0;
        mSubstituteWeight = 1.0;
        
        logger.info(getComponentName());
        
    	NameValueTable nameValueTable = config.getSubSection(this.getClass().getCanonicalName(), "instance1");
    	
    	try {
    		String multiWordnet = nameValueTable.getString("multiWordnet");
    		if (multiWordnet != null) {
    			if (multiWordnet.equals("/configuration-file/")) {
	    			try {
	    				initializeItalianWordnet();
	    			} catch (LexicalResourceException e) {
	    				throw new ComponentException(e.getMessage());
	    	    	}
    			}
	    		else {
	    			try {
	    				initializeEnglishWordnet(multiWordnet);
	    			} catch (LexicalResourceException e) {
	    				throw new ComponentException(e.getMessage());
	    	    	}
	    		}
    		}
    	} catch (ConfigurationException e) {
    		// no multiWordnet option
    		//throw new ComponentException(e.getMessage());
    	}
    	
    	
    	try {
    		stopWordRemoval = Boolean.parseBoolean(nameValueTable.getString("stopWordRemoval"));
    		logger.info("stopWordRemoval activated");
    	} catch (ConfigurationException e) {
    		stopWordRemoval = false;
    		logger.info("stopWordRemoval deactivated");
    	}
    }
    
    
    /* 
	 * @see DistanceCalculation#getComponentName()
	 */
    public String getComponentName() {
    	
    	return "FixedWeightTokenEditDistance";
    	
    }
    
    
    /* 
	 * @see DistanceCalculation#getInstanceName()
	 */
    public String getInstanceName() {
    	
    	return null;
    	
    }
    
    
    /* 
	 * 
	 */
	public void shutdown() {
		
		if (lexR != null)
			lexR.close();
		
	}
    
    
    /* 
	 * @see DistanceCalculation#calculation()
	 */
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
    		//e.printStackTrace();
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
    		//e.printStackTrace();
    		throw new ScoringComponentException(e.getMessage());
    	}
    	
    	v.add(distanceValue.getDistance()); 
    	v.add(distanceValue.getUnnormalizedValue()); 
    	
    	return v;
    }

    
    /* 
	 * Returns a list of tokens contained in the specified CAS
	 * 
     * @param aCas The CAS
     * @return The list of tokens in the CAS.
     *
	 */
    private List<Token> getTokenSequences(JCas jcas) {
    	
    	List<Token> tokensList = new ArrayList<Token>();
    	
    	AnnotationIndex<Annotation> tokenIndex = jcas.getAnnotationIndex(Token.type);
    	
    	Iterator<Annotation> tokenIter = tokenIndex.iterator();
    	while(tokenIter.hasNext()) {
    		Token curr = (Token) tokenIter.next();
    		
    		//soluzione temporanea per la demo
    		//SimplerCanonicalPosTag.PREPOSITION
    		//SimplerCanonicalPosTag.PUNCTUATION
    		
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
     * @return String-based representation of this distance.
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
     * @param tMatched Token matched.
     * @return Weight of matching token.
     */
    public double matchWeight(Token tMatched) {
    	
        return mMatchWeight;
        
    }
    
    
    /**
     * Returns the constant weight of deleting the specified token.
     *
     * @param tDeleted Token deleted.
     * @return Weight of deleting token.
     */
    public double deleteWeight(Token tDeleted) {
    	
        return mDeleteWeight;
        
    }

    
    /**
     * Returns the constant weight of inserting the specified token.
     *
     * @param tInserted Token inserted.
     * @return Weight of inserting token.
     */
    public double insertWeight(Token tInserted) {
    	
        return mInsertWeight;
        
    }

    
    /**
     * Returns the constant weight of substituting the inserted token for
     * the deleted token.
     *
     * @param tDeleted Deleted token.
     * @param tInserted Inserted token.
     * @return The weight of substituting the inserted token for
     * the deleted token.
     */
    public double substituteWeight(Token tDeleted, Token tInserted) {
    	
        return mSubstituteWeight;
        
    }
    
    
    /**
     * Returns the weighted edit distance between the specified
     * token sequences. The first argument is considered to be the
     * input and the second argument the output.
     *
     * @param source First token sequence.
     * @param target Second token sequence.
     * @return The edit distance between the sequences of tokens.
     */
    public DistanceValue distance(List<Token> source, List<Token> target ) throws ArithmeticException {
        	
    	//DistanceValue distanceValue = new EditDistanceValue(0, false, 0);
    	
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
	    			
	    			//if(getRulesFromWordnet(source.get(i-1).getLemma().getValue(), new DKProPartOfSpeech(source.get(i-1).getPos().getType().getShortName()), 
    				//		target.get(j-1).getLemma().getValue(), new DKProPartOfSpeech(target.get(j-1).getPos().getType().getShortName()))) {
	    			//	System.out.println("s:" + source.get(i-1).getLemma().getValue() + "\t" + new DKProPartOfSpeech(source.get(i-1).getPos().getType().getShortName()));
		    		///	System.out.println("t:" + target.get(j-1).getLemma().getValue() + "\t" + new DKProPartOfSpeech(target.get(j-1).getPos().getType().getShortName()));
		    		//	System.exit(0);
	    			//}
    					
	    			//System.out.println("========" + source.get(i-1).getPos().getType().getName());
	    			//System.out.println("s:" + source.get(i-1).getLemma().getCoveredText() + "\t" + new DKProPartOfSpeech(source.get(i-1).getPos().getType().getShortName()));
	    			//System.out.println("t:" + target.get(j-1).getLemma().getCoveredText() + "\t" + new DKProPartOfSpeech(target.get(j-1).getPos().getType().getShortName()));
	    			distanceTable[i][j] = minimum(
	    					source.get(i-1).getLemma().getValue().equals(target.get(j-1).getLemma().getValue()) || (
	    							//getRulesFromWordnet("mela", null, "frutta", null))
	    					//lexR != null && getRulesFromWordnet(source.get(i-1).getLemma().getValue(), null, 
	    						//target.get(j-1).getLemma().getValue(), null))
	    					lexR != null && source.get(i-1).getPos().getType().getName().equals(target.get(j-1).getPos().getType().getName()) && getRulesFromWordnet(source.get(i-1).getLemma().getValue(), new DKProPartOfSpeech(source.get(i-1).getPos().getType().getShortName()), 
	    						target.get(j-1).getLemma().getValue(), new DKProPartOfSpeech(target.get(j-1).getPos().getType().getShortName())))
	    					//new PennPartOfSpeech(posAnnotation.getPosValue())
	    						
	    					? distanceTable[i - 1][j - 1] + matchWeight(source.get(i-1))
	    				    : distanceTable[i - 1][j - 1] + substituteWeight(source.get(i-1), target.get(j-1)),
	    				    distanceTable[i - 1][j] + deleteWeight(source.get(i-1)),
	    				    distanceTable[i][j - 1] + insertWeight(target.get(j-1)));
	    		}
    	
    	} catch(Exception e) {
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
     * Returns the smaller of three double values.
     */
    private double minimum(double a, double b, double c) {
    	
    	return Math.min(Math.min(a, b), c);
    	
    }
    
    
    /**
     * EditDistanceValue extends DistanceValue
     */
    private class EditDistanceValue extends DistanceValue {

    	public EditDistanceValue(double distance, boolean simBased, double rawValue)
    	{
    		//super(distance, simBased, rawValue, null); 
    		super(distance, simBased, rawValue); 
    	}
    	
//    	public EditDistanceValue(double distance, boolean simBased, double rawValue, Vector<Double> distanceVector)
//    	{
//    		super(distance, simBased, rawValue, distanceVector); 
//    	}
    	
    }
    
    
    private void initializeItalianWordnet() throws LexicalResourceException {
		
    	logger.info("initialize ItalianWordnet");
    	
    	try {
    	
	    	URL filePath =  ClassLoader.getSystemClassLoader().getResource("./configuration-file/");
	    	
			relations.add(WordNetRelation.SYNONYM);
			relations.add(WordNetRelation.HYPERNYM);
			
			lexR = new WordnetLexicalResource(new File(filePath.toURI()), false, false, relations, 3, WordnetDictionaryImplementationType.JMWN);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
		
    }
    
    
    private void initializeEnglishWordnet(String path) throws LexicalResourceException {
    	
    	logger.info("initialize EnglishWordnet");
    	
    	try {
    	
			relations.add(WordNetRelation.SYNONYM);
			relations.add(WordNetRelation.HYPERNYM);
			
			lexR = new WordnetLexicalResource(new File(path), false, false, relations, 3);
			
		} catch (Exception e) {
			throw new LexicalResourceException(e.getMessage());
		}
		
    }
    
    
    private boolean getRulesFromWordnet(String leftLemma, PartOfSpeech leftPos, 
    		String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException {
    	
        //System.out.println("leftLemma:" + leftLemma + "\t" + "leftPos:" + leftPos +  
    	//	"\t" + "rightLemma:" + rightLemma + "\t" +  "rightPos:" + rightPos);
    	
    	List<LexicalRule<? extends WordnetRuleInfo>> rules = null;
    	
		try {
			
			rules = lexR.getRules(leftLemma, leftPos, rightLemma, rightPos);
			
			//System.out.println("Got "+rules.size());
			//for (LexicalRule<? extends WordnetRuleInfo> rule : rules)
				//System.out.println(rule);
		
		} catch (LexicalResourceException e) {
			//System.out.println("Error extracting entailment rules from Italian MultiWordNet");
			// TODO Auto-generated catch block
			throw new LexicalResourceException(e.getMessage());
			//e.printStackTrace();
			//System.exit(0);
		}
		
		
		if (rules.size() >0) {
			//@SuppressWarnings("rawtypes")
			//Iterator it = rules.iterator();
			//System.out.println("==" + ((LexicalRule)it.next()).getRelation());
			//System.exit(1);
			return true;
		}
		
		return false;
    }

    
}
