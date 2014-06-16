package eu.excitement.type.alignment;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unused")
public class LinkUtilsTest {

	@Ignore 
	@Test
	public void test() {
		fail("Not yet implemented");
		
		// Set Log4J for the test 
		BasicConfigurator.resetConfiguration(); 
		BasicConfigurator.configure(); 
		Logger.getRootLogger().setLevel(Level.INFO);  // for UIMA (hiding < INFO) 
		Logger testlogger = Logger.getLogger("eu.excitement.type.alignment.LunkUtilsTest"); 
		
		// TODO 
		// normal one should make no exceptions, 
		// erroratic should make exceptions, 
	}

	
	// some methods that will give us "test" CASes with alignment.Link instances. 	
	private JCas normalCas1()
	{
		// TODO 
		JCas aJCas = null; 
		return aJCas;
	}

	private JCas normalCas2()
	{
		// TODO 
		JCas aJCas = null; 
		return aJCas;
	}

	private JCas erroraticCas1()
	{
		// TODO 
		JCas aJCas = null; 
		return aJCas;
	}

	private JCas warnableCas1()
	{
		// TODO 
		JCas aJCas = null; 
		return aJCas;
	}
	
	private JCas warnableCas2()
	{
		// TODO 
		JCas aJCas = null; 
		return aJCas;
	}

}
