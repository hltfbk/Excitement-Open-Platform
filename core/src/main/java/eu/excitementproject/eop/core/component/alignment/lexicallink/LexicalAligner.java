package eu.excitementproject.eop.core.component.alignment.lexicallink;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Link.Direction;
import eu.excitement.type.alignment.Target;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceCloseException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia.WikiRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;


/**
 * Produces alignment links between the text and the hypothesis,
 * based on lexical rules: if T contains a phrase t, H contains
 * a phrase h and a lexical resource contains one of the rules 
 * t->h or h->t, then an alignment link between t and h will be 
 * created.
 * <P>
 * Usage: align a sentence pair by calling {@link #align(String, String)} method.
 * When the {@linkplain Aligner} object is no longer to be used, the
 * {@link #cleanUp()} method should be called.
 * 
 * @author Vered Shwartz
 * @since 26/05/2014
 */
public class LexicalAligner implements AlignmentComponent {

	// Constants
	private static final String LEXICAL_RESOURCES_CONF_SECTION = "LexicalResources";
	private static final String GENERAL_PARAMS_CONF_SECTION = "GeneralParameters";
	private static final String MAX_PHRASE_KEY = "maxPhraseLength";
	private static final String WORDNET = "wordnet";
	private static final String USE_LEMMA_PARAM = "useLemma";
	private static final String VERSION_PARAM = "version";
	
	// Private Members
	private List<Token> textTokens;
	private List<Token> hypoTokens;
	private List<LexicalResource<? extends RuleInfo>> lexicalResources;
	private int maxPhrase = 0;
	private HashMap<String, LexicalResourceInformation> lexicalResourcesInformation;
	private static final Logger logger = Logger.getLogger(LexicalAligner.class);
	
	// Public Methods
	
	/**
	 * Initialize a lexical aligner
	 * @param config a CommonConfig instance. The aligner retrieves the lexical 
	 * resources configuration values. 
	 * @throws AlignmentComponentException if initialization failed
	 */
	public LexicalAligner(CommonConfig config) throws AlignmentComponentException {
		
		lexicalResourcesInformation = new HashMap<String, LexicalResourceInformation>();
		
		// Initialize the lexical aligner
		try {
			init(config);
		} catch (ConfigurationException | LexicalResourceException e) {
			throw new AlignmentComponentException(
					"Could not initialize the lexical aligner", e);
		}
	}
	
	/**
	 * Align the text and the hypothesis.
	 * <P>
	 * This method receives a JCAS object containing two views:
	 * Hypothesis and text views. The method assumes that the views
	 * were already annotated with a tokenizer.
	 * <P>
	 * The lexical aligner looks at every phrase t in the text and every phrase
	 * h in the hypothesis, and uses the lexical resources to find rules with
	 * lhs = t and rhs = h.  
	 * @param aJCas the JCAS object with the text and hypothesis view.
	 * @throws AlignmentComponentException 
	 */
	@Override
	public void annotate(JCas aJCas) throws AlignmentComponentException {
		
		try {

			logger.info("Started annotating a text and hypothesis pair using lexical aligner");
			
			// Get the tokens and lemmas of the text and hypothesis
			getTokenAnnotations(aJCas);
			
			// Check in all the resources for rules of type textPhrase -> hypoPhrase 
			for (LexicalResource<? extends RuleInfo> resource : lexicalResources) {
				
				LexicalResourceInformation resourceInfo = 
						lexicalResourcesInformation.get(resource.getClass().getName());
				
				// For every phrase t in T and phrase h in H, check the lexical
				// resources if they contain a rule t->h
				String textPhrase = "", hypoPhrase = "";
				
				for (int textStart = 0; textStart < textTokens.size(); ++textStart) {
					for (int textEnd = textStart; textEnd < Math.min(textTokens.size(), 
							textStart + maxPhrase); ++textEnd) {
						
						textPhrase = getPhrase(textTokens, textStart, textEnd, 
								resourceInfo.useLemma());
						
						for (int hypoStart = 0; hypoStart < hypoTokens.size(); ++hypoStart) {
							for (int hypoEnd = hypoStart; hypoEnd < Math.min(hypoTokens.size(), 
									hypoStart + maxPhrase); ++hypoEnd) {
								
								hypoPhrase = getPhrase(hypoTokens, hypoStart, hypoEnd, 
										resourceInfo.useLemma());
								
								// Get the rules textPhrase -> hypoPhrase
								List<LexicalRule<? extends RuleInfo>> ruleFromLeft = 
										getRules(resource, textPhrase, hypoPhrase);
								
								// Get the rules hypoPhrase -> textPhrase
								List<LexicalRule<? extends RuleInfo>> ruleFromRight = 
										getRules(resource, hypoPhrase, textPhrase);
								
								// Create the alignment links for the rules
								createAlignmentLinks(
										aJCas, textStart, textEnd,
										hypoStart, hypoEnd, ruleFromLeft, ruleFromRight,
										resourceInfo.getVersion());
							}
						}
					}
				}
			}
			
			logger.info("Finished annotating a text and hypothesis pair using lexical aligner");
			
		} catch (CASException | LexicalResourceException e) {
			
			throw new AlignmentComponentException(
					"LexicalAligner failed aligning the sentence pair.", e);
		} 		
	}

	@Override
	public String getComponentName() {

		// Name of this component that is used to identify the related configuration section
		return this.getClass().getName(); 
	}

	@Override
	public String getInstanceName() {
		
		// This component does not support instance configuration 
		return null; 
	} 
	
	/**
	 * Cleans up any resources that were used by the aligner.
	 * <P>
	 * Call this method when the aligner is no longer to be used.
	 */
	public void cleanUp() {

		// Close the lexical resources
		for (LexicalResource<? extends RuleInfo> lexicalResource : lexicalResources) {
			try {
				lexicalResource.close();
			} catch (LexicalResourceCloseException e) {
				logger.warn("Closing the resource failed.", e);
			}
		}
	}
	
	// Private Methods
	
	/**
	 * Call this method once before starting to align sentence pairs.
	 * @param config a CommonConfig instance. The aligner retrieves the lexical 
	 * resources configuration values. 
	 * @throws LexicalResourceException if initialization of a resource failed
	 * @throws ConfigurationException if the configuration is invalid
	 */
	private void init(CommonConfig config) throws LexicalResourceException, 
													ConfigurationException {
		
		// Get the general parameters configuration section
		NameValueTable paramsSection = null;
		try {
			paramsSection = config.getSection(GENERAL_PARAMS_CONF_SECTION);
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e);
		}
		
		maxPhrase = paramsSection.getInteger(MAX_PHRASE_KEY);
				
		// Get the Lexical Resources configuration section
		NameValueTable lexicalResourcesSection = null;
		try {
			lexicalResourcesSection = config.getSection(LEXICAL_RESOURCES_CONF_SECTION);
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e);
		}
		
		lexicalResources = new ArrayList<LexicalResource<? extends RuleInfo>>();
		ConfigurationFile configFile = new ConfigurationFile(config);
		
		// Get each resource and create it using the configuration section related to it
		for (String resourceName : lexicalResourcesSection.keySet()) {
			
			// Get the class name
			String resourceClassName = lexicalResourcesSection.getString(resourceName);
			
			// Get the configuration params
			ConfigurationParams resourceParams = 
					configFile.getModuleConfiguration(resourceName);
			resourceParams.setExpandingEnvironmentVariables(true);
			LexicalResource<? extends RuleInfo> lexicalResource = 
					createLexicalResource(resourceClassName, resourceParams);
			
			if (lexicalResource != null) {
				lexicalResources.add(lexicalResource);
				
				// Add the information about this resource
				lexicalResourcesInformation.put(lexicalResource.getClass().getName(), 
						new LexicalResourceInformation(
								resourceParams.getString(VERSION_PARAM), 
								resourceParams.getBoolean(USE_LEMMA_PARAM)));
			}
		}
	}
	
	/**
	 * Uses the annotations in the CAS and extracts the tokens and 
	 * their lemmas from the text and hypothesis views
	 * @param aJCas The JCas object of the text and hypothesis, 
	 * after tokenization and lemmatization.
	 * @throws CASException
	 */
	private void getTokenAnnotations(JCas aJCas) throws CASException {
		
		// Get the text and hypothesis views
		JCas textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
		JCas hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
		// Get the tokens
		textTokens = new ArrayList<Token>(JCasUtil.select(textView, Token.class));
		hypoTokens = new ArrayList<Token>(JCasUtil.select(hypoView, Token.class));
	}
	
	/**
	 * Get a phrase from a list of consecutive tokens
	 * @param tokens The list of tokens
	 * @param start The start token index
	 * @param end The end token index
	 * @param supportLemma The current lexical resources needs right and left lemmas 
	 * rather than surface words
	 * @return The phrase containing the tokens from start to end
	 */
	private String getPhrase(List<Token> tokens, int start, int end, boolean supportLemma) {

		StringBuilder phrase = new StringBuilder();
		
		for (int tokenIndex = start; tokenIndex < end + 1; ++tokenIndex) {
			phrase.append(supportLemma ? 
					tokens.get(tokenIndex).getLemma().getValue() :
					tokens.get(tokenIndex).getCoveredText());
			phrase.append(" ");
		}
		
		// Remove last space
		if (phrase.length() > 0) {
			phrase.deleteCharAt(phrase.length() - 1);
		}
		
		return phrase.toString();
	}

	/**
	 * Get rules of type leftSide -> rightSide, using the given lexical resource
	 * @param resource The lexical resource to use
	 * @param leftSide The phrase that will be looked for as lhs of a rule
	 * @param rightSide The phrase that will be looked for as rhs of a rule
	 * @return The list of rules leftSide -> rightSide
	 * @throws LexicalResourceException 
	 */
	private List<LexicalRule<? extends RuleInfo>> 
						getRules(LexicalResource<? extends RuleInfo> resource,
								String leftSide, String rightSide) 
								throws LexicalResourceException {

		List<LexicalRule<? extends RuleInfo>> rules = 
				new ArrayList<LexicalRule<? extends RuleInfo>>();
		
		try {
			
			// WordNet workaround:
			// Make sure the synsets of the right and left sides of the rule
			// are equal to the right and left phrases.
			// (WN returns rules associated with any of the words in the phrase)
			if (resource.getClass().getName().toLowerCase().contains(WORDNET)) {
				
				for (LexicalRule<? extends RuleInfo> rule : 
						resource.getRules(leftSide, null, rightSide, null)) {
					
					WordnetRuleInfo ruleInfo = (WordnetRuleInfo)rule.getInfo();
					
					if ((ruleInfo.getLeftSense().getWords().contains(leftSide)) &&
						(ruleInfo.getRightSense().getWords().contains(rightSide))) {
					
						addRuleToList(rules, rule);
					}
				}
				
			} else {
				
				// Get rules from t to h
				for (LexicalRule<? extends RuleInfo> rule : 
					resource.getRules(leftSide, null, rightSide, null)) {
					
					addRuleToList(rules, rule);
				}
			}
				
		} catch (Exception e) {
			logger.warn("Could not add rules from " + 
						resource.getClass().getSimpleName() + " for " +
						leftSide + "->" + rightSide, e);
		}
		
		return rules;
	}

	/**
	 * Adds a rule to the list of rules, only if there exists no other rule with the
	 * same rule info and a lower confidence
	 * @param rules The list of rules
	 * @param rule The new rule to add
	 */
	private void addRuleToList(List<LexicalRule<? extends RuleInfo>> rules,
			LexicalRule<? extends RuleInfo> rule) {
		
		boolean addRule = true;
		
		for (int otherIndex = 0; otherIndex < rules.size(); ++otherIndex) {
			
			LexicalRule<? extends RuleInfo> otherRule = rules.get(otherIndex);
			
			if (getLinkInfo(rule).equals(getLinkInfo(otherRule))) {
			
				addRule = false;
				
				// Replace the rule with the same info and a lower confidence
				if (rule.getConfidence() > otherRule.getConfidence()) {
					rules.set(otherIndex, rule);
				}
				
				break;
			}
		}
		
		if (addRule) {
			rules.add(rule);
		}
	}

	/**
	 * Add an alignment link from T to H, based on the rule t->h
	 * in which t is a phrase in T from index textStart to textEnd of the tokens,
	 * and h is a phrase in H from index hypoStart to hypoEnd of the tokens,
	 * @param aJCas The JCas object
	 * @param textStart The index of the first token in T in this alignment link 
	 * @param textEnd The index of the last token in T in this alignment link 
	 * @param hypoStart The index of the first token in H in this alignment link 
	 * @param hypoEnd The index of the last token in H in this alignment link 
	 * @param resourceName The lexical resource that this rule came from
	 * @param lexicalResourceVersion The version of the lexical resource
	 * @param confidence The confidence of the rule
	 * @param linkDirection The direction of the link (t to h, h to t or bidirectional). 
	 * @param linkInfo The relation of the rule (Wordnet synonym, Wikipedia redirect etc).
	 * @throws CASException 
	 */
	private void addAlignmentAnnotations(JCas aJCas, 	int textStart, int textEnd, 
														int hypoStart, int hypoEnd, 
														String resourceName,
														String lexicalResourceVersion,
														double confidence,
														Direction linkDirection,
														String linkInfo) 
																throws CASException {
		
		// Get the text and hypothesis views
		JCas textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
		JCas hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		
		// Prepare the Target instances
		Target textTarget = new Target(textView);
		Target hypoTarget = new Target(hypoView);
		
		// Prepare an FSArray instance and put the target annotations in it   
		FSArray textAnnots = new FSArray(textView, textEnd - textStart + 1);
		FSArray hypoAnnots = new FSArray(hypoView, hypoEnd - hypoStart + 1);
		
		int tokenIndex = 0;
		
		for (Token token : textTokens.subList(textStart, textEnd + 1)) { 
			textAnnots.set(tokenIndex++, token);
		}
		
		tokenIndex = 0;
		
		for (Token token : hypoTokens.subList(hypoStart, hypoEnd + 1)) { 
			hypoAnnots.set(tokenIndex++, token);
		}
		
		textTarget.setTargetAnnotations(textAnnots);
		hypoTarget.setTargetAnnotations(hypoAnnots);
		
		// Set begin and end value of the Target annotations
		textTarget.setBegin(textTokens.get(textStart).getBegin());
		textTarget.setEnd(textTokens.get(textEnd).getEnd());
		hypoTarget.setBegin(hypoTokens.get(hypoStart).getBegin());
		hypoTarget.setEnd(hypoTokens.get(hypoEnd).getEnd());
		
		// Add the targets to the indices 
		textTarget.addToIndexes(); 
		hypoTarget.addToIndexes();
		
		// Mark an alignment.Link and add it to the hypothesis view
		Link link = new Link(hypoView); 
		link.setTSideTarget(textTarget); 
		link.setHSideTarget(hypoTarget); 

		// Set the link direction
		link.setDirection(linkDirection); 
		
		// Set strength according to the rule data
		link.setStrength(confidence); 
		
		// Add the link information
		link.setAlignerID(resourceName);  
		link.setAlignerVersion(lexicalResourceVersion); 
		link.setLinkInfo(linkInfo);
		
		// Mark begin and end according to the hypothesis target
		link.setBegin(hypoTarget.getBegin()); 
		link.setEnd(hypoTarget.getEnd());
		
		// Add to index 
		link.addToIndexes(); 
	}
	
	/**
	 * Receives a rule and return the type of the rule,
	 * such as "synonym" or "hypernym" for WordNet, "redirect" 
	 * for Wikipedia, etc. The default value is "local-entailment".<br>
	 * A better solution is to add an abstract class implementing RuleInfo, 
	 * that all the concrete RuleInfos will extend. This class will contain a 
	 * field "relation" with a default of "local-entailment".
	 * Then we can call: rule.getInfo().getRelation() without having to
	 * know which resource the rule belongs to.
	 * @param rule
	 * @return The type of the rule
	 */
	private String getLinkInfo(LexicalRule<? extends RuleInfo> rule) {

		String type = "local-entailment";
		
		// WordNet
		if (rule.getResourceName().equals("WORDNET")) {
			type = ((WordnetRuleInfo)rule.getInfo()).getTypedRelation().name();
		}
		
		// Wikipedia
		else if (rule.getResourceName().equals("Wikipedia")) {
			type = ((WikiRuleInfo)rule.getInfo()).getBestExtractionType().name();
		}
		
		// VerbOcean
		else if (rule.getResourceName().equals("VerbOcean")) {
			type = ((VerbOceanRuleInfo)rule.getInfo()).getRelationType().name();
		}
		
		return type;
	}

	/**
	 * Constructs a {@link LexicalResource} for the given class name
	 * and a configuration subsection with parameters related to it. 
	 * 
	 * This function is allowed to return null.<BR>
	 * The caller must check if the return value is null.
	 * 
	 * @param resourceClassName The class name of the lexical resource to load
	 * @param configurationParams The {@link ConfigurationParams} object related
	 * to the specific lexical resources.
	 * @return
	 * @throws ConfigurationException
	 * @throws LexicalResourceException
	 */
	@SuppressWarnings("unchecked")
	private LexicalResource<? extends RuleInfo> 
		createLexicalResource(String resourceClassName, 
				ConfigurationParams configurationParams) 
				throws ConfigurationException, LexicalResourceException
	{
		LexicalResource<? extends RuleInfo> lexicalResource = null;
		
		// Load the class using reflection
		Class<LexicalResource<? extends RuleInfo>> resourceClass;
		Constructor<LexicalResource<? extends RuleInfo>> ctor;
		
		try {
			resourceClass = (Class<LexicalResource<? extends RuleInfo>>) 
					Class.forName(resourceClassName);
			ctor = resourceClass.getConstructor(ConfigurationParams.class);
			lexicalResource = ctor.newInstance(configurationParams);
			logger.info("Loaded resource: " + resourceClassName);
		} catch (Exception e) {
			logger.error("Could not instantiate the lexical resource " + 
					resourceClassName, e);
			return null;
		}
	
		return lexicalResource;
	}
	
	/**
	 * Receives a list of rules of type t->h and h->t and creates the 
	 * alignment links for them
	 * @param aJCas The JCas object
	 * @param textStart The index of the first token in T in this alignment link 
	 * @param textEnd The index of the last token in T in this alignment link 
	 * @param hypoStart The index of the first token in H in this alignment link 
	 * @param hypoEnd The index of the last token in H in this alignment link 
	 * @param rulesFromLeft The list of rules t->h
	 * @param rulesFromRight The list of rules h->t
	 * @param lexicalResourceVersion The lexical resource version
	 * @throws CASException 
	 */
	private void createAlignmentLinks(JCas aJCas, int textStart, int textEnd,
			int hypoStart, int hypoEnd,
			List<LexicalRule<? extends RuleInfo>> rulesFromLeft,
			List<LexicalRule<? extends RuleInfo>> rulesFromRight, 
			String lexicalResourceVersion) throws CASException {
		
		// Find rules that match by rule info and make them bidirectional
		for (int leftRuleIndex = rulesFromLeft.size() - 1; 
				leftRuleIndex >= 0; --leftRuleIndex) {
			for (int rightRuleIndex = rulesFromRight.size() - 1; 
					rightRuleIndex >= 0; --rightRuleIndex) {
				
				if (getLinkInfo(rulesFromLeft.get(leftRuleIndex)).
						equals(getLinkInfo(rulesFromRight.get(rightRuleIndex)))) {
					
					// Remove these rules from the list
					LexicalRule<? extends RuleInfo> rightRule = 
							rulesFromRight.remove(rightRuleIndex);
					LexicalRule<? extends RuleInfo> leftRule = 
							rulesFromLeft.remove(leftRuleIndex);
					
					// Add the annotation
					addAlignmentAnnotations(aJCas, textStart, textEnd, hypoStart, hypoEnd, 
							rightRule.getResourceName(), lexicalResourceVersion, 
							Math.max(rightRule.getConfidence(), leftRule.getConfidence()),
							Direction.Bidirection, getLinkInfo(rightRule));
					
					break;
				}
			}
		}
		
		// Add rules from t to h
		for (LexicalRule<? extends RuleInfo> rule : rulesFromLeft) {
			
			addAlignmentAnnotations(aJCas, textStart, textEnd, hypoStart, hypoEnd, 
					rule.getResourceName(), lexicalResourceVersion, 
					rule.getConfidence(), Direction.TtoH, getLinkInfo(rule));
		}
		
		// Add rules from h to t
		for (LexicalRule<? extends RuleInfo> rule : rulesFromRight) {
			
			addAlignmentAnnotations(aJCas, textStart, textEnd, hypoStart, hypoEnd, 
					rule.getResourceName(), lexicalResourceVersion, 
					rule.getConfidence(), Direction.HtoT, getLinkInfo(rule));
		}
	}
}
