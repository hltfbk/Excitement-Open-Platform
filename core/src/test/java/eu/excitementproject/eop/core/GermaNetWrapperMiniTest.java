
package eu.excitementproject.eop.core;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetInfo;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetNotInstalledException;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetRelation;
import eu.excitementproject.eop.core.component.lexicalknowledge.germanet.GermaNetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ImplCommonConfig;



/**
 * this class provides a demonstration on how to access GermaNet via the GermaNetWrapper
 * 3 possibilities of initiating GermaNet:
 * (1) specify path to GermaNet files in constructor call
 * (2) specify path to GermaNet files in configuration file and call the constructor with path to the config file
 * (3) initiate GermaNetWrapper with configuration file (see 2) and given confidence values
 */
public class GermaNetWrapperMiniTest {

	@Test
	public void test() throws UnsupportedPosTagStringException {

		// case (1): create GermaNetWrapper instance by specifying path to GermaNet files 
		// -> path should direct to GN_V80_XML
		
		GermaNetWrapper gnw1=null;
		try {
			gnw1 = new GermaNetWrapper("/path/to/GermaNet/version8.0/germanet-8.0/GN_V80_XML/");			
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
			//throw e;
		}
		
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw1); // if gnw1 is null, the following tests will not be run. 		
		
		// Test for common nouns
		try{
			for (LexicalRule<? extends GermaNetInfo> rule : gnw1.getRulesForLeft("Hitze", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym)) {
				assertTrue(rule.getLLemma().equals("Hitze"));
				assertTrue(rule.getRLemma().equals("Kälte"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() == 0.5); //default confidence
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}
		
		
		
		// case (2): define the path to GermaNet files in configuration file and construct GermaNetWrapper instance via this CommonConfig file
		// therefore change value of <property name="germaNetFilesPath">
		
		GermaNetWrapper gnw2 = null;
		try {
			File f = new File("./src/test/resources/german_resource_test_configuration.xml");
			gnw2 = new GermaNetWrapper(new ImplCommonConfig(f)); 
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and update the path in the configuration file");
			//throw e;
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		Assume.assumeNotNull(gnw2); // if gnw2 is null, the following tests will not be run. 

		// repeat the test for common nouns, with CommonConfig initiated gnw2
		try{
			for (LexicalRule<? extends GermaNetInfo> rule : gnw2.getRulesForLeft("Hitze", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym)) {
				assertTrue(rule.getLLemma().equals("Hitze"));
				assertTrue(rule.getRLemma().equals("Kälte"));
				assertTrue(rule.getRelation().equals("has_antonym"));
				assertTrue(rule.getConfidence() == 0.0);
			}
		}
		catch (LexicalResourceException e)
		{
			e.printStackTrace(); 
		}			
		
		
		
		// case (3): initiate GermaNetWrapper with confidence values
		// not that 0.0 confidence relations are still included in resulting rules list
		
		GermaNetWrapper gnw3 = null;

		try { 
			gnw3 = new GermaNetWrapper("/home/julia/Dokumente/HiWi/germanet/germanet-8.0/GN_V80_XML/", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
		}
		catch (GermaNetNotInstalledException e) {
			System.out.println("WARNING: GermaNet files are not found in the given path. Please correctly install and pass the path to GermaNetWrapper");
			//throw e;
		}
		catch(BaseException e)
		{
			e.printStackTrace(); 
		}
		
		// repeat the test for common nouns, with manually set confidence values 
				try{
					for (LexicalRule<? extends GermaNetInfo> rule : gnw3.getRulesForLeft("Hitze", new GermanPartOfSpeech("NN"), GermaNetRelation.has_antonym)) {
						assertTrue(rule.getLLemma().equals("Hitze"));
						assertTrue(rule.getRLemma().equals("Kälte"));
						assertTrue(rule.getRelation().equals("has_antonym"));
						assertTrue(rule.getConfidence() == 1.0);
					}
				}
				catch (LexicalResourceException e)
				{
					e.printStackTrace(); 
				}			
		
	}
}

