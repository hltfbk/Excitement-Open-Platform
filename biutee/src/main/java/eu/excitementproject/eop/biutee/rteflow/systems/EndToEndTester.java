package eu.excitementproject.eop.biutee.rteflow.systems;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Proof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Results;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Tester;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 16, 2013
 *
 */
public abstract class EndToEndTester<I extends Instance, P extends Proof> extends EndToEndSystem
{
	public EndToEndTester(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
	}

	public void test() throws BiuteeException
	{
		logger.info("Creating dataset.");
		Dataset<I> dataset = createDataset();
		logger.info("Creating tester.");
		Tester<I, P> tester = new Tester<I, P>(
				dataset, scripts,
				createClassifierGenerator(), createProver(),
				numberOfThreads, createResultsFactory()
				);
		logger.info("Testing.");
		tester.test();
		logger.info("Testing done.");
		printAndSaveResults(tester.getResults());
	}
	
	protected abstract Dataset<I> createDataset() throws BiuteeException;
	protected abstract ClassifierGenerator createClassifierGenerator() throws BiuteeException;
	protected abstract Prover<I, P> createProver() throws BiuteeException;
	protected abstract ResultsFactory<I, P> createResultsFactory() throws BiuteeException;
	protected abstract void printAndSaveResults(Results<I, P> results) throws BiuteeException;
	
	private static final Logger logger = Logger.getLogger(EndToEndTester.class);
}
