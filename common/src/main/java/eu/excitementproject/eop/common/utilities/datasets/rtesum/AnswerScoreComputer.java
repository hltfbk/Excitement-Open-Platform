package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;



/**
 * Computes the recall, precision and F1 of the RTE6-main/novelty task.
 * The computation is done by comparing the gold standard file and the answer file produced
 * by the system.
 * 
 * @author Asher Stern
 * 
 *
 */
public class AnswerScoreComputer
{
	/////////////////////////////////// PUBLIC ////////////////////////////////////////

	public AnswerScoreComputer(String goldStandardFileName,
			String systemAnswerFileName)
	{
		super();
		this.goldStandardFileName = goldStandardFileName;
		this.systemAnswerFileName = systemAnswerFileName;
	}
	
	public void compute() throws Rte6mainIOException
	{
		AnswersFileReader gsReader = new DefaultAnswersFileReader();
		gsReader.setXml(goldStandardFileName);
		gsReader.read();
		Map<String,Map<String,Set<SentenceIdentifier>>> gsAnswer = gsReader.getAnswers();
		
		AnswersFileReader sysAnswerReader = new DefaultAnswersFileReader();
		sysAnswerReader.setXml(systemAnswerFileName);
		sysAnswerReader.read();
		Map<String,Map<String,Set<SentenceIdentifier>>> sysAnswer = sysAnswerReader.getAnswers();
		
		compute(gsAnswer,sysAnswer);
	}
	
	
	

	
	public String getResultsAsString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("total recall = ");
		buffer.append(percentString(totalRecall));
		buffer.append("\n");
		buffer.append("total precision = ");
		buffer.append(percentString(totalPrecision));
		buffer.append("\n");
		buffer.append("total F1 = ");
		buffer.append(percentString(totalF1));
		buffer.append("\n");

		return buffer.toString();
	}
	
	

	public Map<String, Double> getTopicRecall()
	{
		return topicRecall;
	}




	public Map<String, Double> getTopicPrecision()
	{
		return topicPrecision;
	}




	public Map<String, Double> getTopicF1()
	{
		return topicF1;
	}




	public double getTotalRecall()
	{
		return totalRecall;
	}




	public double getTotalPrecision()
	{
		return totalPrecision;
	}




	public double getTotalF1()
	{
		return totalF1;
	}
	
	/////////////////////////////////// PROTECTED ////////////////////////////////////////
	
	
	/**
	 * You can override this method if you wish to prevent warnings from throwing exceptions.
	 */
	protected void warn(String message) throws Rte6mainIOException
	{
		throw new Rte6mainIOException(message);
	}
	
	
	/////////////////////////////////// PRIVATE ////////////////////////////////////////
	
	
	private void compute(Map<String,Map<String,Set<SentenceIdentifier>>> gsAnswer,
			Map<String,Map<String,Set<SentenceIdentifier>>> sysAnswer) throws Rte6mainIOException
	{
		int totalGsYes = 0;
		int totalCorrectYes = 0;
		int totalYesAnswer = 0;
		for (String topicId : gsAnswer.keySet())
		{
			int topicGsYes = 0;
			int topicCorrectYes = 0;
			int topicYesAnswer = 0;

			Map<String,Set<SentenceIdentifier>> gsTopic = gsAnswer.get(topicId);
			Map<String,Set<SentenceIdentifier>> sysTopic = sysAnswer.get(topicId);
			if (null==sysTopic) { 
				throw new Rte6mainIOException("missing topic "+topicId);
			}
			
			for (String hypothesisId : gsTopic.keySet())
			{
				Set<SentenceIdentifier> gsHypothesis = gsTopic.get(hypothesisId);
				Set<SentenceIdentifier> sysHypothesis = sysTopic.get(hypothesisId);
				if (null==sysHypothesis)
				{
					warn("System results for hypothesis "+hypothesisId+" are not available (sysHypothesis is empty).");
					sysHypothesis = Collections.emptySet();
				}
				
				topicGsYes += gsHypothesis.size();
				topicYesAnswer += sysHypothesis.size();
				for (SentenceIdentifier sid : sysHypothesis)
				{
					if (gsHypothesis.contains(sid))
						++topicCorrectYes;
				}
			}
			
			topicPrecision.put(topicId,computePrecision(topicYesAnswer, topicCorrectYes,topicGsYes));
			topicRecall.put(topicId, computeRecall(topicCorrectYes, topicGsYes));
			
			
			
			totalGsYes += topicGsYes;
			totalCorrectYes += topicCorrectYes;
			totalYesAnswer += topicYesAnswer;
		}
		
		totalRecall = computeRecall(totalCorrectYes, totalGsYes);
		totalPrecision = computePrecision(totalYesAnswer, totalCorrectYes, totalGsYes);
		
		computeF1All();
	}
	
	private void computeF1All()
	{
		for (String topicId : topicRecall.keySet())
		{
			double recall = topicRecall.get(topicId);
			double precision = topicPrecision.get(topicId);
			topicF1.put(topicId, computeF1(recall, precision));
		}
		totalF1 = computeF1(totalRecall,totalPrecision);
	}
	
	private double computePrecision(int yes, int correctYes, int realYes)
	{
		double ret = 0;
		if (yes==0)
		{
			if (realYes!=0) ret = 0;
			else ret = 1;
		}
		else
		{
			ret = ((double)correctYes)/((double)yes);
		}
		return ret;
	}
	
	private double computeRecall(int correctYes, int realYes)
	{
		return ((double)correctYes)/((double)realYes);
	}
	
	private double computeF1(double recall, double precision)
	{
		if (0==(recall+precision))return 0;
		else return (recall*precision*2)/(recall+precision);
	}
	
	private String percentString(double d)
	{
		return String.format("%3.2f%%", d*100);
	}
	
	private String goldStandardFileName;
	private String systemAnswerFileName;
	
	private Map<String,Double> topicRecall = new LinkedHashMap<String, Double>();
	private Map<String,Double> topicPrecision = new LinkedHashMap<String, Double>();
	private Map<String,Double> topicF1 = new LinkedHashMap<String, Double>();
	private double totalRecall = 0;
	private double totalPrecision = 0;
	private double totalF1 = 0;
	
	
	
	//////////////////////////// demo main method ////////////////////////////
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length<2)throw new Exception("args");
			AnswerScoreComputer computer = new AnswerScoreComputer(args[0], args[1])
			{
				@Override protected void warn(String message) throws Rte6mainIOException
				{
					System.out.println("Warning: "+message);
				}
			};
			computer.compute();
			System.out.println(computer.getResultsAsString());
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
	}
	
}
