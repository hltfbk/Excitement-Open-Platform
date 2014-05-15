package eu.excitementproject.eop.lap.biu.en.sentencesplit;

import eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel.NagelSentenceSplitterTests;

public class TestAllSentenceSplitters {
	public static void main(String args[]) {
		System.out.println("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n% Testing NagelSentenceSplitter\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		NagelSentenceSplitterTests.main(args);
		System.out.println("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n% Testing MorphAdornerSentenceSplitter\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		MorphAdornerSentenceSplitterTests.main(args);
		System.out.println("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n% Testing LingPipeSentenceSplitter\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n");
		LingPipeSentenceSplitterTests.main(args);
	}
}
