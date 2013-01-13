package eu.excitementproject.eop.common.representation.parse.easyfirst;
import eu.excitementproject.eop.common.representation.parse.ParserRunException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.IdLemmaPosRelNodeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;

public class DemoEasyFirst
{
	public static void main(String[] args)
	{
		try
		{
			DemoEasyFirst app = new DemoEasyFirst(args);
			app.f();
			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	public DemoEasyFirst(String[] args)
	{
		this.args = args;
	}
	
	public void f() throws ParserRunException, TreeStringGeneratorException
	{
		String posTaggerFile = args[0];
		
		EasyFirstParser parser = new EasyFirstParser(posTaggerFile);
		parser.init();
		try
		{
			parser.setSentence("I love you.");
			parser.parse();
			BasicNode tree = parser.getParseTree();
			
			TreeStringGenerator<Info> tsg = new TreeStringGenerator<Info>(new IdLemmaPosRelNodeString(), tree);
			String treeAsString = tsg.generateString();
			System.out.println(treeAsString);
		}
		finally
		{
			parser.cleanUp();
		}
		
		
		
		
		
	}
	
	
	
	
	
	private String[] args;
}
