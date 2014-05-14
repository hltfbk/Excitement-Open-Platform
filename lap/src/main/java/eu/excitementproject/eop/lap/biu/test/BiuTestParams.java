package eu.excitementproject.eop.lap.biu.test;

/**
 * The class contains constant values used as parameters for different classes
 * in the tests.<BR>
 * This conceptually replaces the configuration file, for the tests. 
 *  
 * @author Ofer Bronstein
 * @since August 2013
 */
public class BiuTestParams {
	public static final String STANFORD_NER_CLASSIFIER_PATH = "../third-party/stanford-ner-2009-01-16/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz";
	public static final String MAXENT_POS_TAGGER_MODEL_FILE = "../third-party/stanford-postagger-full-2008-09-28/models/left3words-wsj-0-18.tagger";
	public static final String EASYFIRST_HOST = "localhost";
	public static final int EASYFIRST_PORT = 8080;
}
