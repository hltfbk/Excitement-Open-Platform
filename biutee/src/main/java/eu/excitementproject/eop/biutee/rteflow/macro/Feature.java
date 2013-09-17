package eu.excitementproject.eop.biutee.rteflow.macro;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains the "built-in" features in the system, i.e. features that are
 * not for rule-applications, not for plug-ins and not "dynamic" (like the indicators
 * of data-set type, which are "dynamic" features).
 * <P>
 * Mostly the features in the enumeration are used for on-the-fly transformations. 
 * 
 * @author Asher Stern
 * 
 *
 */
public enum Feature
{
// If you want to uncomment those lines, the method initialFeatureVector in TextTreesProcessor should
// be restored as well.
	HYPOTHESIS_MAIN_PREDICATE_IS_VERB(true),
//	HYPOTHESIS_MAIN_PREDICATE_IS_NOT_VERB(true),
	INVERSE_HYPOTHESIS_LENGTH(true),
	TASK_IR(true,"IR"),
	TASK_IE(true,"IE"),
	TASK_QA(true,"QA"),
	TASK_SUM(true,"SUM"),
	INSERT_NAMED_ENTITY(false),
//	INSERT_NUMBER(false),
	INSERT_CONTENT_VERB(false),
	INSERT_CONTENT_WORD(false),
	INSERT_NON_CONTENT_NON_EMPTY_WORD(false),
	INSERT_EMPTY_WORD(false),
	INSERT_NAMED_ENTITY_EXIST_IN_PAIR(false),
//	INSERT_NUMBER_EXIST_IN_PAIR(false),
	INSERT_CONTENT_VERB_EXIST_IN_PAIR(false),
	INSERT_CONTENT_WORD_EXIST_IN_PAIR(false),
	INSERT_NON_CONTENT_NON_EMPTY_WORD_EXIST_IN_PAIR(false),
//	MOVE_CROSS_CONTENT_VERB(false),
	MOVE_ONLY_CHANGE_RELATION_STRONG(false),
//	MOVE_ONLY_CHANGE_RELATION_WEAK(false),
//	MOVE_IN_VERB_TREE_CHANGE_RELATION_STRONG(false),
	MOVE_INTRODUCE_SURFACE_RELATION(false),
//	MOVE_CONNECT_TO_EMPTY_NODE(false),
	MOVE_NODE_CHANGE_CONTEXT(false),
	MOVE_NODE_SAME_CONTEXT(false),
	MOVE_TO_ROOT_IF_PT_PLUS_OR_MINUS(false),
	SUBSTITUTION_MULTI_WORD_ADD_WORDS(false),
	SUBSTITUTION_MULTI_WORD_ADD_WORDS_NAMED_ENTITY(false),
	SUBSTITUTION_MULTI_WORD_REMOVE_WORDS(false),
	SUBSTITUTION_FLIP_POS(false),
	SUBSTITUTION_PARSER_ANTECEDENT(false),
	SUBSTITUTION_COREFERENCE(false),
	IS_A_COREFERENCE(false),
	CHANGE_PREDICATE_TRUTH(false),
	
	GAP_V1_COUNT_MISSING_NODES(false,true),
	
	GAP_V2_MISSING_PREDICATES(false,true),
	GAP_V2_ARGUMENT_HEAD_NOT_CONNECTED(false,true),
	GAP_V2_ARGUMENT_HEAD_MISSING(false,true),
	GAP_V2_ARGUMENT_NODE_NOT_CONNECTED(false,true),
	GAP_V2_ARGUMENT_NODE_MISSING(false,true),
	
	/**
	 * Arguments in the hypothesis that have no matching argument in the text.
	 */
	GAP_V3_MISSING_ARGUMENT(false,true),
	
	/**
	 * Like GAP_V3_MISSING_ARGUMENT, but the argument is a named-entity.
	 */
	GAP_V3_MISSING_NAMED_ENTITIES(false,true),
	
	/**
	 * Hypothesis arguments which have corresponding arguments in the text, but:
	 * <UL>
	 * <LI>the text's corresponding arguments are connected to other predicates than
	 * the corresponding predicates in the hypothesis</LI>
	 * <LI>the hypothesis arguments have some content words that do not
	 * exist in the text arguments</LI>
	 * </UL>
	 * 
	 */
	GAP_V3_WRONG_PREDICATE_MISSING_WORDS(false,true),

	/**
	 * Hypothesis arguments which have corresponding arguments in the text, but
	 * the text's corresponding arguments are connected to other predicates than
	 * the corresponding predicates in the hypothesis.
	 */
	GAP_V3_WRONG_PREDICATE(false,true),
	
	/**
	 * Hypothesis arguments which have corresponding arguments in the text, but
	 * the hypothesis arguments have some content words that do not
	 * exist in the text arguments.
	 */
	GAP_V3_MISSING_WORDS(false,true),
	
	/**
	 * Content words in the hypothesis, which are not predicates, and do not exist in the text at all
	 */
	GAP_V3_MISSING_WORDS_TOTALLY_NON_PREDICATES(false,true),

	/**
	 * Content words in the hypothesis, which are also predicates, and do not exist in the text at all
	 */
	GAP_V3_MISSING_WORDS_TOTALLY_PREDICATES(false,true),
	
	GAP_V3_PREDICATE_NO_MATCH(false,true),
	
	GAP_BASELINE_V1_MISSING_NODE(false,true),
	GAP_BASELINE_V1_MISSING_NODE_NON_CONTENT_WORD(false,true),
	GAP_BASELINE_V1_MISSING_NODE_NAMED_ENTITY(false,true),
	GAP_BASELINE_V1_MISSING_EDGE(false,true),
	
	GAP_BASELINE_V2_MISSING_LEMMA(false,true),
	GAP_BASELINE_V2_MISSING_NODE(false,true),
	GAP_BASELINE_V2_MISSING_RELATION(false,true)
	;

	//////////////////// PUBLIC METHODS AND PRIVATE CONSTRUCTORS //////////////////////////
	/**
	 * Global indicates that the feature is not a feature of a generation-operation (a transformation),
	 * but it is feature about the text-hypothesis pair (regardless any operations done on them)
	 * @param global
	 */
	private Feature(boolean global)
	{
		this(global,null,false);
	}

	private Feature(boolean global, boolean gapFeature)
	{
		this(global,null,gapFeature);
	}
	
	private Feature(boolean global, String taskName)
	{
		this(global,taskName,false);
	}
	
	private Feature(boolean global, String taskName, boolean gapFeature)
	{
		this.global=global;
		this.taskName=taskName;
		this.gapFeature = gapFeature;
	}
	


	/**
	 * Global indicates that the feature is not a feature of a generation-operation,
	 * but it is feature about the text-hypothesis pair (regardless any operations done on them)
	 * 
	 * @return <tt>true</tt> if the feature is global. <tt>false</tt> otherwise. 
	 */
	public boolean isGlobal()
	{
		return global;
	}
	
	public String getTaskName()
	{
		return this.taskName;
	}

	public boolean isGapFeature()
	{
		return gapFeature;
	}

	public static Set<Feature> getGlobalFeatures()
	{
		return getFeatures(true);
	}

	public static Set<Feature> getNonGlobalFeatures()
	{
		return getFeatures(false);
	}


	public int getFeatureIndex()
	{
		return this.ordinal()+1;
	}
	public static int largestFeatureIndex()
	{
		return values().length;
	}

	
	public static Map<Integer, String> toMapOfNames()
	{
		Map<Integer, String> ret = new LinkedHashMap<Integer, String>();
		
		for (Feature feature : Feature.values())
		{
			ret.put(feature.getFeatureIndex(), feature.name());
		}
		
		return ret;
	}
	
	public static LinkedHashSet<String> getAllFeaturesNames()
	{
		LinkedHashSet<String> ret = new LinkedHashSet<String>();
		for (Feature feature : values())
		{
			ret.add(feature.name());
		}
		return ret;
	}

	public static Set<Feature> getGapFeatures()
	{
		Set<Feature> ret = new LinkedHashSet<Feature>();
		for (Feature feature : Feature.values())
		{
			if (feature.isGapFeature())
			{
				ret.add(feature);
			}
		}
		return ret;
	}

	
	///////////////////////////////// PRIVATE //////////////////////////////////////////////

	private static Set<Feature> getFeatures(boolean globalValue)
	{
		Set<Feature> ret = new LinkedHashSet<Feature>();
		for (Feature feature : Feature.values())
		{
			if (feature.isGlobal()==globalValue)
				ret.add(feature);
		}
		return ret;
	}

	private final boolean global;
	private final String taskName;
	private final boolean gapFeature;
}
