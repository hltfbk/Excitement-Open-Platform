/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.conll;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

	public static final String INPUT_FILE_INDICATOR = "-f";
	
	private static Logger logger = null;
	
	private static AnnotatedConllStringConverter CONLL_CONVERTER  = new AnnotatedConllStringConverter();
	private static SentenceSplitter SENTENCE_SPLITTER = new LingPipeSentenceSplitter();
	private EasyFirstParser parser;
	private DefaultSentenceAnnotator annotator;
	private final File conllOutputFolder;
	
	private ConfigurationParams annotationParams = null;

	/**
	 * Ctor
	 * @throws ConfigurationException 
	 * @throws ConllConverterException 
	 */
	public AnnotateSentenceToConll(ConfigurationFile confFile) throws ConfigurationException, ConllConverterException {
		
		confFile.setExpandingEnvironmentVariables(true);
		annotationParams = confFile.getModuleConfiguration(TransformationsConfigurationParametersNames.TRUTH_TELLER_MODULE_NAME); 

		try {
			annotator = new DefaultSentenceAnnotator(annotationParams);
			
			String posTaggerString = annotationParams.get(TransformationsConfigurationParametersNames.PREPROCESS_EASYFIRST);
			String easyFirstHost = annotationParams.get(TransformationsConfigurationParametersNames.PREPROCESS_EASYFIRST_HOST);
			int easyFirstPort = annotationParams.getInt(TransformationsConfigurationParametersNames.PREPROCESS_EASYFIRST_PORT);
			parser = new EasyFirstParser(easyFirstHost, easyFirstPort, posTaggerString);
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
	
	
	public List<String> getSentencesToAnnotate(String inputFileName) throws ConfigurationException, FileNotFoundException, IOException
	{
		List<String> sentences = new LinkedList<String>();
		File inputFile = new File(inputFileName);
		try(BufferedReader reader = new BufferedReader(new FileReader(inputFile)))
		{
			String line = reader.readLine();
			while (line !=null)
			{
				sentences.add(line);
				line = reader.readLine();
			}
		}
		return sentences;
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
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args)
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		logger = Logger.getLogger(AnnotateSentenceToConll.class);
		try
		{
			annotateByCommandLineArguments(args);
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
			logger.error("TruthTeller failed.",t);
		}
	}
	
	private static Iterable<String> getSentencesIterable(Iterator<String> argsIterator, AnnotateSentenceToConll app) throws FileNotFoundException, ConfigurationException, IOException, SentenceSplitterException
	{
		List<String> sentencesToAnnotate = null;
		
		
		String firstArgumentAfterConfigurationFile = null;
		if (argsIterator.hasNext())
		{
			firstArgumentAfterConfigurationFile = argsIterator.next();
		}
		
		
		if (INPUT_FILE_INDICATOR.equalsIgnoreCase(firstArgumentAfterConfigurationFile))
		{
			if (argsIterator.hasNext())
			{
				sentencesToAnnotate = app.getSentencesToAnnotate(argsIterator.next());
			}
			else
			{
				throw new RuntimeException("No input file is given, though \""+INPUT_FILE_INDICATOR+"\" has been encountered as a command line argument.");
			}
		}
		else
		{
			// Read the text from command line
			StringBuffer sbInputWords = new StringBuffer();
			
			if (firstArgumentAfterConfigurationFile!=null)
			{
				sbInputWords.append(firstArgumentAfterConfigurationFile);
				while (argsIterator.hasNext())
				{
					sbInputWords.append(" ");
					sbInputWords.append(argsIterator.next());
				}
			}
			
//			List<String> listOfWords = Utils.arrayToCollection(args, new Vector<String>());
//			listOfWords.remove(0);	// remove the confFile parameter
//			listOfWords.remove(1); // remove the pos-tagger-file-name
//			String text = StringUtil.joinIterableToString(listOfWords, " ");
			
			String text = sbInputWords.toString();
			
			SENTENCE_SPLITTER.setDocument(text);
			SENTENCE_SPLITTER.split();
			sentencesToAnnotate = SENTENCE_SPLITTER.getSentences();
		}
		
		return sentencesToAnnotate;
	}
		
		
	private static void annotateByCommandLineArguments(String[] args) throws AnnotatorException, ConfigurationFileDuplicateKeyException, ConfigurationException, ParserRunException, ConllConverterException, SentenceSplitterException, FileNotFoundException, IOException
	{
		if (args.length < (1))
			throw new AnnotatorException(String.format("usage: %s configurationFile.xml sentence(s)", AnnotateSentenceToConll.class.getSimpleName()));
		
		List<String> argsList = Utils.arrayToCollection(args, new Vector<String>());
		Iterator<String> argsIterator = argsList.iterator();
		
		ConfigurationFile confFile = new ConfigurationFile(new File(argsIterator.next()));
		confFile.setExpandingEnvironmentVariables(true);
		AnnotateSentenceToConll app = new AnnotateSentenceToConll(confFile);
		

		Iterable<String> sentencesToAnnotate = getSentencesIterable(argsIterator,app);

		List<ExtendedNode> list = new ArrayList<ExtendedNode>();
		for (String sentence : sentencesToAnnotate)
		{
			ExtendedNode annotatedSentece =  app.annotateSentece(sentence);
			list.add(annotatedSentece);
		}
		AnnotatedTreeToConllCoverter.treesToConllFiles(list, app.conllOutputFolder, CONLL_CONVERTER);	
	}
}
