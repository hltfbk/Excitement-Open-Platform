package eu.excitementproject.eop.distsim.redis;

import org.junit.Ignore;
//import org.junit.Test;

import eu.excitementproject.eop.redis.BasicRedisRunner;

public class BasicRedisRunnerTest {

	
	//@Test
	@Ignore
	public void test() {
		try {
			BasicRedisRunner.getInstance();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}

}
