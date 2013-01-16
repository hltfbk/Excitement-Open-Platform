package eu.excitementproject.eop.common.utilities.datasets.rtesum;

import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.utilities.Utils;


/**
 * 
 * @author Asher Stern
 * @since Aug 16, 2010
 *
 */
public class EvaluationPairsIntersection
{
	public EvaluationPairsIntersection(String xmlFileName1,
			String xmlFileName2, String xmlFileResult)
	{
		super();
		this.xmlFileName1 = xmlFileName1;
		this.xmlFileName2 = xmlFileName2;
		this.xmlFileResult = xmlFileResult;
	}
	
	public void writeIntersectionFile() throws Rte6mainIOException
	{
		EvaluationPairsReader reader1 = getReader(xmlFileName1);
		EvaluationPairsReader reader2 = getReader(xmlFileName2);
		
		if (!reader1.getTopicId().equals(reader2.getTopicId()))
			throw new Rte6mainIOException("Wrong xml files. not containing the same topic id.");
		
		EvaluationPairsWriter writer = new DefaultEvaluationPairsWriter();
		writer.setXml(this.xmlFileResult);
		writer.setTopicId(reader1.getTopicId());
		Map<String, Set<SentenceIdentifier>> candidates = Utils.mapsIntersection(reader1.getCandidateSentencesMap(), reader2.getCandidateSentencesMap());
		writer.setCandidates(candidates);
		writer.write();
	}
	
	private EvaluationPairsReader getReader(String xmlFileName) throws Rte6mainIOException
	{
		EvaluationPairsReader reader = new DefaultEvaluationPairsReader();
		reader.setXml(xmlFileName);
		reader.read();
		return reader;
	}
	
	
	
	private String xmlFileName1;
	private String xmlFileName2;
	private String xmlFileResult;
	

}
