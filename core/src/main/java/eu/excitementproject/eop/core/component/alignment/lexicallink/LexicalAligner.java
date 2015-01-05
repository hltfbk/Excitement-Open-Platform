package eu.excitementproject.eop.core.component.alignment.lexicallink;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyStringList;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.NonEmptyStringList;
import org.apache.uima.jcas.cas.StringList;
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
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanRuleInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Produces alignment links between the text and the hypothesis,
 * based on lexical rules: if T contains a phrase t, H contains
 * a phrase h and a lexical resource contains one of the rules 
 * t->h or h->t, then an alignment link between t and h will be 
 * created.
 * <P>
 * Usage: Align a sentence pair by calling {@link #annotate(JCas)} method.
 * Configure the aligner using the LexicalAligner.xml configuration file. 
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
	private static final String LEFT_SIDE_POS_PARAM = "leftSidePOS";
	private static final String RIGHT_SIDE_POS_PARAM = "rightSidePOS";
	private static final String VERSION_PARAM = "version";
	
	// Private Members
	private List<Token> textTokens;
	private List<Token> hypoTokens;
	private List<LexicalResource<? extends RuleInfo>> lexicalResources;
	private int maxPhrase = 0;
	private HashMap<String, LexicalResourceInformation> lexicalResourcesInformation;
	private static final Logger logger = Logger.getLogger(LexicalAligner.class);
	private static final HashMap<String, String> linkInfoToDomainLevel;
	private static final HashMap<String, String> linkInfoToInferenceLevel;
	private static final HashMap<String, String> linkInfoToDirectionality;
	
	// Static Initializer
	static {
		
		// Define the specific relations to domain level table
		linkInfoToDomainLevel = new HashMap<String, String>();
		linkInfoToDomainLevel.put("WORDNET__SYNONYM", "SYNONYM");
		linkInfoToDomainLevel.put("WORDNET__HYPERNYM", "HYPERNYM");
		linkInfoToDomainLevel.put("WORDNET__INSTANCE_HYPERNYM", "HYPERNYM");
		linkInfoToDomainLevel.put("VerbOcean__STRONGER_THAN", "HYPERNYM");
		linkInfoToDomainLevel.put("WORDNET__HYPONYM", "HYPONYM");
		linkInfoToDomainLevel.put("WORDNET__INSTANCE_HYPONYM", "HYPONYM");
		linkInfoToDomainLevel.put("WORDNET__TROPONYM", "HYPONYM");
		linkInfoToDomainLevel.put("WORDNET__MEMBER_MERONYM", "MERONYM");
		linkInfoToDomainLevel.put("WORDNET__PART_MERONYM", "MERONYM");
		linkInfoToDomainLevel.put("WORDNET__SUBSTANCE_MERONYM", "MERONYM");
		linkInfoToDomainLevel.put("WORDNET__MEMBER_HOLONYM", "HOLONYM");
		linkInfoToDomainLevel.put("WORDNET__PART_HOLONYM", "HOLONYM");
		linkInfoToDomainLevel.put("WORDNET__SUBSTANCE_HOLONYM", "HOLONYM");
		linkInfoToDomainLevel.put("WORDNET__CAUSE", "CAUSE");
		linkInfoToDomainLevel.put("WORDNET__DERIVATIONALLY_RELATED", "DERIVATIONALLY_RELATED");
		linkInfoToDomainLevel.put("VerbOcean__HAPPENS_BEFORE", "HAPPENS_BEFORE");
		linkInfoToDomainLevel.put("WORDNET__ANTONYM", "ANTONYM");
		linkInfoToDomainLevel.put("VerbOcean__OPPOSITE_OF", "ANTONYM");
		
		// Define the specific relations to inference level table
		linkInfoToInferenceLevel = new HashMap<String, String>();
		linkInfoToInferenceLevel.put("WORDNET__ANTONYM", "LOCAL_CONTRADICTION");
		linkInfoToInferenceLevel.put("VerbOcean__OPPOSITE_OF", "LOCAL_CONTRADICTION");
		linkInfoToInferenceLevel.put("Wikipedia_Redirect", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__SYNONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__DERIVATIONALLY_RELATED", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("CatVar__local-entailment", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("Wordnet__ENTAILMENT", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("Wikipedia_BeComp", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("Wikipedia_Parenthesis", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("Wikipedia_Category", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__HYPERNYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__INSTANCE_HYPERNYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("VerbOcean__STRONGER_THAN", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__HYPONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__INSTANCE_HYPONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__TROPONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__MEMBER_MERONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__PART_MERONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__SUBSTANCE_MERONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__MEMBER_HOLONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__PART_HOLONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__SUBSTANCE_HOLONYM", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__CAUSE", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("VerbOcean__HAPPENS_BEFORE", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("GEO__local-entailment", "LOCAL_ENTAILMENT");
		linkInfoToInferenceLevel.put("WORDNET__SIMILAR_TO", "LOCAL_SIMILARITY");
		linkInfoToInferenceLevel.put("WORDNET__VERB_GROUP", "LOCAL_SIMILARITY");
		linkInfoToInferenceLevel.put("VerbOcean__SIMILAR", "LOCAL_SIMILARITY");
		linkInfoToInferenceLevel.put("Wikipedia_AllNouns", "LOCAL_SIMILARITY");
		linkInfoToInferenceLevel.put("Wikipedia_Link", "LOCAL_SIMILARITY");
		linkInfoToInferenceLevel.put("distsim-lin-proximity__local-entailment", "LOCAL_SIMILARITY");
		linkInfoToInferenceLevel.put("distsim-lin-dependency__local-entailment", "LOCAL_SIMILARITY");
		linkInfoToInferenceLevel.put("distsim-bap__local-entailment", "LOCAL_SIMILARITY");
		
		// Define the specific relations to directionality table
		linkInfoToDirectionality = new HashMap<String, String>();
		linkInfoToDirectionality.put("Wikipedia_Redirect", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__SYNONYM", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__DERIVATIONALLY_RELATED", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("CatVar__local-entailment", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("Wordnet__ENTAILMENT", "DIRECTIONAL");
		linkInfoToDirectionality.put("Wikipedia_BeComp", "DIRECTIONAL");
		linkInfoToDirectionality.put("Wikipedia_Parenthesis", "DIRECTIONAL");
		linkInfoToDirectionality.put("Wikipedia_Category", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__HYPERNYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__INSTANCE_HYPERNYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("VerbOcean__STRONGER_THAN", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__HYPONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__INSTANCE_HYPONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__TROPONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__MEMBER_MERONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__PART_MERONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__SUBSTANCE_MERONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__MEMBER_HOLONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__PART_HOLONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__SUBSTANCE_HOLONYM", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__CAUSE", "DIRECTIONAL");
		linkInfoToDirectionality.put("VerbOcean__HAPPENS_BEFORE", "DIRECTIONAL");
		linkInfoToDirectionality.put("GEO__local-entailment", "DIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__SIMILAR_TO", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("WORDNET__VERB_GROUP", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("VerbOcean__SIMILAR", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("Wikipedia_AllNouns", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("Wikipedia_Link", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("distsim-lin-proximity__local-entailment", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("distsim-lin-dependency__local-entailment", "BIDIRECTIONAL");
		linkInfoToDirectionality.put("distsim-bap__local-entailment", "DIRECTIONAL");
	}
	
	// Public Methods
	
	/**
	 * Initialize a lexical aligner from the configuration
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
	 * Initialize a lexical aligner using parameters
	 * @param lexicalResources A set of initialized lexical resources
	 * @param maxPhrase The maximum length of phrase to align
	 * @param lexicalResourcesInformation Additional information required for the aligner
	 * about each of the resources, such as whether this resource uses lemma or surface-level tokens,
	 * and whether to limit the alignments to certain relations only. 
	 * The lexicalResourcesInformation should hold keys of type: resource.getClass().getName()
	 */
	public LexicalAligner(List<LexicalResource<? extends RuleInfo>> lexicalResources, 
			int maxPhrase, 
			HashMap<String, LexicalResourceInformation> lexicalResourcesInformation) {
		
		this.lexicalResources = lexicalResources;
		this.lexicalResourcesInformation = lexicalResourcesInformation;
		this.maxPhrase = maxPhrase;
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
										getRules(resource, textPhrase, hypoPhrase,
												resourceInfo.getLeftSidePOS(), resourceInfo.getRightSidePOS());
								
								// Get the rules hypoPhrase -> textPhrase
								List<LexicalRule<? extends RuleInfo>> ruleFromRight = 
										getRules(resource, hypoPhrase, textPhrase,
												resourceInfo.getLeftSidePOS(), resourceInfo.getRightSidePOS());
								
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
				
				PartOfSpeech leftSidePOS = null, rightSidePOS = null;
				
				// Add the information about this resource
				
				// Get the right and left side POS, in case it's mentioned
				if (resourceParams.keySet().contains(LEFT_SIDE_POS_PARAM)) {
					try {
						leftSidePOS = new ByCanonicalPartOfSpeech(resourceParams.getString(LEFT_SIDE_POS_PARAM));
					} catch (UnsupportedPosTagStringException e) {
						logger.warn("Could not load POS for left side: " + 
								resourceParams.getString(LEFT_SIDE_POS_PARAM) + 
								". Alignment links of all POS will be retreived.");
					}
				}
				
				if (resourceParams.keySet().contains(RIGHT_SIDE_POS_PARAM)) {
					try {
						rightSidePOS = new ByCanonicalPartOfSpeech(resourceParams.getString(RIGHT_SIDE_POS_PARAM));
					} catch (UnsupportedPosTagStringException e) {
						logger.warn("Could not load POS for right side: " + 
								resourceParams.getString(RIGHT_SIDE_POS_PARAM) + 
								". Alignment links of all POS will be retreived.");
					}
				}
				
				lexicalResourcesInformation.put(lexicalResource.getClass().getName(), 
						new LexicalResourceInformation(
								resourceParams.getString(VERSION_PARAM), 
								resourceParams.getBoolean(USE_LEMMA_PARAM),
								leftSidePOS, rightSidePOS));
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
	 * @param partOfSpeech2 
	 * @param partOfSpeech 
	 * @return The list of rules leftSide -> rightSide
	 * @throws LexicalResourceException 
	 */
	private List<LexicalRule<? extends RuleInfo>> 
						getRules(LexicalResource<? extends RuleInfo> resource,
								String leftSide, String rightSide, 
								PartOfSpeech leftSidePOS, PartOfSpeech rightSidePOS) 
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
						resource.getRules(leftSide, leftSidePOS, rightSide, rightSidePOS)) {
					
					WordnetRuleInfo ruleInfo = (WordnetRuleInfo)rule.getInfo();
					
					if ((ruleInfo.getLeftSense().getWords().contains(leftSide)) &&
						(ruleInfo.getRightSense().getWords().contains(rightSide))) {
					
						addRuleToList(rules, rule);
					}
				}
				
			} else {
				
				// Get rules from t to h
				for (LexicalRule<? extends RuleInfo> rule : 
					resource.getRules(leftSide, leftSidePOS, rightSide, rightSidePOS)) {
					
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
		
		// Set the group label
		List<String> labels = new ArrayList<String>();
		String relationType = resourceName + "__" + linkInfo; 
		
		if (linkInfoToDomainLevel.containsKey(relationType)) {
			labels.add(linkInfoToDomainLevel.get(relationType));
		}
		
		if (linkInfoToInferenceLevel.containsKey(relationType)) {
			labels.add(linkInfoToInferenceLevel.get(relationType));
		}
		
		if (linkInfoToDirectionality.containsKey(relationType)) {
			labels.add(linkInfoToDirectionality.get(relationType));
		}
		
		link.setGroupLabel(createStringList(aJCas, labels));
		
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
		
		// VerbOcean
		else if (rule.getResourceName().equals("VerbOcean")) {
			type = ((VerbOceanRuleInfo)rule.getInfo()).getRelationType().name();
		}
		
		// Wikipedia
		if (rule.getResourceName().equals("Wikipedia")) {
			type = rule.getRelation();
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
				
				if (areOppositeLinks(rulesFromLeft.get(leftRuleIndex), 
									rulesFromRight.get(rightRuleIndex))) {
					
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

	/**
	 * Returns true if these two rules are opposite, meaning that:
	 * the first rule is w1->w2, with confidence c and relation r
	 * the second rule is w2->w1, with confidence c and relation r
	 * @param firstRule The first rule
	 * @param secondRule The second rule
	 * @return Whether the rules are opposite
	 */
	private boolean areOppositeLinks(
			LexicalRule<? extends RuleInfo> firstRule, 
			LexicalRule<? extends RuleInfo> secondRule) {
		
		return ((getLinkInfo(firstRule).equals(getLinkInfo(secondRule))) &&
				((Math.abs(firstRule.getConfidence() - 
						secondRule.getConfidence()) <= 0.000001)));
	}
	
	/**
	 * Create a StringList containing the strings in the collection
	 * @param aJCas
	 * @param aCollection
	 * @return
	 */
	public static StringList createStringList(JCas aJCas, Collection<String> aCollection) {
 		if (aCollection.size() == 0) {
 			return new EmptyStringList(aJCas);
 		}

 		NonEmptyStringList head = new NonEmptyStringList(aJCas);
 		NonEmptyStringList list = head;
 		Iterator<String> i = aCollection.iterator();
 		while (i.hasNext()) {
 			head.setHead(i.next());
 			if (i.hasNext()) {
 				head.setTail(new NonEmptyStringList(aJCas));
 				head = (NonEmptyStringList) head.getTail();
 			}
 			else {
 				head.setTail(new EmptyStringList(aJCas));
 			}
 		}

 		return list;
 	}
}
