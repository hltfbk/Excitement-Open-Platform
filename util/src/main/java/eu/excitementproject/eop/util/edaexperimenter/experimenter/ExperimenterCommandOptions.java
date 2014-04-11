package eu.excitementproject.eop.util.edaexperimenter.experimenter;

import org.kohsuke.args4j.*;

public class ExperimenterCommandOptions{

	@Option(name="-balance", usage="Balance the dataset (positive/negative ratio close to 1) or not")
	public boolean balance = false;
	
	@Option(name="-config", usage="Configuration file for the EDA")
	public String config = null;

	@Option(name="-dataDir", usage="Directory with xml files for training/testing")
	public String dataDir = null;

	@Option(name="-fakeRun", usage="Do not actually run the EOP, only show the call with parameters (for testing purposes)")
	public boolean fakeRun = false;
	
	@Option(name="-model", usage="File containing the model to be created during training or used for testing")
	public String model = null;
	
	@Option(name="-output", usage="Directory for outputting the results (default /tmp/)")
	public String output = "/tmp/"; 
	
	@Option(name="-pattern", usage="The file name pattern for processing (e.g. email*.xml")
	public String pattern = "xml";
	
	@Option(name="-ratio", usage="If xval = 1, then use this as a ratio of training/testing (default 0.5)")
	public double ratio = 0.5;

	@Option(name="-split", usage="If split = mix, then each cluster will be represented in both training and testing, if split = pure, then clusters are put without splitting in either train or test data")
	public String split = "mix";
	
	@Option(name="-test", usage="If it is given, then use the given model and data for testing only")
	public boolean test = false;
	
	@Option(name="-xval", usage="Number of folds for cross-validation (default 1)")
	public int xval = 1;
	
}
