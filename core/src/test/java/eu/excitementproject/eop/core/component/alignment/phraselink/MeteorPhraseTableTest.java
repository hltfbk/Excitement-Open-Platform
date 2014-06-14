package eu.excitementproject.eop.core.component.alignment.phraselink;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import eu.excitementproject.eop.core.component.alignment.phraselink.MeteorPhraseTable.ScoredString;

public class MeteorPhraseTableTest {

	@Ignore // blocked, due to the fact that this test is also done by MeteorPhraseResource class test that uses this class.  
	@Test
	public void test() {
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);   
		Logger testlogger = Logger.getLogger(this.getClass().toString()); 
		
		testlogger.info("This test may take upto 30 seconds..."); 
		// load test. 
		// (2.8Ghz Pentium dual takes 26 seconds on loading English paraphrase.) 
		MeteorPhraseTable englishTable = null; 
		final long loadStart = System.currentTimeMillis();
		try 
		{
			englishTable = new MeteorPhraseTable("/meteor-1.5/data/paraphrase-en"); 
			
		}
		catch (Exception e)
		{
			// no exception should happen 
			fail(e.getMessage()); 
		}
		final long loadEnd = System.currentTimeMillis();
		final long duration = ( loadEnd - loadStart ) / 1000; 
		testlogger.debug("loading took " + duration + " seconds"); 
		
		// read test for known value, english. 
		String lhs = "all those who have"; 
		List<ScoredString> rhsAndProbList = englishTable.lookupParaphrasesFor(lhs); 
	    assertEquals(rhsAndProbList.size(), 4);  
		for (ScoredString rhsProbTuple : rhsAndProbList)
		{
			String rhs = rhsProbTuple.getString(); 
			Double prob = rhsProbTuple.getScore(); 
			testlogger.debug(lhs + " -> " + rhs + " : " + prob.toString()); 
		}
		
		// null result. 
		String lhs2 = "bikini atoll"; // not in the resource 
		rhsAndProbList = englishTable.lookupParaphrasesFor(lhs2); 
	    assertEquals(rhsAndProbList.size(), 0);  
		for (ScoredString rhsProbTuple : rhsAndProbList)
		{
			String rhs = rhsProbTuple.getString(); 
			Double prob = rhsProbTuple.getScore(); 
			testlogger.debug(lhs + " -> " + rhs + " : " + prob.toString()); 
		}
	}

}
