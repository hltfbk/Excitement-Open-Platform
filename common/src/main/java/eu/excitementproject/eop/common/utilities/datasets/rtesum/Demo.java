package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;


/**
 * Demonstration of the IO wrappers in the package.
 * 
 * @author asher
 *
 */
public class Demo
{
	public static final String CORPUS_FILE_NAME = "/media/data2/data/asher/phd/data/rte/rte_data/rte6/RTE6_Main_Novelty_DEV_SET/RTE6_DEVSET/Topic_D0909/Corpus/AFP_ENG_20050614.0518.xml";
	public static final String EVALUATION_FILE_NAME = "/media/data2/data/asher/phd/data/rte/rte_data/rte6/RTE6_Main_Novelty_DEV_SET/RTE6_DEVSET/Topic_D0909/Main_Task/Main_evaluation_pairs.xml";
	public static final String HYPOTHESES_FILE_NAME = "/media/data2/data/asher/phd/data/rte/rte_data/rte6/RTE6_Main_Novelty_DEV_SET/RTE6_DEVSET/Topic_D0909/Main_Task/Main_hypotheses.xml";
	public static final String ANSWERS_FILE_NAME = "/media/data2/data/asher/phd/data/rte/rte_data/rte6/RTE6_Main_Novelty_DEV_SET/RTE6_DEVSET/RTE6_Main_DEVSET_GS.xml";
	public static final String MY_ANSWERS_FILE_NAME = "/media/data2/data/asher/phd/data/rte/rte_data/rte6/RTE6_Main_Novelty_DEV_SET/RTE6_DEVSET/myanswers.xml";
	public static final String ORIG_EVALUATION_PAIRS = "/media/data2/data/asher/phd/data/rte/rte_data/rte6/RTE6_Main_Novelty_DEV_SET/RTE6_DEVSET/Topic_D0909/Main_Task/Main_evaluation_pairs.xml";
	public static final String MY_EVALUATION_PAIRS = "/media/data2/data/asher/phd/data/rte/rte_data/rte6/RTE6_Main_Novelty_DEV_SET/RTE6_DEVSET/Topic_D0909/Main_Task/myevaluationpairs.xml";

	
	public void f() throws Exception
	{
		CorpusDocumentReader reader = new DefaultCorpusDocumentReader();
		reader.setXml(CORPUS_FILE_NAME);
		reader.read();
		System.out.println("headline = "+reader.getHeadline());
		System.out.println("sentences:");
		LinkedHashMap<Integer, String> sentences = reader.getMapSentences();
		for (Integer id : sentences.keySet())
		{
			System.out.println("id: "+id+": "+sentences.get(id));
		}
		
		System.out.println("evaluation");
		EvaluationPairsReader ereader = new DefaultEvaluationPairsReader();
		ereader.setXml(EVALUATION_FILE_NAME);
		ereader.read();
		System.out.println("topic = "+ereader.getTopicId());
		Map<String, Set<SentenceIdentifier>> mapEvaluation = ereader.getCandidateSentencesMap();
		for (String hid : mapEvaluation.keySet())
		{
			System.out.println("hypothesis: "+hid);
			Set<SentenceIdentifier> candidates = mapEvaluation.get(hid);
			for (SentenceIdentifier sid : candidates)
			{
				System.out.println("document id:"+sid.getDocumentId()+" sentence id:"+sid.getSentenceId());
			}
			System.out.println("-------------------------------------");
		}
		
		System.out.println("hypotheses");
		HypothesisFileReader hreader = new DefaultHypothesisFileReader();
		hreader.setXml(HYPOTHESES_FILE_NAME);
		hreader.read();
		Map<String,String> mapHypothesesTexts = hreader.getHypothesisTextMap();
		for (String hid : mapHypothesesTexts.keySet())
		{
			System.out.println("h: "+hid+" = "+mapHypothesesTexts.get(hid));
		}
		
		System.out.println("answers");
		AnswersFileReader areader = new DefaultAnswersFileReader();
		areader.setXml(ANSWERS_FILE_NAME);
		areader.read();
		Map<String,Map<String,Set<SentenceIdentifier>>> mapAnswers = areader.getAnswers();
		for (String topicId : mapAnswers.keySet())
		{
			System.out.println("topic = "+topicId);
			Map<String,Set<SentenceIdentifier>> answersForTopic = mapAnswers.get(topicId);
			for (String hid : answersForTopic.keySet())
			{
				System.out.println("hypothesis : "+hid);
				Set<SentenceIdentifier> answersForHypothesis = answersForTopic.get(hid);
				for (SentenceIdentifier sid : answersForHypothesis)
				{
					System.out.println("document: "+sid.getDocumentId()+" sentence: "+sid.getSentenceId());
				}
				
			}
		}
		
		AnswersFileWriter awriter = new DefaultAnswersFileWriter();
		awriter.setXml(MY_ANSWERS_FILE_NAME);
		awriter.setAnswers(mapAnswers);
		awriter.setWriteTheEvaluationAttribute(false);
		awriter.write();
		
		
		
		
		
		

	}
	
	/**
	 * Writes the given gold-standard to a new file that has
	 * no "text" element (i.e. the output is in the submission format)
	 * @throws Rte6mainIOException
	 */
	public void g() throws Rte6mainIOException
	{
		final String GS_FILE_NAME = "/media/Data/asher/data/dev/alpha_system/rte6/RTE6_DEVSET/RTE6_Main_DEVSET_GS.xml";
		AnswersFileReader afr = new DefaultAnswersFileReader();
		afr.setGoldStandard(false);
		afr.setXml(GS_FILE_NAME);
		afr.read();
		Map<String,Map<String,Set<SentenceIdentifier>>> answers = afr.getAnswers();
		
		
		AnswersFileWriter afw = new DefaultAnswersFileWriter();
		afw.setWriteTheEvaluationAttribute(true);
		afw.setAnswers(answers);
		afw.setXml(GS_FILE_NAME+".my.xml");
		afw.write();
		
		
		
		
		
		
	}
	
	public void h() throws Rte6mainIOException
	{
		EvaluationPairsReader reader = new DefaultEvaluationPairsReader();
		reader.setXml(ORIG_EVALUATION_PAIRS);
		reader.read();
		EvaluationPairsWriter writer = new DefaultEvaluationPairsWriter();
		writer.setXml(MY_EVALUATION_PAIRS);
		writer.setTopicId(reader.getTopicId());
		writer.setCandidates(reader.getCandidateSentencesMap());
		writer.write();
	}
	
	public static void main(String[] args)
	{
		try
		{
			Demo demo = new Demo();
			//demo.f();
			//demo.g();
			demo.h();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}


	}

}


