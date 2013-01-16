package eu.excitementproject.eop.lap.biu.en.coreference.bart;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.LinkedList;
//import java.util.List;
//
//import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
//import eu.excitementproject.eop.common.representation.parse.BasicParser;
//import eu.excitementproject.eop.common.representation.parse.minipar.MiniparClientParser;
//import eu.excitementproject.eop.common.representation.parse.minipar.MiniparParser;
//import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
//import eu.excitementproject.eop.lap.biu.en.coreference.TreeCoreferenceInformationUtils;
//import eu.excitementproject.eop.lap.biu.en.coreference.merge.WordWithCoreferenceTag;
//import eu.excitementproject.eop.lap.biu.en.coreference.merge.english.EnglishCorefMerger;
//import eu.excitementproject.eop.lap.biu.en.sentencesplit.SentenceSplitter;
//import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitter;


/**
 * See {@link BartClient} about a bug in JDK6 that causes problems here.
 * 
 * @author asher
 *
 */
public class BartClientDemo
{
//	public void f() throws Exception
//	{
//		String miniparArgument = "192.168.56.101";
//		//String miniparArgument = "/media/data2/data/asher/phd/thrid_party/minipar/minipar-0.5-Windows/data"
//		//String text = "Danny is nice. He is beautiful. I saw him yesterday, and he smiled to me. I am happy.";
//		//String text = "Days before the first anniversary of the antigovernment protests that rocked Iran, the nation\'s supreme religious leader, Ayatollah Ali Khamenei, renewed his attack on opposition leaders on Friday, saying that they had betrayed the values of the 1979 Islamic Revolution.";
//		//String text = "Days before the first anniversary of the antigovernment protests that rocked Iran, the nation’s supreme religious leader, Ayatollah Ali Khamenei, renewed his attack on opposition leaders on Friday, saying that they had betrayed the values of the 1979 Islamic Revolution."; 
//		String text = "The video is interspersed with clips from the recent Israeli raid on the Gaza-bound aid ship, the Mavi Marmara, showing activists attacking Israeli soldiers in the clashes that would eventually claim the lives of nine activists and leave dozens, including Israeli commandos, injured. The Israeli government spokesperson's office accidentally circulated a link to the clip on Friday, after which they stated the video was \"not intended for general release. The contents of the video in no way represent the official policy of either the Government Press Office or of the State of Israel\". The clip was created by a Hebrew-language media satire site called Latma.";
//		SentenceSplitter splitter = new NagelSentenceSplitter();
//		splitter.setDocument(text);
//		splitter.split();
//		List<String> sentences = splitter.getSentences();
//		
//		BartClient bartClient = new BartClient(text);
//		//bartClient.setCleanXml(false);
//		bartClient.process();
//		List<WordWithCoreferenceTag> bartOutput = bartClient.getBartOutput();
//		for (WordWithCoreferenceTag wwct : bartOutput)
//		{
//			System.out.print(wwct.getWord());
//			if (wwct.getCoreferenceTag()!=null)
//			{
//				System.out.print(" - "+wwct.getCoreferenceTag());
//			}
//			System.out.println();
//		}
//		
//		BasicParser parser = null;
//		File miniparArgumentFile = new File(miniparArgument);
//		if (miniparArgumentFile.exists()) if (miniparArgumentFile.isDirectory())
//			parser = new MiniparParser(miniparArgument);
//		if (null==parser)
//			parser = new MiniparClientParser(miniparArgument);
//		
//		parser.init();
//		try
//		{
//			List<BasicNode> listTrees = new LinkedList<BasicNode>();
//			for (String sentence : sentences)
//			{
//				parser.setSentence(sentence);
//				parser.parse();
//				listTrees.add(parser.getParseTree());
//			}
//			EnglishCorefMerger merger = new EnglishCorefMerger(listTrees, bartOutput);
//			merger.merge();
//			TreeCoreferenceInformation<BasicNode> corefInformation = merger.getCoreferenceInformation();
//			
//			
//			for (Integer groupId : corefInformation.getAllExistingGroupIds())
//			{
//				System.out.print(groupId+": ");
//				for (BasicNode node : corefInformation.getGroup(groupId))
//				{
//					System.out.print(node.getInfo().getNodeInfo().getWord()+", ");
//				}
//				System.out.println();
//			}
//			
//			System.out.println();
//			System.out.println("after removing nested tags:");
//			
//			TreeCoreferenceInformationUtils.removeNestedTags(corefInformation);
//			for (Integer groupId : corefInformation.getAllExistingGroupIds())
//			{
//				System.out.print(groupId+": ");
//				for (BasicNode node : corefInformation.getGroup(groupId))
//				{
//					System.out.print(node.getInfo().getNodeInfo().getWord()+", ");
//				}
//				System.out.println();
//			}
//			
//			
//			
//			
//		}
//		finally
//		{
//			parser.cleanUp();
//		}
//	}
//	
//	public void g() throws Exception
//	{
//		BufferedReader reader = new BufferedReader(new FileReader("/media/Data/asher/data/bart/my/out.text2.xml"));
//		try
//		{
//			StringBuffer sb = new StringBuffer();
//			String line = reader.readLine();
//			while (line != null)
//			{
//				sb.append(line);
//				sb.append("\n");
//				line = reader.readLine();
//			}
//			String s = sb.toString();
//			s = s.replaceAll("&.*;", "");
//			System.out.println(s);
//		}
//		finally
//		{
//			reader.close();
//		}
//		
//		
//	}
//	
//	public static void main(String[] args)
//	{
//		try
//		{
//			BartClientDemo demo = new BartClientDemo();
//			demo.f();
//			//demo.g();
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace(System.out);
//		}
//
//	}

}
