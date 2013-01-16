package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraphException;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraphFactory;



/**
 * Generates a {@link DirectedGraph} from the C&C parser's output.
 * <P>
 * Construct an object of {@linkplain CandCOutputToGraph} using the constructor
 * that receives the output as list of lines, then call {@link #generateGraph()},
 * then call {@link #getGraph()}.
 * @author Asher Stern
 *
 */
public class CandCOutputToGraph
{
	
	/////////////////////// CONSTANTS //////////////////////////////
	
	public static final String RELATION_LINE_BEGIN_MARKER = "(";
	public static final String RELATION_LINE_END_MARKER = ")";
	public static final String POS_TAGGER_LINE_BEGIN_MARKER = "<c>";
	public static final String RELATION_DELIMITER = "_";
	public static final String POS_TAGGER_DELIMITER = "\\|";
	public static final int POS_TAGGER_WORD_INDEX=0;
	public static final int POS_TAGGER_LEMMA_INDEX=1;
	public static final int POS_TAGGER_PART_OF_SPEECH_INDEX=2;
	public static final int INDEX_BEGIN = 0; 
	
	
	////////////////// PROTECTED AND PACKAGE LEVEL METHODS //////////////////////
	
	
	protected void extractRelevantOutputs() throws CandCMalformedOutputException
	{
		relationOutput = new LinkedList<String>();
		
		
		Iterator<String> iterCandcOutput = candcOutput.iterator();
		while (iterCandcOutput.hasNext())
		{
			String currentLine = iterCandcOutput.next();
			if (currentLine.startsWith(RELATION_LINE_BEGIN_MARKER))
			{
				if (currentLine.endsWith(RELATION_LINE_END_MARKER))
				{
					relationOutput.add(currentLine);
				}
				else
					throw new CandCMalformedOutputException("error relation line detected: "+currentLine);
			}
			else if (currentLine.startsWith(POS_TAGGER_LINE_BEGIN_MARKER))
				partOfSpeechOutput = currentLine;
		}
		
	}
	
	
	
	static boolean isWordUnderscoreNumber(String str)
	{
		boolean ret = false;
		if (str.contains(RELATION_DELIMITER))
		{
			String[] wordAndNumber = str.split(RELATION_DELIMITER);
			if (wordAndNumber.length==(1+1))
			{
				String number = wordAndNumber[1];
				boolean isNumber = true;
				if (number.length()==0) isNumber = false;
				for (int index=0;index<number.length();++index)
				{
					if (Character.isDigit(number.charAt(index))) ;
					else isNumber = false;
				}
				ret = isNumber;
			}
		}
		return ret;
	}
	
	static String getWordOfWordUnderscoreNumber(String str)
	{
		String ret = null;
		int underscoreIndex = str.lastIndexOf(RELATION_DELIMITER);
		if (underscoreIndex<0)
			ret = null;
		else
		{
			ret = str.substring(0,underscoreIndex);
		}
		return ret;
		
	}
	
	static Integer getNumberOfWordUnderscoreNumber(String str)
	{
		Integer ret = null;
		String[] wordAndNumber = str.split(RELATION_DELIMITER);
		if (wordAndNumber.length != (1+1))
			;
		else
		{
			String number = wordAndNumber[1];
			try
			{
				ret = Integer.parseInt(number);
			}
			catch(Exception e)
			{
				ret = null;
			}
		}
		return ret;
		
		
	}
	
	
	protected void addNodes() throws CandCMalformedOutputException, DirectedGraphException
	{
		if (null==partOfSpeechOutput)
			throw new CandCMalformedOutputException("no \"word-lemma-part of speech\" output exist.");
		
		if (!this.partOfSpeechOutput.startsWith(POS_TAGGER_LINE_BEGIN_MARKER))
			throw new CandCMalformedOutputException("\"word-lemma-part of speech\" output does not start with \""+POS_TAGGER_LINE_BEGIN_MARKER+"\"");

		String[] eachWordOutput = partOfSpeechOutput.substring(POS_TAGGER_LINE_BEGIN_MARKER.length()).trim().split(" ");
		
		int index = INDEX_BEGIN;
		for (String currentWordInfo : eachWordOutput)
		{
			String[] wordAndLemmaAndPartOfSpeechAndTheRest = currentWordInfo.split(POS_TAGGER_DELIMITER);
			
			if (wordAndLemmaAndPartOfSpeechAndTheRest.length<=POS_TAGGER_WORD_INDEX)
				throw new CandCMalformedOutputException("bad \"word-lemma-part of speech\" output: "+currentWordInfo);
			if (wordAndLemmaAndPartOfSpeechAndTheRest.length<=POS_TAGGER_LEMMA_INDEX)
				throw new CandCMalformedOutputException("bad \"word-lemma-part of speech\" output: "+currentWordInfo);
			if (wordAndLemmaAndPartOfSpeechAndTheRest.length<=POS_TAGGER_PART_OF_SPEECH_INDEX)
				throw new CandCMalformedOutputException("bad \"word-lemma-part of speech\" output: "+currentWordInfo);
			
			String word = wordAndLemmaAndPartOfSpeechAndTheRest[POS_TAGGER_WORD_INDEX];
			String lemma = wordAndLemmaAndPartOfSpeechAndTheRest[POS_TAGGER_LEMMA_INDEX];
			String partOfSpeech = wordAndLemmaAndPartOfSpeechAndTheRest[POS_TAGGER_PART_OF_SPEECH_INDEX];
			
			CCNode node = new CCNode(index, new CCNodeInfo(word, lemma, partOfSpeech));
			nodesMap.put(index, node);
			graph.addNode(node);
			
			++index;

		}
		
	}
	
	
	
	protected void addEdges() throws CandCMalformedOutputException, DirectedGraphException
	{
		if (null==this.relationOutput)
			throw new CandCMalformedOutputException("no relation output exist.");
		
		for (String relationOutputLine : relationOutput)
		{
			
			CCRelationLineParser lineParser =
				new CCRelationLineParser(relationOutputLine.substring(RELATION_LINE_BEGIN_MARKER.length(),relationOutputLine.length()-RELATION_LINE_END_MARKER.length() ));
			
			CCEdgeInfo edgeIfno = new CCEdgeInfo(lineParser.getGrType(), lineParser.getOptionalSubtype(), lineParser.getOptionalInitialGr());
			CCNode head = nodesMap.get(lineParser.getHeadNumber());
			CCNode dependent = nodesMap.get(lineParser.getDependentNumber());
			if (null==head)
				throw new CandCMalformedOutputException("head does not exist: "+lineParser.getHeadNumber());
			if (null==dependent)
				throw new CandCMalformedOutputException("dependent does not exist: "+lineParser.getDependentNumber());
			
			graph.addEdge(head, dependent, edgeIfno);
		}
		
	}
	
	
	
	///////////////////////////////// PUBLIC PART /////////////////////////////////
	

	
	/**
	 * Construct an object of {@link CandCOutputToGraph}, by getting the C&C parser's
	 * output as list of Strings - <B> each String is a line </B>.
	 * <P>
	 * A typical output is:
	 * <pre>
	 * (xcomp _ am_1 here_2)
	 * (ncsubj am_1 I_0 _)
	 * <c> I|I|PRP|I-NP|O|NP am|be|VBP|I-VP|O|(S[dcl]\NP)/(S[adj]\NP) here|here|RB|I-ADVP|O|S[adj]\NP .|.|.|O|O|.
	 * </pre>
	 * 
	 * @param cAndCOutput A list of Strings, each String is C&C parser's output line.
	 */
	public CandCOutputToGraph(List<String> cAndCOutput)
	{
		this.candcOutput = cAndCOutput;
	}
	
	
	/**
	 * Generates a {@link DirectedGraph} according to the output provided in the
	 * constructor
	 * @throws CandCMalformedOutputException
	 * @throws DirectedGraphException
	 */
	public void generateGraph() throws CandCMalformedOutputException, DirectedGraphException
	{
		extractRelevantOutputs();
		addNodes();
		addEdges();
	}
	
	/**
	 * Returns <code>true</code> if the C&C parser succeeded to parse
	 * the sentence - i.e. generated the relation information.
	 * <P>
	 * In other words: returns <code>true</code> if the graph has edges,
	 * and <code>false</code> if all of the vertices are isolated.
	 * 
	 * @return <code>true</code> if the graph has edges.
	 * <code>false</code> if all vertices are isolated.
	 */
	public boolean hasEdges()
	{
		boolean ret = false;
		if (this.relationOutput!=null)
			if (this.relationOutput.size()>0)
				ret = true;
		
		return ret;
	}
	
	/**
	 * Returns the {@link DirectedGraph} built by {@link #generateGraph()} method.
	 * @return
	 */
	public DirectedGraph<CCNode, CCEdgeInfo> getGraph()
	{
		return this.graph;
	}
	
	
	
	protected List<String> candcOutput;
	protected DirectedGraph<CCNode, CCEdgeInfo> graph =
		new DirectedGraphFactory<CCNode, CCEdgeInfo>().getDefaultDirectedGraph();

	
	protected Map<Integer,CCNode> nodesMap = new HashMap<Integer, CCNode>();

	protected List<String> relationOutput;
	protected String partOfSpeechOutput;

}
