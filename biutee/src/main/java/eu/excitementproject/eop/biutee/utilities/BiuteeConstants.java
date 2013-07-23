package eu.excitementproject.eop.biutee.utilities;

import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.transformations.codeannotations.Workaround;
import eu.excitementproject.eop.transformations.operations.finders.SubstitutionFlipPosFinder;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.utilities.Constants;



/**
 * 
 * @author Asher Stern
 *
 */
public class BiuteeConstants
{
	//////////////////////////////////////////////////////////////////////////
	// Important constants: constants that you might consider changing their values

	public static final boolean USE_NEGATIVES_FROM_PREVIOUS_ITERATIONS_IN_ACCURACY_TRAINING = true;
	
	/**
	 * The feature {@link Feature#INVERSE_HYPOTHESIS_LENGTH} is assigned
	 * a value only if this constant is <tt>true</tt>. Otherwise it is zero, which
	 * means that hypothesis-length is not given as a feature to the classifier at all.
	 */
	public static final boolean USE_HYPOTHESIS_LENGTH_FEATURE = true;
	
	
	/**
	 * If <tt>true</tt>, then the value assigned to {@link Feature#INVERSE_HYPOTHESIS_LENGTH} is
	 * the length of the hypothesis (the number of the hypothesis parse-tree nodes).<BR>
	 * If <tt>false</tt>, then the value assigned to that feature is 1/length, where length
	 * is the hypothesis length (the number of the hypothesis parse-tree nodes).
	 */
	public static final boolean INVERSE_HYPOTHESIS_LENGTH_IS_HYPOTHESIS_LENGTH = true;

	
	/**
	 * A flag indicates if the system should print time statistics to the log
	 * file. "Time statistics" - means time measurements of search algorithm.
	 */
	public static final boolean PRINT_TIME_STATISTICS = false;


	/**
	 * If true - it means that BIUTEE uses value of Unigram language model
	 * for an inserted-on-the-fly node. If false - than a constant value is
	 * used, regardless whether the inserted word is common or rare.
	 */
	public static final boolean USE_MLE_FOR_INSERTION_COST = true;

	public static final int LOCAL_CREATIVE_NUMBER_OF_LOCAL_ITERATIONS = 3;
	
	// 0 means no heuristic
	public static final int LOCAL_CREATIVE_HEURISTIC_LOCAL_ITERATIONS_HISTORY = 3;
	
	// if <=0 it means all.
	public static final int LOCAL_CREATIVE_NUMBER_OF_TREES_TO_PROCESS = -1;


	public static final int FIRST_ITERATION_IN_DEFAULT_OPERATION_SCRIPT = 3;
	
	/**
	 * The "script" ({@link OperationsScript}) determines which transformations
	 * can be applied in a current step, or iteration, of the proof.
	 * Generally, there are two lists of available transformations, one
	 * is "first" and one is "other". "other" is contained in "first".
	 * <BR>
	 * In LLGS algorithm, each step is part of "global loop" and "local loop"
	 * (the loop of the local-lookahead). So, this constants, determines in how
	 * many "global" iterations, the "first" list is returned, when we are
	 * in the first iteration of the "local loop". In other words: when the
	 * "global" iteration number is smaller than this constant, and the "local"
	 * iteration number is 0, then the "first" list is returned. Otherwise, "other"
	 * list is returned.
	 */
	public static final int NUMBER_OF_FIRST_GLOBAL_ITERATIONS_IN_LOCAL_CREATIVE_IN_DEFAULT_OPERATION_SCRIPT = 2;

	/**
	 * The -c parameter that SVM light uses when classifying the dataset
	 * Note that currently SVM is not in use.
	 */
	public static final double SVM_SLACK_COEFFICIENT = 7.0;

	public static final double F1_CLASSIFIER_GAMMA_FOR_SIGMOID = 100.0;
	public static final double INCREASE_PARAMETERS_VALUE_IN_F1_CLASSIFIER = 0.0005;
	
	public static final int NUMBER_OF_SAMPLES_BY_WHICH_REASONABLE_GUESS_IS_TRAINED = 2000;
	public static final Double DEFAULT_HYPOTHESIS_LENGTH_FOR_TRAINING_REASONABLE_GUESS = 10.0;

	//////////////////////////////////////////////////////////////////////////
	// Constants that are less likely to be changed.

	public static final long RANDOM_SEED_FOR_GAUSSIAN_GENERATOR_FOR_REASONABLE_GUESS_TRAINING = 0L;
	
	public static final boolean USE_NUMBER_NORMALIZER = true;
	
	/**
	 * If true - then it uses the feature {@link Feature#MOVE_TO_ROOT_IF_PT_PLUS_OR_MINUS}
	 * for move transformations when the move is to the root, and the moved node has
	 * predicate-truth specified as + or -.
	 */
	public static final boolean SPECIAL_FEATURE_FOR_MOVE_BASED_ON_PT = true;
	
	/**
	 * If you are working on RTE-Sum (RTE 6,7) and it looks like that
	 * the method DefaultOperationsScript.setHypothesisInformation() is called 
	 * too many times, then consider changing the value of this constant to <tt>true</tt>.
	 */
	public static final boolean USE_OLD_CONCURRENCY_IN_RTE_SUM = false;

	/**
	 * Number of elements in the cache of hypothesis-templates. Used by
	 * {@link InitializationTextTreesProcessor}.
	 */
	public static final int CACHE_SIZE_HYPOTHESIS_TEMPLATES = 50;

	/**
	 * The main loop of training - the iterative learning loop - can stop
	 * either due to convergence (delta of accuracy between two iterations
	 * is small), or it can be stopped after a constant number of iterations.
	 * Though in theory the stop-condition should be convergence, in practice
	 * however, we use a constant number of iterations for the main loop.
	 */
	public static final boolean MAIN_LOOP_STOPS_WHEN_ACCURACY_CONVERGES = false;

	/**
	 * The main loop of training - the iterative learning loop - can stop
	 * either due to convergence (delta of accuracy between two iterations
	 * is small), or it can be stopped after a constant number of iterations.
	 * Though in theory the stop-condition should be convergence, in practice
	 * however, we use a constant number of iterations for the main loop.
	 */
	public static final int MAX_NUMBER_OF_MAIN_LOOP_ITERATIONS = 3;

	public static final double TRAINER_ACCURACY_DIFFERENCE_TO_STOP = 0.001;

	/**
	 * Used by {@link BeamSearchTextTreesProcessor}. specifies the number
	 * of iterations in the beam search performed by the beam-search loop after
	 * the goal is found. The beam-search loop run until the goal is found, but
	 * it will not stop immediately when the goal is found, but will continue
	 * for more some iterations.
	 */
	public static final int PAIR_PROCESS_ITERATION_AFTER_CONVERSION = 5;

	/**
	 * Used by {@link BeamSearchTextTreesProcessor}. This is the beam-size parameter.
	 */
	public static final int MAX_NUMBER_OF_TREES = 135;
	
	


	/**
	 * Assumes that classifier-for-search is logistic-regression, and indicates
	 * that the restriction that all weights should be positive is enforced
	 * during the classifier training, and not as a post-process.
	 */
	public static final boolean RESTRICT_SEARCH_CLASSIFIER_DURING_TRAINING = true;

	public static final double INCREASE_PARAMETERS_VALUE_IN_SEARCH_CLASSIFIER = 0.05;
	
	/**
	 * Specifies whether a single instance of lemmatizer will be created and
	 * used by all threads of the system. See {@link SystemInitialization}, and
	 * see {@link SubstitutionFlipPosFinder} (It seems that that class is the 
	 * only user of that lemmatizer).
	 */
	public static final boolean LEMMATIZER_SINGLE_INSTANCE = true;
	
	public static final double LEARNING_RATE_ASTAR_FUTURE_ESTIMATION = 1.0;
	public static final int ASTAR_DFS_ITERATIONS = 3;

	public static final int SIZE_QUEUE_RECENT_PAIRS_IN_GUI = 100;
	public static final String FILENAME_RECENT_PAIRS_IN_GUI = "gui.last.pairs.xml";


	
	/**
	 * Indicates whether the default classifier returned by
	 * {@link ClassifierFactory} will be {@link HypothesisNoramlizeLinearClassifier} or
	 * not.
	 */
	public static final boolean USE_HYPOTHESIS_NORMALIZE_CLASSIFIER = false;
	
	public static final boolean GUI_LOADS_LABELED_SAMPLES = false;

	//////////////////////////////////////////////////////////////////////////
	// Constants that there is no reason to change, now and ever.


	/**
	 * A delimiter in the value of "dataset" parameter, for RTE-sum.
	 * The parameter value should be annual-flag#dev-test-flag#dataset-path
	 */
	public static final String RTESUM_DATASET_PARAM_DELIMITER = "#";
	
	/**
	 * Indicates if {@link TreeHistory} will contain not only {@link Specification}s
	 * of the operations performed, but also the feature-vector assigned after
	 * each iteration.
	 */
	public static final boolean ADD_FEATURE_VECTOR_TO_HISTORY = true;
	
	
	public static final boolean BEAM_SEARCH_USE_CACHE_OF_GENERATED_TREES = true;
	
	public static final boolean DEBUG_USE_DUMMY_COREF = false;
	
	/**
	 * Used by old beam search
	 */
	public static final double RELIEF_CLASSIFIER_EXPONENT_IN_SEARCH = 15.0; 
	
	public static final String LABELED_SAMPLES_FILE_PREFIX = "labeled_samples";
	public static final String LABELED_SAMPLES_FILE_POSTFIX = ".ser";
	
	public static final String LEARNING_MODEL_FILE_PREFIX = "model";
	public static final String LEARNING_MODEL_FILE_POSTFIX = ".xml";
	public static final String LEARNING_MODEL_FILE_SEARCH_INDICATOR = "search";
	public static final String LEARNING_MODEL_FILE_PREDICTIONS_INDICATOR = "predictions";
	
	public static final String RTE_SUM_OUTPUT_ANSWER_FILE_PREFIX = "answers";
	public static final String RTE_SUM_OUTPUT_ANSWER_FILE_POSTFIX = ".xml";
	
	public static final String RTE_SUM_OUTPUT_RESULTS_FILE_PREFIX = "serialized_results";
	public static final String RTE_SUM_OUTPUT_RESULTS_FILE_POSTFIX = ".ser";
	
	public static final String RTE_PAIRS_OUTPUT_RESULTS_FILE_PREFIX = "serialized_results";
	public static final String RTE_PAIRS_OUTPUT_RESULTS_FILE_INFIX_TEX = "_test";
	public static final String RTE_PAIRS_OUTPUT_RESULTS_FILE_POSTFIX = ".ser";
	
	public static final String RTE_PAIRS_XML_RESULTS_FILE_NAME_PREFIX = "rte_pairs_results";
	public static final String RTE_PAIRS_XML_RESULTS_FILE_NAME_POSTFIX = ".xml";

	
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
		/**
		 * Currently, Easy-First has a bug and sometimes produces bad trees which cannot be annotated.
		 * If this constant is <tt>false</tt>, then no exception will be thrown, and the engine will
		 * use the un-annotated tree.
		 */
		public static final boolean ANNOTATOR_FAILURE_IS_BLOCKING = false;
		
		public static final boolean USE_WORKAROUND_TEXT_PROCESSOR = true;
	}

}