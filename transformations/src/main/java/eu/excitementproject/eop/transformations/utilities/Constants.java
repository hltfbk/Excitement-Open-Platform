package eu.excitementproject.eop.transformations.utilities;

import eu.excitementproject.eop.transformations.codeannotations.Workaround;
import eu.excitementproject.eop.transformations.operations.operations.SubstituteSubtreeOperation;
import eu.excitementproject.eop.transformations.operations.rules.distsimnew.DirtDBRuleBase;
import eu.excitementproject.eop.transformations.operations.specifications.RuleSpecification;

/**
 * All constants of the system are stored in this class.
 * Workaround flags should be defined in the nested-class {@link Workarounds}.
 *  
 * @author Asher Stern
 * 
 *
 */
public class Constants
{
	//////////////////////////////////////////////////////////////////////////
	// Important constants: constants that you might consider changing their values
	
	public static final boolean COLLAPSE_MULTIPLE_TREES_TO_SINGLE_TREE = true;
	
	/**
	 * If <tt>true</tt> then if {@link #REQUIRE_PREDICATE_TRUTH_EQUALITY} is <tt>true</tt>, then
	 * a predicate-truth of value "unknown" is counted as "not match" a predicate-truth of "positive" or "negative".
	 * If this constant is false, then mismatch is only between positive and negative, but "unknown" is counted as match
	 * either of them.<BR>
	 * If {@link #REQUIRE_PREDICATE_TRUTH_EQUALITY} is <tt>false</tt>, then this constant has no effect.
	 */
	public static final boolean REQUIRE_PREDICATE_TRUTH_MATCH_FOR_UNKNOWN = false;
	
	public static final boolean TRACE_ORIGINAL_NODES = false;

	/**
	 * When copying a sub-tree, in {@link SubstituteSubtreeOperation}, usually used
	 * in coreference-substitution transformation, this constant determines the depth
	 * of the copied-sub-tree to be copied. In other words, nodes that are deeper than
	 * the value of this constant will not be copied.
	 */
	public static final Integer DEFAULT_COPY_SUBTREE_DEPTH = new Integer(3);

	
	public static final boolean DIRT_LIKE_FILTER_BY_HYPOTHESIS_TEMPLATES = true;
	public static final boolean DIRT_LIKE_FILTER_BY_HYPOTHESIS_WORDS = false;
	
	public static final boolean DIRT_LIKE_QUERY_RULE_ALSO_BY_HYPOTHESIS_TEMPLATES = true; 


	public static final int DEFAULT_LEXICAL_RESOURCES_CACHE_SIZE = 100000;
	public static final int DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE = 100000;

	/**
	 * This feature adds periods to sentences that do not terminate with punctuation, as they are read from the dataset. 
	 */
	public static final boolean COMPLETE_PERIODS = true;
	/**
	 * apply {@link TextualNoiseReducer#reduceNoise(String)} to correct small errors in the text before preprocess
	 */
	public static final boolean REDUCE_NOISE_IN_DATASET = true;


	/**
	 * If <tt>true</tt> then all dirt-like rules have the same score, which
	 * is e^(-1), as specified in {@link DirtDBRuleBase}.CONSTANT_SCORE
	 */
	public static final boolean DIRT_LIKE_USE_CONSTANT_SCORE_FOR_ALL_RULES = true;


	public static final boolean FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES = true;
	public static final boolean FILTER_STOP_WORDS_IN_RIGHT_IN_LEXICAL_RESOURCES = true;
	@SuppressWarnings("unused")
	public static final boolean FILTER_STOP_WORDS_IN_LEXICAL_RESOURCES = FILTER_STOP_WORDS_IN_LEFT_IN_LEXICAL_RESOURCES || FILTER_STOP_WORDS_IN_RIGHT_IN_LEXICAL_RESOURCES;

	//////////////////////////////////////////////////////////////////////////
	// Constants that are less likely to be changed.


	


	/**
	 * Indicates if the system takes into account the annotations of
	 * predicate-truth (which are based on clause-truth and other annotations).
	 * If <code>true</code> then two nodes (of the text and the hypothesis) are
	 * not considered equal if one of them has negative predicate-truth and the
	 * other has positive predicate-truth. 
	 */
	public static final boolean REQUIRE_PREDICATE_TRUTH_EQUALITY = true;
	
	public static final boolean APPLY_CHANGE_ANNOTATION = REQUIRE_PREDICATE_TRUTH_EQUALITY && true;

	public static final boolean USE_ADVANCED_EQUALITIES = true;


	public static final int LEMMATIZER_CACHE_CAPACITY = 7000;

	/**
	 * Indicates whether the template-table in the data-base of DIRT-like resources
	 * will be loaded in advance to memory, such that later no queries to the data-base
	 * for that table will be executed.
	 */
	public static final boolean DIRT_LIKE_LOAD_ALL_TEMPLATES_IN_ADVANCE = true;
	
	/**
	 * Indicates whether the rules-table in the data-base of DIRT-like resources
	 * will be loaded in advance to memory, such that later no queries to the data-base
	 * for that table will be executed (it means actually that the whole rule base will
	 * be loaded to memory in advance).
	 */
	public static final boolean DIRT_LIKE_LOAD_ALL_RULES_IN_ADVANCE = false;

	/**
	 * Indicates whether the toString() method in {@link RuleSpecification}
	 * will also print the (the lemmas of) nodes which were matched to the rule's left-hand-side.
	 */
	public static final boolean PRINT_SENTENCE_PART_IN_RULE_SPECIFICATION = true;
	
	
	/**
	 * If {@link #PRINT_SENTENCE_PART_IN_RULE_SPECIFICATION} is <tt>true</tt>,
	 * then this constant indicates whether also the whole sub-trees rooted by the nodes which were
	 * matched to the rule's left-hand-side will be printed in the toString() method of
	 * {@link RuleSpecification}.
	 */
	public static final boolean WHEN_PRINT_SENTENCE_PART_IN_RULE_SPECIFICATION_INCLUDE_NON_RULE_MODIFIERS = false;



	
	
	//////////////////////////////////////////////////////////////////////////
	// Constants that there is no reason to change, now and ever.
	

	
	public static final boolean HANDLE_MULTI_WORD_NAMED_ENTITIES = true;
	public static final boolean HANDLE_LEXICAL_MULTI_WORD = true;
	public static final boolean DO_NOT_APPLY_LEXICALLY_LEXICAL_MULTI_WORD_WHEN_EASYFIRST = HANDLE_LEXICAL_MULTI_WORD;
	

	
	public static final String CACHE_DISTSIM_NAME_LHS_POSTFIX = "lhs";
	public static final String CACHE_DISTSIM_NAME_RULES_POSTFIX = "rules";
	
	//////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * A nested class with constants for workarounds only.
	 * 
	 * <P>
	 * How to write a workaround:
	 * <OL>
	 * <LI>The method with the work-around code should be annotated with {@link Workaround}</LI>
	 * <LI>A constant-flag that decides whether the workaround will be run, or not (which
	 * means that the bug will make its damages) should be defined in {@link Workarounds} (nested class of {@link Constants})</LI>
	 * <LI>A warning (or error) should be printed by the logger (logger.warn("..."))</LI>
	 * </OL>
	 * 
	 * 
	 * 
	 * @author Asher Stern
	 * @since Mar 23, 2012
	 *
	 */
	public static final class Workarounds
	{
		public static final boolean LIN_REUTERS_USE_CONSTANT_SCORE = true;
	}
	
}
