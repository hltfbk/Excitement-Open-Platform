package ac.biu.nlp.nlp.instruments.parse.tree.dependency.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparClientParser;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;


/**
 * 
 * @author Asher Stern
 *
 */
public class DotDemo
{

	/**
	 * @param args
	 * <ol>
	 * <li>a file with one sentence in each line.</li>
	 * <li>Ip address of minipar server. (or Minipar data if you change the code to run locally)</li>
	 * </ol>
	 */
	public static void main(String[] args)
	{
		try
		{
			Integer id = new Integer(1);
			NodeString<Info> ns = new WordAndPosNodeString();
			BasicParser parser = new MiniparClientParser(args[1]);
			parser.init();
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
				try
				{
					String line = br.readLine();
					while (line!=null)
					{
						parser.setSentence(line);
						parser.parse();
						BasicNode root = parser.getParseTree();
						TreeDotFileGenerator<Info> tdfg = new TreeDotFileGenerator<Info>(ns,root,line,new File("/media/Data/asher/data/samples/4",id.toString()+".dot"));
						id = new Integer(id.intValue()+1);
						tdfg.generate();
						line = br.readLine();
					}
				}
				finally
				{
					br.close();
				}
			}
			finally
			{
				parser.cleanUp();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
