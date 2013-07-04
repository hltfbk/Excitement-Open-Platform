package eu.excitementproject.eop.lap.biu.en.parser.easyfirst;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech.PennPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.postagger.stanford.MaxentPosTagger;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;

/**
 * This class basically wraps Yoav Goldberg's EasyFirst parser. It publishes a {@link #parse(String)} method.
 * 
 * <p>It is assumed that the parser server is up and running (probably via {@code sdparser_server.py}) at http://localhost:8080/parse. It assumes each sentence is a line, and will
 * produce several parses if you give it several lines. It lemmatizes its input and includes the lemmas with the surface forms in the output. 
 *  
 * <p><b>Prereqs</b>:  
 * <li>{@link Tokenizer} and {@link MaxentPosTagger}, using stanford-postagger-2008-09-28 or later
 * <li>Python 2.7 or newer
 * <li>possibly necessary, {@link http://pyyaml.org/download/pyyaml/PyYAML-3.09.zip} - run: python setup.py install
 * <li>the Easy First files, including a shared\ml.pyd library compiled for local platform
 * <p>
 * <b>Output format:</b> 
 * <br>{@code #node word lemma POS POS _ #parent relation _ _}  
 * <br>As in:
 * <br> {@code 28 celebrations celebration NN NN _ 12 nsubj  _ _}
 * <br>
 * 
 * @author Amnon Lotan & Asher Stern
 *
 * @since 16/02/2011
 */
public class EasyFirstClient
{
	/////////////////////// CONSTANTS ///////////////////////
	
	private static final String ENCODING = "UTF-8";
	private static final String TAGGED_TEXT_FIELD = "tagged_text=";
	private static final String UNDERSCORE = "_";
	private static final String EXTRA_NODES_PART_OF_SPEECH_PREFIX = "COIDX_";
	
	// list all pronoun words that must be tagged as PRONOUN. notice that postfix genitives like mine, yours, ours, theirs, may also be tagged as JJ adjectives. so they're excluded. 
	private static final Set<String> NOMINATIVE_ACCUSATIVE_PRONOUNS = Utils.arrayToCollection(new String[]{
		"i", "me", "myself","you","yourself","he","him","himself","she","her","herself","it","itself","we","us","ourselves","they","them","themselves"}, new HashSet<String>(20));
	// 'her' is both accusative and genitive, so it's excluded here
	private static final Set<String> GENITIVE_PRONOUNS = Utils.arrayToCollection(new String[]{"my","your","his","its","our","their"}, new HashSet<String>(6));
	private static PartOfSpeech PRP_POS;
	private static PartOfSpeech PRP$_POS;
	{
		try {	PRP_POS = new PennPartOfSpeech(PennPosTag.PRP);
		} catch (UnsupportedPosTagStringException e) {
			System.err.println("Some strange bug prevents me from constructing a new PennPartOfSpeech(PennPosTag.PRP)");
			e.printStackTrace();	}
		try {	PRP$_POS = new PennPartOfSpeech(PennPosTag.PRP);
		} catch (UnsupportedPosTagStringException e) {
			System.err.println("Some strange bug prevents me from constructing a new PennPartOfSpeech(PennPosTag.PRP$)");
			e.printStackTrace();	}
	}
	
	
	//////////////////// PUBLIC CONSTRUCTOR AND METHODS ////////////////////
	
	/**
	 * <B>tokenizer and posTagger should be initialized!</B>
	 * 
	 * @param tokenizer an already initialized tokenizer
	 * @param posTagger an already initialized posTagger
	 * @param parserUrl parser's URL (e.g. http://localhost:8080/parse)
	 */
	public EasyFirstClient(Tokenizer tokenizer, PosTagger posTagger, URL parserUrl) throws ParserRunException
	{
		if (!tokenizer.isInitialized())
			throw new ParserRunException("Tokenizer is not initialized");
		if (!posTagger.isInitialized())
			throw new ParserRunException("POS tagger is not initialized");
		this.tokenizer = tokenizer;
		this.posTagger = posTagger;
		this.parserUrl = parserUrl;
	}
	
	
	public EasyFirstClient(URL parserUrl) throws ParserRunException
	{
		this.parserUrl = parserUrl;
		this.tokenizer = null;
		this.posTagger = null;
	}
	
	public void parse(String rawText) throws ParserRunException
	{
		List<String> parserOutput = getParserOutput(rawText);
		parseParserOutput(parserOutput,rawText);
	}
	
	public void parse(List<PosTaggedToken> posTaggedSentence) throws ParserRunException
	{

		String taggedText = fixPosTaggedTokensAndConvertToString(posTaggedSentence);
		try
		{
			List<String> parserOutput = getParserOutputFromPosTaggedText(taggedText);
			parseParserOutput(parserOutput,null);
		} 
		catch (IOException e)
		{
			throw new ParserRunException("Failed to run parser. Input was the following tagger text:\n"+taggedText+"\nPlease see nested exception",e);
		}
	}
	
	public BasicConstructionNode getTree()
	{
		return tree;
	}

	public ArrayList<BasicConstructionNode> getNodesAsList()
	{
		return nodesAsList;
	}

	public ArrayList<BasicConstructionNode> getWordsNodesList()
	{
		return wordsNodesList;
	}
	
	
	////////////////////////// PRIVATE //////////////////////////
	
	private List<PosTaggedToken> tokenizeAndPosTag(String rawText) throws ParserRunException
	{
		try
		{
			// tokenize and pos tag
			tokenizer.setSentence(rawText);
			tokenizer.tokenize();
			posTagger.setTokenizedSentence(tokenizer.getTokenizedSentence());
			posTagger.process();
			return posTagger.getPosTaggedTokens();

		}
		catch (TokenizerException e)
		{
			throw new ParserRunException("Tokenizing error", e);
		}
		catch (PosTaggerException e)
		{
			throw new ParserRunException("POS tagging error", e);
		}
	}
	

	
	/**
	 * Repair pronouns that were wrongly tagged as FW (or anything else other than PRONOUN)
	 * @param posTaggedTokens
	 * @return
	 */
	private List<PosTaggedToken> inspectPosTaggedTokens(List<PosTaggedToken> posTaggedTokens) {
		for (ListIterator<PosTaggedToken> iter = posTaggedTokens.listIterator(); iter.hasNext() ; )
		{
			PosTaggedToken posTaggedToken = iter.next();
			String origToken = posTaggedToken.getToken();
			String token = origToken.toLowerCase();
			PartOfSpeech pos = posTaggedToken.getPartOfSpeech();
			if (NOMINATIVE_ACCUSATIVE_PRONOUNS.contains(token) && !simplerPos(pos.getCanonicalPosTag()).equals(SimplerCanonicalPosTag.PRONOUN))
			// replace this postaggedToken
			{
				iter.remove();
				iter.add(new PosTaggedToken(origToken, PRP_POS));
			}
			else if (GENITIVE_PRONOUNS.contains(token) && !simplerPos(pos.getCanonicalPosTag()).equals(SimplerCanonicalPosTag.PRONOUN))
			// replace this postaggedToken with PRP$
			{
				iter.remove();
				iter.add(new PosTaggedToken(origToken, PRP$_POS));
			}
		}
		
		return posTaggedTokens;
	}

	/**
	 * prepare input for the parser
	 * 
	 * @param posTaggedTokens
	 * 
	 * @return
	 */
	private String posTaggedTokensToString(List<PosTaggedToken> posTaggedTokens)
	{
		StringBuilder buf = new StringBuilder();
		for (PosTaggedToken posTaggedToken : posTaggedTokens)
			buf.append(posTaggedToken.getToken() + UNDERSCORE + posTaggedToken.getPartOfSpeech() + " ");
		return buf.toString();
	}
	

	
	
	private List<String> getParserOutputFromPosTaggedText(String taggedText) throws ParserRunException, IOException
	{
		List<String> ret = new LinkedList<String>();
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		try
		{
			// Create query string
			String queryString = TAGGED_TEXT_FIELD + URLEncoder.encode(taggedText, ENCODING);

			// Set up the URLConnection to the parser server, which should be upped by the user
			URLConnection urlConnection = parserUrl.openConnection();
			urlConnection.setDoOutput(true);

			// Write query string to request body
			writer = new OutputStreamWriter(urlConnection.getOutputStream());
			writer.write(queryString);
			writer.flush();

			// Read the response -- only AFTER writing the request!
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				ret.add(line);
			}
		}
		finally
		{
			try
			{
				if (writer!=null)writer.close();
				if (reader!=null)reader.close();
			} 
			catch (IOException e) {	}

		}
		return ret;
	}
	

	
	
	private String fixPosTaggedTokensAndConvertToString(List<PosTaggedToken> posTaggedTokens)
	{
		return posTaggedTokensToString(inspectPosTaggedTokens(posTaggedTokens));
	}



	/**
	 * Get some raw text and return parsed text 
	 * @param rawText each sentence is a line, and will produce several parses if you give it several lines
	 * @return
	 * @throws ParserRunException
	 */
	private List<String> getParserOutput(String rawText) throws ParserRunException
	{
		List<String> ret = null;	
		try
		{
			List<PosTaggedToken> posTaggedTokensForRawText = tokenizeAndPosTag(rawText);
			String taggedText = fixPosTaggedTokensAndConvertToString(posTaggedTokensForRawText);
			ret = getParserOutputFromPosTaggedText(taggedText);
		} 
		catch (IOException e)
		{
			throw new ParserRunException("Error openning a new URLConnection, or writing to or reading from the parser server", e);
		}
		finally
		{
		}

		return ret;
	}
	
	
	private void parseParserOutput(List<String> parserOutput, String optionalRawText) throws ParserRunException
	{
		String rawTextForExceptionString;
		if (optionalRawText!=null)
		{
			rawTextForExceptionString = "\nInput was: \""+optionalRawText+"\"";
		}
		else
		{
			rawTextForExceptionString = "";
		}
		
		tree = null;
		nodesAsList = new ArrayList<BasicConstructionNode>();
		wordsNodesList = new ArrayList<BasicConstructionNode>();
		mapIdToNode = new HashMap<Integer, BasicConstructionNode>();
		mapNodeIdToParentId = new HashMap<Integer, Integer>();
		
		if (parserOutput==null)throw new ParserRunException("null output");
		if (parserOutput.size()==0)throw new ParserRunException("empty output");
		for (String line : parserOutput)
		{
			if (line!=null){if(line.length()>0)
			{
				String[] lineComponents = line.split("\\s");
				try
				{
					int index=0;
					String counterString = lineComponents[index];
					index++;
					String word = lineComponents[index];
					index++;
					String lemma = lineComponents[index];
					index++;
					String pos = lineComponents[index];
					index++;

					index++;

					index++;

					String parent = lineComponents[index];
					index++;
					String relation = lineComponents[index];
					index++;

					int serial=0;
					int id = Integer.parseInt(counterString);
					int parentId = Integer.parseInt(parent);
					boolean isExtraNode = false;
					BasicConstructionNode antecedent = null;
					if (pos.startsWith(EXTRA_NODES_PART_OF_SPEECH_PREFIX))
					{
						isExtraNode=true;
						String antecedentIdString = pos.substring(EXTRA_NODES_PART_OF_SPEECH_PREFIX.length());
						int antecedentId=0;
						try
						{
							antecedentId = Integer.parseInt(antecedentIdString);
						}catch(NumberFormatException e){throw new ParserRunException("Bad antecedent id, when parsing "+pos+".\nParser output was:\n"+listStringToString(parserOutput)+rawTextForExceptionString,e);}
						antecedent = mapIdToNode.get(antecedentId);
						if (antecedent==null)throw new ParserRunException("Antecedent not found for node: "+counterString+".\nParser output was:\n"+listStringToString(parserOutput)+rawTextForExceptionString);
						serial = antecedent.getInfo().getNodeInfo().getSerial();
						pos = InfoGetFields.getPartOfSpeech(antecedent.getInfo());
					}
					else
					{
						isExtraNode=false;
						serial = id;
					}


					if ( (isExtraNode) && (parentId==0) )
					{
						// Do nothing. This node is an anomaly.
					}
					else
					{
						BasicConstructionNode node = null;
						EdgeInfo edgeInfo = null;
						if (parentId==0)
						{
							edgeInfo = new DefaultEdgeInfo(null);
						}
						else
						{
							edgeInfo = new DefaultEdgeInfo(new DependencyRelation(relation, null));
						}
						node = new BasicConstructionNode(new DefaultInfo(counterString,new DefaultNodeInfo(word, lemma, serial, null, new DefaultSyntacticInfo(new PennPartOfSpeech(pos))),edgeInfo));

						if (isExtraNode)
						{
							node.setAntecedent(antecedent);
						}

						mapIdToNode.put(id, node);
						nodesAsList.add(node);
						if (!isExtraNode)
						{
							wordsNodesList.add(node);
						}


						if (parentId==0)
						{
							if (tree!=null)throw new ParserRunException("More than one root.\nParser output was:\n"+listStringToString(parserOutput)+"\n"+rawTextForExceptionString);
							tree=node;
						}
						else
						{
							mapNodeIdToParentId.put(id, parentId);
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					throw new ParserRunException("Wrong line returned by the parser: "+line+".\nParser output was:\n"+listStringToString(parserOutput)+"\n"+rawTextForExceptionString);
				}
				catch (UnsupportedPosTagStringException e)
				{
					throw new ParserRunException("Unsupported part-of-speech tag, occurred in line: \""+line+"\". See nested exception.\nParser output was:\n"+listStringToString(parserOutput)+"\n"+rawTextForExceptionString,e);
				}
			}}
			
		}
		
		for (Map.Entry<Integer, Integer> entry : mapNodeIdToParentId.entrySet())
		{
			Integer nodeCounter = entry.getKey();
			Integer parentCounter = entry.getValue();
			
			BasicConstructionNode parentNode = mapIdToNode.get(parentCounter);
			BasicConstructionNode node = mapIdToNode.get(nodeCounter);
			parentNode.addChild(node);
		}
	}
	
	
	private static String listStringToString(List<String> list)
	{
		StringBuffer sb = new StringBuffer();
		for (String str : list)
		{
			sb.append(str);
			sb.append('\n');
		}
		return sb.toString();
	}
	
	
	
	private Tokenizer tokenizer;
	private PosTagger posTagger;
	private URL parserUrl;
	
	private BasicConstructionNode tree;
	private ArrayList<BasicConstructionNode> nodesAsList;
	private ArrayList<BasicConstructionNode> wordsNodesList;
	
	private Map<Integer,BasicConstructionNode> mapIdToNode;
	private Map<Integer,Integer> mapNodeIdToParentId;
}
