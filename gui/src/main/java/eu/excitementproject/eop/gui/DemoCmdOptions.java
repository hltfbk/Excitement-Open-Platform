package eu.excitementproject.eop.gui;

import org.kohsuke.args4j.*;

/**
 * Command line options for running the Demo class (@link eu.excitementproject.eop.gui.Demo)
 * 
 * @author Vivi Nastase (FBK)
 *
 */
public class DemoCmdOptions {

	@Option(name="-config", usage="Configuration file for the EDA")
	public String config = "";
	
	@Option(name="-language", usage="Language of dataset (DE|EN|IT)")
	public String language = null;
	
	@Option(name="-resource", usage="Specificy the resource to be used (WordNet|Wikipedia|VerbOcean|...)")
	public String resource = null;
	
	@Option(name="-activatedEDA", usage="The EDA to be used")
	public String activatedEDA = null;
	
	@Option(name="-lap", usage="The LAP to be used")
	public String lap = null;
	
	@Option(name="-distance", usage="The distance algorithm to be used")
	public String distance = null;
	
	@Option(name="-text", usage="The test (for providing a test/hypothesis pair)")
	public String text = "";
	
	@Option(name="-hypothesis", usage="The hypothesis (for providing a test/hypothesis pair)")
	public String hypothesis = "";
	
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
	
	@Option(name="-dir", usage="Directory for the configuration files")
	public String dir = null;

}
