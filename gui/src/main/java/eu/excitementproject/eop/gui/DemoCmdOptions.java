package eu.excitementproject.eop.gui;

import org.kohsuke.args4j.*;

public class DemoCmdOptions {

	@Option(name="-language", usage="Language of dataset (DE|EN|IT)")
	public String language = "";
	
	@Option(name="-resource", usage="Specificy the resource to be used (WordNet|Wikipedia|VerbOcean|...)")
	public String resource = "";
	
	@Option(name="-activatedEDA", usage="The EDA to be used")
	public String activatedEDA = "";
	
	@Option(name="-distance", usage="The distance algorithm to be used")
	public String distance = "";
	
	@Option(name="-text", usage="The test (for providing a test/hypothesis pair)")
	public String text = "";
	
	@Option(name="-hypothesis", usage="The hypothesis (for providing a test/hypothesis pair)")
	public String hypothesis = "";
	
	@Option(name="-train", usage="Input file with TE pairs for training")
	public String train = null;
	
	@Option(name="-test", usage="Input file with TE pairs for testing")
	public String test = null;
	
	@Option(name="-model", usage="Input model file (obtained through training previously)")
	public String model = null;
	
	@Option(name="-output", usage="Directory for the run's output")
	public String output = "";
	
	@Option(name="-dir", usage="Directory for the configuration files")
	public String dir = "";

}
