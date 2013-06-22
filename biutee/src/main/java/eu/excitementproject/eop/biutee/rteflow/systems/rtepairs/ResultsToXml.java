package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.excitementproject.eop.biutee.classifiers.Classifier;
import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEClassificationType;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtilitiesException;
import eu.excitementproject.eop.common.utilities.xmldom.XmlDomUtils;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * Output results of RTE-pairs to an XML file.
 * 
 * @author Asher Stern
 * @since March-1-2013
 *
 */
public class ResultsToXml
{
	public static class ScoreAndRTEClassificationType
	{
		public ScoreAndRTEClassificationType(double score,RTEClassificationType classification)
		{this.score = score;this.classification = classification;}
		
		public double getScore(){return score;}
		public RTEClassificationType getClassification(){return classification;}

		private final double score;
		private final RTEClassificationType classification;
	}
	
	public static Map<String, ScoreAndRTEClassificationType> convertPairResults(Map<ExtendedPairData, PairResult> pairsResults, Classifier classifier) throws ClassifierException
	{
		Map<String, ScoreAndRTEClassificationType> ret = new LinkedHashMap<String, ScoreAndRTEClassificationType>();
		for (Map.Entry<ExtendedPairData, PairResult> pairResult : pairsResults.entrySet())
		{
			double classificationScore = classifier.classify(pairResult.getValue().getBestTree().getFeatureVector());
			boolean entailment = ClassifierUtils.classifierResultToBoolean(classificationScore);
			ScoreAndRTEClassificationType value = new ScoreAndRTEClassificationType(classificationScore,entailment?RTEClassificationType.ENTAILMENT:RTEClassificationType.UNKNOWN);
			ret.put(pairResult.getKey().getPair().getId().toString(),value);
		}
		return ret;
	}
	
	public ResultsToXml(Map<String, ScoreAndRTEClassificationType> results,
			File outputFile)
	{
		super();
		this.results = results;
		this.outputFile = outputFile;
	}

	public void output() throws TeEngineMlException
	{
		try
		{
			Document document = createXmlDocumentOfResults();
			XmlDomUtils.writeDocumentToFile(document, outputFile);
		}
		catch (ParserConfigurationException | RuntimeException | XmlDomUtilitiesException e)
		{
			throw new TeEngineMlException("Could not output results to XML. See nested exception.",e);
		}
	}
	
	private Document createXmlDocumentOfResults() throws ParserConfigurationException
	{
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element rootElement = document.createElement("results");
		document.appendChild(rootElement);
		for (Map.Entry<String, ScoreAndRTEClassificationType> result : results.entrySet())
		{
			Element pairElement = document.createElement("pair");
			pairElement.setAttribute("id", result.getKey());
			pairElement.setAttribute("entailment", result.getValue().getClassification().name());
			pairElement.setAttribute("score", String.valueOf(result.getValue().getScore()));
			rootElement.appendChild(pairElement);
		}
		return document;
	}

	
	private final Map<String, ScoreAndRTEClassificationType> results;
	private final File outputFile;
}
