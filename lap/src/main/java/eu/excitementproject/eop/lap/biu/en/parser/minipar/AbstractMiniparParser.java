package eu.excitementproject.eop.lap.biu.en.parser.minipar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelationType;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNodeConstructor;
import eu.excitementproject.eop.common.representation.partofspeech.MiniparPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;

/**
 * Implementation of {@link EnglishSingleTreeParser} for Minipar.
 * <P>
 * Minipar can be found here: http://www.cs.ualberta.ca/~lindek/minipar.htm
 * <P>
 * This class does all the work with Minipar, <B> except </B> of getting the
 * information from Minipar.
 * <BR>
 * I.e. getting the actual information from Minipar,
 * by calling the cpp dynamic link library created by Asher Stern,
 * is done by subclasses of this class that implement {@link #getMiniparNativeOutput()} method.
 * 
 * @author Asher Stern
 *
 */
public abstract class AbstractMiniparParser implements EnglishSingleTreeParser
{
	////////////////////////// CONSTANTS ///////////////////////
	public static final String ROOT_NODE_ID = "ROOT";
	// Used if no other node was found as root (i.e. never used).
	
	protected static final int IGNORED_MINIPAR_LINES_HEADER = 1;
	protected static final int IGNORED_MINIPAR_LINES_FOOTER = 1;
	protected static final int IGNORED_MINIPAR_LINES = IGNORED_MINIPAR_LINES_HEADER+IGNORED_MINIPAR_LINES_FOOTER;
	protected static final HashSet<String> IGNORED_MINIPAR_LINES_CONTENTES = new HashSet<String>();
	protected static final LinkedHashMap<String,DependencyRelationType> mapRelation = new LinkedHashMap<String, DependencyRelationType>();
	
	public static DependencyRelationType getDependencyRelationType(String dependencyRelationString) {
		return mapRelation.get(dependencyRelationString);
	}
	
	static
	{
		IGNORED_MINIPAR_LINES_CONTENTES.add("(");
		IGNORED_MINIPAR_LINES_CONTENTES.add(")");
		
		
	
		mapRelation.put("obj", DependencyRelationType.OBJECT);
		mapRelation.put("obj2", DependencyRelationType.OBJECT);
		mapRelation.put("subj", DependencyRelationType.SUBJECT);
		mapRelation.put("s", DependencyRelationType.SUBJECT);
	}
	
	/////////////////// PRIVATE & PROTECTED ///////////////////////	

	
	/**
	 * This is an abstract method, that must be implemented by subclasses of
	 * this class.
	 * <P>
	 * This method returns the Minipar parse tree in a format similar to the
	 * "pdemo" format (but <B>not</B> absolutely equal), by calling the dynamic
	 * link library created by Asher Stern for exactly this purpose. 
	 * @return
	 */
	protected abstract ArrayList<String> getMiniparNativeOutput() throws ParserRunException;
	
	/**
	 * This class groups together a {@link BasicConstructionNode},
	 * and an ID of the parent of that node.
	 * <P>
	 * Note: the ID is <B> not </B> the id of that
	 * {@link BasicConstructionNode}, but the ID of its parent.
	 * @author Asher Stern
	 *
	 */
	protected final static class ParentAndConstructionNode
	{
		public String parentId;
		public BasicConstructionNode node;
	}

	protected static MiniparPartOfSpeech convertMiniparCategoryToPartOfSpeech(String miniparCategory) throws UnsupportedPosTagStringException
	{
		return new MiniparPartOfSpeech(miniparCategory);
	}
	
	
	protected void cleanSentence()
	{
		sentence = null;
		allNodes = null;
		wordsOnlyNodes = null;
		root = null;
		rootOfImmutable = null;
		serial = 1;
		
	}
	
	/**
	 * Builds a {@link BasicConstructionNode} that reflects the given line.
	 * In addition it extracts the parent-id.
	 * <P>
	 * Then, the {@link BasicConstructionNode} and the parent-id are grouped
	 * together into a {@link ParentAndConstructionNode}.
	 * <P>
	 * Note that {@link ParentAndConstructionNode} object holds a {@link BasicConstructionNode}
	 * and an ID, but that ID does <B> not </B> belong to the {@link BasicConstructionNode},
	 * but to its parent.
	 * 
	 * @param miniparOutputLine a line, which is one line in {@link ArrayList}
	 * of lines that was returned from {@link MiniparJni#parse(String)} method.
	 * 
	 * @return a {@link ParentAndConstructionNode} object, that contains the
	 * {@link BasicConstructionNode} that reflects the given line, with the ID of
	 * <B> its parent </B>.
	 * @throws UnsupportedPosTagStringException 
	 */
	protected ParentAndConstructionNode buildConstructionNode(String miniparOutputLine) throws UnsupportedPosTagStringException
	{
		ParentAndConstructionNode ret = new ParentAndConstructionNode();
		
		// The Minipar output is just a TAB separated String.
		// This lineParser object retrieves the actual components.
		MiniparJniLineParser lineParser = new MiniparJniLineParser(miniparOutputLine);
		lineParser.parse();
		
		DependencyRelation relation = null;
		if (mapRelation.containsKey(lineParser.getRelation()))
			relation = new DependencyRelation(lineParser.getRelation(),mapRelation.get(lineParser.getRelation()));
		else
			relation = new DependencyRelation(lineParser.getRelation(),null);
			
		MiniparPartOfSpeech pos = null;
		pos = convertMiniparCategoryToPartOfSpeech(lineParser.getCategory());
		
		
		DefaultEdgeInfo defaultEdgeInfo = new DefaultEdgeInfo(relation);
		DefaultSyntacticInfo syntacticInfo = new DefaultSyntacticInfo(pos);
		DefaultNodeInfo defaultNodeInfo = new DefaultNodeInfo(lineParser.getWord(),lineParser.getRootForm(),this.serial,null,syntacticInfo);
		DefaultInfo info = new DefaultInfo(lineParser.getLabel(), defaultNodeInfo,defaultEdgeInfo);
		
		
		
		
		if (lineParser.getWord()==null)
			; // serial has no meaning. Next word should get the current serial.
		else
			this.serial++; // We assigned the current serial to the current ConstructionNode.
		                   // So the next word should get the next serial.
		
		
		ret.node = new BasicConstructionNode(info);
		ret.parentId = lineParser.getParentLabel();
		
		// and - add the antecedent information
		if (lineParser.getAntecedentLabel() != null)
		{
			mapAntecedentOf.put(lineParser.getLabel(), lineParser.getAntecedentLabel());
		}
		
		return ret;
	}
	
	
	
	
	
	
	
	
	////////////////////////////// PUBLIC METHODS //////////////////////////


	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#init()
	 */
	public abstract void init() throws ParserRunException;

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#setSentence(java.lang.String)
	 */
	public void setSentence(String sentence)
	{
		cleanSentence();
		this.sentence = sentence;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#parse()
	 */
	public void parse() throws ParserRunException
	{
		// Verifying parameters and flow are legal.
		if (this.sentence==null)
			throw new ParserRunException("the parse method was called with null sentence.");
		
		// Calling the native method for getting information from Minipar.
		ArrayList<String> miniparOutput = getMiniparNativeOutput();
		// Verify there was no error in the native method.
		if (miniparOutput==null)
			throw new ParserRunException("parser failed. (returned null)");
		if (miniparOutput.size()<IGNORED_MINIPAR_LINES)
			throw new ParserRunException("Illegal minipar output. Most likely to be an error in the initialization phase. May be due to wrong data directory path. Looking for standard error messages printed by minipar may help find the problem.");
		
		// Creating the parse tree. First creating the root.
		DefaultInfo rootInfo = new DefaultInfo(ROOT_NODE_ID,new DefaultNodeInfo(null,null,0,null,new DefaultSyntacticInfo(null)),new DefaultEdgeInfo(null)); 
		root = new BasicConstructionNode(rootInfo);
		
		// This map: maps an ID to its ConstructionNode.
		// In addition, the mapping also maps to the ID of its parent node.
		LinkedHashMap<String, ParentAndConstructionNode> mapIdToNode = new LinkedHashMap<String, ParentAndConstructionNode>();
		this.allNodes = new ArrayList<BasicConstructionNode>(miniparOutput.size()-IGNORED_MINIPAR_LINES);
		this.mapAntecedentOf = new LinkedHashMap<String, String>();
		
		try
		{
			for (String miniparLine : miniparOutput)
			{
				if (IGNORED_MINIPAR_LINES_CONTENTES.contains(miniparLine)) ; // do nothing
				else
				{
					ParentAndConstructionNode parentAndNode = buildConstructionNode(miniparLine);
					allNodes.add(parentAndNode.node);
					mapIdToNode.put(parentAndNode.node.getInfo().getId(), parentAndNode);
				}
			}
			wordsOnlyNodes = new ArrayList<BasicConstructionNode>(serial-1);
			
			// This loop has two purposes:
			// 1. Add elements to the wordsOnlyNodes list. Since Minipar
			//    returns the lines in the order the words appear in the sentence,
			//    they are just added in that order (which is the correct order)
			// 2. Add the children of a node to it.
			//    I.e. We just have list of nodes, but none of them has children.
			//    Though, we do know for each node who is parent is (by mapIdToNode map).
			for (BasicConstructionNode node : allNodes)
			{
				// The first purpose.
				if (node.getInfo()!=null) if (node.getInfo().getNodeInfo()!=null) if (node.getInfo().getNodeInfo().getWord()!=null)
				{
					wordsOnlyNodes.add(node);
				}
				
				// REMOVE this lines
				// If we see a node here - we have already seen all nodes
				// that appear in the sentence before this node.
				//node.setMyIndexHere();
				
				// Now, just locate the parent, and add this node as child to its parent.
				String parentId = mapIdToNode.get(node.getInfo().getId()).parentId;
				if (parentId != null)
				{
					mapIdToNode.get(parentId).node.addChild(node);
				}
				else
				{
					root.addChild(node);
				}
				
			} // end of for loop.
			
			// Add antecedent information
			for (String id : this.mapAntecedentOf.keySet())
			{
				String idAntecedent = mapAntecedentOf.get(id);
				BasicConstructionNode currentNode = mapIdToNode.get(id).node;
				if (idAntecedent != null) // and it never should be null...
				{
					BasicConstructionNode antecedentNode = mapIdToNode.get(idAntecedent).node;
					currentNode.setAntecedent(antecedentNode);
				}
			}
			
			if (root.getChildren()==null)
				throw new ParserRunException("It looks as if the parser has returned a cyclic graph, instead of a tree.");
			if (root.getChildren().size()==0)
				throw new ParserRunException("It looks as if the parser has returned a cyclic graph, instead of a tree.");
			if (root.getChildren().size()==1)
				root = root.getChildren().get(0); // replace the artificial root, by the root specified by Minipar.
			
		}
		catch(NullPointerException e) // should never happen.
		{
			throw new ParserRunException("Malformed Construction node was created, and caused NullPointerException",e);
		}
		catch (UnsupportedPosTagStringException e)
		{
			throw new ParserRunException("Unsupported part-of-speech tag. See nested exception",e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#getMutableParseTree()
	 */
	public BasicConstructionNode getMutableParseTree() throws ParserRunException
	{
		if (null==this.root)
			throw new ParserRunException("Tree does not exist. Did you forget calling parse() method?");
		
		return this.root;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#getNodesOrderedByWords()
	 */
	public ArrayList<BasicConstructionNode> getNodesOrderedByWords() throws ParserRunException
	{
		if (null==this.root)
			throw new ParserRunException("Tree does not exist. Did you forget calling parse() method?");

		return this.wordsOnlyNodes;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#getNodesAsList()
	 */
	public ArrayList<BasicConstructionNode> getNodesAsList() throws ParserRunException
	{
		if (null==this.root)
			throw new ParserRunException("Tree does not exist. Did you forget calling parse() method?");

		return this.allNodes;
	}


	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#getParseTree()
	 */
	public BasicNode getParseTree() throws ParserRunException
	{
		if (null==this.root)
			throw new ParserRunException("Tree does not exist. Did you forget calling parse() method?");

		this.rootOfImmutable = AbstractNodeUtils.copyTree(root, new BasicNodeConstructor());
		return this.rootOfImmutable;
	}
	

	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#reset()
	 */
	public void reset()
	{
		cleanSentence();
	}


	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.Parser#cleanUp()
	 */
	public void cleanUp()
	{
		cleanSentence();
	}


	
	
	protected String features = null;
	protected String sentence = null;
	protected ArrayList<BasicConstructionNode> allNodes;
	protected ArrayList<BasicConstructionNode> wordsOnlyNodes;
	protected BasicConstructionNode root;
	protected BasicNode rootOfImmutable = null; // can stay null until somebody
	                                       // wants the immutable tree.
	protected LinkedHashMap<String,String> mapAntecedentOf; // map ID to ID.
	protected int serial=1;


}
