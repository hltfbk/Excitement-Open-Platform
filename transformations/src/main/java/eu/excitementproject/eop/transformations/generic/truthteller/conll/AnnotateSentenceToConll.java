/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.conll;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.LingPipeSentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.DefaultSentenceAnnotator;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * @author Amnon Lotan
 *
 * @since Jul 18, 2012
 */
public class AnnotateSentenceToConll {

	private static AnnotatedConllStringConverter CONLL_CONVERTER  = new AnnotatedConllStringConverter();
	private static SentenceSplitter SENTENCE_SPLITTER = new LingPipeSentenceSplitter();
	private EasyFirstParser parser;
	private DefaultSentenceAnnotator annotator;
	private final File conllOutputFolder;

	/**
	 * Ctor
	 * @throws ConfigurationException 
	 * @throws ConllConverterException 
	 */
	public AnnotateSentenceToConll(ConfigurationFile confFile, String posTaggerString) throws ConfigurationException, ConllConverterException {
		
		confFile.setExpandingEnvironmentVariables(true);
		ConfigurationParams annotationParams = confFile.getModuleConfiguration(TransformationsConfigurationParametersNames.TRUTH_TELLER_MODULE_NAME); 

		try {
			annotator = new DefaultSentenceAnnotator(annotationParams);
			
//			String posTaggerString = 
//				confFile.getModuleConfiguration(ConfigurationParametersNames.RTE_SUM_PREPROCESS_MODULE_NAME).get(ConfigurationParametersNames.PREPROCESS_EASYFIRST);
			parser = new EasyFirstParser(posTaggerString);
			parser.init();
		} catch (Exception e) {
			throw new ConllConverterException("see nested", e);
		}
		
		String conllOutputFolderPath = annotationParams.get(TransformationsConfigurationParametersNames.CONLL_FORMAT_OUTPUT_DIRECTORY);
		conllOutputFolder = new File(conllOutputFolderPath);
		conllOutputFolder.mkdirs();
	}
	
	
	/**
	 * Get some text, sentence split it, and return 
	 * @param sentence
	 * @return
	 * @throws ConllConverterException
	 */
	public String textToAnnotatedConllFiles(String sentence) throws ConllConverterException
	{
		ExtendedNode annotatedSentece = annotateSentece(sentence);
		String conllString = AnnotatedTreeToConllCoverter.treeToConll(annotatedSentece , CONLL_CONVERTER);
		return conllString;
	}
	
	/**
	 * Get a single sentence, annotate it, and return its string CoNLL representation.
	 * @param sentence
	 * @return
	 * @throws ConllConverterException
	 */
	public String sentenceToAnnotatedConllString(String sentence) throws ConllConverterException
	{
		ExtendedNode annotatedSentece = annotateSentece(sentence);
		String conllString = AnnotatedTreeToConllCoverter.treeToConll(annotatedSentece , CONLL_CONVERTER);
		return conllString;
	}
	
	private ExtendedNode annotateSentece(String sentence) throws ConllConverterException
	{
			parser.setSentence(sentence);
		ExtendedNode annotatedSentece;
		try {
			parser.parse();
			BasicNode parsedTree = parser.getParseTree();
			ExtendedNode extendedTree = TreeUtilities.copyFromBasicNode(parsedTree);
			annotator.setTree(extendedTree);
			annotator.annotate();
			annotatedSentece = annotator.getAnnotatedTree();
		} catch (Exception e) {
			throw new ConllConverterException("see nested", e);
		}
		return annotatedSentece;
	}
	
	/**
	 * Command Line DEMO for the TruthTeller: get the configuration file and text sentence(s), annotate the sentences and print each one in CoNLL format to a separate file.  
	 * 
	 * @param args
	 * @throws AnnotatorException 
	 * @throws ConfigurationException 
	 * @throws ConfigurationFileDuplicateKeyException 
	 * @throws ParserRunException 
	 * @throws ConllConverterException 
	 * @throws SentenceSplitterException 
	 */
	public static void main(String[] args) throws AnnotatorException, ConfigurationFileDuplicateKeyException, ConfigurationException, ParserRunException, ConllConverterException, SentenceSplitterException {

		if (args.length < (1+1))
			throw new AnnotatorException("usage: EntailmentRuleCompiler configurationFile.xml <pos-tagger-module-filename> sentence(s)");
		
		List<String> argsList = Utils.arrayToCollection(args, new Vector<String>());
		Iterator<String> argsIterator = argsList.iterator();
		
		ConfigurationFile confFile = new ConfigurationFile(new File(argsIterator.next()));
		confFile.setExpandingEnvironmentVariables(true);
		String posTaggerModuleFileName = argsIterator.next();
		
		AnnotateSentenceToConll app = new AnnotateSentenceToConll(confFile,posTaggerModuleFileName);
		
		// Read the text from command line
		StringBuffer sbInputWords = new StringBuffer();
		boolean firstIteration = true;
		while (argsIterator.hasNext())
		{
			if (firstIteration) firstIteration=false;
			else sbInputWords.append(" ");
			
			sbInputWords.append(argsIterator.next());
		}
		
//		List<String> listOfWords = Utils.arrayToCollection(args, new Vector<String>());
//		listOfWords.remove(0);	// remove the confFile parameter
//		listOfWords.remove(1); // remove the pos-tagger-file-name
//		String text = StringUtil.joinIterableToString(listOfWords, " ");
		
		String text = sbInputWords.toString();
		
		SENTENCE_SPLITTER.setDocument(text);
		SENTENCE_SPLITTER.split();
		List<ExtendedNode> list = new ArrayList<ExtendedNode>();
		for (String sentence : SENTENCE_SPLITTER.getSentences())
		{
			ExtendedNode annotatedSentece =  app.annotateSentece(sentence);
			list.add(annotatedSentece);
		}
		AnnotatedTreeToConllCoverter.treesToConllFiles(list, app.conllOutputFolder, CONLL_CONVERTER);	
	}
}
