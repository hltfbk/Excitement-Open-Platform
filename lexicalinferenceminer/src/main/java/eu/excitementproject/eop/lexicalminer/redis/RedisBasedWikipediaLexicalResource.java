/**
 * 
 */
package eu.excitementproject.eop.lexicalminer.redis;


import java.io.File;




import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.Classifier;
import eu.excitementproject.eop.redis.Configuration;
import eu.excitementproject.eop.redis.RedisBasedStringListBasicMap;
import eu.excitementproject.eop.redis.RedisRunException;


/**
 * @author Meni Adler
 *
 */
public class RedisBasedWikipediaLexicalResource implements LexicalResource<WikiRuleInfo> {

	private static final int DEFAULT_RULES_LIMIT = 100;
	private static final double MINIMAL_CONFIDENCE = 0.00000000001;
	private static final double MAXIMAL_CONFIDENCE = 1 - 0.00000000001;
	public static final String CLASSIFIER_CLASS_NAME = "###classifier-class###";
	public static final String CLASSIFIER_ID_NAME = "###classifier-id###";

	//protected static final String PARAM_STOP_WORDS = "stop-words";
	//protected static final String PARAM_EXTRACTION_TYPES = "extraction-types";
	//protected Set<String> stopWords;
	//protected final Set<String> extractionTypes;

	//private static Logger logger = Logger.getLogger(WikipediaLexicalResource.class);
	
	public Classifier getClassifier() {
		return m_classifier;
	}

	private PartOfSpeech m_nounPOS;
	private Classifier m_classifier;
	private final int m_limitOnRetrievedRules;
	private RedisBasedStringListBasicMap leftRules, rightRules;

	//private RetrievalTool m_retrivalTool;

	public static void main(String[] args) throws Exception{
		
		ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
		RedisBasedWikipediaLexicalResource wlr = new RedisBasedWikipediaLexicalResource(confFile.getModuleConfiguration("WikiV3"));
		
		String term = "";
		for (int i = 1; i < args.length; i++)
			term += (" " + args[i]);
		term = term.trim();

		List<LexicalRule<? extends WikiRuleInfo>> l1 = wlr.getRulesForLeft(term, null);
		List<LexicalRule<? extends WikiRuleInfo>> l2 = wlr.getRulesForRight(term, null);


		System.out.println("\n" + term + "--> :");
		for (LexicalRule<? extends WikiRuleInfo> rule : l1)
			System.out.println("\t" + rule.getRLemma() + ", " + rule.getConfidence());
		System.out.println("\n--> " + term + ": ");
		for (LexicalRule<? extends WikiRuleInfo> rule : l2) 
			System.out.println("\t" + rule.getLLemma() + ", " + rule.getConfidence());	
	}
 
	public RedisBasedWikipediaLexicalResource(ConfigurationParams params) throws ConfigurationException, LexicalResourceException, RedisRunException {

		try {
			this.m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
		} catch (UnsupportedPosTagStringException e) {
			throw new LexicalResourceException(e.toString());
		}

		this.m_limitOnRetrievedRules = params.getInt("limitOnRetrievedRules");
		double NPBouns;
		NPBouns = params.getDouble("NPBouns");
		initRedisDB(params);
		
		String classifierPathName = params.getString("classifierPath");
		String classifierClassName = params.getString("classifierClass");

		String sLeftClassId = leftRules.getKeyValue(classifierClassName);
		if (sLeftClassId == null) 
			throw new LexicalResourceException("Class " + classifierClassName + " is not defined in the given l2r Redis database");
		String sRightClassId = rightRules.getKeyValue(classifierClassName);
		if (sRightClassId == null) 
			throw new LexicalResourceException("Class " + classifierClassName + " is not defined in the given r2l Redis database");

		int leftClassifierId = Integer.parseInt(sLeftClassId);
		int rightClassifierId = Integer.parseInt(sRightClassId);

		if (leftClassifierId != rightClassifierId)
			throw new LexicalResourceException("Wrong classifier class definition: l2r " + leftClassifierId + ", r2l " + rightClassifierId);
		
		try {
			this.m_classifier = (Classifier)Class.forName(classifierPathName + "." + classifierClassName).getConstructor(RetrievalTool.class,Double.class).newInstance(null,NPBouns);			
			this.m_classifier.setClassifierId(leftClassifierId);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			throw new LexicalResourceException(e.toString());
		}

		
	}
	
	private void initRedisDB(ConfigurationParams params) throws RedisRunException {
	
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
		
		if (hostLeft == null || portLeft == -1 || hostRight == null || portRight == -1) {
			try {
				leftRules = (redisDir == null ? new RedisBasedStringListBasicMap(params.get(Configuration.L2R_REDIS_DB_FILE)) : new RedisBasedStringListBasicMap(params.get(Configuration.L2R_REDIS_DB_FILE),redisDir));
				rightRules = (redisDir == null ? new RedisBasedStringListBasicMap(params.get(Configuration.R2L_REDIS_DB_FILE)) : new RedisBasedStringListBasicMap(params.get(Configuration.R2L_REDIS_DB_FILE), redisDir));
			} catch (Exception e) {
				throw new RedisRunException(e);
			}
			
		} else  {
			leftRules = new RedisBasedStringListBasicMap(hostLeft,portLeft);
			rightRules = new RedisBasedStringListBasicMap(hostRight,portRight);
		}


	}

	
	public RedisBasedWikipediaLexicalResource(Classifier classifier, String leftRedisDBFile, String rightRedisDBFile) throws UnsupportedPosTagStringException, FileNotFoundException, RedisRunException{
		this(classifier, DEFAULT_RULES_LIMIT, leftRedisDBFile, rightRedisDBFile);
	}

	public RedisBasedWikipediaLexicalResource(Classifier classifier, int limitOnRetrievedRules, String leftRedisDBFile, String rightRedisDBFile) throws UnsupportedPosTagStringException, FileNotFoundException, RedisRunException {
		leftRules = new RedisBasedStringListBasicMap(leftRedisDBFile);
		rightRules = new RedisBasedStringListBasicMap(rightRedisDBFile);
		this.m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
		this.m_classifier = classifier;
		this.m_limitOnRetrievedRules = limitOnRetrievedRules;
		
	}
	
	public RedisBasedWikipediaLexicalResource(Classifier classifier, int limitOnRetrievedRules, String leftRedisHost, int leftRedisPort, String rightRedisHost, int rightRedisPort) throws UnsupportedPosTagStringException {
		leftRules = new RedisBasedStringListBasicMap(leftRedisHost,leftRedisPort);
		rightRules = new RedisBasedStringListBasicMap(rightRedisHost,rightRedisPort);
		this.m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
		this.m_classifier = classifier;
		this.m_limitOnRetrievedRules = limitOnRetrievedRules;
	}



	public List<LexicalRule<? extends WikiRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRules(lemma,pos,true);
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource#getRulesForRight(java.lang.String, eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech)
	 */
	public List<LexicalRule<? extends WikiRuleInfo>> getRulesForRight(String lemma, PartOfSpeech pos) throws LexicalResourceException {
		return getRules(lemma,pos,false);
	}
	
	public List<LexicalRule<? extends WikiRuleInfo>> getRules(String lemma, PartOfSpeech pos, boolean l2r) throws LexicalResourceException {	
			
			// the terms were saved with lower case
			if (lemma != null)
				lemma = lemma.toLowerCase();
		
			//If it's not a noun, we ignore it...
			if ((pos !=null) && (!(pos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
			{
				return new LinkedList<LexicalRule<? extends WikiRuleInfo>>();
			}

			//get all rules
			try 
			{	List<RedisRuleData> rulesData = new LinkedList<RedisRuleData>();
			 	for (String value : (l2r ? leftRules.get(lemma) : rightRules.get(lemma))) 			 		
			 		rulesData.add(new RedisRuleData(lemma,value,l2r));
			 	return makeLexicalRules(rulesData);	
			} catch (Exception e) {
				throw new LexicalResourceException("Exception while trying to get rules",e);
			}			
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource#getRules(java.lang.String, eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech, java.lang.String, eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech)
	 */
	public List<LexicalRule<? extends WikiRuleInfo>> getRules(String leftLemma,
			PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos)
			throws LexicalResourceException {

		// the terms were saved with lower case
		if (leftLemma != null)
			leftLemma = leftLemma.toLowerCase();
		if (rightLemma != null)
			rightLemma = rightLemma.toLowerCase();
		
		//If it's not a noun, we ignore it...1
		if ((leftPos !=null) && (!(leftPos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
		{
			return new LinkedList<LexicalRule<? extends WikiRuleInfo>>();
		}

		//If it's not a noun, we ignore it...
		if ((rightPos !=null) && (!(rightPos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
		{
			return new LinkedList<LexicalRule<? extends WikiRuleInfo>>();
		}		

		try 
		{
			List<RedisRuleData> rulesData = new LinkedList<RedisRuleData>();
		 	for (String value : leftRules.get(leftLemma)) {
		 		RedisRuleData ruleData = new RedisRuleData(leftLemma,value,true);
		 		if (ruleData.getRightTerm().equals(rightLemma))
		 			rulesData.add(ruleData);
		 	}
		 	return makeLexicalRules(rulesData);	
		} catch (Exception e) {
			throw new LexicalResourceException("Exception while trying to get Rules For Both sides",e);
		}
	}

	/**
	 * The function is used to create a LexicalRule List out of the rulesData List
	 * @param rulesData
	 * @return
	 * @throws LexicalResourceException
	 */
	private List<LexicalRule<? extends WikiRuleInfo>> makeLexicalRules(List<RedisRuleData> rulesData) throws LexicalResourceException {

		// sim,ulates the 'order by' in the original SQL query
		Collections.sort(rulesData, new RuleDataReverseComparator());
		
		LinkedList<LexicalRule<? extends WikiRuleInfo>> rules = new LinkedList<LexicalRule<? extends WikiRuleInfo>>(); 
	 	for (RedisRuleData ruleData : rulesData) {	
			LexicalRule<WikiRuleInfo> rule  =
					new LexicalRule<WikiRuleInfo>(ruleData.getLeftTerm(), this.m_nounPOS,
					ruleData.getRightTerm(), this.m_nounPOS, Math.max(Math.min(m_classifier.getRank(ruleData),MAXIMAL_CONFIDENCE),MINIMAL_CONFIDENCE), ruleData.getRuleType(),
					"Wikipedia", WikiRuleInfo.getInstance());
			rules.add(rule);		
		}

	 	//sort rules
	 	Collections.sort(rules, new LexicalRuleReverseComparator());
	 	if (rules.size() > this.m_limitOnRetrievedRules)
	 	{
	 		return rules.subList(0, this.m_limitOnRetrievedRules);
	 	}
	 	else
	 	{
	 		return rules;
	 	}
	}

	/**
	 * A private comparator class to compare 2 LexicalRuleComparator by their Confidence (return the reverse result (for a desc sort))
	 */
	public class LexicalRuleReverseComparator implements Comparator<LexicalRule<? extends RuleInfo>>
	{
		public int compare(LexicalRule<? extends RuleInfo> arg0, LexicalRule<? extends RuleInfo> arg1) {
			double diff= arg0.getConfidence() - arg1.getConfidence();
			if (diff> 0)
				return (-1);
			if (diff == 0)
				return 0;
			else
				return (1);
		}

	}
	
	/**
	 * A private comparator class to compare 2 LexicalRuleComparator by their Confidence (return the reverse result (for a desc sort))
	 */
	public class RuleDataReverseComparator implements Comparator<RedisRuleData>
	{
		public int compare(RedisRuleData arg0, RedisRuleData arg1) {
			double diff= m_classifier.getRank(arg0) - m_classifier.getRank(arg1);
			if (diff == 0)
				diff = arg0.getDefultRank() - arg1.getDefultRank();
			
			if (diff> 0)
				return (-1);
			if (diff == 0)
				return 0;
			else
				return (1);
		}

	}

	public void close() throws LexicalResourceCloseException {
		leftRules.close();
		rightRules.close();
	}


}