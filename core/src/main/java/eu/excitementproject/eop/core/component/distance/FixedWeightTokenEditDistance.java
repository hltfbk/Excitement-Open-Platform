package eu.excitementproject.eop.core.component.distance;

import java.util.Map;

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
	 * Constructor used to create this object. All the main parameters of the component are
	 * exposed in the constructor. Here is an example on how it can be used. 
	 * 
	 * <pre>
	 * {@code
	 * 
	 * //setting the weights of the edit distance operations
	 * double mMatchWeight = 0.0;
	 * double mDeleteWeight = 0.0;
	 * double mInsertWeight = 1.0;
	 * double mSubstituteWeight = 1.0;
	 * //enable stop words so that stop words will be removed.
	 * boolean stopWordRemoval = true;
	 * //the component has to work on a data set for Italian language
	 * String language = "IT";
	 * //setting the resources: wikipedia and wordnet will be used
	 * Map<String,String> resources = new HasMap<String,String>();
	 * resources.put("wordnet", "/tmp/wordnet/");
	 * resources.put("wikipedia", "jdbc:mysql://nathrezim:3306/wikilexresita#johnsmith#mypasswd");
	 * //creating an instance of the FixedWeightTokenEditDistance component
	 * FixedWeightEditDistance fwed = 
	 * new FixedWeightTokenEditDistance(mMatchWeight, mDeleteWeight, mInsertWeight, mSubstituteWeight, stopWordRemoval, language, resources)
	 * 
	 * }
	 * </pre>
	 * 
	 * @param mMatchWeight weight for match
     * @param mDeleteWeight weight for delete
     * @param mInsertWeight weight for insert
     * @param mSubstituteWeight weight for substitute
     * @param stopWordRemoval if stop words has to be removed or not; Possible values are: true, false
     * @param language the language of the data the component has to deal with; Possible values are: DE, EN, IT
     * @param resources the external resources the component has to use; it is a key/value pairs table.
     * The supported resources with their parameters are (reported as key/value pairs):
     * wordnet, path of the resource residing in the file system, e.g. /tmp/wordnet/
     * wikipedia, dbConnection#dbUser#dbPasswd, e.g. jdbc:mysql://nathrezim:3306/wikilexresita#johnsmith#mypasswd
     * 
	 */
    public FixedWeightTokenEditDistance(double mMatchWeight, double mDeleteWeight, double mInsertWeight, double mSubstituteWeight, boolean stopWordRemoval, String language, Map<String,String> resources) throws ConfigurationException, ComponentException {
    
        super(mMatchWeight, mDeleteWeight, mInsertWeight, mSubstituteWeight, stopWordRemoval, language, resources);
    		
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
