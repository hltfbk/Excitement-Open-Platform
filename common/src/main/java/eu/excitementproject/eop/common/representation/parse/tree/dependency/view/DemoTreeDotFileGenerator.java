package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;

//import java.io.File;
//
//import eu.excitementproject.eop.common.representation.parse.BasicParser;
//import eu.excitementproject.eop.common.representation.parse.minipar.MiniparClientParser;
//import eu.excitementproject.eop.common.representation.parse.minipar.MiniparParser;
//import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
//


public class DemoTreeDotFileGenerator {
//
//	public static void main(String[] args)
//	{
//		try
//		{
//			if (args.length<1)
//				throw new Exception("Arguments error. Enter minipar data dir or minipar server host name / ip");
//			String miniparDataOrServer = args[0];
//			BasicParser parser = (new File(miniparDataOrServer).isDirectory())? new MiniparParser(miniparDataOrServer): new MiniparClientParser(miniparDataOrServer);
//			parser.init();
//			parser.setSentence("I wouldn't like to go there, if you are here.");
//			//parser.setSentence("I go home");
//			parser.parse();
//			BasicNode root = parser.getParseTree();
//			parser.cleanUp();
//			
//			//TreeDotFileGenerator tdfg = new TreeDotFileGenerator(new SimpleNodeString(),root,null,new File("C:\\Program Files\\Graphviz2.24\\asher\\2.dot"));
//			//TreeDotFileGenerator tdfg = new TreeDotFileGenerator(new WordOnlyNodeString(),root,null,new File("C:\\Program Files\\Graphviz2.24\\asher\\2.dot"));
//			TreeDotFileGenerator<Info> tdfg = new TreeDotFileGenerator<Info>(new WordOnlyNodeString(),root,null);
//			tdfg.generate();
//			System.out.println(tdfg.getDotFileString());
//			System.out.println("done");
//
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//
//
//
//	}

}
