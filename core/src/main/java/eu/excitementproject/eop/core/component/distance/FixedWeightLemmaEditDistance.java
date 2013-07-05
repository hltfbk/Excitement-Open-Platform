package eu.excitementproject.eop.core.component.distance;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;


/**
 * The <code>FixedWeightedLemmaEditDistance</code> class extends FixedWeightedEditDistance.
 * Given a pair of T-H, each of them represented as a sequences of tokens (i.e. the lemma of the tokens), the edit distance between 
 * T and H is the minimum number of operations required to convert T to H.
 * 
 * @author  Roberto Zanoli
 * @version 0.1
 */
public class FixedWeightLemmaEditDistance extends FixedWeightEditDistance {

    /**
     * Construct a fixed weight edit distance with the following constant
     * weights for edits:
     * match weight is 0, substitute, insert and delete weights are
     */
    public FixedWeightLemmaEditDistance() {
    	
    	super();
        
    }

    
    /** 
	 * Constructor used to create this object. 
	 * 
	 * @param config the configuration
	 * 
	 */
    public FixedWeightLemmaEditDistance(CommonConfig config) throws ConfigurationException, ComponentException {
    
        super(config);
    		
    }
    
    
    @Override
    public String getComponentName() {
    	
    	return "FixedWeightLemmaEditDistance";
    	
    }
    
    
    @Override
    public String getTokenBaseForm(Token token) {
    	
    	return token.getLemma().getValue();
    	
    }
    
    
}

