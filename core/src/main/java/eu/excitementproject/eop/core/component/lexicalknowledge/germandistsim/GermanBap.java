package eu.excitementproject.eop.core.component.lexicalknowledge.germandistsim;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.distsim.redisinjar.EmbeddedRedisBasedLexicalResource;

/**
 * A lexical resource based on EXCITEMENT project DistSim distributional 
 * similarity-based lexical relation miner. 
 * 
 * This class holds data mined from SDEWAC corpus, with BAP measure. 
 * 
 * The class can be initialized in two ways. 
 *  - Without server-name and port: The resource will run redis-server on local. 
 *  - With server-name and port: the resource will just tap to that redis-server. 
 * @author Tae-Gil Noh 
 *
 */
public class GermanBap extends EmbeddedRedisBasedLexicalResource {

	/**
	 * This constructor is for those cases where the redis server is already up and 
	 * running somewhere. The resource simply taps to that server/port and offer 
	 * LexicalResource. 
	 * 
	 * @param l2rServer Name (or IP) of the server to connect, L->R sim redis server  
	 * @param l2rPort port number of the server to connect, L->R sim redis server 
	 * @param r2lServer Name (or IP) of the server to connect, R->L sim redis server 
	 * @param r2lPort port number of the server to connect, R->L sim redis server 
	 * @param maxNumOfRetrievedRules Maximum number of rules that will be returned. They are ordered. If null, return all. 
	 */
	public GermanBap(String l2rServer, int l2rPort, String r2lServer, int r2lPort, int maxNumOfRetrievedRules)
	{
		super(l2rServer, l2rPort, r2lServer, r2lPort, maxNumOfRetrievedRules); 
	}
	
	/**
	 * This constructor is for the cases where there is no running redis server for the 
	 * requested resource. The constructor will first run a redis-server on local computer 
	 * and initialize the resource. 
	 * 
	 * The constructor exposes port number, so the user/caller can assign a specific port for the purpose. 
	 * 
	 * @param l2rPort port number in integer; to be opened on local host for redis-server L->R sim 
	 * @param r2lPort port number in integer; to be opened on local host for redis-server R->L sim 
	 * @param maxNumOfRetrievedRules Maximum number of rules that will be returned. They are ordered. If null, return all.
	 */	
	public GermanBap(int l2rPort, int r2lPort, int maxNumOfRetrievedRules) throws LexicalResourceException
	{
		super(GermanLinProx.class.getClassLoader().getResource(resourcePathL2R), GermanLinProx.class.getClassLoader().getResource(resourcePathR2L), l2rPort, r2lPort, maxNumOfRetrievedRules); 
	}

	/**
	 * This constructor is for the cases where there is no running redis server for the 
	 * requested resource. The constructor will first run a redis-server on local computer 
	 * and initialize the resource. 
	 * 
	 * This constructor will use its assigned default port number. 
	 * 
	 * @param maxNumOfRetrievedRules Maximum number of rules that will be returned. They are ordered. If null, return all.
	 */	
	public GermanBap(int maxNumOfRetrievedRules) throws LexicalResourceException
	{
		this(redisPortL2RDefault, redisPortR2LDefault, maxNumOfRetrievedRules); 
	}
	
	/**
	 * This constructor is for the cases where there is no running redis server for the 
	 * requested resource. The constructor will first run a redis-server on local computer 
	 * and initialize the resource. 
	 * 
	 * This constructor will use its assigned default port number, with default number of maximum rules.
	 * 
	 */	
	public GermanBap() throws LexicalResourceException
	{
		this(redisPortL2RDefault, redisPortR2LDefault, maxRuleNumDefault); 
	}

	// private variables
	//
	// path to the rdb files in Jar. 
	private static final String resourcePathL2R = "redis-german-bap/similarity-l2r.rdb"; 
	private static final String resourcePathR2L = "redis-german-bap/similarity-r2l.rdb"; 
	
	// the values to be used if not given in the constructor. 
	private static final int redisPortL2RDefault = 9521; // if not given in the constructor 
	private static final int redisPortR2LDefault = 9522; // if not given in the constructor. 

	// maximum number of rules returned by the resource 
	private static final int maxRuleNumDefault = 20; 
}