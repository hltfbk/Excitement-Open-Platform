/**
 * 
 */
package eu.excitementproject.eop.lexicalminer.LexiclRulesRetrieval;


import java.io.File;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

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
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.Common.BaseRuleInfo;
import eu.excitementproject.eop.lexicalminer.definition.Common.RelationType;
import eu.excitementproject.eop.lexicalminer.definition.Common.PatternRuleInfo.LexicalPatternRuleInfo;
import eu.excitementproject.eop.lexicalminer.definition.Common.PatternRuleInfo.SyntacticPatternRuleInfo;
import eu.excitementproject.eop.lexicalminer.definition.classifier.Classifier;
import eu.excitementproject.eop.lexicalminer.definition.idm.Pattern;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo.CategoryRuleInfo;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo.LinkRuleInfo;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo.ParenthesesRuleInfo;
import eu.excitementproject.eop.lexicalminer.wikipedia.common.ruleInfo.RedirectRuleInfo;


/**
 * @author mirond
 *
 */
public class WikipediaLexicalResource implements LexicalResource<BaseRuleInfo> {

	private static final int DEFAULT_RULES_LIMIT = 1000;
	private static final double MINIMAL_CONFIDENCE = 0.00000000001;

	//private static Logger logger = Logger.getLogger(WikipediaLexicalResource.class);
	
	public Classifier getClassifier() {
		return m_classifier;
	}

	private PartOfSpeech m_nounPOS;
	private Classifier m_classifier;
	private final int m_limitOnRetrievedRules;
	private RetrievalTool m_retrivalTool;

	public static void main(String[] args) throws Exception{
		ConfigurationFile confFile = new ConfigurationFile(new ImplCommonConfig(new File(args[0])));
		WikipediaLexicalResource wlr = new WikipediaLexicalResource(confFile.getModuleConfiguration("WikiV3"));
		List<LexicalRule<? extends BaseRuleInfo>> l1 = wlr.getRulesForLeft("anarchism", null);
		List<LexicalRule<? extends BaseRuleInfo>> l2 = wlr.getRulesForRight("anarchism", null);
		List<LexicalRule<? extends BaseRuleInfo>> l3 = wlr.getRulesForLeft("philosophy", null);
		List<LexicalRule<? extends BaseRuleInfo>> l4 = wlr.getRulesForRight("philosophy", null);
		
		System.out.println("\nanarchism --> :");
		for (LexicalRule<? extends BaseRuleInfo> rule : l1)
			System.out.println("\t" + rule.getRLemma() + ", " + rule.getConfidence());
		System.out.println("\n--> anarchism:");
		for (LexicalRule<? extends BaseRuleInfo> rule : l2)
			System.out.println("\t" + rule.getLLemma() + ", " + rule.getConfidence());
		System.out.println("\nphilosophy --> : ");
		for (LexicalRule<? extends BaseRuleInfo> rule : l3) 
			System.out.println("\t" + rule.getRLemma() + ", " + rule.getConfidence());	
		System.out.println("\n--> philosophy: ");
		for (LexicalRule<? extends BaseRuleInfo> rule : l4) 
			System.out.println("\t" + rule.getLLemma() + ", " + rule.getConfidence());	
	}
 
	public WikipediaLexicalResource(ConfigurationParams params) throws ConfigurationException, LexicalResourceException {

		try {
			this.m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
		} catch (UnsupportedPosTagStringException e) {
			throw new LexicalResourceException(e.toString());
		}

		m_retrivalTool = new RetrievalTool(params);			
		this.m_limitOnRetrievedRules = params.getInt("limitOnRetrievedRules");
		String classifierPathName;
		String classifierClassName;	
		double NPBouns;
		classifierPathName = params.getString("classifierPath");
		classifierClassName = params.getString("classifierClass");
		NPBouns = params.getDouble("NPBouns");
		try {
			this.m_classifier = (Classifier)Class.forName(classifierPathName + "." + classifierClassName).getConstructor(RetrievalTool.class,Double.class).newInstance(m_retrivalTool, NPBouns);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			throw new LexicalResourceException(e.toString());
		}
	}

	
	public WikipediaLexicalResource(Classifier classifier, String  driver, String  url, String  username,
			String  password ) throws UnsupportedPosTagStringException{
		this(classifier, DEFAULT_RULES_LIMIT, driver, url, username, password);
	}

	public WikipediaLexicalResource(Classifier classifier, int limitOnRetrievedRules, String  driver, String  url, String  username,
							String  password  ) throws UnsupportedPosTagStringException {
		this.m_nounPOS = new eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
		this.m_classifier = classifier;
		this.m_limitOnRetrievedRules = limitOnRetrievedRules;

		this.m_retrivalTool = new RetrievalTool(driver, url, username, password);
	}



	public List<LexicalRule<? extends BaseRuleInfo>> getRulesForLeft(String lemma, PartOfSpeech pos) throws LexicalResourceException {

		List<RuleData> rulesData;

		//If it's not a noun, we ignore it...
		if ((pos !=null) && (!(pos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
		{
			return new LinkedList<LexicalRule<? extends BaseRuleInfo>>();
		}

		//get all rules
		try 
		{
		 	rulesData = this.m_retrivalTool.getRulesForLeft(lemma, this.m_classifier.getClassifierId());
		} catch (Exception e) {
			throw new LexicalResourceException("Exception while trying to get Rules For Left",e);
		}

		return makeLexicalRules(rulesData);
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource#getRules(java.lang.String, eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech, java.lang.String, eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech)
	 */
	public List<LexicalRule<? extends BaseRuleInfo>> getRules(String leftLemma,
			PartOfSpeech leftPos, String rightLemma, PartOfSpeech rightPos)
			throws LexicalResourceException {
		List<RuleData> rulesData;

		//If it's not a noun, we ignore it...
		if ((leftPos !=null) && (!(leftPos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
		{
			return new LinkedList<LexicalRule<? extends BaseRuleInfo>>();
		}

		//If it's not a noun, we ignore it...
		if ((rightPos !=null) && (!(rightPos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
		{
			return new LinkedList<LexicalRule<? extends BaseRuleInfo>>();
		}		

		//get all rules
		try 
		{
		 	rulesData = this.m_retrivalTool.getRulesForBothSides(leftLemma, rightLemma, this.m_classifier.getClassifierId());
		} catch (Exception e) {
			throw new LexicalResourceException("Exception while trying to get Rules For Both sides",e);
		}

		return makeLexicalRules(rulesData);
	}

	/**
	 * The function is used to create a LexicalRule List out of the rulesData List
	 * @param rulesData
	 * @return
	 * @throws LexicalResourceException
	 */
	private List<LexicalRule<? extends BaseRuleInfo>> makeLexicalRules(List<RuleData> rulesData) throws LexicalResourceException {

		LinkedList<LexicalRule<? extends BaseRuleInfo>> rules = new LinkedList<LexicalRule<? extends BaseRuleInfo>>(); 
		 	for (RuleData ruleData : rulesData)
		 	{	 						
				LexicalRule<BaseRuleInfo> rule  = 
						new LexicalRule<BaseRuleInfo>(ruleData.getLeftTerm(), this.m_nounPOS,
						ruleData.getRightTerm(), this.m_nounPOS, Math.max(m_classifier.getRank(ruleData),MINIMAL_CONFIDENCE), ruleData.getRuleType(),
						ruleData.getRuleResource(), getRuleInfo(ruleData));
				
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
	 * returns the right RuleInfo according to the RuleType
	 * @param ruleData
	 * @return
	 */
	private BaseRuleInfo getRuleInfo(RuleData ruleData) {
		BaseRuleInfo ruleInfo;
		RelationType rT = RelationType.valueOf(ruleData.getRuleType());
		String pageTitle  = null;
		switch (rT) {

		case LexicalIDM:
			ruleInfo =  new LexicalPatternRuleInfo(ruleData.getWordsPattern(), ruleData.getPOSPattern()
					, ruleData.getFullPattern(), ruleData.getRuleSourceId());
			break;

		case SyntacticIDM:
			Pattern pattern = new Pattern(ruleData.getPOSPattern(), ruleData.getWordsPattern()
											, ruleData.getRelationPattern(), ruleData.getPOSrelationsPattern(), ruleData.getFullPattern());			
			String sentence = ruleData.getRuleMetadata().substring("[m_Sentence= ".length(), ruleData.getRuleMetadata().indexOf(", m_patternString=Pattern ["));

			boolean isNP = false;
			if (ruleData.getRuleMetadata().contains("m_isNPphrase=true"))
			{
				isNP = true;
			}		


			ruleInfo =  new SyntacticPatternRuleInfo(pattern, ruleData.getRuleSourceId(), isNP, ruleData.getLeftTerm(), ruleData.getRightTerm(), sentence);
			break;

		case Category:
			pageTitle = ruleData.getRuleMetadata().substring("m_pageTitle= ".length());			
			ruleInfo =  new CategoryRuleInfo(ruleData.getRuleSourceId(), pageTitle);
			 break;

		case Parenthesis:
			pageTitle = ruleData.getRuleMetadata().substring("m_pageTitle= ".length());						
			ruleInfo =  new ParenthesesRuleInfo(ruleData.getRuleSourceId(), pageTitle);
			break;

		case Redirect:
			pageTitle = ruleData.getRuleMetadata().substring("m_pageTitle= ".length(), ruleData.getRuleMetadata().indexOf(", m_titleToTarget="));		
			boolean m_titleToTarget = false;
			if (ruleData.getRuleMetadata().contains("m_titleToTarget= true"))
			{
				m_titleToTarget = true;
			}	
			ruleInfo =  new RedirectRuleInfo(ruleData.getRuleSourceId(), pageTitle, m_titleToTarget);
			break;

		case Link:
			pageTitle = ruleData.getRuleMetadata().substring("m_linkPageTitle= ".length());						
			ruleInfo = new LinkRuleInfo(ruleData.getRuleSourceId(), pageTitle);
			break;

		default:
			ruleInfo = new LinkRuleInfo(ruleData.getRuleSourceId(), null);		
			break;
		}

		return ruleInfo;
	}


	/**
	 * A private comparator class to compare 2 LexicalRuleComparator by their Confidence (return the reverse result (for a desc sort))
	 */
	public class LexicalRuleReverseComparator implements Comparator<LexicalRule<? extends BaseRuleInfo>>
	{
		public int compare(LexicalRule<? extends BaseRuleInfo> arg0, LexicalRule<? extends BaseRuleInfo> arg1) {
			double diff= arg0.getConfidence() - arg1.getConfidence();
			if (diff> 0)
				return (-1);
			if (diff == 0)
				return 0;
			else
				return (1);
		}

	}public void close() throws LexicalResourceCloseException {
		try {
			RetrievalTool.closeConnection();
		} catch (SQLException e) {
			throw new LexicalResourceCloseException("The DB connction could not be closed", e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource#getRulesForRight(java.lang.String, eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech)
	 */
	public List<LexicalRule<? extends BaseRuleInfo>> getRulesForRight(
			String lemma, PartOfSpeech pos) throws LexicalResourceException {

			List<RuleData> rulesData;

			//If it's not a noun, we ignore it...
			if ((pos !=null) && (!(pos.getCanonicalPosTag().equals(CanonicalPosTag.N))))
			{
				return new LinkedList<LexicalRule<? extends BaseRuleInfo>>();
			}

			//get all rules
			try 
			{
			 	rulesData = this.m_retrivalTool.getRulesForRight(lemma, this.m_classifier.getClassifierId());
			} catch (Exception e) {
				throw new LexicalResourceException("Exception while trying to get Rules For Right",e);
			}

			return makeLexicalRules(rulesData);
		}
}