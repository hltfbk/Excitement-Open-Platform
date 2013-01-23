package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import arkref.analysis.FindMentions;
import arkref.analysis.Preprocess;
import arkref.analysis.RefsToEntities;
import arkref.data.Document;
import arkref.data.EntityGraph;
import arkref.data.Mention;
import arkref.data.Sentence;
import edu.stanford.nlp.trees.Tree;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.merge.WordWithCoreferenceTag;


/**
 * Used as client to Arkref system for fetching co-reference information
 * for a given text, and
 * return them as List (<code>java.util.List</code>) of {@link WordWithCoreferenceTag}.
 * <p>
 * About Arkref, see http://www.ark.cs.cmu.edu/ARKref/
 * <p>
 * <b>NOTE!</b> for arkref to work, you must have <code>JARS\arkref\lib</code> and <code>JARS\arkref\config</code> in the
 * work dir. 
 * 
 * 
 * 
 * 
 * @author Lili Kotlerman & Asher Stern
 *
 */
public class ArkrefClient
{
	//////////////////// PUBLIC PART /////////////////////////////
	
	// CONSTANTS
	@Deprecated
	public static final String DEFAULT_ARGUMENT = "-input";

	// EXCEPTION CLASS
	@SuppressWarnings("serial")
	public static class ArkrefClientException extends Exception
	{
		public ArkrefClientException(String str){super(str);}
		public ArkrefClientException(String str,Throwable t){super(str,t);}
	}

	// CONSTRUCTOR & METHODS
	
	
	/**
	 * Constructs {@link ArkrefClient} with the text as string.
	 * @param text the text to be processed by Arkref.
	 * @param filename the file to be created as an input file for Arkref.
	 */
	public ArkrefClient(String text, String filename) throws ArkrefClientException
	{
		if (!isValidInputFilename(filename)) throw new ArkrefClientException("Cannot create ArkrefClient - filename should end with '.txt'");
		this.text = text;
		this.inputTextFilename=filename;
	}
	
	
	/**
	 * Calls Arkref, retrieving its output, and creates the list of
	 * {@link WordWithCoreferenceTag} to be returned by {@link #getArkrefOutput()}.
	 * 
	 * @throws ArkrefClientException
	 * @throws CoreferenceResolutionException 
	 */
	public void process() throws ArkrefClientException, CoreferenceResolutionException
	{
		try
		{
			////// A workaround due to an ArkRef bug.
			// this.text = StringUtil.trimSentenceRight(this.text);
			//////
			
			// Write the text into a text file
			createDocument();
			
			// Call ArkRef's methods
			doArkrefProcessing();
			
			// Methods no-longer-in-use
//			processInputDocument();
//			analyzeOutputDocument();
			
			processDone = true;			
		}
		catch(IOException e)
		{
			throw new ArkrefClientException("Technical problem using Arkref. See nested exception",e);
		}
	}
	
	/**
	 * Returns the Arkref's output, as list of {@link WordWithCoreferenceTag}.
	 * First call {@link #process()} method. Then call this method.
	 * @return
	 * @throws ArkrefClientException
	 */
	public List<WordWithCoreferenceTag> getArkrefOutput() throws ArkrefClientException
	{
		if (processDone)
			return this.arkrefOutput;
		else
			throw new ArkrefClientException("process() was not called.");
	}

	
	public List<WordAndStackTags> getArkrefStackOutput()
	{
		return arkrefStackOutput;
	}
	
		
	////////////////// PROTECTED & PRIVATE PART //////////////////////
	


	protected void doArkrefProcessing() throws IOException, ArkrefClientException
	{
		synchronized(ArkrefClient.class) // ArkRef uses static methods, thus it is unsafe to have two instances of ArkrefClient work together (in parallel).
		{
			Document arkrefDocument = null;
			try
			{
				File file = new File(this.inputTextFilename);
				String path = file.getPath();

				// Call ArkRef Classes and methods
				path = Preprocess.shortPath(path);
				Preprocess.go(path);
				arkrefDocument = Document.loadFiles(path);
				FindMentions.go(arkrefDocument);
				eu.excitementproject.eop.lap.biu.en.coreference.arkref.workaround.Resolve.go(arkrefDocument);
				RefsToEntities.go(arkrefDocument);
			}
			catch(RuntimeException e)
			{
				throw new ArkrefClientException("ArkRef failed due to a runtime exception. Text is:\n"+this.text+"\nSee nested exception.",e);
			}

			// Process ArkRef's result, to get List<WordWithCoreferenceTag>
			this.arkrefOutput = new ArrayList<WordWithCoreferenceTag>();
			String leafString = null;
			Stack<String> currentTags = null;
			try
			{
				EntityGraph entityGraph = arkrefDocument.entGraph();
				for(Sentence sentence : arkrefDocument.sentences()) // for each sentence
				{
					LinkedHashMap<Tree, WordAndStackTags> mapLeavesToWords = new LinkedHashMap<Tree, WordAndStackTags>();
					currentTags = new Stack<String>();

					for(Tree leaf : sentence.rootNode().getLeaves()) // for each word in the sentence (Arkref uses constituency trees, in which all the words are in the leaves)
					{
						leafString = leaf.nodeString();
						Map<Mention, Integer> mapMentionToSpanOfLeaves = new LinkedHashMap<Mention, Integer>();
						
						// Find all mentions the begin in the current leaf
						for (Mention mention : arkrefDocument.mentions())
						{
							List<Tree> mentionLeaves = mention.node().getLeaves();
							if(mentionLeaves.get(0) == leaf) // This mention begins in the current leaf
							{
								mapMentionToSpanOfLeaves.put(mention, mentionLeaves.size()); // put the mention and number of words that are in this mention in the map
							}
						}
						
						// Create a list of all mentions that begin in the current leaf, sorted by size of number of words in the mention.
						List<Mention> sortedMentions = Utils.getSortedByValue(mapMentionToSpanOfLeaves);
						Collections.reverse(sortedMentions); // now it is from largest to smallest
						
						// For each mention that begins in this leaf, push the mention's tag into the stack.
						for (Mention mention : sortedMentions)
						{
							String tag = entityGraph.entName(mention);
							currentTags.push(tag);
						}

						// Create a copy of the stack for the current word (the current leaf).
						Stack<String> currentLeafStack = new Stack<String>();
						currentLeafStack.addAll(currentTags);

						// Add the word-string and the stack to the map.
						mapLeavesToWords.put(leaf,new WordAndStackTags(leaf.nodeString(),currentLeafStack));


						// Find all mentions that end in the current leaf (current word).
						for (Mention mention : arkrefDocument.mentions())
						{
							List<Tree> mentionLeaves = mention.node().getLeaves();
							if(mentionLeaves.get(mentionLeaves.size()-1) == leaf)
							{
								// If this mention ends in this leaf (this word), remove it (its tag) from the stack.
								currentTags.pop();
							}
						}
					}

					// Create a list of WordWithCoreferenceTag - the coreference tag is the one at the top of the stack
					// which refers to the mention with the smallest number of words (among all mentions that cover this word).
					for (Tree leaf : mapLeavesToWords.keySet())
					{
						String tag = mapLeavesToWords.get(leaf).getTags().isEmpty()?null:mapLeavesToWords.get(leaf).getTags().peek();
						this.arkrefOutput.add(new WordWithCoreferenceTag(leaf.nodeString(), tag));
					}

					// For debugging
					arkrefStackOutput = new ArrayList<WordAndStackTags>(mapLeavesToWords.keySet().size());
					for (Tree leaf : mapLeavesToWords.keySet())
					{
						arkrefStackOutput.add(mapLeavesToWords.get(leaf));
					}
				}
			}
			catch(RuntimeException e) // if run-time exception has been thrown - throw a new exception with very detailed information.
			{
				StringBuffer sb = new StringBuffer();
				sb.append("ArkRef client failed due to runtime exception. See nested.");
				if (leafString!=null)
				{
					sb.append("\nFailed on Node: \"").append(leafString).append("\"\n");
				}
				if (currentTags!=null)
				{
					sb.append("Current tags:\n");
					ArrayList<String> listTags = new ArrayList<String>(currentTags.size());
					listTags.addAll(currentTags);
					Collections.reverse(listTags);
					for (String tag : listTags)
					{
						sb.append(tag).append("\n");
					}
				}
				if (this.arkrefOutput!=null)
				{
					sb.append("\nCurrent arkrefOutput is:\n");
					for (WordWithCoreferenceTag wwct : arkrefOutput)
					{
						sb.append(wwct.getWord()).append("/").append(wwct.getCoreferenceTag()).append("\n");
					}
				}
				throw new ArkrefClientException(sb.toString(),e);
			} // end of catch block
		} // end of synchronized(ArkrefClient.class)
	}
	
	/**
	 * Creates a "txt" file {@link #inputTextFilename} and writes the text {@link #text} into this file. The file will be further processed by Arkref. 
	 * @return
	 * @throws IOException
	 */	
	protected void createDocument() throws IOException
	{
		BufferedWriter docWriter = new BufferedWriter(new FileWriter(this.inputTextFilename));
		try
		{
			docWriter.write(this.text);
		}
		finally
		{
			docWriter.close();
		}
	}
	
//	/**
//	 * Applies Arkref to the "txt" file {@link #inputTextFilename} created by {@link #createDocument()}.
//	 * As a result, an output file is created. It is placed in the same directory as {@link #inputTextFilename} file and has the same name, with extension ".tagged" instead of ".txt"
//	 * @return
//	 * @throws ArkrefClientException
//	 */
//	protected void processInputDocument() throws ArkrefClientException
//	{
//		String[] arguments = {DEFAULT_ARGUMENT, this.inputTextFilename};
//		try {
//			ARKref.main(arguments);
//		} catch (Exception e) {
//			throw new ArkrefClientException("Problem with ARKref processing file '"+ this.inputTextFilename+"'. See nested exception",e);
//		}
//	}
//
//	/**
//	 * Reads the document {@link #document}, which contains the lines of Arkref output file in xml format.
//	 * Extracts coreference information from the document and saves it to {@link #arkrefOutput}.
//	 * @return
//	 * @throws ArkrefClientException
//	 * @throws CoreferenceResolutionException 
//	 */	
//	protected void analyzeOutputDocument() throws ArkrefClientException, CoreferenceResolutionException
//	{
//		try
//		{
//			StringBuffer sb = new StringBuffer();
//			BufferedReader reader = new BufferedReader(new FileReader(getOutputFilename()));
//			try
//			{
//				for (String line = reader.readLine();line!=null;line = reader.readLine())
//				{
//					// System.out.println(line);
//					sb.append(line).append(" ");
//				}
//			}
//			finally
//			{
//				reader.close();
//			}
//			
//			
//
//			ArkrefOutputParser parser = new ArkrefOutputParser(sb.toString());
//			parser.parse();
//			this.arkrefOutput = parser.getListOfWordsWithTags();
//		}
//		catch(IOException e)
//		{
//			throw new ArkrefClientException("Failed due to IO exception.",e);
//
//		}
//
//	}
//
//	
//	
//	/**
//	 * Reads the document {@link #document}, which contains the lines of Arkref output file in xml format.
//	 * Extracts coreference information from the document and saves it to {@link #arkrefOutput}.
//	 * @return
//	 * @throws ArkrefClientException
//	 */	
//	protected void analyzeOutputDocumentOld() throws ArkrefClientException
//	{
//		this.arkrefOutput = new LinkedList<WordWithCoreferenceTag>();
//		createXml();
//		NodeList n = this.document.getElementsByTagName("mention");
//		for (int i=0; i<n.getLength(); i++){
//			Node mention = n.item(i);
//			String word=mention.getTextContent();
//			String coreferenceTag = mention.getAttributes().getNamedItem("entityid").getNodeValue();
//			for (String subword : word.split(" ")){
//			//	System.out.println(coreferenceTag+"\t"+subword);		// uncomment me to get a good idea of what the coref is digging up
//				this.arkrefOutput.add(new WordWithCoreferenceTag(subword, coreferenceTag));				
//			}
//		}
//	}
//
//	/**
//	 * Reads the Arkref output file, which is placed in the same directory as {@link #inputTextFilename} file and has the same name, with extension ".tagged" instead of ".txt".
//	 * @return lines of the Arkref output file in xml format
//	 * @throws ArkrefClientException
//	 */	
//	protected String getOutputLines() throws ArkrefClientException
//	{
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(getOutputFilename()));
//			StringBuffer ret = new StringBuffer();
//			ret.append("<xml>");
//			String line = reader.readLine();
//			while (line !=null)
//			{
//				ret.append(line);
//				ret.append("\n");
//				line = reader.readLine();
//			}
//			reader.close();
//			ret.append("</xml>");
//			return ret.toString();
//		} catch (FileNotFoundException e) {
//			throw new ArkrefClientException("Arkref cannot find output file. See nested exception",e);
//		}catch (IOException e) {
//		throw new ArkrefClientException("Arkref cannot read from output file. See nested exception",e);
//		}
//	}
//
//	/**
//	 * Gets the lines of the Arkref output file in xml format and creates a document {@link #document}, containing these lines.
//	 * @return 
//	 * @throws ArkrefClientException
//	 */		
//	protected void createXml() throws ArkrefClientException{
//		try {
//				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();		
//				String xml = getOutputLines();
//				StringReader reader = new StringReader(xml);
//				InputSource inputSource = new InputSource(reader);
//				this.document = factory.newDocumentBuilder().parse(inputSource);
//				reader.close();
//		} catch (SAXException e) {
//			throw new ArkrefClientException("Technical problem using Arkref. See nested exception",e);
//		} catch (IOException e) {
//			throw new ArkrefClientException("Technical problem using Arkref. See nested exception",e);
//		} catch (ParserConfigurationException e) {
//			throw new ArkrefClientException("Technical problem using Arkref. See nested exception",e);
//		}
//	}
	
	/**
	 * Verifies whether a given filename ends with ".txt". Needed since Arkref accepts ".txt" files as its input.
	 * @param filename a filename to verify 
	 * @return true if the filename ends with ".txt", otherwise false
	 */		
	protected boolean isValidInputFilename(String filename){
		if (filename.endsWith(".txt")) return true;
		else return false;
	}
	
//	/** Returns the name of the Arkref output file, which is placed in the same directory as {@link #inputTextFilename} file and has the same name, with extension ".tagged" instead of ".txt".
//	 * @return Arkref output filename
//	 */
//	protected String getOutputFilename(){
//		String res=this.inputTextFilename;
//		res=res.substring(0, res.length()-4);
//		res+=".tagged";
//		return res;
//	}
	
	
	
	
	protected String text;
	protected String inputTextFilename;
	
	// protected boolean cleanXml = true;
	
//	protected org.w3c.dom.Document document;
	// protected Stack<String> stackCoreferenceTags = new Stack<String>();
	
	
	
	protected List<WordWithCoreferenceTag> arkrefOutput = null;
	protected List<WordAndStackTags> arkrefStackOutput = null;	
	protected boolean processDone = false;
	
}
