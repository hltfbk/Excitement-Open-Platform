package eu.excitementproject.eop.core.component.distance;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;


/**
 * The <code>FixedWeightedTokenEditDistance</code> class extends FixedWeightedEditDistance.
 * Given a pair of T-H, each of them represented as a sequences of tokens (i.e. the form of the tokens), the edit distance between 
 * T and H is the minimum number of operations required to convert T to H.
 * 
 * @author Roberto Zanoli
 * 
 */
public class FixedWeightTokenEditDistance extends FixedWeightEditDistance {

    /**
     * Construct a fixed weight edit distance with the following constant
     * weights for edits:
     * match weight is 0, substitute, insert and delete weights are
     */
    public FixedWeightTokenEditDistance() {
    	
    	super();
        
    }

    
    /** 
	 * Constructor used to create this object. 
	 * 
	 * @param config the configuration
	 * 
	 */
    public FixedWeightTokenEditDistance(CommonConfig config) throws ConfigurationException, ComponentException {
    
        super(config);
    		
    }
    
    
    @Override
    public String getComponentName() {
    	
    	return "FixedWeightTokenEditDistance";
    	
    }
    
    
    @Override
    public String getTokenBaseForm(Token token) {
    	
    	return token.getCoveredText();
    	
    }
    
    
}
