package eu.excitementproject.eop.util.edaexperimenter.experimenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;

import eu.excitementproject.eop.util.edaexperimenter.data.DataHandling;
import eu.excitementproject.eop.lap.implbase.RawDataFormatReader.PairXMLData;
import eu.excitementproject.eop.util.runner.EOPRunner;

public class Experimenter {
	
	public EOPRunner runner;
	
	private String tmpTrainFile = "train.xml";
	private String tmpTestFile = "test.xml";
	
	private String language = "EN";
	
	// data read from the given files
	// index keys: fileName (a.k.a. cluster) and class (Entailment/Non-Entailment) -- useful for balancing if necessary
	private HashMap<String,HashMap<String,Set<PairXMLData>>> dataRaw;
	
	// data filtered according to the "balance" parameter
	private HashMap<String,HashMap<String,Set<PairXMLData>>> dataFiltered;
	
	// fold for crossvalidation/training and testing
	private HashMap<String,Set<PairXMLData>> dataFolds;
	
	public ExperimenterCommandOptions options;

	private final Logger logger; 
	
	public Experimenter(String[] args) {
		
		Logger.getRootLogger().setLevel(Level.INFO);
		logger = Logger.getLogger("eu.excitementproject.eda_exp.experimenter.Experimenter");
		
		options = new ExperimenterCommandOptions();
		CmdLineParser parser = new CmdLineParser(options);
		
		if (args.length == 0){
			showHelp(parser);
		}
	
		try{ 
			parser.parseArgument(args);
			
			tmpTrainFile = options.output + "/" + tmpTrainFile;
			tmpTestFile = options.output + "/" + tmpTestFile;				

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Run the experimenter according to the given parameters
	 * @throws IOException
	 */
	public void run() throws IOException {
		
		if (options.dataDir != null) {

			dataRaw = ExperimenterFileUtils.loadDataFromXML(options.dataDir, options.pattern);
			language = ExperimenterFileUtils.getLanguage(options.dataDir, options.pattern);
			
			logger.info("Data raw -- keys: " + dataRaw.keySet().size());
			
			logger.info("Language: " + language);

			if (options.test) {
				if (options.model != null) {
					test(dataRaw, options.model);
				} else {
					throw new IOException("Cannot do testing without either training or an already produced model");
				}
			} else {
				dataFiltered = DataHandling.filterData(dataRaw, options.balance);
				
				logger.info("Data filtered -- keys: " + dataFiltered.keySet().size());
				
				if (options.xval > 1) {
					crossValidation();
				} else {
					oneRun();
				}
			}
		}
	}
	

	// 
	private void test(HashMap<String, HashMap<String, Set<PairXMLData>>> data, String model) {
		
		// write the data to a file to be passed as an argument to the EOPRunner
		ExperimenterFileUtils.writeDataToFile(data, tmpTestFile, language);
		
		// replace the name of the model in the configuration file
		ExperimenterFileUtils.writeModelInConfig(options.config,options.model);
		
		String outFileStem = options.output + TimeStamp.getCurrentTime().toString();
		
		// make the list of arguments for the EOPRunner
		String[] args =  new String[] {"-config", options.config, "-test", "-testFile", tmpTestFile, "-output", outFileStem, "-score"};
		
		logger.info("Running the EOP with arguments: " + StringUtils.join(args," "));
		
		if (! options.fakeRun) {
			EOPRunner runner = new EOPRunner(args);
			runner.run();
		}
	}



	/**
	 * Makes one training and testing run.
	 * Separated the training and testing from the given data, based on the given (or the default) split ratio
	 * Performs training and testing with these files, and evaluates the results (assumes that the data contains the gold standard)
	 */
	private void oneRun() {

		// replace the name of the model in the configuration file
		ExperimenterFileUtils.writeModelInConfig(options.config, options.model);

		// split data into training and testing according to the given split ratio
		dataFolds = DataHandling.splitData(dataFiltered, options.ratio, 2, options.split);

		ExperimenterFileUtils.writeDataToFile(dataFolds.get("train"), tmpTrainFile, language);
		ExperimenterFileUtils.writeDataToFile(dataFolds.get("test"), tmpTestFile, language);

		// make the list of arguments for the EOPRunner
		String[] args =  new String[] {"-config", options.config, "-train", "-trainFile", tmpTrainFile, "-test", "-testFile", tmpTestFile, "-output", options.output, "-score"};
		
		logger.info("Running the EOP with arguments: " + StringUtils.join(args," "));		
//		System.out.println("Running the EOP with arguments: " + StringUtils.join(args," "));		

		if (! options.fakeRun) {
			EOPRunner runner = new EOPRunner(args);
			runner.run();
		}
	}


	/**
	 * performs cross-validation with the number of folds given as argument (xval parameter)
	 */
	private void crossValidation() {
		
		dataFolds = DataHandling.splitData(dataFiltered, 1.0 / options.xval, options.xval, options.split);
		Set<PairXMLData> dataTrain = new HashSet<PairXMLData>();
		String thisTestFile, thisTrainFile;
		
		for (String i: dataFolds.keySet()) {
			for (String x: dataFolds.keySet()) {
				if (! i.matches(x)) {
					dataTrain.addAll(dataFolds.get(x));
				}
			}

			thisTrainFile= tmpTrainFile.replace("xml", i + ".xml");
			thisTestFile = tmpTestFile.replace("xml", i + ".xml");
			
			ExperimenterFileUtils.writeDataToFile(dataFolds.get(i), thisTestFile, language);
			ExperimenterFileUtils.writeDataToFile(dataTrain, thisTrainFile, language);
			
			// make the list of arguments for the EOPRunner
			String[] args =  new String[] {"-config", options.config, "-train", "-trainFile", thisTrainFile, "-test", "-testFile", thisTestFile, "-output", options.output, "-score"};
			
			logger.info("Running the EOP with arguments: " + StringUtils.join(args," "));

			if (! options.fakeRun) {
				EOPRunner runner = new EOPRunner(args);
				runner.run();
			}
			
			dataTrain.clear();
		}
	}
		
	/**
	 * When the command line arguments could not be parsed, show the help
	 * 
	 * @param parser
	 */
	private static void showHelp(CmdLineParser parser) {
		System.out.println("EOPRunner  [options ...] [arguments ...]");
		parser.printUsage(System.out);
	}

	
	public static void main(String[] args) {
		Experimenter ex = new Experimenter(args);
		try {
			ex.run();
		} catch (IOException e) {
			ex.logger.error("Problems running the experimenter!" + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
