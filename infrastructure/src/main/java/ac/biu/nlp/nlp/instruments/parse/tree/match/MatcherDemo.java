package ac.biu.nlp.nlp.instruments.parse.tree.match;

import java.io.File;
import java.util.Set;

import ac.biu.nlp.nlp.general.BidirectionalMap;
import ac.biu.nlp.nlp.general.ExceptionUtil;
import ac.biu.nlp.nlp.instruments.parse.EnglishSingleTreeParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparClientParser;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparParser;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultMatchCriteria;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.IdLemmaPosRelNodeString;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;

/**
 * 
 * @author Asher Stern
 * 
 *
 */
public class MatcherDemo
{
	
	public static void main(String[] args)
	{
		try
		{
			MatcherDemo demo = new MatcherDemo();
			demo.f(args);
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}

	}


	public void f(String[] args) throws ParserRunException, TreeStringGeneratorException, MatcherException
	{
		EnglishSingleTreeParser parser = null;
		File miniparDir = new File(args[0]);
		if (miniparDir.exists())
		{
			parser = new MiniparParser(args[0]);
		}
		else
		{
			parser = new MiniparClientParser(args[0]);
		}
		parser.init();
		try
		{

			parser.setSentence("I said I love you, I said I love you!");
			parser.parse();
			BasicNode mainTree = parser.getParseTree();

			parser.setSentence("I love you.");
			parser.parse();
			BasicNode testedTree = parser.getParseTree();

			BasicNode theNode = null;
			for (BasicNode node : AbstractNodeUtils.treeToSet(testedTree))
			{
				if (InfoGetFields.getLemma(node.getInfo()).equalsIgnoreCase("love"))
					theNode = node;
			}
			testedTree = theNode;





			TreeStringGenerator<Info> tsg = new TreeStringGenerator<Info>(new IdLemmaPosRelNodeString(), mainTree);
			System.out.println(tsg.generateString());

			System.out.println("-------------------------------------------------------------------------------------------------");
			tsg = new TreeStringGenerator<Info>(new IdLemmaPosRelNodeString(), testedTree);
			System.out.println(tsg.generateString());

			System.out.println("-------------------------------------------------------------------------------------------------");

			AllEmbeddedMatcher<Info,Info, BasicNode,BasicNode> aem = new AllEmbeddedMatcher<Info, Info, BasicNode, BasicNode>(new DefaultMatchCriteria());
			aem.setTrees(mainTree, testedTree);
			aem.findMatches();
			Set<BidirectionalMap<BasicNode, BasicNode>> matches = aem.getMatches();
			System.out.println("matches: "+matches.size());
			for (BidirectionalMap<BasicNode, BasicNode> match : matches)
			{
				printMatch(match);
				System.out.println();
			}
		}
		finally
		{
			parser.cleanUp();
		}
	}
	
	
	private void printMatch(BidirectionalMap<BasicNode, BasicNode> match)
	{
		for (BasicNode mainNode : match.leftSet())
		{
			BasicNode testedNode = match.leftGet(mainNode);
			System.out.println(mainNode.getInfo().getId()+" -> "+testedNode.getInfo().getId());
		}
	}
	
	
}
