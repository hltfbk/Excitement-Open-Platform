package eu.excitementproject.eop.lap.biu.en.sentencesplit;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.runner.JUnitCore;

import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitter;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterTests;
import eu.excitementproject.eop.lap.biu.test.BiuTestUtils;

public class MorphAdornerSentenceSplitterTests extends SentenceSplitterTests {

	@BeforeClass
	public static void beforeClass() throws IOException {
		
		// Run tests only under BIU environment
		BiuTestUtils.assumeBiuEnvironment();
	}
	
	public SentenceSplitter getSplitter() {
		return new MorphAdornerSentenceSplitter();
	}

	public static void main(String args[]) {
	    JUnitCore.runClasses(MorphAdornerSentenceSplitterTests.class);
	}

}
