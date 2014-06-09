package eu.excitementproject.eop.distsim.redisinjar;

import static org.junit.Assert.*;

import static org.junit.Assume.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.*;

import com.google.common.io.Files;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.distsim.resource.SimilarityStorageBasedLexicalResource;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;
import eu.excitementproject.eop.redis.RedisBasedStringListBasicMap;

public class EmbeddedRedisServerRunnerTest {

	// ignored for now; we need more through "binary" test.
	// TODO: check with Windows computers. (hate that but well) 
	// TODO: remove maxmemory setting on runner --- we are read only, and maxmemory does not affect reading setting 
	
	@Ignore   
	@Test
	public void test() {
		
        Logger.getRootLogger().setLevel(Level.DEBUG); // (hiding < INFO)

		// Simple running itself. Without specifying rdb file. (won't create/load any) 
		EmbeddedRedisServerRunner rs = null; 		
		try {
			rs = new EmbeddedRedisServerRunner(6371); 
			rs.start();
			// no need // Thread.sleep(5000); 
		} catch (Exception e)
		{
			System.out.println("Redis Server runner raised an exception: " + e.getMessage()); 
			e.printStackTrace(); 
			fail("Redis server runner test failed."); 
		}
		assertNotNull(rs); 
		rs.stop(); 
		
		// Running with a specific RDB file. 
		// first check that the files are in classpath. If not, just ignore 
		// the rest of the test. 
		URL l2rResource = EmbeddedRedisServerRunnerTest.class.getClassLoader().getResource("redis-german-lin/similarity-l2r.rdb"); 
		URL r2lResource = EmbeddedRedisServerRunnerTest.class.getClassLoader().getResource("redis-german-lin/similarity-r2l.rdb"); 

		assumeNotNull(l2rResource); 
		assumeNotNull(r2lResource); 
		
		// The following test will be done only when there is 
		// redis-german-lin/  rdb files are in classpath (artifact/Jar). 
		
		EmbeddedRedisServerRunner rs_l = null; 
		EmbeddedRedisServerRunner rs_r = null; 
		try {
//			rs_l = new RedisServerRunner(6379, "/home/tailblues/temp/", "similarity-l2r.rdb"); 
//			rs_r = new RedisServerRunner(6380, "/home/tailblues/temp/", "similarity-r2l.rdb"); 
			System.out.println("extracting l2r file");
			//File l2rRdb = extractDataFileFromJar("redis-german-lin/similarity-l2r.rdb");
			File l2rRdb = extractDataFileFromResource(l2rResource); 
			System.out.println("extracted in: " + l2rRdb.getParent() + "//" + l2rRdb.getName()); 
			System.out.println("extracting r2l file");
			//File r2lRdb = extractDataFileFromJar("redis-german-lin/similarity-r2l.rdb"); 
			File r2lRdb = extractDataFileFromResource(r2lResource); 
			System.out.println("extracted in: " + r2lRdb.getParent() + "//" + r2lRdb.getName()); 
//			rs_l = new RedisServerRunner(6379, "/Users/tailblues/temp", "similarity-l2r.rdb"); 
//			rs_r = new RedisServerRunner(6380, "/Users/tailblues/temp", "similarity-r2l.rdb"); 
			rs_l = new EmbeddedRedisServerRunner(6379, l2rRdb.getParent(), l2rRdb.getName()); 
			rs_r = new EmbeddedRedisServerRunner(6380, r2lRdb.getParent(), r2lRdb.getName()); 
			
			// run 
			rs_l.start();
			//Thread.sleep(1000); // do we need this? no. 
			rs_r.start(); 
			//Thread.sleep(5000); 

		} catch (Exception e)
		{
			System.out.println("Redis Server runner raised an exception: " + e.getMessage()); 
			e.printStackTrace(); 
			fail("Redis server runner test failed."); 
		}
		assertNotNull(rs_l); 
		assertNotNull(rs_r); 


		// Reading testing sequence 
		RedisBasedStringListBasicMap l2r = new RedisBasedStringListBasicMap("127.0.0.1", 6379);
		RedisBasedStringListBasicMap r2l = new RedisBasedStringListBasicMap("127.0.0.1", 6380); 
		
		SimilarityStorage ss = new DefaultSimilarityStorage(l2r, r2l, "GermanLin", "inst1", "eu.excitementproject.eop.distsim.items.LemmaPosBasedElement"); 
		LexicalResource<? extends RuleInfo> resource = new SimilarityStorageBasedLexicalResource(ss, 10);

		try {
			List<? extends LexicalRule<? extends RuleInfo>> similarities_l = resource.getRulesForLeft("ewig", null); 
			System.out.println("left-2-right rules: ");
			for (LexicalRule<? extends RuleInfo> similarity : similarities_l)
				System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());

			List<? extends LexicalRule<? extends RuleInfo>> similarities_r = resource.getRulesForRight("ewig", null); 
			System.out.println("right-2-left rules: ");
			for (LexicalRule<? extends RuleInfo> similarity : similarities_r)
				System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());
		
		
		} catch (Exception e)
		{
			System.out.println("Lexical resource access via Redis Server runner raised an exception: " + e.getMessage()); 
			e.printStackTrace(); 
			fail("Redis server runner test failed."); 			
		}
		
		rs_l.stop();
		rs_r.stop(); 
		
	}
	
	private static File extractDataFileFromResource(URL resource) throws IOException
	{
		File tmpDir = Files.createTempDir();
		tmpDir.deleteOnExit();
		
		File rdb = new File(tmpDir, resource.getFile());
		FileUtils.copyURLToFile(resource, rdb);
		rdb.deleteOnExit();
				
		return rdb; 
	}
	
//	private static File extractDataFileFromJar(String resourceName) throws IOException {
//		File tmpDir = Files.createTempDir();
//		tmpDir.deleteOnExit();
//
//		File rdb = new File(tmpDir, resourceName);
//		FileUtils.copyURLToFile(Resources.getResource(resourceName), rdb);
//		rdb.deleteOnExit();
//		
//		return rdb;
//	}

}
