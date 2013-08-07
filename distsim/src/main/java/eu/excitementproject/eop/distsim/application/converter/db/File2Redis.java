package eu.excitementproject.eop.distsim.application.converter.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import eu.excitementproject.eop.distsim.storage.Redis;
import eu.excitementproject.eop.distsim.storage.RedisBasedIDKeyPersistentBasicMap;
import eu.excitementproject.eop.distsim.storage.RedisBasedStringListBasicMap;
import eu.excitementproject.eop.distsim.util.SortUtil;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Meni Adler
 * @since 28 Feb 2013
 *
 * This program gets a dump of SQL table contains element similarities, represented by three fields: right, left, score
 * and add each row to Redis db, composed of element id map, and element similarity map
 */
public class File2Redis {
	
	public static void main(String[] args) throws Exception {
		
		if (args.length != 3) {
			System.out.println("Usage: java eu.excitementproject.eop.distsim.application.converter.db.File2Redis" +
					"<in tupple file> " +
					"<out redis host>" +
					"<out redis port");
			System.exit(0);
		}
		

		String infile = args[0];
		String redisHost = args[1];
		int redisPort = Integer.parseInt(args[2]);
		Redis redis = new Redis(redisHost,redisPort);
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
				redis.rpush(item1, item2 + RedisBasedStringListBasicMap.ELEMENT_SCORE_DELIMITER + score);
			}
		}
		reader.close();
	}
}
