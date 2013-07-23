package eu.excitementproject.eop.biutee.utilities;
import eu.excitementproject.eop.transformations.builtin_knowledge.KnowledgeResource;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.ConfigurationModuleAnnotation;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.ConfigurationParameterAnnotation;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.MandatoryLevel;

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
	public static final String RTE_SERIALIZED_DATASET_FOR_TRAINING = "serialized_training_data";
	@ConfigurationParameterAnnotation({RTE_PAIRS_TRAIN_AND_TEST_MODULE_NAME,RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTE_SERIALIZED_DATASET_FOR_TEST = "serialized_test_data";
	@ConfigurationParameterAnnotation({RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTESUM_DATASET_FOR_TRAINING = "training_data";
	@ConfigurationParameterAnnotation({RTE_SUM_TRAIN_AND_TEST_MODULE_NAME})
	public static final String RTESUM_DATASET_FOR_TEST = "test_data";
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

	@Deprecated
	public static final String NORMALIZER_MODULE_NAME = "Normalizer";
}
