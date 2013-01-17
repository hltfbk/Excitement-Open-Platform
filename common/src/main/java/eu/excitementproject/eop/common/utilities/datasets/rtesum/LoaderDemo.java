package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.File;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;


public class LoaderDemo
{
	public LoaderDemo(String datasetDir)
	{
		super();
		this.datasetDir = datasetDir;
	}

	public void f() throws Rte6mainIOException
	{
		Rte6DatasetLoader loader = new Rte6DatasetLoader(new File(this.datasetDir));
		loader.setFileSystemNames(new DefaultRte6NoveltyFileSystemNames());
		loader.load();
		Map<String, TopicDataSet> topics = loader.getTopics();
		for (String topicId : topics.keySet())
		{
			System.out.println("topic: "+topicId);
			TopicDataSet topicDataSet = topics.get(topicId);
			Map<String, Map<Integer, String>> documents = topicDataSet.getDocumentsMap();
			for (String docId:documents.keySet())
			{
				System.out.print("docId: "+docId);
				int numberOfSentences = documents.get(docId).keySet().size();
				System.out.println(" number of sentences: "+numberOfSentences);
			}
		}
		
		System.out.println("printing gold standard...");
		Map<String, Map<String, Set<SentenceIdentifier>>> answers = loader.getAnswers();
		if (null==answers)
			System.out.println("no gold standard!");
		else
		{
			for (String topicId : answers.keySet())
			{
				System.out.println("topic: "+topicId);
				Map<String, Set<SentenceIdentifier>> topicAnswer = answers.get(topicId);
				for (String hypothesisId : topicAnswer.keySet())
				{
					for (SentenceIdentifier sid : topicAnswer.get(hypothesisId))
					{
						System.out.println("h: "+hypothesisId+" <-- "+sid.getDocumentId()+"/"+sid.getSentenceId());
					}
				}
			}
		}
		
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1) throw new Rte6mainIOException("args");
			
			new LoaderDemo(args[0]).f();
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
		
	}

	
	private String datasetDir;


}

