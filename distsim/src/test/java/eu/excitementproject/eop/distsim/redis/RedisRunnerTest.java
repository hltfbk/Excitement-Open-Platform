package eu.excitementproject.eop.distsim.redis;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assume;
import org.junit.Test;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.exception.BaseException;
import eu.excitementproject.eop.distsim.resource.SimilarityStorageBasedLexicalResource;
import eu.excitementproject.eop.distsim.storage.DefaultSimilarityStorage;
import eu.excitementproject.eop.distsim.storage.RedisBasedStringListBasicMap;
import eu.excitementproject.eop.distsim.storage.SimilarityStorage;

public class RedisRunnerTest {

	@Test
	public void test() {
		
        Logger.getRootLogger().setLevel(Level.DEBUG); // (hiding < INFO)

		// Simple running itself. 
		RedisServer rs = null; 		
		try {
			rs = new RedisServer(6379); 
			rs.start();
			Thread.sleep(5000); 
		} catch (Exception e)
		{
			System.out.println("Redis Server runner raised an exception: " + e.getMessage()); 
			e.printStackTrace(); 
			fail("Redis server runner test failed."); 
		}
		assertNotNull(rs); 
		rs.stop(); 
		
		// Running with a specific RDB file. 
		// TODO (update: those files need to be provided by Jar resources!) 
//		RedisServer rs_l = null; 
//		RedisServer rs_r = null; 
//		try {
//			rs_l = new RedisServer(6379, "/home/tailblues/temp/", "similarity-l2r.rdb"); 
//			rs_r = new RedisServer(6380, "/home/tailblues/temp/", "similarity-r2l.rdb"); 
//			rs_l.start();
//			rs_r.start(); 
//			Thread.sleep(5000); 
//
//		} catch (Exception e)
//		{
//			System.out.println("Redis Server runner raised an exception: " + e.getMessage()); 
//			e.printStackTrace(); 
//			fail("Redis server runner test failed."); 
//		}
//		assertNotNull(rs_l); 
//		assertNotNull(rs_r); 


//		// Reading testing sequence 
//		RedisBasedStringListBasicMap l2r = new RedisBasedStringListBasicMap("localhost", 6379);
//		RedisBasedStringListBasicMap r2l = new RedisBasedStringListBasicMap("localhost", 6380); 
//		
//		SimilarityStorage ss = new DefaultSimilarityStorage(l2r, r2l, "GermanLin", "inst1", "eu.excitementproject.eop.distsim.items.LemmaPosBasedElement"); 
//		LexicalResource<? extends RuleInfo> resource = new SimilarityStorageBasedLexicalResource(ss);
//
//		try {
//			List<? extends LexicalRule<? extends RuleInfo>> similarities = resource.getRulesForLeft("ewig", null); 
//		
//		System.out.println("left-2-right rules: ");
//		for (LexicalRule<? extends RuleInfo> similarity : similarities)
//			System.out.println("<" + similarity.getLLemma() + "," + similarity.getLPos() + ">" + " --> " + "<" + similarity.getRLemma() + "," + similarity.getRPos() + ">" + ": " + similarity.getConfidence());
//
//		} catch (Exception e)
//		{
//			System.out.println("Lexical resource access via Redis Server runner raised an exception: " + e.getMessage()); 
//			e.printStackTrace(); 
//			fail("Redis server runner test failed."); 			
//		}
		
		
	}
}
