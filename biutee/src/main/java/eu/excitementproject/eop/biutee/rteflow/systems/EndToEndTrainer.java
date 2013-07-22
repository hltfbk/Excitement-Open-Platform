package eu.excitementproject.eop.biutee.rteflow.systems;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierTrainer;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Proof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Trainer;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.AccuracyClassifierTrainer;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;

/**
 * 
 * @author Asher Stern
 * @since Jul 15, 2013
 *
 * @param <I>
 * @param <P>
 */
public abstract class EndToEndTrainer<I extends Instance, P extends Proof> extends EndToEndSystem
{
	public EndToEndTrainer(String configurationFileName, String configurationModuleName)
	{
		super(configurationFileName, configurationModuleName);
	}
	
	public void train() throws BiuteeException
	{
		logger.info("Start training.");
		logger.info("Loading dataset.");
		Dataset<I> dataset = createDataset();
		logger.info("Creating trainer.");
		
		Trainer<I,P> trainer = new Trainer<I,P>(
				dataset,scripts,
				createClassifierGenerator(),createProver(),
				numberOfThreads,createResultsFactory(),
				createClassifierTrainer(),
				teSystemEnvironment.getFeatureVectorStructureOrganizer()
				);
		logger.info("Training.");
		trainer.train();
		logger.info("Training done.");
	}
	

	
	

	protected abstract Dataset<I> createDataset() throws BiuteeException;
	protected abstract ClassifierGenerator createClassifierGenerator() throws BiuteeException;
	protected abstract Prover<I, P> createProver() throws BiuteeException;
	protected abstract ResultsFactory<I, P> createResultsFactory() throws BiuteeException;
	
	protected ClassifierTrainer createClassifierTrainer() throws BiuteeException
	{
		return new AccuracyClassifierTrainer(teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}


	
	private static final Logger logger = Logger.getLogger(EndToEndTrainer.class);
}
