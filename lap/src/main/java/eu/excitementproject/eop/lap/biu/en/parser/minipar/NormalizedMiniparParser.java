package eu.excitementproject.eop.lap.biu.en.parser.minipar;

import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.strings.distance.LevenshteinDistance;
import eu.excitementproject.eop.common.utilities.strings.distance.StringsDistanceException;
import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;


/**
 * 
 * @author Asher Stern
 *
 */
public class NormalizedMiniparParser implements EnglishSingleTreeParser
{
	public NormalizedMiniparParser(AbstractMiniparParser realParser) throws ParserRunException 
	{
		this.realParser = realParser;
	}
	
	public void init() throws ParserRunException
	{
		realParser.init();
	}

	public void setSentence(String sentence)
	{
		realParser.setSentence(sentence);
	}

	public void parse() throws ParserRunException
	{
		realParser.parse();
		List<BasicConstructionNode> wordNodes = realParser.getNodesOrderedByWords();
		for (BasicConstructionNode node : wordNodes)
		{
			normalizeNode(node);
		}
	}

	public BasicConstructionNode getMutableParseTree()
			throws ParserRunException
	{
		return realParser.getMutableParseTree();
	}

	public ArrayList<BasicConstructionNode> getNodesAsList()
			throws ParserRunException
	{
		return realParser.getNodesAsList();
	}

	public ArrayList<BasicConstructionNode> getNodesOrderedByWords()
			throws ParserRunException
	{
		return realParser.getNodesOrderedByWords();
	}

	public BasicNode getParseTree() throws ParserRunException
	{
		return realParser.getParseTree();
	}




	public void reset()
	{
		realParser.reset();
	}

	
	public void cleanUp()
	{
		realParser.cleanUp();
	}

	

	protected void normalizeNode(BasicConstructionNode node)
	{
		try
		{
			String word = node.getInfo().getNodeInfo().getWord();
			String lemma = node.getInfo().getNodeInfo().getWordLemma();
			if (lemma.contains(" "))
			{
				LevenshteinDistance distance = new LevenshteinDistance();
				String[] words = lemma.split(" ");
				String normalizedLemma = words[0];
				distance.setCaseSensitive(false);
				distance.setFirstString(normalizedLemma);
				distance.setSecondString(word);
				long ldistance = distance.computeDistance();
				for (int index=0;index<words.length;index++)
				{
					distance.setFirstString(words[index]);
					distance.setSecondString(word);
					long currentDistance = distance.computeDistance();
					if (currentDistance<ldistance)
					{
						normalizedLemma = words[index];
						ldistance = currentDistance;
					}
				}
				NodeInfo newNodeInfo = new DefaultNodeInfo(word, normalizedLemma, node.getInfo().getNodeInfo().getSerial(), node.getInfo().getNodeInfo().getNamedEntityAnnotation(), node.getInfo().getNodeInfo().getSyntacticInfo());
				Info newInfo = new DefaultInfo(node.getInfo().getId(), newNodeInfo, node.getInfo().getEdgeInfo());
				node.setInfo(newInfo);
			}
		}
		catch (StringsDistanceException e)
		{}
		catch(NullPointerException e)
		{}
		
		
	}
	
	protected AbstractMiniparParser realParser;
	

}
