package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;



/**
 * Takes the data set, and creates an answer file that contains
 * all the candidates.
 * @author Asher Stern
 *
 */
public class DemoCandidatesToAnswer
{
	
	@SuppressWarnings("serial")
	private static class DemoCandidatesToAnswerException extends Exception
	{public DemoCandidatesToAnswerException(String str){super(str);}}
	
	public static String TASK_DIR_NAME = "Main_Task";
	public static String TOPIC_DIR_PREFIX = "Topic";
	public static String CANDIDATES_FILE_NAME = "Main_evaluation_pairs.xml";
	public static String ANSWER_FILE_NAME = "answer_all_candidates.xml";
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1) throw new DemoCandidatesToAnswerException("args");
			String rootDirName = args[0];
			DemoCandidatesToAnswer demo = new DemoCandidatesToAnswer();
			demo.runDemo(rootDirName);
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
	}

	public void runDemo(String rootDirName)
	{
		try
		{
			this.answers = new LinkedHashMap<String, Map<String,Set<SentenceIdentifier>>>();
			addAllCandidatesFiles(rootDirName);
			writeAnswersFile(rootDirName);
		}
		catch(Rte6mainIOException e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
		catch(DemoCandidatesToAnswerException e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
		
	}
	
	private void writeAnswersFile(String rootDirName) throws Rte6mainIOException
	{
		AnswersFileWriter writer = new DefaultAnswersFileWriter();
		writer.setXml(new File(rootDirName,ANSWER_FILE_NAME).getPath());
		writer.setWriteTheEvaluationAttribute(false);
		writer.setAnswers(answers);
		writer.write();
	}
	
	private void addAllCandidatesFiles(String rootDirName) throws DemoCandidatesToAnswerException, Rte6mainIOException
	{
		File rootDir = new File(rootDirName);
		if (rootDir.isDirectory()); else throw new DemoCandidatesToAnswerException("not dir");
		File[] topicDirs = rootDir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				if (pathname.isDirectory()) if (pathname.getName().startsWith(TOPIC_DIR_PREFIX))
					return true;
				
				return false;
				
			}
		});
		
		for (File topicDir : topicDirs)
		{
			File mainTaskDir = new File(topicDir,TASK_DIR_NAME);
			File candidatesFile = new File(mainTaskDir,CANDIDATES_FILE_NAME);
			if (!candidatesFile.exists()) throw new DemoCandidatesToAnswerException("not exist");
			if (!candidatesFile.isFile()) throw new DemoCandidatesToAnswerException("not a file");
			
			addCandidateFile(candidatesFile.getPath());
		}
		
		
	}
	
	private void addCandidateFile(String candidatesFileName) throws Rte6mainIOException
	{
		EvaluationPairsReader reader = new DefaultEvaluationPairsReader();
		reader.setXml(candidatesFileName);
		reader.read();
		String topicId = reader.getTopicId();
		Map<String, Set<SentenceIdentifier>> candidatesMape = reader.getCandidateSentencesMap();
		answers.put(topicId, candidatesMape);
	}
	
	private Map<String,Map<String,Set<SentenceIdentifier>>> answers;
}
