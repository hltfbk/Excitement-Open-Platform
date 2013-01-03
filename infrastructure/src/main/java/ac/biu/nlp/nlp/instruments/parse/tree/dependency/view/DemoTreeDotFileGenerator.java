package ac.biu.nlp.nlp.instruments.parse.tree.dependency.view;

import java.io.File;

import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparClientParser;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparParser;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;


public class DemoTreeDotFileGenerator {

	public static void main(String[] args)
	{
		try
		{
			if (args.length<1)
				throw new Exception("Arguments error. Enter minipar data dir or minipar server host name / ip");
			String miniparDataOrServer = args[0];
			BasicParser parser = (new File(miniparDataOrServer).isDirectory())? new MiniparParser(miniparDataOrServer): new MiniparClientParser(miniparDataOrServer);
			parser.init();
			parser.setSentence("I wouldn't like to go there, if you are here.");
			//parser.setSentence("I go home");
			parser.parse();
			BasicNode root = parser.getParseTree();
			parser.cleanUp();
			
			//TreeDotFileGenerator tdfg = new TreeDotFileGenerator(new SimpleNodeString(),root,null,new File("C:\\Program Files\\Graphviz2.24\\asher\\2.dot"));
			//TreeDotFileGenerator tdfg = new TreeDotFileGenerator(new WordOnlyNodeString(),root,null,new File("C:\\Program Files\\Graphviz2.24\\asher\\2.dot"));
			TreeDotFileGenerator<Info> tdfg = new TreeDotFileGenerator<Info>(new WordOnlyNodeString(),root,null);
			tdfg.generate();
			System.out.println(tdfg.getDotFileString());
			System.out.println("done");

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}



	}

}
