package eu.excitementproject.eop.distsim.redisinjar;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

public class EmbeddedRedisBasedLexicalResourceTest {

	// Hello, this test class also shows the usage of the 
	// EmbeddedRedisBasedLexicalResource. --- you only need to provide 
	// the rdb model file in the classpath (in a Jar of an artifact). 
	//
	// You can check how you can use EmbeddedRedisBasedLexicalResource 
	// by following the test code. 
	
	// Q1: How to run this test? 
	//
	// As it is, the test is not executed and simply passed, 
	// if the redis-db files are not present in classpath. 
	// 1. Remove comment and add back artifact "redis-german-lin-prox"
	//    in distsim/pom.xml. 
	// 		This artifact is currently commented out, 
	//      because of the model file's size.
 	// 2. Run the test. Check the code to see how you can use. 
	
	// Q2: Okay. I understand how it works with rdb file in Jar. 
	//    But how I put my rdb files in Jar? 
	// 
	// 1. Generate redis model, as "distsim" tool manual describes. 
	// 2. locate the two rdb files (L->R and R->L), pack them with a 
	//    unique classpath in jar. (e.g.  jar:/resourceName/rdbfilename) 
	// 3. Make the Jar as an artifact, by deploying it in the repository.  
	// 4. Then, you code can locate those rdb files from Jar, just as the 
	//    same way this usage code locates redis-german-lin-prox files. 
	//    (e.g. getClassLoader().getResource("path/rdbfilename") ) 
	
	@Ignore
	@Test
	public void test() {
		// If you are reading this test file to check usage,
		// make sure you read till the end (close() call is a must, etc). 
		
        Logger.getRootLogger().setLevel(Level.INFO); // (hiding < INFO)
        Logger testLogger = Logger.getLogger(EmbeddedRedisBasedLexicalResourceTest.class.getName()); 
        // This test class also shows the usage of RedisBasedLexicalResource class 

        // To initiate the resource, you first need to locate the Redis RDB file in Jar.         
        // let's test it with german lin 
        // Note that, the artifact (it's Jar file) holds redis model file in 
        // classpath "redis-german-lin/similarity-l2r.rdb" and "redis-german-lin/similarity-r2l.rdb"
        // You can also pack RDB files in a Jar and make an artifact by deploying it. 
		URL l2rResource = EmbeddedRedisServerRunnerTest.class.getClassLoader().getResource("redis-german-lin/similarity-l2r.rdb"); 
		URL r2lResource = EmbeddedRedisServerRunnerTest.class.getClassLoader().getResource("redis-german-lin/similarity-r2l.rdb"); 

		// if those rdb files are not in classpath (e.g. the artifacts are not added in POM)
		// simply ignore this test, since we can't test. 
		if (l2rResource == null || r2lResource == null)
		{
			testLogger.info("Rdb model files are not found in classpath (artifact not added in POM) - This is Okay, we skip this test."); 
		}
		assumeNotNull(l2rResource); 
		assumeNotNull(r2lResource); 
		
		// We have the file resource URL. Time to initiate RedisBasedLexicalResource. 
		EmbeddedRedisBasedLexicalResource testResource=null; 
		try {
			 testResource = new EmbeddedRedisBasedLexicalResource(l2rResource, r2lResource, 6379, 6380, 20); 
		}
		catch (Exception e)
		{
			e.printStackTrace(); 
			fail (e.getMessage()); 		
		}
		assertNotNull(testResource); 
		
		// Okay. The resource is ready. time to send some queries as test. 
		try {
			List<? extends LexicalRule<? extends RuleInfo>> similarities_l = testResource.getRulesForLeft("ewig", null); 
			System.out.println("left-2-right rules: ");
			for (LexicalRule<? extends RuleInfo> similarity : similarities_l)
				System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());

			List<? extends LexicalRule<? extends RuleInfo>> similarities_r = testResource.getRulesForRight("ewig", null); 
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
			testResource.close(); 
		} catch (Exception e)
		{
			e.printStackTrace(); 
			fail(e.getMessage());  						
		}
	}

}
