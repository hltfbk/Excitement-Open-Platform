package eu.excitementproject.eop.core.alignment;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.berkeley.nlp.lm.util.Logger;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Link.Direction;
import eu.excitement.type.alignment.Target;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
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
import eu.excitementproject.eop.core.component.lexicalknowledge.verb_ocean.VerbOceanLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.distsim.resource.SimilarityStorageBasedLexicalResource;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;


/**
 * Produces alignment links between the text and the hypothesis,
 * based on lexical rules: if T contains a 
 * <P>
 * Usage: First call {@link #init()} method, than start aligning
 * any sentences pair.
 * When the {@linkplain Aligner} object is no longer to be used, the
 * {@link #cleanUp()} method should be called.
 * <P>
 * To align sentences: call {@link #align(String, String)} method.
 * <P>
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
	private static final String REDIS_BAP = "distsim-bap";
	private static final String REDIS_LIN_PROXIMITY = "redis-lin-proximity";
	private static final String REDIS_LIN_DEPENDENCY = "redis-lin-dependency";
	private static final String VERB_OCEAN = "verb-ocean";
	
	// Private Members
	private List<Token> textTokens;
	private List<Token> hypoTokens;
	private List<LexicalResource<? extends RuleInfo>> lexicalResources;
	private int maxPhrase = 0;
	private static List<String> lexicalResourceThatSupportLemma;
	
	// Initialization
	static {
		
		// These resources should be queried with lemmas and not surface words.
		lexicalResourceThatSupportLemma = new ArrayList<String>();
		lexicalResourceThatSupportLemma.add(VerbOceanLexicalResource.class.getName());
	}
	
	// Public Methods
	
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
	 * @throws AlignerRunException 
	 */
	@Override
	public void annotate(JCas aJCas) {
		
		try {

			// Get the tokens of the text and hypothesis
			getTokensAnnotations(aJCas);
			
			// Get the lemmas
			
			
			// Check in all the resources for rules of type textPhrase -> hypoPhrase 
			for (LexicalResource<? extends RuleInfo> resource : lexicalResources) {
				
				// For every phrase t in T and phrase h in H, check the lexical
				// resources if they contain a rule t->h
				String textPhrase = "", hypoPhrase = "";
				
				for (int textStart = 0; textStart < textTokens.size(); ++textStart) {
					for (int textEnd = textStart; textEnd < Math.min(textTokens.size(), 
							textStart + maxPhrase); ++textEnd) {
						
						textPhrase = getPhrase(textTokens, textStart, textEnd, 
								lexicalResourceThatSupportLemma.contains(resource.getClass().getName()));
						
						for (int hypoStart = 0; hypoStart < hypoTokens.size(); ++hypoStart) {
							for (int hypoEnd = hypoStart; hypoEnd < Math.min(hypoTokens.size(), 
									hypoStart + maxPhrase); ++hypoEnd) {
								
								hypoPhrase = getPhrase(hypoTokens, hypoStart, hypoEnd,
										lexicalResourceThatSupportLemma.contains(resource.getClass().getName()));
								
								// Get the rules textPhrase -> hypoPhrase
								for (LexicalRule<? extends RuleInfo> rule : 
										getRules(resource, textPhrase, hypoPhrase)) {
									
									// Add alignment annotations
									addAlignmentAnnotations(aJCas, textStart, textEnd, 
											hypoStart, hypoEnd, rule);
								}
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			
			// TODO: Write to log / raise exception
			System.out.println(this.getClass().getName() + 
					"LexicalAligner failed aligning the sentence pair. " + 
					e.getMessage());
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
	 * Call this method once before starting to align sentence pairs.
	 * @param config a CommonConfig instance. The aligner retrieves the lexical 
	 * resources configuration values. 
	 * @throws AlignerRunException if initialization failed
	 * @throws ConfigurationException if the configuration is invalid
	 */
	public void init(CommonConfig config) throws AlignerRunException, ConfigurationException {
		
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
		
		try {
			
			ConfigurationFile configFile = new ConfigurationFile(config);
			
			// Get each resource and create it using the configuration section related to it
			for (String resourceName : lexicalResourcesSection.keySet()) {
				lexicalResources.add(createLexicalResource(resourceName, 
						configFile.getModuleConfiguration(resourceName)));
			}
	
		} catch (LexicalResourceException e) {
			throw new AlignerRunException("Could not load resources", e);
		}
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
				// TODO: Write to log
				e.printStackTrace();
			}
		}
	}
	
	// Private Methods
	
	/**
	 * Uses the annotations in the CAS and extracts the tokens of the
	 * text and hypothesis.
	 * @param aJCas The JCas object of the text and hypothesis, after tokenization.
	 * @throws CASException
	 */
	private void getTokensAnnotations(JCas aJCas) throws CASException {
		
		// Get the text and hypothesis views
		JCas textView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
		JCas hypoView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
			
		// Get the text and hypothesis tokens
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
		
		for (Token token : tokens.subList(start, end + 1)) {		
			phrase.append(supportLemma ? token.getLemma().getValue() : 
				token.getCoveredText());
			phrase.append(" ");
		}
		
		// Remove last space
		if (phrase.length() > 0) {
			phrase.deleteCharAt(phrase.length() - 1);
		}
		
		return phrase.toString();
	}

	/**
	 * Get rules of type textPhrase -> hypoPhrase, using the lexical resource given
	 * @param resource The lexical resource to use
	 * @param textPhrase The text phrase, will be looked for as lhs of a rule
	 * @param hypoPhrase The hypothesis phrase, will be looked for as rhs of a rule
	 * @return The list of rules textPhrase -> hypoPhrase
	 * @throws LexicalResourceException 
	 */
	private List<LexicalRule<? extends RuleInfo>> 
						getRules(LexicalResource<? extends RuleInfo> resource,
								String textPhrase, String hypoPhrase) 
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
						resource.getRules(textPhrase, null, hypoPhrase, null)) {
					
					WordnetRuleInfo ruleInfo = (WordnetRuleInfo)rule.getInfo();
					
					if ((ruleInfo.getLeftSense().getWords().contains(textPhrase)) &&
						(ruleInfo.getRightSense().getWords().contains(hypoPhrase))) {
					
						rules.add(rule);
					}
				}
				
			} else {
				rules.addAll(resource.getRules(textPhrase, null, hypoPhrase, null));
			}
				
		} catch (Exception e) {
			Logger.warn("Could not add rules from " + 
						resource.getClass().getSimpleName() + " for " +
						textPhrase + "->" + hypoPhrase, e);
		}
		
		return rules;
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
	 * @param rule The rule t->h
	 * @throws CASException 
	 */
	private void addAlignmentAnnotations(JCas aJCas, 	int textStart, int textEnd, 
														int hypoStart, int hypoEnd, 
														LexicalRule<? extends RuleInfo> rule) 
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
		for (Token token : textTokens.subList(textStart, textEnd)) {
			textAnnots.set(tokenIndex++, token);
		}
		
		tokenIndex = 0;
		for (Token token : hypoTokens.subList(hypoStart, hypoEnd)) {
			hypoAnnots.set(tokenIndex++, token);
		}
		
		textTarget.setTargetAnnotations(textAnnots);
		hypoTarget.setTargetAnnotations(hypoAnnots);
		
		// Set begin and end value of the Target annotations
		textTarget.setBegin(textStart);
		textTarget.setEnd(textEnd);
		hypoTarget.setBegin(hypoStart);
		hypoTarget.setEnd(hypoEnd);
		
		// Add the targets to the indices 
		textTarget.addToIndexes(); 
		hypoTarget.addToIndexes();
		
		// Mark an alignment.Link and add it to the hypothesis view
		Link link = new Link(hypoView); 
		link.setTSideTarget(textTarget); 
		link.setHSideTarget(hypoTarget); 
		
		// TODO: Get the direction according to the resource
		// and maybe set it as Bi-directional if h->t by the resource 
		link.setDirection(Direction.TtoH); 
		
		// Set strength according to the rule data
		link.setStrength(rule.getConfidence()); 
		
		// Add the link information
		link.setAlignerID(rule.getResourceName());  
		
		// TODO: Set these fields differently?
		link.setAlignerVersion("1.0"); 
		link.setLinkInfo("local-entailment");
		
		// Mark begin and end according to the hypothesis target
		link.setBegin(hypoTarget.getBegin()); 
		link.setEnd(hypoTarget.getBegin());
		
		// Add to index 
		link.addToIndexes(); 
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
	public LexicalResource<? extends RuleInfo> 
		createLexicalResource(String resourceClassName, 
				ConfigurationParams configurationParams) 
				throws ConfigurationException, LexicalResourceException
	{
		LexicalResource<? extends RuleInfo> lexicalResource = null;
		
		// TODO: Implement all resources
		switch (resourceClassName) {
//			case BAP:
//				ret = new Direct200LexicalResource(params);
//				break;
//			case CATVAR:
//				ret = new CatvarLexicalResource(params);
//				break;
		
			// VerbOcean
			case VERB_OCEAN: {
				lexicalResource = new VerbOceanLexicalResource(configurationParams);
				break;
			}
			
//			case WIKIPEDIA:
//				ret = new WikiLexicalResource(params);
//				break;
//			case WIKIPEDIA_NEW:
//				ret = new  eu.excitementproject.eop.lexicalminer.LexiclRulesRetrieval.WikipediaLexicalResource(params);
//				break;
				
			// WordNet
			case WORDNET: {
				lexicalResource = new WordnetLexicalResource(configurationParams);
				break;
			}
//			case LIN_DEPENDENCY_ORIGINAL:
//				ret = new LinDependencyOriginalLexicalResource(params);
//				break;
//			case LIN_PROXIMITY_ORIGINAL:
//				ret = new LinProximityOriginalLexicalResource(params);
//				break;
//			case LIN_DEPENDENCY_REUTERS:
//				ret = new LinDistsimLexicalResource(params);
//				break;
			
			// Redis similarity-based resources
			case REDIS_LIN_PROXIMITY:
			case REDIS_LIN_DEPENDENCY:
			case REDIS_BAP: {
				try {
					lexicalResource = new 
							SimilarityStorageBasedLexicalResource(configurationParams);
				} catch (Exception e) {
					throw new LexicalResourceException(e.toString());
				}
				break;
			}
			
//			case REDIS_WIKI:
//				try {
//					ret = new RedisBasedWikipediaLexicalResource(params);
//				} catch (Exception e) {
//					throw new LexicalResourceException(e.toString());
//				}
//				break;
//			case REDIS_GEO:
//				try {
//					ret = new RedisBasedGeoLexicalResource(params);
//				} catch (Exception e) {
//					throw new LexicalResourceException(e.toString());
//				}
//				break;								
			default: {
				lexicalResource = null;
				break;
			}
		}

		return lexicalResource;
	}
}
