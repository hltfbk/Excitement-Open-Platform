package ac.biu.nlp.nlp.engineml.rteflow.systems;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ac.biu.nlp.nlp.engineml.builtin_knowledge.KnowledgeResource;

/**
 * 
 * Constants required to read the configuration file. Those constants are
 * modules' parameters' names.
 * 
 * 
 * More module names can be found in the enum {@link KnowledgeResource}
 * 
 * @author Asher Stern
 * 
 *
 */
public class ConfigurationParametersNames
{
	public static enum MandatoryLevel
	{
		MANDATORY,OPTIONAL,IGNORE;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationModuleAnnotation
	{
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationParameterAnnotation
	{
		/**
		 * Indicates the module for which this parameter belongs to.
		 * @return
		 */
		public String[] value();
		public MandatoryLevel mandatoryLevel() default MandatoryLevel.MANDATORY;
	}

	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationDirtParameterAnnotation
	{
		public MandatoryLevel mandatoryLevel() default MandatoryLevel.MANDATORY;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationTruthTellerParameterAnnotation
	{
		public MandatoryLevel mandatoryLevel() default MandatoryLevel.MANDATORY;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationKnowledgeParameterAnnotation
	{
		public KnowledgeResource[] knowledgeResources();
		public MandatoryLevel mandatoryLevel() default MandatoryLevel.MANDATORY;
	}

	
	// pre-process modules
	@ConfigurationModuleAnnotation
	public static final String RTE_PAIRS_PREPROCESS_MODULE_NAME = "rte_pairs_preprocess";
	@ConfigurationParameterAnnotation(RTE_PAIRS_PREPROCESS_MODULE_NAME)
	public static final String RTE_PAIRS_PREPROCESS_DATASET_FILE_NAME = "dataset";
	@ConfigurationParameterAnnotation(RTE_PAIRS_PREPROCESS_MODULE_NAME)
	public static final String RTE_PAIRS_PREPROCESS_ANNOTATED = "annotated";
	@ConfigurationParameterAnnotation(RTE_PAIRS_PREPROCESS_MODULE_NAME)
	public static final String RTE_PAIRS_PREPROCESS_SERIALIZATION_FILE_NAME = "serialization_filename";
	
	@ConfigurationModuleAnnotation
	public static final String RTE_SUM_PREPROCESS_MODULE_NAME = "rte_sum_preprocess";
	@ConfigurationParameterAnnotation(RTE_SUM_PREPROCESS_MODULE_NAME)
	public static final String RTE_SUM_DATASET_DIR_NAME = "dataset";
	@ConfigurationParameterAnnotation(RTE_SUM_PREPROCESS_MODULE_NAME)
	public static final String RTE_SUM_PREPROCESS_SERIALIZATION_FILE_NAME = "serialization_filename";

	@ConfigurationParameterAnnotation(value={RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String PREPROCESS_MINIPAR = "minipar";
	@ConfigurationParameterAnnotation({RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME})
	public static final String PREPROCESS_EASYFIRST = "easyfirst_stanford_pos_tagger";
	@ConfigurationParameterAnnotation({RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME})
	public static final String PREPROCESS_EASYFIRST_HOST = "easyfirst_host";
	@ConfigurationParameterAnnotation({RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME})
	public static final String PREPROCESS_EASYFIRST_PORT = "easyfirst_port";
	@ConfigurationParameterAnnotation(value={RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String PREPROCESS_BART_SERVER = "bart_server";
	@ConfigurationParameterAnnotation(value={RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String PREPROCESS_BART_PORT = "bart_port";
	@ConfigurationParameterAnnotation({RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME})
	public static final String PREPROCESS_STANFORD_NE_CLASSIFIER_PATH = "stanford_ner_classifier_path";
	@ConfigurationParameterAnnotation({RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME})
	public static final String PREPROCESS_NEW_NORMALIZER_FILE = "new_normalizer";
	@ConfigurationParameterAnnotation(value={RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String PREPROCESS_DO_NER = "do_named_entity_recognition";
	@ConfigurationParameterAnnotation(value={RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String PREPROCESS_DO_TEXT_NORMALIZATION = "do_text_normalization";
	@ConfigurationParameterAnnotation({RTE_PAIRS_PREPROCESS_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME})
	public static final String PREPROCESS_COREFERENCE_RESOLUTION_ENGINE = "coreferencer";

	
	// Run RTE modules
	@ConfigurationModuleAnnotation
	public static final String RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME = "rte_pairs_train_and_test";
	@ConfigurationModuleAnnotation
	public static final String RTE_SUM_TRAIN_AND_TEST_MODULE_NAME = "rte_sum_train_and_test";

	// Used both for preprocess & run
	@ConfigurationParameterAnnotation({RTE_SUM_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_PREPROCESS_MODULE_NAME})
	public static final String RTE_SUM_IS_NOVELTY_TASK_FLAG = "is_novelty_task";

	@ConfigurationParameterAnnotation({RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTE_ENGINE_GATE_LEMMATIZER_RULES_FILE = "lemmatizer_rule_file";
	@ConfigurationParameterAnnotation(value={RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String RTE_ENGINE_PARSER_PARAMETER_NAME = "parser";
	@ConfigurationParameterAnnotation({RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTE_ENGINE_NUMBER_OF_THREADS_PARAMETER_NAME = "threads";
	@ConfigurationParameterAnnotation({RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTE_ENGINE_UNIGRAM_LIDSTON_SER_FILE = "unigram_lidston_ser_file";

	@ConfigurationParameterAnnotation(value={RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String RTE_TRAIN_SERIALIZED_SAMPLES_BASE_PATH = "save_serialized_samples";
	@ConfigurationParameterAnnotation({RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTE_TEST_SERIALIZED_SAMPLES_NAME = "serialized_samples";
	@ConfigurationParameterAnnotation({RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTE_TEST_SAMPLES_FOR_SEARCH_CLASSIFIER = "search_classifier_samples";
	@ConfigurationParameterAnnotation({RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTE_TEST_PREDICTIONS_MODEL = "predictions_model";
	@ConfigurationParameterAnnotation({RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTE_TEST_SEARCH_MODEL = "search_model";
	@ConfigurationParameterAnnotation(value={RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String RTE_TEST_SEARCH_CLASSIFIER_REASONABLE_GUESS = "search_classifier_is_reasonable_guess";
	@ConfigurationParameterAnnotation(value={RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME},mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String RTE_PAIRS_GUI_CLASSIFIER_FOR_PREDICTIONS_IS_DUMMY = "dummy_prediction_classifier";
	
	// Knowledge resources module
	@ConfigurationModuleAnnotation
	public static final String KNOWLEDGE_RESOURCES_MODULE_NAME = "transformations";
	@ConfigurationParameterAnnotation(KNOWLEDGE_RESOURCES_MODULE_NAME)
	public static final String KNOWLEDGE_RESOURCES_PARAMETER_NAME = "knowledge_resources";
	@ConfigurationParameterAnnotation(KNOWLEDGE_RESOURCES_MODULE_NAME)
	public static final String LEXICAL_RESOURCES_RETRIEVE_MULTIWORDS_PARAMETER_NAME = "multiword_resources";
	@ConfigurationParameterAnnotation(value=KNOWLEDGE_RESOURCES_MODULE_NAME,mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String PLUGIN_REGISTERER_PARAMETER_NAME="plugin_registerer";
	@ConfigurationParameterAnnotation(value=KNOWLEDGE_RESOURCES_MODULE_NAME,mandatoryLevel=MandatoryLevel.OPTIONAL)
	public static final String PLUGINS_TO_APPLY = "plugins_to_apply";
	@ConfigurationParameterAnnotation(KNOWLEDGE_RESOURCES_MODULE_NAME)
	public static final String RTE_TRAIN_AND_TEST_STOP_WORDS = "stop_words";

	// Parameters for specific knowledge resources
	public static final String MANUAL_FILE_RULEBASE_FILE_PARAMETER_NAME = "file";
	public static final String MANUAL_FILE_RULEBASE_DYNAMIC_PARAMETER_NAME = "dynamic";
	public static final String MANUAL_FILE_RULEBASE_USE_PARAMETER_NAME = "use";
	
	@ConfigurationDirtParameterAnnotation
	public static final String DB_DRIVER = "database_driver";
	@ConfigurationDirtParameterAnnotation
	public static final String DB_URL = "database_url";
	@ConfigurationDirtParameterAnnotation
	public static final String TEMPLATES_TABLE_NAME = "templates_table";
	@ConfigurationDirtParameterAnnotation
	public static final String RULES_TABLE_NAME = "rules_table";
	@ConfigurationDirtParameterAnnotation
	public static final String LIMIT_NUMBER_OF_RULES = "limit_number_of_rules";
	@ConfigurationDirtParameterAnnotation(mandatoryLevel=MandatoryLevel.IGNORE)
	public static final String DIRT_LIKE_SER_FILE_PARAMETER_NAME = "serialization_file";
	
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.SYNTACTIC)
	public static final String SYNTACTIC_RULES_FILE = "syntactic_rules_file";

	//
	// TruthTeller
	//
	@ConfigurationModuleAnnotation
	public static final String TRUTH_TELLER_MODULE_NAME = "TruthTeller";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String ANNOTATION_RULES_FILE = "annotation_rules_file";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String USER_REQUIRES_ANNOTATIONS = "do_annotations";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String CONLL_FORMAT_OUTPUT_DIRECTORY = "conll_format_output_directory";
	
	// simple-lexical-chain
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.SIMPLE_LEXICAL_CHAIN)
	public static final String SIMPLE_LEXICAL_CHAIN_DEPTH_PARAMETER_NAME = "depth";
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.SIMPLE_LEXICAL_CHAIN)
	public static final String SIMPLE_LEXICAL_CHAIN_KNOWLEDGE_RESOURCES = "knowledge_resources";
	
	
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.GEO)
	public static final String CONNECTION_STRING_GEO_PARAMETER_NAME = "connection";
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.GEO)
	public static final String TABLE_NAME_PARAMETER_NAME = "table";
	
	
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.LIN_DEPENDENCY_REUTERS)
	public static final String CONNECTION_STRING_LIN_REUTERS_PARAMETER_NAME = "database_url";
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.LIN_DEPENDENCY_REUTERS)
	public static final String LIMIT_LIN_REUTERS_PARAMETER_NAME = "limit on retrieved rules";
	
	@Deprecated
	public static final String NORMALIZER_MODULE_NAME = "Normalizer";
}
