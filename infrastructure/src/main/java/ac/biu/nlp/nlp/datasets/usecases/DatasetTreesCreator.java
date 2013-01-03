package ac.biu.nlp.nlp.datasets.usecases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ac.biu.nlp.nlp.datasets.DefaultRTEMainReader;
import ac.biu.nlp.nlp.datasets.RTEClassificationType;
import ac.biu.nlp.nlp.datasets.RTEMainReader;
import ac.biu.nlp.nlp.datasets.RTEMainReaderException;
import ac.biu.nlp.nlp.datasets.TextHypothesisPair;
import ac.biu.nlp.nlp.general.ExceptionUtil;
import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.minipar.AbstractMiniparParser;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparClientParser;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparParser;
import ac.biu.nlp.nlp.instruments.parse.minipar.NormalizedMiniparParser;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.IdLemmaPosRelNodeAndEdgeString;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeDotFileGenerator;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeDotFileGenerator.TreeDotFileGeneratorException;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitterException;
import ac.biu.nlp.nlp.instruments.sentencesplit.nagel.NagelSentenceSplitter;


/**
 * Makes sentence split + parsing for all sentences in a
 * given RTE XML file (for RTE1-RTE5 main task data set).
 * 
 * The first argument should be the XML file name.
 * The second argument should be MiniPar argument.
 * When done, open terminal on the output directory, and run
 * <BR><code>
 * find . -name "*.dot" -exec 'dot' '-O' '-Tjpg' '{}' \;
 * </code><BR>
 * to get the the parse trees as JPEG files.
 * 
 * The output is written <B>to the current directory.</B>
 * <BR>
 * In addition, this application creates an HTML file with all sentences and trees.
 * 
 * 
 * @author Asher Stern
 *
 */
public class DatasetTreesCreator
{
	public static final String TEXT_TREE_MARKER = "t";
	public static final String HYPOTHESIS_TREE_MARKER = "h";
	public static final String YES_DIR_NAME = "entailment";
	public static final String NO_DIR_NAME = "no_entailment";
	public static final String HTML_FILE_EXTENSION = ".html";
	
	
	@SuppressWarnings("serial")
	public static class DatasetTreesCreatorException extends Exception
	{
		public DatasetTreesCreatorException(String message, Throwable cause){super(message, cause);}
		public DatasetTreesCreatorException(String message){super(message);}
	}
	
	public DatasetTreesCreator(String xmlFileName, String miniparArg)
	{
		this.xmlFileName = xmlFileName;
		this.miniparArg = miniparArg;
	}
	
	public void create() throws DatasetTreesCreatorException, ParserRunException, RTEMainReaderException, SentenceSplitterException, TreeDotFileGeneratorException, FileNotFoundException
	{
		xmlFile = new File(xmlFileName);
		if (!xmlFile.isFile()) throw new DatasetTreesCreatorException ("Not a file.");
		
		File miniparArgDataDir = new File(miniparArg);
		
		AbstractMiniparParser miniparParser = null;
		
		if (miniparArgDataDir.isDirectory())
		{
			miniparParser = new MiniparParser(miniparArg);
		}
		else
		{
			miniparParser = new MiniparClientParser(miniparArg);
		}
		parser = new NormalizedMiniparParser(miniparParser);
		
		parser.init();
		try
		{
			RTEMainReader reader = new DefaultRTEMainReader();
			reader.setXmlFile(xmlFile);
			reader.read();
			mapPairs = reader.getMapIdToPair();
			sentenceSplitter = new NagelSentenceSplitter();
			createAll();
		}
		finally
		{
			parser.cleanUp();
		}
		
		
		
		
	}
	
	
	/**
	 *
	 * The first argument should be the XML file name.
	 * The second argument should be output directory.
	 * When done, open terminal on the output directory, and run
	 * <BR><code>
	 * find . -name "*.dot" -exec 'dot' '-O' '-Tjpg' '{}' \;
	 * </code><BR>
	 * to get the the parse trees as JPEG files.
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<2) throw new DatasetTreesCreatorException("args");
			String xmlFileName = args[0];
			String miniparArg = args[1];
			DatasetTreesCreator creator = new DatasetTreesCreator(xmlFileName,miniparArg);
			creator.create();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
	}
	
	private void createAll() throws SentenceSplitterException, ParserRunException, TreeDotFileGeneratorException, DatasetTreesCreatorException, FileNotFoundException
	{
		File yesDir = new File(YES_DIR_NAME);
		File noDir = new File(NO_DIR_NAME);
		if (!yesDir.mkdir()) throw new DatasetTreesCreatorException("could not create directory: "+yesDir.getAbsolutePath());
		if (!noDir.mkdir()) throw new DatasetTreesCreatorException("could not create directory: "+noDir.getAbsolutePath());
		
		startHtmlFile();
		
		for (Integer id : mapPairs.keySet())
		{
			TextHypothesisPair pair = mapPairs.get(id);
			boolean entailment = pair.getBooleanClassificationType();
			File treesOutputDir = entailment?yesDir:noDir;
			sentenceSplitter.setDocument(pair.getText());
			sentenceSplitter.split();
			List<String> textSentences = sentenceSplitter.getSentences();
			List<String> textTreesFiles = new ArrayList<String>(textSentences.size());
			int index=1;
			for (String sentence : textSentences)
			{
				parser.setSentence(sentence);
				parser.parse();
				BasicNode tree = parser.getParseTree();
				String treeFileName = "t_"+id+"_"+index+".dot";
				writeTree(treesOutputDir,sentence, tree,treeFileName);
				textTreesFiles.add(treesOutputDir.getPath()+File.separator+treeFileName+".jpg");
				++index;
			}
			String hypothesisSentence = pair.getHypothesis();
			parser.setSentence(hypothesisSentence);
			parser.parse();
			BasicNode hypothesisTree = parser.getParseTree();
			String hypothesisTreeFileName = "h_"+id+".dot";
			writeTree(treesOutputDir,hypothesisSentence,hypothesisTree,hypothesisTreeFileName);
			
			writePair(id, textSentences, hypothesisSentence, pair.getClassificationType(), textTreesFiles, treesOutputDir.getPath()+File.separator+hypothesisTreeFileName+".jpg");
		}
		
		endHtmlFile();
		
		
	}
	
	private void writeTree(File dir,String sentence, BasicNode tree, String fileName) throws TreeDotFileGeneratorException
	{
		File outputFile = new File(dir,fileName);
		TreeDotFileGenerator<Info> tdfg = new TreeDotFileGenerator<Info>(new IdLemmaPosRelNodeAndEdgeString(),tree,sentence,outputFile);
		tdfg.generate();
	}
	
	
	// Write HTML methods
	private void startHtmlFile() throws FileNotFoundException
	{
		File htmlFile = new File(xmlFile.getName()+HTML_FILE_EXTENSION);
		htmlWriter = new PrintWriter(htmlFile);
		htmlWriter.println("<HTML>");
		htmlWriter.println("<BODY>");
	}
	
	private void endHtmlFile()
	{
		htmlWriter.println("</BODY>");
		htmlWriter.println("</HTML>");
		
		htmlWriter.close();
	}
	
	private void writePair(Integer id, List<String> textSentences, String hypothesis,RTEClassificationType classification, List<String> textTreesFiles, String hypothesisTreeFile)
	{
		htmlWriter.println("pair: "+id+" <B>"+classification.toString()+"</B><BR>");
		htmlWriter.println("text:<BR>");
		Iterator<String> textTreesIter = textTreesFiles.iterator();
		for (String textSentence : textSentences)
		{
			String textTreeFile = textTreesIter.next();
			htmlWriter.println("<A HREF=\""+textTreeFile+"\"> "+textSentence+"</A><BR>");
		}
		htmlWriter.println("<BR>hypothesis:<BR>");
		htmlWriter.println("<A HREF=\""+hypothesisTreeFile+"\"> "+hypothesis+"</A><BR>");
		htmlWriter.println("<HR>");
	}
	
	

	private String xmlFileName;
	private String miniparArg;
	private File xmlFile;
	private BasicParser parser;
	private Map<Integer,TextHypothesisPair> mapPairs;
	private SentenceSplitter sentenceSplitter;
	
	
	private PrintWriter htmlWriter;
	
	
}



