package eu.excitementproject.eop.util.runner;

import org.kohsuke.args4j.*;

/**
 * Command line options for running the EOPRunner class (@link eu.excitementproject.eop.util.test.runner.Demo)
 * 
 * @author Vivi Nastase (FBK)
 *
 */
public class EOPRunnerCmdOptions {

	@Option(name="-config", usage="Configuration file for the EDA")
	public String config = null;
	
	@Option(name="-language", usage="Language of dataset (DE|EN|IT)")
	public String language = null;
	
	@Option(name="-eda", usage="The EDA to be used")
	public String eda = null;
	
	@Option(name="-lap", usage="The LAP to be used")
	public String lap = null;
	
	@Option(name="-nolap", usage="Even if train and test files are given, don't do preprocessing (useful for the Experimenter)")
	public boolean nolap = false;
	
	@Option(name="-text", usage="The text part of a text/hypothesis pair")
	public String text = "";
	
	@Option(name="-hypothesis", usage="The hypothesis part of a text/hypothesis pair")
	public String hypothesis = "";
		
	@Option(name="-results", usage="The name of the (xml formatted) results file")
	public String results = null;
	
	@Option(name="-train", usage="Do the training or not")
	public boolean train = false;
	
	@Option(name="-trainFile", usage="Input file with TE pairs for training")
	public String trainFile = null;

	@Option(name="-trainDir", usage="Directory for LAP-produced xmi files from training data")
	public String trainDir = null;

	@Option(name="-test", usage="Test or not")
	public boolean test = false;

	@Option(name="-testFile", usage="Input file with TE pairs for testing")
	public String testFile = null;

	@Option(name="-testDir", usage="Directory for LAP-produced xmi files for test data")
	public String testDir = null;
	
	@Option(name="-model", usage="Input model file (obtained through training previously)")
	public String model = null;
	
	@Option(name="-output", usage="Directory for the run's output")
	public String output = "";
	
	@Option(name="-score", usage="Compute the scores on the test data or not")
	public boolean score = false;
	
}
