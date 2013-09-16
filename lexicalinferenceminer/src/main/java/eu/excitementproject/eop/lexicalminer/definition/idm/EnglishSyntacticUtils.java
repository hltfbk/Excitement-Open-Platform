package eu.excitementproject.eop.lexicalminer.definition.idm;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.configuration.InitException;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.tokenizer.Tokenizer;
import eu.excitementproject.eop.lap.biu.postagger.PosTagger;
import eu.excitementproject.eop.lexicalminer.definition.Common.ParserUtils;
import eu.excitementproject.eop.lexicalminer.definition.Common.UtilClass;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.InstrumentCombinationException;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.LexicalToolsFactory;

/**
 * This class is used as additional class to the Syntactic, 
 * in order to be able to change it according for each tree structure
 */

public class EnglishSyntacticUtils implements SyntacticUtils,  Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5179334067025602412L;
	public int m_maxNPSize = 7;	//max length of rule (number of words) we allow  
	private EnglishSingleTreeParser m_EnglishSingleTreeParser = null;
	
	private ConfigurationParams m_processingToolsConf = null;
	Tokenizer m_Tokenizer = null;
	PosTagger m_PosTagger = null;
	
	public EnglishSyntacticUtils(int maxNPSize, ConfigurationParams processingToolsConf) throws InstrumentCombinationException
	{
		this.m_maxNPSize = maxNPSize;
		this.m_processingToolsConf=processingToolsConf;
		this.m_Tokenizer = LexicalToolsFactory.createTokenizer(m_processingToolsConf);
		this.m_PosTagger = LexicalToolsFactory.createPosTagger(m_processingToolsConf);	
		
	}
	
	public EnglishSyntacticUtils(int maxNPSize, Tokenizer tokenizer, PosTagger posTagger,ConfigurationParams processingToolsConf)
	{
		this.m_processingToolsConf=processingToolsConf;
		this.m_maxNPSize = maxNPSize;
		this.m_Tokenizer = tokenizer;
		this.m_PosTagger = posTagger;		
	}	
	
	@Override
	public BasicParser getParserInstance() throws ParserRunException {
		if (m_EnglishSingleTreeParser == null)
		{
			String parserHost;
			int parserPort;
			try {
				parserHost = this.m_processingToolsConf.getString("parser-host");
			} catch (ConfigurationException e) {
				throw new ParserRunException("can't get parser-host from config file.", e);
			}
			
			try {
				parserPort = this.m_processingToolsConf.getInt("parser-port");
			} catch (ConfigurationException e) {
				throw new ParserRunException("can't get parser-port from config file.", e);
			}
			m_EnglishSingleTreeParser = ParserUtils.getInitializedStandardParser(this.m_PosTagger, this.m_Tokenizer, parserHost, parserPort);
			m_EnglishSingleTreeParser.init();			
		}
		return m_EnglishSingleTreeParser; 
	}	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.lexicalMiner.definition.idm.SyntacticUtils#getPatternStrings(ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode, java.util.List, int, boolean, java.lang.String, java.lang.String)
	 */
	@Override
	public Pattern getPatternStrings(BasicNode sentTree, List<BasicNode> path, int wordId,boolean isNP, String leftLemma, String rightLemma)
	{
		StringBuilder sbWords = new StringBuilder();
		StringBuilder sbPos = new StringBuilder();
		StringBuilder sbRelations = new StringBuilder();
		StringBuilder sbPosRelations = new StringBuilder();		
		StringBuilder sbAll = new StringBuilder();
		String word = null;
		String pos = null;		
		String relation = null;		
		boolean firstWord = true;
		boolean firstRelation = true;		
		
		for (BasicNode node : path) {
			if (!firstWord)	//Not the first
			{
				sbWords.append(">");	//add separator
				sbPos.append(">");	//add separator
				if (firstRelation == false)
				{
					sbRelations.append(">");	//add separator					
				}					
			}
			
			word = node.getInfo().getNodeInfo().getWordLemma();
			// if the word is same as the words in the relation- don't save that word in the pattern...
			if (word.equalsIgnoreCase(leftLemma))
			{
				word = "_LEFT_TERM_";				
			}	
			else if (word.equalsIgnoreCase(rightLemma))
			{
				word = "_RIGHT_TERM_";				
			}	
			
			pos =	node.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech().getStringRepresentation();
			sbPos.append(pos);
			sbWords.append(word);
			
			if (!firstWord)
			{
				relation = node.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation();
				sbRelations.append(relation);
				sbPosRelations.append(">" + relation + ">" +  pos);				
				sbAll.append(">" + relation + ">" + word + ":" + pos);				
				firstRelation = false;
			}		
			else
			{
				sbPosRelations.append(pos);								
				sbAll.append(word + ":" + pos);				
				
			}
			firstWord = false;
		}
		
		return new Pattern(sbPos.toString(),sbWords.toString(),sbRelations.toString(), sbPosRelations.toString(), sbAll.toString());
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.lexicalMiner.definition.idm.SyntacticUtils#getNPRuleForNoun(ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode, ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode)
	 */
	@Override
	public String getNPRuleForNoun(BasicNode full_tree, BasicNode current_tree) throws InitException
	{
		if (current_tree.hasChildren())	//if it have children, it's a NP, so also save it's NP phrase
		{
			List<NodeInfo> nodes = new ArrayList<NodeInfo>();
			
			//Get all the relevantNodes
			nodes.addAll(getRelevantNPnodes(current_tree));
		
			return getFinalStringRepresentation(nodes, current_tree.getInfo().getNodeInfo());	//returns null if not in order 
		}
		else 
		{
			return null;
		}
	}
	
	/**
	 * the function check if that word can be a part of the NP
	 * @param currentPOS
	 * @return
	 * @throws InitException 
	 */
	protected boolean isAGoodNpPart(PartOfSpeech currentPOS) throws InitException
	{

		CanonicalPosTag pos = currentPOS.getCanonicalPosTag();
	
		if (pos.equals(CanonicalPosTag.ADJ))
		{
			return true;
		}
		else if (pos.equals(CanonicalPosTag.PP))	//it's a PREPOSITION (of)
		{
				return true;
		}
		else if (pos.equals(CanonicalPosTag.ART))	//it's a DETERMINER (the)
		{
				return true;
		}					
		else if (UtilClass.getInstance().isANoun(currentPOS))	//it's a noun
		{
				return true;
		}
		else if (currentPOS.getStringRepresentation().equals("CD"))	//it's a number
		{		
				return true;
		}			
		else
		{
			return false;
		}
	}	

	/**
	 * Adds that node to be part of the NP, and if ask for- add more children
	 * @param current_tree
	 * @param continueTochildrens
	 * @return
	 * @throws InitException 
	 */
	protected List<NodeInfo> getRelevantNPnodes(BasicNode current_tree) throws InitException
	{
		List<NodeInfo> nodes = new ArrayList<NodeInfo>();
		PartOfSpeech currentPOS = null;
		
		//if got here, add it...
		nodes.add(current_tree.getInfo().getNodeInfo());
		
		//if have children- check them
		if (current_tree.hasChildren())
		{
			//run on all children
			for (BasicNode child : current_tree.getChildren()) {
				
				currentPOS= child.getInfo().getNodeInfo().getSyntacticInfo().getPartOfSpeech();
				
				//if it's supposed to be added to the NP (it's a part of a NP) - add it...
				if (isAGoodNpPart(currentPOS))
				{
					nodes.addAll(getRelevantNPnodes(child));	
				}
			} 
		}
		
		return nodes;
	}
	
	
	/**
	 * A private comparator class to compare 2 NodeInfo by their Serial num
	 */
	protected class NodesComparator implements Comparator<NodeInfo>
	{
		@Override
		public int compare(NodeInfo arg0, NodeInfo arg1) {
			return arg0.getSerial() - arg1.getSerial();
		}
		
	}
	
	/**
	 * The function is used to sort the Nodes by there id's
	 * @param nodes
	 * @return
	 */
	protected NodeInfo[] sortNodes(List<NodeInfo> nodes)
	{
		Collections.sort(nodes, new NodesComparator());
		return nodes.toArray(new NodeInfo[nodes.size()]);
	}
	
	
	/**
	 * The function make sure that we are talking about a sequence of words 
	 * (with no skips, if there are, we get the biggest sequence with the original word)
	 * @param nodes
	 * @param NPsourceNode
	 * @return
	 */
	protected LinkedList<NodeInfo> filterNodesArray(NodeInfo[] nodes, NodeInfo NPsourceNode)
	{
		LinkedList<NodeInfo> orderedList = new LinkedList<NodeInfo>();
		NodeInfo lastNode = null;

		
		for (int i = 0; i < nodes.length; i++)
		{	
			if (lastNode == null)	//if it's the first, add it...
			{
				orderedList.add(nodes[i]);
				lastNode = nodes[i];
			}
			else
			{
				if (lastNode.getSerial() == (nodes[i].getSerial() -1)) //make sure the word are in a sequence
				{
					orderedList.addLast(nodes[i]);
					lastNode = nodes[i];					
				}
				else	//if the words are not a sequence
				{
					if (nodes[i].getSerial() > NPsourceNode.getSerial())	//if already passed the source, then skip the rest
					{	
						if (orderedList.size() > 1)
						{
							return orderedList;			
						}
						else	//have only the source word, so ignore that NP
						{
							return new LinkedList<NodeInfo>();
						}
					}
					else	//start a new sequence , to include the the word that created that NP
					{
						orderedList = new LinkedList<NodeInfo>();
						orderedList.add(nodes[i]);
						lastNode = nodes[i];
					}
				}
			}
		}
		if (orderedList.size() > 1)
		{
			return orderedList;			
		}
		else	//have only the source word, so ignore that NP
		{
			return new LinkedList<NodeInfo>();
		}	
	}
	
	/**
	 * The function deletes all last words that are not nouns (because a NP can't end without a noun)
	 * it also delete the first words that are not nouns or ADJECTIVEs
	 * @param nodes
	 * @return
	 * @throws InitException 
	 */
	protected LinkedList<NodeInfo> filterFirstLastNodes(LinkedList<NodeInfo> nodes) throws InitException
	{
		//remove last non-nouns
		while ((nodes.size() > 0) 
			&&(!UtilClass.getInstance().isANoun(nodes.getLast().getSyntacticInfo().getPartOfSpeech())))
		{
			nodes.removeLast();	
		}
		
		if (nodes.size() > 0)
		{
			//remove first non-adjective or nouns or CD (numbers)
			PartOfSpeech firstpos = nodes.getFirst().getSyntacticInfo().getPartOfSpeech();
			while ((nodes.size() > 0) &&
					(!(	(UtilClass.getInstance().isANoun(firstpos))
							|| (firstpos.getCanonicalPosTag().equals(CanonicalPosTag.ADJ))
							|| (firstpos.getStringRepresentation().equals("CD"))
							)))		
				{
					nodes.removeFirst();	
					firstpos = nodes.getFirst().getSyntacticInfo().getPartOfSpeech();				
				}	
		}
		
		if (nodes.size() == 1)	//if have only 1 word- don;t save it, it will only be the source word
		{
			return new LinkedList<NodeInfo>();
		}
		else
		{
			return nodes;
		}
	}
	
	/**
	 * Return a String that represent that NP-parts array (it may filter some of the word if not neeeded)
	 * @param nodes
	 * @return
	 * @throws InitException 
	 */
	protected String getFinalStringRepresentation(List<NodeInfo> nodes, NodeInfo NPsourceNode) throws InitException{
		NodeInfo[] sortedNodes = sortNodes(nodes);
		String stringRep = new String();
					
		LinkedList<NodeInfo> filteredNodes = new LinkedList<NodeInfo>();
		filteredNodes = filterNodesArray(sortedNodes,NPsourceNode);
		filteredNodes = filterFirstLastNodes(filteredNodes);
		
		if (filteredNodes.size() <= m_maxNPSize)	//only save that NP if less then 7 words length
		{
			//Write array to string 
			for (int i = 0; i < filteredNodes.size(); i++)
			{	
				if (!(stringRep.isEmpty()))	//if it's not the first word
				{
					stringRep = stringRep + " ";
				}		
				stringRep = stringRep + filteredNodes.get(i).getWordLemma();
			}
		}
		return stringRep;
	}
	
}
