package eu.excitementproject.eop.common.representation.parse.tree.dependency.view;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//import eu.excitementproject.eop.common.representation.parse.minipar.MiniparParser;
//import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;


/**
 * 
 * @author Asher Stern
 *
 */
public class SerializationDemo
{
//	
//	public static void main(String[] args)
//	{
//		try
//		{
//			if (args.length<2) throw new Exception ("args. Please give Minipar data dir and destination file 1 and destination file 2 and serialization file");
//			String miniparDataDir = args[0];
//			MiniparParser parser = new MiniparParser(miniparDataDir);
//			parser.init();
//			parser.setSentence("This is a serialization demo.");
//			parser.parse();
//			BasicNode tree = parser.getParseTree();
//			TreeDotFileGenerator<Info> tdfg = new TreeDotFileGenerator<Info>(new LemmaPosRelNodeAndEdgeString(), tree, "serialization demo", new File(args[1]));
//			tdfg.generate();
//			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(args[3]));
//			outputStream.writeObject(tree);
//			outputStream.close();
//			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(args[3])));
//			try
//			{
//				BasicNode reproducedTree = (BasicNode) inputStream.readObject();
//				TreeDotFileGenerator<Info> tdfg2 = new TreeDotFileGenerator<Info>(new LemmaPosRelNodeAndEdgeString(), reproducedTree, "reproduced", new File(args[2]));
//				tdfg2.generate();
//			}
//			finally
//			{
//				inputStream.close();
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}

}
