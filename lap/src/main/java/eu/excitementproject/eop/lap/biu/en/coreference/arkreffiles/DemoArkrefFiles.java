package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;


/**
 * 
 * @author Asher Stern
 * @since Dec 9, 2013
 *
 */
public class DemoArkrefFiles
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			new DemoArkrefFiles(args).go();
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}

	}
	
	public DemoArkrefFiles(String[] args)
	{
		super();
		this.args = args;
	}
	
	public void go() throws ParserRunException, IOException, CoreferenceResolutionException, SentenceSplitterException, TreeCoreferenceInformationException
	{
		String text = ArkreffilesUtils.readTextFile(args[0]);
		SentenceSplitter sentenceSplitter = new NagelSentenceSplitter();
		sentenceSplitter.setDocument(text);
		sentenceSplitter.split();
		List<String> sentences = sentenceSplitter.getSentences();
		
		BasicParser parser = new EasyFirstParser("localhost", 10345, "/media/Data/asher/data/code/jars/jars/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger");
		parser.init();
		List<BasicNode> trees = new ArrayList<>(sentences.size());
		for (String sentence : sentences)
		{
			parser.setSentence(sentence);
			parser.parse();
			trees.add(parser.getParseTree());
		}
		
		ArkrefFilesCoreferenceResolver corefResolver = new ArkrefFilesCoreferenceResolver();
		corefResolver.init();
		corefResolver.setInput(trees, text);
		corefResolver.resolve();
		TreeCoreferenceInformation<BasicNode> corefInformation = corefResolver.getCoreferenceInformation();
		
		for (Integer groupId : corefInformation.getAllExistingGroupIds())
		{
			System.out.print(groupId+": ");
			for (BasicNode node : corefInformation.getGroup(groupId))
			{
				System.out.print(InfoGetFields.getLemma(node.getInfo())+", ");
			}
			System.out.println();
		}
		
		
	}
	
	public void go3() throws ParserRunException, IOException, CoreferenceResolutionException
	{
		String text = ArkreffilesUtils.readTextFile(args[0]);
		ArkrefOutputReader<Info,BasicNode> reader = new ArkrefOutputReader<Info,BasicNode>(text);
		reader.read();
		ArrayList<ArkrefOutputWord<Info, BasicNode>> output = reader.getArkrefOutput();
		for (ArkrefOutputWord<Info, BasicNode> word : output)
		{
			System.out.println(word.getWord());
			System.out.println("Begin markers:\n"+printListMarkers(word.getBeginMarkers()));
			System.out.println("End markers:\n"+printListMarkers(word.getEndMarkers()));
		}
		
		
		
	}
	
	private String printListMarkers(List<ArkrefMarker> markers)
	{
		StringBuilder sb = new StringBuilder();
		for (ArkrefMarker marker : markers)
		{
			sb.append(marker.getMentionId()).append("/").append(marker.getEntityId()).append("\n");
		}
		return sb.toString();
	}
	


	public void go2() throws ParserRunException
	{
		BasicParser parser = new EasyFirstParser("localhost", 10345, "/media/Data/asher/data/code/jars/jars/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger");
		parser.init();
		parser.setSentence("I am glad that it works.");
		parser.parse();
		BasicNode tree = parser.getParseTree();
		Map<BasicNode, Integer> depthMap = ArkreffilesUtils.mapNodesToDepth(tree);
		for (Map.Entry<BasicNode, Integer> entry : depthMap.entrySet())
		{
			System.out.println(InfoGetFields.getLemma(entry.getKey().getInfo())+": "+entry.getValue());
		}
	
	}

	private String[] args;
}
