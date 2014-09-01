package eu.excitementproject.eop.core.component.lexicalknowledge.geo;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.core.component.lexicalknowledge.EmptyRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceNothingToClose;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.redis.Configuration;
import eu.excitementproject.eop.redis.RedisBasedStringListBasicMap;
import eu.excitementproject.eop.redis.RedisRunException;

/**
 * A {@link LexicalResource} to wrap the Geo resource's Redis files. 
 *
 * The part of speech on GEO is always NOUN, and thus the POS parameters are ignored.
 *   
 * @author Meni Adler
 *
 * @since 26 March 2014
 */
public class RedisBasedGeoLexicalResource extends LexicalResourceNothingToClose<EmptyRuleInfo> {

	private static final String GEO_RESOURCE_NAME = "GEO";
	private static final EmptyRuleInfo EMPTY_INFO = EmptyRuleInfo.getInstance();	
	private final BySimplerCanonicalPartOfSpeech NOUN;
	private RedisBasedStringListBasicMap leftRules, rightRules;

	public RedisBasedGeoLexicalResource(ConfigurationParams params) throws ConfigurationException, LexicalResourceException, RedisRunException {

		String redisDir = null;
		try {
			redisDir = params.get(Configuration.REDIS_BIN_DIR);
		} catch (ConfigurationException e) {}

		String hostLeft = null;
		int portLeft = -1;
		String hostRight = null;
		int portRight = -1;
		try {
			hostLeft = params.get(Configuration.L2R_REDIS_HOST);
			portLeft = params.getInt(Configuration.L2R_REDIS_PORT);
			hostRight = params.get(Configuration.R2L_REDIS_HOST);
			portRight = params.getInt(Configuration.R2L_REDIS_PORT);
		} catch (ConfigurationException e) {
		}

		boolean bVM = false;
		try {
			bVM = params.getBoolean(Configuration.REDIS_VM);
		} catch (ConfigurationException e) {
		}
		if (hostLeft == null || portLeft == -1 || hostRight == null || portRight == -1) {
			try {
				leftRules = (redisDir == null ? new RedisBasedStringListBasicMap(params.get(Configuration.L2R_REDIS_DB_FILE),bVM) : new RedisBasedStringListBasicMap(params.get(Configuration.L2R_REDIS_DB_FILE),redisDir,bVM));
				rightRules = (redisDir == null ? new RedisBasedStringListBasicMap(params.get(Configuration.R2L_REDIS_DB_FILE),bVM) : new RedisBasedStringListBasicMap(params.get(Configuration.R2L_REDIS_DB_FILE), redisDir,bVM));
			} catch (Exception e) {
				throw new RedisRunException(e);
			}
			
		} else  {
			leftRules = new RedisBasedStringListBasicMap(hostLeft,portLeft);
			rightRules = new RedisBasedStringListBasicMap(hostRight,portRight);
		}
		try 										{ NOUN = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);	} 
		catch (UnsupportedPosTagStringException e) 	{ throw new LexicalResourceException("Bug: couldn't construct a new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.NOUN)",e);		}		


	}

	
	public RedisBasedGeoLexicalResource(String leftRedisDBFile, String rightRedisDBFile) throws UnsupportedPosTagStringException, FileNotFoundException, RedisRunException, LexicalResourceException{
		leftRules = new RedisBasedStringListBasicMap(leftRedisDBFile, false);
		rightRules = new RedisBasedStringListBasicMap(rightRedisDBFile,false);
		try 										{ NOUN = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);	} 
		catch (UnsupportedPosTagStringException e) 	{ throw new LexicalResourceException("Bug: couldn't construct a new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.NOUN)",e);		}		
	}
	
	public RedisBasedGeoLexicalResource(String leftRedisHost, int leftRedisPort, String rightRedisHost, int rightRedisPort) throws UnsupportedPosTagStringException, LexicalResourceException {
		leftRules = new RedisBasedStringListBasicMap(leftRedisHost,leftRedisPort);
		rightRules = new RedisBasedStringListBasicMap(rightRedisHost,rightRedisPort);
		try 										{ NOUN = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);	} 
		catch (UnsupportedPosTagStringException e) 	{ throw new LexicalResourceException("Bug: couldn't construct a new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.NOUN)",e);		}				
	}

	public List<LexicalRule<? extends EmptyRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRules(lemma,pos,leftRules);
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource#getRulesForRight(java.lang.String, eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech)
	 */
	public List<LexicalRule<? extends EmptyRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRules(lemma,pos,rightRules);
	}
	
	public List<LexicalRule<? extends EmptyRuleInfo>> getRules(String lemma1, PartOfSpeech pos, RedisBasedStringListBasicMap rules) throws LexicalResourceException {
		
			List<LexicalRule<? extends EmptyRuleInfo>> ret = new ArrayList<LexicalRule<? extends EmptyRuleInfo>>();
			//If it's not a noun, we ignore it...		
			if ((pos !=null) && (!(pos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
			{
				return ret;
			}

			//get all rules
			try {				
			 	for (String lemma2 : rules.get(lemma1)) {
			 		ret.add(new LexicalRule<EmptyRuleInfo>(lemma1, NOUN, lemma2, NOUN, null, GEO_RESOURCE_NAME, EMPTY_INFO));
			 	}
			 	return ret;	
			} catch (Exception e) {
				throw new LexicalResourceException("Exception while trying to get rules",e);
			}			
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.lexical_resource.LexicalResource#getRules(java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech, java.lang.String, ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	public List<LexicalRule<? extends EmptyRuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos)
			throws LexicalResourceException {
		
		List<LexicalRule<? extends EmptyRuleInfo>> ret = new ArrayList<LexicalRule<? extends EmptyRuleInfo>>();
		
		//If it's not a noun, we ignore it...
		if ((leftPos !=null) && (!(leftPos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
		{
			return ret;
		}

		//If it's not a noun, we ignore it...
		if ((rightPos !=null) && (!(rightPos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
		{
			return ret;
		}		

		try 
		{
		 	for (String lemma2 : leftRules.get(leftLemma)) {
		 		if (lemma2.equals(rightLemma))
		 			ret.add(new LexicalRule<EmptyRuleInfo>(leftLemma, NOUN, rightLemma, NOUN, null, GEO_RESOURCE_NAME, EMPTY_INFO));
		 	}
		 	return ret;	
		} catch (Exception e) {
			throw new LexicalResourceException("Exception while trying to get Rules For Both sides",e);
		}		
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceNothingToClose#close()
	 */
	@Override
	public void close() throws LexicalResourceCloseException {
		leftRules.close();
		rightRules.close();
	}
	
	
	
	public static void main(String[] args) throws LexicalResourceException, UnsupportedPosTagStringException, FileNotFoundException, RedisRunException {

		if (args.length != 2) {
			System.out.println("Usage: eu.excitementproject.eop.core.component.lexicalknowledge.geo.RedisBasedGeoLexicalResource <l2r geo redis file> <r2l geo redfis file>");
			System.exit(0);
		}
		
		String lLemma = "San Jose";
		PartOfSpeech pos2 = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
		String rLemma = "United Kingdom";
		
		RedisBasedGeoLexicalResource GEOLexR = new  RedisBasedGeoLexicalResource(args[0],args[1]); 

		List<LexicalRule<? extends EmptyRuleInfo>> rules2 = GEOLexR.getRulesForLeft(lLemma, pos2 );
		
		System.out.println("Got "+rules2.size() + " for: " + lLemma + ", " + pos2 );
		for (LexicalRule<? extends EmptyRuleInfo> rule : rules2)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + rules2.size() + " relations");
		System.out.println("\n*****************************\n");
				
		List<LexicalRule<? extends EmptyRuleInfo>> otherRules = GEOLexR.getRules(lLemma, null, rLemma, null);
		System.out.println("Got "+otherRules.size() + " for: " + lLemma + ", " + pos2 + ", "  + rLemma + ", "  + pos2 );
		for (LexicalRule<? extends EmptyRuleInfo> rule : otherRules)
			System.out.println(rule);
		
		System.out.println(lLemma +" has " + otherRules.size() + " relations");
	}


}

