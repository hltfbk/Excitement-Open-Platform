package eu.excitementproject.eop.core.component.lexicalknowledge.germandistsim;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

/**
 * This test class will test GermanLinProx lexical resource. 
 * 
 * Note that the test case will pass, if the underlying Jar (artifact) is not added on the project. 
 * 
 * @author Tae-Gil Noh 
 */
public class GermanLinProxTest {

	@Test
	public void test() {
        Logger.getRootLogger().setLevel(Level.INFO); // (hiding < INFO)
        Logger testLogger = Logger.getLogger(GermanLinProxTest.class.getName()); 

        LexicalResource<RuleInfo> lr = null; 
        try {
        	lr = new GermanLinProx(); 
        }
        catch(LexicalResourceException e)
        {
        	// Lexical Resource initialization failed: This mainly caused by 
        	testLogger.info("Test instance init failed: probably the model artifact is not added in POM. This is Okay! --- " + e.getMessage()); 
        	testLogger.info("This test will be ignored."); 
        }
        
		assumeNotNull(lr); 
		
		// Okay. The resource is ready. time to send some queries as test. 
		try {
			List<LexicalRule<? extends RuleInfo>> similarities_l = lr.getRulesForLeft("ewig", null); 
			System.out.println("left-2-right rules: ");
			for (LexicalRule<? extends RuleInfo> similarity : similarities_l)
				System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());

			List<LexicalRule<? extends RuleInfo>> similarities_r = lr.getRulesForRight("ewig", null); 
			System.out.println("right-2-left rules: ");
			for (LexicalRule<? extends RuleInfo> similarity : similarities_r)
				System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());
		
		
		} catch (Exception e)
		{
			e.printStackTrace(); 
			fail(e.getMessage());  			
		}
		
		// Note that you *MUST* call close() all the time, other wise
		// the underlying redis-server does not closes!  
		try {
			lr.close(); 
		} catch (Exception e)
		{
			e.printStackTrace(); 
			fail(e.getMessage());  						
		}
		
	}
}
