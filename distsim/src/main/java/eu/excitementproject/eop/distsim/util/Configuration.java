package eu.excitementproject.eop.distsim.util;

/**
 * Defines the names of the configuration modules and features 
 * 
 * @author Meni Adler
 * @since 28/12/2012
 *
 */
public class Configuration {

	public static final String LOGGING = "logging";
	public static final String PROPERTIES_FILE = "properties-file";
	public static final String CO_OCCURRENCE_EXTRACTOR = "cooccurence-extractor";
	public static final String ELEMENT_FEATURE_EXTRACTOR = "element-feature-extractor";
	public static final String THREAD_NUM = "thread-num";
	public static final String CLASS = "class";
	public static final String EXTRACTOR_CLASS = "extractor-class";
	public static final String EXTRACTION_CLASS = "extraction-class";
	public static final String CORPUS = "corpus";
	public static final String TEXT_UNITS_DATA_STRUCTURE = "text-units-data-structure";
	public static final String CO_OCCURRENCES_DATA_STRUCTURE = "co-occurrences-data-structure";
	public static final String ELEMENTS_DATA_STRUCTURE = "elements-data-structure";
	public static final String FEATURES_DATA_STRUCTURE = "features-data-structure";
	public static final String ELEMENT_FEATURE_COUNTS_DATA_STRUCTURE = "element-feature-counts-data-structure";
	public static final String FEATURE_ELEMENTS_DATA_STRUCTURE = "feature-elements-data-structure";
	public static final String ELEMENT_FEATURE_SCORES_DATA_STRUCTURE = "element-feature-scores-data-structure";
	public static final String ELEMENT_SCORES_DATA_STRUCTURE = "element-scores-data-structure";
	public static final String PRED_ARG_EXTRACTION = "pred-arg-extraction";
	public static final String SLOT = "slot";
	public static final String ELEMENT_FEATURE_SCORING = "element-feature-scoring";
	public static final String FEATURE_SCORING_CLASS = "feature-scoring-class";
	public static final String ELEMENT_SCORING_CLASS = "element-scoring-class";
	public static final String ELEMENT_SIMILARITY_CLACULATOR = "element-similarity-calculator";
	public static final String SIMILARITY_SCORING_CLASS = "similarity-scoring-class";
	public static final String TEXT_UNITS_STORAGE_DEVICE = "text-units-storage-device";
	public static final String CO_OCCURENCES_STORAGE_DEVICE = "co-occurrences-storage-device";
	public static final String ELEMENTS_STORAGE_DEVICE = "elements-storage-device";
	public static final String FEATURES_STORAGE_DEVICE = "features-storage-device";
	public static final String ELEMENT_FEATURE_COUNTS_STORAGE_DEVICE = "element-feature-counts-storage-device";
	public static final String FEATURE_ELEMENTS_STORAGE_DEVICE = "feature-elements-storage-device";
	public static final String TRUNCATED_FEATURE_ELEMENTS_STORAGE_DEVICE = "truncated-feature-elements-storage-device";
	public static final String ELEMENT_FEATURE_SCORES_STORAGE_DEVICE = "element-feature-scores-storage-device";
	public static final String ELEMENT_SCORES_STORAGE_DEVICE = "element-scores-storage-device";
	public static final String ELEMENTS_SIMILARITIES_R2L_STORAGE_DEVICE = "elements-similarities-r2l-storage-device";
	public static final String ELEMENTS_SIMILARITIES_L2R_STORAGE_DEVICE = "elements-similarities-l2r-storage-device";
	public static final String FILE = "file";
	public static final String READ_WRITE = "read-write";
	public static final String REDIS_HOST = "redis-host";
	public static final String REDIS_PORT = "redis-port";
	public static final String STOP_WORDS_FILE = "stop-words-file";
	public static final String VECTOR_TRUNCATE = "vector-truncate";
	public static final String COMMON_FEATURE_CRITERION = "common-feature-criterion";
	public static final String PERCENT = "percent";
	public static final String TOPN = "top-n";
	public static final String MIN_FEATURE_ELEMENTS_NUM = "min-feature-elements-num";
	public static final String ENTAILING_ELEMENT_FEATURE_CONSTRUCTOR = "entailing-element-feature-constructor"; 
	public static final String ENTAILED_ELEMENT_FEATURE_CONSTRUCTOR = "entailed-element-feature-constructor";
	public static final String PREV_ELEMENTS_STORAGE_DEVICE = "prev-elements-storage-device";
	public static final String PREV_FEATURES_STORAGE_DEVICE = "prev-features-storage-device";
	public static final String RELEVANT_POS_LIST = "relevant-pos-list";
	public static final String ELEMENT_SIMILARITY_COMBINER = "element-similarity-combiner";
	public static final String OUT_COMBINED_FILE = "out-combined-file";
	public static final String NEW_TMP_DIR = "new-tmp-dir";
	public static final String IN_FILES = "in-files";
	public static final String SIMILARITY_COMBINATION_CLASS = "similarity-combination-class";
	public static final String FILE_TO_REDIS = "file-to-redis";
	public static final String INFILE = "infile";
	public static final String OUTFILE = "outfile";
	public static final String TEXTUNITS_OUTFILE = "textunits-outfile";
	public static final String COOCCURRENCES_OUTFILE = "cooccurrences-outfile";
	public static final String RIGHT_TO_LEFT_SIMILARITIES = "right-to-left-similarities";
	public static final String INCLUDE_DEPENDENCY_RELATION = "include-dependency-relation";
	public static final String STORAGE_DEVICE_CLASS = "storage-device-class";
	public static final String MIN_SCORE = "min-score";
	public static final String MIN_COUNT = "min-count";
	public static final String TOP_PERCENT = "top-percent";
	public static final String SORT = "sort";
	public static final String SENTENCE_READER_CLASS = "sentence-reader-class";
	public static final String ENCODING = "encoding";
	public static final String DELIMITER = "delimiter";
	public static final String PART_OF_SPEECH_CLASS = "part-of-speech-class";
	public static final String PART_OF_SPEECH_FACTORY_CLASS = "part-of-speech-factory-class";
	public static final String IGNORE_SAVED_CANONICAL_POS_TAG = "ignore-saved-canonical-pos-tag";
	public static final String FILTERED_TEXTUNITS_FILE = "filtered-textunits-file";
	public static final String TMP_CONTENT_DIR = "tmp-content-dir";
	public static final String IS_CORPUS_INDEX = "is-index-corpus";
	public static final String CO_OCCURENCE_CLASS = "cooccurrence-class";
	public static final String ELEMENT_CLASS = "element-class";
	public static final String FEATURE_CLASS = "feature-class";
	public static final String SIMILARITY_FILE = "similarity-file";
	public static final String ELEMENTS_FILE = "elements-file";
	public static final String CONFIGURATION_FILE = "configuration-file";
	public static final String CONFIGURATION_MODULE = "configuration-module";
	public static final String MAPRED_COOCCURRENCE_COUNTING = "mapred-cooccurrence-counting";
	public static final String MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1 = "separate-filter-and-index-elements-features-1";
	public static final String MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2 = "separate-filter-and-index-elements-features-2";
	public static final String INDIR = "in-dir";
	public static final String OUTDIR = "out-dir";
	public static final String COOCCURENCE_EXTRACTION_CLASS = "cooccurrence-extraction-class";
	public static final String ELEMENT_FEATURE_EXTRACTION_CLASS = "element-feature-extraction-class";
	public static final String FEATURES_FILE = "features-file";
	public static final String ELEMENT_FEATURE_COUNTS_FILE = "element-feature-counts-file";
	public static final String FEATURE_ELEMENTS_FILE = "feature-elements-file";
	public static final String MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1_X = "separate-filter-and-index-elements-features-1-x";
	public static final String MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2_X = "separate-filter-and-index-elements-features-2-x";
	public static final String MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_1_Y = "separate-filter-and-index-elements-features-1-y";
	public static final String MAPRED_SEPARATE_FILTER_INDEX_ELEMENT_FEATURE_2_Y = "separate-filter-and-index-elements-features-2-y";
	public static final String AE_TEMPLATE_FILE = "ae-template-file";
	public static final String IS_SORTED = "is-sorted";
	public static final String TMP_DIR = "tmp-dir";
	public static final String TOP_N_RULES = "top-n-rules";
	public static final String L2R_REDIS_HOST = "l2r-redis-host";
	public static final String L2R_REDIS_PORT = "l2r-redis-port"; 
	public static final String R2L_REDIS_HOST = "r2l-redis-host";
	public static final String R2L_REDIS_PORT = "r2l-redis-port";
	public static final String RESOURCE_NAME = "resource-name";
	public static final String INSTANCE_NAME = "instance-name";
	public static final String L2R_REDIS_DB_FILE = "l2r-redis-db-file"; 
	public static final String R2L_REDIS_DB_FILE = "r2l-redis-db-file";
	public static final String KNOWLEDGE_RESOURCE = "knowledge-resource";
	public static final String WINDOW_SIZE = "window-size";
	public static final String REDIS_CONFIGURATION_TEMPLATE_FILE = "redis-configuration-template-file";
	public static final String REDIS_BIN_DIR = "redis-binary-dir";
	public static final String GENERATE_ARTIFICIAL_ROOT = "generate-artificial-root";
	public static final String REDIS_FILE = "redis-file";
	public static final String START_ELEMENT_ID = "stasrt-element-id";
	public static final String MIN_FEATURES_SIZE = "min-features-size";
	public static final String MAX_SIMILARITIES_PER_ELEMENT = "max-similarities-per-element"; 

}

