package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.RTEPairsETETrainer;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.RTEPairsPreProcessor;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.transformations.utilities.GlobalMessages;

/**
 * Runs BIUTEE Stand-alone via command line.
 * 
 * @author Ofer Bronstein
 * 
 *
 */
public class BiuteeMain {

	/**
	 * Run specific steps in the BIUTEE flow, according to specific values specified as a comma-separated list in parameter flowList:
	 * <tt>lap_train, train, lap_test, test</tt>, or <tt>full</tt> for all steps.<BR> 
	 * @throws Throwable 
	 */
	public static void runBiuteeCustomFlow(String configPath, String flowList) throws Throwable  {
		// (Reminder: the first command-line parameter is the configuration file name).
		// Read the second command-line parameter. This might be something like "lap_train,train"
		Set<String> flow = new LinkedHashSet<String>(Arrays.asList(flowList.split(",")));
		if (flow.size()==0) {
			throw new BiuteeMainException("At least one flow step must be provided, got none.");
		}

		// Validate correctness of second command-line parameter
		Set<String> diff = new LinkedHashSet<String>(flow);
		diff.removeAll(ALLOWED_STEPS);
		if (diff.size() != 0) {
			throw new BiuteeMainException("Disallowed flow steps: " + StringUtil.join(diff, ","));
		}

		if (flow.contains("full") && flow.size()!=1) {
			throw new BiuteeMainException("Flow step \"full\" must not be provided with other steps.");
		}
		boolean hasFull = flow.contains("full");

		// Run the appropriate action, according to the second command-line parameter
		if (hasFull || flow.contains("lap_train"))	doLAP(configPath, RTEPairsPreProcessor.TrainTestEnum.TRAIN.name());
		if (hasFull || flow.contains("train"))		doTraining(configPath);
		if (hasFull || flow.contains("lap_test"))	doLAP(configPath, RTEPairsPreProcessor.TrainTestEnum.TEST.name());
		if (hasFull || flow.contains("test"))		doTesting(configPath);
	}

	

	private static void doLAP(String configPath, String trainOrTest) throws Throwable {
		RTEPairsPreProcessor.initAndRun(configPath, trainOrTest);
	}
	
	private static void doTraining(String configPath) throws Throwable {
		RTEPairsETETrainer.initAndRun(configPath);
	}
	
	private static void doTesting(String configPath) throws Throwable {
		RTEPairsETETrainer.initAndRun(configPath);
	}
	
	public static void main(String[] args)
	{
		// The only important line in this function is
		// runBiuteeCustomFlow(args[0], args[1]);
		try
		{
			try
			{
				if (args.length != 2) {
					throw new BiuteeMainException("Exactly 2 arguments must be provided: <configuration path> <flow list>");
				}
				runBiuteeCustomFlow(args[0], args[1]);
			}
			finally
			{
				if (logger!=null)
				{
					GlobalMessages.getInstance().addToLogAndExperimentManager(logger);
				}
			}
		}
		catch(Throwable t)
		{
			ExceptionUtil.outputException(t, System.out);
			if (logger!=null)
			{
				try{ExceptionUtil.logException(t, logger);}catch(Throwable tt){}
			}
		}
	}

	public static final Set<String> ALLOWED_STEPS = new LinkedHashSet<String>(Arrays.asList(new String[] {"full", "lap_train", "train", "lap_test", "test"}));
	
	private static final Logger logger = Logger.getLogger(BiuteeMain.class);

}
