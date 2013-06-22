package eu.excitementproject.eop.biutee.utilities.unigram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.excitementproject.eop.common.utilities.corpora.reuters.ReutersCorpusReader;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitter;
import eu.excitementproject.eop.lap.biu.en.tokenizer.MaxentTokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;

/**
 * 
 * @author Asher Stern
 * @since May 13, 2013
 *
 */
public class UnigramCreator
{
	public static final String LOG4J_PROPERTIES = "log4j.properties";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			 File log4jPropertiesFile = new File(LOG4J_PROPERTIES);
			 if (!log4jPropertiesFile.exists()) throw new UnigramCreatorException("File "+log4jPropertiesFile.getPath()+" does not exist.");
			 PropertyConfigurator.configure(log4jPropertiesFile.getPath());
			 logger = Logger.getLogger(UnigramCreator.class);
			 
			 new UnigramCreator().go(args);
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
			if (logger!=null)
			{
				logger.error("An error occurred.", t);
			}
		}
	}
	
	public void go(String[] args) throws UnigramCreatorException, FileNotFoundException, IOException
	{
		int argsIndex=0;
		Tokenizer tokenizer = new MaxentTokenizer();
		SentenceSplitter sentenceSplitter = new NagelSentenceSplitter();
		File ReutersCorpusDir = new File(args[argsIndex++]);
		ReutersCorpusReader reader = new ReutersCorpusReader(ReutersCorpusDir);
		String lambdaString = args[argsIndex++];
		double lambda = Double.parseDouble(lambdaString);
		File output = new File(args[argsIndex++]);
		
		logger.info("Reuters corpus directory = "+ReutersCorpusDir.getPath());
		logger.info("lambda = "+String.format("%-4.4f", lambda));
		logger.info("Output = "+output.getPath());
		
		logger.info("Reading corpus.");
		UnigramCountersCreator countersCreator = new UnigramCountersCreator(reader,tokenizer,sentenceSplitter);
		countersCreator.create();
		logger.info("Reading corpus - done.");
		
		logger.info("Creating estimations.");
		UnigramCreatorByCounters creatorByCounters = new UnigramCreatorByCounters(countersCreator.getCounters(),lambda);
		creatorByCounters.create();
		logger.info("Creating estimations - done.");
		
		logger.info("Writing to file.");
		try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(output)))
		{
			outputStream.writeObject(creatorByCounters.getUnigramEstimation());
			outputStream.writeDouble(lambda);
			outputStream.writeLong(creatorByCounters.getVocabularySize());
			outputStream.writeLong(creatorByCounters.getTotalCounters());
		}
		logger.info("Writing to file - done.");
	}
	

	private static Logger logger = null;
}
