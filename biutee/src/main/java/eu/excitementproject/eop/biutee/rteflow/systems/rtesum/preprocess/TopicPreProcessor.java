package eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.preprocess.Instruments;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.DocumentMetaData;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.TopicDataSet;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessor;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.PreprocessUtilities;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizer;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * @author Asher Stern
 * @since Jun 5, 2011
 *
 */
public class TopicPreProcessor
{
	public TopicPreProcessor(TopicDataSet topicDataSet,
			Instruments<Info, BasicNode> instruments,
			boolean recognizeNameEntities, boolean doTextNormalization) throws TeEngineMlException
	{
		super();
		if (null==topicDataSet) throw new TeEngineMlException("Null topicDataSet");
		if (null==instruments) throw new TeEngineMlException("Null instruments");
		
		this.topicDataSet = topicDataSet;
		this.instruments = instruments;
		this.recognizeNameEntities = recognizeNameEntities;
		this.doTextNormalization = doTextNormalization;
	}

	public void preprocess() throws TeEngineMlException, ParserRunException, NamedEntityRecognizerException, CoreferenceResolutionException, TreeCoreferenceInformationException, TextPreprocessorException
	{
		logger.info("Pre-processing topic: "+this.topicDataSet.getTopicId());
		
		if (doTextNormalization)
			normalizeTopicDataSet();
		
		Map<String,BasicNode> mapHypothesisIdToTree = new LinkedHashMap<String, BasicNode>();
		BasicParser parser = instruments.getParser();
		NamedEntityRecognizer neRecognizer = instruments.getNamedEntityRecognizer();
		Map<String, String> hypothesisMap = topicDataSet.getHypothesisMap();
		for (String hypothesisId : hypothesisMap.keySet())
		{
			String sentence = hypothesisMap.get(hypothesisId);
			if (null==sentence) throw new TeEngineMlException("Null hypothesis for id "+hypothesisId);
			logger.debug("Pre-processing hypothesis: "+sentence);
			sentence = sanityCheckSentence(sentence);
			BasicNode hypothesisTree = PreprocessUtilities.generateParseTree(sentence, parser, neRecognizer, recognizeNameEntities);
			mapHypothesisIdToTree.put(hypothesisId, hypothesisTree);
		}
		
		Map<String, Map<Integer, BasicNode>> allDocumentsTrees = new LinkedHashMap<String, Map<Integer,BasicNode>>();
		Map<String,BasicNode> mapDocumentsHeadlines = new LinkedHashMap<String, BasicNode>();
		Map<String,TreeCoreferenceInformation<BasicNode>> allDocumentsCoreferenceInformation = new LinkedHashMap<String, TreeCoreferenceInformation<BasicNode>>();

		CoreferenceResolver<BasicNode> coreferenceResolver = instruments.getCoreferenceResolver();
		for (String documentId : topicDataSet.getDocumentsMap().keySet())
		{
			logger.debug("Pre-processing document: "+documentId);
			Map<Integer,String> document = topicDataSet.getDocumentsMap().get(documentId);
			String documentText = getFullDocumentText(document);
			if(logger.isDebugEnabled()){logger.debug("Full document text (used for coreference resolution) is"+documentText);}
			
			Map<Integer,BasicNode> documentTrees = new LinkedHashMap<Integer, BasicNode>();
			for (Integer sentenceId : document.keySet())
			{
				String textSentence = document.get(sentenceId);
				if(logger.isDebugEnabled()){logger.debug("Working on sentence: "+textSentence);}
				textSentence = sanityCheckSentence(textSentence);
				BasicNode tree = PreprocessUtilities.generateParseTree(textSentence, parser, neRecognizer, recognizeNameEntities);
				documentTrees.put(sentenceId, tree);
			}
			logger.debug("Performing co-reference resolution.");
			List<BasicNode> listTrees = getMapAsList(documentTrees);
			coreferenceResolver.setInput(listTrees, documentText);
			coreferenceResolver.resolve();
			TreeCoreferenceInformation<BasicNode> corefInformation = coreferenceResolver.getCoreferenceInformation();
			PreprocessUtilities.integrateParserAntecedentToCoreference(listTrees, corefInformation);


			
			allDocumentsTrees.put(documentId, documentTrees);
			allDocumentsCoreferenceInformation.put(documentId, corefInformation);
			
			String documentHeadline = topicDataSet.getDocumentsMetaData().get(documentId).getHeadline();
			if(logger.isDebugEnabled()){logger.debug("Document head-line = "+documentHeadline);}
			documentHeadline = sanityCheckSentence(documentHeadline);
			BasicNode headlineTree = PreprocessUtilities.generateParseTree(documentHeadline, parser, neRecognizer, recognizeNameEntities);
			mapDocumentsHeadlines.put(documentId, headlineTree);
		}
		
		this.preprocessedTopicDataSet = new PreprocessedTopicDataSet(this.topicDataSet, mapHypothesisIdToTree, allDocumentsTrees, allDocumentsCoreferenceInformation, mapDocumentsHeadlines);
		
		logger.info("Pre-processing of topic: "+this.topicDataSet.getTopicId()+" is done.");
	}
	
	public PreprocessedTopicDataSet getPreprocessedTopicDataSet()
	{
		return preprocessedTopicDataSet;
	}

	/**
	 * Add periods to sentences that do not end with punctuation. including those that end with punctuation and quotes like 
	 * <i>It may also "purchase, lease, receive, own, sell and convey real and personal property of all kinds."</i><br>
	 * This should improve the parses a bit
	 * 
	 * @param textSentence
	 * @return sanitized sentence
	 */
	private String sanityCheckSentence(String textSentence) {
		if (logger.isDebugEnabled())
		{
			logger.debug("Performing sanity-check on sentence...");
			logger.debug("Sentence is: \""+textSentence+"\"");
		}
		
		if (Constants.COMPLETE_PERIODS)
		{
			textSentence = textSentence.trim();
			if  (!textSentence.isEmpty())
			{
				Character lastChar1 = textSentence.charAt(textSentence.length()-1);
				String lastChar = lastChar1.toString();
				if (!lastChar.matches(TERMINATING_PUNCTUATION_REGEX))
				{
					textSentence += PERIOD;
				}
			}
		}
		if (Constants.REDUCE_NOISE_IN_DATASET)
			textSentence = TextualNoiseReducer.reduceNoise(textSentence);
		
		if (logger.isDebugEnabled())
		{
			logger.debug("Sanity-check done.");
			logger.debug("Sentence being returned is: \""+textSentence+"\"");
		}
		return textSentence;
	}

	private String getFullDocumentText(Map<Integer,String> document)
	{
		List<String> list = getMapAsList(document);
		StringBuffer sb = new StringBuffer();
		for (String sentence : list)
		{
			sb.append(sentence);
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private static <T> List<T> getMapAsList(Map<Integer,T> map)
	{
		List<T> list = new ArrayList<T>(map.keySet().size());
		Set<Integer> ids = map.keySet();
		Integer[] idsArray = Utils.collectionToArray(ids, new Integer[0]);
		Arrays.sort(idsArray);
		for (Integer id : idsArray)
		{
			list.add(map.get(id));
		}
		return list;
	}
	
	private void normalizeTopicDataSet() throws TextPreprocessorException, TeEngineMlException
	{
		logger.info("Performing text-normalization over the whole topic.");
		TextPreprocessor textPreprocessor = instruments.getTextPreprocessor();
		Map<String,String> hypothesisMap = new LinkedHashMap<String, String>();
		for (String hypothesisId : topicDataSet.getHypothesisMap().keySet())
		{
			String hypothesisSentence = topicDataSet.getHypothesisMap().get(hypothesisId);
			textPreprocessor.setText(hypothesisSentence);
			textPreprocessor.preprocess();
			hypothesisMap.put(hypothesisId, textPreprocessor.getPreprocessedText());
		}
		Map<String,Map<Integer, String>> documentsMap = new LinkedHashMap<String, Map<Integer,String>>();
		Map<String,DocumentMetaData> documentsMetaData = new LinkedHashMap<String, DocumentMetaData>();
		Map<String,Map<Integer, String>> originalDocumentsMap = topicDataSet.getDocumentsMap();
		Map<String,DocumentMetaData> originalDocumentsMetaData = topicDataSet.getDocumentsMetaData();
		for (String documentId : originalDocumentsMap.keySet())
		{
			Map<Integer, String> document = new LinkedHashMap<Integer, String>();
			for (Integer sentenceIndex : originalDocumentsMap.get(documentId).keySet())
			{
				String sentence = originalDocumentsMap.get(documentId).get(sentenceIndex);
				textPreprocessor.setText(sentence);
				textPreprocessor.preprocess();
				document.put(sentenceIndex, textPreprocessor.getPreprocessedText());
			}
			documentsMap.put(documentId, document);
			
			DocumentMetaData originalDocumentMetaData = originalDocumentsMetaData.get(documentId);
			if (null==originalDocumentMetaData) throw new TeEngineMlException("Null metadata for document "+documentId);
			textPreprocessor.setText(originalDocumentMetaData.getHeadline());
			textPreprocessor.preprocess();
			String headline = textPreprocessor.getPreprocessedText();
			documentsMetaData.put(documentId,new DocumentMetaData(originalDocumentMetaData.getDocId(), originalDocumentMetaData.getType(), headline, originalDocumentMetaData.getDateline()));
		}
		
		TopicDataSet normalizedTopicDataSet = new TopicDataSet(topicDataSet.getTopicId(), topicDataSet.getCandidatesMap(), hypothesisMap, documentsMap, documentsMetaData);
		
		this.topicDataSet = normalizedTopicDataSet;
		logger.info("Text normalization done.");
	}
	
	
	private TopicDataSet topicDataSet;
	private Instruments<Info, BasicNode> instruments; // initialized
	private boolean recognizeNameEntities = true;
	private boolean doTextNormalization = true;
	
	private PreprocessedTopicDataSet preprocessedTopicDataSet;
	
	private static final String TERMINATING_PUNCTUATION_REGEX = "[\\.!?]";
	private static final String PERIOD = ".";
	
	private static final Logger logger = Logger.getLogger(TopicPreProcessor.class);
}
 
