package eu.excitementproject.eop.distsim.application.converter.db;

import java.io.BufferedReader;

import java.io.FileReader;

import eu.excitementproject.eop.distsim.storage.Redis;
import eu.excitementproject.eop.redis.RedisBasedStringListBasicMap;

/**
 * @author Meni Adler
 * @since 28 Feb 2013
 *
 * This program gets a dump of SQL table contains element similarities, represented by three fields: right, left, score
 * and add each row to Redis db, composed of element id map, and element similarity map
 */
public class File2Redis {
	
	public static void main(String[] args) throws Exception {
		
		if (args.length != 2) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.converter.db.File2Redis" +
					"<in tupple file> " +
					"<out redis file>");
			System.exit(0);
		}
		

		String infile = args[0];
		String redisFile = args[1];
		Redis redis = new Redis(redisFile);
		redis.open();
		redis.clear();
		
		BufferedReader reader = new BufferedReader(new FileReader(infile));
		String line=null;		
		while ((line=reader.readLine())!=null) {
			String[] toks = line.split("\t");			
			String item1 = toks[0];
			for (int i=1; i<toks.length; i+=2) {
				String item2 = toks[i];
				double score = Double.parseDouble(toks[i+1]);
				//tmp #V
				if (item2.contains(RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER))
					System.out.println("Element " + item2 + " contains the delimiter '" + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + "', and considered insignificant. The element is filtered from the model");
				else 
					redis.rpush(item1, item2 + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + score);
			}
		}
		reader.close();
	}
}
