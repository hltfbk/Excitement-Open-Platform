
package eu.excitementproject.eop.core.component.distance;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.resource.SimilarityStorageBasedLexicalResource;


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
 * @author Roberto Zanoli /w Redis part by Vivi
 * 
 * 
 */
public abstract class FixedWeightEditDistancewRedis extends FixedWeightEditDistance {

	
	public static int ruleCounter = 0;
	
    static Logger logger = Logger.getLogger(FixedWeightEditDistancewRedis.class.getName());
    
    
    /**
     * Construct a fixed weight edit distance with the following constant
     * weights for edits:
     * match weight is 0, substitute, insert and delete weights are
     */
	public FixedWeightEditDistancewRedis() {

		super();
    }

    
    /** 
	 * Constructor used to create this object. 
	 * 
	 * @param config the configuration
	 * 
	 */
    public FixedWeightEditDistancewRedis(CommonConfig config) throws ConfigurationException, ComponentException {
    
    	super(config);
        
    	logger.info("Creating an instance of " + this.getComponentName() + " ...");
    	    	
    	String[] instancesList = instances.split(",");
    		
    	for (int i = 0; i < instancesList.length; i++) {
    			
    		String instance = instancesList[i];
    			
     		if (instance.contains("redis")) {
	    		try {
		    			
		    		initializeRedisResource(instance, config);
		    			
	    		} catch (LexicalResourceException e) {
	    			logger.info("Problem initializing redis resource");
		    		throw new ComponentException(e.getMessage());
		    	}
     		}
    	}
    	
        logger.info("done.");
    	
    }


    
    public FixedWeightEditDistancewRedis(double mMatchWeight,
			double mDeleteWeight, double mInsertWeight,
			double mSubstituteWeight, boolean stopWordRemoval, String language,
			Map<String, String> resources) throws ConfigurationException, ComponentException {
		// TODO Auto-generated constructor stub
    	super(mMatchWeight, mDeleteWeight, mInsertWeight, mSubstituteWeight, stopWordRemoval, language, resources);
	}


	/**
     * Generates an instance of a Redis-based lexical resource, using the corresponding section from the configuration file
     * 
     * @param config the EDA's configuration file
     * @throws LexicalResourceException
     */
    private void initializeRedisResource(String resourceType, CommonConfig config) throws LexicalResourceException {
    	
    	logger.info("Redis-based resource initialization : " + resourceType);
    	
    	try {
   		    		
    	    ConfigurationFile confFile = new ConfigurationFile(config);
    		
    		ConfigurationParams confParams = confFile.getModuleConfiguration(resourceType);
    		
    		logger.info("Parameters: " + confParams.get("resource-name"));
    		
    		@SuppressWarnings("rawtypes")
			LexicalResource resource = new SimilarityStorageBasedLexicalResource(confParams);
    		lexR.add(resource);
    		
/*    		try {
    			logger.info("LR test: ");
    			
    			PartOfSpeech pos = new ByCanonicalPartOfSpeech("V");
    			    			
    			logger.info("Part of speech created: " + pos.getCanonicalPosTag());
    			
    			@SuppressWarnings("rawtypes")

//    			List<LexicalRule> rules = resource.getRulesForLeft("vedere", new ByCanonicalPartOfSpeech("V"));
    			List<LexicalRule> rules = new ArrayList<LexicalRule>();     			  
//    			List<LexicalRule> rules = resource.getRulesForLeft("vedere", null);

      	        rules = resource.getRulesForLeft("vedere", pos);
      	        
    			if (rules != null) {
    				for (LexicalRule r: rules) {
    					logger.info("Rules for left: " + r.toString());
    				}
    			}
    			
    			rules = resource.getRulesForRight("vedere", new ByCanonicalPartOfSpeech("V"));
    			if (rules != null) {
    				for (LexicalRule r: rules) {
    					logger.info("Rules for right: " + r.toString());
    				}
    			}
   			
    		} //catch (LexicalResourceException lre) {
    			catch (Exception lre) {
    			logger.info("Testing the resource didn't work out");
    			lre.printStackTrace();
    		}
*/    		
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
    @Override
    @SuppressWarnings("unchecked")
	protected boolean getRulesFromResource(String leftLemma, PartOfSpeech leftPos, 
    		String rightLemma, PartOfSpeech rightPos) throws LexicalResourceException {
    	
    	//logger.info("leftLemma:" + leftLemma + " leftPos:" + leftPos + "\t" + "rightLemma:" + rightLemma + " " + "rightPos:" + rightPos);
    	
    	List<LexicalRule<?>> rules = null;
    	
		try {
			
			for (int i = 0; i < lexR.size(); i++) {
				rules = lexR.get(i).getRules(leftLemma, leftPos, rightLemma, rightPos);
				if (rules != null && rules.size() > 0) {
					ruleCounter++;
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
    
}
