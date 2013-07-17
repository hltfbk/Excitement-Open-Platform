package eu.excitementproject.eop.biutee.rteflow.systems;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierGenerator;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ClassifierTrainer;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Dataset;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Instance;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Proof;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Prover;
import eu.excitementproject.eop.biutee.rteflow.endtoend.ResultsFactory;
import eu.excitementproject.eop.biutee.rteflow.endtoend.Trainer;
import eu.excitementproject.eop.biutee.rteflow.endtoend.default_impl.DefaultClassifierTrainer;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

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
		TrainerPrintProofs trainer = new TrainerPrintProofs(
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
	
	protected class TrainerPrintProofs extends Trainer<I,P>
	{
		public TrainerPrintProofs(Dataset<I> dataset,List<OperationsScript<Info, BasicNode>> scripts,ClassifierGenerator classifierGenerator, Prover<I, P> prover,int numberOfThreads, ResultsFactory<I, P> resultsFactory,ClassifierTrainer classifierTrainer,FeatureVectorStructure featureVectorStructure)
		{
			super(dataset, scripts, classifierGenerator, prover, numberOfThreads, resultsFactory, classifierTrainer, featureVectorStructure);
		}
		
		@Override
		protected void endOfIterationEntryPoint() throws BiuteeException
		{
			logger.info("Result of current iteration: "+resultsLastIteration.print());
			logger.info("Proofs:");
			Iterator<String> detailsIterator = resultsLastIteration.instanceDetailsIterator();
			while (detailsIterator.hasNext())
			{
				String details = detailsIterator.next();
				logger.info(details);
			}
		}
	}
	
	

	protected abstract Dataset<I> createDataset() throws BiuteeException;
	protected abstract ClassifierGenerator createClassifierGenerator() throws BiuteeException;
	protected abstract Prover<I, P> createProver() throws BiuteeException;
	protected abstract ResultsFactory<I, P> createResultsFactory() throws BiuteeException;
	
	protected ClassifierTrainer createClassifierTrainer() throws BiuteeException
	{
		return new DefaultClassifierTrainer(teSystemEnvironment.getFeatureVectorStructureOrganizer());
	}


	
	private static final Logger logger = Logger.getLogger(EndToEndTrainer.class);
}
