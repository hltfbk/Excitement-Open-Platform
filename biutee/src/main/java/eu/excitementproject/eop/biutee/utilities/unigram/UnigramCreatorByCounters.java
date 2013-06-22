package eu.excitementproject.eop.biutee.utilities.unigram;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * @author Asher Stern
 * @since May 13, 2013
 *
 */
public class UnigramCreatorByCounters
{
	public UnigramCreatorByCounters(Map<String, Long> counters, double lambda)
	{
		super();
		this.counters = counters;
		this.lambda = lambda;
	}
	
	
	public void create()
	{
		unigramEstimation = new LinkedHashMap<>();
		vocabularySize = counters.size();
		double vocabularySizeDouble = (double)vocabularySize;
		logger.info("vocabulary size = "+vocabularySize);
		
		
		logger.info("Calculating total-counters.");
		totalCounters=0;
		for (String token : counters.keySet())
		{
			totalCounters += counters.get(token);
		}
		logger.info("Total-counters = "+totalCounters);
		
		double totalCountersDouble = (double)totalCounters;
		
		double lambdaTimesVocabularySize = lambda*vocabularySizeDouble;
		
		double totalCountersPlusLambdaTimesVocabularySize = totalCountersDouble+lambdaTimesVocabularySize;
		
		logger.info("Creating estimations.");
		for (String token : counters.keySet())
		{
			unigramEstimation.put(token,
					(((double)counters.get(token))+lambda)/( totalCountersPlusLambdaTimesVocabularySize )
					);
		}
		logger.info("Creating estimations - done.");
	}
	
	
	
	public Map<String, Double> getUnigramEstimation() throws UnigramCreatorException
	{
		if (null==unigramEstimation) throw new UnigramCreatorException("Not created");
		return unigramEstimation;
	}


	public long getVocabularySize() throws UnigramCreatorException
	{
		if (null==unigramEstimation) throw new UnigramCreatorException("Not created");
		return vocabularySize;
	}


	public long getTotalCounters() throws UnigramCreatorException
	{
		if (null==unigramEstimation) throw new UnigramCreatorException("Not created");
		return totalCounters;
	}



	private final Map<String, Long> counters;
	private final double lambda;
	
	private Map<String, Double> unigramEstimation=null;
	private long vocabularySize=0;
	private long totalCounters=0;
	
	private static final Logger logger = Logger.getLogger(UnigramCreatorByCounters.class);
}
