package ac.biu.nlp.nlp.instruments.parse.minipar;

import java.util.ArrayList;
import java.util.List;

import ac.biu.nlp.nlp.general.strings.distance.LevenshteinDistance;
import ac.biu.nlp.nlp.general.strings.distance.StringsDistanceException;
import ac.biu.nlp.nlp.instruments.parse.EnglishSingleTreeParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultNodeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NodeInfo;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicConstructionNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;


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
