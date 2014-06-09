package eu.excitementproject.eop.core.component.lexicalknowledge.geo;

import eu.excitementproject.eop.redis.BasicRedisRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Meni Adler
 * @since Mar 26, 2014
 *
 * A simple program which converts a given Geo MySQL dump to Redis
 */
public class ConvertGeoSQL2Redis {
	public static void main(String[] args) throws Exception {		
		
		if (args.length != 3) {
			System.out.println("Usage: eu.excitementproject.eop.core.component.lexicalknowledge.geo.ConvertGeoSQL2Redis " +
					"<in sql dump file> " +
					"<out l2r redis file> " + 
					"<out r2l redis file> ");
			System.exit(0);
		}
		
		int lPort = BasicRedisRunner.getInstance().run(args[1]);
		JedisPool lPool = new JedisPool(new JedisPoolConfig(), "localhost",lPort,10000);
		Jedis lJedis = lPool.getResource();
		lJedis.connect();
		lJedis.getClient().setTimeoutInfinite();
		lJedis.flushAll();
		
		int rPort = BasicRedisRunner.getInstance().run(args[2]);
		JedisPool rPool = new JedisPool(new JedisPoolConfig(), "localhost",rPort,10000);
		Jedis rJedis = rPool.getResource();
		rJedis.connect();
		rJedis.getClient().setTimeoutInfinite();
		rJedis.flushAll();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
		String line = null;
		int pushedRules=0;
		String prefix = "INSERT INTO `tipster` VALUES ";
		while ((line=reader.readLine())!=null) {
			if (line.startsWith(prefix)) {
				line = line.substring(prefix.length());
				String[] splits = line.split("\\),\\(");
				int i =0;
				for (String split : splits) {					
					if (i == 0)
						split = split.substring(1);
					if (i == splits.length-1) 
						split = split.substring(0,split.length()-2);
					
					String[] toks = split.split(",");
					String entailing = (toks[0].length() > 2 ? toks[0].substring(1,toks[0].length()-1) : null);
					String entailed = (toks[1].length() > 2 ? toks[1].substring(1,toks[1].length()-1) : null);
					if (entailing != null && entailed != null) {
						lJedis.rpush(entailing, entailed);
						rJedis.rpush(entailed,entailing);
						pushedRules++;
					}
					i++;
				}
			}
		}		
		reader.close();
		
		System.out.println(pushedRules + " rules were added to Redis");

	}

}
