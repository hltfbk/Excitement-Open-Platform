package eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//
//import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolutionException;
//import ac.biu.nlp.nlp.instruments.coreference.CoreferenceResolver;
//import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformation;
//import ac.biu.nlp.nlp.instruments.coreference.TreeCoreferenceInformationException;
//import ac.biu.nlp.nlp.instruments.coreference.arkref.ArkrefClient.ArkrefClientException;
//import ac.biu.nlp.nlp.instruments.coreference.arkref.ArkrefCoreferenceResolver;
//import ac.biu.nlp.nlp.instruments.ner.NamedEntityMergeServices;
//import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizer;
//import ac.biu.nlp.nlp.instruments.ner.NamedEntityRecognizerException;
//import ac.biu.nlp.nlp.instruments.ner.NamedEntityWord;
//import ac.biu.nlp.nlp.instruments.ner.stanford.StanfordNamedEntityRecognizer;
//import ac.biu.nlp.nlp.instruments.tokenizer.MaxentTokenizer;
//import ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer;
//import ac.biu.nlp.nlp.instruments.tokenizer.TokenizerException;
//import eu.excitementproject.eop.common.representation.parse.BasicParser;
//import eu.excitementproject.eop.common.representation.parse.ParserRunException;
//import eu.excitementproject.eop.common.representation.parse.easyfirst.EasyFirstParser;
//import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
//import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.IdLemmaPosRelNodeAndEdgeString;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeDotFileGenerator;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeDotFileGenerator.TreeDotFileGeneratorException;
//import eu.excitementproject.eop.common.utilities.match.Matcher;

public class Demo
{
//	
//	public static final String FILENAME = "trees.xml";
//	public static final boolean RESOLVE_COREFERENCE = true;
//
//	/**
//	 * @param args should be:<BR>
//	 * easy-first-host<BR>
//	 * easy-first-port<BR>
//	 * pos-tagger-file-name (e.g. D:\\asher\\data\\code\\jars\\jars\\stanford-postagger-full-2008-09-28\\models\\bidirectional-wsj-0-18.tagger)<BR>
//	 * 
//	 */
//	public static void main(String[] args)
//	{
//		try
//		{
//			Iterator<String> argsIterator = Arrays.asList(args).iterator();
//			Demo app = new Demo(argsIterator.next(),Integer.parseInt(argsIterator.next()),argsIterator.next(),argsIterator.next());
//			app.go();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace(System.out);
//		}
//
//	}
//
//	public Demo(String hostname, int port, String postaggerFileName, String nerModelFileName)
//	{
//		super();
//		this.hostname = hostname;
//		this.port = port;
//		this.postaggerFileName = postaggerFileName;
//		this.nerModelFileName = nerModelFileName;
//	}
//
//
//	public void go() throws ParserRunException, TreeXmlException, TreeDotFileGeneratorException, ArkrefClientException, IOException, CoreferenceResolutionException, TreeCoreferenceInformationException, NamedEntityRecognizerException, TokenizerException
//	{
//		System.out.println("Writing...");
//		write();
//		System.out.println("Writing done.");
//		System.out.println("Reading...");
//		read();
//		System.out.println("Reading done.");
//	}
//	
//	public void read() throws TreeXmlException, TreeDotFileGeneratorException, TreeCoreferenceInformationException
//	{
//		XmlToListTrees xmlToListTrees = new XmlToListTrees(FILENAME,new PennXmlTreePosFactory());
//		xmlToListTrees.createListTrees();
//		System.out.println("Corpus information: "+xmlToListTrees.getCorpusInformation());
//		System.out.println("Number of trees: "+xmlToListTrees.getListTrees().size());
//		int index=1;
//		for (TreeAndSentence tas : xmlToListTrees.getListTrees())
//		{
//			TreeDotFileGenerator<Info> tdfg = new TreeDotFileGenerator<Info>(new IdLemmaPosRelNodeAndEdgeString(), tas.getTree(), tas.getSentence(), new File("tree"+index+".dot"));
//			tdfg.generate();
//			++index;
//		}
//
//		// print coreference information
//		System.out.println("coreference information:");
//		TreeCoreferenceInformation<BasicNode> corefInformation =
//				xmlToListTrees.getCoreferenceInformation();
//		if (corefInformation!=null)
//		{
//			for (Integer groupId : corefInformation.getAllExistingGroupIds())
//			{
//				System.out.print(groupId+": ");
//				boolean firstIteration = true;
//				for (BasicNode node : corefInformation.getGroup(groupId))
//				{
//					if (firstIteration) firstIteration = false;
//					else System.out.print(", ");
//					System.out.print(node.getInfo().getId()+" ("+InfoGetFields.getLemma(node.getInfo())+")");
//				}
//				System.out.println();
//			}
//		}
//		else
//		{
//			System.out.println("no coreference information has been read.");
//		}
//		
//		
//		
//	}
//
//	public void write() throws ParserRunException, TreeXmlException, ArkrefClientException, IOException, CoreferenceResolutionException, NamedEntityRecognizerException, TokenizerException, TreeCoreferenceInformationException
//	{
//
//		Tokenizer tokenizer = new MaxentTokenizer();
//		tokenizer.init();
//		try
//		{
//			NamedEntityRecognizer namedEntityRecognizer = new StanfordNamedEntityRecognizer(new File(this.nerModelFileName));
//			namedEntityRecognizer.init();
//			try
//			{
//				CoreferenceResolver<BasicNode> coreferenceResolver = new ArkrefCoreferenceResolver();
//				coreferenceResolver.init();
//				try
//				{
//					List<BasicNode> listTrees = new LinkedList<BasicNode>();
//
//
//					String sentence = "This is a sentence.";
//					String sentence2 = " An Israeli official says Prime Minister Benjamin Netanyahu will visit Europe in a few months to persuade leaders to step up sanctions on Iran over its nuclear program.";
//
//					BasicParser parser = new EasyFirstParser(hostname,port,postaggerFileName);
//					parser.init();
//					try
//					{
//						List<TreeAndSentence> list = new ArrayList<TreeAndSentence>();
//
//						namedEntityRecognizer.setSentence(sentence, tokenizer);
//						namedEntityRecognizer.recognize();
//						parser.setSentence(sentence);
//						parser.parse();
//						
//						Matcher<NamedEntityWord, BasicConstructionNode> matcher =
//								new Matcher<NamedEntityWord, BasicConstructionNode>(
//										namedEntityRecognizer.getAnnotatedSentence().iterator(),
//										parser.getNodesOrderedByWords().iterator(),
//										NamedEntityMergeServices.getMatchFinder(),
//										NamedEntityMergeServices.getOperator()
//										);
//						matcher.makeMatchOperation();
//						
//						BasicNode tree = parser.getParseTree();
//						TreeAndSentence tas = new TreeAndSentence(sentence, tree);
//						list.add(tas);
//						listTrees.add(tree);
//
//						namedEntityRecognizer.setSentence(sentence2, tokenizer);
//						namedEntityRecognizer.recognize();
//						parser.setSentence(sentence2);
//						parser.parse();
//						matcher =
//								new Matcher<NamedEntityWord, BasicConstructionNode>(
//										namedEntityRecognizer.getAnnotatedSentence().iterator(),
//										parser.getNodesOrderedByWords().iterator(),
//										NamedEntityMergeServices.getMatchFinder(),
//										NamedEntityMergeServices.getOperator()
//										);
//						matcher.makeMatchOperation();
//						tree = parser.getParseTree();
//						tas = new TreeAndSentence(sentence2, tree);
//						list.add(tas);
//						listTrees.add(tree);
//
//						TreeCoreferenceInformation<BasicNode> corefInformation = null;
//						if (RESOLVE_COREFERENCE)
//						{
//							coreferenceResolver.setInput(listTrees, sentence+" "+sentence2);
//							coreferenceResolver.resolve();
//							corefInformation = coreferenceResolver.getCoreferenceInformation();
//						}
//
//						ListTreesToXml lttx = new ListTreesToXml(list, "demo corpus", FILENAME, corefInformation);
//						lttx.create();
//
//
//
//						// print coreference information
//						if (corefInformation!=null)
//						{
//							System.out.println("coreference information:");
//							for (Integer groupId : corefInformation.getAllExistingGroupIds())
//							{
//								System.out.print(groupId+": ");
//								boolean firstIteration = true;
//								for (BasicNode node : corefInformation.getGroup(groupId))
//								{
//									if (firstIteration) firstIteration = false;
//									else System.out.print(", ");
//									System.out.print(node.getInfo().getId()+" ("+InfoGetFields.getLemma(node.getInfo())+")");
//								}
//								System.out.println();
//							}
//						}
//						else
//						{
//							System.out.println("no coreference information has been resolved.");
//						}
//					}
//					finally
//					{
//						parser.cleanUp();	
//					}
//				}
//				finally
//				{
//					coreferenceResolver.cleanUp();
//				}
//			}
//			finally
//			{
//				namedEntityRecognizer.cleanUp();
//			}
//		}
//		finally
//		{
//			tokenizer.cleanUp();
//		}
//
//	}
//
//	private final String hostname;
//	private final int port;
//	private final String postaggerFileName;
//	private final String nerModelFileName; 
}
