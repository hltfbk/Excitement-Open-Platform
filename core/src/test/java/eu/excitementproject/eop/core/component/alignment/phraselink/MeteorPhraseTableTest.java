package eu.excitementproject.eop.core.component.alignment.phraselink;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class MeteorPhraseTableTest {

	@Test
	public void test() {
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.DEBUG);   
		@SuppressWarnings("unused")
		Logger testlogger = Logger.getLogger(this.getClass().toString()); 
		
		// TODO 
		// load test. 
		MeteorPhraseTable englishTable = null; 
		try 
		{
			englishTable = new MeteorPhraseTable("/meteor-1.5/data/paraphrase-en"); 
			
		}
		catch (Exception e)
		{
			fail(e.getMessage()); 
		}
		
		// read test for known value.  
		
		// null result. 
		
	}

}
