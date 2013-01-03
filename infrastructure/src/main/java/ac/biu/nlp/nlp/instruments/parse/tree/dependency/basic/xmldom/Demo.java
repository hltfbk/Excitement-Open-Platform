package ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.xmldom;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.easyfirst.EasyFirstParser;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.IdLemmaPosRelNodeAndEdgeString;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeDotFileGenerator;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.view.TreeDotFileGenerator.TreeDotFileGeneratorException;

public class Demo
{
	public static final String FILENAME = "trees.xml";

	/**
	 * @param args should be:<BR>
	 * easy-first-host<BR>
	 * easy-first-port<BR>
	 * pos-tagger-file-name (e.g. D:\\asher\\data\\code\\jars\\jars\\stanford-postagger-full-2008-09-28\\models\\bidirectional-wsj-0-18.tagger)<BR>
	 * 
	 */
	public static void main(String[] args)
	{
		try
		{
			Iterator<String> argsIterator = Arrays.asList(args).iterator();
			Demo app = new Demo(argsIterator.next(),Integer.parseInt(argsIterator.next()),argsIterator.next());
			app.go();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}

	}

	public Demo(String hostname, int port, String postaggerFileName)
	{
		super();
		this.hostname = hostname;
		this.port = port;
		this.postaggerFileName = postaggerFileName;
	}


	public void go() throws ParserRunException, TreeXmlException, TreeDotFileGeneratorException
	{
		write();
		read();
	}
	
	public void read() throws TreeXmlException, TreeDotFileGeneratorException
	{
		XmlToListTrees xmlToListTrees = new XmlToListTrees(FILENAME,new PennXmlTreePosFactory());
		xmlToListTrees.createListTrees();
		System.out.println(xmlToListTrees.getCorpusInformation());
		System.out.println(xmlToListTrees.getListTrees().size());
		int index=1;
		for (TreeAndSentence tas : xmlToListTrees.getListTrees())
		{
			TreeDotFileGenerator<Info> tdfg = new TreeDotFileGenerator<Info>(new IdLemmaPosRelNodeAndEdgeString(), tas.getTree(), tas.getSentence(), new File("tree"+index+".dot"));
			tdfg.generate();
			++index;
		}
		
		
	}

	public void write() throws ParserRunException, TreeXmlException
	{
		String sentence = "This is a sentence";
		String sentence2 = " An Israeli official says Prime Minister Benjamin Netanyahu will visit Europe in a few months to persuade leaders to step up sanctions on Iran over its nuclear program.";
		
		BasicParser parser = new EasyFirstParser(hostname,port,postaggerFileName);
		parser.init();
		try
		{
			List<TreeAndSentence> list = new ArrayList<TreeAndSentence>();

			parser.setSentence(sentence);
			parser.parse();
			BasicNode tree = parser.getParseTree();
			TreeAndSentence tas = new TreeAndSentence(sentence, tree);
			list.add(tas);

			parser.setSentence(sentence2);
			parser.parse();
			tree = parser.getParseTree();
			tas = new TreeAndSentence(sentence2, tree);
			list.add(tas);

			ListTreesToXml lttx = new ListTreesToXml(list, "demo corpus", FILENAME);
			lttx.create();
		}
		finally
		{
			parser.cleanUp();	
		}
		
	}

	private final String hostname;
	private final int port;
	private final String postaggerFileName;
}
