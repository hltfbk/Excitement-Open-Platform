package eu.excitementproject.eop.distsim.redisinjar;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

public class EmbeddedRedisBasedLexicalResourceTest {

	@Test
	public void test() {
        Logger.getRootLogger().setLevel(Level.INFO); // (hiding < INFO)

        // This test class also shows the usage of RedisBasedLexicalResource class 

        // To initiate the resource, you first need to locate the Redis RDB file in Jar.         
        // let's test it with german lin 
        // Note that, the artifact (it's Jar file) holds redis model file in 
        // classpath "redis-german-lin/similarity-l2r.rdb" and "redis-german-lin/similarity-r2l.rdb"
        // You can also pack RDB files in a Jar and make an artifact by deploying it. 
		URL l2rResource = EmbeddedRedisServerRunnerTest.class.getClassLoader().getResource("redis-german-lin/similarity-l2r.rdb"); 
		URL r2lResource = EmbeddedRedisServerRunnerTest.class.getClassLoader().getResource("redis-german-lin/similarity-r2l.rdb"); 

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
