package eu.excitementproject.eop.biutee.rteflow.systems.rtepairs;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.preprocess.Instruments;
import eu.excitementproject.eop.biutee.utilities.DoNothingShortMessageFire;
import eu.excitementproject.eop.biutee.utilities.ShortMessageFire;
import eu.excitementproject.eop.biutee.utilities.preprocess.ValidateTexts;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.PreprocessUtilities;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolver;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * Used by {@link PairsPreProcessor} to make pre-processing for a single
 * {@link TextHypothesisPair}.
 * 
 * @author Asher Stern
 * @since May 28, 2011
 *
 */
public class SinglePairPreProcessor
{
	/**
	 * 
	 * @param pair
	 * @param makingTextNormalization
	 * @param processingNamedEntities
	 * @param initializedInstruments - <B> must be already initialized! </B>
	 */
	public SinglePairPreProcessor(TextHypothesisPair pair,
			boolean makingTextNormalization, boolean processingNamedEntities,
			Instruments<Info, BasicNode> initializedInstruments)
	{
		this(pair,makingTextNormalization,processingNamedEntities,initializedInstruments,new DoNothingShortMessageFire());
	}

	/**
	 * 
	 * @param pair
	 * @param makingTextNormalization
	 * @param processingNamedEntities
	 * @param initializedInstruments - <B> must be already initialized! </B>
	 * @param messageFire
	 */
	public SinglePairPreProcessor(TextHypothesisPair pair,
			boolean makingTextNormalization, boolean processingNamedEntities,
			Instruments<Info, BasicNode> initializedInstruments, ShortMessageFire messageFire)
	{
		super();
		this.pair = pair;
		this.makingTextNormalization = makingTextNormalization;
		this.processingNamedEntities = processingNamedEntities;
		this.initializedInstruments = initializedInstruments;
		this.messageFire = messageFire;
	}


	public void preprocess() throws TextPreprocessorException, SentenceSplitterException, NamedEntityRecognizerException, ParserRunException, CoreferenceResolutionException, TreeCoreferenceInformationException, TreeStringGeneratorException
	{
		logger.info("Pre process of pair: "+pair.getId());

//		if (makingTextNormalization)
//		{
//			messageFire.fire("Performing text normalization");
//			pair = makeTextNormalization(pair);
//		}

		if (makingTextNormalization)
		{
			messageFire.fire("Normalizing hypothesis");
			initializedInstruments.getTextPreprocessor().setText(pair.getHypothesis());
			initializedInstruments.getTextPreprocessor().preprocess();
			normalizedHypothesis = initializedInstruments.getTextPreprocessor().getPreprocessedText();
			if (logger.isDebugEnabled())
			{
				logger.debug("Normalized hypotehsis");
				logger.debug(normalizedHypothesis);
			}
		}


		messageFire.fire("Performing sentence split");
		SentenceSplitter sentenceSplitter = initializedInstruments.getSentenceSplitter();
		final String text = pair.getText();
		sentenceSplitter.setDocument(text);
		sentenceSplitter.split();
		List<String> sentences = sentenceSplitter.getSentences();
		ValidateTexts validatorSentenceSplitter = ValidateTexts.createForSentences(text, sentences);
		if (!(validatorSentenceSplitter.compare()))
		{
			logger.warn("Text was modified by the sentence splitter.\n" +
					"Original text:\n" +
					validatorSentenceSplitter.getOriginalText_noDuplicateSpaces() +
					"\nText after sentence splitter:\n" +
					validatorSentenceSplitter.getGeneratedText_noDuplicateSpaces()+"\n"+
					ValidateTexts.generateMismatchMarks(validatorSentenceSplitter.getMismatches())
					);
		}
		
		if (logger.isDebugEnabled())
		{
			int index=1;
			for (String sentence : sentences)
			{logger.debug("#"+index+": "+sentence);++index;}
		}
		
		if (makingTextNormalization)
		{
			messageFire.fire("Normalizing text");
			List<String> normalizedSentences = new ArrayList<String>(sentences.size());
			for (String sentence : sentences)
			{
				initializedInstruments.getTextPreprocessor().setText(sentence);
				initializedInstruments.getTextPreprocessor().preprocess();
				normalizedSentences.add(initializedInstruments.getTextPreprocessor().getPreprocessedText());
			}
			sentences = normalizedSentences;
			
			if (logger.isDebugEnabled())
			{
				logger.debug("Text sentences after normalization:");
				int index=1;
				for (String sentence : sentences)
				{logger.debug("#"+index+": "+sentence);++index;}
			}
		}


		
		textTrees = new ArrayList<BasicNode>(sentences.size());
		mapTreesToSentences = new LinkedHashMap<BasicNode, String>();

		messageFire.fire("Parsing");
		for (String textSentence : sentences)
		{
			if (logger.isDebugEnabled()){logger.debug("Parsing sentence: \""+textSentence+"\"");}
			BasicNode tree = PreprocessUtilities.generateParseTree(textSentence, initializedInstruments.getParser(), initializedInstruments.getNamedEntityRecognizer(), processingNamedEntities);
			textTrees.add(tree);
			mapTreesToSentences.put(tree, textSentence);
		}

		messageFire.fire("Resolving coreference");
		CoreferenceResolver<BasicNode> coreferenceResolver = initializedInstruments.getCoreferenceResolver();
		String textAfterNormalization = StringUtil.joinIterableToString(sentences, " ", true);
		coreferenceResolver.setInput(textTrees, textAfterNormalization);
		coreferenceResolver.resolve();
		coreferenceInformation = coreferenceResolver.getCoreferenceInformation();
		
		
		// Asher 30-May-2011: Should this be done?
		// Asher 31-May-2011: Yes. To make a single operation of substitution
		// instead of two: from a node to the co-referring node, and then to
		// the "virtual" node created by the parser.
		PreprocessUtilities.integrateParserAntecedentToCoreference(textTrees, coreferenceInformation);

		if (logger.isDebugEnabled())
		{
			StringBuilder sb = new StringBuilder();
			for (Integer groupId : coreferenceInformation.getAllExistingGroupIds())
			{
				sb.append(groupId+": ");
				for (BasicNode node : coreferenceInformation.getGroup(groupId))
				{
					sb.append(InfoGetFields.getLemma(node.getInfo())+", ");
				}
				sb.append("\n");
			}

		}
		
		messageFire.fire("Parsing hypothesis");
		hypothesisTree = PreprocessUtilities.generateParseTree(normalizedHypothesis, initializedInstruments.getParser(), initializedInstruments.getNamedEntityRecognizer(), processingNamedEntities);
		
		// Log everything, if in debug mode
		if (logger.isDebugEnabled())
		{
			logDebugTheResult();
		}
	}
	
	
	public List<BasicNode> getTextTrees() throws TeEngineMlException
	{
		if (null==textTrees)throw new TeEngineMlException("Null textTrees. Seems that preprocess() method was not called.");
		return textTrees;
	}


	public Map<BasicNode, String> getMapTreesToSentences() throws TeEngineMlException
	{
		if (null==mapTreesToSentences)throw new TeEngineMlException("Null mapTreesToSentences. Seems that preprocess() method was not called.");
		return mapTreesToSentences;
	}


	public TreeCoreferenceInformation<BasicNode> getCoreferenceInformation() throws TeEngineMlException
	{
		if (null==coreferenceInformation)throw new TeEngineMlException("Null coreferenceInformation. Seems that preprocess() method was not called.");
		return coreferenceInformation;
	}


	public BasicNode getHypothesisTree() throws TeEngineMlException
	{
		if (null==hypothesisTree)throw new TeEngineMlException("Null hypothesisTree. Seems that preprocess() method was not called.");
		return hypothesisTree;
	}

	/////////////////////////// PROTECTED AND PRIVATE /////////////////////////////////////////

	@Deprecated
	protected TextHypothesisPair makeTextNormalization(TextHypothesisPair originalPair) throws TextPreprocessorException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Text before normalization:\n"+originalPair.getText());
		}
		if (logger.isDebugEnabled())
		{
			logger.debug("Hypothesis before normalization:\n"+originalPair.getHypothesis());
		}


		
		initializedInstruments.getTextPreprocessor().setText(originalPair.getText());
		initializedInstruments.getTextPreprocessor().preprocess();
		String normalizedText = initializedInstruments.getTextPreprocessor().getPreprocessedText();

		initializedInstruments.getTextPreprocessor().setText(originalPair.getHypothesis());
		initializedInstruments.getTextPreprocessor().preprocess();
		String normalizedHypothesis = initializedInstruments.getTextPreprocessor().getPreprocessedText();

		TextHypothesisPair normalizedPair = new TextHypothesisPair(normalizedText, normalizedHypothesis, originalPair.getId(), originalPair.getClassificationType() , originalPair.getAdditionalInfo());
		

		if (logger.isDebugEnabled())
		{
			logger.debug("Text after normalization:\n"+normalizedPair.getText());
		}
		if (logger.isDebugEnabled())
		{
			logger.debug("Hypothesis after normalization:\n"+normalizedPair.getHypothesis());
		}
		
		return normalizedPair;
	}
	
	private void logDebugTheResult() throws TreeCoreferenceInformationException, TreeStringGeneratorException
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\nText parse trees:\n");
		for (BasicNode tree : textTrees)
		{
			sb.append(TreeUtilities.treeToString(tree));
			sb.append("\n");
			sb.append(StringUtil.generateStringOfCharacter('-', 50));
			sb.append("\n");
		}
		sb.append("Hypothesis tree:\n");
		sb.append(TreeUtilities.treeToString(hypothesisTree));
		sb.append("\n");
		sb.append(StringUtil.generateStringOfCharacter('-', 50));
		sb.append("\n");
		
		sb.append("Coreference information:\n");
		for (Integer id : coreferenceInformation.getAllExistingGroupIds())
		{
			sb.append(id);
			sb.append(": ");
			
			for (BasicNode node : coreferenceInformation.getGroup(id))
			{
				sb.append(node.getInfo().getId());
				sb.append("-");
				sb.append(InfoGetFields.getLemma(node.getInfo()));
				sb.append(" ");
			}
			sb.append("\n");
		}
		
		logger.debug(sb.toString());
		
	}
	
	

	

	
	

	private TextHypothesisPair pair;
	private String normalizedHypothesis = null;
	private boolean makingTextNormalization = true;
	private boolean processingNamedEntities = true;
	private Instruments<Info, BasicNode> initializedInstruments; // initialized
	
	private ShortMessageFire messageFire;
	
	private List<BasicNode> textTrees = null;
	private Map<BasicNode,String> mapTreesToSentences = null;
	private TreeCoreferenceInformation<BasicNode> coreferenceInformation = null;
	private BasicNode hypothesisTree = null;

	
	private static Logger logger = Logger.getLogger(SinglePairPreProcessor.class);
}
