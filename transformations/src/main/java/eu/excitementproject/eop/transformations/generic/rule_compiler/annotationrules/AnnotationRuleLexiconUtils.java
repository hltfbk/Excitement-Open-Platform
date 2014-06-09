package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.Constants;
import eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules.lexicon.ExpandPredicatedWithWN;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;

/**
 * Some static utils for manipulating the {@link PredicateSignature} lexicon files
 * @author Amnon Lotan
 *
 * @since Apr 11, 2012
 */
public class AnnotationRuleLexiconUtils {
	private static final String SLASH = "[/\\s]+";
	private static final String COMMA = "[,\\s]+";
	
	/**
	 * constructs a private lemma-->{@link PredicateSignature} map out of the predicate lists file
	 * @param predListPropsName
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	public static HashMap<String, PredicateSignature> loadPredicateTypePropertiesFile(String predListPropsName) throws AnnotationCompilationException {
		if (null == predListPropsName )
			throw new AnnotationCompilationException("Got null instead of predicate lists properties file name");
		File predListFile = new File(predListPropsName);
		if (!predListFile.exists())
			throw new AnnotationCompilationException("The single-word predicate lexicon file " + predListPropsName + " doesn't exist. Either you just did \n " +
					"not enter its path correctly in the configuration file, or you still need to generate it from the kernal lexicon, by running ExpandPredicatedWithWN.\n" +
					"In most cases the BIU NLP lab does not disclose the kernal version of the lexicon, which means you should only possess the expanded version,\n " +
					"whose path should be entered in the configuration file for the " + AnnotationRuleCompiler.class.getName() + " class. In case you have the kernal version\n" +
					" of the lexicon, you must first generate the expanded version by running " + ExpandPredicatedWithWN.class.getName() + " on the kernal lexicon file.\n" +
					"After that, you can run  " + AnnotationRuleCompiler.class.getSimpleName() + " with the expanded version.");
		Properties predProps = new Properties();
		try {		predProps.load(new FileInputStream(predListPropsName));
		} catch (Exception e) { throw new AnnotationCompilationException("Error loading properties file " + predListPropsName, e);}

		// iterate over the props file's keys (predicate types)and map their values (predicates) back to them
		LinkedHashMap<String, PredicateSignature> lemmaToPredTypeMap = new LinkedHashMap<String, PredicateSignature>();
		for (Object key : predProps.keySet())
		{
			String keyStr = key.toString();
			// this will fail if the predListPropsName's key names contain something not in PredicateType
			PredicateSignature predType;
			try {	predType = PredicateSignature.valueOf(keyStr);	}
			catch (Exception e) { throw new AnnotationCompilationException(keyStr + " was found in " + predListPropsName + " but isn't a PredicateType", e);}
			
			for (String predicate : splitAndCleanPredicateList(predProps.getProperty(keyStr)))
				if (lemmaToPredTypeMap.containsKey(predicate))
					throw new AnnotationCompilationException("\"" + predicate + "\" appears twice in " + predListPropsName);
				else
					lemmaToPredTypeMap.put(predicate, predType);
		}
		
		// now double check that all the PredicateTypes are in the map
		for (PredicateSignature predType : PredicateSignature.values())
			if (!lemmaToPredTypeMap.containsValue(predType))
				throw new AnnotationCompilationException(predType + " is in the PredicateType enum but isn't in " + predListPropsName);

		return lemmaToPredTypeMap;
	}
	
	/**
	 * Get the full texts of rule CGX files, check if each is a special phrasal verb rule. If one is not, return it as is.
	 * If one is, replace it with all possible instantiations of phrasal verbs (verb+noun combinations) and matching implicative signature in the given record.
	 * 
	 * This follows the phrasal verb resource in "Simple and Phrasal Implicatives, Lauri Karttunen, 2012"
	 * 
	 * @see	http://www.stanford.edu/group/csli_lnr/Lexical_Resources/phrasal-implicatives/simple-and-phrasal-implicatives.pdf
	 * @param ruleTexts
	 * @param phrasalVerbTemplates
	 * @return
	 * @throws AnnotationCompilationException
	 */
	static Set<String> expandPhrasalVerbSignatureAnnotationRule(Collection<String> ruleTexts, Set<PhrasalVerbTemplate> phrasalVerbTemplates) 
			throws AnnotationCompilationException 
	{
		Set<String> expandedRuleTexts = new LinkedHashSet<String>();
		
		for (String ruleText : ruleTexts)
		{
			if (ruleText.contains(Constants.PHRASAL_VERB_LABEL))
			{
				if (!ruleText.contains(Constants.PHRASAL_NOUN_LABEL) || 
					!ruleText.contains(Constants.PHRASAL_IMPLICATION_SIGNATURE_LABEL))
					throw new AnnotationCompilationException("This rule has the \""+Constants.PHRASAL_VERB_LABEL+
							"\" label but does not have one or two or these: \""+Constants.PHRASAL_NOUN_LABEL+
							"\", \""+Constants.PHRASAL_IMPLICATION_SIGNATURE_LABEL+"\"");
				
				for(PhrasalVerbTemplate phrasalVerbTemplate : phrasalVerbTemplates)
				{
					String predicateType = phrasalVerbTemplate.getPredicateType().name();
					String ruleTextWithPredType = ruleText.replaceAll(Constants.PHRASAL_IMPLICATION_SIGNATURE_LABEL, predicateType);
					for (String verb : phrasalVerbTemplate.getVerbs())
					{
						String ruleTextWithPredicateTypeAndVerb = ruleTextWithPredType.replaceAll(Constants.PHRASAL_VERB_LABEL, verb);
						for (String noun : phrasalVerbTemplate.getNouns())
						{
							String ruleTextWithPredicateTypeVerbAndNoun = ruleTextWithPredicateTypeAndVerb.replaceAll(Constants.PHRASAL_NOUN_LABEL, noun);
							expandedRuleTexts.add(ruleTextWithPredicateTypeVerbAndNoun);
						}
					}
				}
			}
			else	// not a phrasal verb rule, so pass it on as is
				expandedRuleTexts.add(ruleText);
		}
		
		return expandedRuleTexts;
	}

	/**
	 * cross reference the three files to make a set of all detailed phrasal verb templates, each consisting of a verb, a noun and an implication signature.<br>
	 * May return an empty set, never returns null.
	 * @param phrasalVerbFamiliesFile
	 * @param phrasalNounFamiliesFile
	 * @param phrasalImplicativeTemplatesFile
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	static Set<PhrasalVerbTemplate> loadPhrasalVerbTemplates(String phrasalVerbFamiliesFile, String phrasalNounFamiliesFile, String phrasalImplicativeTemplatesFile)
			throws AnnotationCompilationException 
	{
		// sanity and input
		if (phrasalVerbFamiliesFile == null || phrasalVerbFamiliesFile == "")
			throw new AnnotationCompilationException("got null/empty phrasalVerbFamiliesFile name" );
		Properties verbFamiliesProperties = new Properties();
		try {verbFamiliesProperties.load(new FileReader(phrasalVerbFamiliesFile));	}
		catch (Exception e) {	throw new AnnotationCompilationException("Error reading " + phrasalVerbFamiliesFile, e);	}
		
		if (phrasalNounFamiliesFile == null || phrasalNounFamiliesFile == "")
			throw new AnnotationCompilationException("got null/empty phrasalNounFamiliesFile name" );
		Properties nounFamiliesProperties = new Properties();
		try {nounFamiliesProperties.load(new FileReader(phrasalNounFamiliesFile));	}
		catch (Exception e) {	throw new AnnotationCompilationException("Error reading " + phrasalNounFamiliesFile, e);	}
		
		if (phrasalImplicativeTemplatesFile == null || phrasalImplicativeTemplatesFile == "")
			throw new AnnotationCompilationException("got null/empty phrasalImplicativeTemplatesFile name" );
		Properties templatesProperties = new Properties();
		try {templatesProperties.load(new FileReader(phrasalImplicativeTemplatesFile));	}
		catch (Exception e) {	throw new AnnotationCompilationException("Error reading " + phrasalImplicativeTemplatesFile, e);	}
		
		// marshal lists
		
		Map<String, Set<String>> verbFamiliesMap = new LinkedHashMap<String, Set<String>>();
		for (Object verbFamilyLabel: verbFamiliesProperties.keySet())
		{
			String labelStr = verbFamilyLabel.toString();
			String verbsStr = verbFamiliesProperties.getProperty(labelStr);
			Set<String> verbs = Utils.arrayToCollection(splitAndCleanPredicateList(verbsStr), new LinkedHashSet<String>()); 
			verbFamiliesMap.put(labelStr, verbs);
		}
		
		Map<String, Set<String>> nounFamiliesMap = new LinkedHashMap<String, Set<String>>();
		for (Object nounFamilyLabel: nounFamiliesProperties.keySet())
		{
			String labelStr = nounFamilyLabel.toString();
			String nounsStr = nounFamiliesProperties.getProperty(labelStr);
			Set<String> nouns = Utils.arrayToCollection(splitAndCleanPredicateList(nounsStr), new LinkedHashSet<String>()); 
			nounFamiliesMap.put(labelStr, nouns);
		}
		
		// cross ref
		
		Set<PhrasalVerbTemplate> phrasalVerbTemplates = new LinkedHashSet<PhrasalVerbTemplate>();
		for (Object tempalte : templatesProperties.keySet())
		{
			String labelStr = tempalte.toString();
			PhrasalVerbTemplate phrasalVerbTemplate = templateStringToPhrasalVerbTemplate(templatesProperties.getProperty(labelStr), verbFamiliesMap, nounFamiliesMap);
			phrasalVerbTemplates.add(phrasalVerbTemplate);
		}
			
		return phrasalVerbTemplates;
	}
	
	//////////////////////////////////////////////////////////PRIVATE //////////////////////////////////////////////////////////////////////

	/**
	 * parse/marshal the value side of one line of the phrasalImplicativeTemplatesFile.
	 * <p>
	 * it looks like this:
	 * LACK,	ABILITY/OPPORTUNITY/COURAGE, I2_FinP_InfP	
	 * @param templateStr
	 * @param verbFamiliesMap
	 * @param nounFamiliesMap
	 * @return
	 * @throws AnnotationCompilationException 
	 */
	private static PhrasalVerbTemplate templateStringToPhrasalVerbTemplate(
			String templateStr, Map<String, Set<String>> verbFamiliesMap, Map<String, Set<String>> nounFamiliesMap) throws AnnotationCompilationException 
	{
		String[] parts = splitAndCleanPredicateList(templateStr);
		if (parts.length != 3)
			throw new AnnotationCompilationException("Bad phrasal verb template: " + templateStr);
		Set<String> verbs = verbFamiliesMap.get(parts[0]);
		String[] nounLabels = splitAndCleanPredicateList(parts[1], SLASH);
		Set<String> nouns = new LinkedHashSet<String>();
		for (String nounLabel : nounLabels)
			nouns.addAll(nounFamiliesMap.get(nounLabel));
		PredicateSignature predicateType = PredicateSignature.valueOf(parts[2]);
		
		return new PhrasalVerbTemplate(verbs, nouns, predicateType);
	}

	/**
	 * @param string
	 * @param string2
	 * @return
	 */
	private static String[] splitAndCleanPredicateList(String predicatesStr, String separator) {
		String[] predicates = predicatesStr.split(separator);
		String[] trimmedPredicates = new String[predicates.length];
		for (int i = 0; i < predicates.length; i++)
			trimmedPredicates[i] = predicates[i].trim();
		return trimmedPredicates;
	}

	/**
	 * @param predicatesStr
	 * @return
	 */
	private static String[] splitAndCleanPredicateList(String predicatesStr)
	{
		return splitAndCleanPredicateList(predicatesStr, COMMA);
	}
}
