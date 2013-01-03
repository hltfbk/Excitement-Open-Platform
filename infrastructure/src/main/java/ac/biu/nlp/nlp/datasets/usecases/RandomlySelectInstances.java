package ac.biu.nlp.nlp.datasets.usecases;

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import ac.biu.nlp.nlp.datasets.rte6main.Rte6DatasetLoader;
import ac.biu.nlp.nlp.datasets.rte6main.Rte6mainIOException;
import ac.biu.nlp.nlp.datasets.rte6main.SentenceIdentifier;
import ac.biu.nlp.nlp.datasets.rte6main.TopicDataSet;
import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.general.Utils;

public class RandomlySelectInstances
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<2) throw new RuntimeException("args");
			String datasetDirectory = args[0];
			int numberOfPositives = Integer.parseInt(args[1]);
			int numberOfNegatives = Integer.parseInt(args[2]);
			PrintWriter writer = new PrintWriter(System.out);
			RandomlySelectInstances app = new RandomlySelectInstances(datasetDirectory,numberOfPositives,numberOfNegatives,writer,writer);
			app.go();
			writer.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	
	
	public RandomlySelectInstances(String datasetDirectoryName,
			int numberOfPositives, int numberOfNegatives, PrintWriter writer,
			PrintWriter messageWriter)
	{
		super();
		this.datasetDirectoryName = datasetDirectoryName;
		this.numberOfPositives = numberOfPositives;
		this.numberOfNegatives = numberOfNegatives;
		this.writer = writer;
		this.messageWriter = messageWriter;
	}



	public void go() throws Rte6mainIOException
	{
		Rte6DatasetLoader loader = new Rte6DatasetLoader(new File(datasetDirectoryName),false,true);
		messageWriter.println("loading dataset");
		loader.load();
		messageWriter.println("dataset loaded. names are: "+loader.getFileSystemNames().getClass().getSimpleName());
		
		Vector<Instance> allCandidates = new Vector<Instance>();
		Map<String, TopicDataSet> topics = loader.getTopics();
		for (Map.Entry<String, TopicDataSet> topic : topics.entrySet())
		{
			Map<String, Set<SentenceIdentifier>> candidates = topic.getValue().getCandidatesMap();
			
			for (Map.Entry<String, Set<SentenceIdentifier>> candidate : candidates.entrySet())
			{
				for (SentenceIdentifier sid : candidate.getValue())
				{
					allCandidates.add(new Instance(topic.getValue().getTopicId(),candidate.getKey(), sid));
				}
			}
		}
		messageWriter.println("Number of all candidates: "+allCandidates.size());
		
		Set<Instance> allPositives = new LinkedHashSet<Instance>();
		Map<String, Map<String, Set<SentenceIdentifier>>> answers = loader.getAnswers();
		for (Map.Entry<String, Map<String, Set<SentenceIdentifier>>> answer : answers.entrySet())
		{
			for (Map.Entry<String, Set<SentenceIdentifier>> hypothesisAnswer : answer.getValue().entrySet())
			{
				for (SentenceIdentifier sid : hypothesisAnswer.getValue())
				{
					allPositives.add(new Instance(answer.getKey(), hypothesisAnswer.getKey(), sid));
				}
			}
		}
		messageWriter.println("Number of all positives: "+allPositives.size());
		Vector<Instance> vectorPositives = new Vector<Instance>();
		vectorPositives.addAll(allPositives);
		
		Vector<Instance> onlyNegatives = new Vector<Instance>();
		for (Instance candidate : allCandidates)
		{
			if (!allPositives.contains(candidate))
			{
				onlyNegatives.add(candidate);
			}
		}
		messageWriter.println("Number of only negatives: "+onlyNegatives.size());
		
		int[] randomPermutationPositives = Utils.randomPermutation(allPositives.size());
		int[] randomPermutationNegatives = Utils.randomPermutation(onlyNegatives.size());
		
		writer.println("selected positives:");
		for (int index=0;(index<numberOfPositives)&&(index<randomPermutationPositives.length);++index)
		{
			writer.println(vectorPositives.get(randomPermutationPositives[index]).toString());
		}
		
		writer.println(StringUtil.generateStringOfCharacter('-', 50));
		writer.println("selected negatives:");
		for (int index=0;(index<numberOfNegatives)&&(index<randomPermutationNegatives.length);++index)
		{
			writer.println(onlyNegatives.get(randomPermutationNegatives[index]).toString());
		}

	}
	
	private static class Instance
	{
		public Instance(String topicId, String hypothesisId,
				SentenceIdentifier sentenceId)
		{
			super();
			this.topicId = topicId;
			this.hypothesisId = hypothesisId;
			this.sentenceId = sentenceId;
		}
		
		
		public String getTopicId()
		{
			return topicId;
		}
		public String getHypothesisId()
		{
			return hypothesisId;
		}
		public SentenceIdentifier getSentenceId()
		{
			return sentenceId;
		}
		
		@Override
		public String toString()
		{
			return getTopicId()+"/"+getHypothesisId()+"/"+getSentenceId().getDocumentId()+"/"+getSentenceId().getSentenceId();
		}
		
		


		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((hypothesisId == null) ? 0 : hypothesisId.hashCode());
			result = prime * result
					+ ((sentenceId == null) ? 0 : sentenceId.hashCode());
			result = prime * result
					+ ((topicId == null) ? 0 : topicId.hashCode());
			return result;
		}


		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Instance other = (Instance) obj;
			if (hypothesisId == null)
			{
				if (other.hypothesisId != null)
					return false;
			} else if (!hypothesisId.equals(other.hypothesisId))
				return false;
			if (sentenceId == null)
			{
				if (other.sentenceId != null)
					return false;
			} else if (!sentenceId.equals(other.sentenceId))
				return false;
			if (topicId == null)
			{
				if (other.topicId != null)
					return false;
			} else if (!topicId.equals(other.topicId))
				return false;
			return true;
		}




		private final String topicId;
		private final String hypothesisId;
		private final SentenceIdentifier sentenceId;
	}
	
	

	private String datasetDirectoryName;
	private int numberOfPositives;
	private int numberOfNegatives;
	private PrintWriter writer;
	private PrintWriter messageWriter;
}
