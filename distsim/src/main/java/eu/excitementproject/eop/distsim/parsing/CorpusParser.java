package eu.excitementproject.eop.distsim.parsing;

import java.io.PrintStream;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;

public class CorpusParser {
	public static void main(String[] args) throws Exception {
		//final String POS_TAGGER_MODEL = System.getenv("JARS")+"/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger";
		

		if (args.length != 5) {
			System.err.println("Usage: org.excitement.parsing.CorpusParser <parser host> <parser port> <pos tagger model> <in corpus dir> <out file>");
			System.exit(0);
		}
		
		// init the parser
		BasicParser parser = new EasyFirstParser(args[0], Integer.parseInt(args[1]), args[2]);
		parser.init();
		
		CorpusReader corpusReader = new ReuterReader(args[3]);
		PrintStream out = new PrintStream(args[4]);
		String sentence = null;
		int iSentences=0, iParsedSentences=0;
		while ((sentence = corpusReader.nextSentence()) != null) {
			iSentences++;
			try {
				parser.setSentence(sentence);
				parser.parse();
				BasicNode tree = parser.getParseTree();
				out.println(Serialization.serialize(tree));	
				iParsedSentences++;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (iSentences % 100 == 0) {
				System.out.println(iSentences);
			}
		}
		out.close();
		System.out.println(iSentences + " sentences were read, " + iParsedSentences + " of them were successfully parsed");
	}
}
