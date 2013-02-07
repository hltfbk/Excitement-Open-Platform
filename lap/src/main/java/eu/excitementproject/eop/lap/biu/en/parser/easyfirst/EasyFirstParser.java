package eu.excitementproject.eop.lap.biu.en.parser.easyfirst;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.BasicPipelinedParser;
import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.postagger.stanford.MaxentPosTagger;
import eu.excitementproject.eop.lap.biu.en.tokenizer.MaxentTokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TokenizerException;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggerException;



/**
 * 
 * @author Asher Stern
 * @since Mar 22, 2011
 *
 */
public class EasyFirstParser implements BasicParser, BasicPipelinedParser,
EnglishSingleTreeParser // for backward compatibility
{
	public static final String URL_PROTOCOL = "http";
	public static final String URL_FILE = "/parse";
	public static final String DEFAULT_HOST = "localhost";
	
	// Needs to be a constant String, for use in UimaFit's annotation @ConfigurationParameter
	public static final String DEFAULT_PORT_STR = "8080";
	
	// Preserving the old const, as a calculation of the above String const
	public static final int DEFAULT_PORT = Integer.parseInt(DEFAULT_PORT_STR);
	
	
	/**
	 * <B>tokenizer and posTagger are not initialized</B>
	 * @param host
	 * @param port
	 * @param tokenizer
	 * @param posTagger
	 * @throws ParserRunException
	 */
	public EasyFirstParser(String host, int port, Tokenizer tokenizer, PosTagger posTagger) throws ParserRunException
	{
		try
		{
			this.tokenizer = tokenizer;
			this.posTagger = posTagger;
			this.url = new URL(URL_PROTOCOL, host, port, URL_FILE);
		}
		catch (MalformedURLException e)
		{
			throw new ParserRunException("URL problem",e);
		}
	}
	
	// usually we use for posTaggerModelFile $JARS/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger
	// According to Yoav Goldberg's suggestion, left3words-wsj-0-18.tagger is good as well, and much faster.
	public EasyFirstParser(String host, int port, String posTaggerModelFile) throws ParserRunException
	{
		try
		{
			this.tokenizer = new MaxentTokenizer();
			this.posTagger = new MaxentPosTagger(posTaggerModelFile);
			this.url = new URL(URL_PROTOCOL, host, port, URL_FILE);
		}
		catch(PosTaggerException e)
		{
			throw new ParserRunException("pos tagger problem",e);
		}
		catch (MalformedURLException e)
		{
			throw new ParserRunException("URL problem",e);
		}
	}
	
	
	// usually we use $JARS/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger
	// According to Yoav Goldberg's suggestion, left3words-wsj-0-18.tagger is good as well, and much faster.
	public EasyFirstParser(String posTaggerModelFile) throws ParserRunException
	{
		this(DEFAULT_HOST,DEFAULT_PORT,posTaggerModelFile);
	}
	
	/**
	 * Constructs the parser with host and port, but no tokenizer and pos-tagger.
	 * Use this constructor only if the {@link #setSentence(String)} method will
	 * never be called (only {@link #setSentence(List)} will be called).
	 * @param host
	 * @param port
	 * @throws ParserRunException
	 */
	public EasyFirstParser(String host, int port) throws ParserRunException
	{
		try
		{
			this.url = new URL(URL_PROTOCOL, host, port, URL_FILE);
			this.tokenizer=null;
			this.posTagger=null;
		}
		catch (MalformedURLException e)
		{
			throw new ParserRunException("Bad URL for the given host and port",e);
		}
	}
	
	/**
	 * Constructs the parser with default parameters for host and port, and with
	 * no tokenizer and no pos-tagger.
	 * Use this constructor only if the {@link #setSentence(String)} method will
	 * never be called (only {@link #setSentence(List)} will be called).
	 * @throws ParserRunException
	 */
	public EasyFirstParser() throws ParserRunException
	{
		this(DEFAULT_HOST,DEFAULT_PORT);
	}
	
	
	public void init() throws ParserRunException
	{
		if ( (this.tokenizer!=null) && (this.posTagger!=null) )
		{
			initTokenizerAndPosTagger();
		}
		else if ( (null==this.tokenizer) && (null==this.posTagger) )
		{
			// do nothing
		}
		else throw new ParserRunException("Tokenizer and Pos-Tagger must be" +
				"either both null, or both non-null.");
		
		initialized=true;
	}

	public void initTokenizerAndPosTagger() throws ParserRunException
	{
		try
		{
			this.tokenizer.init();
		}
		catch (TokenizerException e)
		{
			throw new ParserRunException("tokenizer initialization problem",e);
		}
		
		try
		{
			this.posTagger.init();
		}
		catch (PosTaggerException e)
		{
			try{this.tokenizer.cleanUp();}catch(Exception ee){}
			throw new ParserRunException("posTagger initialization problem",e);
		}
	}

	public void setSentence(String sentence)
	{
		reset();
		this.sentence = sentence;
	}
	
	@Override
	public void setSentence(List<PosTaggedToken> posTaggedSentence)
	{
		reset();
		this.posTaggedSentence = posTaggedSentence;
	}


	/**
	 * @return true if this parser is initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}
	
	public void parse() throws ParserRunException
	{
		if (!initialized)   throw new ParserRunException("You must call init() before doing anything with this parser");
		if ( (null==sentence) && (null==posTaggedSentence) ) throw new ParserRunException("Sentence not set." +
				" Please set the sentence to be parsed by calling one of the setSentence() methods.");

		EasyFirstClient client;
		if (this.sentence!=null)
		{
			if ( (this.tokenizer!=null) && (this.posTagger!=null) )
			{
				client = new EasyFirstClient(tokenizer, posTagger, url);
				client.parse(sentence);
			}
			else
			{
				throw new ParserRunException("Tried to parse raw text, but the parser was " +
						"constructed for pipe-line only mode.");
			}
		}
		else if (this.posTaggedSentence!=null)
		{
			client = new EasyFirstClient(url);
			client.parse(this.posTaggedSentence);
		}
		else
		{
			throw new ParserRunException("Internal bug"); // has already been checked, so they cannot be both null.
		}


		nodesList = client.getNodesAsList();
		mutableTree = client.getTree();
		wordsNodesList = client.getWordsNodesList();
	}

	public BasicConstructionNode getMutableParseTree() throws ParserRunException
	{
		if (mutableTree==null) throw new ParserRunException("Not parsed.");
		return mutableTree;
	}

	public BasicNode getParseTree() throws ParserRunException
	{
		if (tree==null)
		{
			if (mutableTree==null) throw new ParserRunException("Not parsed.");
			tree = AbstractNodeUtils.copyTree(mutableTree, new BasicNodeConstructor());
		}
		return tree;
	}

	public ArrayList<BasicConstructionNode> getNodesOrderedByWords() throws ParserRunException
	{
		if (wordsNodesList==null) throw new ParserRunException("Not parsed.");
		return wordsNodesList;
	}

	public ArrayList<BasicConstructionNode> getNodesAsList() throws ParserRunException
	{
		if (nodesList==null) throw new ParserRunException("Not parsed.");
		return nodesList;
	}

	public void reset()
	{
		this.tree = null;
		this.mutableTree = null;
		this.wordsNodesList = null;
		this.nodesList = null;
		this.sentence = null;
		this.posTaggedSentence = null;
	}

	public void cleanUp()
	{
		if (initialized)
		{
			if (tokenizer!=null)
				this.tokenizer.cleanUp();
			if (posTagger!= null)
				this.posTagger.cleanUp();
		}
	}

	private URL url;
	private Tokenizer tokenizer = null;
	private PosTagger posTagger = null;
	private boolean initialized = false;
	
	private String sentence = null;
	List<PosTaggedToken> posTaggedSentence = null;
	
	private BasicConstructionNode mutableTree = null;
	private ArrayList<BasicConstructionNode> wordsNodesList = null;
	private ArrayList<BasicConstructionNode> nodesList = null;
	
	private BasicNode tree = null;
}
