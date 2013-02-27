/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;

/**
 * Names of {@link ConfigurationFile} parameters used for {@link SyntacticRule} and {@link AnnotationRule} compilation
 * @author Amnon Lotan
 *
 * @since Jun 14, 2012
 */
public class RuleCompilerParameterNames {
	public static final String SYNTACTIC_PARAMS_MODULE = "Syntactic";
	public static final String RULE_COMPILER_PARAMS_MODULE = "RuleCompilation";
	/**
	 * inside the {@link #RULE_COMPILER_PARAMS_MODULE} 
	 */
	public static final String RULE_ABBREVIATIONS_MODULE = "abbreviations";
	
	/**
	 * the expanded single word predicate lexicon, used by AnnotationRuleCompiler - is a properties file
	 */
	public static final String SINGLE_WORD_PREDICATE_TYPE_LEXICON = "predicateLexiconFile";
	public static final String PHRASAL_VERB_FAMILIES_FILE = "phrasalVerbFamiliesFile";
	public static final String PHRASAL_NOUN_FAMILIES_FILE = "phrasalNounFamiliesFile";
	public static final String PHRASAL_IMPLICATIVE_TEMPLATES_FILE = "phrasalImplicativeTemplatesFile";
	
	public static final String ENTAILMENT_RULES_DIRECTORY = "directoryName";
	public static final String ANNOTATION_RUELS_DIRECTORY = "annotation_rules_directory";
	public static final String RECURSIVE_CT_RUELS_DIRECTORY = "recursive_ct_rules_directory";
	public static final String RULE_FILE_SUFFIX = "rule_file_suffix";
	public static final String CONLL_RULES_DIRECTORY = "conll_format_directory";
	
	/**
	 * the kernal lexicon of cherry picked single word signature lexicon, used by ExpandPredicatedWithWN to create the expanded predicateLexiconFile
	 */
	public static final String KERNAL_SINGLE_WORD_LEXICON = "kernal_single_word_lexicon";
	/**
	 * wordnet dictionary folder used by ExpandPredicatedWithWN
	 */
	public static final String WORDNET_DICT = "wordnet_dict";
}
